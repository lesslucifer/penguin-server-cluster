/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import java.util.Objects;

/**
 *
 * @author suaongmattroi
 */
public class MinaAddress {
    
    private final String address;
    private final int port;
    
    public MinaAddress(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.address);
        hash = 71 * hash + this.port;
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
        final MinaAddress other = (MinaAddress) obj;
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        return this.port == other.port;
    }
}
