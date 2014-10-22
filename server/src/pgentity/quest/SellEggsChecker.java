/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import pgentity.CheckerLogPool;
import pgentity.EggStore;
import pgentity.EntityContext;
import pgentity.services.EggStoreServices;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
class SellEggsChecker implements QuestChecker
{
    private final Map<String, Number> needEggs;

    public SellEggsChecker(Map<String, Number> needEggs) {
        this.needEggs = needEggs;
    }
    
    @Override
    public boolean isAccept(EntityContext context) {
        EggStore eggStore = context.getUser().inventory().eggStore();
        for (Map.Entry<String, Number> needEggEntry : needEggs.entrySet()) {
            String eggType = needEggEntry.getKey();
            Number nEgg = needEggEntry.getValue();
            
            if (eggStore.nOfEggs(eggType) < nEgg.intValue())
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void returnQuest(EntityContext context) {
        EggStore eggStore = context.getUser().inventory().eggStore();
        for (Map.Entry<String, Number> needEggEntry : needEggs.entrySet()) {
            String eggType = needEggEntry.getKey();
            Number nEgg = needEggEntry.getValue();
            
            int nEggHas = eggStore.nOfEggs(eggType);
            PGException.Assert(nEggHas >= nEgg.intValue(),
                    PGError.NOT_ENOUGH_EGG,
                    "Not enough egg " + eggType + " (" + nEggHas + "/" + nEgg + ")");
        }
        
        EggStoreServices.inst().removeEggFromStore(eggStore, needEggs);
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new TreeMap();
        
        EggStore eggStore = context.getUser().inventory().eggStore();
        Map<String, Number> invEggs = eggStore.getEggs();
        Map<String, Number> sellEggs = new HashMap();
        
        for (String eggKind : needEggs.keySet()) {
            if (invEggs.containsKey(eggKind))
            {
                sellEggs.put(eggKind, invEggs.get(eggKind));
            }
            else
            {
                sellEggs.put(eggKind, 0);
            }
        }
        
        data.put("sell_egg", sellEggs);
        
        return data;
    }

    @Override
    public Map<String, Object> dump() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public void destroy() {
    }
    
    public static class Factory implements QuestCheckerFactory
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
        public QuestChecker createChecker(Object data, CheckerLogPool logPool) {
            Map<String, String> mData = (Map) data;
            Map<String, Number> needEggs = new HashMap();
            
            for (Map.Entry<String, String> needEggEntry : mData.entrySet()) {
                String eggKind = needEggEntry.getKey();
                String nEgg = needEggEntry.getValue();
                
                needEggs.put(eggKind, Integer.valueOf(nEgg));
            }
            
            return new SellEggsChecker(needEggs);
        }
    }
}
