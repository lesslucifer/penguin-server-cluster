/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection;

import connection.data.interfaces.IPGData;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author suaongmattroi
 */
public class PGWaitingResponding {
    
    public static final String RESP_FUNC = "handleResp";
    private IPGData data;
    
    public PGWaitingResponding() {
        this.data = null;
    }
    
    public IPGData doReq() {
        while(data == null) 
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(PGWaitingResponding.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return this.data;
    }
    
    public void handleResp(IPGData data) {
        this.data = data;
    }
}
