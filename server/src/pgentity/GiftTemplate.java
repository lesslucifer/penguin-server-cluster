/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
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
@EntityFactory(factorier = "loadTemplate")
public class GiftTemplate implements PGEntity {
    private final String templID;
    private final RedisKey redisKey;
    
    private String giftData;
    private int giftExpire;
    
    private GiftTemplate(String templID)
    {
        this.templID = templID;
        this.redisKey = PGKeys.GAMEMESSAGES.getChild(templID);
    }
    
    public static GiftTemplate getTemplate(String templID)
    {
        return EntityPool.inst().get(GiftTemplate.class, templID);
    }
    
    public static GiftTemplate newTemplate(String templID,
            Map<String, Object> giftData, int giftExpire, int templExpire)
    {
        PGException.Assert(!isExist(templID),
                PGError.UNDEFINED, "Gift template are already existed");
        
        GiftTemplate giftTemplate = new GiftTemplate(templID);
        giftTemplate.setGiftData(JSONValue.toJSONString(giftData));
        giftTemplate.setGiftExpire(giftExpire);
        giftTemplate.saveToDB();
        giftTemplate.expireKey(templExpire);
        
        EntityPool.inst().put(giftTemplate, GiftTemplate.class, templID);
        
        return giftTemplate;
    }
    
    private static GiftTemplate loadTemplate(String templID)
    {
        PGException.Assert(isExist(templID),
                PGError.INVALID_GIFT_DATA, "Gift data are invalid or expired");
        
        GiftTemplate giftTemplate = new GiftTemplate(templID);
        giftTemplate.updateFromDB();
        
        return giftTemplate;
    }
    
    public static boolean isExist(String templID)
    {
        return DBContext.Redis().isExists(
            PGKeys.GAMEMESSAGES.getChild(templID));
    }
    
    @Override
    public void updateFromDB() {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        
        this.giftData = data.get(PGMacro.GIFT_TEMPLATE_DATA);
        this.giftExpire = Integer.parseInt(data.get(PGMacro.GIFT_EXPIRED_TIME));
    }

    @Override
    public void saveToDB() {
        Map<String, String> data = new HashMap(2);
        data.put(PGMacro.GIFT_TEMPLATE_DATA, this.giftData);
        data.put(PGMacro.GIFT_EXPIRED_TIME, String.valueOf(this.giftExpire));
        
        DBContext.Redis().hset(redisKey, data);
    }
    
    private void expireKey(int expire)
    {
        DBContext.Redis().expire(redisKey, expire);
    }

    public String getTemplID() {
        return templID;
    }

    public String getGiftData() {
        return giftData;
    }

    public void setGiftData(String giftData) {
        this.giftData = giftData;
    }

    public int getGiftExpire() {
        return giftExpire;
    }

    public void setGiftExpire(int giftExpire) {
        this.giftExpire = giftExpire;
    }
}
