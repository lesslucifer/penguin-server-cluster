/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.actions;

import java.util.HashMap;
import java.util.Map;
import pgentity.EntityContext;
import pgentity.HackEntity;
import pgentity.User;
import pgentity.services.UserServices;
import share.PGMacro;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class PGHackServices
{
    private PGHackServices()
    {
        super();
    }
    
    private static final PGHackServices inst = new PGHackServices();
    
    public static PGHackServices inst()
    {
        return inst;
    }
    
    public Map<String, Object> increaseUserGold(String uid, int nGold, long now) throws PGException
    {
        User user = User.getUser(uid);
        user.updateFromDB();
        
        UserServices.inst().increaseGold(user, nGold);
        
        user.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        return result;
    }
    
    public Map<String, Object> increaseUserCoin(String uid, int nCoin, long now) throws PGException
    {
        User user = User.getUser(uid);
        user.updateFromDB();
        
        user.changeCoin(nCoin);
        
        user.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        return result;
    }
    
    public Map<String, Object> increaseUserExp(String uid, int nExp, long now) throws PGException
    {
        EntityContext context = EntityContext.getContext(uid);
        
        UserServices.inst().increaseUserExp(context, nExp, now);
        context.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        return result;
    }
    
    public Map<String, Object> increaseUserTime(String uid, long nTime, long now)
    {
        HackEntity hacker = HackEntity.getEntity(uid);
        
        long newDTime = hacker.getDeltaTime() + nTime;
        hacker.setDeltaTime(newDTime);
        
        hacker.saveToDB();
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(PGMacro.SUCCESS, true);
        return result;
    }
}