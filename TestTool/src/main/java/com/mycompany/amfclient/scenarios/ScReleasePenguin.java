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
public class ScReleasePenguin extends ScBase{
    
    private void load(String uid, int nLoop)
            throws ClientStatusException, ServerStatusException
    {
        limit = 5 + nLoop;
        
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
                        
                        String last_cote = (String)user.get("last_cote");
                        Map<String, Object> cotes = (Map<String, Object>)user.get("cote");
                        for(Map.Entry<String, Object> entry:cotes.entrySet())
                        {
                            Map<String, Object> cote = (Map<String, Object>)entry.getValue();
                            String coteId = (String) cote.get("cote_id");
                            if(coteId.equals(last_cote))
                            {
                                release(nLoop, action, cote, uid, coteId);
                            }
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
    
    private void release(int loop, Actions action, Map<String, Object> cote, String uid, String coteId)
    {
        int c = 0;
        Map<String, Object> penguinList = (Map<String, Object>)cote.get("penguin_list");
        for(Map.Entry<String, Object> entry : penguinList.entrySet())
        {
            if(++c > loop)
                break;
            
            Map<String, Object> penguin = (Map<String, Object>)entry.getValue();
            String peng_id = (String) penguin.get("penguin_id");
            
            try {
                action.releasePenguin(uid, coteId, peng_id, sessions, (Responder resp) -> {
                    checkCompleteScenario(uid);});
            } catch (ClientStatusException | ServerStatusException ex) {
                Logger.getLogger(ScReleasePenguin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        int nLoop = 1;
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("release_penguin");
        for(int i = 0; i < nTest; ++i)
        {
            ScReleasePenguin loader = new ScReleasePenguin();
            try {
                loader.load("23885717", nLoop);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
