package git.webserver;

import libCore.config.Config;
import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
		singleton = new WebServer();
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
        servlet.addServletWithMapping("git.httpservices.ZingController", "/payment/zing");
        servlet.addServletWithMapping("git.httpservices.ZingController", "/payment/zing/*");
        servlet.addServletWithMapping("git.httpservices.ZingBilling", "/payment/zingbilling");
        servlet.addServletWithMapping("git.httpservices.ZingBilling", "/payment/zingbilling/*");
        servlet.addServletWithMapping("git.httpservices.BillingItem", "/payment/ajaxbillitem/*");
        servlet.addServletWithMapping("git.httpservices.BillingItem", "/payment/ajaxbillitem");
        
        // HTTP SERVICES
        servlet.addServletWithMapping("git.httpservices.WhiteList", "/httpservices/whitelist/*");
        servlet.addServletWithMapping("git.httpservices.WhiteList", "/httpservices/whitelist");
        
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

        
        
	//==========
	// Init server data

	//
	Logger log = LoggerFactory.getLogger(WebServer.class);
	log.info("==============================");
	log.info("Webserver is starting to listen");

	server.start();
	server.join();
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