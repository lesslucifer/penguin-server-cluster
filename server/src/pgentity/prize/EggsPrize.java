/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.prize;

import config.PGConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pgentity.EntityContext;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class EggsPrize implements PGPrize
{
    private Map<String, Number> prizeEggs;
    
    private EggsPrize(Map<String, Number> data)
    {
        prizeEggs = data;
    }
    
    @Override
    public Map<String, Object> award(EntityContext context, long now)
    {
        context.getUser().inventory().eggStore().addEggs(prizeEggs);
        return Collections.EMPTY_MAP;
    }

    @Override
    public boolean canPrize(EntityContext context)
    {
        int totalPrizeEggs = calcTotalPrizeEggs();
        return PGConfig.inst().temp().MaxInventory() >= 
                (context.getUser().inventory().eggStore().nOfEggs()
                + totalPrizeEggs);
    }
    
    private int calcTotalPrizeEggs()
    {
        int totalEggs = 0;
        for (Map.Entry<String, Number> prizeEgg : prizeEggs.entrySet()) {
            totalEggs += prizeEgg.getValue().intValue();
        }
        
        return totalEggs;
    }
    
    public static class Factory implements IPrizeFactory
    {
        private Factory()
        {
            super();
        }
        
        private static final Factory inst = new Factory();
        
        public static Factory inst()
        {
            return inst;
        }
        
        @Override
        public PGPrize createPrize(Object prizeData) {
            Map<String, String> mData = (Map) prizeData;
            Map<String, Number> prizeEggs = new HashMap();
            
            for (Map.Entry<String, String> needEggEntry : mData.entrySet()) {
                String eggKind = needEggEntry.getKey();
                String nEgg = needEggEntry.getValue();
                
                prizeEggs.put(eggKind, PGHelper.toInteger(nEgg));
            }
            
            return new EggsPrize(prizeEggs);
        }
    }
}
