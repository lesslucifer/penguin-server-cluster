/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libCore.config.Config;

/**
 *
 * @author KieuAnh
 */
class PGRedisImpl implements PGRedis
{
    private RedisClient jedis;
    
    private PGRedisImpl()
    {
        super();
        
        String host = Config.getParam("redis", "host");
        int port = Integer.valueOf(Config.getParam("redis", "port"));
        String password = Config.getParam("redis", "pass");
        int database = Integer.valueOf(Config.getParam("redis", "database"));
        jedis = RedisClient.getInstance(host, port, password, database);
    }
    
    private static final PGRedisImpl inst = new PGRedisImpl();
    
    public static PGRedisImpl inst()
    {
        return inst;
    }
    
    @Override
    public String get(RedisKey key)
    {
        return jedis.get(key.toString());
    }
    
    @Override
    public Map<String, String> hgetall(RedisKey key)
    {
        return jedis.getHm(key.toString());
    }
    
    @Override
    public boolean sismember(RedisKey key, String member)
    {
        return (jedis.sismember(key.toString(), member) == 1);
    }
    
    @Override
    public Set<String> smembers(RedisKey key)
    {
        return jedis.smember(key.toString());
    }
    
    @Override
    public long scard(RedisKey key)
    {
        return jedis.scard(key.toString());
    }
    
    @Override
    public String hget(RedisKey key, String field)
    {
        return jedis.hget(key.toString(), field);
    }
    
    @Override
    public boolean isExists(RedisKey key)
    {
        return jedis.isExits(key.toString());
    }
    
    @Override
    public List<String> lgetall(RedisKey key)
    {
        return jedis.list_getAll(key.toString());
    }
    
    @Override
    public String lgetat(RedisKey key, int index)
    {
        return jedis.list_getAt(key.toString(), index);
    }
    
    @Override
    public long llen(RedisKey key)
    {
        return jedis.llen(key.toString());
    }
    
    // ************************************************************
    
    @Override
    public long set(RedisKey key, String val)
    {
        return jedis.set(key.toString(), val);
    }
    
    @Override
    public String hset(RedisKey key, Map<String, String> val)
    {
        return jedis.setHm(key.toString(), val);
    }
    
    @Override
    public void hset(RedisKey key, String field, String val)
    {
        jedis.hset(key.toString(), field, val);
    }
    
    @Override
    public long sadd(RedisKey key, String val)
    {
        return jedis.sadd(key.toString(), val);
    }
    
    @Override
    public long sadd(RedisKey key, String[] vals)
    {
        return jedis.sadd(key.toString(), vals);
    }
    
    @Override
    public long srem(RedisKey key, String val)
    {
        return jedis.srem(key.toString(), val);
    }
    
    @Override
    public long srem(RedisKey key, String[] vals)
    {
        return jedis.srem(key.toString(), vals);
    }
    
    @Override
    public long smove(RedisKey from, RedisKey to, String val)
    {
        return jedis.smove(to.toString(), from.toString(), val);
    }
    
    @Override
    public String spop(RedisKey key)
    {
        return jedis.spop(key.toString());
    }
    
    @Override
    public long hDel(RedisKey key, String... fields)
    {
        return jedis.hDel(key.toString(), fields);
    }
    
    @Override
    public long del(RedisKey key)
    {
        return jedis.del(key.toString());
    }
    
    @Override
    public long del(RedisKey[] keys)
    {
        String[] strKeys = new String[keys.length];
        int i = 0;
        for (RedisKey key : keys)
        {
            strKeys[i++] = key.toString();
        }
        
        return jedis.del(strKeys);
    }
    
    @Override
    public long lpush(RedisKey key, String vals)
    {
        return jedis.list_push(key.toString(), vals);
    }
    
    @Override
    public long lappend(RedisKey key, String vals)
    {
        return jedis.list_append(key.toString(), vals);
    }
    
    @Override
    public long expireAt(RedisKey key, long time)
    {
        return jedis.expireAt(key.toString(), time);
    }
    
    @Override
    public long expire(RedisKey key, int secs)
    {
        return jedis.expire(key.toString(), secs);
    }
    
    @Override
    public Set<String> keys(String keyPattern)
    {
        return jedis.keys(keyPattern);
    }

    @Override
    public long hincrby(RedisKey key, String hKey, long incrBy)
    {
        return jedis.hincrby(key.toString(), hKey, incrBy);
    }

    @Override
    public int hlen(RedisKey key) {
        return jedis.hlen(key.toString());
    }

    @Override
    public int incrby(RedisKey key, long by)
    {
        return jedis.incrby(key.toString(), by);
    }

    @Override
    public long incr(RedisKey key)
    {
        return jedis.incr(key.toString());
    }

    @Override
    public boolean hexists(RedisKey key, String field) {
        return jedis.hexists(key.toString(), field);
    }

    @Override
    public List<String> lrange(RedisKey key, int start, int end) {
        return jedis.lrange(key.toString(), start, end);
    }

    @Override
    public Set<String> sinter(RedisKey... keys) {
        String[] sKeys = new String[keys.length];
        
        for (int i = 0; i < sKeys.length; i++) {
            sKeys[i] = keys[i].toString();
        }
        
        return jedis.sinter(sKeys);
    }

    @Override
    public Set<String> sdiff(RedisKey fromKey, RedisKey... toKeys) {
        String[] sKeys = new String[toKeys.length + 1];
        
        sKeys[0] = fromKey.toString();
        for (int i = 0; i < sKeys.length; i++) {
            sKeys[i + 1] = toKeys[i].toString();
        }
        
        return jedis.sdiff(sKeys);
    }

    @Override
    public int sndiff(RedisKey key, Collection<String> vals) {
        return jedis.sndiff(key.toString(), vals);
    }
    
    @Override
    public String type(RedisKey key)
    {
        return jedis.type(key.toString());
    }

    @Override
    public long zadd(RedisKey key, Number score, String val) {
        return jedis.zadd(key.toString(), val, score.doubleValue());
    }

    @Override
    public long zadd(RedisKey key, Map<Double, String> vals) {
        return jedis.zadd(key.toString(), vals);
    }

    @Override
    public Set<String> zrangebyscore(RedisKey key, Number min, Number max) {
        return jedis.zrangebyscore(key.toString(), min, max);
    }

    @Override
    public void zrem(RedisKey key, String... fields) {
        if (fields.length > 0)
        {
            jedis.zremMulti(key.toString(), fields);
        }
    }
}