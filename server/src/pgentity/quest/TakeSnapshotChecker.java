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
public class TakeSnapshotChecker extends AbstractQuestChecker
{
    private int nReq = 0;
    
    private TakeSnapshotChecker(int nReq, CheckerLogPool logPool)
    {
        super(logPool);
        this.nReq = nReq;
    }
    
    @Override
    public boolean isAccept(EntityContext context)
    {
        return nShapshotTaken() >= nReq;
    }
    
    private int nShapshotTaken()
    {
        String strSnapshot = DBContext.Redis().get(getLogPool().getKey("take_snapshot"));
        int currentSnapshot = 0;
        if (strSnapshot != null)
        {
            currentSnapshot = Integer.valueOf(strSnapshot);
        }
        
        return currentSnapshot;
    }

    @Override
    public Map<String, Object> buildAMF(EntityContext context)
    {
        Map<String, Object> data = new HashMap(1);
        data.put("take_snapshot", this.nShapshotTaken());
        
        return data;
    }

    @Override
    public void returnQuest(EntityContext context)
    {
        PGLog.debug("Return quest take %d snapshot", this.nReq);
        this.destroy();
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> data = new HashMap(1);
        data.put("take_snapshot", DBContext.Redis().get(getLogPool().getKey("take_snapshot")));
        
        return data;
    }

    @Override
    public void destroy() {
        DBContext.Redis().del(getLogPool().getKey("take_snapshot"));
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
        public QuestChecker createChecker(Object data, CheckerLogPool logPool)
        {
            return new TakeSnapshotChecker(PGHelper.toInteger(data), logPool);
        }
        
    }
}
