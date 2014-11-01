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

/**
 *
 * @author KieuAnh
 */
interface PGRedis
{
    public static final int ERROR = -1;
    
    long del(RedisKey key);

    long del(RedisKey[] keys);

    long expire(RedisKey key, int secs);

    long expireAt(RedisKey key, long time);

    String get(RedisKey key);

    long hDel(RedisKey key, String... fields);

    String hget(RedisKey key, String field);

    Map<String, String> hgetall(RedisKey key);

    String hset(RedisKey key, Map<String, String> val);
    
    void hset(RedisKey key, String field, String val);

    boolean isExists(RedisKey key);

    Set<String> keys(String keyPattern);

    long lappend(RedisKey key, String vals);

    List<String> lgetall(RedisKey key);

    String lgetat(RedisKey key, int index);

    long llen(RedisKey key);

    long lpush(RedisKey key, String vals);

    long sadd(RedisKey key, String val);

    long sadd(RedisKey key, String[] vals);

    long scard(RedisKey key);

    // ************************************************************
    long set(RedisKey key, String val);

    boolean sismember(RedisKey key, String member);

    Set<String> smembers(RedisKey key);

    long smove(RedisKey from, RedisKey to, String val);

    String spop(RedisKey key);

    long srem(RedisKey key, String val);

    long srem(RedisKey key, String[] vals);
    
    long hincrby(RedisKey key, String hKey, long incrBy);
    
    int hlen(RedisKey key);
    
    int incrby(RedisKey key, long by);
    
    long incr(RedisKey key);
    
    boolean hexists(RedisKey key, String field);
    
    List<String> lrange(RedisKey key, int start, int length);
    
    Set<String> sinter(RedisKey... keys);
    
    Set<String> sdiff(RedisKey fromKey, RedisKey... toKeys);
    
    int sndiff(RedisKey key, Collection<String> vals);
    String type(RedisKey key);
    
    long zadd(RedisKey key, Number score, String val);
    long zadd(RedisKey key, Map<Double, String> vals);
    Set<String> zrangebyscore(RedisKey key, Number from, Number to);
    void zrem(RedisKey key, String... fields);
}