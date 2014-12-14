/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.scenarios;

import PGLogCalculator.PGLogCalculator;
import com.mycompany.amfclient.PGTestLog;
import com.mycompany.amfclient.action.AcAuthenticate;
import flex.messaging.io.amf.client.exceptions.ClientStatusException;
import flex.messaging.io.amf.client.exceptions.ServerStatusException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class ScAuthenticate extends ScBase{
    
    private void authenticate(String uid)
            throws ClientStatusException, ServerStatusException
    {
        AcAuthenticate authenticate = new AcAuthenticate();
        PGLogCalculator.getInst().push(uid, new Date());
        authenticate.authenticate(uid, (String t) -> {
            PGLogCalculator.getInst().push(uid, new Date());
        });
    }
    
    public static void main(String[] args)
    {
        int nTest = 1;
        
        PGLogCalculator.getInst().serve(nTest);
        PGLogCalculator.getInst().setScenario("authenticate");
        for(int i = 0; i < nTest; ++i)
        {
            ScAuthenticate sc = new ScAuthenticate();
            try {
                sc.authenticate("23885717");
            } catch (ClientStatusException | ServerStatusException ex) {
                PGTestLog.getInst().error(ex.toString() + Integer.toString(i));
                Logger.getLogger(ScAuthenticate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}