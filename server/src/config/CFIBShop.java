/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import config.CFIBShop.Item;
import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public class CFIBShop extends JSONMapString<CFIBShop.Item> implements JSONable
{
    private CFIBShop() {}
    static CFIBShop parse(Map<String, Object> json)
    {
        CFIBShop items = new CFIBShop();
        items.deser(json);
        
        return items;
    }

    @Override
    protected Item newElement(Map<String, Object> elemJSON)
    {
        String itemKind = elemJSON.get("kind").toString();
        if ("penguin".equals(itemKind))
        {
            return new PenguinShopItem();
        }
        else if ("fish".equals(itemKind))
        {
            return new FishShopItem();
        }
        else if ("gold".equals(itemKind))
        {
            return new GoldShopItem();
        }
        
        return null;
    }
    
    public static class Item implements JSONable
    {
        protected Item() {}
        
        private String kind;
        private UnlockRequirement unlockRequire;
        private Price price;
        private Promotion promotion;

        /**
         * @return the kind
         */
        public String getKind() {
            return kind;
        }

        /**
         * @return the unlockRequire
         */
        public UnlockRequirement getUnlockRequire() {
            return unlockRequire;
        }

        /**
         * @return the price
         */
        public Price getPrice() {
            return price;
        }

        /**
         * @return the promotion
         */
        public Promotion getPromotion() {
            return promotion;
        }
        
        public static class UnlockRequirement implements JSONable
        {
            private UnlockRequirement() {}
            
            int level;

            @Override
            public void deser(Map<String, Object> json)
            {
                level = Integer.parseInt(json.get("level").toString());
            }

            public int getLevel() {
                return level;
            }
        }
        
        public static class Price implements JSONable
        {
            private Price() {}
            
            int gold;
            int coin;

            @Override
            public void deser(Map<String, Object> json)
            {
                gold = Integer.parseInt(json.get("gold").toString());
                coin = Integer.parseInt(json.get("coin").toString());
            }

            public int getGold() {
                return gold;
            }

            public int getCoin() {
                return coin;
            }
        }
        
        public static class Promotion implements JSONable
        {
            private Promotion() {}
            
            float percent;

            @Override
            public void deser(Map<String, Object> json)
            {
                percent = Float.parseFloat(json.get("percent").toString());
            }

            public float getPercent() {
                return percent;
            }
        }
        
        @Override
        public void deser(Map<String, Object> json)
        {
            kind = json.get("kind").toString();
            unlockRequire = new UnlockRequirement();
            unlockRequire.deser((Map<String, Object>) json.get("unlock"));
            price = new Price();
            price.deser((Map<String, Object>) json.get("price"));
            promotion = new Promotion();
            promotion.deser((Map<String, Object>) json.get("promotion"));
        }
    }
    
    public static class PenguinShopItem extends Item
    {
        private PenguinShopItem() {}
        
        private String entity;

        @Override
        public void deser(Map<String, Object> json) {
            super.deser(json);
            
            entity = json.get("entity").toString();
        }

        public String getEntity() {
            return entity;
        }
    }
    
    public static class FishShopItem extends Item
    {
        private FishShopItem() {}
        
        private int nFish;

        @Override
        public void deser(Map<String, Object> json) {
            super.deser(json);
            
            this.nFish = Integer.parseInt(json.get("n_fish").toString());
        }

        public int getnFish() {
            return nFish;
        }
    }
    
    public static class GoldShopItem extends Item
    {
        private GoldShopItem() {}
        
        private int nGold;

        @Override
        public void deser(Map<String, Object> json) {
            super.deser(json);
            
            this.nGold = Integer.parseInt(json.get("n_gold").toString());
        }

        public int getnGold() {
            return nGold;
        }
    }
}
