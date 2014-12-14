/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import share.PGHelper;

/**
 *
 * @author Salm
 */
class JedisPoolFactory {
    private JedisPoolFactory() {}
    
    public static JedisPool create(String host, int port, int timeOut,
            String password, int db,
            int maxActive, int maxIdle, int maxWait)
    {
        JedisPoolConfig conf = new JedisPoolConfig();
        conf.setTestOnBorrow(false);
        conf.setTestOnReturn(false);
        conf.setTestWhileIdle(false);
        conf.setMaxActive(maxActive);
        conf.setMaxIdle(maxIdle);
        conf.setMaxWait(maxWait);
        
        password = PGHelper.isNullOrEmpty(password)?null:password;
        return new JedisPool(conf, host, port, timeOut, password);
    }
}
