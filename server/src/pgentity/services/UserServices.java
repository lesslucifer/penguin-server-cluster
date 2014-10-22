/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import config.CFInit;
import config.CFUser;
import config.PGConfig;
import db.DBContext;
import db.PGKeys;
import db.PGRedis;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import libCore.BackgroundServices;
import libCore.SNServices;
import libCore.config.Config;
import logging.Logging;
import logging.report.PGLogCategory;
import logging.report.PGRecord;
import pgentity.CoteList;
import pgentity.EntityContext;
import pgentity.FriendList;
import pgentity.Inventory;
import pgentity.MainQuestLine;
import pgentity.NPCList;
import pgentity.Penguindex;
import pgentity.UIData;
import pgentity.User;
import pgentity.UserDailyData;
import pgentity.UserGifts;
import pgentity.UserList;
import pgentity.UserSettings;
import pgentity.UserTempData;
import pgentity.prize.PGPrize;
import pgentity.prize.PrizeFactory;
import pgentity.quest.ExpenseGoldRecord;
import pgentity.quest.LoginDayRecord;
import pgentity.quest.QuestLogger;
import share.*;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author KieuAnh
 */
public class UserServices
{
    private UserServices()
    {
        super();
    }
    
    private static final UserServices inst = new UserServices();
    
    public static UserServices inst()
    {
        return inst;
    }
    
    public User createNewUser(String uid, String signedReq, long createdTime)
            throws PGException, IOException, ZingMeApiException
    {
        if (DBContext.Redis().sadd(PGKeys.ALL_USERS, uid) != PGRedis.ERROR)
        {
            SNServices sns = new SNServices(signedReq);
            PGException.Assert(sns.validUser(uid), PGError.INVALID_SIGNED_REQUEST,
                    "Signed request are invalid");
            SNServices.ZMUserInfo userInfo = sns.getUserInfo();
            
            String name    =   userInfo.getDisplayName();
            String avatar   =   userInfo.getAvatar();
        
            @SuppressWarnings("deprecated")
            User user = User.newUser(uid, name, avatar, createdTime);      
            UserTempData uTempData = UserTempData.getTempData(uid);
            FriendServices.inst().reloadFriendList(user.friendList(),
                    sns, uTempData, createdTime);
            
            Penguindex penguindex = Penguindex.getPenguindex(uid);
            CFUser.Default coteConfs = PGConfig.inst()
                    .getUser().getDefaultUser();
            this.initCote(user.cotes(), coteConfs, penguindex, createdTime);
            
            this.initQuest(user, createdTime);
            this.initNPCs(user, createdTime);
            
            user.saveToDB();
            
            return user;
        }
        
        return null;
    }
    
    private void initCote(CoteList coteList, Map<Integer, ? extends CFInit.Cote> coteConfs,
            Penguindex penguindex, long createdTime) throws PGException
    {
        String[] coteIDs = new String[coteConfs.size()];
        for (Integer index : coteConfs.keySet()) {
            CFInit.Cote coteConfig = coteConfs.get(index);
            
            final String coteID = PGKeys.randomKey();
            coteIDs[index] = coteID;
            
            CoteServices.inst().createCote(coteList.getUid(), coteID, coteConfig, penguindex, createdTime);
        }
        
        for (String coteID : coteIDs) {
            coteList.append(coteID);
        }
    }
    
    private void initQuest(User user, long createdTime)
    {
        Iterable<String> qLines = PGConfig.inst().getMainQuest().keySet();
        for (String qLine : qLines) {
            MainQuestLine.newQuestLine(user.getUid(), qLine);
        }
        
        QuestServices.inst().newDailyQuest(user, createdTime);
    }
    
