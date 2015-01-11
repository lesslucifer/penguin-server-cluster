/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.scenarios;

import PGLogCalculator.PGLogCalculator;
import com.mycompany.amfclient.PGTestLog;
import com.mycompany.amfclient.Responder;
import com.mycompany.amfclient.action.AcAuthenticate;
import com.mycompany.amfclient.action.Actions;
import static com.mycompany.amfclient.scenarios.ScBase.sessions;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class ScStealEgg extends ScBase{
   
    private void load(String uid, int nFriend, int loop)
            throws ClientStatusException, ServerStatusException
    {
        limit = 5 + nFriend + loop;
        
        AcAuthenticate authenticate = new AcAuthenticate();
        PGLogCalculator.getInst().push(uid, new Date());
        
        authenticate.authenticate(uid, (String t) -> {
            checkCompleteScenario(uid);
            sessions.put(uid, t);
            Actions action = new Actions();
            try {
                action.loadGame(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
                action.getFriendlist_async(uid, sessions, (Responder resp) -> {
                    checkCompleteScenario(uid);
                    Map<String, Object> params = resp.getParams();
                    if(params.size() > 0)
                    {
                        // Pickup friend in random
                        int friendCount = 0;
                        for(Map.Entry<String, Object> entry : params.entrySet())
                        {
                            if(++friendCount > nFriend)
                                break;
                            String friendId = entry.getKey();
                            stealEgg(action, uid, friendId, loop);
                        }
                    }
                });
                action.getUserGifts(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
                action.getGameMessages(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
            } catch (ClientStatusException | ServerStatusException ex) {
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void stealEgg(Actions action, String uid, String fid, int loop)
    {
        try {
            // Visit friend first
            action.getDetailFriend(uid, fid, sessions, 
                (Responder resp) -> {
                    checkCompleteScenario(uid);
                    Map<String, Object> dataFriend = (Map<String, Object>)resp.getParams().get("friend");
                    Map<String, Object> dataCote = (Map<String, Object>)dataFriend.get("cote");
                    Map<String, Object> cote = (Map<String, Object>)dataCote.get("0");
                    String coteId = (String) cote.get("cote_id");
                    for(int i = 0; i < loop; ++i)
                    {
                        try {
                            action.stealEgg(uid, fid, coteId, "Egg_0", sessions, (Responder resp1) -> {checkCompleteScenario(uid);});
                        } catch (ClientStatusException | ServerStatusException ex) {
                            PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                            Logger.getLogger(ScHelpFriendFish.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        } catch (ClientStatusException | ServerStatusException ex) {
            Logger.getLogger(ScVisitFriend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        int nEggSteal = 1;
        int nFriend = 1;
        
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("steal_egg");
        for(int i = 0; i < nTest; ++i)
        {
            ScStealEgg loader = new ScStealEgg();
            try {
                loader.load("23885717", nFriend, nEggSteal);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
