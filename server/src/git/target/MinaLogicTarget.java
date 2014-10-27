/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import minaconnection.MinaServerHandler;
import minaconnection.MinaSender;
import minaconnection.SimpleResponder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import pgentity.pool.EntityPool;
import target.Request;
import target.RequestType;
import target.Response;

/**
 *
 * @author suaongmattroi
 */
class MinaLogicTarget {

    private static final int MAX_THREAD = 10;
    
    private final QueuedThreadPool exec = new QueuedThreadPool(MAX_THREAD);
    private final SimpleResponder accepter;
    
    private final Class<?> httpClass;
    private final ThreadLocal<Object> httpTargets = new ThreadLocal<>();
    
    private final Class<?> amfClass;
    private final ThreadLocal<Object> amfTargets = new ThreadLocal<>();
    
    
    public MinaLogicTarget(int port, Class<?> amfClass, Class<?> httpClass)
            throws IOException, Exception
    {
        this.amfClass = amfClass;
        this.httpClass = httpClass;
        accepter = new SimpleResponder(port,
            new MinaServerHandler()
            {
                @Override
                public void messageReceived(MinaSender sender, Object message) throws Exception {
                    Request req = (Request) message;
                    String method = req.getMethod();
                    if (req.getType() == RequestType.HTTP)
                    {
                        doHTTP(req, sender);
                    }
                    else
                    {
                        doAMF(req, sender);
                    }
                }
            });
        accepter.start();
        exec.start();
    }
    
    public void doAMF(final Request req, final MinaSender sender) 
    {
        exec.execute(() -> {
            Response resp = new Response(req.getNow());
            try {
                Method m = amfClass.getMethod(req.getMethod(),
                    String.class, Map.class, long.class);
                Object data = m.invoke(getAMFTarget(), req.getCaller(),
                        req.getParams(), req.getNow());
                resp.setData(data);
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
    
    public void doHTTP(final Request req, final MinaSender sender) 
    {
        exec.execute(() -> {
            Response resp = new Response(req.getNow());
            try {
                Method proc = httpClass.getMethod(req.getMethod(), Object.class);
                Object data = proc.invoke(getHTTPTarget(), req.getParams());
                resp.setData(data);
            }
            catch (Exception ex)
            {
                resp.putError(ex);
                Logger.getLogger(MinaLogicTarget.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally
            {
                EntityPool.inst().releaseAllThreadResources();
                sender.send(resp);
            }
        });
    }
    
    private Object getTarget(ThreadLocal<Object> targets, Class<?> factory)
            throws Exception
    {
        Object target = targets.get();
        if (target == null)
        {
            target = factory.newInstance();
            targets.set(target);
        }
        
        return target;
    }
    
    private Object getAMFTarget() throws Exception
    {
        return getTarget(amfTargets, amfClass);
    }
    
    private Object getHTTPTarget() throws Exception
    {
        return getTarget(httpTargets, httpClass);
    }
}
