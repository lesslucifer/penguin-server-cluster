/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import share.PGHelper;
import share.StringBuilderPoolFactory;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class DeleteExecutor implements SQLExecutor {
    private final String query;

    private DeleteExecutor(String query) {
        this.query = query;
    }
    
    public static DeleteExecutor fromDesc(String table, String where)
    {
        StringBuilder sb = null;
        try
        {
            sb = SQLStringBuilder.inst().borrowObject();
            sb.append("DELETE FROM ").append(table);
            
            if (!PGHelper.isNullOrEmpty(where))
            {
                sb.append(" WHERE ").append(where);
            }
            
            final String query = sb.toString();
            SQLStringBuilder.inst().returnObject(sb);
            sb = null;
            
            return new DeleteExecutor(query);
        }
        catch (Exception ex)
        {
            PGException.pgThrow(ex, "Cannot exec SQL");
        }
        finally
        {
            if (sb != null)
            {
                try {
                    SQLStringBuilder.inst().returnObject(sb);
                } catch (Exception ex) {
                    Logger.getLogger(DeleteExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return null;
    }
    
    @Override
    public Object exec(Statement stm) throws SQLException {
        return stm.execute(query);
    }
}
