/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis;

import db.DBContext;
import java.util.List;
import pgentity.pool.PooledEntity;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public abstract class PGRedisListEntity implements PooledEntity
{
    /**
     * Provide an weak cache for Redis list
     */
    protected abstract RedisKey redisKey();
    
    public List<String> getAll()
    {
        return DBContext.Redis().lgetall(redisKey());
    }
    
    public String at(int index)
    {
        return DBContext.Redis().lgetat(redisKey(), index);
    }
    
    public List<String> in(int start, int end)
    {
        return DBContext.Redis().lrange(redisKey(), start, end);
    }
    
    public long append(String id)
    {
        return DBContext.Redis().lappend(redisKey(), id);
    }
    
    public long push(String id)
    {
        return DBContext.Redis().lpush(redisKey(), id);
    }
    
    public boolean contains(String id)
    {
        return this.getAll().contains(id);
    }
    
    public int length()
    {
        return (int) DBContext.Redis().llen(redisKey());
    }
}