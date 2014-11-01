/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisSetEntity;
import db.PGKeys;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadGift")
public class UserGifts extends PGRedisSetEntity
{
    private final String uid;
    private final RedisKey redisKey;

    private UserGifts(String uid) {
        this.uid = uid;
        this.redisKey = redisKey(uid);
    }
    
    public static UserGifts getGift(String uid)
    {
        return EntityPool.inst().get(UserGifts.class, uid);
    }
    
    private static UserGifts loadGift(String uid)
    {
        return new UserGifts(uid);
    }
    
    /**
     * Use GiftServices.destroy instead
     * @param uid
     */
    @Deprecated
    public static void destroy(String uid)
    {
        DBContext.Redis().del(redisKey(uid));
        EntityPool.inst().remove(UserGifts.class, uid);
    }
    
    public static RedisKey redisKey(String uid)
    {
        return PGKeys.USERS.getChild(uid).getChild(PGKeys.FD_GIFTS);
    }
    
    @Override
    protected RedisKey redisKey()
    {
        return this.redisKey;
    }

    public String getUid() {
        return uid;
    }
    
    public Map<String, Object> buildAMF()
    {
        Set<Gift> allGifts = getAllGifts();
        
        Map<String, Object> giftAMFs = new HashMap(allGifts.size());
        for (Gift gift : allGifts) {
            giftAMFs.put(gift.getGiftID(), gift.buildAMF());
        }
        
        return giftAMFs;
    }

    @Override
    public Set<String> getAll() {
        Set<String> expiredGifts = new HashSet();
        Set<String> giftIDs = super.getAll();
        Set<String> availGifts = new HashSet(giftIDs.size());
        
        for (String giftID : giftIDs) {
            if (Gift.isExist(giftID))
            {
                availGifts.add(giftID);
            }
            else
            {
                expiredGifts.add(giftID);
            }
        }
        
        if (expiredGifts.size() > 0)
        {
            this.remove(expiredGifts.toArray(new String[giftIDs.size()]));
        }
        
        return availGifts;
    }
    
    private Set<Gift> getAllGifts()
    {
        Set<String> giftIDs = this.getAll();
        Set<Gift> gifts = new HashSet(giftIDs.size());
        for (String giftID : giftIDs) {
            gifts.add(Gift.getGift(giftID));
        }
        
        return gifts;
    }
    
    public Object dump()
    {
        return this.getAll();
    }
}
