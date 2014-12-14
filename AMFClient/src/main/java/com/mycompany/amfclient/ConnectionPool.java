/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.amfclient;

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
    private static final ConnectionPool inst = new ConnectionPool();
    
    public static ConnectionPool inst()
    {
        return inst;
    }
    //static final String url = "http://210.211.118.28:9696/v2/logic";
    private static final String url = "http://192.168.1.108:6969/v2/logic";
    private final ObjectPool<AMFConnection> connectionPool;

    private ConnectionPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(200);
        this.connectionPool = new GenericObjectPool(ConnectionPooledFactory.inst(), config);
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
        private ConnectionPooledFactory()
        {
            super();
        }
        
        private static final ConnectionPooledFactory inst = new ConnectionPooledFactory();
        
        public static ConnectionPooledFactory inst()
        {
            return inst;
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
