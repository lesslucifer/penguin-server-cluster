/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import config.CFDailyQuest;
import config.PGConfig;
import db.DBContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.prize.PGPrize;
import pgentity.prize.PrizeFactory;
import pgentity.quest.QuestChecker;
import pgentity.quest.QuestFactory;
import pgentity.quest.QuestLogger;
import pgentity.quest.QuestState;
import pgentity.redis.PGRedisListEntity;
import share.PGHelper;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;
import share.AMFBuilder;
import share.TimeUtil;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadQuest")
public class DailyQuest implements PGEntity
{
    private final String uid;
    private final String dayToken;
    private final RedisKey redisKey;
    private final Map<Integer, DailyQuest.Quest> quests =
            new HashMap(PGConfig.inst().temp().NumberDailyQuest());
    
    private int lastAcceptLevel;
    private int currentIndex;
    private QuestState currentState;

    private DailyQuest(String uid, String dayToken) {
        this.uid = uid;
        this.dayToken = dayToken;
        this.redisKey = redisKey(uid, dayToken);
        
        for (int i = 1; i <= PGConfig.inst().temp().NumberDailyQuest(); i++) {
            quests.put(i, new Quest(this, i));
        }
    }
    
    public static DailyQuest getQuest(String uid, long now)
    {
        String dayToken = TimeUtil.dayToken(now);
        return EntityPool.inst().get(DailyQuest.class, uid, dayToken);
    }
    
    public static DailyQuest loadQuest(String uid, String dayToken)
    {
        DailyQuest dailyQuest = new DailyQuest(uid, dayToken);
        dailyQuest.updateFromDB();
        
        return dailyQuest;
    }
    
    @Deprecated
    /**
     * User QuestService.createNewDailyQuest instead
     */
    public static DailyQuest newQuest(String uid, int uLevel, long now)
    {
        final String dayToken = TimeUtil.dayToken(now);
        DailyQuest dailyQuest = new DailyQuest(uid, dayToken);
        dailyQuest.setCurrentIndex(1);
        dailyQuest.setCurrentState(QuestState.NEW);
        dailyQuest.setLastAcceptLevel(uLevel);
        
        for (Quest quest : dailyQuest.quests.values()) {
            quest.generateNewQuest(uLevel);
        }
        
        dailyQuest.saveToDB();
        dailyQuest.expireKey();
        
        EntityPool.inst().put(dailyQuest, DailyQuest.class, uid, dayToken);
        
        return dailyQuest;
    }
    
