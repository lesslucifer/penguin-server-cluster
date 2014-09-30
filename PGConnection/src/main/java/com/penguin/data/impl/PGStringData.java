/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.data.impl;

import com.penguin.data.interfaces.IPGData;

/**
 *
 * @author suaongmattroi
 */
public class PGStringData implements IPGData{

    private final int index;
    
    private final String method;
    
    private final String data;
    
    public PGStringData(int index, String method, String data)
    {
        this.index = index;
        this.method = method;
        this.data = data;
    }
    
    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public Object getData() {
        return this.data;
    }
}
