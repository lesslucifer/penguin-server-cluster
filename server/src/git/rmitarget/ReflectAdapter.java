/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import amfservices.actions.ServiceReflectTarget;
import minaconnection.data.impl.PGMapData;
import minaconnection.interfaces.IPGData;
import minaconnection.interfaces.IServices;
import java.io.IOException;
import java.util.Map;
import share.PGException;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author suaongmattroi
 */
public class ReflectAdapter implements IServices{
    
    private final ServiceReflectTarget servicesReflector;
    
    public ReflectAdapter() {
        
        this.servicesReflector = new ServiceReflectTarget();
    }
    
    private IPGData preResp(IPGData reqData, Map<String, Object> dataResp) 
    {
        return new PGMapData(reqData.getIndex(), reqData.getCaller(), "", dataResp, reqData.getNow());
    }
    
    public IPGData loadGame(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.loadGame(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendList(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.getFriendList(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData visitFriend(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.visitFriend(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData dropFish(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.dropFish(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData buyPenguin(IPGData reqData) throws Exception, PGException
    {
        return preResp(reqData, this.servicesReflector.buyPenguin(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData buyFish(IPGData reqData) throws Exception , PGException
    {
        return preResp(reqData, this.servicesReflector.buyFish(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData buyGold(IPGData reqData) throws Exception , PGException
    {
        return preResp(reqData, this.servicesReflector.buyGold(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData sellPenguin(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.sellPenguin(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData penguinWannaEat(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.penguinWannaEat(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
        
    public IPGData upgradeBoxEgg(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.upgradeBoxEgg(reqData.getCaller(), (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData upgradeCote(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.upgradeCote(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData moveEggFromCoteToInventory(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.moveEggFromCoteToInventory(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData moveEggFromBoxEggToInventory(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.moveEggFromBoxEggToInventory(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData sellEggsFromInventory(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.sellEggsFromInventory(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData stealEgg(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.stealEgg(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData helpFriendFish(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.helpFriendFish(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData spawnEgg(IPGData reqData) throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.spawnEgg(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
 
    public IPGData buyExpPenguin(IPGData reqData) throws PGException 
    {
        return preResp(reqData, this.servicesReflector.buyExpPenguin(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
 
    public IPGData buyLevelPenguin(IPGData reqData) throws PGException 
    {
        return preResp(reqData, this.servicesReflector.buyLevelPenguin(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData renameCote(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.renameCote(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData takeSnapshot(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.takeSnapshot(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData takeAds(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.takeAds(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData setUIState(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.setUIState(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData saveSettings(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.saveSettings(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData wakeDogUp(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.wakeDogUp(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getMails(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getMails(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData clearAllMails(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.clearAllMails(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getUserGifts(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getUserGifts(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData receiveGift(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.receiveGift(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData destroyGift(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.destroyGift(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData loginAward(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.loginAward(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getAchievements(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getAchievements(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData receiveAchievementPrize(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.receiveAchievementPrize(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData takeRandomizePrize(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.takeRandomizePrize(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData buyRandomizePrizeTurn(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.buyRandomizePrizeTurn(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData reloadFriendList(IPGData reqData)
            throws PGException, ZingMeApiException, IOException
    {
        return preResp(reqData, this.servicesReflector.reloadFriendList(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData wakeDogFirstTime(IPGData reqData)
            throws PGException, ZingMeApiException, IOException
    {
        return preResp(reqData, this.servicesReflector.wakeDogFirstTime(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData useGiftCode(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.useGiftCode(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getPayment(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.getPayment(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendInfoDetail(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.getFriendInfoDetail(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData friendPenguinWannaEat(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.friendPenguinWannaEat(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData friendPenguinSpawnEgg(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.friendPenguinSpawnEgg(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendAchievements(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.getFriendAchievements(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendPenguinInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendPenguinInfo(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendCoteInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendCoteInfo(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendCoteInfoDetail(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendCoteInfoDetail(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getFriendBoxEggInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getFriendBoxEggInfo(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData acceptDailyQuest(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.acceptDailyQuest(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData returnDailyQuest(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.returnDailyQuest(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData completeDailyQuestImmediately(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.completeDailyQuestImmediately(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData returnMainQuest(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.returnMainQuest(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getUserInfoDetail(IPGData reqData)throws Exception, PGException 
    {
        return preResp(reqData, this.servicesReflector.getUserInfoDetail(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getPenguinInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getPenguinInfo(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getCoteInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getCoteInfo(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getCoteInfoDetail(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getCoteInfoDetail(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getBoxEggInfo(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.getBoxEggInfo(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData increaseUserGold(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserGold(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData increaseUserCoin(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserCoin(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData increaseUserExp(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserExp(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData increaseUserTime(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.increaseUserTime(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData sendGift(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.sendGift(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData sendGiftToAllUsers(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.sendGiftToAllUsers(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData makeGiftCodes(IPGData reqData) throws PGException
    {
        return preResp(reqData, this.servicesReflector.makeGiftCodes(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData addWhiteList(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.addWhiteList(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData addSystemList(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.addSystemList(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData deleteUser(IPGData reqData)
    {
        return preResp(reqData, this.servicesReflector.deleteUser(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    // <editor-fold defaultstate="collapsed" desc="GLOBAL SERVICES">
    public IPGData inSystemList(IPGData reqData)
    {
        return preResp(reqData, (Map<String, Object>)this.servicesReflector.inSystemList(reqData.getCaller(), 
                (Map<String, Object>) reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getHackTime(IPGData reqData)
    {
        return preResp(reqData, (Map<String, Object>)this.servicesReflector.getHackTime(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    
    public IPGData getAllConfigs(IPGData reqData) throws Exception
    {
        return preResp(reqData, (Map<String, Object>)this.servicesReflector.getAllConfigs(reqData.getCaller(), 
                (Map<String, Object>)reqData.getData(), reqData.getNow()));
    }
    // </editor-fold>
}
