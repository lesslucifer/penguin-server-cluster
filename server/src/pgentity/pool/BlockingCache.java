/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.Striped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
class BlockingCache<K, V> {
    private final Lock sync = new ReentrantLock();
    private final Striped<Lock> locks;
    private final Striped<Lock> createLocks;
    private final Cache<K, V> cache;
    private final MultiCacheLoader<K, V> loader;
    private final ThreadLocal<LockCollection> resourceMapper;

    public BlockingCache(Striped<Lock> locks, Cache<K, V> cache,
            MultiCacheLoader<K, V> loader) {
        this.locks = locks;
        this.createLocks = Striped.lazyWeakLock(256);
        this.cache = cache;
        this.loader = loader;
        this.resourceMapper = new ThreadLocal();
    }
    
    public V get(K key)
    {
        this.threadHoldResource(key, locks);
        V resource = this.loadGet(key);
        
        return resource;
    }
    
    public List<V> getMulti(List<K> keys)
    {
        this.threadHoldResources(keys, locks);
        return this.loadGetMulti(keys);
    }
    
    public void put(K key, V v)
    {
        cache.put(key, v);
    }
    
    public void remove(K key)
    {
        cache.invalidate(key);
    }
    
    private V loadGet(K key)
    {
        V v = cache.getIfPresent(key);
        if (v == null)
        {
            try {
                threadHoldResource(key, createLocks);
                if (v == null)
                {
                    v = loader.load(key);
                    cache.put(key, v);
                }
            }
            catch (Exception ex)
            {
                PGException.pgThrow(ex);
            }
            finally
            {
                threadReleaseResource(key, createLocks);
            }
        }
        
        return v;
    }
    
    private List<V> loadGetMulti(List<K> keys)
    {
        List<V> vals = new ArrayList(keys.size());
        Map<K, Integer> needLoads = new HashMap(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            V v = cache.getIfPresent(keys.get(i));
            vals.add(v);
            
            if (v == null)
            {
                needLoads.put(keys.get(i), i);
            }
        }
        
        if (needLoads.size() > 0)
        {
            try
            {
                threadHoldResources(keys, createLocks);
                do
                {
                    boolean ready = true;
                    for (Map.Entry<K, Integer> loadEntry : needLoads.entrySet()) {
                        K k = loadEntry.getKey();
                        Integer idx = loadEntry.getValue();
                        
                        V v = cache.getIfPresent(k);
                        if (v != null)
                        {
                            vals.set(idx, v);
                            needLoads.remove(k);
                            ready = false;
                        }
                    }
                        
                    if (ready)
                    {
                        break;
                    }
                } while (true);
                
                if (needLoads.size() > 0)
                {
                    List<K> loadKeys = new ArrayList(needLoads.keySet());
                    List<V> loadedVals = loader.loadMulti(loadKeys);
                    
                    for (int i = 0; i < loadKeys.size(); i++) {
                        K key = loadKeys.get(i);
                        V v = loadedVals.get(i);
                        vals.set(needLoads.get(key), v);
                        cache.put(key, v);
                    }
                }
            }
            catch (Exception ex)
            {
                PGException.pgThrow(ex);
            }
            finally
            {
                threadReleaseResources(keys, createLocks);
            }
        }
        
        return vals;
    }
    
    /**
     * Thread-safe by ThreadLocal
     * @param resource
     * @param lock 
     */
    private void threadHoldResource(K key, Striped<Lock> stripedLocks)
    {
        LockCollection threadHold = resourceMapper.get();
        if (threadHold == null)
        {
            threadHold = new LockCollection();
            resourceMapper.set(threadHold);
        }

        Lock lock = stripedLocks.get(key);
        threadHold.add(lock);
    }
    
    /**
     * Thread-safe by ThreadLocal
     * @param resource
     * @param lock 
     */
    private void threadHoldResources(List<K> keys, Striped<Lock> stripedLocks)
    {
        LockCollection threadHold = resourceMapper.get();
        if (threadHold == null)
        {
            threadHold = new LockCollection();
            resourceMapper.set(threadHold);
        }

        for (K key : keys) {
            Lock lock = stripedLocks.get(key);
            threadHold.add(lock);
        }
    }
    
    private void threadReleaseResource(K key, Striped<Lock> stripedLocks)
    {
        LockCollection threadHold = resourceMapper.get();
        if (threadHold != null)
        {
            Lock lock = stripedLocks.get(key);
            threadHold.release(lock);
        }
    }
    
    private void threadReleaseResources(List<K> keys, Striped<Lock> stripedLocks)
    {
        LockCollection threadHold = resourceMapper.get();
        if (threadHold != null)
        {
            for (K key : keys) {
                Lock lock = stripedLocks.get(key);
                threadHold.release(lock);
            }
        }
    }
    
    /**
     * Thread-safe by ThreadLocal
     */
    public void releaseThread()
    {
        try {
            LockCollection threadHold = resourceMapper.get();
            if (threadHold != null)
            {
                threadHold.clear();
                resourceMapper.set(null);
            }
        } finally {
        }
    }
}