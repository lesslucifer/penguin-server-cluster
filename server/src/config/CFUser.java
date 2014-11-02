/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.List;
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
    
    public static class Default implements JSONable
    {
        private int level;
        private int fish;
        private int gold;
        private int coin;
        private List<String> cotes;
        
        private Default() {}

        @Override
        public void deser(Map<String, Object> json) {
            this.level = PGHelper.toInteger(json.get("level"));
            this.fish = PGHelper.toInteger(json.get("fish"));
            this.gold = PGHelper.toInteger(json.get("gold"));
            this.coin = PGHelper.toInteger(json.get("coin"));
            this.cotes = PGHelper.mapToList((Map) json.get("cote"));
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
        
        public List<String> getCotes()
        {
            return this.cotes;
        }
    }
    
    public static class NPCs extends JSONMapArray<NPCs.NPC>
    {
        private NPCs() {}
        
        @Override
        protected NPC newElement(Map<String, Object> elemJSON) {
            return new NPC();
        }
        
        public static class NPC implements JSONable
        {
            private String name, avatar;
            private int level, fish, gold, coin;
            private List<String> cotes;
            
            @Override
            public void deser(Map<String, Object> json) {
                this.name = (String) json.get("name");
                this.avatar = (String) json.get("avatar");
                this.level = PGHelper.toInteger(json.get("level"));
                this.fish = PGHelper.toInteger(json.get("fish"));
                this.gold = PGHelper.toInteger(json.get("gold"));
                this.coin = PGHelper.toInteger(json.get("coin"));
                
                this.cotes =  PGHelper.mapToList((Map) json.get("cotes"));
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
            
            public List<String> cotes()
            {
                return this.cotes;
            }
        }
    }
}