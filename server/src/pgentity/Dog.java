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
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadDog")
public class Dog implements PGEntity
{
    private final String coteID;
    private final RedisKey redisKey;
    private long nextSleep;
    
    private Dog(String uid, String coteID)
    {
        this.coteID = coteID;
        this.redisKey = redisKey(uid, coteID);
    }
    
    public static Dog getDog(String uid, String coteID)
    {
        return EntityPool.inst().get(Dog.class, uid, coteID);
    }
    
    public static Dog loadDog(String uid, String coteID)
    {
        Dog dog = new Dog(uid, coteID);
        dog.updateFromDB();
        
        return dog;
    }
    
    public static Dog newDog(String uid, String coteID, long now)
    {
        Dog dog = new Dog(uid, coteID);
        dog.setNextSleep(now);
        dog.saveToDB();
        
        EntityPool.inst().put(dog, Dog.class, coteID);
        
        return dog;
    }
    
    public static void destroy(String uid, String coteID)
    {
        DBContext.Redis().del(redisKey(uid, coteID));
        EntityPool.inst().remove(Dog.class, uid, coteID);
    }
    
    private static RedisKey redisKey(String uid, String coteID)
    {
        return PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_COTES).getChild(coteID)
                .getChild(PGKeys.FD_DOG);
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        
        this.nextSleep = Long.parseLong(String.valueOf(data.get(PGMacro.NEXT_SLEEP)));
    }

    @Override
    public void saveToDB()
    {
        Map<String, String> data = new HashMap();
        data.put(PGMacro.NEXT_SLEEP, String.valueOf(this.nextSleep));
        
        DBContext.Redis().hset(redisKey, data);
    }

    /**
     * @return the nextSleep
     */
    public long getNextSleep() {
        return nextSleep;
    }

    /**
     * @param nextAwake the nextSleep to set
     */
    public void setNextSleep(long nextAwake) {
        this.nextSleep = nextAwake;
    }
    
    public boolean isAwake(long time)
    {
        return time < this.nextSleep;
    }
    
    public Map<String, Object> buildAMF()
    {
        Map<String, Object> data = new HashMap();
        data.put(PGMacro.NEXT_SLEEP, this.nextSleep);
        
        return data;
    }
    
    public Object dump()
    {
        return DBContext.Redis().hgetall(redisKey);
    }
    
    public static void restore(String uid, String coteID, Map<String, String> data)
    {
        DBContext.Redis().hset(redisKey(uid, coteID), data);
    }
}