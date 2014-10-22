/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import share.PGError;
import share.PGHelper;
import share.StringBuilderPoolFactory;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class SelectExecutor implements SQLExecutor
{
    private final String[] columns;
    private final String query;

    private SelectExecutor(String query, String[] columns) {
        this.columns = columns;
        this.query = query;
    }
    
    public static SelectExecutor fromQuery(String query, String... cols)
    {
        return new SelectExecutor(query, cols);
    }
    
    public static SelectExecutor fromDesc(String table, String where, String... cols)
    {
        PGException.Assert(cols.length > 0, PGError.UNDEFINED, "Empty col in query");
        
        StringBuilder sb = null;
        try
        {
            sb = SQLStringBuilder.inst().borrowObject();
            sb.append("SELECT ");
            SQLUtils.joinStringWithCommaSep(sb, cols);
            
            sb.append(" FROM ").append(table);
            
            if (!PGHelper.isNullOrEmpty(where))
            {
                sb.append(" WHERE ").append(where);
            }
            
            final String query = sb.toString();
            SQLStringBuilder.inst().returnObject(sb);
            sb = null;
            
            return new SelectExecutor(query, cols);
        }
        catch (Exception ex)
        {
            PGException.pgThrow(ex, "Cannot exec SQL");
        }
        finally
        {
            if (sb != null)
            {
                SQLStringBuilder.inst().returnObject(sb);
            }
        }
        
        return null;
    }
    
    @Override
    public Object exec(Statement stm) throws SQLException {
        ResultSet rs = stm.executeQuery(query);
        try
        {
            return DataTable.createDataTable(rs, columns);
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }
    }
}
