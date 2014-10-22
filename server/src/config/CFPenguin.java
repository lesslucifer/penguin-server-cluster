/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author KieuAnh
 */
public class CFPenguin
{
    private PenguinGroups pengGroups;
    private PenguinKinds pengKinds;
    
    private CFPenguin() {}
    static CFPenguin parse(Map<String, Object> pGroupJSON, Map<String, Object> pKindJSON)
    {
        CFPenguin penguin = new CFPenguin();
        penguin.pengGroups = new PenguinGroups();
        penguin.pengGroups.deser(pGroupJSON);
        
        penguin.pengKinds = new PenguinKinds();
        penguin.pengKinds.deser(pKindJSON);
        
        return penguin;
    }
    
    public Set<String> getAllGroups()
    {
        return this.pengGroups.keySet();
    }
    
    public Set<String> getAllKinds()
    {
        return this.pengKinds.keySet();
    }
    
    public CFPenguin.Group getGroup(String kind)
    {
        return this.pengGroups.get(this.getKind(kind).group);
    }
    
    public CFPenguin.Kind getKind(String kind)
    {
        return this.pengKinds.get(kind);
    }
    
    private static class PenguinGroups extends JSONMapString<CFPenguin.Group> implements JSONable
    {
        private PenguinGroups() {}

        @Override
        protected Group newElement(Map<String, Object> elemJSON)
        {
            return new Group();
        }
    }
    
    public static class Group extends JSONExperienceLevelArray<Group.Level> implements JSONable
    {
        private int firstSpawnableLevel;
        
        private Group() {}

        @Override
        protected Level newElement(Map<String, Object> elemJSON)
        {
            return new Level();
        }
        
        public static class Level implements JSONable, CFExperienceable
        {
            private int exp;
            private int feed;
            private long timeSpawn;
            private String eggKind;

            private Level() {}

            /**
             * @return the exp
             */
            @Override
            public int getExp() {
                return exp;
            }

            /**
             * @return the feed
             */
            public int getFeed() {
                return feed;
            }

            /**
             * @return the timeSpawn
             */
            public long getTimeSpawn() {
                return timeSpawn;
            }

            /**
             * @return the eggKind
             */
            public String getEggKind() {
                return eggKind;
            }
            
            @Override
            public void deser(Map<String, Object> json)
            {
                exp = Integer.valueOf(json.get("exp").toString());
                feed = Integer.valueOf(json.get("feed").toString());
                timeSpawn = 1000L*Long.valueOf(json.get("time_spawn").toString());
                eggKind = json.get("egg_kind").toString();
            }
        }

        @Override
        public void deser(Map<String, Object> json)
        {
            Map<String, Object> jsonLevels = (Map<String, Object>) json.get("levels");
            super.deser(jsonLevels);
            
            this.firstSpawnableLevel = Integer.MAX_VALUE;
            for (Integer level : this) {
                Group.Level conf = this.get(level);
                
                if (level < this.firstSpawnableLevel && conf.timeSpawn > 0)
                {
                    this.firstSpawnableLevel = level;
                }
            }
        }

        public final int getFirstSpawnableLevel() {
            return firstSpawnableLevel;
        }
    }
    
    private static class PenguinKinds extends JSONMapString<CFPenguin.Kind> implements JSONable
    {
        private PenguinKinds() {}

        @Override
        protected Kind newElement(Map<String, Object> elemJSON)
        {
            return new Kind();
        }
    }
    
    public static class Kind implements JSONable
    {
        private Kind() {}
        
        private String group;
        private int star;

        @Override
        public void deser(Map<String, Object> json)
        {
            group = json.get("kind").toString();
            star = Integer.valueOf(json.get("star").toString());
        }

        /**
         * @return the group
         */
        public String getGroup() {
            return group;
        }

        /**
         * @return the star
         */
        public int getStar() {
            return star;
        }
    }
}
