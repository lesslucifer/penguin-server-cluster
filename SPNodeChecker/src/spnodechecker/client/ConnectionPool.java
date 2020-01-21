/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spnodechecker.client;

import flex.messaging.io.amf.client.AMFConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 *
 * @author KieuAnh
 */
public class ConnectionPool {
    
    private final ObjectPool<AMFConnection> connectionPool;

    public ConnectionPool(String url) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(200);
        this.connectionPool = new GenericObjectPool(new ConnectionPooledFactory(url), config);
    }
    
    public AMFConnection get()
    {
        try {
            return connectionPool.borrowObject();
        } catch (Exception ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public void returnConnection(AMFConnection conn)
    {
        try {
            connectionPool.returnObject(conn);
        } catch (Exception ex) {
            Logger.getLogger(ConnectionPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static class ConnectionPooledFactory extends 
            BasePooledObjectFactory<AMFConnection>
    {
        private final String url;
        
        private ConnectionPooledFactory(String url)
        {
            super();
            
            this.url = url;
        }
        
        @Override
        public AMFConnection create() throws Exception {
            AMFConnection connection = new AMFConnection();
            connection.connect(url);
            
            return connection;
        }

        @Override
        public PooledObject<AMFConnection> wrap(AMFConnection obj) {
            return new DefaultPooledObject(obj);
        }

        @Override
        public void destroyObject(PooledObject<AMFConnection> p) throws Exception {
            p.getObject().close();
            super.destroyObject(p);
        }
    }
}
