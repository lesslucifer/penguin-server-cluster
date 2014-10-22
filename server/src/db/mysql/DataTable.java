/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author salm
 */
public class DataTable extends ArrayList<DataRow>
{   
    public static DataTable createDataTable(ResultSet rs, String ... columns) throws SQLException
    {
        DataTable dt = new DataTable();
        
        while (rs.next())
        {
            DataRow row = new DataRow(columns.length);
            
            for (String col : columns)
            {
                row.put(col, rs.getString(col));
            }
            
            dt.add(row);
        }
        
        return dt;
    }
}