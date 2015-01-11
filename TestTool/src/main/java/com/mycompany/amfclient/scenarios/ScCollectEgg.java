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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class ScCollectEgg extends ScBase {
    
    private void collectEgg(String uid, Integer nEgg)
            throws ClientStatusException, ServerStatusException
    {
        limit = 6;
        
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
                        // Get data cote - egg
                        Map<String, Object> params = resp.getParams();
                        Map<String, Object> user = (Map<String, Object>)params.get("user");

                        String last_cote = (String)user.get("last_cote");

                        Map<String, Integer> eggTypes = new HashMap();
                        eggTypes.put("Egg_10", nEgg);
                        try {
                            action.collectEggs(uid, last_cote, eggTypes, sessions, 
                                    (Responder resp1) ->{ 
                                        checkCompleteScenario(uid); });
                        } catch (ClientStatusException | ServerStatusException ex) {
                            Logger.getLogger(ScCollectEgg.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                
                action.getFriendlist_async(uid, sessions, (Responder resp) -> { 
                        checkCompleteScenario(uid); });
                action.getUserGifts(uid, sessions, (Responder resp) -> { 
                        checkCompleteScenario(uid); });
                action.getGameMessages(uid, sessions, (Responder resp) -> { 
                        checkCompleteScenario(uid); });
            } catch (ClientStatusException | ServerStatusException ex) {
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void collectEggMultiTime(String uid, Integer nEgg, int nTime)
            throws ClientStatusException, ServerStatusException
    {
        limit = 5 + nTime;
        
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
                        // Get data cote - egg
                        Map<String, Object> params = resp.getParams();
                        Map<String, Object> user = (Map<String, Object>)params.get("user");

                        String last_cote = (String)user.get("last_cote");

                        Map<String, Integer> eggTypes = new HashMap();
                        eggTypes.put("Egg_10", nEgg);
                        try {
                            for(int i = 0; i < nTime; ++i)
                            {
                                action.collectEggs(uid, last_cote, eggTypes, sessions, 
                                        (Responder resp1) ->{ 
                                            checkCompleteScenario(uid); });
                            }
                        } catch (ClientStatusException | ServerStatusException ex) {
                            Logger.getLogger(ScCollectEgg.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                
                action.getFriendlist_async(uid, sessions, (Responder resp) -> { 
                        checkCompleteScenario(uid); });
                action.getUserGifts(uid, sessions, (Responder resp) -> { 
                        checkCompleteScenario(uid); });
                action.getGameMessages(uid, sessions, (Responder resp) -> { 
                        checkCompleteScenario(uid); });
            } catch (ClientStatusException | ServerStatusException ex) {
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("collect_egg");
        // Collect egg per user
        for(int i = 0; i < nTest; ++i)
        {
            ScCollectEgg loader = new ScCollectEgg();
            try {
                loader.collectEgg("23885717", 10);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Collect egg 
        for(int i = 0; i < nTest; ++i)
        {
            ScCollectEgg loader = new ScCollectEgg();
            try {
                loader.collectEggMultiTime("23885717", 10, 10);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
