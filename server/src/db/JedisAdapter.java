/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

/**
 *
 * @author Salm
 */
class JedisAdapter implements Redis {

    private final JedisPool pool;

    public JedisAdapter(JedisPool jedisPool) {
        this.pool = jedisPool;
    }

    @Override
    public String set(RedisKey key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key.toString(), value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String get(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long del(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Boolean exists(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.exists(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String type(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.type(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long expire(RedisKey key, int seconds) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expire(key.toString(), seconds);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long expireAt(RedisKey key, long unixTime) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.expireAt(key.toString(), unixTime);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long ttl(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.ttl(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String getSet(RedisKey key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.getSet(key.toString(), value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long setnx(RedisKey key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setnx(key.toString(), value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String setex(RedisKey key, int seconds, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setex(key.toString(), seconds, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long decrBy(RedisKey key, long integer) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.decrBy(key.toString(), integer);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long decr(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.decr(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long incrBy(RedisKey key, long integer) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.incrBy(key.toString(), integer);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long incr(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.incr(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long append(RedisKey key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.append(key.toString(), value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long hset(RedisKey key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hset(key.toString(), field, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String hset(RedisKey key, Map<String, String> hash) {
        if (hash.isEmpty()) return "OK";
        return hmset(key, hash);
    }

    @Override
    public String hget(RedisKey key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hget(key.toString(), field);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long hsetnx(RedisKey key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hsetnx(key.toString(), field, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String hmset(RedisKey key, Map<String, String> hash) {
        if (hash.isEmpty()) return "OK";
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmset(key.toString(), hash);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public List<String> hmget(RedisKey key, String... fields) {
        if (fields.length <= 0) return Collections.EMPTY_LIST;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hmget(key.toString(), fields);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long hincrBy(RedisKey key, String field, long value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hincrBy(key.toString(), field, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long hincrby(RedisKey key, String field, long value) {
        return hincrBy(key, field, value);
    }

    @Override
    public Boolean hexists(RedisKey key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hexists(key.toString(), field);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long hdel(RedisKey key, String... field) {
        if (field.length <= 0) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hdel(key.toString(), field);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long hlen(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hlen(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> hkeys(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hkeys(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public List<String> hvals(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hvals(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Map<String, String> hmget(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.hgetAll(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Map<String, String> hgetall(RedisKey key) {
        return hmget(key);
    }

    @Override
    public Long rpush(RedisKey key, String... string) {
        if (string.length <= 0) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.rpush(key.toString(), string);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long lpush(RedisKey key, String... string) {
        if (string.length <= 0) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key.toString(), string);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
    
    @Override
    public Long lappend(RedisKey key, String... strings)
    {
        return lpush(key, strings);
    }

    @Override
    public Long llen(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.llen(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public List<String> lrange(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
    
    @Override
    public List<String> lgetall(RedisKey key)
    {
        return lrange(key, 0, -1);
    }

    @Override
    public String ltrim(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.ltrim(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String lindex(RedisKey key, long index) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lindex(key.toString(), index);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String lset(RedisKey key, long index, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lset(key.toString(), index, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long lrem(RedisKey key, long count, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrem(key.toString(), count, value);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String lpop(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpop(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String rpop(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.rpop(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long sadd(RedisKey key, String... member) {
        if (member.length <= 0) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> smembers(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.smembers(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long srem(RedisKey key, String... member) {
        if (member.length <= 0) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String spop(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.spop(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long scard(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Boolean sismember(RedisKey key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public String srandmember(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srandmember(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
    
    @Override
    public Set<String> sdiff(RedisKey key, RedisKey... keys)
    {
        if (keys.length <= 0) return Collections.EMPTY_SET;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String[] _keys = new String[keys.length + 1];
            _keys[0] = key.toString();
            for (int i = 0; i < keys.length; ++i)
            {
                _keys[i + 1] = keys[i].toString();
            }
            return jedis.sdiff(_keys);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
    
    @Override
    public Set<String> sinter(RedisKey key, RedisKey... keys)
    {
        if (keys.length <= 0) return Collections.EMPTY_SET;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String[] _keys = new String[keys.length + 1];
            _keys[0] = key.toString();
            for (int i = 0; i < keys.length; ++i)
            {
                _keys[i + 1] = keys[i].toString();
            }
            return jedis.sinter(_keys);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zadd(RedisKey key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key.toString(), score, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zadd(RedisKey key, Map<Double, String> scoreMembers) {
        if (scoreMembers.isEmpty()) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key.toString(), scoreMembers);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrange(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrange(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zrem(RedisKey key, String... member) {
        if (member.length <= 0) return 0L;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrem(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Double zincrby(RedisKey key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zincrby(key.toString(), score, member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zrank(RedisKey key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrank(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zrevrank(RedisKey key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrank(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrevrange(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrangeWithScores(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeWithScores(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeWithScores(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zcard(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Double zscore(RedisKey key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key.toString(), member);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public List<String> sort(RedisKey key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sort(key.toString());
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public List<String> sort(RedisKey key, SortingParams sortingParameters) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sort(key.toString(), sortingParameters);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zcount(RedisKey key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcount(key.toString(), min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zcount(RedisKey key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcount(key.toString(), min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrangeByScore(RedisKey key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScore(key.toString(), min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrangeByScore(RedisKey key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScore(key.toString(), min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrevrangeByScore(RedisKey key, double max, double min) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScore(key.toString(), max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrangeByScore(RedisKey key, double min, double max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScore(key.toString(), min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrevrangeByScore(RedisKey key, String max, String min) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScore(key.toString(), max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrangeByScore(RedisKey key, String min, String max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScore(key.toString(), min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrevrangeByScore(RedisKey key, double max, double min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScore(key.toString(), max, min, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(RedisKey key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScoreWithScores(key.toString(), min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, double max, double min) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScoreWithScores(key.toString(), max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(RedisKey key, double min, double max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScoreWithScores(key.toString(), min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> zrevrangeByScore(RedisKey key, String max, String min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScore(key.toString(), max, min, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(RedisKey key, String min, String max) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScoreWithScores(key.toString(), min, max);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, String max, String min) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScoreWithScores(key.toString(), max, min);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(RedisKey key, String min, String max, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrangeByScoreWithScores(key.toString(), min, max, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, double max, double min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScoreWithScores(key.toString(), max, min, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(RedisKey key, String max, String min, int offset, int count) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrangeByScoreWithScores(key.toString(), max, min, offset, count);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zremrangeByRank(RedisKey key, long start, long end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByRank(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zremrangeByScore(RedisKey key, double start, double end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByScore(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long zremrangeByScore(RedisKey key, String start, String end) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zremrangeByScore(key.toString(), start, end);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long lpushx(RedisKey key, String string) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpushx(key.toString(), string);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Long rpushx(RedisKey key, String string) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.rpushx(key.toString(), string);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }

    @Override
    public Set<String> keys(String patt) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.keys(patt);
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
    
    @Override
    public Long sndiff(RedisKey key, Set<String> vals)
    {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            
            long nDiff = vals.size();
            
            Pipeline pipeline = jedis.pipelined();
            List<Response<Boolean>> resp = new ArrayList(vals.size());
            
            for (String val : vals) {
                resp.add(pipeline.sismember(key.toString(), val));
            }
            pipeline.exec();
            
            for (Response<Boolean> response : resp) {
                if (response.get())
                {
                    --nDiff;
                }
            }
            
            return nDiff;
        } finally {
            if (jedis != null) {
                pool.returnResource(jedis);
            }
        }
    }
}
