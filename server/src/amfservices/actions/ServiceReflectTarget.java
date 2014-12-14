/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.actions;

import config.PGConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pgentity.HackEntity;
import pgentity.UserList;
import share.AMFBuilder;
import share.PGError;
import share.PGException;
import share.PGHelper;
import share.PGMacro;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author KieuAnh
 */
public class ServiceReflectTarget {
    private final PGServicesAction serviceActions;
    private final ReloadServices reloadServices;
    private final FriendServiceActions friendServices;
    
    public ServiceReflectTarget()
    {
        this.serviceActions = PGServicesAction.inst();
        this.reloadServices = ReloadServices.inst();
        this.friendServices = FriendServiceActions.inst();
    }
    
    public Map<String, Object> checkConnection(String uid, Map<String, Object> params, long now) throws Exception, PGException
    {
        Map<String, Object> result = new HashMap();
        result.put("state", true);
        return result;
    }
    
    public Map<String, Object> loadGame(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        final String signedReq = (String) params.get(PGMacro.SIGNED_REQUEST);
        return this.serviceActions.loadGameAction(uid, signedReq, now);
    }
    
    public Map<String, Object> getFriendList(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        return this.serviceActions.getFriendListAction(uid);
    }
    
    public Map<String, Object> visitFriend(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String fid =   params.get(PGMacro.FID).toString();
        
        Map<String, Object> resp = this.friendServices
                .visitFriendAction(uid, fid, now);
        
        return resp;
    }
    
    public Map<String, Object> dropFish(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String coteId   =   params.get(PGMacro.COTE_ID).toString();
        int nFish       =   Integer.parseInt(params.get(PGMacro.FISH).toString());
        
        return this.serviceActions.dropFishAction(uid, coteId, nFish, now);
    }
    
    public Map<String, Object> buyPenguin(String uid, Map<String, Object> params, long now) throws Exception, PGException
    {
        String coteId   =   String.valueOf( params.get(PGMacro.COTE_ID) );
        String itemId   =   String.valueOf( params.get(PGMacro.ITEM_ID));
        int nPeng = Integer.parseInt( String.valueOf( params.get(PGMacro.NUMBER_ITEM)));
        
        Map<String, Object> amfResult = this.serviceActions.buyPenguinAction(now, uid, coteId, itemId, nPeng);
        
        return amfResult;
    }
    
    public Map<String, Object> buyFish(String uid, Map<String, Object> params, long now) throws Exception , PGException
    {
        String itemType =   params.get(PGMacro.ITEM_ID).toString();
        
        Map<String, Object> amfResult = this.serviceActions.buyFishAction(uid, itemType, now);
        
        return amfResult;
    }
    
    public Map<String, Object> buyGold(String uid, Map<String, Object> params, long now) throws Exception , PGException
    {
        String itemType =   params.get(PGMacro.ITEM_ID).toString();
        
        Map<String, Object> amfResult = this.serviceActions.buyGoldAction(uid, itemType, now);
        
        return amfResult;
    }
    
    public Map<String, Object> sellPenguin(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String coteID = String.valueOf(params.get(PGMacro.COTE_ID));
        String pengID = String.valueOf(params.get(PGMacro.PENGUIN_ID));
        
        Map<String, Object> amfResult = this.serviceActions.sellPenguinAction(uid, coteID, pengID, now);
        
        return amfResult;
    }
    
    public Map<String, Object> penguinWannaEat(String uid, Map<String, Object> params, long now) throws PGException
    {
        String coteId   =   params.get(PGMacro.COTE_ID).toString();
        List<String> penguinIDs = AMFBuilder.amfToList(params.get(PGMacro.PENGUIN_ID_LIST));
        
        return this.serviceActions.penguinWannaEatAction(uid, coteId, penguinIDs, now);
    }
        
