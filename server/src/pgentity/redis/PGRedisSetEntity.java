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
        return DBContext.Redis().sadd(redisKey(), IDs).intValue();
    }
    
    public long remove(String... IDs)
    {
        return DBContext.Redis().srem(redisKey(), IDs);
    }
    
    public void removeAll()
    {
        DBContext.Redis().del(redisKey());
    }
    
    public boolean contains(String ID)
    {
        return DBContext.Redis().sismember(redisKey(), ID);
    }
    
    public int size()
    {
        return DBContext.Redis().scard(redisKey()).intValue();
    }
}