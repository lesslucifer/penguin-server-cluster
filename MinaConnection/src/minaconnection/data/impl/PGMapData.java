/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.data.impl;

import java.util.Map;
import java.util.Objects;
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
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (this.index ^ (this.index >>> 32));
        hash = 71 * hash + Objects.hashCode(this.caller);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PGMapData other = (PGMapData) obj;
        if (this.index != other.index) {
            return false;
        }
        if (!Objects.equals(this.caller, other.caller)) {
            return false;
        }
        return true;
    }
}
