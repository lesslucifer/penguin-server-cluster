/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import amfservices.actions.ServiceReflectTarget;
import share.data.PGMapData;
import share.data.IPGData;
import minaconnection.interfaces.IServices;
import java.io.IOException;
import java.util.Map;
import share.data.PGDataType;
import share.PGException;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author suaongmattroi
 */
class ReflectAdapter implements IServices{
    
    private final ServiceReflectTarget servicesReflector;
    
    public ReflectAdapter() {
        
        this.servicesReflector = new ServiceReflectTarget();
    }
    
    private IPGData preResp(IPGData reqData, Map<String, Object> dataResp) 
    {
        return new PGMapData(
                reqData.caller(),
                "", dataResp,
                reqData.now(),
                PGDataType.AMF);
    }
    
    public IPGData loadGame(IPGData reqData) throws Exception
    {
        return preResp(reqData, this.servicesReflector.loadGame(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendList(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.getFriendList(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData visitFriend(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.visitFriend(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData dropFish(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.dropFish(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData buyPenguin(IPGData reqData) throws Exception, PGException
    {
        return preResp(reqData, this.servicesReflector.buyPenguin(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData buyFish(IPGData reqData) throws Exception , PGException
    {
        return preResp(reqData, this.servicesReflector.buyFish(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData buyGold(IPGData reqData) throws Exception , PGException
    {
        return preResp(reqData, this.servicesReflector.buyGold(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData sellPenguin(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.sellPenguin(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData penguinWannaEat(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.penguinWannaEat(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
        
    public IPGData upgradeBoxEgg(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.upgradeBoxEgg(reqData.caller(), (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData upgradeCote(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.upgradeCote(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData moveEggFromCoteToInventory(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.moveEggFromCoteToInventory(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData moveEggFromBoxEggToInventory(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.moveEggFromBoxEggToInventory(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData sellEggsFromInventory(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.sellEggsFromInventory(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData stealEgg(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.stealEgg(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData helpFriendFish(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.helpFriendFish(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData spawnEgg(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.spawnEgg(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
 
    public IPGData buyExpPenguin(IPGData reqData) throws PGException 
    {
        return preResp(reqData, this.servicesReflector.buyExpPenguin(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
 
    public IPGData buyLevelPenguin(IPGData reqData) throws PGException 
    {
        return preResp(reqData, this.servicesReflector.buyLevelPenguin(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData renameCote(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.renameCote(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData takeSnapshot(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.takeSnapshot(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData takeAds(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.takeAds(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData setUIState(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.setUIState(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData saveSettings(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.saveSettings(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData wakeDogUp(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.wakeDogUp(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getMails(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getMails(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData clearAllMails(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.clearAllMails(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getUserGifts(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getUserGifts(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData receiveGift(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.receiveGift(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData destroyGift(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.destroyGift(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData loginAward(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.loginAward(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getAchievements(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getAchievements(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData receiveAchievementPrize(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.receiveAchievementPrize(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData takeRandomizePrize(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.takeRandomizePrize(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData buyRandomizePrizeTurn(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.buyRandomizePrizeTurn(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData reloadFriendList(IPGData reqData)
            throws PGException, ZingMeApiException, IOException
    {
        return preResp(reqData, this.servicesReflector.reloadFriendList(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData wakeDogFirstTime(IPGData reqData)
            throws PGException, ZingMeApiException, IOException
    {
        return preResp(reqData, this.servicesReflector.wakeDogFirstTime(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData useGiftCode(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.useGiftCode(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getPayment(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.getPayment(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendInfoDetail(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.getFriendInfoDetail(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData friendPenguinWannaEat(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.friendPenguinWannaEat(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData friendPenguinSpawnEgg(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.friendPenguinSpawnEgg(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendAchievements(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.getFriendAchievements(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendPenguinInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendPenguinInfo(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendCoteInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendCoteInfo(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendCoteInfoDetail(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendCoteInfoDetail(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getFriendBoxEggInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendBoxEggInfo(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData acceptDailyQuest(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.acceptDailyQuest(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData returnDailyQuest(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.returnDailyQuest(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData completeDailyQuestImmediately(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.completeDailyQuestImmediately(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData returnMainQuest(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.returnMainQuest(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getUserInfoDetail(IPGData reqData)throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.getUserInfoDetail(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getPenguinInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getPenguinInfo(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getCoteInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getCoteInfo(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getCoteInfoDetail(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getCoteInfoDetail(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getBoxEggInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getBoxEggInfo(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData increaseUserGold(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserGold(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData increaseUserCoin(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserCoin(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData increaseUserExp(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserExp(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData increaseUserTime(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserTime(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData sendGift(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.sendGift(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData sendGiftToAllUsers(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.sendGiftToAllUsers(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData makeGiftCodes(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.makeGiftCodes(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getGameMessages(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getGameMessages(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData addWhiteList(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.addWhiteList(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData addSystemList(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.addSystemList(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData deleteUser(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.deleteUser(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    // <editor-fold defaultstate="collapsed" desc="GLOBAL SERVICES">
    public IPGData inSystemList(IPGData reqData)
    {
        return preResp(reqData, (Map<String, Object>)this.servicesReflector.inSystemList(reqData.caller(), 
                (Map<String, Object>) reqData.data(), reqData.now()));
    }
    
    public IPGData getHackTime(IPGData reqData)
    {
        return preResp(reqData, (Map<String, Object>)this.servicesReflector.getHackTime(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    
    public IPGData getAllConfigs(IPGData reqData) throws Exception
    {
        return preResp(reqData, (Map<String, Object>)this.servicesReflector.getAllConfigs(reqData.caller(), 
                (Map<String, Object>)reqData.data(), reqData.now()));
    }
    // </editor-fold>
}
