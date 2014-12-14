/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis.set;

import db.DBContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import pgentity.redis.RedisSet;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class RedisSetImpl implements RedisSet
{
    private final RedisKey redisKey;
    private final Lock sync = new ReentrantLock();
    private Set<String> cache;
    private final Set<String> addedElem = new HashSet();
    private final Set<String> removedElem = new HashSet();

    public RedisSetImpl(RedisKey redisKey) {
        this.redisKey = redisKey;
    }
    
    @Override
    public Set<String> getAll()
    {
        if (cache == null)
        {
            try
            {
                sync.lock();
                if (cache == null)
                {
                    cache = this.loadCache();
                }
            }
            finally
            {
                sync.unlock();
            }
        }
        
        return cache;
    }

    @Override
    public void add(String... IDs)
    {
        List<String> lstIDs = Arrays.asList(IDs);
        if (cache != null)
        {
            cache.addAll(lstIDs);
        }
        
        addedElem.addAll(lstIDs);
        removedElem.removeAll(lstIDs);
    }

    @Override
    public void remove(String... IDs) {
        List<String> lstIDs = Arrays.asList(IDs);
        if (cache != null)
        {
            cache.removeAll(lstIDs);
        }
        
        addedElem.removeAll(lstIDs);
        removedElem.addAll(lstIDs);
    }

    @Override
    public boolean contains(String ID) {
        if (cache != null)
        {
            return cache.contains(ID);
        }
        
        return addedElem.contains(ID) || (!removedElem.contains(ID) &&
                DBContext.Redis().sismember(redisKey, ID));
    }

    @Override
    public int size() {
        if (cache != null)
        {
            return cache.size();
        }
        else
        {
            int dataSz = DBContext.Redis().scard(redisKey).intValue();
            int nDiffAdd = DBContext.Redis().sndiff(redisKey, addedElem).intValue();
            int nDiffRem = removedElem.size() -
                    DBContext.Redis().sndiff(redisKey, removedElem).intValue();
            
            return dataSz + nDiffAdd - nDiffRem;
        }
    }

    @Override
    public void updateFromDB() {
        this.cache = null;
        this.addedElem.clear();
        this.removedElem.clear();
    }

    @Override
    public void saveToDB() {
        if (!addedElem.isEmpty())
        {
            String[] addElemArr = addedElem.toArray(new String[addedElem.size()]);
            DBContext.Redis().sadd(redisKey, addElemArr);
            addedElem.clear();
        }
        
        if (!removedElem.isEmpty())
        {
            String[] removeElemArr = removedElem.toArray(new String[removedElem.size()]);
            DBContext.Redis().srem(redisKey, removeElemArr);
            removedElem.clear();
        }
    }
    
    /**
     * Protect by lock
     */
    private Set<String> loadCache()
    {
        Set<String> data = DBContext.Redis().smembers(redisKey);
        if (data == null)
        {
            data = new HashSet(); 
        }
        
        data.addAll(addedElem);
        data.removeAll(removedElem);
        
        return data;
    }
}