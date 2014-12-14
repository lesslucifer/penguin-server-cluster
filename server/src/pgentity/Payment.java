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
import share.PGHelper;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadPayment")
public class Payment implements PGEntity
{
    private final String billNo;
    private final RedisKey redisKey;
    private String uid, zingCreID;
    private int zingCoin;
    private int gameCoin;

    private Payment(String billNo) {
        this.billNo = billNo;
        this.redisKey = PGKeys.PAYMENTS.getChild(billNo);
    }
    
    public static Payment getPayment(String billNo)
    {
        return EntityPool.inst().get(Payment.class, billNo);
    }
    
    private static Payment loadPayment(String billNo)
    {
        PGException.Assert(Payment.isExist(billNo),
                PGError.BILL_NO_NOT_EXISTED,
                "Bill no: " + billNo + " not exist");
        
        Payment payment = new Payment(billNo);
        payment.updateFromDB();
        
        return payment;
    }
    
    public static Payment newPayment(String billNo, String uid,
            String zingCreID, int zingCoin, int gameCoin, int expire)
    {
        Payment payment = new Payment(billNo);
        payment.uid = uid;
        payment.zingCoin = zingCoin;
        payment.zingCreID = zingCreID;
        payment.gameCoin = gameCoin;
        payment.saveToDB();
        
        DBContext.Redis().expire(payment.redisKey, expire);
        
        EntityPool.inst().put(payment, Payment.class, billNo);
        
        return payment;
    }
    
    public static boolean isExist(String billNo)
    {
        RedisKey redisKey = PGKeys.PAYMENTS.getChild(billNo);
        return DBContext.Redis().exists(redisKey);
    }

    @Override
    public void updateFromDB() {
        Map<String, String> data = DBContext.Redis().hgetall(this.redisKey);
        
        this.uid = data.get(PGMacro.UID);
        this.zingCoin = PGHelper.toInteger(data.get(PGMacro.ZING_COIN));
        this.zingCreID = data.get(PGMacro.ZING_CRE_ID);
        this.gameCoin = Integer.parseInt(data.get(PGMacro.COIN));
    }

    @Override
    public void saveToDB() {
        Map<String, String> data = new HashMap(4);
        
        data.put(PGMacro.UID, uid);
        data.put(PGMacro.ZING_COIN, String.valueOf(zingCoin));
        data.put(PGMacro.ZING_CRE_ID, zingCreID);
        data.put(PGMacro.COIN, String.valueOf(gameCoin));
        
        DBContext.Redis().hset(this.redisKey, data);
    }

    public String getBillNo() {
        return billNo;
    }

    public String getUid() {
        return uid;
    }

    public String getZingCreID() {
        return zingCreID;
    }

    public int getZingCoin() {
        return zingCoin;
    }

    public int getGameCoin() {
        return gameCoin;
    }
    
    public Map<String, Object> buildAMF()
    {
        Map<String, Object> data = new HashMap(4);
        data.put(PGMacro.UID, uid);
        data.put(PGMacro.ZING_COIN, zingCoin);
        data.put(PGMacro.ZING_CRE_ID, zingCreID);
        data.put(PGMacro.COIN, gameCoin);
        
        return data;
    }
}
