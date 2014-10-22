/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author KieuAnh
 */
public interface SQLExecutor {
    Object exec(Statement stm) throws SQLException;
}
