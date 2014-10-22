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
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
public class NumberFishFedChecker extends AbstractQuestChecker
{
    private int nReq;

    public NumberFishFedChecker(int nReq, CheckerLogPool logPool) {
        super(logPool);
        this.nReq = nReq;
    }

    @Override
    public boolean isAccept(EntityContext context)
    {
        return this.nFishFed() >= nReq;
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap(1);
        data.put("penguin_ate",
                DBContext.Redis().get(getLogPool().getKey("fish_fed")));
        
        return data;
    }
    
    private int nFishFed()
    {
        return PGHelper.toInteger(DBContext.Redis().get(getLogPool().getKey("fish_fed")));
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return quest fed %d fish", this.nReq);
        this.destroy();
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> data = new HashMap(1);
        data.put("fish_fed", DBContext.Redis().get(getLogPool().getKey("fish_fed")));
        
        return data;
    }

    @Override
    public void destroy() {
        DBContext.Redis().del(getLogPool().getKey("fish_fed"));
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
            return new NumberFishFedChecker(PGHelper.toInteger(data), logPool);
        }
    }
}
