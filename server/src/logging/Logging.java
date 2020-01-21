/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging;

import logging.report.SQLLogger;
import logging.report.SQLLogger;

/**
 *
 * @author KieuAnh
 */
public class Logging {
    private static final logging.report.Logger REPORTER = new SQLLogger();
    
    public static void log(logging.report.Record record)
    {
        REPORTER.writeLog(record);
    }
}
