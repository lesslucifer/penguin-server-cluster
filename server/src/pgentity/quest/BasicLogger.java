/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import db.DBContext;
import java.util.Map;
import share.PGLog;
import pgentity.LogPool;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
class BasicLogger<E extends BasicRecord> extends GenericQuestLogger<E>
{
    private final LogPool logPool;
    private final String field;
    private final Class<E> clazz;

    public BasicLogger(LogPool logPool, Class<E> clazz, String field) {
        this.logPool = logPool;
        this.field = field;
        this.clazz = clazz;
    }
    
    @Override
    protected void genLog(E record)
    {
        if (!clazz.isInstance(record))
        {
            return;
        }
        
        RedisKey key = logPool.beginLog(field);
        DBContext.Redis().incrby(key, record.getValue());
        PGLog.info("Basic quest log: %s - inc: %d - into: %s",
                field, record.getValue(), key);
        logPool.endLog(field);
    }

    @Override
    public void restore(Map<String, Object> data) {
        String val = (String) data.get(field);
        if (val != null)
        {
            RedisKey key = logPool.beginLog(field);
            DBContext.Redis().set(key, val);
            logPool.endLog(field);
        }
    }
    
    private static class Factory<E extends BasicRecord> implements QuestLoggerFactory
    {
        private final String field;
        private final Class<E> clazz;
        
        private Factory(String field, Class<E> clazz)
        {
            super();
            
            this.field = field;
            this.clazz = clazz;
        }
        
        @Override
        public QuestLogger createLogger(LogPool logPool) {
            return new BasicLogger<E>(logPool, clazz, field);
        }
    }
    
    public static <E extends BasicRecord> QuestLoggerFactory getFactory(Class<E> clazz, String field)
    {
        return new BasicLogger.Factory<E>(field, clazz);
    }
}
