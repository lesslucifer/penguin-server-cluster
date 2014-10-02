/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import amfservices.actions.ServiceReflectTarget;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import target.RemoteTarget;

/**
 *
 * @author KieuAnh
 */
public class Server {
    private Server()
    {
        super();
    }
    
    private static final Server inst = new Server();
    
    public static Server inst()
    {
        return inst;
    }
    
    public void start() throws Exception
    {
        RMITarget target = new RMITarget(ServiceReflectTarget.class,
                git.httpservices.Services.class);
        RemoteTarget stub = (RemoteTarget) UnicastRemoteObject.exportObject(target, 3377);
        
        LocateRegistry.createRegistry(3377);
        Naming.rebind("rmi://localhost:3377/Target", stub);
        
        System.out.println("Server start...");
    }
}
