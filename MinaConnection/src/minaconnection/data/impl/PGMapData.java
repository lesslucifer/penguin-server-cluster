/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.data.impl;

import java.util.Map;
import minaconnection.interfaces.IPGData;

/**
 *
 * @author suaongmattroi
 */
public class PGMapData implements IPGData{

    private final long index;
    
    private final String method;
    
    private final Map data;
    
    private final String caller;
    
    private final long now;
    
    public PGMapData(long index, String caller, String method, Map data, long now)
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
