/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minaconnection;

import minaconnection.interfaces.IClientHandler;

/**
 *
 * @author suaongmattroi
 */
public class ClientHandlerFactory {
    
    public static IClientHandler create()
    {
        IClientHandler h = new MinaWaitResponseObject();
        return h;
    }
    
}
