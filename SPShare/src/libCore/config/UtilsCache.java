/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libCore.config;

import com.google.gson.Gson;
import org.apache.commons.configuration.CompositeConfiguration;

/**
 *
 * @author linhta
 */
class UtilsCache<K,V>
{
    public V get(GuavaLoadingCache<CacheByteData> loadingCache, Gson jsonSerializer, K key, Class<V> cls)
    {
        CacheByteData cacheObj = loadingCache.get(String.valueOf(key).getBytes());
        if (cacheObj != null)
        {
            byte[] data = cacheObj.getData();
            if (data != null)
            {
                String strData = new String(data);
                return cls.cast(strData);
                //return jsonSerializer.fromJson(strData,cls);
            }
        }

        return null;
    }
     
    public V put (CompositeConfiguration config,GuavaLoadingCache<CacheByteData> loadingCache,Gson jsonSerializer,String key, Class<V> cls)
    {
        String value = config.getString(key);
        String strData = jsonSerializer.toJson(value);
        byte[] csData = strData.getBytes();

        if (value != null)
            loadingCache.set(key.getBytes(), CacheByteData.wrap(csData));
        try
        {
            return cls.cast(value);
        }
        catch(Exception e)
        {
            System.out.print(e.getMessage());
            return null;
        }
    }
    
    public static void main(String[] args)
    {
        UtilsCache<String, String > cl= new UtilsCache<String, String>();
        System.out.print(cl.get(null, null, null, String.class));
    }
}


