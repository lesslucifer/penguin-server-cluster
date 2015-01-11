/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.io.Serializable;
import minaconnection.interfaces.IClientHandler;

/**
 *
 * @author suaongmattroi
 */
class MinaWaitResponseObject implements IClientHandler {
    
    private Serializable data;
    
    public MinaWaitResponseObject() {
        this.data = null;
    }
    
    @Override
    public Serializable doReq() throws Exception {
        // use another sync methods
        while (data == null)
        {
            synchronized (this)
            {
                this.wait();
            }
        }
        return this.data;
    }
    
    @Override
    public void callback(Serializable data) {
        this.data = data;
        synchronized (this)
        {
            this.notify();
        }
    }
}
