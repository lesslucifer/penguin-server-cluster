/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.Striped;
import java.util.concurrent.locks.Lock;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class SingleloadCache<K, V>
{
    protected final Cache<K, V> cache;
    private final SingleCacheLoader<K, V> loader;
    protected final LockManager<K> lockManager;
    protected final Striped<Lock> locks;

    public SingleloadCache(Cache<K, V> cache, SingleCacheLoader<K, V> loader,
            LockManager<K> lockManager, Striped<Lock> locks) {
        this.cache = cache;
        this.loader = loader;
        this.lockManager = lockManager;
        this.locks = locks;
    }
    
    public V get(K key)
    {
        V v = cache.getIfPresent(key);
        if (v == null)
        {
            try {
                lockManager.hold(locks, key);
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
                lockManager.release(locks, key);
            }
        }
        
        return v;  
    }
    
    public void put(K k, V v)
    {
        cache.put(k, v);
    }
    
    public void remove(K k)
    {
        cache.invalidate(k);
    }

    LockManager<K> getLockManager() {
        return lockManager;
    }
}