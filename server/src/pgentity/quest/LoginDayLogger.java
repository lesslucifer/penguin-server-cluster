/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import db.DBContext;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.time.DateUtils;
import pgentity.LogPool;
import share.PGHelper;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class LoginDayLogger extends GenericQuestLogger<LoginDayRecord>
{
    private final LogPool logPool;

    public LoginDayLogger(LogPool logPool) {
        this.logPool = logPool;
    }
    
    @Override
    protected void genLog(LoginDayRecord record) {
        RedisKey key = logPool.beginLog("login_day");
        
        Date lastLoginDate = new Date(getLastLogin(key));
        Date now = new Date(record.getLoginTime());
        
        if (!DateUtils.isSameDay(lastLoginDate, now))
        {
            DBContext.Redis().hincrby(key, "login_day", 1);
        }
        
        DBContext.Redis().hset(key, "last_login", String.valueOf(record.getLoginTime()));
        
        logPool.endLog("login_day");
    }
    
    private long getLastLogin(RedisKey key)
    {
        return PGHelper.toLong(DBContext.Redis().hget(key, "last_login"));
    }

    @Override
    public void restore(Map<String, Object> data) {
        Map<String, String> loginDayData = (Map) data.get("login_day");
        if (loginDayData == null)
        {
            return;
        }
        
        RedisKey key = logPool.beginLog("login_day");
        DBContext.Redis().hset(key, loginDayData);
        logPool.endLog("login_day");
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
            return new LoginDayLogger(logPool);
        }
    }
}
