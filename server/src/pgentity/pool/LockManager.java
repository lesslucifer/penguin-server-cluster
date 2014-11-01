/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import com.google.common.util.concurrent.Striped;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author KieuAnh
 */
public class LockManager<K> {
    private final ThreadLocal<LockCollection> lockCollections = new ThreadLocal();
    
    public void hold(Striped<Lock> locks, K... keys)
    {
        hold(locks, Arrays.asList(keys));
    }
    
    public void hold(Striped<Lock> locks, List<K> keys)
    {
        LockCollection lockCollection = this.lockCollections.get();
        if (lockCollection == null)
        {
            lockCollection = new LockCollection();
            this.lockCollections.set(lockCollection);
        }
        
        for (K key : keys) {
            Lock lock = locks.get(key);
            lockCollection.add(lock);
        }
    }
    
    public void release(Striped<Lock> locks, K... keys)
    {
        release(locks, Arrays.asList(keys));
    }
    
    public void release(Striped<Lock> locks, List<K> keys)
    {
        LockCollection lockCollection = lockCollections.get();
        if (lockCollection != null)
        {
            for (K key : keys) {
                Lock lock = locks.get(key);
                lockCollection.release(lock);
            }
        }
    }
    
    public void releaseAll()
    {
        LockCollection lockCollection = lockCollections.get();
        if (lockCollection != null)
        {
            lockCollection.clear();
            lockCollections.set(null);
        }
    }
}
