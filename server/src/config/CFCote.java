/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Map;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFCote extends JSONMapArray<CFCote.Level> implements JSONable
{
    private Dog dog;
    private Templates templs;
    
    private CFCote() {}
    static CFCote parse(Map<String, Object> json)
    {
        CFCote cote = new CFCote();
        cote.deser(json);
        
        return cote;
    }
    
    @Override
    protected Level newElement(Map<String, Object> elemJSON)
    {
        return new Level();
    }
    
    @Override
    public void deser(Map<String, Object> json)
    {
        super.deser((Map) json.get("levels"));
        
        this.dog = new Dog();
        this.dog.deser((Map) json.get("dogs"));
        
        this.templs = new Templates();
        this.templs.deser((Map) json.get("templates"));
    }

    public Dog getDog() {
        return dog;
    }
    
    public Templates templs()
    {
        return this.templs;
    }
    
    public static class Level implements JSONable
    {
        private Level() {}
        
        private int maxFish;
        private int maxPenguin;
        private float eatTimeReduce;
        private CFUpgradeRequirement upgradeRequire;

        @Override
        public void deser(Map<String, Object> json)
        {
            maxFish = Integer.parseInt(json.get("max_fish").toString());
            maxPenguin = Integer.parseInt(json.get("max_penguin").toString());
            eatTimeReduce = Float.parseFloat(json.get("eat_time_reduce").toString()) / 100f;
            Map<String, Object> upgradeReqJSON = (Map<String, Object>) json.get("upgrade_require");
            this.upgradeRequire = CFUpgradeRequirement.parse(upgradeReqJSON);
        }

        public int getMaxFish() {
            return maxFish;
        }

        public int getMaxPenguin() {
            return maxPenguin;
        }

        public float getEatTimeReduce() {
            return eatTimeReduce;
        }

        public CFUpgradeRequirement getUpgradeRequire() {
            return upgradeRequire;
        }
    }
    
    public static class Dog extends JSONMapString<CFCote.Dog.Item>
    {
        private Dog() {}
        
        @Override
        protected Item newElement(Map<String, Object> elemJSON)
        {
            return new Item();
        }

        @Override
        public void deser(Map<String, Object> json) {
            Map<String, Object> itemsJSON = (Map) json.get("items");
            super.deser(itemsJSON);
        }
        
        public static class Item implements JSONable
        {
            private Item() {}
            
            public static final int PAYMENT_TYPE_INVALID = 0;
            public static final int PAYMENT_TYPE_GOLD = 1;
            public static final int PAYMENT_TYPE_COIN = 2;
            
            private int price;
            private int paymentType;
            private long time;
            
            @Override
            public void deser(Map<String, Object> json)
            {
                this.price = PGHelper.toInteger(json.get("price"));
                this.time = 1000L * PGHelper.toLong(json.get("time"));
                
                String sPaymentType = (String) json.get("payment_type");
                this.paymentType = PAYMENT_TYPE_INVALID;
                if ("gold".equals(sPaymentType))
                {
                    this.paymentType = PAYMENT_TYPE_GOLD;
                }
                else if ("coin".equals(sPaymentType))
                {
                    this.paymentType = PAYMENT_TYPE_COIN;
                }
            }

            public int getPrice() {
                return price;
            }

            public int getPaymentType() {
                return paymentType;
            }

            public long getTime() {
                return time;
            }
        }
    }
    
    public static class Templates extends JSONMapString<Templates.Template>
    {
        private Templates() {}
        
        @Override
        protected Template newElement(Map<String, Object> elemJSON) {
            return new Template();
        }
        
        public static class Template implements JSONable
        {
            private Template() {}
            
            private String name;
            private int level;
            private int fish;
            private int boxegg_level;
            private Map<String, Number> penguins;
            private Map<String, Number> eggs;

            public String getName() {
                return name;
            }

            public int getLevel() {
                return level;
            }

            public int getFish() {
                return fish;
            }

            public int getBoxeggLevel() {
                return boxegg_level;
            }

            public Map<String, Number> getPenguins() {
                return penguins;
            }

            public Map<String, Number> getEggs() {
                return eggs;
            }

            @Override
            public void deser(Map<String, Object> json) {
                this.name = (String) json.get("name");
                this.level = PGHelper.toInteger(json.get("level"));
                this.fish = PGHelper.toInteger(json.get("fish"));
                this.boxegg_level = PGHelper.toInteger(json.get("boxegg_level"));
                this.penguins = (Map) json.get("penguins");
                this.eggs = (Map) json.get("eggs");
            }
        }
    }
}
