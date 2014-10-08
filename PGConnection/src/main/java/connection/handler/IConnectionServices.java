/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package connection.handler;

import connection.data.interfaces.IPGData;

/**
 *
 * @author suaongmattroi
 */
public interface IConnectionServices {
    
    IPGData reflectCall(final IPGData pgd);
    
    void dispose();
    
}
