/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import db.mysql.SQLClient;

/**
 *
 * @author KieuAnh
 */
public class DBContext
{
    private DBContext() {}
    
    static
    {
        REDIS = PGRedisImpl.inst();
        SQL = new SQLClient();
    }
    
    private static PGRedis REDIS;
    private static SQLClient SQL;
    /**
     * @return the REDIS
     */
    public static PGRedis Redis() {
        return REDIS;
    }
    
    public static SQLClient SQL()
    {
        return SQL;
    }
    
}
