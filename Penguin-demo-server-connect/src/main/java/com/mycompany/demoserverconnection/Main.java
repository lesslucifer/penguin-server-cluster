/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.demoserverconnection;

import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author hieubui
 */
public class Main {
    
    // Change to true first to run server
    // Change to false after to run client -> connect to server
    private static final boolean isServer = true;
    
    private static final int MY_PORT = 3015;
    private static final int SERVER_PORT = 3015;
    private static final String SERVER_ADDRESS = "127.0.0.1";
    
    public static void main(String[] args) throws IOException, InterruptedException{
        
        if(isServer)
        {
            Sender sender = new Sender(MY_PORT);
        }
        else
        {
            final Reciever reciever = new Reciever(SERVER_ADDRESS, SERVER_PORT);
            new Thread()
            {
                @Override
                public void run()
                {
                    reciever.connect();
                }
            }.start();
            
            Scanner scan = new Scanner(System.in);
            while(true)
            {
                String name = scan.nextLine();
                for(int i = 0; i < 1000; ++i)
                {
                    SerializeObject obj = new SerializeObject(name, i);
                    reciever.write(obj);
                }
            }
        }
    }
}
