/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import share.PGLog;
import target.Response;

/**
 *
 * @author KieuAnh
 */
public class Server {
    
    private final int port;
    
    private Server(int port)
    {
        super();
        this.port = port;
    }
    
    public static Server create(int port)
    {
        return new Server(port);
    }
    
    public void start() throws Exception
    {
        Response.VERBOSE_LOG = false;
        MinaLogicTarget minaTarget = new MinaLogicTarget(port, git.httpservices.Services.class);
        
        PGLog.info("Server start...");
    }
}
