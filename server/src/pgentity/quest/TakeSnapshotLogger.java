/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import db.DBContext;
import java.util.Map;
import pgentity.LogPool;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
class TakeSnapshotLogger extends GenericQuestLogger<TakeSnapshotQuestRecord>
{
    private final LogPool logPool;
    
    private TakeSnapshotLogger(LogPool logPool)
    {
        this.logPool = logPool;
    }
    
    @Override
    protected void genLog(TakeSnapshotQuestRecord record)
    {
        RedisKey key = logPool.beginLog("take_snapshot");
        DBContext.Redis().incr(key);
        logPool.endLog("take_snapshot");
    }

    @Override
    public void restore(Map<String, Object> data) {
        RedisKey key = logPool.beginLog("take_snapshot");
        DBContext.Redis().set(key, (String) data.get("take_snapshot"));
        logPool.endLog("take_snapshot");
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
        public QuestLogger createLogger(LogPool logPool)
        {
            return new TakeSnapshotLogger(logPool);
        }
    }
}
