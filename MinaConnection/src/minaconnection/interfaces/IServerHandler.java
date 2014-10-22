/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minaconnection.interfaces;

import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 */
public interface IServerHandler {
    
    void messageSent(IoSession session, long index, Object message) throws Exception;
    void messageReceived(IoSession session, long index, Object message) throws Exception;
    void exceptionCaught(IoSession session, Throwable cause) throws Exception;
    
}
