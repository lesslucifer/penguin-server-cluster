/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.handler;

import com.penguin.data.interfaces.IPGData;

/**
 *
 * @author suaongmattroi
 */
public interface IConnectionServices {
    
    IPGData reflectCall(final IPGData pgd);
    
    void dispose();
    
}
