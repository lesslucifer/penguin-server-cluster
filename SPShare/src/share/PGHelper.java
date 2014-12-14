/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
     * @param o
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
    
    public static float toFloat(Object o)
    {
        try
        {
            if (o instanceof Number)
            {
                return ((Number) o).floatValue();
            }
            
            return Float.parseFloat(o.toString());
        }
        catch (Exception ex)
        {
            return 0f;
        }
    }
    
    public static boolean toBoolean(Object o)
    {
        try
        {
            if (o instanceof Boolean)
            {
                return (Boolean) o;
            }
            else if (o instanceof Number)
            {
                return ((Number) o).intValue() != 0;
            }
            
            return Boolean.parseBoolean(o.toString());
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
    public static <E> List<E> mapToList(Map<?, E> m)
    {
        List<E> list = new ArrayList<E>(m.size());
        for (int i = 0; i < m.size(); ++i)
        {
            list.add(null);
        }
        
        for (Map.Entry<? extends Object, E> entry : m.entrySet()) {
            Object key = entry.getKey();
            E value = entry.getValue();
            
            int index = PGHelper.toInteger(key);
            if (list.get(index) != null)
            {
                throw new IllegalArgumentException(
                        "Map " + m + " not valid with LIST_MAP, duplicate key " + index);
            }
            
            list.set(index, value);
        }
        
        for (int i = 0; i < m.size(); ++i)
        {
            if (list.get(i) == null)
            {
                throw new IllegalArgumentException(
                        "Map " + m + " not valid with LIST_MAP, empty key " + i);
            }
        }
        
        return Collections.unmodifiableList(list);
    }
    
    public static boolean isNullOrEmpty(Map m)
    {
        return m == null || m.isEmpty();
    }
    
    public static boolean isNullOrEmpty(String s)
    {
        return (s == null) || (s.isEmpty()) || ("".equals(s));
    }
    
    public static String validStr(String s)
    {
        return (s != null)?s:"";
    }
    
    public static <E> E valid(E e, E def)
    {
        return (e != null)?e:def;
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
    
    public static String stackTrace(StackTraceElement []traces)
    {
        StringBuilder sb = new StringBuilder();
        
        for(StackTraceElement stackTraceElement : traces) {
            sb.append(stackTraceElement.toString());
            sb.append(System.lineSeparator());
        }
        
        return sb.toString();
    }
    
    public static String randomGUID()
    {
        return UUID.randomUUID().toString();
    }
    
    private static final Gson prettyJson = new GsonBuilder().setPrettyPrinting().create();
    public static String obj2PrettyJSON(Object obj)
    {
        String strData = prettyJson.toJson(obj);

        return strData;
    }
    
    public static final Gson getJSONParser()
    {
        return prettyJson;
    }
}
