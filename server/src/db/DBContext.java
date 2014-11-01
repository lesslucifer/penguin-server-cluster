/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db;

import db.mysql.SQLClient;
import libCore.config.Config;

/**
 *
 * @author KieuAnh
 */
public class DBContext
{
    private DBContext() {}
    
    static
    {
        REDIS = initRedis();
        SQL = new SQLClient();
    }
    
    private static Redis initRedis()
    {
        String host = Config.getParam("redis", "host");
        int port = Integer.valueOf(Config.getParam("redis", "port"));
        String password = Config.getParam("redis", "pass");
        int db = Integer.valueOf(Config.getParam("redis", "database"));
        
        int maxActive = Integer.valueOf(Config.getParam("redis", "max_active"));
        int maxIdle = Integer.valueOf(Config.getParam("redis", "max_idle"));
        int maxWait = Integer.valueOf(Config.getParam("redis", "max_wait"));
        
        return new JedisAdapter(JedisPoolFactory.create(host, port,
                5000, password, db, maxActive, maxIdle, maxWait));
    }
    
    private final static Redis REDIS;
    private final static SQLClient SQL;
    /**
     * @return the REDIS
     */
    public static Redis Redis() {
        return REDIS;
    }
    
    public static SQLClient SQL()
    {
        return SQL;
    }
    
}
