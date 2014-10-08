/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import connection.PGAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import share.PGException;
import target.RemoteTarget;
import target.RemoteTargetAdapter;
import target.Target;
import target.TargetResolver;

/**
 *
 * @author suaongmattroi
 */
public class SocketTargetResolver  implements TargetResolver{
    
    private static final String SOCKET_HOST = "127.0.0.1";
    private static final int SOCKET_PORT = 3377;
    
    private final PGAddress address;
    private Target target;
//    private final Lock lock = new ReentrantLock();
    
    private SocketTargetResolver() {
        this(SOCKET_HOST, SOCKET_PORT);
    }
    
    private static final SocketTargetResolver inst = new SocketTargetResolver();
    
    public static SocketTargetResolver inst() {
        return inst;
    }
    
    public SocketTargetResolver(String host, int port) {
        this.address = new PGAddress(host, port);
    }
    
    @Override
    public Target getUserTarget(String uid) {
        return getTarget();
    }

    @Override
    public Target getMasterTarget() {
        return getTarget();
    }
    
    private Target getTarget() {
        if (target == null)
        {
            target = new SocketTarget(address);
//            lock.lock();
//            try {
//                target = new SocketTarget(address);
//            }
//            catch(Exception ex) {
//                System.out.println(ex);
//            }finally {
//                lock.unlock();
//            }
        }
        
        return target;
    }
}
