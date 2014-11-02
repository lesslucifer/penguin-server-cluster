/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONValue;
import pgentity.Gift;
import pgentity.UserGifts;
import pgentity.prize.PrizeFactory;
import share.PGError;
import db.PGKeys;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class GiftServices {
    private GiftServices()
    {
        super();
    }
    
    private static final GiftServices inst = new GiftServices();
    
    public static GiftServices inst()
    {
        return inst;
    }
    
    public Gift sendGift(Collection<String> receivers,
            Map<String, Object> giftPrize, long now, int expired)
    {
        try
        {
            PrizeFactory.getPrize(giftPrize);
        }
        catch(Exception ex)
        {
            PGException.Assert(false, PGError.INVALID_GIFT,
                    "Invalid gift prize " + JSONValue.toJSONString(giftPrize));
        }
        
        String giftID = PGKeys.randomKey();
        Gift gift = Gift.newGift(giftID, giftPrize, now, expired);
        
        for (String uid : receivers) {
            UserGifts userGifts = UserGifts.getGift(uid);
            userGifts.add(giftID);
        }
        
        return gift;
    }
    
    public void destroyGifts(String uid)
    {
        UserGifts userGifts = UserGifts.getGift(uid);
        Set<String> allGifts = userGifts.getAll();
        
        for (String giftID : allGifts) {
            Gift.destroyGift(giftID);
        }
        
        UserGifts.destroy(uid);
    }
}
