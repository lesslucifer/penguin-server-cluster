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
import share.PGHelper;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadEggStore")
public class EggStore implements PooledEntity{
    private final String TOTAL_EGG_FIELD = "total";
    
    private final RedisKey redisKey;
    
    private EggStore(RedisKey eggStoreKey)
    {
        this.redisKey = eggStoreKey;
    }
    
    public static EggStore getEggStore(RedisKey eggStoreKey)
    {
        return EntityPool.inst().get(EggStore.class, eggStoreKey);
    }
    
    private static EggStore loadEggStore(RedisKey eggStoreKey)
    {
        return new EggStore(eggStoreKey);
    }
    
    public static void destroyEggStore(RedisKey esKey)
    {
        DBContext.Redis().del(esKey);
        EntityPool.inst().remove(EggStore.class, esKey);
    }
    
    public Map<String, Number> getEggs()
    {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        Map<String, Number> eggs = new HashMap(Math.max(data.size()-1, 0));
        
        for (Map.Entry<String, String> eggEntry : data.entrySet()) {
            String eggKind = eggEntry.getKey();
            int nEgg = PGHelper.toInteger(eggEntry.getValue());
            
            if (!TOTAL_EGG_FIELD.equals(eggKind))
            {
                eggs.put(eggKind, nEgg);
            }
        }
        
        return eggs;
    }
    
    public int addEggs(String eggKind, int nEgg)
    {
        DBContext.Redis().hincrby(redisKey, TOTAL_EGG_FIELD, nEgg);
        DBContext.Redis().hincrby(redisKey, eggKind, nEgg);
        
        return nEgg;
    }
    
    public int addEgg(String... eggKinds)
    {
        if (eggKinds.length < 5)
        {
            for (String eggKind : eggKinds) {
                DBContext.Redis().hincrby(redisKey, eggKind, 1);
            }
            
            DBContext.Redis().hincrby(redisKey, TOTAL_EGG_FIELD, eggKinds.length);
            return eggKinds.length;
        }
        else
        {
            Map<String, Number> classifiedEggs = this.classifyEggs(eggKinds);
            return removeEggs(classifiedEggs);
        }
        
    }
    
    public int addEggs(Map<String, Number> eggs)
    {
        int nAdded = 0;
        for (Map.Entry<String, Number> eggEntry : eggs.entrySet()) {
            String eggKind = eggEntry.getKey();
            int nEgg = eggEntry.getValue().intValue();
            
            nAdded += nEgg;
            DBContext.Redis().hincrby(redisKey, eggKind, nEgg);
        }
        
        DBContext.Redis().hincrby(redisKey, TOTAL_EGG_FIELD, nAdded);
        return nAdded;
    }
    
    public int removeEggs(String eggKind, int nEgg)
    {
        int nRemoved;
        int nCurrent = PGHelper.toInteger(DBContext.Redis().hget(redisKey, eggKind));
        if (nCurrent <= nEgg)
        {
            DBContext.Redis().hdel(redisKey, eggKind);
            nRemoved = nCurrent;
        }
        else
        {
            DBContext.Redis().hincrby(redisKey, eggKind, -nEgg);
            nRemoved = nEgg;
        }
        
        if (nRemoved > 0)
        {
            DBContext.Redis().hincrby(redisKey, TOTAL_EGG_FIELD, -nRemoved);
        }
        
        return nRemoved;
    }
    
    public int removeEggs(String... eggKinds)
    {
        if (eggKinds.length < 5)
        {
            for (String eggKind : eggKinds) {
                DBContext.Redis().hincrby(redisKey, eggKind, -1);
            }
            
            DBContext.Redis().hincrby(redisKey, TOTAL_EGG_FIELD, -eggKinds.length);
            return eggKinds.length;
        }
        else
        {
            Map<String, Number> classifiedEggs = this.classifyEggs(eggKinds);
            return removeEggs(classifiedEggs);
        }
    }
    
    public int removeEggs(Map<String, Number> eggs)
    {
        int nRemoved = 0;
        for (Map.Entry<String, Number> eggEntry : eggs.entrySet()) {
            String eggKind = eggEntry.getKey();
            Integer nEgg = eggEntry.getValue().intValue();
            
            nRemoved += nEgg;
            DBContext.Redis().hincrby(redisKey, eggKind, -nEgg);
        }
        
        DBContext.Redis().hincrby(redisKey, TOTAL_EGG_FIELD, -nRemoved);
        return nRemoved;
    }
    
    public int nOfEggs()
    {
        return PGHelper.toInteger(DBContext.Redis()
                .hget(redisKey, TOTAL_EGG_FIELD));
    }
    
    public int nOfEggs(String eggKind)
    {
        return PGHelper.toInteger(DBContext.Redis().hget(redisKey, eggKind));
    }
    
    private Map<String, Number> classifyEggs(String[] eggKinds)
    {
        Map<String, Number> clasifiedEggs = new HashMap();
        
        for (String eggKind : eggKinds)
        {
            if (!clasifiedEggs.containsKey(eggKind))
            {
                clasifiedEggs.put(eggKind, 0);
            }
            
            int nEgg = clasifiedEggs.get(eggKind).intValue();
            clasifiedEggs.put(eggKind, nEgg + 1);
        }
        
        return clasifiedEggs;
    }
    
    public Object dump()
    {
        return DBContext.Redis().hgetall(redisKey);
    }
}
