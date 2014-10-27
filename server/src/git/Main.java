package git;

import config.PGConfig;
import java.io.File;
import java.util.TimeZone;
import libCore.LogUtil;
import libCore.config.Config;
import git.rmitarget.*;
import share.PGHelper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KieuAnh
 */
public class Main
{
    private static final org.apache.log4j.Logger LOG =
            org.apache.log4j.Logger.getLogger(Main.class);

    public static void main(String[] args) throws Exception
    {
        LogUtil.init();
	String pidFile = System.getProperty("pidfile");	
        // For linux service running mode
        if (pidFile != null)
            new File(pidFile).deleteOnExit();

        // Set default time zone
        TimeZone.setDefault(TimeZone.getTimeZone(Config.getParam("time_zone", "tz")));

        System.out.println("Server is started for listen...");
        System.out.println("===============================");

        PGConfig.inst();
        
        int port = PGHelper.toInteger(Config.getParam("logic", "port"));
        Server.create(port).start();
    }
}