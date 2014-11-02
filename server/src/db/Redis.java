/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

/**
 *
 * @author Salm
 */
public interface Redis {

    String set(RedisKey key, String value);

    String get(RedisKey key);
    
    Long del(RedisKey key);

    Boolean exists(RedisKey key);

    String type(RedisKey key);

    Long expire(RedisKey key, int seconds);

    Long expireAt(RedisKey key, long unixTime);

    Long ttl(RedisKey key);

    String getSet(RedisKey key, String value);

    Long setnx(RedisKey key, String value);

    String setex(RedisKey key, int seconds, String value);

    Long decrBy(RedisKey key, long integer);

    Long decr(RedisKey key);

    Long incrBy(RedisKey key, long integer);

    Long incr(RedisKey key);

    Long append(RedisKey key, String value);

    Long hset(RedisKey key, String field, String value);
    
    String hset(RedisKey key, Map<String, String> hash);

    String hget(RedisKey key, String field);

    Long hsetnx(RedisKey key, String field, String value);

    String hmset(RedisKey key, Map<String, String> hash);

    List<String> hmget(RedisKey key, String... fields);

    Long hincrBy(RedisKey key, String field, long value);

    Long hincrby(RedisKey key, String field, long value);

    Boolean hexists(RedisKey key, String field);

    Long hdel(RedisKey key, String... field);

    Long hlen(RedisKey key);

    Set<String> hkeys(RedisKey key);

    List<String> hvals(RedisKey key);

    Map<String, String> hmget(RedisKey key);

    Long rpush(RedisKey key, String... string);

    Long lpush(RedisKey key, String... string);

    Long llen(RedisKey key);

    List<String> lrange(RedisKey key, long start, long end);

    String ltrim(RedisKey key, long start, long end);

    String lindex(RedisKey key, long index);

    String lset(RedisKey key, long index, String value);

    Long lrem(RedisKey key, long count, String value);

    String lpop(RedisKey key);

    String rpop(RedisKey key);

    Long sadd(RedisKey key, String... member);

    Set<String> smembers(RedisKey key);

    Long srem(RedisKey key, String... member);

    String spop(RedisKey key);

    Long scard(RedisKey key);

    Boolean sismember(RedisKey key, String member);

    String srandmember(RedisKey key);
    
    Set<String> sdiff(RedisKey key, RedisKey... keys);
    
    Set<String> sinter(RedisKey key, RedisKey... keys);

    Long zadd(RedisKey key, double score, String member);

    Long zadd(RedisKey key, Map<Double, String> scoreMembers);

    Set<String> zrange(RedisKey key, long start, long end);

    Long zrem(RedisKey key, String... member);

    Double zincrby(RedisKey key, double score, String member);

    Long zrank(RedisKey key, String member);

    Long zrevrank(RedisKey key, String member);

    Set<String> zrevrange(RedisKey key, long start, long end);

    Set<Tuple> zrangeWithScores(RedisKey key, long start, long end);

    Set<Tuple> zrevrangeWithScores(RedisKey key, long start, long end);

    Long zcard(RedisKey key);

    Double zscore(RedisKey key, String member);

    List<String> sort(RedisKey key);

    List<String> sort(RedisKey key, SortingParams sortingParameters);

    Long zcount(RedisKey key, double min, double max);

    Long zcount(RedisKey key, String min, String max);

    Set<String> zrangeByScore(RedisKey key, double min, double max);

    Set<String> zrangeByScore(RedisKey key, String min, String max);

    Set<String> zrevrangeByScore(RedisKey key, double max, double min);

    Set<String> zrangeByScore(RedisKey key, double min, double max, int offset,
            int count);

    Set<String> zrevrangeByScore(RedisKey key, String max, String min);

    Set<String> zrangeByScore(RedisKey key, String min, String max, int offset,
            int count);

    Set<String> zrevrangeByScore(RedisKey key, double max, double min,
            int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(RedisKey key, double min, double max);

    Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, double max, double min);

    Set<Tuple> zrangeByScoreWithScores(RedisKey key, double min, double max,
            int offset, int count);

    Set<String> zrevrangeByScore(RedisKey key, String max, String min,
            int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(RedisKey key, String min, String max);

    Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, String max, String min);

    Set<Tuple> zrangeByScoreWithScores(RedisKey key, String min, String max,
            int offset, int count);

    Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, double max, double min,
            int offset, int count);

    Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, String max, String min,
            int offset, int count);

    Long zremrangeByRank(RedisKey key, long start, long end);

    Long zremrangeByScore(RedisKey key, double start, double end);

    Long zremrangeByScore(RedisKey key, String start, String end);

    Long lpushx(RedisKey key, String string);

    Long rpushx(RedisKey key, String string);
    
    // multi
    Set<String> keys(String patt);
    
    // custom
    
    Long sndiff(RedisKey key, Set<String> vals);

    List<String> lgetall(RedisKey key);
    
    Map<String, String> hgetall(RedisKey key);

    Long lappend(RedisKey key, String... string);
}
