/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services.penguin;

import config.PGConfig;
import share.PGLog;
import pgentity.Dog;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.quest.QuestLogger;
import pgentity.services.EggStoreServices;
import pgentity.services.PenguinServices;
import pgentity.services.PenguinUpdator;

public class PenguinNormalUpdator implements PenguinUpdator {
    private static enum PenguinAction
    {
        NONE, EAT, SPAWN
    }
    
    private final Penguin penguin;
    private final long now;
    private final EntityContext context;
    private final Dog dog;
    private final QuestLogger uQLogger;
    
    private long nextEatTime, nextSpawnTime;

    public PenguinNormalUpdator(Penguin penguin, long now,
            EntityContext context, Dog dog, QuestLogger uQLogger) {
        this.penguin = penguin;
        this.now = now;
        this.context = context;
        this.dog = dog;
        this.uQLogger = uQLogger;
        
        this.nextEatTime = calcNextEatTime();
        this.nextSpawnTime = calcNextSpawnTime();
    }
    
    @Override
    public boolean update()
    {
        boolean ret = this.updateAction();
        
        // update Time
        this.nextEatTime = calcNextEatTime();
        this.nextSpawnTime = calcNextSpawnTime();
        
        return ret;
    }
    
    private boolean updateAction()
    {
        switch (this.nextAction())
        {
            case EAT:
                if (context.getCote().getPoolFish() > 0)
                {
                    PenguinServices.inst().eat(penguin, context, uQLogger, nextEatTime);
                    return true;
                }
                
                return false;
            case SPAWN:
                if (context.getCote().eggStore().nOfEggs() < PGConfig.inst().temp().CotesEggs_Limit())
                {
                    String eggKind = PenguinServices.inst().
                            spawnEgg(penguin, nextSpawnTime);
                    EggStoreServices.EggStorage storage = EggStoreServices.inst()
                            .addEgg(context.getCote(), context.getBoxEgg(), dog, eggKind, now);
                    penguin.setLastEggStorage(storage);
                    
                    PGLog.info("Penguin %s spawn %s at %d",
                            penguin.shortDescription(), eggKind, nextSpawnTime);
                    return true;
                }
                
                // update may be skipped by full eggs, update spawn time
                long penguinSpawnTime = PenguinServices.inst()
                        .configOf(penguin).getTimeSpawn();
                long nearestSpawnTime = penguin.getLastSpawn() +
                        penguinSpawnTime * ((now - penguin.getLastSpawn()) / penguinSpawnTime);

                penguin.setLastSpawn(nearestSpawnTime);
                return !(nextEatTime > now) && !(context.getCote().getPoolFish() > 0);
            case NONE:
                return false;
        }
        
        return false;
    }
    
    @Override
    public void save()
    {
        this.penguin.saveToDB();
    }

    @Override
    public long nextActionTime()
    {
        switch (this.nextAction())
        {
            case EAT:
                return nextEatTime;
            case SPAWN:
                return nextSpawnTime;
        }
        
        return Long.MIN_VALUE;
    }
    
    private PenguinAction nextAction()
    {
        if (nextSpawnTime <= now && nextSpawnTime < nextEatTime)
        {
            return PenguinAction.SPAWN;
        }
        else if (nextEatTime <= now)
        {
            return PenguinAction.EAT;
        }
        
        return PenguinAction.NONE;
    }
    
    private long calcNextEatTime()
    {
        if (PenguinServices.inst().configOf(penguin).getFeed() > 0)
        {
            float eatRed = PGConfig.inst().getCote()
                    .get(context.getCote().getLevel()).getEatTimeReduce();
            long eatTime = (long) ((1f - eatRed) * penguin.getFood() * PGConfig.inst().temp().DigestTimePerFish());
            eatTime *= 1000; // convert to milsec
            
            return penguin.getLastEat() + eatTime;
        }
        
        return Long.MAX_VALUE;
    }
    
    private long calcNextSpawnTime()
    {
        long spawnTime = PenguinServices.inst().configOf(penguin).getTimeSpawn();
        if (spawnTime > 0)
        {
            return penguin.getLastSpawn() + spawnTime;
        }
        
        return Long.MAX_VALUE;
    }
}
