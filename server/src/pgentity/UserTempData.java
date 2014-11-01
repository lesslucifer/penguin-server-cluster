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


/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadTempData")
public class UserTempData implements PooledEntity
{
    private final String uid;
    private final RedisKey redisKey;

    private UserTempData(String uid) {
        this.uid = uid;
        
        this.redisKey = redisKey(uid);
    }
    
    public static UserTempData getTempData(String uid)
    {
        return EntityPool.inst().get(UserTempData.class, uid);
    }
    
    private static UserTempData loadTempData(String uid)
    {
        PGException.Assert(User.isExist(uid), PGError.INVALID_USER,
                String.format("%s doesn't exist", uid));
        
        UserTempData userTemp = new UserTempData(uid);
        return userTemp;
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis().del(PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_TEMP_DATA));
        
        EntityPool.inst().remove(UserTempData.class, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return User.redisKey(uid).getChild(PGKeys.FD_TEMP_DATA);
    }
    
    public String getData(String dataID)
    {
        String data = DBContext.Redis().hget(redisKey, dataID);
        return (data != null)?data:"";
    }
    
    public void setData(String dataID, Object data)
    {
        DBContext.Redis().hset(redisKey, dataID, String.valueOf(data));
    }
    
    public void incData(String dataID, int by)
    {
        DBContext.Redis().hincrby(redisKey, dataID, by);
    }

    public String getUid() {
        return uid;
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