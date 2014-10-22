/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import com.google.common.collect.Sets;
import config.CFUser;
import config.PGConfig;
import db.DBContext;
import db.PGKeys;
import db.RedisKey;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import libCore.SNServices;
import pgentity.BoxEgg;
import pgentity.Cote;
import pgentity.FriendList;
import pgentity.NPCList;
import pgentity.Penguin;
import pgentity.User;
import pgentity.UserTempData;
import share.PGMacro;
import zme.api.exception.ZingMeApiException;

/**
 *
 * @author KieuAnh
 */
public class FriendServices
{
    private FriendServices()
    {
        super();
    }
    
    private static final FriendServices inst = new FriendServices();
    
    public static FriendServices inst()
    {
        return inst;
    }
    
    public boolean isFriend(String uid, String fid)
    {
        FriendList userFriendList = FriendList.getFriendList(uid);
        return userFriendList.contains(fid);
    }
    
    public int npcIndex(String uid, String npcID)
    {
        NPCList npcList = NPCList.getNPCList(uid);
        List<String> npcIDs = npcList.getAll();
        return npcIDs.indexOf(npcID);
    }
    
    public void setNPCData(User npc, int idx, long now)
    {
        CFUser.NPCs.NPC conf = PGConfig.inst().getUser().npc().get(idx);
        
        npc.changeCoin(conf.getCoin() - npc.getCoin());
        npc.setFish(conf.getFish());
        npc.increaseGold(conf.getGold() - npc.getGold());
        int lvlExp = PGConfig.inst().getUser().get(conf.getLevel()).getExp();
        npc.increaseExp(lvlExp - npc.getExp());
        npc.setLevel(conf.getLevel());
        npc.saveToDB();
        
        for (Integer coteIdx : conf.keySet())
        {
            CFUser.NPCs.NPC.Cote coteConf = conf.get(coteIdx);
            
            Cote npcCote = Cote.getCote(npc.getUid(), npc.cotes().at(coteIdx));
            npcCote.setPoolFish(coteConf.getPoolFish());
            npcCote.setLevel(coteConf.getLevel());
            
            BoxEgg boxEgg = BoxEgg.getBoxEgg(npc.getUid(), npc.cotes().at(coteIdx));
            boxEgg.setLevel(coteConf.getBoxEggLevel());
            boxEgg.saveToDB();
            
            npcCote.eggStore().removeEggs(npcCote.eggStore().getEggs());
            npcCote.eggStore().addEggs(coteConf.getEggs());
            
            for (String penguinID : npcCote.penguins().getAll()) {
                Penguin penguin = Penguin.getPenguin(npcCote.getUid(),
                        npcCote.getCoteID(), penguinID);
                
                penguin.setLastEat(now);
                penguin.setLastSpawn(now);
                penguin.saveToDB();
            }
            
            npcCote.saveToDB();
        }
    }
    
    public void reloadFriendList(FriendList frList, SNServices sns,
            UserTempData uTempData, long now)
            throws ZingMeApiException, IOException
    {
        Set<String> zmFriendIDs = this.getPGFriends(sns);
        
        Set<String> npcs = new HashSet(NPCList.getNPCList(frList.getUid()).getAll());
        Set<String> curFriendIDs = Sets.difference(frList.getAll(), npcs);
        
        Set<String> unfriendIDs = Sets.difference(curFriendIDs, zmFriendIDs);
        if (unfriendIDs.size() > 0)
        {
            for (String unFid : unfriendIDs) {
                FriendList frFriendList = FriendList.getFriendList(unFid);
                frFriendList.remove(frList.getUid());
            }

            String[] unfriendIDArr = unfriendIDs.toArray(new String[unfriendIDs.size()]);
            frList.remove(unfriendIDArr);
        }
        
        Set<String> newFriendIDs = new HashSet(Sets.difference(zmFriendIDs, curFriendIDs));
        for (String fid : newFriendIDs) {
            FriendList frFriendList = FriendList.getFriendList(fid);
            frFriendList.add(frList.getUid());
        }
        
        String adminUID = PGConfig.inst().temp().Admin_UID();
        if (!adminUID.equals(uTempData.getUid()) && User.isExist(adminUID))
        {
            newFriendIDs.add(PGConfig.inst().temp().Admin_UID());
        }
        if (newFriendIDs.size() > 0)
        {
            String[] newFriendIDArr = newFriendIDs.toArray(new String[newFriendIDs.size()]);
            frList.add(newFriendIDArr);
        }
        
        uTempData.setData(PGMacro.LAST_TIME_SYNC_FRIEND_LIST, now);
    }
    
    public Set<String> getPGFriends(SNServices sns)
            throws ZingMeApiException, IOException
    {
        List<Object> zmFriends = sns.getZMFriends();
        String[] zmFriendsArr = new String[zmFriends.size()];
        for (int i = 0; i < zmFriends.size(); i++) {
            zmFriendsArr[i] = String.valueOf(zmFriends.get(i));
        }
        
        // store zmFriends to temp key for use redis set-intersection
        RedisKey zmFrKey = RedisKey.root().getChild(PGKeys.randomKey());
        DBContext.Redis().sadd(zmFrKey, zmFriendsArr);
        DBContext.Redis().expire(zmFrKey, 5);   // expire in 10 secs
        
        return DBContext.Redis().sinter(zmFrKey, PGKeys.ALL_USERS);
    }
    
    public Object dumpFriendList(String uid)
    {
        Set<String> friendIDs = (Set) FriendList.getFriendList(uid).dump();
        List<String> npcIDs = NPCList.getNPCList(uid).getAll();
        
        friendIDs.removeAll(npcIDs);
        
        return friendIDs;
    }
}