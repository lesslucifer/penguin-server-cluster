/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package target;

import java.io.Serializable;
import java.util.Map;
import share.AMFBuilder;
import share.PGException;
import share.PGHelper;

/**
 *
 * @author Salm
 */
public class Response implements Serializable {
    public static boolean VERBOSE_LOG = false;
    
    private final long timeServer;
    private Object data;
    private String message;
    private int error;

    public Response(long timeServer) {
        this.timeServer = timeServer;
    }

    public long getTimeServer() {
        return timeServer;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public int getError_code() {
        return error;
    }
    
    public void putError(Exception ex)
    {
        if (ex == null) return;
        
        this.putErrorResc(ex);
        if (VERBOSE_LOG)
        {
            this.message = PGHelper.stackTrace(ex.getStackTrace());
        }
        else
        {
            this.message = ex.getMessage();
        }
    }
    
    private void putErrorResc(Throwable t)
    {
        if (t == null) return;
        
        if (t instanceof PGException)
        {
            PGException pgEx = (PGException) t;
            this.error = pgEx.getErrorCode();
        }
        else
        {
            this.putErrorResc(t.getCause());
        }
    }
    
    public Map<String, Object> toAMF()
    {
        return AMFBuilder.make("timeServer", timeServer,
                "error", error,
                "message", message,
                "data", data
        );
    }
}
