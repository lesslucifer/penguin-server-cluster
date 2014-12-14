/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import config.CFPenguin;
import config.PGConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import share.PGLog;
import pgentity.Cote;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.quest.FeedPenguinRecord;
import pgentity.quest.PenguinMaxLevelRecord;
import pgentity.quest.QuestLogger;
import share.PGError;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class PenguinServices 
{
    private PenguinServices()
    {
        super();
    }
    
    private static final PenguinServices inst = new PenguinServices();
    
    public static PenguinServices inst()
    {
        return inst;
    }
    
    public List<String> getPenguinByEatPriorited(Set<String> penguins)
    {
        List<String> penguinIDs = new ArrayList<String>(penguins);
        Collections.sort(penguinIDs);
        
        return penguinIDs;
    }
    
    public void eat(final Penguin penguin, final EntityContext context,
            QuestLogger quest, long eatTime) throws PGException
    {
        Cote cote = context.getCote();
        PGException.Assert(cote.getPoolFish() > 0,
                PGError.EMPTY_POOL, "Empty pool");
        
        int nFishToEat = Math.min(configOf(penguin).getFeed(), cote.getPoolFish());
        PGException.Assert(nFishToEat > 0, PGError.PENGUIN_CANNOT_EAT,
                "Penguin must eat positive fish (" + nFishToEat + ")");
        PGLog.info("Penguin %s eat %d fish", penguin.shortDescription(), nFishToEat);
        
        cote.setPoolFish(cote.getPoolFish() - nFishToEat);
        penguin.setFood(nFishToEat);
        penguin.setLastEat(eatTime);
        this.increasePenguinExp(context.getUser().getUid(), penguin,
                (int) (nFishToEat * PGConfig.inst().temp().PenguinExpPerFish()), eatTime);
        UserServices.inst().increaseUserExp(context,
                (int) (nFishToEat * PGConfig.inst().temp().PenguinExpPerFish()), eatTime);
        
        FeedPenguinRecord questRecord = new FeedPenguinRecord(penguin.getPenguinID(), nFishToEat);
        quest.log(questRecord);
    }
    
    public String spawnEgg(Penguin penguin, long spawnTime) throws PGException
    {
        PGException.Assert(configOf(penguin).getTimeSpawn() > 0,
                PGError.PENGUIN_NOT_ENOUGH_LEVEL_SPAWN, "Cannot spawn egg");
        
        penguin.setLastSpawn(spawnTime);
        String eggKind = configOf(penguin).getEggKind();
        
        return eggKind;
    }
    
    public void increasePenguinExp(String owner, Penguin penguin, int nExp, long now)
    {
        penguin.increaseExp(nExp);
        
        if (this.updatePenguinLevel(penguin))
        {
            CFPenguin.Group conf = PGConfig.inst().getPenguin()
                    .getGroup(penguin.getKind());
            // do something on level changed
            if (penguin.getLevel() == conf.getFirstSpawnableLevel())
            {
                penguin.setLastSpawn(now);
            }
            
            if (penguin.getLevel() >= conf.size() - 1)
            {
                QuestLogger qLogger = QuestServices.inst().getQuestLogger(owner, now);
                qLogger.log(new PenguinMaxLevelRecord());
            }
        }
    }
    
    private boolean updatePenguinLevel(Penguin penguin)
    {
        int newLevel = PGConfig.inst().getPenguin()
                .getGroup(penguin.getKind()).levelByExp(penguin.getExp());
        
        if (newLevel != penguin.getLevel())
        {
            penguin.setLevel(newLevel);
            return true;
        }
        
        return false;
    }
    
    public CFPenguin.Group.Level configOf(Penguin penguin)
    {
        return PGConfig.inst().getPenguin()
                .getGroup(penguin.getKind())
                .get(penguin.getLevel());
    }
    
    public long nextEat(Penguin penguin, Cote cote)
    {
        if (configOf(penguin).getFeed() > 0 && cote.getPoolFish() > 0)
        {
            float eatRed = PGConfig.inst().getCote()
                    .get(cote.getLevel()).getEatTimeReduce();
            return penguin.getLastEat() + food2TimeHungry(penguin.getFood(), eatRed);
        }
        
        return Long.MAX_VALUE;
    }
    
    public long nextSpawn(Penguin penguin, Cote cote)
    {
        if (configOf(penguin).getTimeSpawn() > 0 &&
                cote.eggStore().nOfEggs() < PGConfig.inst().temp().CotesEggs_Limit())
        {
            return penguin.getLastSpawn() + configOf(penguin).getTimeSpawn();
        }
        
        return Long.MAX_VALUE;
    }
    
    private static long food2TimeHungry(int food, float eatTimeReducement)
    {
        return (long) (food * 1000 * PGConfig.inst().temp().DigestTimePerFish() * (1f - eatTimeReducement));
    }
}