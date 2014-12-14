/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author KieuAnh
 */
public class SQLBatch implements SQLBatchStatement.Manager {
    private final String query;
    private final List<SQLBgStatement> statements = new LinkedList();

    public SQLBatch(String query) {
        this.query = query;
    }

    public static SQLBatch getBatch(String query)
    {
        return new SQLBatch(query);
    }

    public void exec(SQLBackgroundExecutor exe)
    {
        exe.prepare(this);
    }
    
    public void execAllBatchs() throws SQLException
    {
        Connection conn = null;
        PreparedStatement stm = null;
        try
        {
            conn = SQLConnectionPool.get();
            stm = conn.prepareStatement(query);
            
            for (SQLBgStatement bgStm : statements) {
                bgStm.prepare(stm);
            }
            
            stm.executeBatch();
        }
        finally
        {
            if (stm != null && !stm.isClosed())
            {
                stm.close();
            }

            if (conn != null)
            {
                SQLConnectionPool.returnConnection(conn);
            }
        }
    }
    
    public int getBatchSize()
    {
        return statements.size();
    }
    
    @Override
    public SQLBatchStatement createStatement() {
        SQLBgStatement stm = new SQLBgStatement();
        this.statements.add(stm);
        
        return stm;
    }
    
    private static class SQLBgStatement implements SQLBatchStatement
    {
        private final Map<Integer, Object> params;

        private SQLBgStatement() {
            this.params = new HashMap();
        }
        
        private SQLBgStatement(int pCap)
        {
            this.params = new HashMap(pCap);
        }
        
        @Override
        public void setString(int i, String val)
        {
            params.put(i, val);
        }
        
        @Override
        public void setInt(int i, int val)
        {
            params.put(i, val);
        }
        
        @Override
        public void setLong(int i, long val)
        {
            params.put(i, val);
        }
        
        void prepare(PreparedStatement stm) throws SQLException
        {
            stm.clearParameters();
            
            for (Map.Entry<Integer, Object> paramEntry : params.entrySet()) {
                Integer index = paramEntry.getKey();
                Object val = paramEntry.getValue();
                if (val instanceof String)
                {
                    stm.setString(index, (String) val);
                }
                else if (val instanceof Integer)
                {
                    stm.setInt(index, (Integer) val);
                }
                else if (val instanceof Long)
                {
                    stm.setLong(index, (Long) val);
                }
                else
                {
                    stm.setObject(index, val);
                }
            }
            stm.addBatch();
        }
    }
}