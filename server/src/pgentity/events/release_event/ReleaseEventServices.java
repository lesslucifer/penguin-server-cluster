/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pgentity.events.release_event;

import config.CFReleaseEvent;
import config.PGConfig;
import java.util.*;
import pgentity.Cote;
import pgentity.CoteList;
import pgentity.EggStore;
import pgentity.EntityContext;
import pgentity.Penguin;
import pgentity.PenguinList;
import pgentity.User;
import pgentity.quest.QuestChecker;
import pgentity.quest.QuestCheckerFactory;
import pgentity.quest.QuestFactory;
import pgentity.services.EggStoreServices;
import pgentity.services.GiftServices;
import pgentity.services.PenguinUpdator;
import share.PGError;
import share.PGException;
import share.PGMacro;

/**
 *
 * @author Salm
 */
public class ReleaseEventServices {
    private ReleaseEventServices() {}
    
    public static final ReleaseEventServices SERVICES = new ReleaseEventServices();
    
    private Collection<PenguinUpdator> getPenguinUpdator(RECote eventCote,
            Collection<Penguin> penguins, long now)
    {
        List<PenguinUpdator> ret = new ArrayList(penguins.size());
        for (Penguin penguin : penguins) {
            REPenguin eventPenguin = REPenguin.getPenguin(penguin);
            ret.add(new REPenguinUpdator(eventCote, eventPenguin, now));
        }
        
        return ret;
    }
    
    Collection<PenguinUpdator> getPenguinUpdator(Cote cote,
            Collection<Penguin> penguins, long now)
    {
        RECote eventCote = RECote.getCote(cote);
        return getPenguinUpdator(eventCote, penguins, now);
    }
    
    public Object spawnReleaseEventItems(Cote cote,
            Collection<Penguin> penguins, long now) throws PGException
    {
        final RECote eCote = RECote.getCote(cote);
        Collection<REPenguin> ePenguins = new HashSet(penguins.size());
        for (Penguin penguin : penguins) {
            ePenguins.add(REPenguin.getPenguin(penguin));
        }
        
        PriorityQueue<REPenguinUpdator> penguinUpdators = new PriorityQueue(penguins.size(),
            new Comparator<REPenguinUpdator>() {
            @Override
            public int compare(REPenguinUpdator p1, REPenguinUpdator p2) {
                long p1NextSpawnTime = p1.nextActionTime();
                long p2NextSpawnTime = p2.nextActionTime();
                
                return (p1NextSpawnTime > p2NextSpawnTime)?1:
                        ((p1NextSpawnTime == p2NextSpawnTime)?0:-1);
            }
        });
        
        Map<String, Object> failData = new HashMap();
        
        // init penguin entities
        for (REPenguin penguin : ePenguins) {
            REPenguinUpdator updator = new REPenguinUpdator(eCote, penguin, now);
            long nextSpawn = updator.nextActionTime();
            if (nextSpawn > now)
            {
                Map<String, Object> failPenguinData = new HashMap(2);
                failPenguinData.put(PGMacro.TIME_LAST_SPAWN, penguin.getLastSpawnTime());
                
                failData.put(penguin.getPenguin().getPenguinID(), failPenguinData);
            }
            else
            {
                penguinUpdators.add(updator);
            }
        }
        
        Map<String, Object> successData = new HashMap(ePenguins.size());
        
        while (!penguinUpdators.isEmpty())
        {
            REPenguinUpdator updator = penguinUpdators.poll();
            updator.update();
            
            successData.put(updator.getPenguinID(), updator.getLastSpawnedItem());
            updator.save();
        }
        
        Map<String, Object> response = new HashMap();
        response.put(PGMacro.SUCCESS, successData);
        response.put(PGMacro.FAIL, failData);
        return response;
    }
    
