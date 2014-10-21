/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.util.logging.Level;
import java.util.logging.Logger;
import minaconnection.interfaces.IMinaData;

/**
 *
 * @author suaongmattroi
 */
public class PGWaitingResponding {
    
    public static final String RESP_FUNC = "handleResp";
    private IMinaData data;
    
    public PGWaitingResponding() {
        this.data = null;
    }
    
    public IMinaData doReq() {
        // use another sync methods
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
    
    public void handleResp(IMinaData data) {
        this.data = data;
    }
}
