/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package git.target;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import target.TargetResolver;

/**
 *
 * @author Salm
 */
public class Targets {
    public static final TargetResolver RESOLVER;
    
    static
    {
        TargetResolver resl;
        try {
            resl = MinaTargetResolver.fromFile(new File("targets.json"));
        } catch (IOException ex) {
            resl = null;
            Logger.getLogger(Targets.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        RESOLVER = resl;
    }
}
