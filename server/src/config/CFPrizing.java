/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 *
 * @author KieuAnh
 */
public class CFPrizing implements JSONable
{
    private DailyLogin dailyLogin;
    private Ads ads;
    
    private CFPrizing() {}
    public static CFPrizing parse(Map<String, Object> json)
    {
        CFPrizing prizing = new CFPrizing();
        prizing.deser(json);
        
        return prizing;
    }
    
    @Override
    public void deser(Map<String, Object> json) {
        this.dailyLogin = new DailyLogin();
        dailyLogin.deser((Map) json.get("daily_login"));
        
        this.ads = new Ads();
        ads.deser((Map) json.get("ads"));
    }

    public DailyLogin dailyLogin() {
        return dailyLogin;
    }

    public Ads getAds() {
        return ads;
    }
    
    public static class DailyLogin implements JSONable
    {
        private NavigableMap<Integer, Map<String, Object>> dayPrizes =
                new TreeMap();
        
        private DailyLogin() {}

        @Override
        public void deser(Map<String, Object> json) {
            for (Map.Entry<String, Object> dayPrizeEntry : json.entrySet()) {
                int day = Integer.parseInt(dayPrizeEntry.getKey());
                Map<String, Object> prizeData = (Map) dayPrizeEntry.getValue();
                String pzType = (String) prizeData.get("prize");
                Object pzParam = prizeData.get("value");
                
                Map<String, Object> prize = new HashMap();
                prize.put(pzType, pzParam);
                
                dayPrizes.put(day, Collections.unmodifiableMap(prize));
            }
        }
        
        public Map<String, Object> prizeForDay(int day)
        {
            return dayPrizes.get(dayPrizes.floorKey(day));
        }
    }
    
    public static class Ads implements JSONable
    {
        private Ads() {}
        
        private NavigableMap<Integer, Map<String, Object>> data;

        @Override
        public void deser(Map<String, Object> json)
        {
            data = new TreeMap();
            for (Map.Entry<String, Object> adEntry : json.entrySet()) {
                Integer minLevel = Integer.valueOf(adEntry.getKey());
                Map<String, Object> prizeData = (Map) adEntry.getValue();
                
                data.put(minLevel, Collections.unmodifiableMap(prizeData));
            }
        }
        
        public Map<String, Object> getPrize(int uLevel)
        {
            return this.data.get(data.floorKey(uLevel));
        }
    }
}
