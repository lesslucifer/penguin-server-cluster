/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suaongmattroi
 */
public class Random {
    
    public static List<Integer> randomInRange(int max, int min, int count)
    {
        List<Integer> newData = new ArrayList<>();
        for(int i = 0; i < count; ++i)
        {
            int tmp = (int)(Math.random() * (max - min)) + min;
            while(newData.contains(i))
            {
                tmp = (int)(Math.random() * (max - min)) + min;
            }
            newData.add(i);
        }
        return newData;
    }
    
    public static long randomMiliseconds()
    {
        return (long)((Math.random() * 7 + 3) / 10) * 1000;
    }
}
