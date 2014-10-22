/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import com.google.common.util.concurrent.Striped;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author KieuAnh
 */
public class SingleBlockedPoolCached<K, V> {
    private final SingleloadCache<K, V> cache;
    protected final LockManager<K> lockManager;
    protected final Striped<Lock> locks;

    public SingleBlockedPoolCached(SingleloadCache<K, V> cache,
            Striped<Lock> locks) {
        this.cache = cache;
        this.lockManager = cache.getLockManager();
        this.locks = locks;
    }
    
    public V get(K key)
    {
        lockManager.hold(locks, key);
        return cache.get(key);
    }
    
    public void put(K key, V v)
    {
        cache.put(key, v);
    }
    
    public void remove(K key)
    {
        cache.remove(key);
    }
    
    public void release(K key)
    {
        lockManager.release(locks, key);
    }
    
    public void releaseAll()
    {
        lockManager.releaseAll();
    }
}
