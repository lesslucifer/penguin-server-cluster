/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share.amf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class AMFBuilder
{
    private AMFBuilder() {}
    
    public static Map<String, Object> toAMF(Map m)
    {
        return m;
    }
    
    public static <E> Map<String, Object> toAMF(Iterable<E> ite)
    {
       Map<String, Object> amf = new HashMap();
       int counter = 0;
       
       for (E o : ite)
       {
           amf.put(String.valueOf(counter++), o);
       }
       
       return amf;
    }
   
    public static <E> Map<String, Object> toAMF(E[] arr)
    {
        Map<String, Object> amf = new HashMap();
        int counter = 0;

        for (E o : arr)
        {
            amf.put(String.valueOf(counter++), o);
        }

        return amf;
    }
    
    public static <E> List<E> amfToList(Object lst)
    {
        if (lst instanceof Object[])
        {
            Object[] arr = (Object[]) lst;
            
            List<E> ret = new ArrayList(arr.length);
            for (Object elem : arr) {
                ret.add((E) elem);
            }
            
            return ret;
        }
        
        return null;
    }
    
    public static Map<String, Object> make(Object... data)
    {
        if (data.length == 0)
        {
            return Collections.EMPTY_MAP;
        }
        
        PGException.Assert(data.length % 2 == 0,
                PGError.UNDEFINED, "Cannot use AMFBuilder.make because data is invalid");
        
        final int n = data.length / 2;
        for (int i = 0; i < n; i += 2)
        {
            PGException.Assert(data[i] instanceof String, PGError.UNDEFINED,
                    "Cannot use AMFBuilder.make because data key not be String");
        }
        
        Map<String, Object> amf = new HashMap(n);
        for (int i = 0; i < n; i += 2) {
            amf.put((String) data[i], data[i + 1]);
        }
        
        return amf;
    }
}
