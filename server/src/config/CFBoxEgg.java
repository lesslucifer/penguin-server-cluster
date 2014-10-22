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
public class CFBoxEgg extends JSONMapArray<CFBoxEgg.Level> implements JSONable
{
    private CFBoxEgg() {}
    static CFBoxEgg parse(Map<String, Object> json)
    {
        CFBoxEgg boxEgg = new CFBoxEgg();
        boxEgg.deser(json);
        
        return boxEgg;
    }
    @Override
    public void deser(Map<String, Object> json)
    {
        Map<String, Object> levelsJSON = (Map<String, Object>) json.get("levels");
        super.deser(levelsJSON);
    }

    @Override
    protected Level newElement(Map<String, Object> elemJSON)
    {
        return new Level();
    }
    
    public static class Level implements JSONable
    {
        private Level() {}
        
        private int maxEgg;
        private CFUpgradeRequirement upgradeRequire;

        @Override
        public void deser(Map<String, Object> json)
        {
            this.maxEgg = Integer.parseInt(json.get("max_egg").toString());
            upgradeRequire = CFUpgradeRequirement.parse((Map<String, Object>) json.get("upgrade_require"));
        }
        
        public int getMaxEgg() {
            return maxEgg;
        }

        public CFUpgradeRequirement getUpgradeRequire() {
            return upgradeRequire;
        }
    }
}
