/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.actions;

import db.PGKeys;
import config.CFCote;
import config.CFMainQuests;
import config.CFPenguin;
import config.CFRandomizePrize;
import config.PGConfig;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import libCore.SNServices;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONValue;
import pgentity.*;
import pgentity.EntityContext;
//import pgentity.events.release_event.ReleaseEventServices;
import pgentity.prize.PGPrize;
import pgentity.prize.PrizeFactory;
import pgentity.quest.*;
import pgentity.services.*;
import share.*;
import share.AMFBuilder;
import share.PGException;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author KieuAnh
 */
public class PGServicesAction
{
    private PGServicesAction()
    {
        super();
    }
    
    private static final PGServicesAction inst = new PGServicesAction();
    
    public static PGServicesAction inst()
    {
        return inst;
    }
    
    public Map<String, Object> loadGameAction(String uid, String signedReq,
            long now) throws PGException, IOException, ZingMeApiException
    {
        User user;
        if (User.isExist(uid))
        {
            AutoMigrate.inst().migrate(uid);
            user = User.getUser(uid);
        }
        else
        {
            user = UserServices.inst().createNewUser(uid, signedReq, now);
        }
        
        EntityContext context = EntityContext.getContext(user);
        
        QuestServices.inst().updateQuest(context, now);
        
        QuestLogger questLogger = QuestServices.inst().getQuestLogger(uid, now);
        CoteServices.inst().updateCote(context, questLogger, now);
        
        context.saveToDB();
        
        UserServices.inst().registerLogin(questLogger, uid, signedReq, now);
        
        // build response
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.USER, user.buidlFullAMF(true, false, true, true, now));
        
        Map<String, Object> playWithFriend = new HashMap();
        playWithFriend.put(PGMacro.NUMBER_FRIEND_HELPED_FISH,
                HelpFriendFish.getEntity(uid, now).numberFriendHelped());
        playWithFriend.put(PGMacro.NUMBER_FRIEND_STOLEN_EGG,
                StealFriendEgg.getEntity(uid, now).numberFriendStolen());
        response.put(PGMacro.PLAY_WITH_FRIEND, playWithFriend);
        
        response.put(PGMacro.QUEST,
                QuestServices.inst().buildQuestAMF(context, now));
        
//        response.put(PGMacro.RELEASE_EVENT,
//                ReleaseEventServices.SERVICES.buildAMF(uid));
        
