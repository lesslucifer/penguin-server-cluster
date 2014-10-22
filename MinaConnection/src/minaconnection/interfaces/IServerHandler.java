/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minaconnection.interfaces;

import minaconnection.MinaSender;

/**
 *
 * @author suaongmattroi
 */
public interface IServerHandler {
    
    void messageSent(MinaSender writer, Object message) throws Exception;
    void messageReceived(MinaSender writer, Object message) throws Exception;
    void exceptionCaught(Throwable cause) throws Exception;
    
}
