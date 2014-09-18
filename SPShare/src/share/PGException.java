/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import share.PGConst;
import share.PGError;

/**
 *
 * @author KieuAnh
 */
public class PGException extends RuntimeException
{
    private int errorCode;
    
    public PGException(int error_code, String message)
    {
        super(message);
        this.errorCode = error_code;
    }
    
    public PGException(int error_code)
    {
        this.errorCode = error_code;
    }
    
    public PGException(String message)
    {
        super(message);
        this.errorCode = PGError.UNDEFINED;
    }
    
    public PGException()
    {
        this.errorCode = PGError.UNDEFINED;
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public static void Assert(boolean ass, int error_code, String message) throws PGException
    {
        if (!ass)
        {
            throw new PGException(error_code, message);
        }
    }
    
    public static void pgThrow(Exception ex)
    {
        if (ex instanceof PGException)
        {
            throw (PGException) ex;
        }
        else if (ex.getCause() instanceof PGException)
        {
            throw (PGException) ex.getCause();
        }
        else
        {
            throw new PGException(PGError.UNDEFINED, "Error: " + ex);
        }
    }
    
    public static void pgThrow(Exception ex, String msg)
    {
        if (ex instanceof PGException)
        {
            throw (PGException) ex;
        }
        else if (ex.getCause() instanceof PGException)
        {
            throw (PGException) ex.getCause();
        }
        else
        {
            throw new PGException(PGError.UNDEFINED, msg);
        }
    }
}