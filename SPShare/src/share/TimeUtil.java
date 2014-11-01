/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Some utility methods for time process
 * @author LinhTA
 */
public class TimeUtil
{
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TimeUtil.class.getName());
    
    public static final int DAY_SECS = 24*60*60;
    
    static
    {
       
		 
        DateFormat dateTimeF = new SimpleDateFormat("yyMMdd_HHmmss");
        dateTimeFormatter = dateTimeF;
        
        DateFormat dayTokenF = new SimpleDateFormat("yyyyMMdd");
        dayTokenFormatter = dayTokenF;
        
        DateFormat dayF = new SimpleDateFormat("dd/MM/yyyy");
        dateFormatter = dayF;
    }
    
    private static final DateFormat dateTimeFormatter;
    public static String formatDateTime(Date date)
    {
        return dateTimeFormatter.format(date);
    }
    
    private static final DateFormat dateFormatter;
    public static String formatDate(Date date)
    {
        return dateFormatter.format(date);
    }
    
    public static Date getDate(String s)
    {
        try {
            return dateFormatter.parse(s);
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static int diffDays(long last, long now)
    {
        Date lastDay = getMidnight(last);
        Date currentDay = getMidnight(now);
		return (int) ((currentDay.getTime() - lastDay.getTime()) / 86400000L);
    }
    
    public static Date getMidnight(long now)
    {
        Date midNight = new Date(now);
        midNight = DateUtils.setHours(midNight, 0);
        midNight = DateUtils.setMinutes(midNight, 0);
        midNight = DateUtils.setSeconds(midNight, 0);
        midNight = DateUtils.setMilliseconds(midNight, 0);
        
        return midNight;
    }
    
    private static final DateFormat dayTokenFormatter;
    public static String dayToken(long now)
    {
        Date toDay = new Date(now);
        return dayTokenFormatter.format(toDay);
    }
    
    public static long tomorowTime(String dayToken)
    {
        try {
            Date toDay = dayTokenFormatter.parse(dayToken);
            Date tomorow = DateUtils.addDays(toDay, 1);
            
            return tomorow.getTime();
        } catch (ParseException ex) {
            Logger.getLogger(TimeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    public static long now()
    {
        return Calendar.getInstance(TimeZone.getDefault()).getTime().getTime();
    }
}
