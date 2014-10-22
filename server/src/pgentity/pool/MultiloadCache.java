/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import com.google.common.cache.Cache;
import com.google.common.util.concurrent.Striped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class MultiloadCache<K, V> extends SingleloadCache<K, V>
{
    private final MultiCacheLoader<K, V> loader;

    public MultiloadCache(Cache<K, V> cache, MultiCacheLoader<K, V> loader,
            LockManager<K> lm, Striped<Lock> locks) {
        super(cache, loader, lm, locks);
        this.loader = loader;
    }
    
    public List<V> getMulti(K... keys)
    {
        return getMulti(Arrays.asList(keys));
    }
    
    public List<V> getMulti(List<K> keys)
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
                lockManager.hold(locks, keys);
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
            finally
            {
                lockManager.release(locks, keys);
            }
        }
        
        return vals;
    }
}
