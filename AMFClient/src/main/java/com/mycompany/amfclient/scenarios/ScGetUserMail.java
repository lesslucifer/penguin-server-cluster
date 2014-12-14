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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class ScGetUserMail extends ScBase{
    
    private void get(String uid)
            throws ClientStatusException, ServerStatusException
    {
        limit = 5;
        
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
                            action.getMails(uid, 0, 10, sessions, (Responder resp1) -> {checkCompleteScenario(uid);});
                        } catch (ClientStatusException | ServerStatusException ex){
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
    
    public static void main(String[] args)
    {
        int nTest = 1;
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("get_user_mails");
        for(int i = 0; i < nTest; ++i)
        {
            ScGetUserMail loader = new ScGetUserMail();
            try {
                loader.get("23885717");
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
