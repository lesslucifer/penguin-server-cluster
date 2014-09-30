/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.test;

import com.penguin.data.impl.PGStringData;
import com.penguin.data.interfaces.IPGData;
import com.penguin.data.interfaces.IServices;

/**
 *
 * @author suaongmattroi
 */
public class TestResponseder implements IServices{
    
    public IPGData getFriendlist(IPGData regParams) {
        return new PGStringData(0, "", "Friendlist: empty");
    }
    
    public IPGData loadGame(IPGData regParams) {
        return new PGStringData(0, "", "LoadGame: initializing...");
    }
    
    public IPGData getDataUser(IPGData regParams) {
        return new PGStringData(0, "", "Data: Hieu, 22, UIT");
    }
    
}
