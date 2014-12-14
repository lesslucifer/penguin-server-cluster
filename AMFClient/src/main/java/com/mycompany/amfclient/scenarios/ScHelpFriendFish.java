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
public class ScHelpFriendFish extends ScBase{
    
    private void load(String uid, int nFriend)
            throws ClientStatusException, ServerStatusException
    {
        limit = 5 + nFriend * 2;
        
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
                            helpFriend(action, uid, friendId);
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
    
    private void helpFriend(Actions action, String uid, String fid)
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
                    
                    try {
                        action.helpFriendFish(uid, fid, coteId, sessions, (Responder resp1) -> {
                            checkCompleteScenario(uid);});
                        
                    } catch (ClientStatusException | ServerStatusException ex) {
                        Logger.getLogger(ScHelpFriendFish.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
        } catch (ClientStatusException | ServerStatusException ex) {
            Logger.getLogger(ScVisitFriend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        int nFriendHelp = 1;
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("help_friend_fish");
        for(int i = 0; i < nTest; ++i)
        {
            ScHelpFriendFish loader = new ScHelpFriendFish();
            try {
                loader.load("23885717", nFriendHelp);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
