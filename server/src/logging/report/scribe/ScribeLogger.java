/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging.report.scribe;

import java.util.ArrayList;
import java.util.List;
import jcommon.transport.client.TClientInfo;
import logging.report.Logger;
import logging.report.Record;

/**
 *
 * @author KieuAnh
 */
public class ScribeLogger implements Logger
{    
    private static final org.apache.log4j.Logger LOG =
            org.apache.log4j.Logger.getLogger(ScribeLogger.class.getName());

    private ScribeLogger()
    {
        super();
    }
    
    private static final ScribeLogger inst = new ScribeLogger();
    
    public static ScribeLogger inst()
    {
        return inst;
    }
    
    @Override
    public void writeLog(Record record)
    {
        List<Record> singleRecord = new ArrayList(1);
        singleRecord.add(record);
        
        writeLogs(singleRecord);
    }
    
    @Override
    public void writeLogs(List<Record> records) {
        List<LogEntry> logEntries = entriesFromRecords(records);
        if (logEntries.isEmpty())
        {
            return;
        }
        
        TClientInfo client = null;
        try
        {
            client = ClientPool.inst().borrowObject();
            
            ScribeService.Client scribeClient = client.getClientT();
            scribeClient.Log2(logEntries);
        }
        catch (Exception ex) {
            LOG.warn(ex);
        }
        finally
        {
            if (client != null)
            {
                try {
                    ClientPool.inst().returnObject(client);
                } catch (Exception ex) {
                    LOG.warn(ex);
                }
            }
        }
    }
    
    private List<LogEntry> entriesFromRecords(List<Record> records)
    {
        List<LogEntry> entries = new ArrayList(records.size());
        for (Record record : records) {
            entries.add(new LogEntry(record.getCategory(), record.toString()));
        }
        
        return entries;
    }
}
