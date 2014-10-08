/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.test;

import connection.data.impl.PGStringData;
import connection.data.interfaces.IPGData;
import connection.data.interfaces.IServices;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class TestResponseder implements IServices{
    
    public IPGData getFriendlist(IPGData regParams) {
        return new PGStringData(regParams.getIndex(), "123", "getFriendlist", "Friendlist: empty", 1234);
    }
    
    public IPGData loadGame(IPGData regParams) {
        return new PGStringData(regParams.getIndex(), "123", "loadGame", "LoadGame: initializing...", 1234);
    }
    
    public IPGData getDataUser(IPGData regParams) {
        return new PGStringData(regParams.getIndex(), "123", "getDataUser", "Data: Hieu, 22, UIT", 1234);
    }
    
    public IPGData Println(IPGData regParams) {
        while(regParams.getIndex() != -1)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(TestResponseder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new PGStringData(regParams.getIndex(), "123", "Println", "Println-> Data: Hieu, 22, UIT", 1234);
    }
}
