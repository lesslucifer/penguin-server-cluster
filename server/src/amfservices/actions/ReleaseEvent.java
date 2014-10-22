/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amfservices.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pgentity.Cote;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.User;
import pgentity.events.release_event.ReleaseEventServices;
import share.AMFBuilder;
import share.PGMacro;

/**
 *
 * @author Salm
 */
class ReleaseEvent {
    private ReleaseEvent() {}
    public static final ReleaseEvent INST = new ReleaseEvent();
    
    public Object pickReleaseEventItems(String uid, String coteID, Map<String, Number> items)
    {
        User user = User.getUser(uid);
        Cote cote = Cote.getCote(uid, coteID);
        
        return ReleaseEventServices.SERVICES.pickReleaseEventItems(user, cote, items);
    }
    
    public Object penguinSpawnReleaseEventItem(String uid, String coteID,
            Collection<String> penguinIDs, long now)
    {
        Cote cote = Cote.getCote(uid, coteID);
        Set<Penguin> penguins = new HashSet(penguinIDs.size());
        for (String penguinID : penguinIDs) {
            penguins.add(Penguin.getPenguin(uid, coteID, penguinID));
        }
        
        return ReleaseEventServices.SERVICES
                .spawnReleaseEventItems(cote, penguins, now);
    }
    
    public Object returnReleaseEvent(String uid, String eventID, long now)
    {
        EntityContext context = EntityContext.getContext(uid);
        Object resp = ReleaseEventServices.SERVICES
                .returnReleaseEvent(context, eventID, now);
        
        context.saveToDB();
        
        return AMFBuilder.make(PGMacro.PRIZE, resp);
    }
}
