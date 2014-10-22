/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services.restores;

import config.CFAchievements;
import config.PGConfig;
import db.DBContext;
import db.PGKeys;
import db.RedisKey;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;
import pgentity.Achievement;
import pgentity.CoteList;
import pgentity.EggStore;
import pgentity.FriendList;
import pgentity.Inventory;
import pgentity.MainQuestLine;
import pgentity.NPCList;
import pgentity.Penguindex;
import pgentity.UIData;
import pgentity.User;
import pgentity.UserGifts;
import pgentity.UserList;
import pgentity.UserSettings;
import pgentity.UserTempData;
import pgentity.pool.EntityPool;
import pgentity.services.CoteServices;
import pgentity.services.UserServices;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */

class V0_Restorer implements Restorer
{
    private V0_Restorer()
    {
        super();
    }
    
    private static final V0_Restorer inst = new V0_Restorer();
    
    public static V0_Restorer inst()
    {
        return inst;
    }
    
    @Override
    public void restore(String uid, JSONObject data, long now) {
    // destroy user before restore
        UserServices.inst().destroyUser(uid);

        restoreUser(uid, (Map) data.get(PGMacro.USER));
        restoreNPCs(User.getUser(uid), ((Number) data.get(PGMacro.NPC_LIST)).intValue(), now);
        restoreFriendList(uid, (List) data.get(PGMacro.FRIEND_LIST));
        restoreMainQuest(uid, (Map) data.get(PGMacro.MAIN_QUEST));
        restoreAchievements(uid, (Map) data.get(PGMacro.ACHIEVEMENTS));
        restoreTemp(uid, (Map) data.get(PGMacro.USER_TEMP_DATA));
        restoreSettings(uid, (Map) data.get(PGMacro.SETTINGS));
        restorePenguindex(uid, (List) data.get(PGMacro.PENGUINDEX));
        restoreUI(uid, (Map) data.get(PGMacro.UIDATA));
        restoreInventory(uid, (Map) data.get(PGMacro.INVENTORY));
        restoreGifts(uid, (List) data.get(PGMacro.GIFTS));

        restoreCoteList(uid, (List) data.get(PGMacro.COTE));
    }
    
    public static void restoreUser(String uid, Map<String, String> data)
    {
        EntityPool.inst().remove(User.class, uid);
        DBContext.Redis().hset(User.redisKey(uid), data);
        UserList.getList(UserList.ListType.ALL_USER).add(uid);
    }
    
    public static void restoreNPCs(User user, int nNPC, long now)
    {
        if (nNPC > 0)
        {
            UserServices.inst().initNPCs(user, now);
        }
    }
    
    public static void restoreFriendList(String uid, List<String> friendIDs)
    {
        FriendList.destroy(uid);
        String[] arrFIDs = friendIDs.toArray(new String[friendIDs.size()]);
        FriendList.getFriendList(uid).add(arrFIDs);
        
        List<String> npcIDs = NPCList.getNPCList(uid).getAll();
        String[] npcIDArr = npcIDs.toArray(new String[npcIDs.size()]);
        FriendList.getFriendList(uid).add(npcIDArr);
    }
    
    public static void restoreMainQuest(String uid, Map<String, Object> data)
    {
        Iterable<String> questLines = PGConfig.inst().getMainQuest();
        for (String qLine : questLines)
        {
            restoreQuestLine(uid, qLine, (Map) data.get(qLine));
        }
    }
    
    public static void restoreQuestLine(String uid, String qLine, Map<String, Object> data)
    {
        EntityPool.inst().remove(MainQuestLine.class, uid, qLine);
        
        DBContext.Redis().hset(MainQuestLine.redisKey(uid, qLine), (Map) data.get("data"));
        MainQuestLine questLine = MainQuestLine.getQuestLine(uid, qLine);
        questLine.getLogger().restore(data);
    }
    
    public static void restoreAchievements(String uid, Map<String, Object> data)
    {
        CFAchievements achConfig = PGConfig.inst().getAchievements();
        for (String achID : achConfig) {
            restoreAch(uid, achID, (Map) data.get(achID));
        }
    }
    public static void restoreAch(String uid, String achID,
            Map<String, Object> data)
    {
        EntityPool.inst().remove(Achievement.class, uid, achID);
        DBContext.Redis().hset(Achievement.redisKey(uid, achID), (Map) data.get("data"));
        
        Achievement ach = Achievement.getAchievements(uid, achID);
        ach.getLogger().restore((Map) data.get("log"));
    }
    
    public static void restoreTemp(String uid, Map<String, String> data)
    {
        EntityPool.inst().remove(UserTempData.class, uid);
        DBContext.Redis().hset(UserTempData.redisKey(uid), data);
    }
    
    public static void restoreSettings(String uid, Map<String, String> data)
    {
        EntityPool.inst().remove(UserSettings.class, uid);
        DBContext.Redis().hset(UserSettings.redisKey(uid), data);
    }
    
    public static void restorePenguindex(String uid, List<String> data)
    {
        RedisKey redisKey = Penguindex.redisKey(uid);
        DBContext.Redis().del(redisKey);
        String[] arrData = data.toArray(new String[data.size()]);
        DBContext.Redis().sadd(redisKey, arrData);
    }
    
    public static void restoreUI(String uid, Map<String, String> data)
    {
        EntityPool.inst().remove(UIData.class, uid);
        DBContext.Redis().hset(UIData.redisKey(uid), data);
    }
    
    public static void restoreInventory(String uid, Map<String, Object> data)
    {
        RedisKey eggStoreKey = Inventory.redisKey(uid).getChild(PGKeys.FD_EGGS);
        restoreEggStore(eggStoreKey, (Map) data.get(PGMacro.EGGS));
    }
    
    public static void restoreEggStore(RedisKey key, Map<String, String> data)
    {
        EntityPool.inst().remove(EggStore.class, key);
        DBContext.Redis().hset(key, data);
    }
    
    public static void restoreGifts(String uid, List<String> data)
    {
        RedisKey redisKey = UserGifts.redisKey(uid);
        String[] arrGIDs = data.toArray(new String[data.size()]);
        DBContext.Redis().del(redisKey);
        DBContext.Redis().sadd(redisKey, arrGIDs);
    }
    
    public static void restoreCoteList(String uid, List<Map<String, Object>> coteData)
    {
        DBContext.Redis().del(CoteList.redisKey(uid));
        CoteList coteList = CoteList.getCotes(uid);
        String[] coteIDs = new String[coteData.size()];
        for (int i = 0; i < coteData.size(); i++) {
            coteIDs[i] = PGKeys.randomKey();
            coteList.append(coteIDs[i]);

            CoteServices.inst().restoreCote(uid, coteIDs[i], coteData.get(i));
        }
    }
}
