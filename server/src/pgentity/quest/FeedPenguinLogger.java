/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import db.DBContext;
import java.util.List;
import java.util.Map;
import pgentity.LogPool;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
class FeedPenguinLogger extends GenericQuestLogger<FeedPenguinRecord>
{
    private final LogPool logPool;
    
    private FeedPenguinLogger(LogPool logPool)
    {
        this.logPool = logPool;
    }
    
    @Override
    protected void genLog(FeedPenguinRecord record)
    {
        RedisKey keyNFishFed = logPool.beginLog("fish_fed");
        RedisKey keyPenguinsFed = logPool.beginLog("penguins_fed");
        
        DBContext.Redis().incrby(keyNFishFed, record.getnFish());
        DBContext.Redis().sadd(keyPenguinsFed, record.getPenguinID());
        
        logPool.endLog("fish_fed");
        logPool.endLog("penguins_fed");
    }

    @Override
    public void restore(Map<String, Object> data)
    {
        String fishFed = (String) data.get("penguin_ate");
        List<String> penguinsFed = (List) data.get("n_penguin_feed");
        
        if (fishFed == null || penguinsFed == null)
        {
            return;
        }
        
        RedisKey keyNFishFed = logPool.beginLog("fish_fed");
        RedisKey keyPenguinsFed = logPool.beginLog("penguins_fed");
        
        DBContext.Redis().set(keyNFishFed, fishFed);
        
        DBContext.Redis().del(keyPenguinsFed);
        String[] arrPenguinsFed = penguinsFed.toArray(new String[penguinsFed.size()]);
        DBContext.Redis().sadd(keyPenguinsFed, arrPenguinsFed);
        
        logPool.endLog("fish_fed");
        logPool.endLog("penguins_fed");
    }
    
    public static class Factory implements QuestLoggerFactory
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
        public QuestLogger createLogger(LogPool logPool) {
            return new FeedPenguinLogger(logPool);
        }
    }
}
