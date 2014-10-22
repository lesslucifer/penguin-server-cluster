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
public class MultiBlockedPoolCached<K, V> extends SingleBlockedPoolCached<K, V>
{
    private final MultiloadCache<K, V> cache;
    
    public MultiBlockedPoolCached(MultiloadCache<K, V> cache, Striped<Lock> locks) {
        super(cache, locks);
        
        this.cache = cache;
    }
    
    public List<V> getMulti(K... keys)
    {
        return getMulti(Arrays.asList(keys));
    }
    
    public List<V> getMulti(List<K> keys)
    {
        lockManager.hold(locks, keys);
        return cache.getMulti(keys);
    }
}
