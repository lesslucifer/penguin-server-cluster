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
public class ScRandomizePrize extends ScBase{
    private void randomizePrize(String uid, int nTest)
            throws ClientStatusException, ServerStatusException
    {
        limit = 6 + nTest;
        
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
                        Map<String, Object> params = resp.getParams();
                        Map<String, Object> user = (Map<String, Object>)params.get("user");

                        try {
                            buyAndRandom(action, uid, nTest);
                        } catch (Exception ex) {
                            Logger.getLogger(ScRandomizePrize.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private void buyAndRandom(Actions action, String uid, int nTest) throws Exception
    {
        action.buyRandomizeTurn(uid, "", sessions, 
            (Responder resp1) -> 
            {
                checkCompleteScenario(uid);
                for(int i = 0; i < nTest; ++i)
                {   
                    try { 
                        action.randomizePrize(uid, sessions,
                                (Responder resp2) ->{checkCompleteScenario(uid);});
                    } catch (ClientStatusException | ServerStatusException ex) {
                        PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                        Logger.getLogger(ScRandomizePrize.class.getName()).log(Level.SEVERE, null, ex);
                    }   
                }
            });
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        int nLoop = 10;
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("randomize_prize");
        for(int i = 0; i < nTest; ++i)
        {
            ScRandomizePrize loader = new ScRandomizePrize();
            try {
                loader.randomizePrize("23885717", nLoop);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
