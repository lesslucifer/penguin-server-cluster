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
    private List<Target> allTargets = new LinkedList();
    private Queue<Target> targetQueue = new LinkedList();
    
    public MinaTargetResolver(List<MinaAddress> addresses) {
        for (MinaAddress address : addresses) {
            Target target = createTarget(address);
            allTargets.add(target);
            targetQueue.add(target);
        }
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
        Target top = targetQueue.poll();
        targetQueue.add(top);
        
        return top;
    }

    @Override
    public Target getMasterTarget() {
        return allTargets.get(0);
    }
    
    private Target createTarget(MinaAddress address) {
        return new MinaTarget(address);
    }
    
    private static class TargetList
    {
        private List<MinaAddress> targets;
    }
}
