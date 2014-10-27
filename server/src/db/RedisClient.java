/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import libCore.config.Config;
import org.apache.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Tuple;
import share.PGHelper;

/**
 *
 * @author tunm
 */
class RedisClient {

    private static final Logger logger = Logger.getLogger(RedisClient.class);
    private static final Map<String, RedisClient> _instances = new NonBlockingHashMap();
    private static final Lock createLock_ = new ReentrantLock();
    private final JedisPool Pool;
    private final int _timeout = 2000; //default
   
    public static RedisClient getClient(String host, int port, String password) {
        try
        {
            createLock_.lock();
            return new RedisClient(host, port, password);
        }
        finally
        {
            createLock_.unlock();
        }
    }

    public RedisClient(String host, int port, String password) {
        JedisPoolConfig poolConf = new JedisPoolConfig();
        poolConf.setTestOnBorrow(false);
        poolConf.setTestOnReturn(false);
        poolConf.setMaxActive(Integer.valueOf(Config.getParam("redis", "max_active")));
        poolConf.setMaxIdle(Integer.valueOf(Config.getParam("redis", "max_idle")));
        poolConf.setMaxWait(Integer.valueOf(Config.getParam("redis", "max_wait")));
        
        password = PGHelper.isNullOrEmpty(password)?null:password;
        Pool = new JedisPool(poolConf, host, port, _timeout, password);
    }

    public long set(String key, String value) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.set(key, value);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String get(String key) {
        String ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.get(key);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Map<String, String> rand() {
        Map<String, String> data = new HashMap<String, String>();

        String from = "";
        String key = "";

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            from = "";
            key = jedis.srandmember(from);

            data.put("from", from);
            data.put("key", key);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }


        return data;
    }

