/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import org.apache.commons.pool2.impl.GenericObjectPool;
import share.StringBuilderPoolFactory;

/**
 *
 * @author KieuAnh
 */
class SQLStringBuilder {
    private static final GenericObjectPool<StringBuilder> pool;
    
    static
    {
        pool = (GenericObjectPool) StringBuilderPoolFactory.makePool();
    }
    
    public static GenericObjectPool<StringBuilder> inst()
    {
        return pool;
    }
}
