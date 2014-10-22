/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import share.StringBuilderPoolFactory;

/**
 *
 * @author KieuAnh
 */
public class SQLUtils {
    private SQLUtils() {}
    
    static void joinStringWithCommaSep(StringBuilder sb, String[] data)
    {
        if (data.length > 0)
        {
            for (int i = 0; i < data.length - 1; ++i)
            {
                sb.append(data[i]);
                sb.append(',');
            }
            
            sb.append(data[data.length - 1]);
        }
    }
    
    static void joinObjWithCommaSep(StringBuilder sb, List<Object> data)
    {
        if (data.size() > 0)
        {
            for (int i = 0; i < data.size() - 1; ++i)
            {
                appendObj(sb, data.get(i));
                sb.append(',');
            }
            
            appendObj(sb, data.get(data.size() - 1));
        }
    }
    
    private static void appendObj(StringBuilder sb, Object obj)
    {
        if (obj instanceof String)
        {
            sb.append("'").append(obj).append("'");
        }
        else
        {
            sb.append(obj);
        }
    }
    
    public static String buildGroupData(Object... vals)
    {
        return buildListData(Arrays.asList(vals));
    }
    
    public static String buildListData(List<Object> vals)
    {
        StringBuilder sb = null;
        try
        {
            sb = SQLStringBuilder.inst().borrowObject();
            sb.append('(');
            joinObjWithCommaSep(sb, vals);
            sb.append(')');
            
            return sb.toString();
        }
        catch (Exception ex) {
            Logger.getLogger(SQLUtils.class.getName()).log(Level.SEVERE, null, ex);
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
}
