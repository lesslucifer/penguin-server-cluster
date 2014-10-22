/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import minaconnection.interfaces.IClientHandler;

/**
 *
 * @author suaongmattroi
 */
class MinaWaitResponseObject implements IClientHandler{
    
    private Serializable data;
    
    public MinaWaitResponseObject() {
        this.data = null;
    }
    
    @Override
    public Serializable doReq() {
        // use another sync methods
        while(data == null) 
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MinaWaitResponseObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return this.data;
    }
    
    @Override
    public void callback(Serializable data) {
        this.data = data;
    }
}
