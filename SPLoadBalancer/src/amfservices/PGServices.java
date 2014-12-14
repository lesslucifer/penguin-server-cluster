/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package amfservices;

import java.util.*;
import libCore.config.Config;
import org.apache.log4j.*;
import share.PGException;
import share.PGMacro;


/**
 *
 * @author linhta
 */
public class PGServices
{
    //========================= INITIALIZE =============================
    
    private final Reflector refelector;
    private static final Logger LOG = Logger.getLogger(PGServices.class.getName());
    
    public PGServices() throws PGException
    {
        super();
        
        String refClassName = Config.getParam("sv_reflection", "reflector");
        Reflector refl = null;
        try {
            Class<?> refClass = Class.forName(refClassName);
            
            refl = (Reflector) refClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
        }
        
        if (refl != null)
        {
            this.refelector = refl;
        }
        else
        {
            throw new IllegalStateException("Cannot create reflection " + refClassName);
        }
    }
    
    public Map<String, Object> authenticate(Map<String, Object> params)
    {
        return this.refelector.authenticate(params);
    }
    
    public Map<String, Object> authenticateSystem(Map<String, Object> params)
    {
        return this.refelector.authenticateSystem(params);
    }
    
    //========================= SERVICES =============================
    
    public Map<String, Object> loadGame(Map<String, Object> params)
    {
        return this.refelector.reflectCall("loadGame", params);
    }
    
    //----------------------------------------------------------------------

    public Map<String, Object> getFriendList(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("getFriendList", params);
    }
    
    //----------------------------------------------------------------------

    public Map<String, Object> visitFriend(Map<String, Object> params) 
    {
        // fake session id
        Map<String, Object> cheatParams = (Map<String, Object>) params.get("cheat");
        Map<String, Object> dataParams = (Map<String, Object>) params.get("data");
        
        String fid = (String) dataParams.get(PGMacro.FID);
        cheatParams.put(PGMacro.UID, fid);
        
        return this.refelector.reflectCall("visitFriend", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyPenguin(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("buyPenguin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> dropFish(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("dropFish", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyFish(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("buyFish", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyGold(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("buyGold", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sellPenguin(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("sellPenguin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> penguinWannaEat(Map<String, Object> params)
    {
        return this.refelector.reflectCall("penguinWannaEat", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> upgradeBoxEgg(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("upgradeBoxEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> upgradeCote(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("upgradeCote", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> moveEggFromCoteToInventory(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("moveEggFromCoteToInventory", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> moveEggFromBoxEggToInventory(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("moveEggFromBoxEggToInventory", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sellEggsFromInventory(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("sellEggsFromInventory", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> stealEgg(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("stealEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> helpFriendFish(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("helpFriendFish", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> spawnEgg(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("spawnEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyExpPenguin(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("buyExpPenguin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyLevelPenguin(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("buyLevelPenguin", params);
    }

    //----------------------------------------------------------------------
    
    public Map<String, Object> renameCote(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("renameCote", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> takeSnapshot(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("takeSnapshot", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> takeAds(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("takeAds", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> setUIState(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("setUIState", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> saveSettings(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("saveSettings", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> wakeDogUp(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("wakeDogUp", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getMails(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("getMails", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> clearAllMails(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("clearAllMails", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getUserGifts(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("getUserGifts", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> receiveGift(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("receiveGift", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> destroyGift(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("destroyGift", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> loginAward(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("loginAward", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getAchievements(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("getAchievements", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> receiveAchievementPrize(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("receiveAchievementPrize", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> takeRandomizePrize(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("takeRandomizePrize", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> buyRandomizePrizeTurn(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("buyRandomizePrizeTurn", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> reloadFriendList(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("reloadFriendList", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> wakeDogFirstTime(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("wakeDogFirstTime", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> useGiftCode(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("useGiftCode", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getPayment(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getPayment", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getGameMessages(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getGameMessages", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> visitCote(Map<String, Object> params)
    {
        return this.refelector.reflectCall("visitCote", params);
    }
    
    //========================= EVENTS SERVICES =============================
    
    // <editor-fold defaultstate="collapsed" desc="EVENTS SERVICES">
    public Map<String, Object> pickReleaseEventItems(Map<String, Object> params)
    {
        return this.refelector.reflectCall("pickReleaseEventItems", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> penguinSpawnReleaseEventItem(Map<String, Object> params)
    {
        return this.refelector.reflectCall("penguinSpawnReleaseEventItem", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> returnReleaseEvent(Map<String, Object> params)
    {
        return this.refelector.reflectCall("returnReleaseEvent", params);
    }
    // </editor-fold>
    
    //========================= FRIEND SERVICES =============================

    // <editor-fold defaultstate="collapsed" desc="FRIEND SERVICES">
    public Map<String, Object> getFriendInfoDetail(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getFriendInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> friendPenguinWannaEat(Map<String, Object> params)
    {
        return this.refelector.reflectCall("friendPenguinWannaEat", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> friendPenguinSpawnEgg(Map<String, Object> params)
    {
        return this.refelector.reflectCall("friendPenguinSpawnEgg", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getFriendAchievements(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getFriendAchievements", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getFriendPenguinInfo(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getFriendPenguinInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getFriendCoteInfo(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getFriendCoteInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getFriendCoteInfoDetail(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getFriendCoteInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getFriendBoxEggInfo(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getFriendBoxEggInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> visitFriendCote(Map<String, Object> params)
    {
        return this.refelector.reflectCall("visitFriendCote", params);
    }
    
    // </editor-fold>
    
    //========================= QUEST SERVICES =============================
    
    // <editor-fold defaultstate="collapsed" desc="QUEST SERVICES">
    public Map<String, Object> acceptDailyQuest(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("acceptDailyQuest", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> returnDailyQuest(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("returnDailyQuest", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> completeDailyQuestImmediately(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("completeDailyQuestImmediately", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> returnMainQuest(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("returnMainQuest", params);
    }
    // </editor-fold>
    
    //========================= RELOAD SERVICES ==========================

    // <editor-fold defaultstate="collapsed" desc="RELOAD SERVICES">
    
    public Map<String, Object> getUserInfoDetail(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("getUserInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getUserCoteList(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getUserCoteList", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getPenguinInfo(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getPenguinInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getCoteInfo(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getCoteInfo", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getCoteInfoDetail(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getCoteInfoDetail", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getBoxEggInfo(Map<String, Object> params)
    {
        return this.refelector.reflectCall("getBoxEggInfo", params);
    }
    
    // </editor-fold>
    
    //========================= ADMIN TOOLS ===================================
    
    // <editor-fold defaultstate="collapsed" desc="ADMIN SERVICES">
    public Map<String, Object> increaseUserGold(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("increaseUserGold", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> increaseUserCoin(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("increaseUserCoin", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> increaseUserExp(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("increaseUserExp", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> increaseUserTime(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("increaseUserTime", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sendGift(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("sendGift", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> sendGiftToAllUsers(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("sendGiftToAllUsers", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> makeGiftCodes(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("makeGiftCodes", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> addWhiteList(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("addWhiteList", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> addSystemList(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("addSystemList", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> deleteUser(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("deleteUser", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> addGameMessages(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("addGameMessages", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> disableGameMessages(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("disableGameMessages", params);
    }
    
    //----------------------------------------------------------------------
    
    public Map<String, Object> getAllGameMessages(Map<String, Object> params) 
    {
        return this.refelector.reflectCall("getAllGameMessages", params);
    }
    // </editor-fold>
    
    //----------------------------------------------------------------------
}