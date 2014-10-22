/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging.report;

import db.DBContext;
import db.mysql.SQLBackgroundExecutor;
import db.mysql.SQLBatchStatement;
import java.util.List;

/**
 *
 * @author KieuAnh
 */
public class SQLLogger implements Logger
{
    private static final String INSERT_QUERY = "INSERT INTO `LOG` (category, uid, time, data) VALUES (?, ?, ?, ?)";
    
    @Override
    public void writeLog(final Record record) {
        DBContext.SQL().executeBackground(new SQLBackgroundExecutor() {
            @Override
            public String queryToken() {
                return INSERT_QUERY;
            }

            @Override
            public int batchSizeLimit() {
                return 5000;
            }

            @Override
            public void prepare(SQLBatchStatement.Manager batch) {
                SQLBatchStatement stm = batch.createStatement();
                stm.setString(1, record.getCategory());
                stm.setString(2, record.getUid());
                stm.setLong(3, record.getTime());
                stm.setString(4, record.getData());
            }
        });
    }

    @Override
    public void writeLogs(List<Record> records) {
        for (Record record : records) {
            writeLog(record);
        }
    }
}
