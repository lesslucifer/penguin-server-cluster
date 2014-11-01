/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONValue;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import share.PGError;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadGift")
public class Gift implements PGEntity
{
    private final String giftID;
    private final RedisKey redisKey;
    private Map<String, Object> prizeData;
    private long expiredTime;

    private Gift(String giftID) {
        this.giftID = giftID;
        this.redisKey = PGKeys.GIFTS.getChild(giftID);
    }
    
    public static Gift getGift(String giftID)
    {
        return EntityPool.inst().get(Gift.class, giftID);
    }
    
    public static Gift loadGift(String giftID)
    {
        PGException.Assert(isExist(giftID),
                PGError.INVALID_GIFT,
                "Gift doesn't exist or expired");
        
        Gift gift = new Gift(giftID);
        gift.updateFromDB();
        
        return gift;
    }
    
    public static Gift newGift(String giftID,
            Map<String, Object> giftPrize, long now, int expired)
    {
        Gift gift = new Gift(giftID);
        gift.prizeData = giftPrize;
        gift.expiredTime = now + expired;
        gift.saveToDB();
        
        EntityPool.inst().put(gift, Gift.class, giftID);
        
        return gift;
    }
    
    public static void destroyGift(String giftID)
    {
        DBContext.Redis().del(PGKeys.GIFTS.getChild(giftID));
        EntityPool.inst().remove(Gift.class, giftID);
    }
    
    public static boolean isExist(String giftID)
    {
        RedisKey redisKey = PGKeys.GIFTS.getChild(giftID);
        return DBContext.Redis().exists(redisKey);
    }
    
    @Override
    public void updateFromDB()
    {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        
        String prizeJSON = data.get(PGMacro.GIFT_PRIZE);
        this.prizeData = Collections.unmodifiableMap((Map) JSONValue.parse(prizeJSON));
        
        this.expiredTime = Long.parseLong(data.get(PGMacro.GIFT_EXPIRED_TIME));
    }

    @Override
    public void saveToDB() {
        Map<String, String> data = new HashMap(2);
        
        data.put(PGMacro.GIFT_PRIZE, JSONValue.toJSONString(prizeData));
        data.put(PGMacro.GIFT_EXPIRED_TIME, String.valueOf(this.expiredTime));
        
        DBContext.Redis().hset(redisKey, data);
    }

    public String getGiftID() {
        return giftID;
    }

    public Map<String, Object> getPrizeData() {
        return prizeData;
    }

    public long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }
    
    public Map<String, Object> buildAMF()
    {
        Map<String, Object> data = new HashMap();
        
        data.put(PGMacro.GIFT_ID, this.giftID);
        data.put(PGMacro.GIFT_PRIZE, prizeData);
        data.put(PGMacro.GIFT_EXPIRED_TIME, this.expiredTime);
        
        return data;
    }
    
    /**
     * Use GiftServices.dumpGifts instead
     * @return
     */
    public Object dump()
    {
        return DBContext.Redis().hgetall(redisKey);
    }
}