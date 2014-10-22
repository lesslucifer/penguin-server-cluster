/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
class BackgroundSQL implements Runnable
{
    private static final int BATCH_TIME_FREQ = 17;
    private static final int BATCH_TIME_ALL = 29;
    private static final int BATCH_SIZE_FREQ = 500;
    private static final int BATCH_SIZE_ALL = 0;
    
    private final Lock lock = new ReentrantLock();
    private final Map<String, SQLBatch> batchs = new ConcurrentHashMap();
    private final Executor executors = Executors.newFixedThreadPool(5);
    private final ScheduledExecutorService scheduleExe =
            Executors.newSingleThreadScheduledExecutor();
    
    public void exec(SQLBackgroundExecutor exe)
    {
        SQLBatch batch = getBacth(exe.queryToken());
        if (batch != null)
        {
            batch.exec(exe);
            updateBatchs(Arrays.asList(new String[] {exe.queryToken()}), exe.batchSizeLimit());
        }
    }
    
    @Override
    public void run()
    {
        scheduleExe.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<String> queries = new ArrayList(batchs.keySet());
                updateBatchs(queries, BATCH_SIZE_FREQ);
            }
        }, 0, BATCH_TIME_FREQ, TimeUnit.SECONDS);
        
        scheduleExe.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                List<String> queries = new ArrayList(batchs.keySet());
                updateBatchs(queries, BATCH_SIZE_ALL);
            }
        }, 0, BATCH_TIME_ALL, TimeUnit.SECONDS);
    }
    
    private SQLBatch getBacth(String queryToken)
    {
        SQLBatch batch = batchs.get(queryToken);
        if (batch == null)
        {
            lock.lock();
            try {
                batch = batchs.get(queryToken);
                if (batch == null)
                {
                    batch = SQLBatch.getBatch(queryToken);
                    batchs.put(queryToken, batch);
                }
            }
            finally {
                lock.unlock();
            }
        }
        
        return batch;
    }

    private void updateBatchs(final List<String> queries, final int maxSize) {
        lock.lock();
        try {
            for (final String query : queries) {
                final SQLBatch batch = getBacth(query);
                if (batch == null)
                {
                    continue;
                }
                
                final int batchSize = batch.getBatchSize();
                if (batchSize < maxSize)
                {
                    continue;
                }
                
                batchs.remove(query);
                executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            batch.execAllBatchs();
                        } catch (SQLException ex) {
                            PGException.pgThrow(ex);
                        }
                    }
                });
            }
        } finally {
            lock.unlock();
        }
    }
}
