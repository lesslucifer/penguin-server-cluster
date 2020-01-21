/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minaconnection;

import java.io.Serializable;

/**
 *
 * @author suaongmattroi
 */
interface IMinaData extends Serializable {
    
    long index();
    
    Serializable data();   
}
