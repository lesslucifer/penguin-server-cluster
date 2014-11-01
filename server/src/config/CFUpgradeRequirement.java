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
public class CFUpgradeRequirement implements JSONable
{
    private CFUpgradeRequirement() {}
    static CFUpgradeRequirement parse(Map<String, Object> json)
    {
        CFUpgradeRequirement req = new CFUpgradeRequirement();
        req.deser(json);
        
        return req;
    }
    
    private int gold;
    private int coin;
    private int level;

    @Override
    public void deser(Map<String, Object> json)
    {
        gold = Integer.parseInt(json.get("gold").toString());
        coin = Integer.parseInt(json.get("coin").toString());
        level = Integer.parseInt(json.get("level").toString());
    }

    /**
     * @return the gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * @return the coin
     */
    public int getCoin() {
        return coin;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}