    public Map<String, Object> upgradeBoxEgg(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String coteId   =   params.get(PGMacro.COTE_ID).toString();
        
        Map<String, Object> amfResult = this.serviceActions.upgradeBoxEggAction(uid, coteId, now);
        
        return amfResult;
    }
    
    public Map<String, Object> upgradeCote(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String coteId = String.valueOf(params.get(PGMacro.COTE_ID));
        
        Map<String, Object> amfResult = this.serviceActions.upgradeCoteAction(uid, coteId, now);
        
        return amfResult;
    }
    
    public Map<String, Object> moveEggFromCoteToInventory(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        Map<String, Number> movedEggs = (Map<String, Number>) params.get(PGMacro.EGGS);
        
        return this.serviceActions.moveEggFromCoteToInventoryAction(uid, movedEggs, now);
    }
    
    public Map<String, Object> moveEggFromBoxEggToInventory(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        Map<String, Number> movedEggs = (Map<String, Number>) params.get(PGMacro.EGGS);
        
        return this.serviceActions.moveEggFromBoxEggToInventoryAction(uid, movedEggs);
    }
    
    public Map<String, Object> sellEggsFromInventory(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        Map<String, Number> soldEggs = (Map<String, Number>) params.get(PGMacro.EGGS);
        
        return this.serviceActions.sellEggsFromInventoryAction(uid, soldEggs, now);
    }
    
    public Map<String, Object> stealEgg(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String fid = (String) params.get(PGMacro.FID);
        String eggKind = (String) params.get(PGMacro.KIND);
        
        return this.friendServices
                .stealEggAction(uid, fid, eggKind, now);
    }
    
    public Map<String, Object> helpFriendFish(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String fid = (String) params.get(PGMacro.FID);
        
        return this.friendServices
                .helpFriendFishAction(uid, fid, now);
    }
    
    public Map<String, Object> spawnEgg(String uid, Map<String, Object> params, long now) throws Exception, PGException 
    {
        String coteID = params.get(PGMacro.COTE_ID).toString();
        List<String> penguinIDs = AMFBuilder.amfToList(params.get(PGMacro.PENGUIN_ID_LIST));
        
        Map<String, Object> resp = this.serviceActions.spawnEggAction(uid, coteID, penguinIDs, now);
        
        return resp;
    }
 
    public Map<String, Object> buyExpPenguin(String uid, Map<String, Object> params, long now) throws PGException 
    {
        String coteId   =   params.get(PGMacro.COTE_ID).toString();
        String pengId   =   params.get(PGMacro.PENGUIN_ID).toString();

        Map<String, Object> amfResult;
        amfResult = serviceActions.buyExpPenguinAction(uid, coteId, pengId,
                PGConfig.inst().temp().BuyPenguinExp_Exp(), now);

        return amfResult;
    }
 
    public Map<String, Object> buyLevelPenguin(String uid, Map<String, Object> params, long now) throws PGException 
    {
        String coteId   =   params.get(PGMacro.COTE_ID).toString();
        String pengId   =   params.get(PGMacro.PENGUIN_ID).toString();

        Map<String, Object> amfResult;
        amfResult = serviceActions.buyLevelPenguinAction(
                uid, coteId, pengId, now);

        return amfResult;
    }
    
    public Map<String, Object> renameCote(String uid, Map<String, Object> params, long now) throws PGException
    {
        String coteID = (String) params.get(PGMacro.COTE_ID);
        String newCoteName = (String) params.get("new_name");
        
        return this.serviceActions.renameCote(uid, coteID, newCoteName);
    }
    
    public Map<String, Object> takeSnapshot(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.takeSnapshotAction(uid, now);
    }
    
    public Map<String, Object> takeAds(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.takeAdsAction(uid, now);
    }
    
    public Map<String, Object> setUIState(String uid, Map<String, Object> params, long now) throws PGException
    {
        Map<String, Number> uiData = (Map) params.get(PGMacro.UIDATA);
        
        return this.serviceActions.setUIStateAction(uid, uiData);
    }
    
