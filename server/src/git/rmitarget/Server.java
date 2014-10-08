/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import amfservices.ReflectAdapter;
import connection.SimpleResponder;
import connection.data.interfaces.IPGData;
import connection.data.interfaces.IServices;
import connection.handler.PGRouter;
import connection.handler.SimpleIoHandler;
import org.apache.mina.core.session.IoSession;

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
        // ============= Stub RMI ===============
//        RMITarget target = new RMITarget(ServiceReflectTarget.class,
//                git.httpservices.Services.class);
//        RemoteTarget stub = (RemoteTarget) UnicastRemoteObject.exportObject(target, 3377);
//        
//        LocateRegistry.createRegistry(3377);
//        Naming.rebind("rmi://localhost:3377/Target", stub);
        
        // ============= Stub socket ============
        // Create services
        IServices services = new ReflectAdapter();
        
        // Create router
        final PGRouter router = new PGRouter(services);
        
        SimpleResponder req = new SimpleResponder(3377,
            new SimpleIoHandler()
            {
                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    if(router != null) {
                        IPGData data = (IPGData) message;
                        String method = data.getMethod();
                        router.drive(method, session, data);
                    }
                }
            });
        
        System.out.println("Server start...");
    }
}
