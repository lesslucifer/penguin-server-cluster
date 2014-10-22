/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis;

import db.DBContext;
import java.util.Set;
import pgentity.pool.PooledEntity;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public abstract class PGRedisSetEntity implements PooledEntity
{
    // cache
    protected abstract RedisKey redisKey();
    
    public Set<String> getAll()
    {
        return DBContext.Redis().smembers(redisKey());
    }
    
    public int add(String... IDs)
    {
        return (int) DBContext.Redis().sadd(redisKey(), IDs);
    }
    
    public long remove(String... IDs)
    {
        return DBContext.Redis().srem(redisKey(), IDs);
    }
    
    public boolean contains(String ID)
    {
        return DBContext.Redis().sismember(redisKey(), ID);
    }
    
    public int size()
    {
        return (int) DBContext.Redis().scard(redisKey());
    }
}