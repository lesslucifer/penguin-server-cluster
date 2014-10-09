/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.data.impl;

import minaconnection.interfaces.IPGData;

/**
 *
 * @author suaongmattroi
 */
public class PGStringData implements IPGData{

    private final long index;
    
    private final String method;
    
    private final String data;
    
    private final String caller;
    
    private final long now;
    
    public PGStringData(long index, String caller, String method, String data, long now)
    {
        this.index = index;
        this.method = method;
        this.data = data;
        this.caller = caller;
        this.now = now;
    }
    
    @Override
    public String getCaller() {
        return caller;
    }

    @Override
    public long getNow() {
        return now;
    }

    @Override
    public long getIndex() {
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
