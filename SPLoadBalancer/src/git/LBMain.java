package  git;

import java.io.File;
import java.util.TimeZone;
import libCore.config.Config;
import libCore.LogUtil;
import git.webserver.WebServer;

public class LBMain
{
    private static final org.apache.log4j.Logger LOG =
            org.apache.log4j.Logger.getLogger(LBMain.class);

    public static void main(String[] args) throws Exception
    {
        LogUtil.init();
	String pidFile = System.getProperty("pidfile");	
	try
	{
	    // For linux service running mode
	    if (pidFile != null)
		new File(pidFile).deleteOnExit();
	    
	    // Set default time zone
	    TimeZone.setDefault(TimeZone.getTimeZone(Config.getParam("time_zone", "tz")));
            
            System.out.println("Server is started for listen...");
            System.out.println("===============================");
            
	    //start thrirt read
	    WebServer.getSingleton().start();
	}
	catch (Throwable e)
	{
	    String msg = "Exception encountered during startup.";
	    LOG.error(msg, e);

	    // try to warn user on stdout too, if we haven't already detached
	    System.out.println(msg);
	    LOG.error("Uncaught exception: " + e.getMessage());

	    System.exit(3);
	}
    }
}