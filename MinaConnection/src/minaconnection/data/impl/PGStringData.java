/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.data.impl;

import java.util.Objects;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) (this.index ^ (this.index >>> 32));
        hash = 47 * hash + Objects.hashCode(this.caller);
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
        final PGStringData other = (PGStringData) obj;
        if (this.index != other.index) {
            return false;
        }
        if (!Objects.equals(this.caller, other.caller)) {
            return false;
        }
        return true;
    }
    
}
