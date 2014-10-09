/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.interfaces;

import java.io.Serializable;

/**
 *
 * @author suaongmattroi
 */
public interface IPGData extends Serializable{
    
    long getIndex();
    
    String getMethod();
    
    Object getData();
    
    String getCaller();
    
    long getNow();
}
