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
public class HelpFriendFish implements PooledEntity
{
    private final String uid;
    private final RedisKey redisKey;

    private HelpFriendFish(String uid, String dayToken) {
        this.uid = uid;
        
        this.redisKey = PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_PLAY_WITH_FRIEND)
                .getChild(dayToken)
                .getChild(PGKeys.FD_HELP_FISH);
    }
    
    public static HelpFriendFish getEntity(String uid, long now)
    {
        String dayToken = TimeUtil.dayToken(now);
        return EntityPool.inst().get(HelpFriendFish.class, uid, dayToken);
    }
    
    public static HelpFriendFish loadEntity(String uid, String dayToken)
    {
        PGException.Assert(User.isExist(uid), PGError.INVALID_USER,
                String.format("%s doesn't exist", uid));
        
        HelpFriendFish helpFriendFish = new HelpFriendFish(uid, dayToken);
        return helpFriendFish;
    }
    
    public int fishHelped(String friendID)
    {
        return PGHelper.toInteger(DBContext.Redis().hget(redisKey, friendID));
    }
    
    public void helpFish(String friendID, int nFish)
    {
        boolean needExpire = DBContext.Redis().exists(redisKey);
        DBContext.Redis().hincrby(redisKey, friendID, nFish);
        
        if (needExpire)
        {
            DBContext.Redis().expire(redisKey, TimeUtil.DAY_SECS);
        }
    }
    
    public int numberFriendHelped()
    {
        return DBContext.Redis().hlen(redisKey).intValue();
    }
}
