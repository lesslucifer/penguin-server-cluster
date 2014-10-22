/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.util.HashSet;
import java.util.Set;

/**
 * An Redis key
 * @author KieuAnh
 */
public class RedisKey
{
    private static final String ROOT = "p";
    
    //separator
    private static final char SEP = ':';
    
    private String currentKey;
    
    /**
     * return ROOT key
     */
    private RedisKey()
    {
        currentKey = ROOT;
    }
    
    private static final RedisKey root = new RedisKey();
    public static RedisKey root()
    {
        return root;
    }
    
    private RedisKey(String key)
    {
        this.currentKey = key;
    }
    
    public RedisKey getChild(String child)
    {
        return new RedisKey(this.currentKey + SEP + child);
    }
    
    public RedisKey getHashChild(String child)
    {
        return new HashRedisKey(this.currentKey + SEP + child);
    }
    
    public static RedisKey make(String redisKey)
    {
        RedisKey key = new RedisKey(redisKey);
        
        return key;
    }
    
    public static Set<RedisKey> make2(Set<String> stringKeys)
    {
        Set<RedisKey> redisKey = new HashSet<RedisKey>(stringKeys.size());
        
        for (String sKey : stringKeys)
        {
            redisKey.add(RedisKey.make(sKey));
        }
        
        return redisKey;
    }

    @Override
    public String toString() {
        return currentKey;
    }

    @Override
    public int hashCode() {
        return this.currentKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RedisKey other = (RedisKey) obj;
        if ((this.currentKey == null) ? (other.currentKey != null) : !this.currentKey.equals(other.currentKey)) {
            return false;
        }
        return true;
    }
    
    private static class HashRedisKey extends RedisKey
    {
        private static final char H_OPEN = '(';
        private static final char H_CLOSE = ')';
        
        private HashRedisKey(String key)
        {
            super(key);
        }
        
        @Override
        public RedisKey getChild(String child) {
            return super.getChild(H_OPEN + child + H_CLOSE);
        }
    }
}
