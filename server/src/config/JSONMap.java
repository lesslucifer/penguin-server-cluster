/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author KieuAnh
 */
abstract class JSONMap<K, E extends JSONable> extends HashMap<K, E> implements JSONable, Iterable<K>
{
    @Override
    public void deser(Map<String, Object> json)
    {
        for (Entry<String, Object> elemEntry : json.entrySet())
        {
            K elemKey = this.keyFromString(elemEntry.getKey());
            Map<String, Object> elemJSON = (Map<String, Object>) elemEntry.getValue();
            
            E e = this.newElement(elemJSON);
            e.deser(elemJSON);
            this.put(elemKey, e);
        }
    }
    
    protected abstract K keyFromString(String o);
    protected abstract E newElement(Map<String, Object> elemJSON);

    @Override
    public Iterator<K> iterator() {
        return this.keySet().iterator();
    }
}
