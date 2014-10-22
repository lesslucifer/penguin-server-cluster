/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging.report;

import share.StringBuilderPoolFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.json.simple.JSONValue;

/**
 *
 * @author KieuAnh
 */
public class PGRecord implements Record
{
    public static final String APP_NAME = "penguin";
    protected static final char DELIMITER = '\t';
    
    private static final GenericObjectPool<StringBuilder> SB_POOL =
            StringBuilderPoolFactory.makePool();
    
    private final PGLogCategory category;
    private final String uid;
    private final String time;
    private final long lTime;
    private final String data;
    private final String execTime;
    
    private String cachedLog;

    public PGRecord(PGLogCategory category, String uid, long time, long execTime, Object data) {
        this.category = category;
        this.uid = uid;
        this.lTime = time;
        this.time = String.valueOf(time);
        this.execTime = String.valueOf(execTime);
        this.data = JSONValue.toJSONString(data);
    }

    @Override
    public String getCategory() {
        return this.category.toString();
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public long getTime() {
        return lTime;
    }

    @Override
    public String getData() {
        return data;
    }

    public String getExecTime() {
        return execTime;
    }
    
    protected int logFields()
    {
        return 3;
    }
    
    protected int logSize()
    {
        return uid.length() + time.length();
    }
    
    protected void buildLog(StringBuilder builder)
    {
        logField(builder, APP_NAME);
        logField(builder, category);
        logField(builder, uid);
        logField(builder, time);
        logField(builder, data);
        logField(builder, execTime);
    }

    @Override
    public String toString() {
        if (this.cachedLog == null)
        {
            try {
                StringBuilder sBuilder = SB_POOL.borrowObject();
                int len = this.logSize() + this.logFields() - 1;
                sBuilder.setLength(len);
                
                buildLog(sBuilder);
                
                this.cachedLog = sBuilder.toString();
                SB_POOL.returnObject(sBuilder);
            } catch (Exception ex) {
                Logger.getLogger(PGRecord.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return cachedLog;
    }
    
    protected static void logField(StringBuilder builder, String field)
    {
        builder.append(field);
        builder.append(DELIMITER);
    }
    
    protected static void logField(StringBuilder builder, Object field)
    {
        logField(builder, field.toString());
    }
}