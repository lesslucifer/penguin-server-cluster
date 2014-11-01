/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.services.penguin;

import pgentity.services.PenguinUpdator;
import config.PGConfig;
import pgentity.Dog;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.services.EggStoreServices;
import pgentity.services.PenguinServices;

/**
 *
 * @author Salm
 */
class PenguinSpawnEggUpdator implements PenguinUpdator {
    private final Penguin penguin;
    private final long now;
    private final EntityContext context;
    private final Dog dog;
    
    private long nextSpawnTime;

    public PenguinSpawnEggUpdator(Penguin penguin, long now,
            EntityContext context, Dog dog) {
        this.penguin = penguin;
        this.now = now;
        this.context = context;
        this.dog = dog;
        
        this.nextSpawnTime = calcNextSpawnTime();
    }

    @Override
    public long nextActionTime() {
        return nextSpawnTime;
    }

    @Override
    public boolean update() {
        if (context.getCote().eggStore().nOfEggs() >=
                PGConfig.inst().temp().CotesEggs_Limit())   // cote's eggs if full
        {
            long penguinSpawnTime = PenguinServices.inst()
                    .configOf(penguin).getTimeSpawn();
            long nearestSpawnTime = penguin.getLastSpawn() +
                    penguinSpawnTime * ((now - penguin.getLastSpawn()) / penguinSpawnTime);

            penguin.setLastSpawn(nearestSpawnTime);
            return false;
        }
        else
        {
            String eggKind = PenguinServices.inst().
                    spawnEgg(penguin, nextSpawnTime);
            EggStoreServices.EggStorage storage = EggStoreServices.inst()
                    .addEgg(context.getCote(), context.getBoxEgg(), dog, eggKind, now);
            penguin.setLastEggStorage(storage);
            this.nextSpawnTime = calcNextSpawnTime();

            return true;
        }
    }
    
    @Override
    public void save()
    {
        this.penguin.saveToDB();
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
