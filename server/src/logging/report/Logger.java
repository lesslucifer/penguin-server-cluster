/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package logging.report;

import java.util.List;

/**
 *
 * @author KieuAnh
 */
public interface Logger
{
    void writeLog(Record record);
    void writeLogs(List<Record> records);
}
