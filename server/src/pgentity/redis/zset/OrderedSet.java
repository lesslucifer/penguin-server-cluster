/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.redis.zset;

import db.DBContext;
import db.RedisKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import pgentity.pool.PooledEntity;
import redis.clients.jedis.Tuple;

/**
 *
 * @author Salm
 */
public class OrderedSet implements PooledEntity {
    protected final RedisKey redisKey;

    protected OrderedSet(RedisKey redisKey) {
        this.redisKey = redisKey;
    }
    
    public static OrderedSet get(RedisKey key)
    {
        return new OrderedSet(key);
    }
    
    public ZField floorScore(Number score)
    {
        List<ZField> rng = revScoreRange(Double.MIN_VALUE, score, 1);
        if (rng.size() > 0)
        {
            rng.get(0);
        }
        
        return null;
    }
    
    public ZField ceilScore(Number score)
    {
        List<ZField> rng = scoreRange(score, Double.MAX_VALUE, 1);
        if (rng.size() > 0)
        {
            rng.get(0);
        }
        
        return null;
    }
    
    public List<ZField> indexRange(int start, int end)
    {
        return TupleAdapter.convert(DBContext.Redis()
                .zrangeWithScores(redisKey, start, end));
    }
    
    public List<ZField> revIndexRange(int start, int end)
    {
        return TupleAdapter.convert(DBContext.Redis()
                .zrevrangeWithScores(redisKey, start, end));
    }
    
    //List<Pair<String, Long>> rangeWithScore(int start, int end)
    
    public List<ZField> scoreRange(Number start, Number end)
    {
        return TupleAdapter.convert(DBContext.Redis().zrangeByScoreWithScores
            (redisKey, start.doubleValue(), end.doubleValue()));
    }
    
    public List<ZField> scoreRange(Number start, Number end, int limit)
    {
        return TupleAdapter.convert(DBContext.Redis().zrangeByScoreWithScores
            (redisKey, start.doubleValue(), end.doubleValue(), 0, limit));
    }
    
    public List<ZField> revScoreRange(Number start, Number end)
    {
        return TupleAdapter.convert(DBContext.Redis().zrevrangeByScoreWithScores
            (redisKey, end.doubleValue(), start.doubleValue()));
    }
    
    public List<ZField> revScoreRange(Number start, Number end, int limit)
    {
        return TupleAdapter.convert(DBContext.Redis().zrevrangeByScoreWithScores
            (redisKey, end.doubleValue(), start.doubleValue(), 0, limit));
    }
    
    public boolean contains(String field)
    {
        return DBContext.Redis().zrank(redisKey, field) != null;
    }
    
    public int rank(String field)
    {
        return DBContext.Redis().zrank(redisKey, field).intValue();
    }
    
    public int revRank(String field)
    {
        return DBContext.Redis().zrevrank(redisKey, field).intValue();
    }
    
    public int size()
    {
        return DBContext.Redis().zcard(redisKey).intValue();
    }
    
    public int add(Number score, String field)
    {
        return DBContext.Redis()
                .zadd(redisKey, score.doubleValue(), field).intValue();
    }
    
    public int incrby(String field, Number score)
    {
        return DBContext.Redis()
                .zincrby(redisKey, score.doubleValue(), field).intValue();
    }
    
    public long remove(String... fields)
    {
        return DBContext.Redis().zrem(redisKey, fields);
    }
    
    // Number count

    public static interface ZField
    {
        String field();
        Number score();
    }
    
    private static class TupleAdapter implements ZField
    {
        private final Tuple tuple;
        private Number _score;

        private TupleAdapter(Tuple tuple) {
            this.tuple = tuple;
        }

        @Override
        public String field() {
            return tuple.getElement();
        }

        @Override
        public Number score() {
            _score = (_score == null)?(tuple.getScore()):_score;
            return _score;
        }
        
        public static List<ZField> convert(Collection<Tuple> tuples)
        {
            List<ZField> ret = new ArrayList(tuples.size());
            for (Tuple tuple : tuples) {
                ret.add(new TupleAdapter(tuple));
            }
            
            return ret;
        }
    }
}
