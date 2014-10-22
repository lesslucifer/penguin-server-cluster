/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.mysql;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import libCore.config.Config;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 *
 * @author salm
 */
class SQLConnectionFactory extends BasePooledObjectFactory<Connection>
{
    private final String HOST;
    private final String PORT;
    private final String USER;
    private final String PASSWORD;
    private final String DB;
    
    private final String CONNECTION_STR;
    
    static
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private SQLConnectionFactory() {
        DB = Config.getParam("mysql", "database");
        PORT = Config.getParam("mysql", "port");
        HOST = Config.getParam("mysql", "host");
        USER = Config.getParam("mysql", "userName");
        PASSWORD = Config.getParam("mysql", "pass");
        
        CONNECTION_STR = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB +
                "?user=" + USER +
                "&password=" + PASSWORD +
                "&useUnicode=true&&characterEncoding=UTF-8";
                
    }
    
    public static SQLConnectionFactory getInstance() {
        return inst;
    }
    private static SQLConnectionFactory inst = new SQLConnectionFactory();
    
    public MysqlDataSource createDataSoure()
    {
        MysqlDataSource ds = new MysqlConnectionPoolDataSource();
        ds.setUrl(CONNECTION_STR);
        
        return ds;
    }

    @Override
    public Connection create() throws Exception {
        return DriverManager.getConnection(CONNECTION_STR);
    }

    @Override
    public PooledObject<Connection> wrap(Connection t) {
        return new DefaultPooledObject(t);
    }

    @Override
    public boolean validateObject(PooledObject<Connection> p) {
        try {
            return !p.getObject().isClosed() && super.validateObject(p);
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public void destroyObject(PooledObject<Connection> p) throws Exception {
        p.getObject().close();
        super.destroyObject(p);
    }
}
