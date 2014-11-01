/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import config.CFAchievements;
import config.PGConfig;
import db.DBContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.pool.PooledEntity;
import pgentity.prize.PGPrize;
import pgentity.prize.PrizeFactory;
import pgentity.quest.QuestChecker;
import pgentity.quest.QuestFactory;
import pgentity.quest.QuestLogger;
import db.PGKeys;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadAchievements")
public class Achievement implements PooledEntity
{    
    private final String uid;
    private final String achID;
    private final RedisKey redisKey;
    
    private Achievement(String uid, String achID) {
        this.uid = uid;
        this.achID = achID;
        this.redisKey = redisKey(uid, achID);
    }
    
    public static Achievement getAchievements(String uid, String achID)
    {
        return EntityPool.inst().get(Achievement.class, uid, achID);
    }
    
    public static Achievement loadAchievements(String uid, String achID)
    {
        Achievement achivements = new Achievement(uid, achID);
        return achivements;
    }
    
    public static void destroyAchievement(String uid, String achID)
    {
        Achievement achievement = getAchievements(uid, achID);
        achievement.getChecker(randomMedal(achID)).destroy();
        
        RedisKey redisKey = redisKey(uid, achID);
        DBContext.Redis().del(redisKey);
        
        EntityPool.inst().remove(Achievement.class, uid, achID);
    }
    
    public static RedisKey redisKey(String uid, String achID)
    {
        return User.redisKey(uid)
                .getChild(PGKeys.FD_QUEST)
                .getChild(PGKeys.FD_ACHIVEMENTS)
                .getChild(achID);
    }
    
    private static String randomMedal(String achID)
    {
        return PGConfig.inst().getAchievements().get(achID)
                .iterator().next();
    }

    public String getUid() {
        return uid;
    }
    
    public boolean isReceivedPrize(String medal)
    {
        String isReceived = DBContext.Redis().hget(redisKey, medal);
        if (isReceived != null)
        {
            return Boolean.parseBoolean(isReceived);
        }
        
        return false;
    }
    
    public void setReceivedPrize(String medal)
    {
        DBContext.Redis().hset(redisKey, medal, String.valueOf(true));
    }
    
    public QuestChecker getChecker(String medal)
    {
        Map<String, Object> achNeed = new HashMap();
        achNeed.put(config().getAction(), config().get(medal).getRequire());
        
        return QuestFactory.getChecker(CheckerLogPool.getPool(redisKey), achNeed);
    }
    
    public PGPrize getPrize(String medal)
    {
        Map<String, Object> prize = config().get(medal).getPrize();
        return PrizeFactory.getPrize(prize);
    }
    
    public QuestLogger getLogger()
    {
        Set<String> loggers = new HashSet(1);
        loggers.add(config().getAction());
        
        return QuestFactory.getLogger(LogPool.getPool(redisKey), loggers);
    }
    
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap(2);
        
        Map<String, Object> receivedPrize = (Map) DBContext.Redis().hgetall(redisKey);
        
        Map<String, Object> achData = getChecker(randomMedal(achID)).buildAMF(context);
        
        data.put("received_prize", receivedPrize);
        data.put("data", achData);
        
        return data;
    }
    
    private CFAchievements.Achievement config()
    {
        return PGConfig.inst().getAchievements().get(achID);
    }
    
    /**
     * use QuestService.dumpAchievements to dump all user's achievements
     * @return
     */
    @Deprecated
    public Object dump()
    {
        Map<String, Object> data = new HashMap(2);
        data.put("data", DBContext.Redis().hgetall(redisKey));
        data.put("log", getChecker(randomMedal(achID)).dump());
        return data;
    }
}