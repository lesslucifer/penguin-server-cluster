/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.amfclient;

import java.util.Map;

/**
 *
 * @author suaongmattroi
 */
public class Responder {
    
    Map<String, Object> params;
    long time;
    
    public Map<String, Object> getParams() {
        return params;
    }

    public long getTime() {
        return time;
    }
    
    public Responder(Map<String, Object> params, long time)
    {
        this.params = params;
        this.time = time;
    }
}
