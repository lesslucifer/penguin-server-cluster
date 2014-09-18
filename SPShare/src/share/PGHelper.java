/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author KieuAnh
 */
public class PGHelper
{
    private PGHelper() {}
    
    /**
     * Convert string to integer
     * if a NumberFormatException is thrown
     * this function return 0 by default
     * @param s number as string
     * @return int value of s
     */
    public static int toInteger(Object o)
    {
        try
        {
            if (o instanceof Number)
            {
                return ((Number) o).intValue();
            }
            
            return Integer.parseInt(o.toString());
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    
    public static long toLong(Object o)
    {
        try
        {
            if (o instanceof Number)
            {
                return ((Number) o).longValue();
            }
            
            return Long.parseLong(o.toString());
        }
        catch (Exception ex)
        {
            return 0;
        }
    }
    
    public static boolean isNullOrEmpty(Map m)
    {
        return m == null || m.isEmpty();
    }
    
    public static boolean isNullOrEmpty(String s)
    {
        return (s == null) || (s.isEmpty()) || ("".equals(s));
    }
    
    public static <E> Set<E> randomSub(Set<E> set, int n)
    {
        Random r = new Random(System.currentTimeMillis());
        List<E> list = new ArrayList(set);
        
        int nRand = Math.min(n, list.size());
        Set<E> out = new HashSet(nRand);
        
        for (int i = 0; i < nRand; i++) {
            int randIdx = i + r.nextInt(list.size() - i);
            Collections.swap(list, i, randIdx);
            
            out.add(list.get(i));
        }
        
        return out;
    }

    public static String randomKey() {
        UUID rand = UUID.randomUUID();
        return Long.toHexString(rand.getMostSignificantBits() + 37 * 
                rand.getLeastSignificantBits());
    }
}
