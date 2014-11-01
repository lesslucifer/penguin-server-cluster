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
 * @author Salm
 */
public class CFReleaseEvent {
    
    private CFReleaseEvent() {}
    public static CFReleaseEvent parse(Map<String, Object> json)
    {
        String jsonData = PGHelper.getJSONParser().toJson(json);
        return PGHelper.getJSONParser().fromJson(jsonData, CFReleaseEvent.class);
    }
    
    private Map<String, Object> totalPrize;
    private Map<String, Penguin> penguins;
    private Map<String, Event> events;
    private Map<String, Item> items;

    public Map<String, Object> getTotalPrize() {
        return totalPrize;
    }

    public Map<String, Penguin> getPenguins() {
        return penguins;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public Map<String, Item> getItems() {
        return items;
    }
    
    public static class Penguin
    {
        private Penguin() {}
        
        private Map<Integer, Level> levels;

        public Level getLevel(int lvl)
        {
            return this.levels.get(lvl);
        }
        
        public static class Level
        {
            private Level() {}
            
            private String item;
            private long time;
            private double rate;

            public String getItem() {
                return item;
            }

            public long getTime() {
                return 1000L * time;
            }

            public double getRate() {
                return rate;
            }
        }
    }
    
    public static class Event
    {
        private Event() {}
        
        private Map<String, Object> actions;
        private Map<String, Object> prize;

        public Map<String, Object> getActions() {
            return actions;
        }

        public Map<String, Object> getPrize() {
            return prize;
        }
    }
    
    public static class Item
    {
        private Item() {}
        
        private String resBucket;
        private String cote_resClass;

        public String getResBucket() {
            return resBucket;
        }

        public String getCote_resClass() {
            return cote_resClass;
        }
    }
}