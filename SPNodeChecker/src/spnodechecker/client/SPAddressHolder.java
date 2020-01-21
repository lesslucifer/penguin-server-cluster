/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker.client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spnodechecker.SPNodeChecker;

/**
 *
 * @author suaongmattroi
 */
public class SPAddressHolder {
    private static final String filePath = "addresses.json";
    
    private final List<SPAddress> addresses;
    
    public SPAddressHolder()
    {
        addresses = new ArrayList();
    }
    
    public void loadAddress()
    {
        try {
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            
            JSONArray targets= (JSONArray) jsonObject.get("targets");
            if(targets != null)
            {
                Iterator i = targets.iterator();
                while(i.hasNext())
                {
                    JSONObject target = (JSONObject)i.next();
                    SPAddress address = new SPAddress((String)target.get("address"), (long)target.get("port"));
                    addresses.add(address);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SPNodeChecker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(SPNodeChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<SPAddress> getAddresses()
    {
        return addresses;
    }
}
