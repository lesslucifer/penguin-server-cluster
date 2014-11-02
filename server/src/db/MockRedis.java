/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author KieuAnh
 */
public class MockRedis// implements PGRedis
{
    /*
    private static final String OK = "OK";
    private Map<RedisKey, Object> db = new HashMap();
    
    @Override
    public long del(RedisKey key)
    {
        return db.remove(key) == null?0:1;
    }

    @Override
    public long del(RedisKey[] keys)
    {
        long nRemoved = 0;
        for (RedisKey key : keys) {
            nRemoved += db.remove(key) == null?0:1;
        }
        
        return nRemoved;
    }

    @Override
    public long expire(RedisKey key, int secs)
    {
        return secs;
    }

    @Override
    public long expireAt(RedisKey key, long time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String get(RedisKey key)
    {
        try
        {
            return (String) db.get(key);
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public long hDel(RedisKey key, String... fields)
    {
        if (!this.db.containsKey(key))
        {
            return 0;
        }
        
        Map<String, String> hash = (Map<String, String>) this.db.get(key);
        
        long nRemoved = 0;
        for (String field : fields) {
            nRemoved += hash.remove(field)==null?0:1;
        }
        
        return nRemoved;
    }

    @Override
    public String hget(RedisKey key, String field)
    {
        if (!this.db.containsKey(key))
        {
            return null;
        }
        
        
        try
        {
            Map<String, String> hash = (Map<String, String>) this.db.get(key);

            return hash.get(field);
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public Map<String, String> hgetall(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return Collections.EMPTY_MAP;
        }
        
        try
        {
            return (Map<String, String>) this.db.get(key);
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public String hset(RedisKey key, Map<String, String> val)
    {
        if (!this.db.containsKey(key))
        {
            this.db.put(key, new HashMap());
        }
        
        try
        {
            Map<String, String> hash = (Map<String, String>) this.db.get(key);
            hash.putAll(val);
        }
        catch (ClassCastException ex)
        {
            return null;
        }
        
        return OK;
    }

    @Override
    public void hset(RedisKey key, String field, String val)
    {
        if (!this.db.containsKey(key))
        {
            this.db.put(key, new HashMap());
        }
        
        try
        {
            Map<String, String> hash = (Map<String, String>) this.db.get(key);
            hash.put(field, val);
        }
        catch (ClassCastException ex)
        {
            
        }
    }

    @Override
    public boolean isExists(RedisKey key)
    {
        return this.db.containsKey(key);
    }

    @Override
    public Set<String> keys(String keyPattern) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long lappend(RedisKey key, String vals) {
        if (!this.db.containsKey(key))
        {
            this.db.put(key, new ArrayList());
        }
        
        try
        {
            List<String> list = (List<String>) this.db.get(key);

            if (list.add(vals))
            {
                return 1;
            }

            return 0;
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }

    @Override
    public List<String> lgetall(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return Collections.EMPTY_LIST;
        }
        
        try
        {
            return (List<String>) this.db.get(key);
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public String lgetat(RedisKey key, int index)
    {
        if (!this.db.containsKey(key))
        {
            return null;
        }
        
        try
        {
            List<String> list = (List<String>) this.db.get(key);
            
            if (list.size() > index)
            {
                return list.get(index);
            }
            
            return null;
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public long llen(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return 0;
        }
        
        try
        {
            List<String> list = (List<String>) this.db.get(key);

            return list.size();
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }

    @Override
    public long lpush(RedisKey key, String vals) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long sadd(RedisKey key, String val) {
        if (!this.db.containsKey(key))
        {
            this.db.put(key, new HashSet());
        }
        
        Set<String> set = (Set<String>) this.db.get(key);
        
        return set.add(val)?1:0;
    }

    @Override
    public long sadd(RedisKey key, String[] vals)
    {
        if (!this.db.containsKey(key))
        {
            this.db.put(key, new HashSet());
        }
        
        try
        {
            Set<String> set = (Set<String>) this.db.get(key);

            return set.addAll(Arrays.asList(vals))?vals.length:0;
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }

    @Override
    public long scard(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return 0;
        }
        
        try
        {
            Set<String> set = (Set<String>) this.db.get(key);
            return set.size();
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }

    @Override
    public long set(RedisKey key, String val)
    {
        this.db.put(key, val);
        return 1;
    }

    @Override
    public boolean sismember(RedisKey key, String member)
    {
        if (!this.db.containsKey(key))
        {
            return false;
        }
        
        try
        {
            Set<String> set = (Set<String>) this.db.get(key);
            return set.contains(member);
        }
        catch (ClassCastException ex)
        {
            return false;
        }
    }

    @Override
    public Set<String> smembers(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return new HashSet(Collections.EMPTY_SET);
        }
        
        try
        {
            return new HashSet((Set<String>) this.db.get(key));
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public long smove(RedisKey from, RedisKey to, String val)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String spop(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return null;
        }
        
        try
        {
            Set<String> set = (Set<String>) this.db.get(key);
            if (set.isEmpty())
            {
                return null;
            }

            String anyString = set.iterator().next();
            set.remove(anyString);

            return anyString;
        }
        catch (ClassCastException ex)
        {
            return null;
        }
    }

    @Override
    public long srem(RedisKey key, String val)
    {
        if (!this.db.containsKey(key))
        {
            return 0;
        }
        
        try
        {
            Set<String> set = (Set<String>) this.db.get(key);

            return set.remove(val)?1:0;
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }

    @Override
    public long srem(RedisKey key, String[] vals)
    {
        if (!this.db.containsKey(key))
        {
            return 0;
        }
        
        try
        {
            Set<String> set = (Set<String>) this.db.get(key);

            long nRemoved = 0;
            for (String val : vals) {
                if (set.remove(val))
                {
                    ++nRemoved;
                }
            }

            return nRemoved;
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }
    
    @Override
    public long hincrby(RedisKey key, String hKey, long incrBy)
    {
        try
        {
            if (!this.db.containsKey(key))
            {
                this.db.put(key, new HashMap());
            }
            
            Map<String, String> hash = (Map<String, String>) this.db.get(key);
            if (!hash.containsKey(hKey))
            {
                hash.put(hKey, String.valueOf(incrBy));
                return incrBy;
            }
            String strVal = hash.get(hKey);
            
            long val = Long.parseLong(strVal);
            val += incrBy;

            strVal = String.valueOf(val);
            hash.put(hKey, strVal);

            this.db.put(key, hash);

            return val;
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
        catch (NumberFormatException ex)
        {
            return -1;
        }
    }
    
    @Override
    public int hlen(RedisKey key)
    {
        if (!this.db.containsKey(key))
        {
            return 0;
        }
        
        try
        {
            Map<String, String> hash = (Map<String, String>) this.db.get(key);
            return hash.size();
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }
    
    @Override
    public int incrby(RedisKey key, long by)
    {
        if (!this.db.containsKey(key))
        {
            this.db.put(key, String.valueOf(by));
        }
        
        try
        {
            long val = PGHelper.toLong(this.db.get(key));
            val += by;
            this.db.put(key, String.valueOf(val));
            return (int) val;
        }
        catch (NumberFormatException ex)
        {
            return -1;
        }
        catch (ClassCastException ex)
        {
            return -1;
        }
    }
    
    @Override
    public boolean hexists(RedisKey key, String field)
    {
        if (!this.db.containsKey(key))
        {
            return false;
        }
        
        try
        {
            Map<String, String> hash = (Map<String, String>) this.db.get(key);
            return hash.containsKey(field);
        }
        catch (ClassCastException ex)
        {
            return false;
        }
    }

    @Override
    public List<String> lrange(RedisKey key, int start, int end)
    {
        if (!this.db.containsKey(key))
        {
            return null;
        }
        
        try
        {
            List<String> list = (List<String>) this.db.get(key);
            if (start < 0)
            {
                start = list.size() + start + 1;
            }
            if (end < 0)
            {
                end = list.size() + end + 1;
            }
            return list.subList(start, end);
        }
        catch (ClassCastException ex)
        {
            return null;
        }
        catch (IndexOutOfBoundsException ex)
        {
            return null;
        }
    }

    @Override
    public Set<String> sinter(RedisKey... keys) {
        if (keys.length >= 1)
        {
            Set<String> result = new HashSet(this.smembers(keys[0]));
            
            for (int i = 1; i < keys.length; ++i)
            {
                result = Sets.intersection(result, this.smembers(keys[i]));
            }
        }
        
        return null;
    }

    @Override
    public Set<String> sdiff(RedisKey fromKey, RedisKey... toKeys) {
        Set<String> result = new HashSet(this.smembers(fromKey));
        
        for (RedisKey key : toKeys) {
            result = Sets.difference(result, this.smembers(key));
        }
        
        return result;
    }
    */
}