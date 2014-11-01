/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
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
public class GiftCode implements PGEntity
{
    private final RedisKey redisKey;
    private final String code;
    
    private String giftTemplateID;
    private int nGiftRemain;

    private GiftCode(String code) {
        this.code = code;
        this.redisKey = PGKeys.GIFTCODES.getChild(code);
    }
    
    public static GiftCode getGift(String code)
    {
        return EntityPool.inst().get(GiftCode.class, code);
    }
    
    public static GiftCode loadGift(String code)
    {
        PGException.Assert(isExist(code),
                PGError.INVALID_GIFT_CODE, "Gift code are invalid or expired");
        
        GiftCode gCode = new GiftCode(code);
        gCode.updateFromDB();
        return gCode;
    }
    
    public static GiftCode newGift(String code, String templID,
            int nGift, int codeExpire)
    {
        PGException.Assert(!isExist(code),
                PGError.EXISTED_GIFT_CODE, "Gift code are already existed");
        
        GiftCode gCode = new GiftCode(code);
        gCode.setGiftTemplateID(templID);
        gCode.setRemain(nGift);
        gCode.saveToDB();
        
        DBContext.Redis().expire(gCode.redisKey, codeExpire);
        
        EntityPool.inst().put(gCode, GiftCode.class, code);
        
        return gCode;
    }
    public static boolean isExist(String code)
    {
        return DBContext.Redis().exists(PGKeys.GIFTCODES.getChild(code));
    }
    
    @Override
    public void updateFromDB() {
        Map<String, String> data = DBContext.Redis().hgetall(redisKey);
        this.giftTemplateID = data.get(PGMacro.GIFT_TEMPLATE_ID);
        this.nGiftRemain = Integer.parseInt(data.get(PGMacro.REMAINS_GIFT));
    }

    @Override
    public void saveToDB() 
    {
        Map<String, String> data = new HashMap(2);
        data.put(PGMacro.GIFT_TEMPLATE_ID, this.giftTemplateID);
        data.put(PGMacro.REMAINS_GIFT, String.valueOf(this.nGiftRemain));
        
        DBContext.Redis().hset(redisKey, data);
    }

    public String getCode() {
        return code;
    }

    public String getGiftTemplateID() {
        return giftTemplateID;
    }

    public void setGiftTemplateID(String giftTemplateID) {
        this.giftTemplateID = giftTemplateID;
    }

    public int getRemain() {
        return nGiftRemain;
    }

    public void setRemain(int nGiftRemain) {
        this.nGiftRemain = nGiftRemain;
    }
}