    public Map<String, Object> saveSettings(String uid, Map<String, Object> params, long now) throws PGException
    {
        Map<String, Object> settings = (Map) params.get(PGMacro.SETTINGS);
        
        return this.serviceActions.saveSettingsAction(uid, settings);
    }
    
    public Map<String, Object> wakeDogUp(String uid, Map<String, Object> params, long now) throws PGException
    {
        String wakeItemID = (String) params.get(PGMacro.WAKE_ITEM);
        
        return this.serviceActions.wakeDogUpAction(uid, wakeItemID, now);
    }
    
    public Map<String, Object> getMails(String uid, Map<String, Object> params, long now) throws PGException
    {
        int offset = ((Number) params.get(PGMacro.MAIL_OFFSET)).intValue();
        int length = ((Number) params.get(PGMacro.MAIL_LENGTH)).intValue();
        
        return this.serviceActions.getMailsAction(uid, offset, length);
    }
    
    public Map<String, Object> clearAllMails(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.clearAllMailsAction(uid);
    }
    
    public Map<String, Object> getUserGifts(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.getUserGiftsAction(uid);
    }
    
    public Map<String, Object> receiveGift(String uid, Map<String, Object> params, long now) throws PGException
    {
        String giftID = (String) params.get(PGMacro.GIFT_ID);
        
        return this.serviceActions.receiveGiftAction(uid, giftID, now);
    }
    
    public Map<String, Object> destroyGift(String uid, Map<String, Object> params, long now) throws PGException
    {
        String giftID = (String) params.get(PGMacro.GIFT_ID);
        
        return this.serviceActions.destroyGiftAction(uid, giftID, now);
    }
    
    public Map<String, Object> loginAward(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.loginAwardAction(uid, now);
    }
    
    public Map<String, Object> getAchievements(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.getAchievemensAction(uid, now);
    }
    
    public Map<String, Object> receiveAchievementPrize(String uid, Map<String, Object> params, long now) throws PGException
    {
        String ach = (String) params.get(PGMacro.ACHIEVEMENT_ID);
        String medal = (String) params.get(PGMacro.MEDAL);
        
        return this.serviceActions.receiveAchievementPrizeAction(uid, ach, medal, now);
    }
    
    public Map<String, Object> takeRandomizePrize(String uid, Map<String, Object> params, long now) throws PGException
    {
        return this.serviceActions.takeRandomizePrizeAction(uid, now);
    }
    
    public Map<String, Object> buyRandomizePrizeTurn(String uid, Map<String, Object> params, long now) throws PGException
    {
        String itemID = (String) params.get(PGMacro.RP_ITEM_ID);
        
        return this.serviceActions.buyRandomizePrizeTurnAction(uid, itemID, now);
    }
    
    public Map<String, Object> reloadFriendList(String uid, Map<String, Object> params, long now)
            throws PGException, ZingMeApiException, IOException
    {
        String signedRequest = (String) params.get(PGMacro.SIGNED_REQUEST);
        
        return this.serviceActions.reloadFriendListAction(uid, signedRequest, now);
    }
    
    public Map<String, Object> wakeDogFirstTime(String uid, Map<String, Object> params, long now)
            throws PGException, ZingMeApiException, IOException
    {
        String coteID = (String) params.get(PGMacro.COTE_ID);
        
        return this.serviceActions.wakeDogFirstTimeAction(uid, coteID, now);
    }
    
    public Map<String, Object> useGiftCode(String uid, Map<String, Object> params, long now) throws PGException
    {
        String code = (String) params.get(PGMacro.GIFT_CODE_ID);
        
        return this.serviceActions.useGiftCode(uid, code, now);
    }
    
    public Map<String, Object> getPayment(String uid,
            Map<String, Object> params, long now)
    {
        String billNo = (String) params.get(PGMacro.BILL_NO);
        
        return this.serviceActions.getPaymentAction(uid, billNo);
    }
    
