/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker.client;

/**
 *
 * @author suaongmattroi
 */
public class SPAddress {
    
    private final String address;
    private final int port;
    
    public SPAddress(String address, long port)
    {
        this.address = address;
        this.port = (int)port;
    }
    
    public String makeUrl()
    {
        return "http://" + this.address + ":" + Integer.toString(this.port) + "/v2/logic";
    }
    
    public String getAddress()
    {
        return this.address;
    }
    
    public int getPort()
    {
        return this.port;
    }
}
