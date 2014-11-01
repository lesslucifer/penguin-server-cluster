/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import config.CFAchievements;
import config.CFMainQuests;
import config.PGConfig;
import java.util.HashMap;
import java.util.Map;
import pgentity.Achievement;
import pgentity.DailyQuest;
import pgentity.EntityContext;
import pgentity.MainQuestLine;
import pgentity.User;
import pgentity.quest.QuestChecker;
import pgentity.quest.QuestLogger;
import pgentity.quest.QuestLoggerBuilder;
import pgentity.quest.QuestState;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class QuestServices {
    private QuestServices()
    {
        super();
    }
    
    private static final QuestServices inst = new QuestServices();
    
    public static QuestServices inst()
    {
        return inst;
    }
    
    public void updateQuest(EntityContext context, long now)
    {
        final String uid = context.getUser().getUid();
        
        this.autoAcceptMainQuest(context.getUser());
        
        DailyQuest dailyQuest = DailyQuest.getQuest(uid, now);
        if (!dailyQuest.hasQuest())
        {
            dailyQuest = this.newDailyQuest(context.getUser(), now);
        }
        this.autoAcceptDailyQuest(dailyQuest, context.getUser().getLevel());
        dailyQuest.saveToDB();
    }
    
    public DailyQuest newDailyQuest(User user, long now)
    {
        return DailyQuest.newQuest(user.getUid(), user.getLevel(), now);
    }
    
    public void acceptDailyQuest(DailyQuest quest, EntityContext context)
    {
        int cost = quest.getCost(context.getUser().getLevel());
        PGException.Assert(context.getUser().getCoin() >= cost,
                PGError.NOT_ENOUGH_COIN,
                "Not enough coin; require " + cost
                + " have " + context.getUser().getCoin());
        
        UserServices.inst().decreaseCoin(context.getUser(), cost);
        
        quest.setCurrentState(QuestState.ACCEPTED);
    }
    
    public Map<String, Object> returnDailyQuest(DailyQuest quest,
            EntityContext context, long now)
    {
        final QuestChecker questNeed = quest.getCurrentNeed();
        PGException.Assert(questNeed.isAccept(context), PGError.INCOMPLETED_QUEST,
                        "User haven't completed quest " + quest.getCurrentIndex()+ " yet!\r\n" + 
                        "Current info is: " + questNeed.buildAMF(context));
        
        questNeed.returnQuest(context);
        
        int acceptLevel = quest.getLastAcceptLevel();
        Map<String, Object> pzDesc = quest.getCurrentPrize(acceptLevel).award(context, now);
        this.completeCurrentAndGotoNextDailyQuest(quest, acceptLevel);
        
        return pzDesc;
    }
    
    public Map<String, Object> completeImmediatelyDailyQuest(DailyQuest quest,
            EntityContext context, long now)
    {
        quest.getCurrentNeed().returnQuest(context);
        
        Map<String, Object> pzDesc = quest
                .getCurrentPrize(context.getUser().getLevel()).award(context, now);
        this.completeCurrentAndGotoNextDailyQuest(quest, context.getUser().getLevel());
        
        return pzDesc;
    }
    
    private boolean completeCurrentAndGotoNextDailyQuest(DailyQuest quest,
            int acceptLevel)
    {
        quest.setCurrentState(QuestState.RETURNED);
        
        if (quest.getCurrentIndex() < PGConfig.inst().temp().NumberDailyQuest())
        {
            quest.setCurrentIndex(quest.getCurrentIndex()+ 1);
            quest.setCurrentState(QuestState.NEW);
            autoAcceptDailyQuest(quest, acceptLevel);
            return true;
        }
        
        return false;
    }
    
    private void autoAcceptDailyQuest(DailyQuest dailyQuest, int acceptLevel)
    {
        if (dailyQuest.hasQuest() &&
            dailyQuest.getCurrentState() == QuestState.NEW &&
                dailyQuest.getCost(acceptLevel) <= 0)
        {
            dailyQuest.setCurrentState(QuestState.ACCEPTED);
        }
    }
    
    public void autoAcceptMainQuest(User user)
    {
        Iterable<String> qLines = PGConfig.inst().getMainQuest();
        for (String qLine : qLines) {
            MainQuestLine questLine = MainQuestLine.getQuestLine(user.getUid(), qLine);
            if (!questLine.isValid())
            {
                // valid questline
                CFMainQuests.QuestLine conf = PGConfig.inst().getMainQuest().get(qLine);
                if (conf == null)
                {
                    MainQuestLine.destroyQuestLine(user.getUid(), qLine);
                    continue;
                }
                
                CFMainQuests.QuestLine.QuestLevel lvlConf = conf.get(questLine.getLastAcceptLevel());
                if (lvlConf == null)
                {
                    questLine.setLastAcceptLevel(conf.minimizeLevel(user.getLevel()));
                    lvlConf = conf.get(questLine.getLastAcceptLevel());
                }
                
                CFMainQuests.QuestLine.QuestLevel.Quest qConf = lvlConf.get(questLine.getIndex());
                if (qConf == null)
                {
                    questLine.setIndex(lvlConf.size() - 1);
                    questLine.setState(QuestState.ACCEPTED);
                }
                
                questLine.saveToDB();
            }
            
            if (questLine.getState() == QuestState.RETURNED)
            {
                CFMainQuests.QuestLine qLineConf = PGConfig.inst().getMainQuest() .get(qLine);

                // check pass new quest pool
                int currentMinLevel = qLineConf.minimizeLevel(user.getLevel());
                int lastAcceptMinLevel =  qLineConf.minimizeLevel(questLine.getLastAcceptLevel());
                if (currentMinLevel > lastAcceptMinLevel)
                {
                    questLine.setIndex(0);
                    questLine.setLastAcceptLevel(user.getLevel());
                    questLine.setState(QuestState.ACCEPTED);
                    
                    questLine.saveToDB();
                    return;
                }
                
                int nextIndex = questLine.getIndex() + 1;
                if (qLineConf.get(user.getLevel()).containsKey(nextIndex))
                {
                    questLine.setIndex(nextIndex);
                    questLine.setLastAcceptLevel(user.getLevel());
                    questLine.setState(QuestState.ACCEPTED);
                    
                    questLine.saveToDB();
                    return;
                }
            }
        }
    }
    
    public QuestLogger getQuestLogger(String uid, long now)
    {
        QuestLoggerBuilder qLoggerBuilder = new QuestLoggerBuilder();
        
//        for (String qLine : PGConfig.inst().getMainQuest()) {
//            MainQuestLine questLine = MainQuestLine.getQuestLine(uid, qLine);
//            if (questLine.isBegan() && questLine.getState() == QuestState.ACCEPTED)
//            {
//                try
//                {
//                    qLoggerBuilder.addLogger(questLine.getLogger());
//                }
//                catch (Exception ex)
//                {
//                    PGLog.info("Error when get logger of qline: %s", questLine.toString());
//                }
//            }
//        }
        
        for (String achID : PGConfig.inst().getAchievements()) {
            CFAchievements.Achievement achConfig = PGConfig.inst()
                    .getAchievements().get(achID);
            
            if (achConfig.isEnable())
            {
                Achievement achievement = Achievement.getAchievements(uid, achID);
                qLoggerBuilder.addLogger(achievement.getLogger());
            }
        }
        
        DailyQuest dailyQuest = DailyQuest.getQuest(uid, now);
        if (dailyQuest.hasQuest() && dailyQuest.getCurrentState() == QuestState.ACCEPTED)
        {
            qLoggerBuilder.addLogger(dailyQuest.getCurrentLogger());
        }
        
        return qLoggerBuilder.getLogger();
    }
    
    public Map<String, Object> buildQuestAMF(EntityContext context, long now)
    {
        final String uid = context.getUser().getUid();
        
        Map<String, Object> data = new HashMap();
        
        // build daily
        DailyQuest dailyQuest = DailyQuest.getQuest(context.getUser().getUid(), now);
        data.put("daily", dailyQuest.buildAMF(context));
        
        // build main
        Map<String, Object> mainData = new HashMap();
        Iterable<String> questLines = PGConfig.inst().getMainQuest();
        for (String qLine : questLines) {
            MainQuestLine questLine = MainQuestLine.getQuestLine(uid, qLine);
            if (questLine.isBegan())
            {
                mainData.put(qLine, questLine.buildAMF(context));
            }
        }
        data.put("main", mainData);
        
        return data;
    }
    
    public Map<String, Object> buildAchievementsAMF(EntityContext context, long now)
    {
        CFAchievements achConfig = PGConfig.inst().getAchievements();
        Map<String, Object> achievementData = new HashMap(achConfig.size());
        for (String achID : achConfig) {
            CFAchievements.Achievement ach = achConfig.get(achID);
            
            if (ach.isEnable())
            {
                Achievement achievement = Achievement
                        .getAchievements(context.getUser().getUid(), achID);
                achievementData.put(achID, achievement.buildAMF(context));
            }
        }
        
        return achievementData;
    }
    
    public void destroyMainQuest(String uid)
    {
        Iterable<String> questLines = PGConfig.inst().getMainQuest();
        for (String qLine : questLines) {
            MainQuestLine.destroyQuestLine(uid, qLine);
        }
    }
    
    public Object dumpMainQuest(String uid)
    {
        Map<String, Object> data = new HashMap();
        Iterable<String> questLines = PGConfig.inst().getMainQuest();
        for (String qLine : questLines) {
            data.put(qLine, MainQuestLine.getQuestLine(uid, qLine).dump());
        }
        
        return data;
    }
    
    public void destroyAchievements(String uid)
    {
        CFAchievements achConfig = PGConfig.inst().getAchievements();
        for (String achID : achConfig) {
            Achievement.destroyAchievement(uid, achID);
        }
    }
    
    public Object dumpAchievements(String uid)
    {
        Map<String, Object> data = new HashMap();
        
        CFAchievements achConfig = PGConfig.inst().getAchievements();
        for (String achID : achConfig) {
            data.put(achID, Achievement.getAchievements(uid, achID).dump());
        }
        
        return data;
    }
}