/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.pool.PooledEntity;
import share.PGError;
import share.PGHelper;
import db.PGKeys;
import db.RedisKey;
import share.PGException;
import share.TimeUtil;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadEntity")
public class StealFriendEgg implements PooledEntity
{
    private final String uid;
    private final RedisKey redisKey;

    private StealFriendEgg(String uid, String dayToken) {
        this.uid = uid;
        
        this.redisKey = PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_PLAY_WITH_FRIEND)
                .getChild(dayToken)
                .getChild(PGKeys.FD_STEAL_EGG);
    }
    
    public static StealFriendEgg getEntity(String uid, long now)
    {
        return EntityPool.inst().get(StealFriendEgg.class, uid, TimeUtil.dayToken(now));
    }
    
    public static StealFriendEgg loadEntity(String uid, String dayToken)
    {
        PGException.Assert(User.isExist(uid), PGError.INVALID_USER,
                String.format("%s doesn't exist", uid));
        
        StealFriendEgg stealFriendEgg = new StealFriendEgg(uid, dayToken);
        return stealFriendEgg;
    }
    
    public void stealEgg(String friendID, int nEggs)
    {
        boolean needExpire = DBContext.Redis().exists(redisKey);
        
        DBContext.Redis().hincrby(redisKey, friendID, nEggs);
        
        if (needExpire)
        {
            DBContext.Redis().expire(redisKey, TimeUtil.DAY_SECS);
        }
    }
    
    public int eggStolen(String friendID)
    {
        return PGHelper.toInteger(DBContext.Redis().hget(redisKey, friendID));
    }
    
    public int numberFriendStolen()
    {
        return DBContext.Redis().hlen(redisKey).intValue();
    }
}
