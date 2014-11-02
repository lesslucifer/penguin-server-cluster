/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import pgentity.CheckerLogPool;
import pgentity.EntityContext;
import pgentity.quest.QuestChecker;
import pgentity.quest.QuestCheckerFactory;
import pgentity.services.EggStoreServices;
import share.PGHelper;

/**
 *
 * @author Salm
 */
class REItemsChecker implements QuestChecker {
    private final Map<String, Number> needItems;

    public REItemsChecker(Map<String, Number> needEggs) {
        this.needItems = needEggs;
    }
    
    @Override
    public boolean isAccept(EntityContext context) {
        REUser eUser = REUser.getUser(context.getUser());
        for (Map.Entry<String, Number> needEggEntry : needItems.entrySet()) {
            String item = needEggEntry.getKey();
            int nItemsReq = needEggEntry.getValue().intValue();
            
            int nItemsHas = eUser.inv().nOfEggs(item);
            if (nItemsHas < nItemsReq)
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void returnQuest(EntityContext context) {
        REUser eUser = REUser.getUser(context.getUser());
        EggStoreServices.inst().removeEggFromStore(eUser.inv(), needItems);
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        return Collections.EMPTY_MAP;
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
            Map<String, Object> mData = (Map) data;
            Map<String, Number> needEggs = new HashMap();
            
            for (Map.Entry<String, Object> needItemsEntry : mData.entrySet()) {
                String eggKind = needItemsEntry.getKey();
                needEggs.put(eggKind, PGHelper.toInteger(needItemsEntry.getValue()));
            }
            
            return new REItemsChecker(needEggs);
        }
    }
}
