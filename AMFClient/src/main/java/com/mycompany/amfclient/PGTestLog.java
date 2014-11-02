/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient;

/**
 *
 * @author suaongmattroi
 */
public class PGTestLog {
    
    private static PGTestLog inst;
    static
    {
        inst = new PGTestLog();
    }
    public static PGTestLog getInst()
    {
        return inst;
    }
    
    private PGTestLog()
    {
        
    }
    
    private void log(String type, String msg)
    {
        switch(type)
        {
            case "ERROR":
                System.out.println("**" + type + ": " + msg);
            break;
                
            case "INFO":
                System.out.println(type + ": " + msg);
                break;
        }
    }
    
    public void error(String msg)
    {
        log("ERROR", msg);
    }
    
    public void info(String msg)
    {
        log("INFO", msg);
    }
}
