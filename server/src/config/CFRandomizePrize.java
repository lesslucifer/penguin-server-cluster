/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFRandomizePrize implements JSONable
{
    private CFRandomizePrize()
    {
        this.randomizer = new SecureRandom(ByteBuffer.allocate(8)
                .putLong(System.currentTimeMillis()).array());
    }
    public static CFRandomizePrize parse(Map<String, Object> json)
    {
        CFRandomizePrize randomizePrize = new CFRandomizePrize();
        randomizePrize.deser(json);
        
        return randomizePrize;
    }
    
    private SecureRandom randomizer;
    private NavigableMap<Integer, String> prizeRates;
    private Map<String, Prize> prizes;
    private int totalRate = 0;
    
    private Items items;
    
    @Override
    public void deser(Map<String, Object> json) {
        this.prizeRates = new TreeMap();
        this.prizes = new HashMap();
        
        Map<String, Object> prizeJSON = (Map) json.get("prize");
        
        for (Map.Entry<String, Object> rPrizeEntry : prizeJSON.entrySet()) {
            String prizeID = rPrizeEntry.getKey();
            Map<String, Object> prizeData = (Map) rPrizeEntry.getValue();
            Prize prize = new Prize();
            prize.deser(prizeData);
            
            prizes.put(prizeID, prize);
            
            int rate = prize.getRate();
            prizeRates.put(totalRate, prizeID);
            totalRate += rate;
        }
        
        Map<String, Object> itemsJSON = (Map) json.get("items");
        this.items = new Items();
        items.deser(itemsJSON);
    }
    
    public synchronized String randomPrize()
    {
        int randVal = randomizer.nextInt(totalRate);
        return this.prizeRates.get(this.prizeRates.floorKey(randVal));
    }
    
    public CFRandomizePrize.Prize get(String prizeID)
    {
        return this.prizes.get(prizeID);
    }

    public Items getItems() {
        return items;
    }
    
    public static class Prize implements JSONable
    {
        private Prize() {}
        
        private Map<String, Object> prize;
        private int rate;
        private Boolean autoPrize;
        
        @Override
        public void deser(Map<String, Object> json) {
            this.prize = Collections.unmodifiableMap((Map) json.get("prize"));
            this.rate = PGHelper.toInteger(json.get("rate"));
            this.autoPrize = Boolean.parseBoolean(json.get("auto").toString());
        }

        public Map<String, Object> getPrize() {
            return prize;
        }

        public int getRate() {
            return rate;
        }

        public Boolean isAutoPrize() {
            return autoPrize;
        }
    }
    
    public static class Items extends JSONMapString<Items.Item>
    {
        private Items() {}
        
        @Override
        protected Item newElement(Map<String, Object> elemJSON) {
            return new Item();
        }
        
        public static class Item implements JSONable
        {
            private Item() {}
            
            private String paymentType;
            private int price;
            private int nTurn;
            
            @Override
            public void deser(Map<String, Object> json) {
                this.paymentType = (String) json.get("payment_type");
                this.price = PGHelper.toInteger(json.get("payment_value"));
                this.nTurn = PGHelper.toInteger(json.get("turn"));
            }

            public String getPaymentType() {
                return paymentType;
            }

            public int getPrice() {
                return price;
            }

            public int getnTurn() {
                return nTurn;
            }
        }
    }
}
