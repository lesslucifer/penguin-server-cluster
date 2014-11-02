/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.action;

import com.mycompany.amfclient.PGTestLog;
import com.mycompany.amfclient.Responder;
import com.mycompany.amfclient.amfconnection.AMFConnection;
import com.mycompany.amfclient.amfparams.PGParamsCreator;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 *
 * @author suaongmattroi
 */
public class Actions {
    
    public void loadGame(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("uid", uid);
        data.put("signed_request", "1@3$5^bayTAM");
        
        AMFConnection.callAsync("loadGame", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Load game completed");
            });
    }
    
    public void getUserGifts(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        
        AMFConnection.callAsync("getUserGifts", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Load gifts completed");
            });
    }
    
    public void getFriendlist_async(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        
        AMFConnection.callAsync("getFriendList", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Load getFriendlist_async completed");
            });
    }
    
    public void getFriendlist_nonAsync(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        
        Responder resp = AMFConnection.call("getFriendList",
                PGParamsCreator.getInst().create(uid, data, sessions));
        
        callback.accept(resp);
        PGTestLog.getInst().info("Load getFriendlist_nonAsync completed");
    }
    
    public void getAchievements(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        
        AMFConnection.callAsync("getAchievements", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Load achievements completed");
            });
    }
    
    public void getGameMessages(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        
        AMFConnection.callAsync("getGameMessages", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Load game messages completed");
            });
    }
    
    public void collectEggs(String uid, 
            String coteId,
            Map<String, Integer> eggTypes,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("eggs", eggTypes);
        
        AMFConnection.callAsync("moveEggFromCoteToInventory", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Collect eggs completed");
            });
    }
    
    public void dropFish(String uid, 
            String coteId,
            Integer food,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("fish", food);
        
        AMFConnection.callAsync("dropFish", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Drop fish completed");
            });
    }
    
    public void randomizePrize(String uid, 
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        
        AMFConnection.callAsync("takeRandomizePrize", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Randomize prize completed");
            });
    }
    
    public void buyRandomizeTurn(String uid, 
            String turnId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("item_id", turnId);
        
        AMFConnection.callAsync("buyRandomizePrizeTurn", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Buy randomize prize completed");
            });
    }
    
    public void useGiftCode(String uid, 
            String code,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("code", code);
        
        AMFConnection.callAsync("useGiftCode", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Use gift code completed");
            });
    }
    
    public void upgradeCote(String uid, 
            String coteId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        
        AMFConnection.callAsync("upgradeCote", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Upgrade cote completed");
            });
    }
    
    public void upgradeBoxEgg(String uid, 
            String coteId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        
        AMFConnection.callAsync("upgradeBoxEgg", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Upgrade boxegg completed");
            });
    }
    
    public void buyPenguinExp(String uid, 
            String coteId,
            String pengId,
            Integer exp,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("penguin_id", pengId);
        data.put("exp", exp);
        
        AMFConnection.callAsync("buyExpPenguin", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Buy exp penguin completed");
            });
    }
    
    public void buyPenguinLevel(String uid, 
            String coteId,
            String pengId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("penguin_id", pengId);
        
        AMFConnection.callAsync("buyLevelPenguin", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Buy level penguin completed");
            });
    }
    
    public void getDetailFriend(String uid, 
            String friendId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("friend_id", friendId);
        
        AMFConnection.callAsync("visitFriend", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Get detail friend completed");
            });
    }
    
    public void releasePenguin(String uid, 
            String coteId,
            String pengId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("penguin_id", pengId);
        
        AMFConnection.callAsync("sellPenguin", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Release penguin completed");
            });
    }
    
    public void helpFriendFish(String uid, 
            String coteId,
            String friendId,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("friend_id", friendId);
        
        AMFConnection.callAsync("helpFriendFish", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Help friend fish completed");
            });
    }
    
    public void stealEgg(String uid, 
            String coteId,
            String friendId,
            String eggType,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("friend_id", friendId);
        data.put("kind", eggType);
        
        AMFConnection.callAsync("stealEgg", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Steal egg completed");
            });
    }
    
    public void buyFish(String uid, 
            String itemType,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("item_id", itemType);
        
        AMFConnection.callAsync("buyFish", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Buy fish completed");
            });
    }
    
    public void buyGold(String uid, 
            String itemType,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("item_id", itemType);
        
        AMFConnection.callAsync("buyGold", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Buy gold completed");
            });
    }
    
    public void buyPenguin(String uid,
            String coteId,
            String penguinType,
            Integer nPenguin,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("cote_id", coteId);
        data.put("item_id", penguinType);
        data.put("number_item", nPenguin);
        
        AMFConnection.callAsync("buyPenguin", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Buy penguin completed");
            });
    }
    
    public void getMails(String uid, 
            Integer offset,
            Integer length,
            Map<String, String> sessions,
            Consumer<Responder> callback)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> data = new HashMap();
        data.put("offset", offset);
        data.put("mail_length", length);
        
        AMFConnection.callAsync("getMails", 
            PGParamsCreator.getInst().create(uid, data, sessions), 
            (Responder dataResp) -> {
                callback.accept(dataResp);
                PGTestLog.getInst().info("Get mails completed");
            });
    }
}
