package webserver;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import libCore.config.Config;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @author linhta
public class WebServer
{
    private static ReentrantLock lock;
    private static WebServer singleton;

    static
    {
	lock = new ReentrantLock();
    }

    public static WebServer getSingleton()
    {
	if (singleton == null)
	{
	    lock.lock();
	    try
	    {
                if (singleton == null)
                {
                    singleton = new WebServer();
                }
	    }
	    finally
	    {
		lock.unlock();
	    }
	}

	return singleton;
    }
    
    //=====================
    private QueuedThreadPool threadPool;
    private Server server;

    public void start() throws Exception
    {
	threadPool = new QueuedThreadPool();
	threadPool.setMinThreads(100);
	threadPool.setMaxThreads(2000);
	server = new Server();
	server.setThreadPool(threadPool);

	SelectChannelConnector connector = new SelectChannelConnector();
	int port = Integer.valueOf(Config.getParam("rest", "port_listen"));
	connector.setPort(port);
	connector.setMaxIdleTime(60000);
	connector.setStatsOn(false);
	connector.setLowResourcesConnections(20000);
	connector.setLowResourcesMaxIdleTime(5000);
	server.setConnectors(new Connector[]
	{
	    connector
	});

	//====================================
	// Setup servlet controller

        ServletHandler servlet = new ServletHandler();
        servlet.addServletWithMapping("SERVLET.PAYMENT.ZingController", "/payment/zing");
        servlet.addServletWithMapping("SERVLET.PAYMENT.ZingController", "/payment/zing/*");
        servlet.addServletWithMapping("SERVLET.PAYMENT.ZingBilling", "/payment/zingbilling");
        servlet.addServletWithMapping("SERVLET.PAYMENT.ZingBilling", "/payment/zingbilling/*");
        servlet.addServletWithMapping("SERVLET.PAYMENT.BillingItem", "/payment/ajaxbillitem/*");
        servlet.addServletWithMapping("SERVLET.PAYMENT.BillingItem", "/payment/ajaxbillitem");
        
        // HTTP SERVICES
        servlet.addServletWithMapping("httpservices.WhiteList", "/httpservices/whitelist/*");
        servlet.addServletWithMapping("httpservices.WhiteList", "/httpservices/whitelist");
        
	// Static resource handler
	ResourceHandler resCtrl = new ResourceHandler();
	resCtrl.setResourceBase(System.getProperty("apppath") + File.separator + "amfservices" + File.separator + "statics");

	// Amf gateway
	WebAppContext afmContext = new WebAppContext();
	String webapp = System.getProperty("apppath") + File.separator + "amfservices" + File.separator + "conf";
	afmContext.setResourceBase(webapp);
	afmContext.setDescriptor(webapp + File.separator + "blazeds.xml");
	afmContext.setContextPath("/v2");
	afmContext.setServer(server);
	afmContext.setParentLoaderPriority(true);
	server.setHandler(afmContext);

	// Append handler
	HandlerCollection handlers = new HandlerCollection();
	handlers.setHandlers(new Handler[]
	{
	    resCtrl, afmContext, servlet
	});
	server.setHandler(handlers);
        
	server.setStopAtShutdown(true);

	server.start();
	server.join();

	Logger log = LoggerFactory.getLogger(WebServer.class);
	log.info("==============================");
	log.info("Webserver is starting to listen");
    }

    public String printStats()
    {
	StringBuilder sb = new StringBuilder();

	sb.append("minThreads: ").append(threadPool.getMinThreads()).append(" | ");
	sb.append("maxThreads: ").append(threadPool.getMaxThreads()).append("</br>");
	sb.append("Threads: ").append(threadPool.getThreads()).append(" | ");
	sb.append("Idle threads: ").append(threadPool.getIdleThreads());

	return sb.toString();
    }
}