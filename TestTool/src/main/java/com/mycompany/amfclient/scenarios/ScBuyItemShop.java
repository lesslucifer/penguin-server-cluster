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
public class ScBuyItemShop extends ScBase{
    
    private static final String TYPE_GOLD = "gold";
    private static final String TYPE_PENGUIND = "penguin";
    private static final String TYPE_FISH = "fish";
    
    private void load(String uid, String type)
            throws ClientStatusException, ServerStatusException
    {
        limit = 8;
        
        AcAuthenticate authenticate = new AcAuthenticate();
        PGLogCalculator.getInst().push(uid, new Date());
        
        authenticate.authenticate(uid, (String t) -> {
            checkCompleteScenario(uid);
            sessions.put(uid, t);
            Actions action = new Actions();
            try {
                action.loadGame(uid, sessions, 
                    (Responder resp) -> 
                    {
                        checkCompleteScenario(uid);
                        try {
                            switch (type) {
                                case TYPE_GOLD:
                                    buyGold(action, uid, resp.getParams());
                                    break;
                                case TYPE_FISH:
                                    buyFish(action, uid, resp.getParams());
                                    break;
                                case TYPE_PENGUIND:
                                    buyPenguin(action, uid, resp.getParams());
                                    break;
                            }
                        } catch(ClientStatusException | ServerStatusException ex) {
                            Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                
                action.getFriendlist_async(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
                action.getUserGifts(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
                action.getGameMessages(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
            } catch (ClientStatusException | ServerStatusException ex) {
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void buyGold(Actions action, String uid, Map<String, Object> data) 
            throws ClientStatusException, ServerStatusException
    {
        action.buyGold(uid, "", sessions, (Responder resp) -> {checkCompleteScenario(uid);});
    }
    
    private void buyFish(Actions action, String uid, Map<String, Object> data)
            throws ClientStatusException, ServerStatusException
    {
        action.buyFish(uid, "", sessions, (Responder resp) -> {checkCompleteScenario(uid);});
    }
    
    private void buyPenguin(Actions action, String uid, Map<String, Object> cote)
            throws ClientStatusException, ServerStatusException
    {
        Map<String, Object> user = (Map<String, Object>)cote.get("user");
        String last_cote = (String)user.get("last_cote");
        
        action.buyPenguin(uid, last_cote, "", 1, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        for(int i = 0; i < nTest; ++i)
        {
            ScBuyItemShop loader = new ScBuyItemShop();
            try {
                loader.load("23885717", TYPE_FISH);
                loader.load("23885717", TYPE_GOLD);
                loader.load("23885717", TYPE_PENGUIND);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
