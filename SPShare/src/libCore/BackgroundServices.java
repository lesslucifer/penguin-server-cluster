/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package libCore;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author KieuAnh
 */
public class BackgroundServices implements Runnable
{
    private BackgroundServices()
    {
        super();
    }
    
    private static final BackgroundServices inst = new BackgroundServices();
    
    public static BackgroundServices inst()
    {
        return inst;
    }
    
    private Executor executor = Executors.newSingleThreadExecutor();
    
    public void runBackground(Runnable runner)
    {
        executor.execute(runner);
    }

    @Override
    public void run() {
    }
}
