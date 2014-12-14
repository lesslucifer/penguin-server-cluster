/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.quest;

import db.DBContext;
import java.util.HashMap;
import java.util.Map;
import share.PGLog;
import pgentity.CheckerLogPool;
import pgentity.EntityContext;
import share.PGHelper;

/**
 *
 * @author KieuAnh
 */
public class LoginDayChecker extends AbstractQuestChecker
{    
    private final int reqDay;

    public LoginDayChecker(int reqDay, CheckerLogPool logPool) {
        super(logPool);
        this.reqDay = reqDay;
    }
    
    @Override
    public boolean isAccept(EntityContext context)
    {
        return getLoginDay() >= reqDay;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return login day with req: %d",
                this.reqDay);
        this.destroy();
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap();
        data.put("login_day", getLoginDay());
        return data;
    }
    
    private int getLoginDay()
    {
        return PGHelper.toInteger(DBContext.Redis()
                .hget(getLogPool().getKey("login_day"), "login_day"));
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> data = new HashMap(1);
        data.put("login_day", DBContext.Redis().hgetall(getLogPool().getKey("login_day")));
        return data;
    }

    @Override
    public void destroy() {
        DBContext.Redis().del(getLogPool().getKey("login_day"));
    }
    
    public static class Factory implements QuestCheckerFactory
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
        public QuestChecker createChecker(Object data, CheckerLogPool logPool) {
            return new LoginDayChecker(PGHelper.toInteger(data), logPool);
        }
    }
}
