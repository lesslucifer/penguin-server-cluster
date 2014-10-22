/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.List;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisSetEntity;
import db.PGKeys;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadPenguindex")
public class Penguindex extends PGRedisSetEntity
{
    private final RedisKey redisKey;
    
    private Penguindex(String uid)
    {
        this.redisKey = redisKey(uid);
    }
    
    public static Penguindex getPenguindex(String uid)
    {
        return EntityPool.inst().get(Penguindex.class, uid);
    }
    
    private static Penguindex loadPenguindex(String uid)
    {
        return new Penguindex(uid);
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis().del(redisKey(uid));
        EntityPool.inst().remove(Penguindex.class, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return User.redisKey(uid).getChild(PGKeys.FD_PENGUINDEX);
    }
    
    @Override
    protected RedisKey redisKey() {
        return redisKey;
    }
    
    public Object dump()
    {
        return this.getAll();
    }
}
