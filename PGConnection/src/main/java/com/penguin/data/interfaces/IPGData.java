/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.data.interfaces;

import java.io.Serializable;

/**
 *
 * @author suaongmattroi
 */
public interface IPGData extends Serializable{
    
    int getIndex();
    
    String getMethod();
    
    Object getData();
    
}