    public void initNPCs(User user, long now)
    {
        CFUser.NPCs conf = PGConfig.inst().getUser().npc();
        NPCList npcList = NPCList.getNPCList(user.getUid());
        List<String> npcIDs = new ArrayList(conf.size());
        
        for (Integer npcIdx : conf.keySet()) {
            String npcID = PGKeys.randomKey();
            String name = conf.get(npcIdx).getName();
            String avatar = conf.get(npcIdx).getAvatar();
            
            UserList.getList(UserList.ListType.ALL_USER).add(npcID);
            UserList.getList(UserList.ListType.NPC).add(npcID);
            User npc = User.newUser(npcID, name, avatar, now);
            Penguindex npcPenguindex = Penguindex.getPenguindex(npcID);
            
            this.initCote(npc.cotes(), conf.get(npcIdx), npcPenguindex, now);
            this.initQuest(npc, now);
            
            FriendServices.inst().setNPCData(npc, npcIdx, now);
            
            npc.saveToDB();
            
            npc.friendList().add(user.getUid());
            npcList.append(npcID);
            npcIDs.add(npcID);
        }
        
        user.friendList().add(npcIDs.toArray(new String[npcIDs.size()]));
    }
    
    public Collection<String> getAllUsers()
    {
        return DBContext.Redis().smembers(PGKeys.ALL_USERS);
    }
    
    public void increaseUserExp(EntityContext context, int incExp, long now)
    {
        int oldUserLevel = context.getUser().getLevel();
        context.getUser().increaseExp(incExp);
        this.updateLevel(context.getUser());
        
        for (int level = oldUserLevel + 1; level <= context.getUser().getLevel();
                ++level)
        {
            // prize on level up
            PGPrize prize = PrizeFactory.getPrize(PGConfig.inst()
                    .getUser().get(level).getLevelUpPrize());

            prize.award(context, now);
            
            // check main quest
            QuestServices.inst().autoAcceptMainQuest(context.getUser());
        }
    }
    
    private void updateLevel(User user)
    {
        int newLevel = PGConfig.inst().getUser().levelByExp(user.getExp());
        
        if (newLevel != user.getLevel())
        {
            user.setLevel(newLevel);
        }
    }
    
    public void increaseGold(User user, int incGold)
    {
        PGException.Assert(incGold >= 0, PGError.INCREASE_NEGATIVE_GOLD,
                "Increment gold must be positive {" + incGold + "}");
        
        user.increaseGold(incGold);
    }
    
    public void decreaseGold(User user, QuestLogger userQLogger, int decGold)
    {
        PGException.Assert(decGold >= 0, PGError.DECREASE_NEGATIVE_GOLD,
                "Decrement gold must be positive {" + decGold + "}");
        
        if (decGold > 0)
        {
            user.decreaseGold(decGold);
            userQLogger.log(new ExpenseGoldRecord(decGold));
            Logging.log(new PGRecord(PGLogCategory.EXPENSE_GOLD,
                    user.getUid(), System.currentTimeMillis(),
                    0, String.valueOf(decGold)));
        }
    }
    
    public void increaseCoin(String uid, int incCoin)
            throws PGException
    {
        PGException.Assert(incCoin >= 0, PGError.INCREASE_NEGATIVE_COIN,
                "Increasement coin must be positive {" + incCoin + "}");
        
        if (incCoin > 0)
        {
            User user = User.getUser(uid);
            user.changeCoin(incCoin);
            user.saveToDB();
        }
    }
    
    public void increaseCoin(User user, int incCoin)
            throws PGException
    {
        PGException.Assert(incCoin >= 0, PGError.INCREASE_NEGATIVE_COIN,
                "Increasement coin must be positive {" + incCoin + "}");
        
        if (incCoin > 0)
        {
            user.changeCoin(incCoin);
        }
    }
    
    public void decreaseCoin(User user, int decCoin)
    {
        PGException.Assert(decCoin >= 0, PGError.INCREASE_NEGATIVE_COIN,
                "Increasement coin must be positive {" + decCoin + "}");
        
        if (decCoin > 0)
        {
            user.changeCoin(-decCoin);
            Logging.log(new PGRecord(PGLogCategory.EXPENSE_COIN,
                    user.getUid(), System.currentTimeMillis(),
                    0, String.valueOf(decCoin)));
        }
    }
    
