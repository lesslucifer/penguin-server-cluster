/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.scenarios;

import com.mycompany.amfclient.PGTestLog;
import com.mycompany.amfclient.Responder;
import com.mycompany.amfclient.action.AcAuthenticate;
import com.mycompany.amfclient.action.Actions;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class ScLoadGame extends ScBase{
    
    private void loadGame(String uid)
            throws ClientStatusException, ServerStatusException
    {
        limit = 5;
        
        AcAuthenticate authenticate = new AcAuthenticate();
        PGLogCalculator.PGLogCalculator.getInst().push(uid, new Date());
        authenticate.authenticate(uid, (String t) -> {
            checkCompleteScenario(uid);
            sessions.put(uid, t);
            Actions action = new Actions();
            try {
                action.loadGame(uid, sessions, 
                        (Responder resp) -> { checkCompleteScenario(uid); });
                
                action.getFriendlist_async(uid, sessions, 
                        (Responder resp) -> { checkCompleteScenario(uid); });
                
                action.getUserGifts(uid, sessions, 
                        (Responder resp) -> { checkCompleteScenario(uid); });
                
                action.getGameMessages(uid, sessions, 
                        (Responder resp) -> { checkCompleteScenario(uid); });
            } catch (ClientStatusException | ServerStatusException ex) {
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        int nTest = 50;
        PGLogCalculator.PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.PGLogCalculator.getInst().setScenario("load_game");
        for(int i = 0; i < nTest; ++i)
        {
            ScLoadGame loader = new ScLoadGame();
            try {
                loader.loadGame("TESTUSER" + i);
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScLoadGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}