    public String set(byte[] key, byte[] value) {
        String ret = "0";
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.set(key, value);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
            ret = "-1";
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public byte[] get(byte[] key) {
        byte[] ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.get(key);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    /**
     * Add on member to sorted set
     *
     * @param key
     * @param value of member
     * @param score of member
     * @return number of member inserted
     */
    public long zadd(String key, String member, double score) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zadd(key, score, member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    /**
     * Add multi members to sorted set
     *
     * @param key
     * @param value of member
     * @param score of member
     * @return number of member inserted
     */
    public int zadd(String key, Map<Double, String> scoreMembers) {
        int ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.zadd(key, scoreMembers);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    /**
     * Get total element is sorted set
     *
     * @param key
     * @param value of member
     * @param score of member
     * @return number of member inserted
     */
    public long zcard(String key) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zcard(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zcard", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Set<String> zrange(String key, long start, long end) {
        Set<String> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrange(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrange", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;

    }

    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        Set<Tuple> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrangeWithScores(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrangeWithScores", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;

    }

    public Set<String> zrevrange(String key, long start, long end) {
        Set<String> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrevrange(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrange", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        Set<Tuple> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrevrangeWithScores", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long zrem(String key, String member) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrem(key, member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zrem", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long zremMulti(String key, String[] member) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zrem(key, member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zremMulti", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long zremrangeByRank(String key, long start, long stop) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.zremrangeByRank(key, start, stop);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.zremrangeByRank", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long smove(byte[] dstKeys, byte[] members, byte[] key) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smove(dstKeys, members, key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long smove(String dstKeys, String members, String key) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smove(dstKeys, members, key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public byte[] srand(byte[] member) {
        byte[] ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.srandmember(member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String srand(String member) {
        String ret = null;
        Jedis jedis = null;
        try {

            jedis = Pool.getResource();
            ret = jedis.srandmember(member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String spop(String member) {
        String ret = null;
        Jedis jedis = null;
        try {

            jedis = Pool.getResource();
            ret = jedis.spop(member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.spop", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public long sadd(byte[] key, byte[] member) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sadd(key, member);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.sadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long sadd(String key, String[] members) {
        if (members.length <= 0)
        {
            return -1;
        }
//        String[] a = {"a","b","c"};
//           jedis.sadd(key, a);

        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sadd(key, members);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.sadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public int sismember(String key, String member) {
        int ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sismember(key, member) ? 1 : 0;
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.get", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public long sadd(String key, String member) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.sadd(key, member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.sadd", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long srem(String key, String member) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.srem(key, member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.srem", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public long srem(String key, String[] members) {
        long ret = 0;
        Jedis jedis = null;
        try {
            if (members.length <= 0)
                return -1;
            
            jedis = Pool.getResource();
            ret = jedis.srem(key, members);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.srem", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public long srem(byte[] key, byte[] member) {
        long ret = 0;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.srem(key, member);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.srem", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public Set<byte[]> smember(byte[] key) {
        Set<byte[]> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smembers(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Set<String> smember(String key) {
        Set<String> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.smembers(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public long scard(String key)
    {
        long card = 0;
        
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            card = jedis.scard(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.smove", ex);
            card = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return card;
    }

    public Map<byte[], byte[]> getHm(byte[] key) {
        Map<byte[], byte[]> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hgetAll(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getHM", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public Map<String, String> getHm(String key) {
        Map<String, String> ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hgetAll(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getHM", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String hget(String key, String field) {
        String ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hget(key, field);
        } catch (Exception e) {
            logger.error("Exception in RedisClient.hget", e);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }

    public byte[] hget(byte[] key, byte[] field) {
        byte[] ret = null;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hget(key, key);
        } catch (Exception e) {
            logger.error("Exception in RedisClient.hget", e);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }

    public String setHm(byte[] key, Map<byte[], byte[]> hm) {
        String ret = "0";
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hmset(key, hm);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.setHm", ex);
            ret = "-1";
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public String setHm(String key, Map<String, String> hm) {
        if (hm.size() <= 0)
            return "ERR";
        
        String ret = "0";
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hmset(key, hm);

        } catch (Exception ex) {
            logger.error("Exception in RedisClient.setHm", ex);
            ret = "-1";
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public List<byte[]> getHm(byte[] key, byte[] hm) {
        List<byte[]> ret = new ArrayList<byte[]>();

        Jedis jedis = null;
        try {

            jedis = Pool.getResource();
            ret = jedis.hmget(key, hm);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.setHm", ex);
            ret = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long getAutoId(String AUTO_ID_KEY) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.incr(AUTO_ID_KEY);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long del(String key) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.del(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long del(String[] keys) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.del(keys);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long del(byte[] key) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.del(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }

    public long hDel(String key, String... fields) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hdel(key, fields);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.hDel", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public long hDel(byte[] key, byte[] fields) {
        long ret = -1;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.hdel(key, fields);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.hDel", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        return ret;
    }
    
    public void flushAll() {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.flushAll();
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);

        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
    }

    public boolean isExits(byte[] key) {
        Boolean ret = true;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.exists(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = false;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }

    public boolean isExits(String key) {
        Boolean ret = true;
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.exists(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = false;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }

    public List<byte[]> list_getAll(byte[] key) {
        List<byte[]> list = new ArrayList<byte[]>();

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            list = jedis.lrange(key, 0, -1);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            list = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return list;
    }

    public List<String> list_getAll(String key) {
        List<String> list = new ArrayList<String>();

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            list = jedis.lrange(key, 0, -1);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            list = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return list;
    }

    public List<String> list_getIn(String key, int beg, int len) {
        List<String> list = new ArrayList<String>();

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            list = jedis.lrange(key, beg, len);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            list = null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return list;
    }

    public String list_getAt(String key, int index) {
        List<String> list = new ArrayList<String>();

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            list = jedis.lrange(key, index, index + 1);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            return null;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        if (list.isEmpty())
        {
            return null;
        }
        return list.get(0);
    }

    public long list_push(String key, String... vals) {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.lpush(key, vals);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }
    
    public long list_append(String key, String... vals)
    {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.rpush(key, vals);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }

    public long list_push(byte[] key, byte[] val)
    {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.lpush(key, val);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }
    
    
    public long llen(String key)
    {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.llen(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }
    
    public long expireAt(String key, long time)
    {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.expireAt(key, time);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }
    
    public long expire(String key, int seconds)
    {
        long ret = 0;

        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            ret = jedis.expire(key, seconds);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }

        return ret;
    }
    
    public Set<String> keys(String keyPattern)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.keys(keyPattern);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            return Collections.EMPTY_SET;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
    }
    
    public long hincrby(String key, String hKey, long incrby)
    {
        Jedis jedis = null;
        long ret = -1;
        try {
            jedis = Pool.getResource();
            ret = jedis.hincrBy(key, hKey, incrby);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return ret;
    }
    
    public int hlen(String key)
    {
        Jedis jedis = null;
        long ret = -1;
        try {
            jedis = Pool.getResource();
            ret = jedis.hlen(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return (int) ret;
    }
    
    public int incrby(String key, long val)
    {
        Jedis jedis = null;
        long ret = -1;
        try {
            jedis = Pool.getResource();
            ret = jedis.incrBy(key, val);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = -1;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return (int) ret;
    }
    
    public long incr(String key)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.incr(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return -1L;
    }
    
    public boolean hexists(String key, String field)
    {
        Jedis jedis = null;
        boolean ret = false;
        try {
            jedis = Pool.getResource();
            ret = jedis.hexists(key, field);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
            ret = false;
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return ret;
    }
    
    public void hset(String key, String field, String val)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            jedis.hset(key, field, val);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
    }
    
    public List<String> lrange(String key, long off, long end)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.lrange(key, off, end);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return null;
    }
    
    public Set<String> sinter(String... keys)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.sinter(keys);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return null;
    }
    
    public Set<String> sdiff(String... toKeys)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.sdiff(toKeys);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return null;
    }
    
    public int sndiff(String key, Collection<String> vals)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            
            int nDiff = vals.size();
            
            Pipeline pipeline = jedis.pipelined();
            List<Response<Boolean>> resp = new ArrayList(vals.size());
            
            for (String val : vals) {
                resp.add(pipeline.sismember(key, val));
            }
            pipeline.exec();
            
            for (Response<Boolean> response : resp) {
                if (response.get())
                {
                    --nDiff;
                }
            }
            
            return nDiff;
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return vals.size();
    }
    
    public String type(String key)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.type(key);
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return "";
    }
    
    public Set<String> zrangebyscore(String key, Number min, Number max)
    {
        Jedis jedis = null;
        try {
            jedis = Pool.getResource();
            return jedis.zrangeByScore(key, min.doubleValue(), max.doubleValue());
        } catch (Exception ex) {
            logger.error("Exception in RedisClient.getAutoId", ex);
        } finally {
            if (jedis != null) {
                Pool.returnResource(jedis);
            }
        }
        
        return Collections.EMPTY_SET;
    }
}
