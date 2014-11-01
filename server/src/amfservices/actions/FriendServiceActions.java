/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.actions;

import com.google.common.collect.Maps;
import config.PGConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import share.PGLog;
import pgentity.Cote;
import pgentity.CoteList;
import pgentity.EggStore;
import pgentity.EntityContext;
import pgentity.FriendList;
import pgentity.HelpFriendFish;
import pgentity.Inventory;
import pgentity.Notification;
import pgentity.StealFriendEgg;
import pgentity.User;
import pgentity.quest.HelpFriendRecord;
import pgentity.quest.QuestLogger;
import pgentity.quest.StealEggRecord;
import pgentity.quest.StolenEggsRecord;
import pgentity.quest.VisitFriendRecord;
import pgentity.services.CoteServices;
import pgentity.services.EggStoreServices;
import pgentity.services.FriendServices;
import pgentity.services.MailServices;
import pgentity.services.QuestServices;
import pgentity.services.UserServices;
import share.PGError;
import share.PGMacro;
import share.AMFBuilder;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class FriendServiceActions
{
    private FriendServiceActions()
    {
        super();
    }
    
    private static final FriendServiceActions inst = new FriendServiceActions();
    
    public static FriendServiceActions inst()
    {
        return inst;
    }
    
    public Map<String, Object> visitFriendAction(String uid, String fid, long now) throws PGException
    {
        Map<String, Object> response = Maps.newHashMap();
        
        PGException.Assert(User.isExist(fid), PGError.INVALID_FRIEND,
                "Friend " + fid + " doesn't existed");
        
        FriendList userFriends = FriendList.getFriendList(uid);
        PGException.Assert(userFriends.contains(fid),
                PGError.FRIEND_NOT_FRIEND_WITH_USER, fid + " aren't " + uid + " friend");
        
        int npcIndex = FriendServices.inst().npcIndex(uid, fid);
        EntityContext friendContext = EntityContext.getContext(fid);
        
        if (npcIndex < 0) // isn't NPC
        {
            QuestLogger questLogger = QuestServices.inst().getQuestLogger(fid, now);
            CoteServices.inst().updateCote(friendContext, questLogger, now);

            friendContext.saveToDB();
        }
        else
        {
            FriendServices.inst().setNPCData(friendContext.getUser(), npcIndex, now);
        }
        
        QuestLogger userQLogger = QuestServices.inst().getQuestLogger(uid, now);
        userQLogger.log(new VisitFriendRecord());
        
        HelpFriendFish helpFriendFish = HelpFriendFish.getEntity(uid, now);
        StealFriendEgg stealFriendEgg = StealFriendEgg.getEntity(uid, now);
        Map<String, Object> playWithFriendAMF = new HashMap();
        playWithFriendAMF.put(PGMacro.HELPED_FISH, helpFriendFish.fishHelped(fid));
        playWithFriendAMF.put(PGMacro.STOLEN_EGG, stealFriendEgg.eggStolen(fid));

        response.put(PGMacro.FRIEND, friendContext.getUser()
                .buildFullAMF(true, false, false, false, now));
        response.put(PGMacro.PLAY_WITH_FRIEND, playWithFriendAMF);

        return response;
    }
    
    public Map<String, Object> helpFriendFishAction(String uid, String fid, long now) throws PGException
    {
        HelpFriendFish helpFriendFish = HelpFriendFish.getEntity(uid, now);
        
        PGException.Assert(helpFriendFish.fishHelped(fid) <= 0,
                PGError.HAVE_HELPED_FRIEND_TODAY, "You have helped your friend " + fid);
        
        int newFriendHelp = helpFriendFish.numberFriendHelped() + 1;
        int maxFriendHelpAllowed = PGConfig.inst().getFriend().getHelpFish().getMaxFriend();
        PGException.Assert(newFriendHelp <= maxFriendHelpAllowed,
                PGError.OVER_FRIEND_HELPED_TODAY, "Max friend helped to day");
        
        int nFishToHelp = PGConfig.inst().getFriend().getHelpFish().getPerFriend();
        
        EntityContext friendContext = EntityContext.getContext(fid);
        QuestLogger friendQLogger = QuestServices.inst().getQuestLogger(fid, now);
        
        Map<String, Integer> atePenguin;
        try
        {
            atePenguin = CoteServices.inst().dropFish(friendContext,
                    friendQLogger, nFishToHelp, now);
        }
        catch (PGException ex)
        {
            if (ex.getErrorCode() == PGError.TOO_MUCH_FISH)
            {
                throw new PGException(PGError.TOO_MUCH_FRIEND_FISH, ex.getMessage());
            }
            throw ex;
        }
        
        EntityContext userContext = EntityContext.getContext(uid);
        UserServices.inst().increaseUserExp(userContext,
                (int) (nFishToHelp * PGConfig.inst().temp().PenguinExpPerFish()), now);
        helpFriendFish.helpFish(fid, nFishToHelp);
        MailServices.inst().sendHelpFishMail(userContext.getUser(),
                fid, nFishToHelp, now);
        
        QuestLogger userQLogger = QuestServices.inst().getQuestLogger(uid, now);
        userQLogger.log(new HelpFriendRecord());
        
        friendContext.saveToDB();
        userContext.saveToDB();
        
        return AMFBuilder.toAMF(atePenguin);
    }
    
    public Map<String, Object> stealEggAction(String uid, String fid, String eggKind, long now) throws PGException
    {
        PGLog.info("%s steal egg from %s", uid, fid);
        StealFriendEgg stealFriendEgg = StealFriendEgg.getEntity(uid, now);
        
        boolean isStealNewFriend = stealFriendEgg.eggStolen(fid) <= 0;
        int newFriendStolen = stealFriendEgg.numberFriendStolen() + (isStealNewFriend?1:0);
        int maxFriendAllowed = PGConfig.inst().getFriend().getStealEgg().getMaxFriend();
        PGException.Assert(newFriendStolen <= maxFriendAllowed,
                PGError.OVER_FRIEND_STEAL_EGG_TODAY, "Over steal friend ("
                + newFriendStolen + ") allowed " + maxFriendAllowed);
        
        final int nEggStolen = 1;
        int newStolenEgg = stealFriendEgg.eggStolen(fid) + nEggStolen;
        int maxStolenEggPerDay = PGConfig.inst().getFriend().getStealEgg().getPerFriend();
        PGException.Assert(newStolenEgg <= maxStolenEggPerDay,
                PGError.OVER_STOLEN_EGG_TODAY, "Over stolen egg ("
                + newStolenEgg + ") allowed " + maxStolenEggPerDay);
        
        Inventory inventory = Inventory.getInventory(uid);
        PGException.Assert(inventory.numberItems() + nEggStolen < PGConfig.inst().temp().MaxInventory(),
                PGError.NOT_ENOUGH_INVENTORY_SLOT,
                String.format("Max slot %d; current %d; Added %d",
                        PGConfig.inst().temp().MaxInventory(), inventory.numberItems(), nEggStolen));
        
        Map<String, Number> stealEggs = new HashMap();
        stealEggs.put(eggKind, nEggStolen);
        
        CoteList friendCotes = CoteList.getCotes(fid);
        String defaultFrCoteID = friendCotes.at(0);
        EggStore frCoteEggs = Cote.getCote(fid, defaultFrCoteID).eggStore();
        
        stealEggs = EggStoreServices.inst().validateEgg(frCoteEggs, stealEggs);
        
        if (stealEggs.get(eggKind).intValue() > 0)
        {
            EggStoreServices.inst().moveEgg(frCoteEggs, inventory.eggStore(), stealEggs);
            stealFriendEgg.stealEgg(fid, nEggStolen);
            MailServices.inst().sendStealEggMail(User.getUser(uid), fid, eggKind, now);
            Notification.getNotif(uid).send("notification",
                    "Bạn vừa trộm của bạn mình một trứng");
            
            StealEggRecord stealEggRecord = new StealEggRecord(nEggStolen);
            QuestLogger qLogger = QuestServices.inst().getQuestLogger(uid, now);
            qLogger.log(stealEggRecord);
            
            StolenEggsRecord stolenEggsRecord = new StolenEggsRecord(eggKind, nEggStolen);
            QuestLogger frLogger = QuestServices.inst().getQuestLogger(fid, now);
            frLogger.log(stolenEggsRecord);
        }
        
        return AMFBuilder.toAMF(stealEggs);
    }
    
    public Map<String, Object> getFriendInfoDetailAction(String uid,
            String fid, boolean getCote, boolean getFrs, boolean getInv,
            long now)
    {
        PGException.Assert(FriendServices.inst().isFriend(uid, fid),
                PGError.USER_NOT_FRIEND_WITH_FRIEND,
                "They're not friend");
        
        return ReloadServices.inst()
                .getUserFullInfo(uid, getCote, getFrs, getInv, now);
    }
    
    public Map<String, Object> friendPenguinWannaEatAction(String uid,
            String fid, String coteID,
            List<String> penguinIDs, long now)
    {
        PGException.Assert(FriendServices.inst().isFriend(uid, fid),
                PGError.USER_NOT_FRIEND_WITH_FRIEND,
                "They're not friend");
        
        return PGServicesAction.inst()
                .penguinWannaEatAction(fid, coteID, penguinIDs, now);
    }
    
    public Map<String, Object> friendPenguinSpawnEggAction(String uid,
            String fid, String coteID,
            List<String> penguinIDs, long now)
    {
        PGException.Assert(FriendServices.inst().isFriend(uid, fid),
                PGError.USER_NOT_FRIEND_WITH_FRIEND,
                "They're not friend");
        
        return PGServicesAction.inst()
                .spawnEggAction(fid, coteID, penguinIDs, now);
    }
    
    public Map<String, Object> friendPenguinSpawnEggAction(String uid,
            String fid, long now)
    {
        PGException.Assert(FriendServices.inst().isFriend(uid, fid),
                PGError.USER_NOT_FRIEND_WITH_FRIEND,
                "They're not friend");
        
        return PGServicesAction.inst()
                .getAchievemensAction(fid, now);
    }
    
    public Map<String, Object> visitFriendCote(String uid, String fid,
            String coteID, long now)
    {
        PGException.Assert(FriendServices.inst().isFriend(uid, fid),
                PGError.USER_NOT_FRIEND_WITH_FRIEND,
                "They're not friend");
        
        return PGServicesAction.inst().visitCote(fid, coteID, now);
    }
}