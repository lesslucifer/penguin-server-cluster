/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.services.penguin;

import pgentity.services.PenguinUpdator;
import config.PGConfig;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.quest.QuestLogger;
import pgentity.services.PenguinServices;

/**
 *
 * @author Salm
 */
class PenguinEatUpdator implements PenguinUpdator {
    
    private final Penguin penguin;
    private final EntityContext context;
    private final QuestLogger uQLogger;
    
    private long nextEatTime;

    public PenguinEatUpdator(Penguin penguin, EntityContext context,
            QuestLogger uQLogger) {
        this.penguin = penguin;
        this.context = context;
        this.uQLogger = uQLogger;
        
        this.nextEatTime = calcNextEatTime();
    }
    
    @Override
    public long nextActionTime() {
        return nextEatTime;
    }
    
    @Override
    public boolean update()
    {
        if (context.getCote().getPoolFish() > 0)
        {
            PenguinServices.inst().eat(penguin, context, uQLogger, nextEatTime);
            this.nextEatTime = calcNextEatTime();
            return true;
        }

        return false;
    }
    
    @Override
    public void save()
    {
        this.penguin.saveToDB();
    }
    
    private long calcNextEatTime()
    {
        if (PenguinServices.inst().configOf(penguin).getFeed() > 0)
        {
            float eatRed = PGConfig.inst().getCote()
                    .get(context.getCote().getLevel()).getEatTimeReduce();
            long eatTime = (long) ((1f - eatRed) * penguin.getFood() * PGConfig.inst().temp().DigestTimePerFish());
            eatTime *= 1000L; // convert to milsec
            
            return penguin.getLastEat() + eatTime;
        }
        
        return Long.MAX_VALUE;
    }
}
