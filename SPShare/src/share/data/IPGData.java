/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share.data;

import java.io.Serializable;

/**
 *
 * @author suaongmattroi
 */
public interface IPGData extends Serializable {
    
    String caller();
    
    String method();
    
    Object data();
    
    long now();
    
    PGDataType type();
}
