/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import spnodechecker.client.SPAddress;
import spnodechecker.client.SPAddressHolder;

/**
 *
 * @author suaongmattroi
 */
public class SPNodeChecker {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SPAddressHolder holder;
        SPChecker checker;
        
        holder = new SPAddressHolder();
        holder.loadAddress();
        
        checker = new SPChecker();
        
        List<SPAddress> addresses = holder.getAddresses();
        try {
            Map<String, Object> data = new HashMap();
            Map<SPAddress, String> results = new HashMap();
            results = checker.check(addresses, data);
            for(Map.Entry<SPAddress, String>entry:results.entrySet())
            {
                System.out.println(entry.getKey().getAddress() + ":" + entry.getKey().getPort() + " - " + entry.getValue());
            }
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SPFrChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
