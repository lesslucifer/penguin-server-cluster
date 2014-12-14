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
public class ScVisitFriend extends ScBase{
    
    private void load(String uid)
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
                action.loadGame(uid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
                action.getFriendlist_async(uid, sessions, (Responder resp) -> {
                    checkCompleteScenario(uid);
                    Map<String, Object> params = resp.getParams();
                    if(params.size() > 0)
                    {
                        // Pickup friend in random
                        for(Map.Entry<String, Object> entry : params.entrySet())
                        {
                            String friendId = entry.getKey();
                            visitFriend(action, uid, friendId);
                            break;
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
    
    private void visitFriend(Actions action, String uid, String fid)
    {
        try {
            action.getDetailFriend(uid, fid, sessions, (Responder resp) -> {checkCompleteScenario(uid);});
        } catch (ClientStatusException | ServerStatusException ex) {
            Logger.getLogger(ScVisitFriend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("visit_friend");
        for(int i = 0; i < nTest; ++i)
        {
            ScVisitFriend loader = new ScVisitFriend();
            try {
                loader.load("23885717");
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
