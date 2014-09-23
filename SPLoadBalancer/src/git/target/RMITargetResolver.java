/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

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
 * @author KieuAnh
 */
public class RMITargetResolver implements TargetResolver
{
    private static final String RMI_HOST = "127.0.0.1";
    private static final int RMI_PORT = 3377;
    
    private final String host;
    private final int port;
    private Target target;
    private final Lock lock = new ReentrantLock();
    
    private RMITargetResolver()
    {
        this(RMI_HOST, RMI_PORT);
    }
    
    private static final RMITargetResolver inst = new RMITargetResolver();
    
    public static RMITargetResolver inst()
    {
        return inst;
    }

    public RMITargetResolver(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Target getUserTarget(String uid)
    {
        return getTarget();
    }

    @Override
    public Target getMasterTarget() {
        return getTarget();
    }

    private Target getTarget() {
        if (target == null)
        {
            lock.lock();
            try
            {
                if (target == null)
                {
                    Registry registry = LocateRegistry.getRegistry(host, port);
                    RemoteTarget remoteTarget = (RemoteTarget) registry.lookup("Target");
                    target = new RemoteTargetAdapter(remoteTarget);
                }
            }
            catch (RemoteException ex) {
                PGException.pgThrow(ex);
            } catch (NotBoundException ex) {
                PGException.pgThrow(ex);
            }
            finally
            {
                lock.unlock();
            }
        }
        
        return target;
    }
}
