/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import pgentity.services.penguin.PenguinNormalUpdator;
import config.CFCote;
import config.PGConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import pgentity.BoxEgg;
import pgentity.Cote;
import pgentity.Dog;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.PenguinList;
import pgentity.Penguindex;
import pgentity.User;
import pgentity.quest.QuestLogger;
import share.PGError;
import db.PGKeys;
//import pgentity.events.release_event.ReleaseEventServices;
import share.PGMacro;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class CoteServices
{
    private CoteServices()
    {
        super();
    }
    
    private static final CoteServices inst = new CoteServices();
    
    public static CoteServices inst()
    {
        return inst;
    }
    
    public void updateCote(final EntityContext context,
            final QuestLogger uQLogger, final long now)
    {
        final String uid = context.getUid();
        final String coteID = context.getCoteID();
        final Cote cote = context.getCote();
        final Dog dog = Dog.getDog(cote.getUid(), cote.getCoteID());
        
        PenguinList penguinsEntity = cote.penguins();
        
        int nPenguin = penguinsEntity.size();
        if (nPenguin > 0)
        {
            //update penguins
            PriorityQueue<PenguinUpdator> penguinUpdators = 
                    new PenguinUpdatorPQ();

            Set<String> penguinIDs = penguinsEntity.getAll();
            Set<Penguin> penguins = new HashSet(penguinIDs.size());
            for (String pID : penguinIDs)
            {
                penguins.add(Penguin.getPenguin(uid, coteID, pID));
            }
            
            // add normal updator
            for (Penguin penguin : penguins) {
                PenguinNormalUpdator penguinUpdator =
                        new PenguinNormalUpdator(penguin, now, context, dog, uQLogger);
                penguinUpdators.add(penguinUpdator);
            }
            
            // add release events
            //penguinUpdators.addAll(ReleaseEventServices.SERVICES.getPenguinUpdator(cote, penguins, now));
            
            //build penguins operations queue
            while (!penguinUpdators.isEmpty())
            {
                PenguinUpdator updator = penguinUpdators.poll();
                
                if (updator.nextActionTime() <= now && updator.update())
                {
                    penguinUpdators.add(updator);
                }
                else
                {
                    updator.save();
                }
            }
        }
        
    }
    
    public void upgradeCote(final User user, final QuestLogger userQLogger,
            final Cote cote) throws PGException
    {
        int nextLevel = cote.getLevel() + 1;
        PGException.Assert(PGConfig.inst().getCote().containsKey(nextLevel),
                PGError.OUT_OF_COTE_LEVEL, "End of config cote level");
        
        CFCote.Level coteConf = PGConfig.inst().getCote().get(nextLevel);
        
        // Check require upgrade
        PGException.Assert(coteConf.getUpgradeRequire().getCoin() <= user.getCoin(),
                PGError.NOT_ENOUGH_COIN, "Not enough coin. Have " + user.getCoin() +
                        " require " + coteConf.getUpgradeRequire().getCoin());
        PGException.Assert(coteConf.getUpgradeRequire().getGold()<= user.getGold(),
                PGError.NOT_ENOUGH_GOLD, "Not enough Gold. Have " + user.getGold() +
                        " require " + coteConf.getUpgradeRequire().getGold());
        PGException.Assert(coteConf.getUpgradeRequire().getLevel()<= user.getLevel(),
                PGError.NOT_ENOUGH_LEVEL, "Not enough Level. Current " + user.getLevel() +
                        " require " + coteConf.getUpgradeRequire().getLevel());
        
        cote.setLevel(nextLevel);
        
        // Update user data
        if (coteConf.getUpgradeRequire().getCoin() > 0)
        {
            UserServices.inst().decreaseCoin(user, coteConf.getUpgradeRequire().getCoin());
        }
        
        if (coteConf.getUpgradeRequire().getGold() > 0)
        {
            UserServices.inst().decreaseGold(user, userQLogger, coteConf.getUpgradeRequire().getGold());
        }
    }
    
    public Map<String, Integer> dropFish(final EntityContext context, QuestLogger quest, int nFish, long now) throws PGException
    {
        final String uid = context.getUid();
        final String coteID = context.getCoteID();
        final Cote cote = context.getCote();
        
        final int nMaxFish = PGConfig.inst().getCote()
                .get(cote.getLevel()).getMaxFish();
        
        PGException.Assert(cote.getPoolFish() + nFish <= nMaxFish,
                PGError.TOO_MUCH_FISH,
                "Too much fish (" + (cote.getPoolFish() + nFish) +
                    ") - maximum is: (" + nMaxFish + ")");
        
        cote.setPoolFish(cote.getPoolFish() + nFish);
        
        List<String> penguinIDs = PenguinServices.inst().
                getPenguinByEatPriorited(cote.penguins().getAll());
        
        Map<String, Integer> atePenguins = new HashMap();
        
        for (String penguinID : penguinIDs)
        {
            Penguin penguin = Penguin.getPenguin(uid, coteID, penguinID);
            
            long nextEatTime = PenguinServices.inst()
                    .nextEat(penguin, cote);
            if (nextEatTime <= now)
            {
                try
                {
                    PenguinServices.inst().eat(penguin, context, quest, now);
                    atePenguins.put(penguinID, penguin.getFood());
                    
                    // reset-spawn time for hungry penguins
                    penguin.setLastSpawn(now);
                }
                catch (PGException ex)
                {
                    // penguin cannot eat
                    break;
                }
                    
                penguin.saveToDB();
            }
        }
        
        cote.saveToDB();
        
        return atePenguins;
    }
    
    public Cote createCote(String uid, String coteID,
            CFCote.Templates.Template templ,
            Penguindex penguindex, long createdTime) throws PGException
    {
        Cote cote = Cote.newCote(uid, coteID,
                templ.getLevel(), templ.getFish(),
                templ.getName());
        
        this.initBoxEgg(cote, templ.getBoxeggLevel());
        this.initEggs(cote, templ.getEggs());
        this.initPenguins(uid, coteID, cote.penguins(),
                templ, penguindex, createdTime);
        this.initDog(cote, createdTime);
        
        cote.saveToDB();
        
        return cote;
    }
    
    private void initBoxEgg(Cote cote, int lvl)
    {
        BoxEgg.newBoxEgg(cote.getUid(), cote.getCoteID(), lvl);
    }
    
    private void initPenguins(String uid, String coteID, PenguinList penguinList,
            CFCote.Templates.Template penguinConfigs,
            Penguindex pDex, long now) throws PGException
    {
        int i = 0;
        String[] penguinIDs = new String[penguinConfigs.getPenguins().size()];
        
        for (Map.Entry<String, Number> pEntry : penguinConfigs.getPenguins().entrySet()) {
            String pKind = pEntry.getKey();
            int pLevel = pEntry.getValue().intValue();
            
            final String penguinID = PGKeys.randomKey();
            penguinIDs[i++] = penguinID;
            
            Penguin.newPenguin(uid, coteID, penguinID, pKind, pLevel, now);
            pDex.add(pKind);
        }
        
        penguinList.add(penguinIDs);
    }
    
    private void initEggs(Cote cote, Map<String, Number> eggs)
    {
        cote.eggStore().addEggs(eggs);
    }
    
    private void initDog(Cote cote, long now)
    {
        Dog.newDog(cote.getUid(), cote.getCoteID(), now);
    }
    
    public void destroyCote(String uid, String coteID)
    {
        Cote.destroy(uid, coteID);
        destroyPenguinList(uid, coteID);
        BoxEgg.destroy(uid, coteID);
        Dog.destroy(uid, coteID);
    }
    
    private void destroyPenguinList(String uid, String coteID)
    {
        PenguinList penguinList = PenguinList.getPenguinList(uid, coteID);
        Set<String> penguinIDs = penguinList.getAll();
        
        PenguinList.destroyList(uid, coteID);
        for (String penguinID : penguinIDs) {
            Penguin.destroy(uid, coteID, penguinID);
        }
    }
    
    public Object dumpCote(String uid, String coteID)
    {
        Map<String, Object> data = new HashMap();
        data.put(PGMacro.COTE, Cote.getCote(uid, coteID).dump());
        data.put(PGMacro.PENGUIN_LIST, dumpPenuinList(uid, coteID));
        data.put(PGMacro.DOG, Dog.getDog(uid, coteID).dump());
        data.put(PGMacro.BOXEGG, BoxEgg.getBoxEgg(uid, coteID).dump());
        
        return data;
    }
    
    public void restoreCote(String uid, String coteID, Map<String, Object> data)
    {
        Cote.restore(uid, coteID, (Map) data.get(PGMacro.COTE));
        restorePenguinList(uid, coteID, (List) data.get(PGMacro.PENGUIN_LIST));
        Dog.restore(uid, coteID, (Map) data.get(PGMacro.DOG));
        BoxEgg.restore(uid, coteID, (Map) data.get(PGMacro.BOXEGG));
    }
    
    private Object dumpPenuinList(String uid, String coteID)
    {
        PenguinList penguinList = PenguinList.getPenguinList(uid, coteID);
        Set<String> penguinIDs = penguinList.getAll();
        
        Set<Object> data = new HashSet(penguinIDs.size());
        
        for (String penguinID : penguinIDs) {
            data.add(Penguin.getPenguin(uid, coteID, penguinID).dump());
        }
        
        return data;
    }
    
    private void restorePenguinList(String uid, String coteID,
            List<Map<String, String>> data)
    {
        String[] pIDs = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            pIDs[i] = PGKeys.randomKey();
            
            Penguin.restore(uid, coteID, pIDs[i], data.get(i));
        }
        
        PenguinList.destroyList(uid, coteID);
        PenguinList.getPenguinList(uid, coteID).add(pIDs);
    }
}