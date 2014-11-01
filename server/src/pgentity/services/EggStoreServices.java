/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import config.PGConfig;
import java.util.HashMap;
import java.util.Map;
import pgentity.BoxEgg;
import pgentity.Cote;
import pgentity.Dog;
import pgentity.EggStore;
import share.PGError;
import share.AMFBuilder;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class EggStoreServices
{
    public static enum EggStorage
    {
        NONE(0, "none"),
        COTE(1, "cote"),
        BOX_EGG(2, "box_egg"),
        LIMITED(3, "limited");

        private final int value;
        private final String description;
        private EggStorage(int state, String desc)
        {
            this.value = state;
            this.description = desc;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString()
        {
            return this.description;
        }

        private static final Map<Integer, EggStorage> allStorages = new HashMap();
        static
        {
            for (EggStorage questState : EggStorage.values()) {
                allStorages.put(questState.value, questState);
            }
        }
        
        public static EggStorage get(int st)
        {
            return allStorages.get(st);
        }
    }
    
    private EggStoreServices()
    {
        super();
    }
    
    private static final EggStoreServices inst = new EggStoreServices();
    
    public static EggStoreServices inst()
    {
        return inst;
    }
    
    public EggStorage addEgg(Cote cote, BoxEgg boxEgg, Dog dog, String eggKind, long now)
    {
        int maxBoxEggEggs = PGConfig.inst().getBoxEgg().get(boxEgg.getLevel()).getMaxEgg();
        
        if ((cote.eggStore().nOfEggs() < PGConfig.inst().temp().CotesEggs_Limit()))
        {
            if (boxEgg.eggStore().nOfEggs() < maxBoxEggEggs && dog.isAwake(now))
            {
                boxEgg.eggStore().addEgg(eggKind);
                return EggStorage.BOX_EGG;
            }
            else
            {
                cote.eggStore().addEgg(eggKind);
                return EggStorage.COTE;
            }
        }
        
        return EggStorage.LIMITED;
    }
    
    /**
     * This method only move egg from store to store, without any valid check (except source store must have enough egg).
     * Just call this method after check all data are valid
     * @param from
     * @param to
     * @param movedEggPacks
     */
    public void moveEgg(EggStore from, EggStore to, Map<String, Number> movedEggPacks)
    {
        PGException.Assert(checkEggs(from, movedEggPacks),
                PGError.NOT_ENOUGH_EGG, "Not enough egg");
        
        from.removeEggs(movedEggPacks);
        to.addEggs(movedEggPacks);
    }
    
    /**
     * Remove some randomly eggs from store
     * @param eggStore - egg store
     * @param eggs - Egg kinds and correspond number should be removed
     * @return all egg price
     * @throws PGException if egg data are invalid
     */
    public int removeEggFromStore(EggStore eggStore, Map<String, Number> eggs) throws PGException
    {
        int totalEggPrice = 0;
        PGException.Assert(checkEggs(eggStore, eggs),
                PGError.NOT_ENOUGH_EGG, "Not enough egg");
        
        for (Map.Entry<String, Number> eggsKindEntry : eggs.entrySet()) {
            String eggKind = eggsKindEntry.getKey();
            Integer nEgg = eggsKindEntry.getValue().intValue();
            
            int eggGold = PGConfig.inst().getEggs().get(eggKind).getGold();
            
            totalEggPrice += eggGold * nEgg;
        }
        
        eggStore.removeEggs(eggs);
    
        return totalEggPrice;
    }
    
    private boolean checkEggs(EggStore eggStore, Map<String, Number> eggs) throws PGException
    {
        for (Map.Entry<String, Number> eggEntry : eggs.entrySet()) {
            String eggKind = eggEntry.getKey();
            Integer nEgg = eggEntry.getValue().intValue();
            
            if (eggStore.nOfEggs(eggKind) < nEgg)
            {
                return false;
            }
        }
        
        return true;
    }
    
    public Map<String, Number> validateEgg(EggStore eggStore, Map<String, Number> wantedEggs)
    {
        Map<String, Number> validEggs = new HashMap();
        Map<String, Number> storeEggs = eggStore.getEggs();
        
        for (Map.Entry<String, Number> eggEntry : wantedEggs.entrySet()) {
            String eggKind = eggEntry.getKey();
            Number nWantedEgg = eggEntry.getValue();
            Number nStoreEgg = storeEggs.get(eggKind);
            if (nStoreEgg == null)
            {
                nStoreEgg = 0;
            }
            
            validEggs.put(eggKind, Math.min(nWantedEgg.intValue(), nStoreEgg.intValue()));
        }
        
        return validEggs;
    }
    
    public Map<String, Number> truncateEgg(Map<String, Number> eggs, int max)
    {
        Map<String, Number> truncated = new HashMap();
        
        int avail = max;
        for (Map.Entry<String, Number> eggEntry : eggs.entrySet()) {
            String eggKind = eggEntry.getKey();
            int nEgg = eggEntry.getValue().intValue();
            
            int nSelectedEgg = Math.min(avail, nEgg);
            truncated.put(eggKind, nSelectedEgg);
            avail -= nSelectedEgg;
        }
        
        return truncated;
    }
    
    public Map<String, Object> buildLimitedAMF(EggStore eggStore)
    {
        return AMFBuilder.toAMF(this.truncateEgg(eggStore.getEggs(),
                PGConfig.inst().temp().CotesEggs_Limit_Display()));
    }
    
    public Map<String, Object> buildAMF(EggStore eggStore)
    {
        return AMFBuilder.toAMF(eggStore.getEggs());
    }
    
    public Map<String, Number> substractEggs(Map<String, Number> from, Map<String, Number> to)
    {
        Map<String, Number> subs = new HashMap();
        
        for (Map.Entry<String, Number> eggEntry : from.entrySet()) {
            String eggKind = eggEntry.getKey();
            Number nFromEgg = eggEntry.getValue();
            Number nToEgg = to.get(eggKind);
            if (nToEgg == null)
            {
                nToEgg = 0;
            }
            
            subs.put(eggKind, Math.max(nFromEgg.intValue() - nToEgg.intValue(), 0));
        }
        
        return subs;
    }
}