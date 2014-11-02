/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONValue;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class PGConfig
{
    private static final String[] CONFIGS = {"user", "friends", "ibshop",
        "ibshop_items", "cotes", "items", "box_eggs", "penguin_groups",
        "penguin_kinds", "main_quests", "daily_quests", "prizing",
        "achievements", "randomize_prize", "payment", "temp", "release_event"};
    
    private Map<String, Object> allConfigs = new HashMap();
    private CFUser user;
    private CFItems eggs;
    private CFBoxEgg boxEgg;
    private CFCote cote;
    private CFIBShop shopItems;
    private CFPenguin penguin;
    private CFPlayWithFriend friend;
    private CFMainQuests mainQuest;
    private CFDailyQuest dailyQuest;
    private CFPrizing prizing;
    private CFAchievements achievements;
    private CFRandomizePrize randomizePrizes;
    private CFPayment payment;
    private CFTemp temp;
    private CFReleaseEvent releaseEvent;
    
    private PGConfig()
    {
        super();
        
        try
        {
            this.readAllConfig();
            this.parseAllModels();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private static final PGConfig inst = new PGConfig();
    public static PGConfig inst()
    {
        return inst;
    }

    public Map<String, Object> getAllConfigs()
    {
        return this.allConfigs;
    }

    public CFUser getUser()
    {
        return user;
    }

    public CFItems getEggs() {
        return eggs;
    }

    public CFBoxEgg getBoxEgg() {
        return boxEgg;
    }

    public CFIBShop getShopItems() {
        return shopItems;
    }

    public CFCote getCote() {
        return cote;
    }
    
    public CFPenguin getPenguin()
    {
        return this.penguin;
    }
    
    public CFPlayWithFriend getFriend()
    {
        return this.friend;
    }

    public CFMainQuests getMainQuest() {
        return mainQuest;
    }

    public CFDailyQuest getDailyQuest() {
        return dailyQuest;
    }

    public CFPrizing getPrizing() {
        return prizing;
    }

    public CFAchievements getAchievements() {
        return achievements;
    }

    public CFRandomizePrize getRandomizePrizes() {
        return randomizePrizes;
    }

    public CFPayment getPayment() {
        return payment;
    }

    public CFTemp temp() {
        return temp;
    }
    
    public CFReleaseEvent releaseEvent()
    {
        return releaseEvent;
    }

    private void readAllConfig()
    {
        String confPath = System.getProperty("confpath");
        if (PGHelper.isNullOrEmpty(confPath))
        {
            confPath = PGHelper.valid(System.getProperty("apppath"), ".") + "/config/";
        }
        
        for (String config : CONFIGS)
        {
            try
            {
                String configFile = confPath + config + ".xfj";
                FileReader configReader = new FileReader(new File(configFile));
                Map<String, Object> configData = (Map<String, Object>) JSONValue.parse(configReader);
                
                allConfigs.put(config, configData.get(config));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PGConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void parseAllModels()
    {
        Map<String, Object> userJSON = (Map<String, Object>) this.allConfigs.get("user");
        this.user = CFUser.parse(userJSON);
        
        Map<String, Object> eggsJSON = (Map<String, Object>) this.allConfigs.get("items");
        this.eggs = CFItems.parse(eggsJSON);
        
        Map<String, Object> boxEggJSON = (Map<String, Object>) this.allConfigs.get("box_eggs");
        this.boxEgg = CFBoxEgg.parse(boxEggJSON);
        
        Map<String, Object> shopItemsJSON = (Map<String, Object>) this.allConfigs.get("ibshop_items");
        this.shopItems = CFIBShop.parse(shopItemsJSON);
        
        Map<String, Object> coteJSON = (Map<String, Object>) this.allConfigs.get("cotes");
        this.cote = CFCote.parse(coteJSON);
        
        Map<String, Object> penguinGroupsJSON = (Map) this.allConfigs.get("penguin_groups");
        Map<String, Object> penguinKindsJSON = (Map) this.allConfigs.get("penguin_kinds");
        this.penguin = CFPenguin.parse(penguinGroupsJSON, penguinKindsJSON);
        
        Map<String, Object> friendJSON = (Map) this.allConfigs.get("friends");
        this.friend = CFPlayWithFriend.parse(friendJSON);
        
        Map<String, Object> mainQuestJSON = (Map) this.allConfigs.get("main_quests");
        this.mainQuest = CFMainQuests.parse(mainQuestJSON);
        
        Map<String, Object> dailyQuestJSON = (Map) this.allConfigs.get("daily_quests");
        this.dailyQuest = CFDailyQuest.parse(dailyQuestJSON);
        
        Map<String, Object> prizingJSON = (Map) this.allConfigs.get("prizing");
        this.prizing = CFPrizing.parse(prizingJSON);
        
        Map<String, Object> achievementJSON = (Map) this.allConfigs.get("achievements");
        this.achievements = CFAchievements.parse(achievementJSON);
        
        Map<String, Object> randomizePrizeJSON = (Map) this.allConfigs.get("randomize_prize");
        this.randomizePrizes = CFRandomizePrize.parse(randomizePrizeJSON);
        
        Map<String, Object> paymentJSON = (Map) this.allConfigs.get("payment");
        this.payment = CFPayment.parse(paymentJSON);
        
        Map<String, Object> tempJSON = (Map) this.allConfigs.get("temp");
        this.temp = CFTemp.parse(tempJSON);
        
        Map<String, Object> releaseEventJSON = (Map) this.allConfigs.get("release_event");
        this.releaseEvent = CFReleaseEvent.parse(releaseEventJSON);
    }
}