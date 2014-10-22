/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.rmitarget;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import minaconnection.MinaServerHandler;
import minaconnection.MinaSender;
import minaconnection.SimpleResponder;
import share.data.IPGData;
import minaconnection.interfaces.IServices;
import share.data.PGDataType;
import pgentity.pool.EntityPool;
import share.data.PGObjectData;
import target.Response;

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
    
    private static class MinaTargetThreadPool
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
    
    public MinaLogicTarget(int port, Class<?> httpClass) throws IOException
    {
        this.httpClass = httpClass;
        this.services = new ReflectAdapter();
        threadPool = new MinaTargetThreadPool(MAX_THREAD);
        accepter = new SimpleResponder(port,
            new MinaServerHandler()
            {
                @Override
                public void messageReceived(MinaSender sender, Object message) throws Exception {
                    IPGData data = (IPGData) message;
                    String method = data.method();
                    if (data.type() == PGDataType.HTTP)
                    {
                        doHTTP(method, sender, data);
                    }
                    else
                    {
                        doAMF(method, sender, data);
                    }
                }
            });
        accepter.start();
    }
    
    public void doAMF(final String method, final MinaSender sender, final IPGData message) 
    {
        threadPool.execute(() -> {
            Response resp = new Response(message.now()); 
            try {
                Method m = services.getClass().getMethod(method, IPGData.class);
                IPGData data = (IPGData) m.invoke(services, message);
                resp.setData(data.data());
            }
            catch (Exception ex) {
                resp.putError(ex);
            }
            finally {
                // Release thread res
                EntityPool.inst().releaseAllThreadResources();
                
                // Resp null if invoke fail
                sender.send(resp);
            }
        });
    }
    
    public void doHTTP(final String method, final MinaSender sender, final IPGData message) 
    {
        threadPool.execute(() -> {
            Object resp = null;
            try {
                Object httpTarget = this.httpTargets.get();
                if (httpTarget == null)
                {
                    httpTarget = httpClass.newInstance();
                    httpTargets.set(httpTarget);
                }
                Method proc = httpClass.getMethod(method, Object.class);

                resp = proc.invoke(httpTarget, message.data());
            }
            catch (Exception ex)
            {
                Logger.getLogger(MinaLogicTarget.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                EntityPool.inst().releaseAllThreadResources();
                
                if (resp == null) {resp = "";}
                
                Serializable data = new PGObjectData(
                            message.caller(),
                            method,
                            resp,
                            message.now(),
                            PGDataType.HTTP);
                sender.send(data);
            }
        });
    }
}
