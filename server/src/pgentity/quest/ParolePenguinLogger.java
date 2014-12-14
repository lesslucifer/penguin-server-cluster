/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import config.PGConfig;
import db.DBContext;
import db.RedisKey;
import java.util.Map;
import pgentity.LogPool;

/**
 *
 * @author KieuAnh
 */
public class ParolePenguinLogger extends GenericQuestLogger<ParolePenguinRecord>
{
    private final LogPool logPool;

    public ParolePenguinLogger(LogPool logPool) {
        this.logPool = logPool;
    }
    
    @Override
    protected void genLog(ParolePenguinRecord record) {
        if (record.getPenguinLevel() >= PGConfig.inst().temp().AdultPenguin_Level())
        {
            RedisKey key = this.logPool.beginLog("parole_penguin");
            DBContext.Redis().incr(key);
        }
    }

    @Override
    public void restore(Map<String, Object> data) {
        if (data.containsKey("parole_penguin"))
        {
            String nParoled = data.get("parole_penguin").toString();
            RedisKey key = this.logPool.beginLog("parole_penguin");
            DBContext.Redis().set(key, nParoled);
        }
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
            return new ParolePenguinLogger(logPool);
        }
    }
}