        return response;
    }
    
    public Map<String, Object> getFriendListAction(String uid) throws PGException
    {
        FriendList friendList = FriendList.getFriendList(uid);
        return friendList.buildAMF();
    }
    
    public Map<String, Object> penguinWannaEatAction(String uid, String coteID,
            List<String> penguinIDs, long now) throws PGException
    {
        final EntityContext context = EntityContext.getContext(uid);
        
        for (String pengId : penguinIDs) {
            PGException.Assert(context.getCote().penguins().contains(pengId),
                    PGError.PENGUIN_NOT_IN_COTE, "Penguin isn't contained in cote");
        }
        
        PriorityQueue<Penguin> penguins = new PriorityQueue(penguinIDs.size(),
            new Comparator<Penguin>() {
            @Override
            public int compare(Penguin p1, Penguin p2) {
                long p1NextEatTime = PenguinServices.inst()
                        .nextEat(p1, context.getCote());
                long p2NextEatTime = PenguinServices.inst()
                        .nextEat(p2, context.getCote());
                
                return (p1NextEatTime > p2NextEatTime)?1:
                        ((p1NextEatTime == p2NextEatTime)?0:-1);
            }
        });
        
        Map<String, Object> failData = new HashMap();
        
        int remainFish = context.getCote().getPoolFish();
        for (String pengId : penguinIDs) {
            Penguin penguin = Penguin.getPenguin(uid, coteID, pengId);
            
            long nextEat = PenguinServices.inst()
                    .nextEat(penguin, context.getCote());
            if (nextEat > now)
            {
                Map<String, Object> lastPenguinEatData = new HashMap();
                lastPenguinEatData.put(PGMacro.TIME_LAST_EAT, penguin.getLastEat());
                lastPenguinEatData.put(PGMacro.FISH_LAST_EAT, penguin.getFood());
                
                failData.put(penguin.getPenguinID(), lastPenguinEatData);
            }
            else
            {
                PGException.Assert(remainFish > 0,
                        PGError.EMPTY_POOL, "Empty pool");
                PGException.Assert(PenguinServices.inst()
                        .configOf(penguin).getFeed() > 0, PGError.PENGUIN_CANNOT_EAT,
                        "Penguin cannot eat");
                
                penguins.add(penguin);
                remainFish -= Math.min(PenguinServices.inst()
                        .configOf(penguin).getFeed(), remainFish);
            }
        }
        
        List<Penguin> fedPenguins = new ArrayList(penguinIDs.size());
        while (!penguins.isEmpty())
        {
            Penguin penguin = penguins.poll();
            long nextEat = PenguinServices.inst()
                    .nextEat(penguin, context.getCote());
            
            QuestLogger questLogger = QuestServices.inst().getQuestLogger(uid, now);
            PenguinServices.inst().eat(penguin, context, questLogger, nextEat);
            fedPenguins.add(penguin);
        }
        
        Map<String, Object> successData = new HashMap();
        for (Penguin penguin : fedPenguins) {
            penguin.saveToDB();
            successData.put(penguin.getPenguinID(), penguin.getFood());
        }

        context.saveToDB();
        
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.SUCCESS, successData);
        response.put(PGMacro.FAIL, failData);
        
        return response;
    }
    
    public Map<String, Object> dropFishAction(String uid, String coteID, int nFish, long now) throws PGException
    {
        EntityContext context = EntityContext.getContext(uid);
        
        PGException.Assert(context.getUser().getFish() >= nFish,
                PGError.NOT_ENOUGH_FISH, "Not enough fish (" + context.getUser().getFish() + ")");
        
        QuestLogger questLogger = QuestServices.inst().getQuestLogger(uid, now);
        
        Map<String, Integer> atePenguins = CoteServices.inst().dropFish(context,
                questLogger, nFish, now);
        
        questLogger.log(new DropFishRecord());
        questLogger.log(new DropFish_FishRecord(nFish));
        
        context.getUser().setFish(context.getUser().getFish() - nFish);
        //PGUserServices.inst().increaseUserExp(context, (int) (nFish * PGConst.FISH_TO_EXP), now);
        
        context.saveToDB();
        
        return AMFBuilder.toAMF(atePenguins);
    }
    
    public Map<String, Object> upgradeCoteAction(String uid, String coteId,
            long now) throws PGException
    {
        User user =   User.getUser(uid);
        user.updateFromDB();
        
        Cote cote   =   Cote.getCote(uid, coteId);
        cote.updateFromDB();
        PGException.Assert(user.cotes().contains(cote.getCoteID()),
                PGError.NOT_FRIEND_COTE, "Cote's not current belong to user");
        
        QuestLogger userQLogger = QuestServices.inst().getQuestLogger(uid, now);
        CoteServices.inst().upgradeCote(user, userQLogger, cote);
        
        // update to db
        cote.saveToDB();
        user.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        result.put(PGMacro.LEVEL, cote.getLevel());
        return result;
    }
    
    public Map<String, Object> upgradeBoxEggAction(String uid, String coteID, long now) throws PGException
    {
        User user =   User.getUser(uid);
        user.updateFromDB();
        
        Cote cote   =   Cote.getCote(uid, coteID);
        cote.updateFromDB();
        PGException.Assert(user.cotes().contains(cote.getCoteID()),
                PGError.NOT_USER_COTE,
                "Cote's not currently belong to user");
        
        BoxEgg boxegg = BoxEgg.getBoxEgg(uid, coteID);
        QuestLogger userQLogger = QuestServices.inst().getQuestLogger(uid, now);
        
        BoxEggServices.inst().upgradeBoxEgg(user, userQLogger, boxegg);
        
        // update to db
        boxegg.saveToDB();
        user.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        result.put(PGMacro.LEVEL, boxegg.getLevel());
        return result;
    }
    
    public Map<String, Object> sellPenguinAction(String uid, String coteID,
            String penguinId, long now) throws PGException
    {
        Cote cote   =   Cote.getCote(uid, coteID);
        PGException.Assert(cote.penguins().contains(penguinId),
                PGError.PENGUIN_NOT_IN_COTE,
                "Penguin's not contained in cote: " + coteID);
        
        Penguin penguin = Penguin.getPenguin(uid, coteID, penguinId);
        int pLevel = penguin.getLevel();
        
        Penguin.destroy(uid, coteID, penguinId);
        cote.penguins().remove(penguinId);
        
        QuestLogger qLogger = QuestServices.inst().getQuestLogger(uid, now);
        qLogger.log(new ParolePenguinRecord(pLevel));
        
        //=======================================================
        // Not current implement
        // Haven't design add gold (or coin) for sell penguin
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        
        return result;
    }
    
    public Map<String, Object> buyPenguinAction(long timeNow, String uid, String coteId, String itemId, int nPeng) throws PGException
    {
        User user = User.getUser(uid);
        PGException.Assert(user.cotes().contains(coteId), PGError.NOT_USER_COTE,
                coteId + " not belong to user " + uid);
        
        // Check number of Peng in cote 
        Cote cote = Cote.getCote(uid, coteId);
        
        int maxPenguin = PGConfig.inst().getCote().get(cote.getLevel()).getMaxPenguin();
        PGException.Assert(cote.penguins().size() + nPeng <= maxPenguin,
                PGError.NOT_ENGOUH_PENGUIN_SLOT,
                "Cote hasn't enough slot for new penguin");
        
        QuestLogger userQLogger = QuestServices.inst().getQuestLogger(uid, timeNow);
        Penguindex penguindex = Penguindex.getPenguindex(uid);
        String[] pengIds = ShopServices.inst().buyPenguins(timeNow, user,
                userQLogger, penguindex, cote.penguins(), itemId, nPeng);
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.PENGUIN_ID_LIST, AMFBuilder.toAMF(pengIds));
        
        user.saveToDB();
        
        return result;
    }
    
    public Map<String, Object> buyFishAction(String userId, String itemId, long now) throws PGException
    {
        // Check user data with item value
        User user =   User.getUser(userId);
        user.updateFromDB();
        
        QuestLogger userQLogger = QuestServices.inst().getQuestLogger(userId, now);
        Boolean canBuy = ShopServices.inst().buyFish(user, userQLogger, itemId, 1);
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, canBuy);
        result.put(PGMacro.FISH, user.getFish());
        
        user.saveToDB();
        
        return result;
    }
    
    public Map<String, Object> buyGoldAction(String userId, String itemId, long now) throws PGException
    {
        // Check user data with item value
        User user =   User.getUser(userId);
        user.updateFromDB();
        
        Boolean canBuy = ShopServices.inst().buyGold(user, itemId);
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, canBuy);
        result.put(PGMacro.GOLD, user.getGold());
        
        user.saveToDB();
        
        return result;
    }
    
    public Map<String, Object> spawnEggAction(String uid,
            String coteID, List<String> penguinIDs, long now)
            throws PGException
    {
        User user = User.getUser(uid);
        PGException.Assert(user.cotes().contains(coteID),
                PGError.INVALID_COTE, "Invalid cote");
        
        final Cote cote = Cote.getCote(uid, coteID);
        for (String pengId : penguinIDs) {
            PGException.Assert(cote.penguins().contains(pengId),
                    PGError.PENGUIN_NOT_IN_COTE, "Penguin isn't contained in cote");
        }
        
        PriorityQueue<Penguin> penguins = new PriorityQueue(Math.max(penguinIDs.size(), 1),
            new Comparator<Penguin>() {
            @Override
            public int compare(Penguin p1, Penguin p2) {
                long p1NextSpawnTime = PenguinServices.inst().nextSpawn(p1, cote);
                long p2NextSpawnTime = PenguinServices.inst().nextSpawn(p2, cote);
                
                return (p1NextSpawnTime > p2NextSpawnTime)?1:
                        ((p1NextSpawnTime == p2NextSpawnTime)?0:-1);
            }
        });
        
        Map<String, Object> failData = new HashMap();
        
        // init penguin entities
        for (String pengId : penguinIDs) {
            Penguin penguin = Penguin.getPenguin(uid, coteID, pengId);
            
            long nextSpawn = PenguinServices.inst().nextSpawn(penguin, cote);
            if (nextSpawn > now)
            {
                Map<String, Object> failPenguinData = new HashMap(2);
                failPenguinData.put(PGMacro.TIME_LAST_SPAWN, penguin.getLastSpawn());
                failPenguinData.put(PGMacro.EGG_STORE, penguin.getLastEggStorage().getValue());
                
                failData.put(pengId, failPenguinData);
            }
            else
            {
                penguins.add(penguin);
            }
        }
        
        Map<String, Object> successData = new HashMap();
        List<String> limitedEggPenguins = new LinkedList();
        
        // need for add egg
        BoxEgg boxEgg = BoxEgg.getBoxEgg(uid, coteID);
        Dog dog = Dog.getDog(uid, coteID);
        
        while (!penguins.isEmpty()) {
            Penguin penguin = penguins.poll();
            long nextSpawn = PenguinServices.inst().nextSpawn(penguin, cote);
            String spawnedEggKind = PenguinServices.inst().spawnEgg(penguin, nextSpawn);

            EggStoreServices.EggStorage eggStorage = EggStoreServices.inst()
                    .addEgg(cote, boxEgg, dog, spawnedEggKind, now);
            if (eggStorage == EggStoreServices.EggStorage.LIMITED)
            {
                limitedEggPenguins.add(penguin.getPenguinID());
            }
            
            penguin.setLastEggStorage(eggStorage);
            penguin.saveToDB();

            Map<String, Object> penguinResp = new HashMap();
            penguinResp.put(PGMacro.KIND, spawnedEggKind);
            penguinResp.put(PGMacro.EGG_STORE, eggStorage.getValue());
            successData.put(penguin.getPenguinID(), penguinResp);
        }
        
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.SUCCESS, successData);
        response.put(PGMacro.FAIL, failData);
        response.put(PGMacro.SPAWN_LIMITED_PENGUINS,
                AMFBuilder.toAMF(limitedEggPenguins));
        return response;
    }
    
    public Map<String, Object> moveEggFromCoteToInventoryAction(String uid,
            Map<String, Number> eggPacks, long now) throws PGException
    {
        CoteList userCotes = CoteList.getCotes(uid);
        Cote cote = Cote.getCote(uid, userCotes.at(0));
        Map<String, Number> validEggs = EggStoreServices.inst()
                .validateEgg(cote.eggStore(), eggPacks);
        
        Inventory inventory = Inventory.getInventory(uid);
        int inventoryAvail = PGConfig.inst().temp().MaxInventory() - inventory.numberItems();
        
        Map<String, Number> successEggs = EggStoreServices.inst()
                .truncateEgg(validEggs, inventoryAvail);
        
        int nEggMoved = 0;
        for (Map.Entry<String, Number> movedEggEntry : successEggs.entrySet()) {
            nEggMoved += movedEggEntry.getValue().intValue();
        }
        
        if (nEggMoved > 0)
        {
            EggStoreServices.inst()
                    .moveEgg(cote.eggStore(), inventory.eggStore(), successEggs);
            
            QuestLogger qLogger = QuestServices.inst().getQuestLogger(uid, now);
            qLogger.log(new CollectEggsRecord(nEggMoved));
        }
        
        // build response amf
        Map<String, Object> response = new HashMap();
        
        // build success
        if (nEggMoved > 0)
        {
            response.put(PGMacro.SUCCESS_EGGS, successEggs);
        }
        
        // build full inventory eggs
        if (!successEggs.equals(validEggs))
        {
            Map<String, Number> fullInvEggs = EggStoreServices.inst()
                    .substractEggs(validEggs, successEggs);
            
            response.put(PGMacro.FULL_INVENTORY_EGGS, fullInvEggs);
        }
        
        // build failed eggs
        if (!validEggs.equals(eggPacks))
        {
            Map<String, Number> failedEggs = EggStoreServices.inst()
                    .substractEggs(eggPacks, validEggs);
            
            response.put(PGMacro.FAILED_EGGS, failedEggs);
        }
        
        return response;
    }
    
    public Map<String, Object> moveEggFromBoxEggToInventoryAction(String uid,
            Map<String, Number> eggPacks) throws PGException
    {
        CoteList userCotes = CoteList.getCotes(uid);
        String coteID = userCotes.at(0);
        BoxEgg boxEgg = BoxEgg.getBoxEgg(uid, coteID);
        
        int nEggMoved = 0;
        for (Map.Entry<String, Number> movedEggEntry : eggPacks.entrySet()) {
            int nEgg = movedEggEntry.getValue().intValue();
            
            nEggMoved += nEgg;
        }
        
        Inventory inventory = Inventory.getInventory(uid);
        int nEggAfterMoved = inventory.eggStore().nOfEggs() + nEggMoved;
        PGException.Assert(nEggAfterMoved <= PGConfig.inst().temp().MaxInventory(),
                PGError.NOT_ENOUGH_INVENTORY_SLOT,
                String.format("Not enough inventory slot; max %d; need %d",
                        PGConfig.inst().temp().MaxInventory(), nEggAfterMoved));
        
        EggStoreServices.inst().moveEgg(boxEgg.eggStore(), inventory.eggStore(), eggPacks);
        
        return AMFBuilder.toAMF(eggPacks);
    }
    
    public Map<String, Object> sellEggsFromInventoryAction(String userId,
            Map<String, Number> soldEggPacks, long now) throws PGException
    {
        User user = User.getUser(userId);
        int totalEggPrice = EggStoreServices.inst()
                .removeEggFromStore(user.inventory().eggStore(), soldEggPacks);
        
        user.increaseGold(totalEggPrice);
        user.saveToDB();
        
        int nEggSold = 0;
        for (Map.Entry<String, Number> soldEggEntry : soldEggPacks.entrySet()) {
            int nEgg = soldEggEntry.getValue().intValue();
            
            nEggSold += nEgg;
        }
        QuestLogger logger = QuestServices.inst().getQuestLogger(userId, now);
        logger.log(new SellEggRecord(nEggSold));
        
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.SUCCESS, true);
        return response;
    }
    
    public Map<String, Object> buyExpPenguinAction(String uid,
            String coteID, String pengId, int nExp, long now) throws PGException
    {
        User user = User.getUser(uid);
        
        int nReqCoin = PGConfig.inst().temp().BuyPenguinExp_Cost();
        PGException.Assert(user.getCoin() >= nReqCoin,
                PGError.NOT_ENOUGH_COIN, "Not enough coin");
        
        PGException.Assert(user.cotes().contains(coteID),
                PGError.NOT_USER_COTE,
                "Cote's not contained in user cotes: " + coteID);
        
        PenguinList penguinList = PenguinList.getPenguinList(uid, coteID);
        
        PGException.Assert(penguinList.contains(pengId), 
                PGError.PENGUIN_NOT_IN_COTE,
                "Penguin's not contained in cote: " + pengId);
        
        Penguin penguin = Penguin.getPenguin(uid, coteID, pengId);
        CFPenguin.Group grConf = PGConfig.inst().getPenguin()
                .getGroup(penguin.getKind());
        CFPenguin.Group.Level conf = grConf.get(penguin.getLevel() + 1);
        
        PGException.Assert(conf != null, PGError.MAX_LEVEL_PENGUIN,
                "Penguin's level is max");
        
        PGException.Assert(penguin.getExp() + nExp <= conf.getExp() ||
                grConf.containsKey(penguin.getLevel() + 2),
                PGError.MAX_LEVEL_PENGUIN,
                "Penguin's level is max");
        
        PenguinServices.inst().increasePenguinExp(uid, penguin, nExp, now);
        UserServices.inst().decreaseCoin(user, nReqCoin);
        
        penguin.saveToDB();
        user.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.PENGUIN, penguin.buildAMF());
        Map<String, Object> userData = new HashMap();
        userData.put(PGMacro.COIN, user.getCoin());
        result.put(PGMacro.USER, userData);
        
        return result;
    }
    
    public Map<String, Object> buyLevelPenguinAction(String uid,
            String coteID, String pengId, long now) throws PGException
    {
        User user = User.getUser(uid);
        
        PGException.Assert(user.cotes().contains(coteID),
                PGError.NOT_USER_COTE,
                "Cote's not contained in user cotes: " + coteID);
        
        PenguinList penguinList = PenguinList.getPenguinList(uid, coteID);
        
        PGException.Assert(penguinList.contains(pengId), 
                PGError.PENGUIN_NOT_IN_COTE,
                "Penguin's not contained in cote: " + pengId);
        
        Penguin penguin = Penguin.getPenguin(uid, coteID, pengId);
        CFPenguin.Group grConf = PGConfig.inst().getPenguin()
                .getGroup(penguin.getKind());
        CFPenguin.Group.Level conf = grConf.get(penguin.getLevel() + 1);
        
        PGException.Assert(conf != null, PGError.MAX_LEVEL_PENGUIN,
                "Penguin's level is max");
        
        int incExp = conf.getExp() - penguin.getExp();
        int nReqCoin = 1 +
                ((incExp - 1) / PGConfig.inst().temp().BuyPenguinLevel_ExpPerCoin());
        
        PGException.Assert(user.getCoin() >= nReqCoin,
                PGError.NOT_ENOUGH_COIN, "Not enough coin");
        
        PenguinServices.inst().increasePenguinExp(uid, penguin, incExp, now);
        UserServices.inst().decreaseCoin(user, nReqCoin);
        
        penguin.saveToDB();
        user.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.PENGUIN, penguin.buildAMF());
        Map<String, Object> userData = new HashMap();
        userData.put(PGMacro.COIN, user.getCoin());
        result.put(PGMacro.USER, userData);
        
        return result;
    }
    
    public Map<String, Object> renameCote(String uid, String coteID, String newName)
    {
        PGException.Assert(newName != null, PGError.NULL_COTE_NAME,
                "Cote " + coteID + " cannot be set to null");
        
        Cote cote = Cote.getCote(uid, coteID);
        cote.setCoteName(newName);
        cote.saveToDB();
        
        Map<String, Object> resp = new HashMap();
        resp.put(PGMacro.SUCCESS, true);
        return resp;
    }
    
    public Map<String, Object> wakeDogUpAction(String uid,
            String wakeItemID, long now)
    {
        CFCote.Dog.Item wakeItem = PGConfig.inst().getCote().getDog().get(wakeItemID);
        PGException.Assert(wakeItem != null,
                PGError.INVALID_ITEM,
                "Wake item " + wakeItemID + " are invalid");
        
        User user = User.getUser(uid);
        switch (wakeItem.getPaymentType())
        {
            case CFCote.Dog.Item.PAYMENT_TYPE_GOLD:
                PGException.Assert(user.getGold() >= wakeItem.getPrice(),
                        PGError.NOT_ENOUGH_GOLD,
                        "Not enough gold (" + user.getGold() + "/" + wakeItem.getPrice() + ")");
                break;
            case CFCote.Dog.Item.PAYMENT_TYPE_COIN:
                PGException.Assert(user.getCoin() >= wakeItem.getPrice(),
                        PGError.NOT_ENOUGH_COIN,
                        "Not enough gold (" + user.getCoin() + "/" + wakeItem.getPrice() + ")");
                break;
            default:
                PGException.Assert(false,
                        PGError.UNDEFINED,
                        "Invalid payment " + wakeItem.getPaymentType());
                break;
        }
        
        String coteID = user.cotes().at(0);
        Dog dog = Dog.getDog(uid, coteID);
        final long nextSleepTime = Math.max(now, dog.getNextSleep()) + wakeItem.getTime();
        PGException.Assert(nextSleepTime <= now + PGConfig.inst().temp().MaxDogTime(), 
                PGError.DOG_MAX_AWAKE_TIME, "Dog max awake time");
        
        switch (wakeItem.getPaymentType())
        {
            case CFCote.Dog.Item.PAYMENT_TYPE_GOLD:
                QuestLogger qLogger = QuestServices.inst().getQuestLogger(uid, now);
                UserServices.inst().decreaseGold(user, qLogger, wakeItem.getPrice());
                break;
            case CFCote.Dog.Item.PAYMENT_TYPE_COIN:
                UserServices.inst().decreaseCoin(user, wakeItem.getPrice());
                break;
        }
        
        dog.setNextSleep(nextSleepTime);
        UserTempData uTempData = UserTempData.getTempData(uid);
        uTempData.setData(PGMacro.WAKE_DOG_FIRST_TIME, true);
        
        dog.saveToDB();
        user.saveToDB();
        
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.NEXT_SLEEP, nextSleepTime);
        return response;
    }
    
    public Map<String, Object> acceptDailyQuestAction(String uid, int questIndex, long now)
    {
        DailyQuest dailyQuest = DailyQuest.getQuest(uid, now);
        PGException.Assert(dailyQuest.getCurrentIndex() == questIndex,
                PGError.QUEST_WAS_NOT_ACCEPTED,
                "Quest " + questIndex + " doesn't current; current is "
                        + dailyQuest.getCurrentIndex());
        
        PGException.Assert(dailyQuest.getCurrentState() == QuestState.NEW,
                PGError.ACCEPT_NOT_NEW_QUEST,
                "Quest " + questIndex + " must be new; current state: "
                        + dailyQuest.getCurrentState());
        
        EntityContext context = EntityContext.getContext(uid);
        QuestServices.inst().acceptDailyQuest(dailyQuest, context);
        
        context.saveToDB();
        dailyQuest.saveToDB();
        
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.SUCCESS, true);
        return response;
    }
    
    public Map<String, Object> returnDailyQuestAction(String uid, int questIndex, long now)
    {
        DailyQuest dailyQuest = DailyQuest.getQuest(uid, now);
        PGException.Assert(dailyQuest.getCurrentIndex() == questIndex,
                PGError.QUEST_WAS_NOT_ACCEPTED,
                "Quest " + questIndex + " doesn't current; current is "
                        + dailyQuest.getCurrentIndex());
        
        PGException.Assert(dailyQuest.getCurrentState()== QuestState.ACCEPTED,
                PGError.QUEST_WAS_NOT_ACCEPTED,
                "Quest " + questIndex + " must be accepted for return; current state: "
                        + dailyQuest.getCurrentState());
        
        EntityContext context = EntityContext.getContext(uid);
        Map<String, Object> pzDesc = QuestServices.inst().returnDailyQuest(dailyQuest, context, now);
        
        context.saveToDB();
        dailyQuest.saveToDB();
        
        return pzDesc;
    }
    
    public Map<String, Object> completeDailyQuestImmediately(String uid, int questIndex, long now)
    {
        DailyQuest dailyQuest = DailyQuest.getQuest(uid, now);
        PGException.Assert(dailyQuest.getCurrentIndex() == questIndex,
                PGError.QUEST_WAS_NOT_ACCEPTED,
                "Quest " + questIndex + " doesn't current; current is "
                        + dailyQuest.getCurrentIndex());
        
        PGException.Assert(dailyQuest.getCurrentState()== QuestState.ACCEPTED,
                PGError.QUEST_WAS_NOT_ACCEPTED,
                "Quest " + questIndex + " must be accepted for complete; current state: "
                        + dailyQuest.getCurrentState());
        
        User user = User.getUser(uid);
        final int completeImmCost = PGConfig.inst().getDailyQuest()
                .get(questIndex).getCompleteImmCost(user.getLevel());
        
        PGException.Assert(user.getCoin() >= completeImmCost,
                PGError.NOT_ENOUGH_COIN,
                "Not enough coin for complete quest immedately; Require: " +
                user.getCoin() + " have: " + completeImmCost);
        
        EntityContext context = EntityContext.getContext(user);
        Map<String, Object> pzDesc = QuestServices.inst()
                .completeImmediatelyDailyQuest(dailyQuest, context, now);
        
        UserServices.inst().decreaseCoin(user, completeImmCost);
        
        context.saveToDB();
        dailyQuest.saveToDB();
        
        return pzDesc;
    }
    
    public Map<String, Object> returnMainQuestAction(String uid, String qLine, long now)
    {
        MainQuestLine questLine = MainQuestLine.getQuestLine(uid, qLine);
        
        PGException.Assert(questLine.getState() == QuestState.ACCEPTED,
                PGError.QUEST_WAS_NOT_ACCEPTED,
                "You haven't accepted quest " + qLine + "{" + questLine.getIndex() +"} yet!");
        
        EntityContext context = EntityContext.getContext(uid);
        PGException.Assert(questLine.getChecker().isAccept(context),
                PGError.INCOMPLETED_QUEST,
                "You not have enough resource for return this quest");
        
        questLine.getChecker().returnQuest(context);
        Map<String, Object> prizeDesc = questLine.getPrize().award(context, now);
        
        QuestLogger qLogger = QuestServices.inst().getQuestLogger(uid, now);
        qLogger.log(new CompletedMainQuestRecord());
        
        questLine.setState(QuestState.RETURNED);
        
        CFMainQuests.QuestLine qLineConf = PGConfig.inst().getMainQuest().get(qLine);
        
        // get new main quest
        int currentMinLevel = qLineConf.minimizeLevel(context.getUser().getLevel());
        int lastAcceptMinLevel = qLineConf.minimizeLevel(questLine.getLastAcceptLevel());
        if (currentMinLevel > lastAcceptMinLevel)
        {
            questLine.setIndex(0);
            questLine.setLastAcceptLevel(context.getUser().getLevel());
            questLine.setState(QuestState.ACCEPTED);
        }
        else if (questLine.getIndex() + 1 < qLineConf.get(currentMinLevel).size())
        {
            questLine.setIndex(questLine.getIndex() + 1);
            questLine.setLastAcceptLevel(context.getUser().getLevel());
            questLine.setState(QuestState.ACCEPTED);
        }
        
        context.saveToDB();
        questLine.saveToDB();
        
        return prizeDesc;
    }
    
    public Map<String, Object> takeSnapshotAction(String uid, long now)
    {
        UserDailyData uDailyData = UserDailyData.getData(uid, now);
        boolean isTakenSnapshot = Boolean.parseBoolean(
                uDailyData.getData(PGMacro.TAKEN_SNAPSHOT));
        if (!isTakenSnapshot)
        {
            User user = User.getUser(uid);
            UserServices.inst().increaseCoin(user,
                    PGConfig.inst().temp().TakeSnapshotFirstTimeInDay_Prize());
            user.saveToDB();
            uDailyData.setData(PGMacro.TAKEN_SNAPSHOT, true);
        }
        
        QuestLogger questLogger = QuestServices.inst().getQuestLogger(uid, now);
        TakeSnapshotQuestRecord qRecord = new TakeSnapshotQuestRecord();
        questLogger.log(qRecord);
        
        Map<String, Object> resp = new HashMap(1);
        resp.put(PGMacro.TAKEN_SNAPSHOT, isTakenSnapshot);
        return resp;
    }
    
    public Map<String, Object> setUIStateAction(String uid, Map<String, Number> tuts)
    {
        UIData tut = UIData.getEntity(uid);
        tut.setStates(tuts);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> saveSettingsAction(String uid, Map<String, Object> settings)
    {
        UserSettings userSettings = UserSettings.getEntity(uid);
        userSettings.setStates((Map) settings);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> getMailsAction(String uid, int offset, int length)
    {
        MailBox mailBox = MailBox.getMailBoxOf(uid);
        mailBox.resetUnreadMail();
        return MailServices.inst().buildMailBox(mailBox, offset, length);
    }
    
    public Map<String, Object> clearAllMailsAction(String uid)
    {
        MailBox mailBox = MailBox.getMailBoxOf(uid);
        MailServices.inst().clearAll(mailBox);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> getUserGiftsAction(String uid)
    {
        UserGifts userGifts = UserGifts.getGift(uid);
        return userGifts.buildAMF();
    }
    
    public Map<String, Object> receiveGiftAction(String uid, String giftID,
            long now)
    {
        UserGifts userGifts = UserGifts.getGift(uid);
        PGException.Assert(userGifts.contains(giftID), PGError.INVALID_GIFT,
                "Gift " + giftID + " not be " + uid + "'s gift");
        
        Gift gift = Gift.getGift(giftID);
        PGPrize giftPrize = PrizeFactory.getPrize(gift.getPrizeData());
        
        EntityContext context = EntityContext.getContext(uid);
        Map<String, Object> prizeDesc = giftPrize.award(context, now);
        
        context.saveToDB();
        userGifts.remove(giftID);
        
        return prizeDesc;
    }
    
    public Map<String, Object> destroyGiftAction(String uid, String giftID,
            long now)
    {
        UserGifts userGifts = UserGifts.getGift(uid);
        PGException.Assert(userGifts.contains(giftID), PGError.INVALID_GIFT,
                "Gift " + giftID + " not be " + uid + "'s gift");
        
        userGifts.remove(giftID);
        Gift.destroyGift(giftID);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> sendGiftAction(List<String> receivers,
            Map<String, Object> giftPrize, long now, int expired)
    {
        Gift gift  = GiftServices.inst()
                .sendGift(receivers, giftPrize, now, expired);
        
        Map<String, Object> resp = new HashMap();
        resp.put(PGMacro.GIFT_ID, gift.getGiftID());
        return resp;
    }
    
    public Map<String, Object> sendGiftToAllUsersAction(
            Map<String, Object> giftPrize, long now, int expired)
    {
        Collection<String> allUsers = UserServices.inst().getAllUsers();
        
        Gift gift = GiftServices.inst()
                .sendGift(allUsers, giftPrize, now, expired);
        
        Map<String, Object> resp = new HashMap();
        resp.put(PGMacro.GIFT_ID, gift.getGiftID());
        return resp;
    }
    
    public Map<String, Object> loginAwardAction(String uid, long now)
    {
        UserDailyData uDailyData = UserDailyData.getData(uid, now);
        PGException.Assert(
                !Boolean.parseBoolean(uDailyData.getData(PGMacro.RECEIVED_LOGIN_PRIZE)),
                PGError.RECEIVED_LOGIN_PRIZE, "You have received this prize");
        
        UserTempData uTempData = UserTempData.getTempData(uid);
        int nRepLoginDay = PGHelper.toInteger(
                uTempData.getData(PGMacro.REPEATED_LOGIN_DAY));
        
        // prizing
        Map<String, Object> prizeDesc = Collections.EMPTY_MAP;
        if (nRepLoginDay > 0)
        {
            PGPrize dailyLoginPrize = PrizeFactory.getPrize(PGConfig.inst()
                    .getPrizing().dailyLogin().prizeForDay(nRepLoginDay));
            
            EntityContext context = EntityContext.getContext(uid);
            prizeDesc = dailyLoginPrize.award(context, now);
            context.saveToDB();
        }
        
        uDailyData.setData(PGMacro.RECEIVED_LOGIN_PRIZE, true);
        
        Map<String, Object> resp = new HashMap(2);
        resp.put(PGMacro.REPEATED_LOGIN_DAY, nRepLoginDay);
        resp.put(PGMacro.PRIZE, prizeDesc);
        return resp;
    }
    
    public Map<String, Object> getAchievemensAction(String uid, long now)
    {
        EntityContext context = EntityContext.getContext(uid);
        return QuestServices.inst().buildAchievementsAMF(context, now);
    }
    
    public Map<String, Object> receiveAchievementPrizeAction(String uid,
            String achID, String medalID, long now)
    {
        Achievement achivement = Achievement.getAchievements(uid, achID);
        EntityContext context = EntityContext.getContext(uid);
        
        PGException.Assert(PGConfig.inst().getAchievements().get(achID).isEnable(),
                PGError.ACHIEVEMENT_DISABLED, "Achievement are disabled");
        PGException.Assert(!achivement.isReceivedPrize(medalID),
                PGError.RECEIVED_ACHIEVEMENT, "You have received this prize");
        PGException.Assert(achivement.getChecker(medalID).isAccept(context),
                PGError.INCOMPLETED_BEFORE_QUEST, "Incompleted achivement");
        
        Map<String, Object> prizeDesc = achivement.getPrize(medalID).award(context, now);
        context.saveToDB();
        
        achivement.setReceivedPrize(medalID);
        return prizeDesc;
    }
    
    public Map<String, Object> takeRandomizePrizeAction(String uid, long now)
    {
        UserTempData uTempData = UserTempData.getTempData(uid);
        
        int nTurn = PGHelper.toInteger(uTempData.getData(PGMacro.RAND_PRIZE_TURN));
        PGException.Assert(nTurn > 0, PGError.NOT_ENOUGH_RP_TURN, "You have 0 turn");
        
        // reduce turn
        --nTurn;
        uTempData.setData(PGMacro.RAND_PRIZE_TURN, nTurn);
        
        String prizeID = PGConfig.inst().getRandomizePrizes().randomPrize();
        
        CFRandomizePrize.Prize prizeData = PGConfig.inst().getRandomizePrizes().get(prizeID);
        if (prizeData.isAutoPrize())
        {
            PGPrize prize = PrizeFactory.getPrize(prizeData.getPrize());
            EntityContext context = EntityContext.getContext(uid);
            Map<String, Object> pzDesc = prize.award(context, now);
            context.saveToDB();
            
            // find total gold prized:
            Deque<Map<String, Object>> pzStack = new ArrayDeque();
            int totalGoldPrized = 0;
            pzStack.add(prizeData.getPrize());
            while (!pzStack.isEmpty())
            {
                Map<String, Object> pz = pzStack.pollLast();
                for (Map.Entry<String, Object> pzEntry : pz.entrySet()) {
                    String pzKey = pzEntry.getKey();
                    Object pzVal = pzEntry.getValue();
                    
                    if (pzVal instanceof Map)
                    {
                        pzStack.addLast((Map) pzVal);
                    }
                    else if ("gold".equals(pzKey))
                    {
                        totalGoldPrized += PGHelper.toInteger(pzVal);
                    }
                }
            }
            
            if (totalGoldPrized > 0)
            {
                QuestLogger qLogger = QuestServices.inst().getQuestLogger(uid, now);
                qLogger.log(new GoldDialRecord(totalGoldPrized));
            }
            
            return AMFBuilder.make(PGMacro.RAND_PRIZE_ID, prizeID,
                    PGMacro.PRIZE, pzDesc);
        }
        else
        {
            String giftID = GiftServices.inst().sendGift(Arrays.asList(new String[] {uid}),
                    prizeData.getPrize(), now, PGConfig.inst().temp().RandomizePrize_Expire()).getGiftID();
            
            return AMFBuilder.make(PGMacro.RAND_PRIZE_ID, prizeID, PGMacro.GIFT_ID, giftID);
        }
    }
    
    public Map<String, Object> buyRandomizePrizeTurnAction(String uid, String itemID, long now)
    {
        CFRandomizePrize.Items itemsConf = PGConfig.inst().getRandomizePrizes().getItems();
        CFRandomizePrize.Items.Item item = itemsConf.get(itemID);
        PGException.Assert(item != null, 
                PGError.INVALID_ITEM, "Invalid item");
        
        User user = User.getUser(uid);
        UserTempData uTempData = UserTempData.getTempData(uid);
        if (PGMacro.GOLD.equals(item.getPaymentType()))
        {
            UserDailyData uDaily = UserDailyData.getData(uid, now);
            int nTurnHaveBought = PGHelper.toInteger(PGMacro.RP_TURN_BOUGHT_BY_GOLD);
            
            PGException.Assert(nTurnHaveBought < PGConfig.inst().temp()
                    .MaxBuyRPByGoldPerDay(), PGError.CANNOT_BUY_RP_TURN,
                    "You have bought maximum turn by gold in day");
            
            PGException.Assert(user.getGold() >= item.getPrice(),
                    PGError.NOT_ENOUGH_GOLD, "Not enough gold");
            
            QuestLogger uQuestLogger = QuestServices.inst().getQuestLogger(uid, now);
            UserServices.inst().decreaseGold(user, uQuestLogger, item.getPrice());
            uDaily.increaseData(itemID, item.getnTurn());
        }
        else if (PGMacro.COIN.equals(item.getPaymentType()))
        {
            PGException.Assert(user.getCoin() >= item.getPrice(),
                    PGError.NOT_ENOUGH_COIN, "Not enough coin");
            
            UserServices.inst().decreaseCoin(user, item.getPrice());
        }
        else
        {
            PGException.Assert(false, PGError.INVALID_ITEM,
                    "Invalid payment " + item.getPaymentType());
        }
        
        int nTurn = PGHelper.toInteger(uTempData.getData(PGMacro.RAND_PRIZE_TURN));
        nTurn += item.getnTurn();
        uTempData.setData(PGMacro.RAND_PRIZE_TURN, nTurn);
        
        user.saveToDB();
        
        return AMFBuilder.make(PGMacro.RAND_PRIZE_TURN, nTurn);
    }
    
    public Map<String, Object> reloadFriendListAction(String uid,
            String signedRequest, long now)
            throws ZingMeApiException, IOException
    {
        UserTempData uTempData = UserTempData.getTempData(uid);
        long lastSync = PGHelper.toLong(
                uTempData.getData(PGMacro.LAST_TIME_SYNC_FRIEND_LIST));
        
        PGException.Assert(lastSync + PGConfig.inst().temp().SyncFriendsCooldown() <
                now, PGError.SYNC_FRIEND_ARE_LOCKED,
                "Sync friend are locked (unlock - " +
                lastSync + PGConfig.inst().temp().SyncFriendsCooldown() + ")");
        
        SNServices sns = new SNServices(signedRequest);
        PGException.Assert(sns.validUser(uid),
                PGError.INVALID_SIGNED_REQUEST, "Signed request are invalid");
        FriendList friendList = FriendList.getFriendList(uid);
        
        FriendServices.inst().reloadFriendList(friendList, sns, uTempData, now);
        
        return friendList.buildAMF();
    }
    
    public Map<String, Object> wakeDogFirstTimeAction(String uid, String coteID,
            long now)
    {
        UserTempData uTempData = UserTempData.getTempData(uid);
        boolean isWakenDogFirstTime = Boolean.parseBoolean(
                uTempData.getData(PGMacro.WAKE_DOG_FIRST_TIME));
        PGException.Assert(!isWakenDogFirstTime, PGError.HAVE_WAKEN_DOG_FIRST_TIME,
                "You have waken dog first time");
        
        Dog dog = Dog.getDog(uid, coteID);
        PGException.Assert(!dog.isAwake(now), PGError.HAVE_WAKEN_DOG_FIRST_TIME,
                "The dog is awaken right now");
        
        final long nextSleepTime = Math.max(now, dog.getNextSleep())
                + PGConfig.inst().temp().WakeDogFirstTime_Time();
        PGException.Assert(nextSleepTime <= now + PGConfig.inst().temp().MaxDogTime(), 
                PGError.DOG_MAX_AWAKE_TIME, "Dog max awake time");
        
        dog.setNextSleep(nextSleepTime);
        uTempData.setData(PGMacro.WAKE_DOG_FIRST_TIME, true);
        dog.saveToDB();
        
        Map<String, Object> resp = new HashMap(1);
        resp.put(PGMacro.NEXT_SLEEP, nextSleepTime);
        return resp;
    }
    
    public Map<String, Object> takeAdsAction(String uid, long now)
    {
        UserDailyData uDailyData = UserDailyData.getData(uid, now);
        boolean isTakenAds = Boolean.parseBoolean(
                uDailyData.getData(PGMacro.TAKEN_ADS));
        PGException.Assert(!isTakenAds, PGError.HAVE_TAKEN_ADS_TODAY,
                "You have taken ads today");
        
        uDailyData.setData(PGMacro.TAKEN_ADS, true);
            
        EntityContext context = EntityContext.getContext(uid);

        Map<String, Object> prizeData = PGConfig.inst()
                .getPrizing().getAds().getPrize(context.getUser().getLevel());
        PGPrize prize = PrizeFactory.getPrize(prizeData);
        Map<String, Object> prizeDesc = prize.award(context, now);
        context.saveToDB();

        return prizeDesc;
    }
    
    public Map<String, Object> makeGiftCodes(Map<String, Object> giftData,
            int giftExpire, int nCode, int codeExpire, long now)
    {
        final String templID = PGKeys.randomKey();
        GiftTemplate.newTemplate(templID, giftData, giftExpire, codeExpire);
        
        List<String> codes = new ArrayList(nCode);
        for (int i = 0; i < nCode; i++) {
            do
            {
                String code = PGKeys.randomCode();
                if (!GiftCode.isExist(code))
                {
                    GiftCode.newGift(code, templID, 1, codeExpire);
                    codes.add(code);
                    break;
                }
            } while(true);
        }
        
        return AMFBuilder.toAMF(codes);
    }
    
    public Map<String, Object> useGiftCode(String uid, String code, long now)
    {
        try
        {
            GiftCode giftCode = GiftCode.getGift(code);
            if (giftCode.getRemain() > 0)
            {
                GiftTemplate giftTemplate = GiftTemplate
                        .getTemplate(giftCode.getGiftTemplateID());

                List<String> receivers = new ArrayList(1);
                receivers.add(uid);

                Map<String, Object> giftPrize = (Map)
                        JSONValue.parse(giftTemplate.getGiftData());

                Gift gift = GiftServices.inst()
                        .sendGift(receivers, giftPrize, now, giftTemplate.getGiftExpire());

                giftCode.setRemain(giftCode.getRemain() - 1);
                giftCode.saveToDB();

                return gift.buildAMF();
            }
        }
        catch (PGException ex)
        {
        }
        
        Map<String, Object> resp = new HashMap(1);
        resp.put(PGMacro.ERROR_CODE, PGError.INVALID_GIFT_CODE);
        return resp;
    } 
    
    public Map<String, Object> getPaymentAction(String uid, String billNo)
    {
        Payment payment = Payment.getPayment(billNo);
        PGException.Assert((uid != null) && uid.equals(payment.getUid()),
                PGError.BILL_NO_NOT_BELONG_TO_USER,
                "Billno " + billNo + " not belong to user " + uid);
        
        return payment.buildAMF();
    }
    
    public Map<String, Object> addWhiteListAction(List<String> whiteList)
    {
        String[] whiteArr = whiteList.toArray(new String[whiteList.size()]);
        UserList.getList(UserList.ListType.WHITE_LIST).add(whiteArr);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> addSystemListAction(List<String> sysList)
    {
        String[] sysArr = sysList.toArray(new String[sysList.size()]);
        UserList.getList(UserList.ListType.SYSTEM_ACCOUNT).add(sysArr);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> deleteUserAction(String uid, String adminPassword)
    {
        String md5Pass = DigestUtils.md5Hex(adminPassword);
        PGException.Assert(PGConfig.inst().temp().SystemPasswordMD5().equals(md5Pass),
                PGError.INVALID_SIGNED_REQUEST, "Invalid password");
        
        try
        {
            UserServices.inst().destroyUser(uid);
        }
        catch (Exception ex)
        {
            PGException.pgThrow(ex);
        }
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> getGameMessagesAction(long now)
    {
        return GameMessageList.getMessages().buildAMF(true);
    }
    
    public Map<String, Object> getAllGameMessagesAction(long now)
    {
        return GameMessageList.getMessages().buildAMF(false);
    }
    
    public Map<String, Object> addGameMessagesAction(List<Map<String, Object>> data, long now)
    {
        String[] msgIDs = new String[data.size()];
        int i = 0;
        for (Map<String, Object> msgData : data) {
            String content = (String) msgData.get("content");
            int order = PGHelper.toInteger(msgData.get("order"));
            int expire = PGHelper.toInteger(msgData.get("expire"));
            
            String msgID = PGKeys.randomKey();
            GameMessage.newMsg(msgID, content, order, expire);
            msgIDs[i++] = msgID;
        }
        
        GameMessageList.getMessages().add(msgIDs);
        
        return Collections.EMPTY_MAP;
    }
    
    public Map<String, Object> disableGameMessagesAction(List<String> msgIDs, long now)
    {
        for (String msgID : msgIDs) {
            if (GameMessage.isExist(msgID))
            {
                GameMessage gm = GameMessage.getMsg(msgID);
                gm.setEnable(false);
                gm.saveToDB();
            }
        }
        
        return Collections.EMPTY_MAP;
    }
}