    public Map<String, Object> getFriendInfoDetail(String uid,
            Map<String, Object> params, long now)
    {
        String fid = (String) params.get(PGMacro.FID);
        Map<String, Boolean> getParam = (Map) params.get("get");
        
        boolean getCote = getParam.containsKey(PGMacro.COTE) && getParam.get(PGMacro.COTE);
        boolean getFriend = getParam.containsKey(PGMacro.FRIEND_LIST) && getParam.get(PGMacro.FRIEND_LIST);
        boolean getInv = getParam.containsKey(PGMacro.INVENTORY) && getParam.get(PGMacro.INVENTORY);
        
        return this.friendServices
                .getFriendInfoDetailAction(uid, fid,
                        getCote, getFriend, getInv, now);
    }
    
    public Map<String, Object> friendPenguinWannaEat(String uid,
            Map<String, Object> params, long now)
    {
        String fid = (String) params.get(PGMacro.FID);
        String coteId = (String) params.get(PGMacro.COTE_ID);
        List<String> penguinIDs = AMFBuilder.amfToList(params.get(PGMacro.PENGUIN_ID_LIST));
        
        return this.friendServices
                .friendPenguinWannaEatAction(uid, fid, coteId, penguinIDs, now);
    }
    
    public Map<String, Object> friendPenguinSpawnEgg(String uid,
            Map<String, Object> params, long now)
    {
        String fid = (String) params.get(PGMacro.FID);
        String coteId = (String) params.get(PGMacro.COTE_ID);
        List<String> penguinIDs = AMFBuilder.amfToList(params.get(PGMacro.PENGUIN_ID_LIST));
        
        return this.friendServices
                .friendPenguinSpawnEggAction(uid, fid, coteId, penguinIDs, now);
    }
    
    public Map<String, Object> getFriendAchievements(String uid,
            Map<String, Object> params, long now)
    {
        String fid = (String) params.get(PGMacro.FID);
        
        return this.friendServices
                .friendPenguinSpawnEggAction(uid, fid, now);
    }
    
    public Map<String, Object> getFriendPenguinInfo(String uid,
            Map<String, Object> params, long now) throws PGException
    {
        String fid = (String) params.get(PGMacro.FID);
        String pengID      =   params.get(PGMacro.PENGUIN_ID).toString();
        String coteID = (String) params.get(PGMacro.COTE_ID);
        
        return this.reloadServices.getPenguinInfo(fid, coteID, pengID);
    }
    
    public Map<String, Object> getFriendCoteInfo(String uid,
            Map<String, Object> params, long now) throws PGException
    {
        String fid = (String) params.get(PGMacro.FID);
        String coteID      =   params.get(PGMacro.COTE_ID).toString();    
        return this.reloadServices.getCoteBasicInfo(fid, coteID);
    }
    
    public Map<String, Object> getFriendCoteInfoDetail(String uid, Map<String, Object> params, long now) throws PGException
    {
        String fid = (String) params.get(PGMacro.FID);
        String coteID      =   params.get(PGMacro.COTE_ID).toString(); 
        Map<String, Boolean> getParams = (Map<String, Boolean>) params.get("get");
        
        boolean getBoxEgg = getParams.containsKey(PGMacro.BOXEGG) && getParams.get(PGMacro.BOXEGG);
        boolean getPenguins = getParams.containsKey(PGMacro.PENGUIN_LIST) && getParams.get(PGMacro.PENGUIN_LIST);
        boolean getEggs = getParams.containsKey(PGMacro.EGGS) && getParams.get(PGMacro.EGGS);
        boolean getDog = getParams.containsKey(PGMacro.DOG) && getParams.get(PGMacro.DOG);
        
        return this.reloadServices.getCoteFullInfo(fid, coteID, getBoxEgg, getPenguins, getEggs, getDog);
    }
    
