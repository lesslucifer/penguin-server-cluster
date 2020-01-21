/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection;

import minaconnection.interfaces.IRequestPool;

/**
 *
 * @author suaongmattroi
 */
public class SimpleRequestPoolFactory {
    
    public static IRequestPool create()
    {
        IRequestPool pool = new SimpleRequestPool();
        return pool;
    }
}
