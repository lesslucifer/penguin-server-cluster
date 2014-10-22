/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

/**
 *
 * @author KieuAnh
 */
public interface SQLBatchStatement {
    void setString(int i, String val);
    void setInt(int i, int val);
    void setLong(int i, long val);
    
    public static interface Manager
    {
        SQLBatchStatement createStatement();
    }
}
