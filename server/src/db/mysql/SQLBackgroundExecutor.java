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
public interface SQLBackgroundExecutor
{
    String queryToken();
    int batchSizeLimit();
    void prepare(SQLBatchStatement.Manager batch);
}