    public void setGold(User user, int gold)
    {
        user.increaseGold(user.getGold() - gold);
    }
    
    public void registerLogin(QuestLogger qLogger, String uid,
            String signedReq, long now)
    {
        qLogger.log(new LoginDayRecord(now));
        
        UserTempData uTempData = UserTempData.getTempData(uid);
        long lastLogin = PGHelper.toLong(
                uTempData.getData(PGMacro.LAST_LOGIN));
        uTempData.setData(PGMacro.LAST_LOGIN, now);
        
        int nDays = TimeUtil.diffDays(lastLogin, now);
        
        if (nDays >= 1) // pass new day
        {
            Logging.log(new PGRecord(PGLogCategory.A1LOGIN, uid, now, 0,
                    TimeUtil.formatDate(new Date(now))));
            
            loginPrizingUpdate(nDays, uTempData, now);
            newDayPrizing(nDays, uTempData, now);
            
            syncFriendUpdate(uTempData, signedReq, now);
            a1Backup(uid, now);
            tryRemoveNPCs(uid);
        }
    }
    
    private void loginPrizingUpdate(int nDays, UserTempData uTempData, long now)
    {
        // update repeated login
        int repLoginDay = PGHelper.toInteger(
                uTempData.getData(PGMacro.REPEATED_LOGIN_DAY));

        if (nDays == 1)
        {
            ++repLoginDay;
        }

        if (nDays > 1 || repLoginDay > 7)
        {
            repLoginDay = 1;
        }

        uTempData.setData(PGMacro.REPEATED_LOGIN_DAY, repLoginDay);

        UserDailyData uDailyData = UserDailyData.getData(uTempData.getUid(), now);
        uDailyData.setData(PGMacro.RECEIVED_LOGIN_PRIZE, false);
    }
    
    private void newDayPrizing(int nDays, UserTempData uTempData, long now)
    {
        if (nDays < 1)
        {
            return;
        }
        
        uTempData.incData(PGMacro.RAND_PRIZE_TURN, 1);
    }
    
