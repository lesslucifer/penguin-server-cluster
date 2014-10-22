/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 *
 * @author KieuAnh
 */
abstract class JSONExperienceLevelArray<E extends CFExperienceable & JSONable>
    extends JSONMapArray<E>
{
    private int maxExp = Integer.MIN_VALUE;
    private final NavigableMap<Integer, Integer> levelByExp = new TreeMap();
    
    public int levelByExp(int exp)
    {
        return this.levelByExp.get(levelByExp.floorKey(exp));
    }
    
    public final int maxExp()
    {
        return maxExp;
    }

    @Override
    public void deser(Map<String, Object> json) {
        super.deser(json);
        
        this.maxExp = Integer.MIN_VALUE;
        for (Integer level : this) {
            E expLevel = this.get(level);
            this.levelByExp.put(expLevel.getExp(), level);
            
            if (maxExp < expLevel.getExp())
            {
                maxExp = expLevel.getExp();
            }
        }
    }
}