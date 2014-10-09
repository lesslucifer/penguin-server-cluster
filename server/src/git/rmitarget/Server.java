/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import minaconnection.SimpleResponder;
import minaconnection.interfaces.IPGData;
import minaconnection.interfaces.IServices;
import minaconnection.handler.PGRouter;
import minaconnection.handler.SimpleIoHandler;
import org.apache.mina.core.session.IoSession;
import pgentity.pool.EntityPool;

/**
 *
 * @author KieuAnh
 */
public class Server {
    
    private static final int PORT = 3377;
    
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
        // ============= Stub RMI ===============
//        RMITarget target = new RMITarget(ServiceReflectTarget.class,
//                git.httpservices.Services.class);
//        RemoteTarget stub = (RemoteTarget) UnicastRemoteObject.exportObject(target, 3377);
//        
//        LocateRegistry.createRegistry(3377);
//        Naming.rebind("rmi://localhost:3377/Target", stub);
        
        // ============= Stub socket ============
        MinaTarget target = new MinaTarget(PORT);
        
        System.out.println("Server start...");
    }
}
