/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amfservices.actions;

import java.util.Map;
import pgentity.BoxEgg;
import pgentity.Cote;
import pgentity.CoteList;
import pgentity.Penguin;
import pgentity.User;
import share.AMFBuilder;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class ReloadServices
{
    private ReloadServices()
    {
        super();
    }
    
    private static final ReloadServices inst = new ReloadServices();
    
    public static ReloadServices inst()
    {
        return inst;
    }
    
    public Map<String, Object> getUserBasicInfo(String userID) throws PGException
    {
        User user = User.getUser(userID);
        return user.buildBasicDataAMF();
    }
    
    public Map<String, Object> getUserCoteList(String uid)
    {
        return AMFBuilder.toAMF(CoteList.getCotes(uid).getAll());
    }
    
    public Map<String, Object> getUserFullInfo(String uid, boolean getCote,
            boolean getFriends, boolean getInventory, long now)
    {
        User user = User.getUser(uid);
        return user.buildFullAMF(getCote, getFriends, getInventory, false, now);
    }
    
    public Map<String, Object> getPenguinInfo(String uid, String coteID, String penguinID)
    {
        Penguin penguin = Penguin.getPenguin(uid, coteID, penguinID);
        
        return penguin.buildAMF();
    }
    
    public Map<String, Object> getCoteBasicInfo(String uid, String coteID)
    {
        Cote cote = Cote.getCote(uid, coteID);
        return cote.buildBasicAMF();
    }
    
    public Map<String, Object> getCoteFullInfo(String uid, String coteID,
            boolean getBoxEgg, boolean getPenguins, boolean getEggs, boolean getDog)
    {
        Cote cote = Cote.getCote(uid, coteID);
        return cote.buildRescusiveAMF(getBoxEgg, getPenguins, getEggs, getDog);
    }
    
    public Map<String, Object> getBoxEggInfo(String uid, String coteID)
    {
        BoxEgg boxEgg = BoxEgg.getBoxEgg(uid, coteID);
        return boxEgg.buildAMF();
    }
}
