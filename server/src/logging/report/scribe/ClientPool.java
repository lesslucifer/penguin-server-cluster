/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging.report.scribe;

import jcommon.transport.client.ClientFactory;
import jcommon.transport.client.TClientInfo;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TBinaryProtocol;

/**
 *
 * @author KieuAnh
 */
class ClientPool
{
    private static TClientInfo makeClientInfo()
    {
        String host = libCore.config.Config.getParam("scribelog_service", "host");
        int port = Integer.parseInt(libCore.config.Config.getParam("scribelog_service","port"));
        return ClientFactory.getClient(host, port, ScribeService.Client.class, TBinaryProtocol.class);
    }
    
    private static final ObjectPool<TClientInfo> clientPool;
    
    static
    {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(1);
        
        clientPool = new GenericObjectPool(PoolableClientFactory.inst(), config);
    }
    
    private static class PoolableClientFactory
        extends BasePooledObjectFactory<TClientInfo>
    {
        private PoolableClientFactory()
        {
            super();
        }
        
        private static final PoolableClientFactory inst = new PoolableClientFactory();
        
        public static PoolableClientFactory inst()
        {
            return inst;
        }
        
        @Override
        public TClientInfo create() throws Exception {
            return makeClientInfo();
        }

        @Override
        public PooledObject<TClientInfo> wrap(TClientInfo t) {
            return new DefaultPooledObject<TClientInfo>(t);
        }

        @Override
        public void passivateObject(PooledObject<TClientInfo> p) throws Exception {
            TClientInfo client = p.getObject();
            if (client != null)
            {
                client.cleanUp();
            }
            
            super.passivateObject(p);
        }

        @Override
        public void destroyObject(PooledObject<TClientInfo> p) throws Exception {
            TClientInfo client = p.getObject();
            if (client != null)
            {
                client.close();
            }
            
            super.destroyObject(p);
        }
    }
    
    public static ObjectPool<TClientInfo> inst()
    {
        return clientPool;
    }
}
