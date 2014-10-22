/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import minaconnection.MinaAddress;
import target.Target;
import target.TargetResolver;

/**
 *
 * @author suaongmattroi
 */
public class MinaTargetResolver  implements TargetResolver{
    
    private static final String SOCKET_HOST = "127.0.0.1";
    private static final int SOCKET_PORT = 9090;
    
    private final MinaAddress address;
    private Target target;
//    private final Lock lock = new ReentrantLock();
    
    private MinaTargetResolver() {
        this(SOCKET_HOST, SOCKET_PORT);
    }
    
    private static final MinaTargetResolver inst = new MinaTargetResolver();
    
    public static MinaTargetResolver inst() {
        return inst;
    }
    
    public MinaTargetResolver(String host, int port) {
        this.address = new MinaAddress(host, port);
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
            target = new MinaTarget(address);
        }
        
        return target;
    }
}
