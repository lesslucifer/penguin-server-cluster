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
import db.PGKeys;
import db.RedisKey;
import share.AMFBuilder;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadSettings")
public class UserSettings implements PooledEntity
{
    private final String uid;
    private final RedisKey redisKey;

    private UserSettings(String uid) {
        this.uid = uid;
        this.redisKey = redisKey(uid);
    }
    
    public static UserSettings getEntity(String uid)
    {
        return EntityPool.inst().get(UserSettings.class, uid);
    }
    
    private static UserSettings loadSettings(String uid)
    {
        UserSettings settings = new UserSettings(uid);
        return settings;
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis().del(redisKey(uid));
        
        EntityPool.inst().remove(UserSettings.class, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_SETTINGS);
    }
    
    public String getState(String tutID)
    {
        if (DBContext.Redis().hexists(redisKey, tutID))
        {
            return DBContext.Redis().hget(redisKey, tutID);
        }
        
        return null;
    }
    
    public void setSate(String settingID, String setting)
    {
        DBContext.Redis().hset(redisKey, settingID, setting);
    }
    
    public void setStates(Map<String, String> settings)
    {
        DBContext.Redis().hset(redisKey, settings);
    }
    
    public Map<String, Object> buildAMF()
    {
        return AMFBuilder.toAMF(DBContext.Redis().hgetall(redisKey));
    }
    
    public Object dump()
    {
        return buildAMF();
    }
}