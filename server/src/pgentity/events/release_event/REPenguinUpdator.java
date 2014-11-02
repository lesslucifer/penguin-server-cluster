/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.events.release_event;

import config.CFReleaseEvent;
import config.PGConfig;
import pgentity.services.PenguinUpdator;

class REPenguinUpdator implements PenguinUpdator
{
    private final RECote cote;
    private final REPenguin penguin;
    
    private long nextSpawnTime;
    private String lastSpawnedItem;
    private CFReleaseEvent.Penguin.Level config;
    
    public REPenguinUpdator(RECote cote, REPenguin penguin, long now) {
        this.cote = cote;
        this.penguin = penguin;
        this.updateConfig();
        this.updateTime(now);
        this.nextSpawnTime = calcNextSpawnTime();
        this.lastSpawnedItem = null;
    }
    
    private void updateTime(long now)
    {
        if (config != null && penguin.getLastSpawnTime() <= 0)
        {
            penguin.setLastSpawnTime(now);
        }
    }
    
    private void updateConfig()
    {
        try
        {
            this.config = PGConfig.inst()
                .releaseEvent().getPenguins()
                .get(penguin.getPenguin().getKind())
                .getLevel(penguin.getPenguin().getLevel());
        }
        catch (Exception ex)
        {
            this.config = null;
        }
    }

    @Override
    public long nextActionTime() {
        return this.nextSpawnTime;
    }
    
    @Override
    public boolean update()
    {
        if (config != null)
        {
            boolean isSpawnOK = Math.random() < config.getRate();
            if (isSpawnOK)
            {
                this.lastSpawnedItem = config.getItem();
                cote.Items.addEgg(lastSpawnedItem);
            }
            else
            {
                this.lastSpawnedItem = null;
            }
            
            penguin.setLastSpawnTime(nextSpawnTime);
            
            this.updateConfig();
            this.nextSpawnTime = calcNextSpawnTime();
            return true;
        }
        
        return false;
    }
    
    @Override
    public void save()
    {
        this.penguin.saveToDB();
    }
    
    private long calcNextSpawnTime()
    {
        if (config != null)
        {
            return penguin.getLastSpawnTime() + config.getTime();
        }
        
        return Long.MAX_VALUE;
    }

    public String getLastSpawnedItem() {
        return lastSpawnedItem;
    }
    
    public String getPenguinID()
    {
        return this.penguin.getPenguin().getPenguinID();
    }
}
