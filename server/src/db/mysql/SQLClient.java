/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class SQLClient implements Runnable {
    private final BackgroundSQL bgSQl = new BackgroundSQL();
    
    public Object execute(SQLExecutor exe)
    {
        Connection conn = null;
        Statement stm = null;
        try
        {
            conn = SQLConnectionPool.get();
            stm = conn.createStatement();
            return exe.exec(stm);
        }
        catch (SQLException ex) {
            PGException.pgThrow(ex);
        }
        finally
        {
            try {
                if (stm != null && !stm.isClosed())
                {
                    stm.close();
                }
                
                if (conn != null)
                {
                    SQLConnectionPool.returnConnection(conn);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
    }
    
    public void executeBackground(SQLBackgroundExecutor bgExe)
    {
        bgSQl.exec(bgExe);
    }

    @Override
    public void run() {
        bgSQl.run();
    }
}
