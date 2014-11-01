/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import config.CFMainQuests;
import config.PGConfig;
import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.prize.PGPrize;
import pgentity.prize.PrizeFactory;
import pgentity.quest.QuestChecker;
import pgentity.quest.QuestFactory;
import pgentity.quest.QuestState;
import share.PGError;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadQuestLine")
public class MainQuestLine implements PGEntity
{
    private final String uid;
    private final String qLine;
    private final RedisKey redisKey;
    
    private int lastAcceptLevel;
    private int index;
    private QuestState state;

    private MainQuestLine(String uid, String qLine) {
        this.uid = uid;
        this.qLine = qLine;
        this.redisKey = redisKey(uid, qLine);
    }
    
    public static MainQuestLine getQuestLine(String uid, String qLine)
    {
        return EntityPool.inst().get(MainQuestLine.class, uid, qLine);
    }
    
    private static MainQuestLine loadQuestLine(String uid, String qLine)
    {
        PGException.Assert(isExist(uid, qLine), PGError.INVALID_QUEST_LINE,
                "Questline " + qLine + " is invalid");
        
        MainQuestLine questLine = new MainQuestLine(uid, qLine);
        questLine.updateFromDB();
        
        return questLine;
    }
    
    public static MainQuestLine newQuestLine(String uid, String qLine)
    {
        MainQuestLine questLine = new MainQuestLine(uid, qLine);
        questLine.setIndex(0);
        questLine.setState(QuestState.ACCEPTED);
        questLine.setLastAcceptLevel(1);
        questLine.saveToDB();
        
        EntityPool.inst().put(questLine, MainQuestLine.class, uid, qLine);
        
        return questLine;
    }
    
    public static MainQuestLine newLockedQuestLine(String uid, String qLine)
    {
        MainQuestLine questLine = new MainQuestLine(uid, qLine);
        questLine.setIndex(0);
        questLine.setState(QuestState.RETURNED);
        questLine.setLastAcceptLevel(1);
        questLine.saveToDB();
        
        EntityPool.inst().put(questLine, MainQuestLine.class, uid, qLine);
        
        return questLine;
    }
    
    public static void destroyQuestLine(String uid, String qLine)
    {
        try
        {
            MainQuestLine mainQuest = getQuestLine(uid, qLine);
            mainQuest.getChecker().destroy();
            DBContext.Redis().del(redisKey(uid, qLine));
        }
        catch (Exception ex)
        {
            // cannot destroy
        }
        
        EntityPool.inst().remove(MainQuestLine.class, uid, qLine);
    }
    
    public static boolean isExist(String uid, String qLine)
    {
        return DBContext.Redis().exists(redisKey(uid, qLine));
    }
    
    public static RedisKey redisKey(String uid, String qLine)
    {
        return User.redisKey(uid)
                .getChild(PGKeys.FD_QUEST)
                .getChild(PGKeys.FD_MAIN_QUEST)
                .getChild(qLine);
    }
    
    public boolean isBegan()
    {
        return !(this.index <= 0 && this.state == QuestState.NEW);
    }

    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(this.redisKey);
        
        this.lastAcceptLevel = Integer.parseInt(data.get(PGMacro.LAST_ACCEPT_LEVEL));
        this.index = Integer.parseInt(data.get(PGMacro.MAIN_QUEST_INDEX));
        this.state = QuestState.get(Integer.parseInt(data.get(PGMacro.QUEST_STATE)));
    }

    @Override
    public void saveToDB()
    {
        Map<String, String> data = new HashMap();
        
        data.put(PGMacro.LAST_ACCEPT_LEVEL, String.valueOf(this.lastAcceptLevel));
        data.put(PGMacro.MAIN_QUEST_INDEX, String.valueOf(this.index));
        data.put(PGMacro.QUEST_STATE, String.valueOf(this.state.getValue()));
        
        DBContext.Redis().hset(this.redisKey, data);
    }

    public String getUid() {
        return uid;
    }

    public String getQuestLine() {
        return qLine;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    /**
     * @return the lastAcceptLevel
     */
    public int getLastAcceptLevel() {
        return lastAcceptLevel;
    }

    /**
     * @param lastAcceptLevel the lastAcceptLevel to set
     */
    public void setLastAcceptLevel(int lastAcceptLevel) {
        this.lastAcceptLevel = lastAcceptLevel;
    }
    
    public QuestChecker getChecker()
    {
        Map<String, Object> need = null;
        if (this.index >= 0)
        {
            need = getConfig().getNeed();
        }
        
        return QuestFactory.getChecker(CheckerLogPool.getPool(this.redisKey), need);
    }
    
    public PGPrize getPrize()
    {
        Map<String, Object> prize = null;
        if (this.index >= 0)
        {
            prize = getConfig().getPrize();
        }
        
        return PrizeFactory.getPrize(prize);
    }
    
    public boolean isValid()
    {
        try
        {
            return getConfig() != null;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
//    public QuestLogger getLogger()
//    {
//        Set<String> loggers = null;
//        if (this.index >= 0 && this.state == QuestState.ACCEPTED)
//        {
//            loggers = getConfig().getNeed().keySet();
//        }
//        
//        return QuestFactory.getLogger(LogPool.getPool(redisKey), loggers);
//    }
    
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap(2);
        
        data.put("accepted_level", this.lastAcceptLevel);
        
        Map<String, Object> questData = new HashMap(1);
        Map<String, Object> currentData = new HashMap(2);
        currentData.put(PGMacro.QUEST_STATE, this.state.getValue());
        if (this.state == QuestState.ACCEPTED)
        {
            currentData.put("actions", this.getChecker().buildAMF(context));
        }
        questData.put(String.valueOf(this.index), currentData);
        
        data.put("data", questData);
        
        return data;
    }
    
    private CFMainQuests.QuestLine.QuestLevel.Quest getConfig()
    {
        return PGConfig.inst().getMainQuest().get(this.qLine)
                .get(this.lastAcceptLevel).get(this.index);
    }
    
    /**
     * use QuestServices.dumpMainQuest for dump all user's MainQuest
     * @return
     * @deprecated
     */
    @Deprecated
    public Object dump()
    {
        Map<String, Object> data = new HashMap();
        data.put("data", DBContext.Redis().hgetall(redisKey));
        
        return data;
    }

    @Override
    public String toString() {
        return "MainQuestLine{" + "uid=" + uid + ", qLine=" + qLine + ", redisKey=" + redisKey + ", lastAcceptLevel=" + lastAcceptLevel + ", index=" + index + ", state=" + state + '}';
    }
}
