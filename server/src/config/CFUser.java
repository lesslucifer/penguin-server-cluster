/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFUser extends JSONExperienceLevelArray<CFUser.Level> implements JSONable
{
    private Default defaultUser;
    private NPCs npcs;
    
    private CFUser() {}
    static CFUser parse(Map<String, Object> json)
    {
        CFUser user = new CFUser();
        user.deser(json);
        
        return user;
    }

    @Override
    public void deser(Map<String, Object> json)
    {
        super.deser((Map<String, Object>) json.get("levels"));
        
        this.defaultUser = new Default();
        this.defaultUser.deser((Map) json.get("default_user"));
        
        this.npcs = new NPCs();
        this.npcs.deser((Map) json.get("npc"));
    }

    @Override
    protected Level newElement(Map<String, Object> elemJSON)
    {
        return new Level();
    }

    public Default getDefaultUser() {
        return defaultUser;
    }
    
    public NPCs npc()
    {
        return npcs;
    }
    
    public static class Level implements JSONable, CFExperienceable
    {
        private int exp;
        private Map<String, Object> prize;
        
        private Level() {}

        @Override
        public void deser(Map<String, Object> json)
        {
            exp = Integer.parseInt(json.get("exp").toString());
            prize = (Map) json.get("prize");
        }

        @Override
        public int getExp() {
            return exp;
        }

        public Map<String, Object> getLevelUpPrize() {
            return prize;
        }
    }
    
    public static class Default extends JSONMapArray<Default.Cote>
    {
        private int level;
        private int fish;
        private int gold;
        private int coin;
        
        private Default() {}
        
        @Override
        protected Cote newElement(Map<String, Object> elemJSON) {
            return new Cote();
        }

        @Override
        public void deser(Map<String, Object> json) {
            this.level = PGHelper.toInteger(json.get("level"));
            this.fish = PGHelper.toInteger(json.get("fish"));
            this.gold = PGHelper.toInteger(json.get("gold"));
            this.coin = PGHelper.toInteger(json.get("coin"));
                
            super.deser((Map) json.get("cote"));
        }

        public int getLevel() {
            return level;
        }

        public int getFish() {
            return fish;
        }

        public int getGold() {
            return gold;
        }

        public int getCoin() {
            return coin;
        }
        
        public static class Cote implements JSONable, CFInit.Cote
        {
            private int level;
            private int poolFish;
            private String name;
            private Map<String, Number> penguins;
            
            private Cote() {}

            @Override
            public void deser(Map<String, Object> json) {
                this.level = PGHelper.toInteger(json.get("level"));
                this.poolFish = PGHelper.toInteger(json.get("pool_fish"));
                this.name = (String) json.get("name");
                
                Map<String, Map<String, String>> penguinJSON = (Map) json.get("penguins");
                this.penguins = new HashMap(penguinJSON.size());
                
                for (Map.Entry<String, Map<String, String>> penguinEntry : penguinJSON.entrySet()) {
                    Map<String, String> penguinData = penguinEntry.getValue();
                    
                    String pKind = (String) penguinData.get("kind");
                    int pLevel = PGHelper.toInteger(penguinData.get("level"));
                    
                    this.penguins.put(pKind, pLevel);
                }
            }

            @Override
            public int getLevel() {
                return level;
            }

            @Override
            public int getPoolFish() {
                return poolFish;
            }

            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public Map<String, Number> getPenguins()
            {
                return this.penguins;
            }
        }
    }
    
    public static class NPCs extends JSONMapArray<NPCs.NPC>
    {
        private NPCs() {}
        
        @Override
        protected NPC newElement(Map<String, Object> elemJSON) {
            return new NPC();
        }
        
        public static class NPC extends JSONMapArray<NPC.Cote>
        {
            private String name, avatar;
            private int level, fish, gold, coin;
            
            @Override
            protected Cote newElement(Map<String, Object> elemJSON) {
                return new Cote();
            }
            
            @Override
            public void deser(Map<String, Object> json) {
                this.name = (String) json.get("name");
                this.avatar = (String) json.get("avatar");
                this.level = PGHelper.toInteger(json.get("level"));
                this.fish = PGHelper.toInteger(json.get("fish"));
                this.gold = PGHelper.toInteger(json.get("gold"));
                this.coin = PGHelper.toInteger(json.get("coin"));
                
                super.deser((Map) json.get("cotes"));
            }

            public String getName() {
                return name;
            }

            public String getAvatar() {
                return avatar;
            }

            public int getLevel() {
                return level;
            }

            public int getFish() {
                return fish;
            }

            public int getGold() {
                return gold;
            }

            public int getCoin() {
                return coin;
            }
            
            public static class Cote implements JSONable, CFInit.Cote
            {
                private Cote() {}
                
                private String name;
                private int level, poolFish, boxEggLevel;
                private Map<String, Number> penguins;
                private Map<String, Number> eggs;
                
                @Override
                public void deser(Map<String, Object> json) {
                    this.name = (String) json.get("name");
                    this.level = PGHelper.toInteger(json.get("level"));
                    this.poolFish = PGHelper.toInteger(json.get("fish"));
                    this.boxEggLevel = PGHelper.toInteger(json.get("boxegg_level"));
                    
                    penguins = (Map) json.get("penguins");
                    eggs = (Map) json.get("eggs");
                }   

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public int getLevel() {
                    return level;
                }

                @Override
                public int getPoolFish() {
                    return poolFish;
                }

                public int getBoxEggLevel() {
                    return boxEggLevel;
                }

                @Override
                public Map<String, Number> getPenguins() {
                    return penguins;
                }

                public Map<String, Number> getEggs() {
                    return eggs;
                }
            }
        }
    }
}