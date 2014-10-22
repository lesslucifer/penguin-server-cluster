/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
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
@EntityFactory(factorier = "loadEntity")
public class UIData implements PooledEntity{
    private final String uid;
    private final RedisKey redisKey;

    private UIData(String uid) {
        this.uid = uid;
        this.redisKey = redisKey(uid);
    }
    
    public static UIData getEntity(String uid)
    {
        return EntityPool.inst().get(UIData.class, uid);
    }
    
    private static UIData loadEntity(String uid)
    {
        return new UIData(uid);
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis().del(redisKey(uid));
        
        EntityPool.inst().remove(UIData.class, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return User.redisKey(uid).getChild(PGKeys.FD_UIDATA);
    }
    
    public int getState(String uiID)
    {
        if (DBContext.Redis().hexists(redisKey, uiID))
        {
            return Integer.parseInt(DBContext.Redis().hget(redisKey, uiID));
        }
        
        return 0;
    }
    
    public void setSate(String uiID, int state)
    {
        DBContext.Redis().hset(redisKey, uiID, String.valueOf(state));
    }
    
    public void setStates(Map<String, Number> states)
    {
        Map<String, String> redisStates = new HashMap();
        for (Map.Entry<String, Number> stateEntry : states.entrySet()) {
            String tutID = stateEntry.getKey();
            Number state = stateEntry.getValue();
            
            redisStates.put(tutID, String.valueOf(state));
        }
        
        DBContext.Redis().hset(redisKey, redisStates);
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