    public Map<String, Object> getFriendBoxEggInfo(String uid, Map<String, Object> params, long now) throws PGException
    {
        String fid = (String) params.get(PGMacro.FID);
        String coteID      =   params.get(PGMacro.COTE_ID).toString(); 
        
        return this.reloadServices.getBoxEggInfo(fid, coteID);
    }
    
    public Map<String, Object> acceptDailyQuest(String uid, Map<String, Object> params, long now) throws PGException
    {
        int qIndex = (Integer) params.get(PGMacro.QUEST_INDEX);
        
        return this.serviceActions.acceptDailyQuestAction(uid, qIndex, now);
    }
    
    public Map<String, Object> returnDailyQuest(String uid, Map<String, Object> params, long now) throws PGException
    {
        int qIndex = ((Number) params.get(PGMacro.QUEST_INDEX)).intValue();
        
        return this.serviceActions.returnDailyQuestAction(uid, qIndex, now);
    }
    
    public Map<String, Object> completeDailyQuestImmediately(String uid, Map<String, Object> params, long now) throws PGException
    {
        int questIndex = ((Number) params.get(PGMacro.QUEST_INDEX)).intValue();
        
        return this.serviceActions.completeDailyQuestImmediately(uid, questIndex, now);
    }
    
    public Map<String, Object> returnMainQuest(String uid, Map<String, Object> params, long now) throws PGException
    {
        String qLine = (String) params.get(PGMacro.QUEST_LINE);
        
        return this.serviceActions.returnMainQuestAction(uid, qLine, now);
    }
    
    public Map<String, Object> getUserInfoDetail(String uid, Map<String, Object> params, long now)throws Exception, PGException 
    {
        Map<String, Boolean> getParam = (Map<String, Boolean>) params.get("get");
        boolean getCote = getParam.containsKey(PGMacro.COTE) && getParam.get(PGMacro.COTE);
        boolean getFriend = getParam.containsKey(PGMacro.FRIEND_LIST) && getParam.get(PGMacro.FRIEND_LIST);
        boolean getInv = getParam.containsKey(PGMacro.INVENTORY) && getParam.get(PGMacro.INVENTORY);
        return this.reloadServices.getUserFullInfo(uid, getCote, getFriend, getInv, now);
    }
    
    public Object getUserCoteList(String uid,
            Map<String, Object> params, long now)
    {
        return reloadServices.getUserCoteList(uid);
    }
    
    public Map<String, Object> getPenguinInfo(String uid, Map<String, Object> params, long now) throws PGException
    {
        String peng_id      =   params.get(PGMacro.PENGUIN_ID).toString();
        String coteID = (String) params.get(PGMacro.COTE_ID);
        
        return this.reloadServices.getPenguinInfo(uid, coteID, peng_id);
    }
    
    public Map<String, Object> getCoteInfo(String uid, Map<String, Object> params, long now) throws PGException
    {
        String coteID      =   params.get(PGMacro.COTE_ID).toString();    
        return this.reloadServices.getCoteBasicInfo(uid, coteID);
    }
    
    public Map<String, Object> getCoteInfoDetail(String uid, Map<String, Object> params, long now) throws PGException
    {
        String coteID      =   params.get(PGMacro.COTE_ID).toString(); 
        Map<String, Boolean> getParams = (Map<String, Boolean>) params.get("get");
        
        boolean getBoxEgg = getParams.containsKey(PGMacro.BOXEGG) && getParams.get(PGMacro.BOXEGG);
        boolean getPenguins = getParams.containsKey(PGMacro.PENGUIN_LIST) && getParams.get(PGMacro.PENGUIN_LIST);
        boolean getEggs = getParams.containsKey(PGMacro.EGGS) && getParams.get(PGMacro.EGGS);
        boolean getDog = getParams.containsKey(PGMacro.DOG) && getParams.get(PGMacro.DOG);
        
        return this.reloadServices.getCoteFullInfo(uid, coteID, getBoxEgg, getPenguins, getEggs, getDog);
    }
    