    public Object pickReleaseEventItems(User user, Cote cote,
            Map<String, Number> items)
    {
        RECote eCote = RECote.getCote(cote);
        Map<String, Number> validItems = EggStoreServices.inst()
                .validateEgg(eCote.Items, items);
        
        EggStore eInv = REUser.getUser(user).inv();
        
        EggStoreServices.inst().moveEgg(eCote.Items, eInv, validItems);
        
        Map<String, Object> resp = new HashMap(2);
        
        resp.put(PGMacro.SUCCESS_EGGS, validItems);
        
        // build failed eggs
        if (!validItems.equals(items))
        {
            Map<String, Number> failedEggs = EggStoreServices.inst()
                    .substractEggs(items, validItems);
            
            resp.put(PGMacro.FAILED_EGGS, failedEggs);
        }
        
        return resp;
    }
    
    public Object returnReleaseEvent(EntityContext context, String eventID, long now)
    {
        CFReleaseEvent.Event conf = PGConfig.inst().releaseEvent().getEvents().get(eventID);
        PGException.Assert(conf != null, PGError.INVALID_CONFIG, "Invalid event id");
        
        UserEvents uEvents = UserEvents.getEvents(eventID);
        PGException.Assert(!uEvents.contains(eventID), PGError.RETURNED_EVENT,
                "You have returned this event");
        
        QuestChecker eventChecker = QuestFactory.getChecker(
                uEvents.getCheckerLogPool(), conf.getActions());
        
        PGException.Assert(!eventChecker.isAccept(context),
                PGError.INCOMPLETED_EVENT,
                "Not have enough resource for complete this event");
        
        eventChecker.returnQuest(context);
        uEvents.add(eventID);
        
        //prizing
        Collection<String> uids = Arrays.asList(new String[]{context.getUid()});
        GiftServices.inst().sendGift(
                uids,
                conf.getPrize(),
                now,
                PGConfig.inst().temp().RandomizePrize_Expire());
        
        // check complete event
        Set<String> allEvents = PGConfig.inst().releaseEvent().getEvents().keySet();
        if (allEvents.equals(uEvents.getAll()))
        {
            uEvents.add("finished");
            GiftServices.inst().sendGift(
                    uids,
                    PGConfig.inst().releaseEvent().getTotalPrize(),
                    now,
                    PGConfig.inst().temp().RandomizePrize_Expire());
        }
        
        return Collections.EMPTY_MAP;
    }
    
    Object buildAMF(String uid)
    {
        Map<String, Object> resp = new HashMap(2);
        resp.put(PGMacro.USER, REUser.getUser(User.getUser(uid)).buildAMF());
        
        List<String> coteIDs = CoteList.getCotes(uid).getAll();
        Map<String, Object> eCotesAMF = new HashMap(coteIDs.size());
        for (String coteID : coteIDs) {
            eCotesAMF.put(coteID, buildCoteAMF(uid, coteID));
        }
        
        resp.put(PGMacro.COTE, eCotesAMF);
        
        return resp;
    }
    
    Object buildCoteAMF(String uid, String coteID)
    {
        Map<String, Object> coteAMF = new HashMap(2);
        
        coteAMF.put(PGMacro.DATA,
                RECote.getCote(Cote.getCote(uid, coteID)).buildAMF());
        
        Set<String> pIDs = PenguinList.getPenguinList(uid, coteID).getAll();
        Map<String, Object> penguinAMFs = new HashMap(pIDs.size());
        
        for (String pID : pIDs) {
            REPenguin penguin = REPenguin.getPenguin(
                    Penguin.getPenguin(uid, coteID, pID));
            
            penguinAMFs.put(pID, penguin.buildAMF());
        }
        
        coteAMF.put(PGMacro.PENGUIN_LIST, penguinAMFs);
        
        return coteAMF;
    }
    
    public static class CheckerPool
    {
        private CheckerPool() {}
        
        public static final Map<String, QuestCheckerFactory> CHECKERS = new HashMap();
        static
        {
            CHECKERS.put("re_items", REItemsChecker.Factory.inst());
        }
    }
}
