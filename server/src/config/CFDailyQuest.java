/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import config.CFDailyQuest.ActionPool;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class CFDailyQuest extends JSONMapArray<CFDailyQuest.Quest>{
    private ActionPools actionPools;

    private CFDailyQuest() {}
    static CFDailyQuest parse(Map<String, Object> json)
    {
        CFDailyQuest dailyQuest = new CFDailyQuest();
        dailyQuest.deser(json);
        return dailyQuest;
    }

    @Override
    protected CFDailyQuest.Quest newElement(Map<String, Object> elemJSON) {
        return new Quest(this.actionPools);
    }

    @Override
    public void deser(Map<String, Object> json) {
        this.actionPools = new ActionPools();
        this.actionPools.deser((Map) json.get("action_pools"));

        super.deser((Map) json.get("quests"));
    }
    
    public ActionPool.Action getActionPool(String actionID, String poolID)
    {
        return this.actionPools.get(poolID).get(actionID);
    }

    public static class ActionPool extends JSONMapString<ActionPool.Action>
    {
        private String[] actionIDs;
        private ActionPool() {}

        @Override
        protected Action newElement(Map<String, Object> elemJSON) {
            return new Action();
        }

        @Override
        public void deser(Map<String, Object> json) {
            super.deser(json);
            
            this.actionIDs = Iterables.toArray(this, String.class);
        }

        public static class Action extends HashMap<String, Object> implements JSONable
        {
            private Action() {}

            @Override
            public void deser(Map<String, Object> json) {
                this.putAll(json);
            }
        }
        
        public String anyActionID(Random randomizer)
        {
            return this.actionIDs[randomizer.nextInt(this.actionIDs.length)];
        }
    }

    private static class ActionPools extends JSONMapString<ActionPool>
    {
        private ActionPools() {}

        @Override
        protected ActionPool newElement(Map<String, Object> elemJSON) {
            return new ActionPool();
        }
    }

    public static class Quest implements JSONable
    {
        private final ActionPools pools;

        private NavigableMap<Integer, ActionPool[]> poolByLevel;
        private NavigableMap<Integer, Value> values;

        private Quest(ActionPools actionPools) {
            this.pools = actionPools;
        }

        @Override
        public void deser(Map<String, Object> json)
        {
            Map<String, Object> poolLevels = (Map) json.get("actions");
            this.poolByLevel = new TreeMap();
            for (Entry<String, Object> poolEntry : poolLevels.entrySet()) {
                Integer level = Integer.valueOf(poolEntry.getKey());
                Map<String, String> poolIDs = (Map) poolEntry.getValue();
                
                ActionPool[] actionPools = new ActionPool[poolIDs.size()];
                for (Entry<String, String> poolIDEntry : poolIDs.entrySet()) {
                    int index = Integer.parseInt(poolIDEntry.getKey());
                    String poolID = poolIDEntry.getValue();

                    actionPools[index] = pools.get(poolID);
                }
                
                poolByLevel.put(level, actionPools);
            }
            

            this.values = new TreeMap();
            Map<String, Object> jsonVals = (Map<String, Object>) json.get("values");
            for (Map.Entry<String, Object> jsonValEntry : jsonVals.entrySet()) {
                String keyFloored = jsonValEntry.getKey();
                Map<String, Object> jsonVal = (Map<String, Object>) jsonValEntry.getValue();

                Value val = new Value();
                val.deser(jsonVal);
                this.values.put(Integer.parseInt(keyFloored), val);
            }

            this.values = Maps.unmodifiableNavigableMap(values);
        }

        public ActionPool[] getActionPools(int uLevel) {
            return poolByLevel.get(poolByLevel.floorKey(uLevel));
        }

        public int getCost(int level)
        {
            return this.values.get(values.floorKey(level)).getCost();
        }
        
        public int getCompleteImmCost(int level)
        {
            return this.values.get(values.floorKey(level))
                    .getCompleteImmediatelyCost();
        }

        public Map<String, Object> getPrize(int level)
        {
            return this.values.get(this.values.floorKey(level)).getPrize();
        }

        private static class Value implements JSONable
        {
            private int cost;
            private int completeImmediatelyCost;
            private Map<String, Object> prize;

            private Value() {}

            @Override
            public void deser(Map<String, Object> json) {
                this.cost = PGHelper.toInteger(json.get("cost"));
                this.completeImmediatelyCost = PGHelper.toInteger(
                        json.get("complete_immediately_cost"));
                this.prize = (Map<String, Object>) json.get("prize");
            }

            public int getCost() {
                return cost;
            }

            public int getCompleteImmediatelyCost() {
                return completeImmediatelyCost;
            }

            public Map<String, Object> getPrize() {
                return prize;
            }
        }
    }
}