    public Map<String, Object> getBoxEggInfo(String uid, Map<String, Object> params, long now) throws PGException
    {
        String coteID      =   params.get(PGMacro.COTE_ID).toString(); 
        
        return this.reloadServices.getBoxEggInfo(uid, coteID);
    }
    
    public Map<String, Object> increaseUserGold(String adminUid, Map<String, Object> params, long now) throws PGException
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        String uid = (String) params.get(PGMacro.UID);
        int nGold = PGHelper.toInteger(params.get("number"));
        
        return PGHackServices.inst().increaseUserGold(uid, nGold, now);
    }
    
    public Map<String, Object> increaseUserCoin(String adminUid, Map<String, Object> params, long now) throws PGException
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        String uid = (String) params.get(PGMacro.UID);
        int nCoin = (Integer) params.get("number");
        
        return PGHackServices.inst().increaseUserCoin(uid, nCoin, now);
    }
    
    public Map<String, Object> increaseUserExp(String adminUid,
            Map<String, Object> params, long now) throws PGException
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        String uid = (String) params.get(PGMacro.UID);
        int nExp = (Integer) params.get("number");
        
        return PGHackServices.inst().increaseUserExp(uid, nExp, now);
    }
    
    public Map<String, Object> increaseUserTime(String adminUid,
            Map<String, Object> params, long now) throws PGException
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        String uid = (String) params.get(PGMacro.UID);
        int nTime = ((Number) params.get("number")).intValue();
        
        return PGHackServices.inst().increaseUserTime(uid, nTime, now);
    }
    
    public Map<String, Object> sendGift(String adminUid,
            Map<String, Object> params, long now) throws PGException
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        List<String> receivers = AMFBuilder.amfToList(params.get(PGMacro.RECEIVERS));
        Map<String, Object> giftPrize = (Map) params.get(PGMacro.GIFT_PRIZE);
        int giftExpireTime = ((Number) params.get(PGMacro.GIFT_EXPIRED_TIME)).intValue();
        
        return this.serviceActions.sendGiftAction(receivers, giftPrize, now, giftExpireTime);
    }
    
    public Map<String, Object> sendGiftToAllUsers(String adminUid,
            Map<String, Object> params, long now) throws PGException
    {
        Map<String, Object> giftPrize = (Map) params.get(PGMacro.GIFT_PRIZE);
        int giftExpireTime = ((Number) params.get(PGMacro.GIFT_EXPIRED_TIME)).intValue();
        
        return this.serviceActions.sendGiftToAllUsersAction(giftPrize, now, giftExpireTime);
    }
    
    public Map<String, Object> makeGiftCodes(String adminUid,
            Map<String, Object> params, long now) throws PGException
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        Map<String, Object> giftPrize = (Map) params.get(PGMacro.GIFT_PRIZE);
        int giftExpireTime = ((Number) params.get(PGMacro.GIFT_EXPIRED_TIME)).intValue();
        int nCode = ((Number) params.get(PGMacro.NUMBER_GIFT_CODE)).intValue();
        Object codeExpireObj = params.get(PGMacro.GIFT_CODE_EXPIRED_TIME);
        int codeExpireTime = ((Number) codeExpireObj).intValue();
        
        return this.serviceActions
                .makeGiftCodes(giftPrize, giftExpireTime, nCode, codeExpireTime, now);
    }
    
    public Map<String, Object> addWhiteList(String adminUid,
            Map<String, Object> params, long now)
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        List<String> whiteList = AMFBuilder.amfToList(params.get(PGMacro.WHITE_LIST));
        return this.serviceActions.addWhiteListAction(whiteList);
    }
    
    public Map<String, Object> addSystemList(String adminUid, Map<String, Object> params, long now)
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        List<String> systemList = AMFBuilder.amfToList(params.get(PGMacro.SYSTEM_LIST));
        return this.serviceActions.addSystemListAction(systemList);
    }
    
    public Map<String, Object> deleteUser(String adminUid, Map<String, Object> params, long now)
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        String uid = (String) params.get(PGMacro.UID);
        String adminPassword = (String) params.get(PGMacro.SIGNED_REQUEST);
        return this.serviceActions.deleteUserAction(uid, adminPassword);
    }
    
    public Map<String, Object> getGameMessages(String uid,
            Map<String, Object> params, long now)
    {
        return this.serviceActions.getGameMessagesAction(now);
    }
    
    public Map<String, Object> getAllGameMessages(String adminUid,
            Map<String, Object> params, long now)
    {
        PGException.Assert(PGConfig.inst().temp().AdminTool_UID().equals(adminUid),
                PGError.UNDEFINED, "Wrong authentication");
        
        return this.serviceActions.getAllGameMessagesAction(now);
    }
    
    public Map<String, Object> addGameMessages(String uid,
            Map<String, Object> params, long now)
    {
        List<Map<String, Object>> data = new ArrayList(params.size());
        for (Object msgDataObject : params.values()) {
            Map<String, Object> msgData = (Map) msgDataObject;
            
            data.add(msgData);
        }
        
        return this.serviceActions.addGameMessagesAction(data, now);
    }
    
    public Map<String, Object> disableGameMessages(String uid,
            Map<String, Object> params, long now)
    {
        List<String> msgIDs = new ArrayList(params.size());
        for (Object value : params.values()) {
            msgIDs.add((String) value);
        }
        
        return this.serviceActions.disableGameMessagesAction(msgIDs, now);
    }
    
    public Object visitCote(String uid,
            Map<String, Object> params, long now)
    {
        String coteID = (String) params.get(PGMacro.COTE_ID);
        return serviceActions.visitCote(uid, coteID, now);
    }
    
    public Object visitFriendCote(String uid,
            Map<String, Object> params, long now)
    {
        String fid = (String) params.get(PGMacro.FID);
        String coteID = (String) params.get(PGMacro.COTE_ID);
        return friendServices.visitFriendCote(uid, fid, coteID, now);
    }
    
    public Object penguinSpawnReleaseEventItem(String uid,
            Map<String, Object> params, long now)
    {
        String coteID = (String) params.get(PGMacro.COTE_ID);
        List<String> penguinIDs = 
                AMFBuilder.amfToList(params.get(PGMacro.PENGUIN_ID_LIST));
        
        return ReleaseEvent.INST
                .penguinSpawnReleaseEventItem(uid, coteID, penguinIDs, now);
    }
    
    public Object pickReleaseEventItems(String uid,
            Map<String, Object> params, long now)
    {
        String coteID = (String) params.get(PGMacro.COTE_ID);
        Map<String, Number> items = (Map) params.get(PGMacro.EGGS);
        
        return ReleaseEvent.INST
                .pickReleaseEventItems(uid, coteID, items);
    }
    
    public Object returnReleaseEvent(String uid,
            Map<String, Object> params, long now)
    {
        String eventID = (String) params.get(PGMacro.EVENT_ID);
        
        return ReleaseEvent.INST.returnReleaseEvent(uid, eventID, now);
    }
    
    // <editor-fold defaultstate="collapsed" desc="GLOBAL SERVICES">
    public Object inSystemList(String unused_uid, Map<String, Object> params, long now)
    {
        String uid = (String) params.get(PGMacro.UID);
        return UserList.getList(UserList.ListType.SYSTEM_ACCOUNT).contains(uid);
    }
    
    public Object getHackTime(String unused_uid, Map<String, Object> params, long now)
    {
        String uid = (String) params.get(PGMacro.UID);
        try
        {
            HackEntity hack = HackEntity.getEntity(uid);
            return hack.getDeltaTime();
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    
    public Object getAllConfigs(String unused_uid, Map<String, Object> params, long now)
    {
        return PGConfig.inst().getAllConfigs();
    }
    // </editor-fold>
}