    private void syncFriendUpdate(UserTempData uTempData, String signedReq, long now)
    {
        final String uid = uTempData.getUid();
        long lastSyncFriend = PGHelper.toLong(
                uTempData.getData(PGMacro.LAST_TIME_SYNC_FRIEND_LIST));
        
        int nDays = TimeUtil.diffDays(lastSyncFriend, now);
        if (nDays >= PGConfig.inst().temp().AutoSyncFriends_Day())
        {
            try
            {
                SNServices sns = new SNServices(signedReq);
                PGException.Assert(sns.validUser(uid),
                        PGError.INVALID_SIGNED_REQUEST, "Invalid signed_request");
                FriendList friendList = FriendList.getFriendList(uid);
                FriendServices.inst().reloadFriendList(friendList, sns, uTempData, now);
                
            } catch (ZingMeApiException ex) {
                Logger.getLogger(UserServices.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(UserServices.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PGException ex) {
                Logger.getLogger(UserServices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void a1Backup(final String uid, final long now)
    {
        Object userData = dumpUser(uid);
        final String userJSONData = PGHelper.obj2PrettyJSON(userData);
        BackgroundServices.inst().runBackground(new Runnable()
        {
            @Override
            public void run() {
                BufferedWriter out = null;
                try {
                    String dayToken = TimeUtil.dayToken(now);
                    String a1BackupDir = Config.getParam("a1backup", "directory");
                    File dayDir = new File(a1BackupDir + dayToken);
                    if (!dayDir.exists())
                    {
                        dayDir.mkdir();
                    }

                    String fileName = dayDir.getAbsolutePath() + '/'
                            + uid + ".bak";

                    File file = new File(fileName);
                    if (!file.exists())
                    {
                        file.createNewFile();
                    }
                    out = new BufferedWriter(new FileWriter(file, false));

                    out.write(userJSONData);

                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(UserServices.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private Object dumpUser(String uid)
    {
        Map<String, Object> dumpData = new HashMap();
        
        dumpData.put(PGMacro.USER, User.getUser(uid).dump());
        dumpData.put(PGMacro.NPC_LIST, dumpNPCs(uid));
        dumpData.put(PGMacro.FRIEND_LIST, FriendServices.inst().dumpFriendList(uid));
        dumpData.put(PGMacro.MAIN_QUEST, QuestServices.inst().dumpMainQuest(uid));
        dumpData.put(PGMacro.ACHIEVEMENTS, QuestServices.inst().dumpAchievements(uid));
        dumpData.put(PGMacro.USER_TEMP_DATA, UserTempData.getTempData(uid).dump());
        dumpData.put(PGMacro.SETTINGS, UserSettings.getEntity(uid).dump());
        dumpData.put(PGMacro.PENGUINDEX, Penguindex.getPenguindex(uid).dump());
        dumpData.put(PGMacro.UIDATA, UIData.getEntity(uid).dump());
        dumpData.put(PGMacro.INVENTORY, Inventory.getInventory(uid).dump());
        dumpData.put(PGMacro.GIFTS, UserGifts.getGift(uid).dump());
        
        CoteList coteList = CoteList.getCotes(uid);
        List<String> coteIDs = coteList.getAll();
        List<Object> cotes = new ArrayList(coteList.length());
        for (String coteID : coteIDs) {
            cotes.add(CoteServices.inst().dumpCote(uid, coteID));
        }
        dumpData.put(PGMacro.COTE, cotes);
        
        int dbVersion = User.getUser(uid).getDbVer();
        return AMFBuilder.make(PGMacro.DB_VERSION, dbVersion, "data", dumpData);
    }
    
    private Object dumpNPCs(String uid)
    {
        return NPCList.getNPCList(uid).length();
    }
    
    private void tryRemoveNPCs(String uid)
    {
        NPCList npcs = NPCList.getNPCList(uid);
        if (npcs.length() <= 0)
        {
            return;
        }
        
        User user = User.getUser(uid);
        if (user.getLevel() >= PGConfig.inst().temp().AutoRemoveNPC_Level() ||
            user.friendList().size() >= PGConfig.inst().temp().AutoRemoveNPC_Friends())
        {
            destroyNPCs(npcs);
        }
    }
    
    public void destroyNPCs(NPCList npcs)
    {
        String uid = npcs.getUid();
                
        List<String> allNPCs = npcs.getAll();
        String[] arrNPCs = allNPCs.toArray(new String[allNPCs.size()]);

        FriendList friends = FriendList.getFriendList(uid);
        friends.remove(arrNPCs);

        NPCList.destroyNPCList(uid);

        UserList npcList = UserList.getList(UserList.ListType.NPC);
        npcList.remove(arrNPCs);
        
        for (String npc : allNPCs) {
            destroyUser(npc);
        }
    }
    
    public void destroyUser(String uid)
    {
        FriendList.destroy(uid);
        QuestServices.inst().destroyMainQuest(uid);
        QuestServices.inst().destroyAchievements(uid);
        destroyNPCs(NPCList.getNPCList(uid));
        MailServices.inst().destroyMail(uid);
        UserTempData.destroy(uid);
        UserSettings.destroy(uid);
        Penguindex.destroy(uid);
        UIData.destroy(uid);
        Inventory.destroy(uid);
        GiftServices.inst().destroyGifts(uid);
        
        CoteList coteList = CoteList.getCotes(uid);
        List<String> coteIDs = coteList.getAll();
        for (String coteID : coteIDs) {
            CoteServices.inst().destroyCote(uid, coteID);
        }
        CoteList.destroy(uid);
        
        User.destroy(uid);
        UserList.getList(UserList.ListType.ALL_USER).remove(uid);
    }
}