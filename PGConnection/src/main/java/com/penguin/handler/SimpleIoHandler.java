/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author suaongmattroi
 */
public class SimpleIoHandler extends IoHandlerAdapter{
    
    // Override that method to handler event
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    }
    
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    }
    
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }
    
    // Final method
    @Override
    public final void sessionClosed(IoSession session) throws Exception {
    }
    
    @Override
    public final void sessionCreated(IoSession session) throws Exception {
    }
    
    @Override
    public final void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }
    
    @Override
    public final void sessionOpened(IoSession session) throws Exception {
    }
}