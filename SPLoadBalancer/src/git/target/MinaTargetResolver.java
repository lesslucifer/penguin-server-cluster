/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package git.target;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import minaconnection.MinaAddress;
import share.PGHelper;
import target.Target;
import target.TargetResolver;

/**
 *
 * @author suaongmattroi
 */
class MinaTargetResolver implements TargetResolver
{
    // re-add after 1 day
    private static final int DELAY_RELOAD_TARGETS = 86400000;
    
    private Timer reloadTimer;
    
    private final List<Target> allTargets = new LinkedList();
    private final Queue<Target> targetQueue = new LinkedList();
    
    public MinaTargetResolver(List<MinaAddress> addresses) {
        createTimer();
        for (MinaAddress address : addresses) {
            Target target = createTarget(address);
            allTargets.add(target);
            targetQueue.add(target);
        }
    }
    
    private void createTimer() {
        reloadTimer = new Timer();
        
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                reAddAllTarget();
            }
        };
        reloadTimer.schedule(task, DELAY_RELOAD_TARGETS, DELAY_RELOAD_TARGETS);
    }
    
    public static MinaTargetResolver fromFile(File conf) throws IOException
    {
        String data = new String(
                Files.readAllBytes(Paths.get(conf.toURI())),
                StandardCharsets.UTF_8);
        
        TargetList targets = PGHelper.getJSONParser().fromJson(data,
                TargetList.class);
        
        return new MinaTargetResolver(targets.targets);
    }
    
    @Override
    public Target getUserTarget(String uid) {
        // Try to get good connection target at head of queue
        Target goodTop = null;
        do {
            goodTop = targetQueue.poll();
        } while(goodTop != null && !goodTop.isGood());
        
        if(goodTop != null && goodTop.isGood())
            targetQueue.add(goodTop);
        
        return goodTop;
    }

    @Override
    public Target getMasterTarget() {
        return allTargets.get(0);
    }
    
    private void reAddAllTarget() {
        // Nothing happened - recheck after 1 day
        if(targetQueue.size() == allTargets.size())
            return;
        
        synchronized(targetQueue) {
            try {
                targetQueue.wait();

                targetQueue.clear();
                targetQueue.addAll(allTargets);
            } catch (InterruptedException ex) {
                Logger.getLogger(MinaTargetResolver.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                targetQueue.notifyAll();
            }
        }
    }
    
    private Target createTarget(MinaAddress address) {
        return new MinaTarget(address);
    }
    
    private static class TargetList {
        private List<MinaAddress> targets;
    }
}
