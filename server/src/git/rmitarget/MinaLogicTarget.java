/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import minaconnection.MinaServerHandler;
import minaconnection.SimpleResponder;
import share.data.PGMapData;
import share.data.PGStringData;
import share.data.IPGData;
import minaconnection.interfaces.IServices;
import share.data.PGDataType;
import org.apache.mina.core.session.IoSession;
import pgentity.pool.EntityPool;

/**
 *
 * @author suaongmattroi
 */
class MinaLogicTarget {

    private static final int MAX_THREAD = 10;
    
    private final IServices services;
    
    private final MinaTargetThreadPool threadPool;
    private final SimpleResponder accepter;
    
    private final Class<?> httpClass;
    private final ThreadLocal<Object> httpTargets = new ThreadLocal<>();
    
    class MinaTargetThreadPool
    {
        private final ExecutorService pool;
        
        public MinaTargetThreadPool(int maxNOfThread)
        {
            pool = Executors.newFixedThreadPool(maxNOfThread);
        }
        
        public void execute(Runnable run) {
            pool.execute(run);
        }
        
        public void shutdown() {
            pool.shutdown();
        }
    }
    
    public MinaLogicTarget(int port, Class<?> httpClass) throws IOException {
        
        services = new ReflectAdapter();
        threadPool = new MinaTargetThreadPool(MAX_THREAD);
        accepter = new SimpleResponder(port,
            new MinaServerHandler()
            {
                @Override
                public void messageReceived(IoSession session, long index, Object message) throws Exception {
                    IPGData data = (IPGData) message;
                    String method = data.method();
                    if (data.type() == PGDataType.HTTP)
                    {
                        doHTTP(method, session, index, data);
                    }
                    else
                    {
                        doAMF(method, session, index, data);
                    }
                }
                
                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    super.exceptionCaught(session, cause);
                }
            });
        
        this.httpClass = httpClass;
    }
    
    public void doAMF(final String method, final IoSession session, final long index, final IPGData message) 
    {
        threadPool.execute(() -> {
            IPGData data = null; 
            try {
                Method m = services.getClass().getMethod(method, IPGData.class);
                data = (IPGData) m.invoke(services, message);
            }
            catch (NoSuchMethodException | SecurityException |
                    IllegalAccessException | IllegalArgumentException |
                    InvocationTargetException ex) {
                Logger.getLogger(MinaLogicTarget.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
                // Release thread res
                EntityPool.inst().releaseAllThreadResources();
                
                // Resp null if invoke fail
                if(data == null)
                {
                    data = new PGMapData(
                            message.caller(),
                            method,
                            Collections.EMPTY_MAP,
                            message.now(),
                            PGDataType.AMF);
                }
                accepter.send(session, index, data);
            }
        });
    }
    
    public void doHTTP(final String method, final IoSession session, final long index, final IPGData message) 
    {
//        threadPool.execute(() -> {
//            Object resp = null;
//            try {
//                Object httpTarget = this.httpTargets.get();
//                if (httpTarget == null)
//                {
//                    httpTarget = httpClass.newInstance();
//                    httpTargets.set(httpTarget);
//                }
//                Method proc = httpClass.method(method, Object.class);
//
//                resp = proc.invoke(httpTarget, message.getData());
//            }
//            catch (InstantiationException | IllegalAccessException |
//                    NoSuchMethodException | SecurityException |
//                    IllegalArgumentException | InvocationTargetException ex)
//            {
//                Logger.getLogger(MinaLogicTarget.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            finally
//            {
//                EntityPool.inst().releaseAllThreadResources();
//                
//                if (resp == null) {resp = "";}
//                
//                
//                session.write(new PGStringData(
//                            message.caller()),
//                            method,
//                            resp.toString(),
//                            message.now(),
//                            PGDataType.HTTP));
//            }
//        });
    }
}
