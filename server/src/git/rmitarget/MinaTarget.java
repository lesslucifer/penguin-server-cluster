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
import minaconnection.SimpleResponder;
import minaconnection.data.impl.PGMapData;
import minaconnection.handler.SimpleIoHandler;
import minaconnection.interfaces.IPGData;
import minaconnection.interfaces.IServices;
import org.apache.mina.core.session.IoSession;
import pgentity.pool.EntityPool;
import target.Request;

/**
 *
 * @author suaongmattroi
 */
class MinaTarget implements target.Target{

    private static final int MAX_THREAD = 10;
    
    private final IServices services;
    
    private final MinaTargetThreadPool threadPool;
    private final SimpleResponder accepter;
    
    class MinaTargetThreadPool
    {
        private ExecutorService pool;
        
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
    
    public MinaTarget(int port) throws IOException {
        
        services = new ReflectAdapter();
        threadPool = new MinaTargetThreadPool(MAX_THREAD);
        accepter = new SimpleResponder(port,
            new SimpleIoHandler()
            {
                @Override
                public void messageReceived(IoSession session, Object message) throws Exception {
                    IPGData data = (IPGData) message;
                    String method = data.getMethod();
                    doAMF(method, session, data);
                }
                
                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    super.exceptionCaught(session, cause);
                }
            });
    }
    
    public void doAMF(final String method, final IoSession session, final IPGData message) 
    {
        Thread t = new Thread() {
            @Override 
            public void run() {
                IPGData data = null;
                try {
                    Method m = services.getClass().getMethod(method, IPGData.class);
                    data = (IPGData) m.invoke(services, message);
                } 
                catch (NoSuchMethodException ex) {
                    Logger.getLogger(MinaTarget.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(MinaTarget.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(MinaTarget.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(MinaTarget.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(MinaTarget.class.getName()).log(Level.SEVERE, null, ex);
                } 
                finally {
                    // Resp null if invoke fail
                    if(data == null)
                        data = new PGMapData(message.getIndex(), message.getCaller(), method, Collections.EMPTY_MAP, message.getNow());
                    session.write(data);

                    // Release thread res
                    EntityPool.inst().releaseAllThreadResources();
                }
            }
        };
        if(threadPool != null)
        {
            threadPool.execute(t);
        }
    }
    
    @Override
    public Object doAMF(Request request) throws InvocationTargetException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object doHTTP(Request request) throws InvocationTargetException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
