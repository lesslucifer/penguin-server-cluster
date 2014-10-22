/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import share.PGException;

/**
 *
 * @author salm
 */
class SQLConnectionPool
{
    private SQLConnectionPool() {}
    
    //private static final ObjectPool<Connection> pool;
    private static final MysqlDataSource dataSource;
    
    static
    {
        //int timeOut = PGHelper.toInteger(Config.getParam("mysql", "timeout"));
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(1);
        config.setMaxIdle(4);
        config.setTestOnBorrow(true);
        config.setTestOnCreate(true);
        config.setTestOnReturn(true);
        //pool = new GenericObjectPool<Connection>(SQLConnectionFactory.getInstance(), config);
        
        dataSource = SQLConnectionFactory.getInstance().createDataSoure();
    }
    
    public static Connection get()
    {
        
        try {
            //return pool.borrowObject();
            return dataSource.getConnection();
        } catch (Exception ex) {
            PGException.pgThrow(ex, "Cannot get SQL connection");
        }
        
        return null;
    }
    
    public static void returnConnection(Connection conn)
    {
        try {
            //pool.returnObject(conn);
            conn.close();
        } catch (Exception ex) {
            PGException.pgThrow(ex, "Error when return SQL connection");
        }
    }
}