    public static RedisKey redisKey(String uid, String dayToken)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_QUEST)
                .getChild(PGKeys.FD_DAILY_QUEST)
                .getChild(dayToken);
    }

    public String getUid() {
        return uid;
    }

    public String getDayToken() {
        return dayToken;
    }

    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        
        if (!PGHelper.isNullOrEmpty(data))
        {
            this.currentIndex = Integer.parseInt(data.get(PGMacro.QUEST_INDEX));
            this.currentState = QuestState.get(Integer.parseInt(data.get(PGMacro.QUEST_STATE)));
            this.lastAcceptLevel = Integer.parseInt(data.get(PGMacro.LAST_ACCEPT_LEVEL));
        }
        else
        {
            this.currentIndex = -1;
            this.currentState = QuestState.LOCKED;
            this.lastAcceptLevel = -1;
        }
    }

    @Override
    public void saveToDB()
    {
        Map<String, String> data = new HashMap();
        
        data.put(PGMacro.QUEST_INDEX, String.valueOf(this.currentIndex));
        data.put(PGMacro.QUEST_STATE, String.valueOf(this.currentState.getValue()));
        data.put(PGMacro.LAST_ACCEPT_LEVEL, String.valueOf(this.lastAcceptLevel));
        
        DBContext.Redis().hset(redisKey, data);
    }
    
    public boolean hasQuest()
    {
        return (this.currentIndex > 0) &&
               (this.currentState != QuestState.INVALID) &&
               (this.lastAcceptLevel > 0);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public QuestState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(QuestState currentState) {
        this.currentState = currentState;
    }

    public int getLastAcceptLevel() {
        return lastAcceptLevel;
    }

    public void setLastAcceptLevel(int lastAcceptLevel) {
        this.lastAcceptLevel = lastAcceptLevel;
    }
    
    public QuestLogger getCurrentLogger()
    {
        if (this.hasQuest())
        {
            return this.quests.get(this.currentIndex).getLogger(lastAcceptLevel);
        }
        
        return QuestFactory.getLogger(null, null);
    }
    
    public PGPrize getCurrentPrize(int uLevel)
    {
        if (this.hasQuest())
        {
            return this.quests.get(this.currentIndex).getPrize(uLevel);
        }
        
        return PrizeFactory.getPrize(null);
    }
    
    public QuestChecker getCurrentNeed()
    {
        if (this.hasQuest())
        {
            return this.quests.get(this.currentIndex).getNeed(lastAcceptLevel);
        }
        
        return QuestFactory.getChecker(null, null);
    }
    
    public int getCost(int uLevel)
    {
        return PGConfig.inst().getDailyQuest()
                .get(this.currentIndex).getCost(uLevel);
    }
    
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap(2);
        data.put(PGMacro.LAST_ACCEPT_LEVEL, this.lastAcceptLevel);
        
        Map<String, Object> qData = new HashMap(quests.size());
        for (Map.Entry<Integer, Quest> questEntry : quests.entrySet()) {
            Integer qIndex = questEntry.getKey();
            Quest quest = questEntry.getValue();
            
            QuestState state = (qIndex < this.currentIndex)?QuestState.RETURNED:
                    ((qIndex == this.currentIndex)?currentState:QuestState.NEW);
            
            qData.put(String.valueOf(qIndex), buildQuestAMF(quest, state, context));
        }
        data.put("data", qData);
        
        return data;
    }
    
    private Map<String, Object> buildQuestAMF(Quest quest, QuestState state, EntityContext context)
    {
        Map<String, Object> data;
        if (state == QuestState.ACCEPTED)
        {
            data = quest.buildFullAMF(context);
        }
        else
        {
            data = quest.buildAMF();
        }
        
        data.put(PGMacro.QUEST_STATE, state.getValue());
        
        return data;
    }
    
    private void expireKey()
    {
        DBContext.Redis().expire(redisKey, TimeUtil.DAY_SECS);
    }
    
    private static class Quest extends PGRedisListEntity
    {
        private final DailyQuest questList;
        private final int index;
        private final RedisKey redisKey;

        private Quest(DailyQuest questList, int index) {
            this.questList = questList;
            this.index = index;
            this.redisKey = questList.redisKey.getChild(String.valueOf(index));
        }

        @Override
        protected RedisKey redisKey() {
            return this.redisKey;
        }

        public DailyQuest getQuestList() {
            return questList;
        }

        public int getIndex() {
            return index;
        }
        
        public QuestLogger getLogger(int uLevel)
        {
            Map<String, Object> need = getNeedConfig(uLevel);
            return QuestFactory.getLogger(LogPool.getPool(redisKey, TimeUtil.DAY_SECS), need.keySet());
        }
        
        public QuestChecker getNeed(int uLevel)
        {
            Map<String, Object> need = getNeedConfig(uLevel);
            return QuestFactory.getChecker(CheckerLogPool.getPool(redisKey), need);
        }
        
        public PGPrize getPrize(int userLevel)
        {
            Map<String, Object> prizeData = PGConfig.inst().getDailyQuest()
                    .get(this.index).getPrize(userLevel);
            
            return PrizeFactory.getPrize(prizeData);
        }
        
        public Map<String, Object> buildAMF()
        {
            Map<String, Object> data = new HashMap();
            
            data.put("action_id_list", AMFBuilder.toAMF(this.getAll()));
            
            return data;
        }
        
        public Map<String, Object> buildFullAMF(EntityContext context)
        {
            Map<String, Object> data = this.buildAMF();
            data.put("actions", this.getNeed(questList.getLastAcceptLevel())
                    .buildAMF(context));
            
            return data;
        }
        
        public void generateNewQuest(int uLevel)
        {
            DBContext.Redis().del(redisKey);
            
            CFDailyQuest.ActionPool[] actionPools =
                    PGConfig.inst().getDailyQuest().get(this.index).getActionPools(uLevel);
            
            Random randomer = new Random(System.currentTimeMillis());
            for (CFDailyQuest.ActionPool actionPool : actionPools) {
                DBContext.Redis().lappend(redisKey, actionPool.anyActionID(randomer));
            }
            
            DBContext.Redis().expire(redisKey, TimeUtil.DAY_SECS);
        }
        
        @Deprecated
        public Map<String, Object> getNeedConfig(int uLevel)
        {
            Map<String, Object> need = new HashMap();
            
            CFDailyQuest.ActionPool[] actionPools =
                    PGConfig.inst().getDailyQuest()
                    .get(this.index).getActionPools(uLevel);
            
            List<String> actionIDs = this.getAll();
            for (int i = 0; i < actionIDs.size(); i++) {
                String actionID = actionIDs.get(i);
                CFDailyQuest.ActionPool pool = actionPools[i];
                
                need.putAll(pool.get(actionID));
            }
            
            return need;
        }
    }
}
