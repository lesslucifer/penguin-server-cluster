/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import logging.Logging;
import logging.report.PGLogCategory;
import logging.report.PGRecord;
import org.json.simple.JSONValue;
import pgentity.Payment;
import share.AMFBuilder;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class PaymentServices {
    private PaymentServices()
    {
        super();
    }
    
    private static final PaymentServices inst = new PaymentServices();
    
    public static PaymentServices inst()
    {
        return inst;
    }
    
    public void addPayment(String billNo, String uid,
            String zingCreID, int zingCoin, int gameCoin)
    {
        this.addPayment(billNo, uid, zingCreID, zingCoin, gameCoin, 3*24*60*60); // expire 3 days
        
        String logData = JSONValue.toJSONString(
                AMFBuilder.make("zCoin", zingCoin, "pCoin", gameCoin));
        Logging.log(new PGRecord(PGLogCategory.PAY, uid,
                System.currentTimeMillis(), 0, logData));
    }
    
    public void addPayment(String billNo, String uid,
            String zingCreID, int zingCoin, int gameCoin, int expire)
    {
        PGException.Assert(!Payment.isExist(billNo),
                PGError.BILL_NO_ALREADY_EXISTED,
                "Bill no: " + billNo + " already exist");
        
        Payment.newPayment(billNo, uid, zingCreID, zingCoin, gameCoin, expire);
    }
    
    public boolean isBillNoExist(String billNo)
    {
        return Payment.isExist(billNo);
    }
    
    public Payment getPayment(String billNo)
    {
        return Payment.getPayment(billNo);
    }
}
