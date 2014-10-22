/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import org.apache.log4j.Logger;

/**
 *
 * @author KieuAnh
 */
public class PGLog
{
    private PGLog() {}
    
    public static final Logger LOG = Logger.getLogger("serviceLogger");
    
    public static void info(String format, Object... args)
    {
        String logMsg = String.format(format, args);
        LOG.info(logMsg);
    }
    
    public static void debug(String format, Object... args)
    {
        String logMsg = String.format(format, args);
        LOG.debug(logMsg);
    }
}