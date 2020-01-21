/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minaconnection;

import minaconnection.interfaces.IServerHandler;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 * a class for user override content
 */
public class MinaServerHandler implements IServerHandler{

    @Override
    public void messageSent(MinaSender sender, Object message) throws Exception {
        
    }

    @Override
    public void messageReceived(MinaSender sender, Object message) throws Exception {
        
    }

    @Override
    public void exceptionCaught(Throwable cause) throws Exception {
        
    }
    
}
