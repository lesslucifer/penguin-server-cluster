/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.data.impl;

import com.penguin.data.interfaces.IPGData;
import java.util.Map;

/**
 *
 * @author suaongmattroi
 */
public class PGMapData implements IPGData{

    private final int index;
    
    private final String method;
    
    private final Map data;
    
    public PGMapData(int index, String method, Map data)
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
