
import config.PGConfig;
import java.io.File;
import java.util.TimeZone;
import libCore.LogUtil;
import libCore.config.Config;
import rmitarget.Server;

public class ServiceDaemon
{
    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(ServiceDaemon.class);

    public static void main(String[] args) throws Exception
    {
        LogUtil.init();
	String pidFile = System.getProperty("pidfile");	
        // For linux service running mode
        if (pidFile != null)
            new File(pidFile).deleteOnExit();

        // Set default time zone
        _log.info("Home: " + Config.getHomePath());
        TimeZone.setDefault(TimeZone.getTimeZone(Config.getParam("time_zone", "tz")));

        PGConfig.inst();
        
        Server.inst().start();
    }
}