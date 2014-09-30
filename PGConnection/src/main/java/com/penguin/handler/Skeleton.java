/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.handler;

import com.penguin.data.interfaces.IPGData;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 */
public class Skeleton extends SimpleIoHandler{
    
    private final IConnectionServices servicesHandler;
    
    private final IoSession session;
    
    private final IPGData data;
    
    public Skeleton(IConnectionServices servicesHandler, IoSession session, IPGData data) {
        
        this.servicesHandler = servicesHandler;
        this.session = session;
        this.data = data;
    }
    
    public void reflect() {
        
        if(this.servicesHandler != null) 
        {
            IPGData result = this.servicesHandler.reflectCall(data);
            if(this.session != null && this.session.isConnected()) 
            {
                this.session.write(result);
                this.servicesHandler.dispose();
            }
        }
    }
}
