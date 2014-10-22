/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public class CFInit
{
    private CFInit() {}
    
    public static interface Cote
    {
        String getName();
        int getLevel();
        int getPoolFish();
        Map<String, Number> getPenguins();
    }
}
