/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisListEntity;
import share.PGError;
import db.PGKeys;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadCotes")
public class CoteList extends PGRedisListEntity
{
    private final String uid;
    private final RedisKey keyCoteList;
    
    public CoteList(String userID)
    {
        this.uid = userID;
        this.keyCoteList = redisKey(uid);
    }
    
    public static CoteList getCotes(String uid) throws PGException
    {
        return EntityPool.inst().get(CoteList.class, uid);
    }
    
    public static CoteList loadCotes(String uid)
    {
        return new CoteList(uid);
    }
    
    public static void destroy(String uid)
    {
        DBContext.Redis().del(PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_COTES));
    }
    
    public static RedisKey redisKey(String uid)
    {
        return User.redisKey(uid).getChild(PGKeys.FD_COTES);
    }
    
    @Override
    protected RedisKey redisKey()
    {
        return keyCoteList;
    }

    public String getUid() {
        return uid;
    }
    
    public Object dump()
    {
        return getAll();
    }
}
