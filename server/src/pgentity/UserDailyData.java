/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.pool.PooledEntity;
import share.PGError;
import db.PGKeys;
import db.RedisKey;
import share.PGException;
import share.TimeUtil;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadData")
public class UserDailyData implements PooledEntity {
    private final String uid;
    private final RedisKey redisKey;

    private UserDailyData(String uid, String dayToken) {
        this.uid = uid;
        this.redisKey = getRedisKey(uid, dayToken);
    }
    
    public static UserDailyData getData(String uid, long now)
    {
        String dayToken = TimeUtil.dayToken(now);
        return EntityPool.inst().get(UserDailyData.class, uid, dayToken);
    }
    
    private static UserDailyData loadData(String uid, String dayToken)
    {
        PGException.Assert(User.isExist(uid), PGError.INVALID_USER,
                String.format("%s doesn't exist", uid));
        
        UserDailyData userDailyData = new UserDailyData(uid, dayToken);
        return userDailyData;
    }
    
    private static RedisKey getRedisKey(String uid, String dayToken)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_DAILY_DATA)
                .getChild(dayToken);
    }
            
    public String getData(String dataID)
    {
        String data = DBContext.Redis().hget(redisKey, dataID);
        return (data != null)?data:"";
    }
    
    public void setData(String dataID, Object data)
    {
        boolean isKeyExisted = DBContext.Redis().exists(redisKey);
        DBContext.Redis().hset(redisKey, dataID, String.valueOf(data));
        
        if (!isKeyExisted)
        {
            DBContext.Redis().expire(redisKey, TimeUtil.DAY_SECS);
        }
    }
    
    public void increaseData(String dataID, int inc)
    {
        boolean isKeyExisted = DBContext.Redis().exists(redisKey);
        DBContext.Redis().hincrby(redisKey, dataID, inc);
        
        if (!isKeyExisted)
        {
            DBContext.Redis().expire(redisKey, TimeUtil.DAY_SECS);
        }
    }
    
    public Map<String, Object> buildAMF()
    {
        return (Map) DBContext.Redis().hgetall(redisKey);
    }
    
    public Object dump()
    {
        return buildAMF();
    }
}
