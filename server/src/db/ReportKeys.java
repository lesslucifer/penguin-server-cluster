/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author KieuAnh
 */
public class ReportKeys {
    
    public static void main(String[] args)
    {
        final String reportFile = "log/redis.txt";
        
        try
        {
            PrintStream fileWriter = new PrintStream(new File(reportFile));
            Set<String> redisKeys = DBContext.Redis().keys("*");
            
            Map<String, Object> classifiedKeys = new HashMap();
            for (String key : redisKeys) {
                putKey(key, classifiedKeys);
            }
            
            writeKey(classifiedKeys, fileWriter, "");
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(ReportKeys.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ReportKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void putKey(String key, Map<String, Object> keyStore) throws Exception
    {
        if (!key.contains(":"))
        {
            if (keyStore.containsKey(key))
            {
                throw new Exception("Duplication key " + key);
            }
            else
            {
                keyStore.put(key, "");
            }
        }
        else
        {
            String tab = key.substring(0, key.indexOf(":") + 1);
            if (!keyStore.containsKey(tab))
            {
                keyStore.put(tab, new HashMap());
            }
            
            Map<String, Object> tabKeyStore = (Map) keyStore.get(tab);
            putKey(key.substring(key.indexOf(":") + 1, key.length()), tabKeyStore);
        }
    }
    
    private static void writeKey(Map<String, Object> keyStore, PrintStream writer, String last)
    {
        for (Map.Entry<String, Object> keyEntry : keyStore.entrySet()) {
            String keyTab = keyEntry.getKey();
            Object keyVal = keyEntry.getValue();
            
            writer.println(last + "\t" + keyTab);
            if (keyVal instanceof Map)
            {
                Map<String, Object> subKeys = (Map) keyVal;
                writeKey(subKeys, writer, last + "\t");
            }
        }
    }
}