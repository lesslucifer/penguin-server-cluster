/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class EntityContext implements PGEntity
{
    private final User user;
    private final Cote cote;
    private final BoxEgg boxEgg;
    private final Penguindex penguindex;

    private EntityContext(User user, Cote cote, BoxEgg boxEgg,
            Penguindex penguindex) {
        this.user = user;
        this.cote = cote;
        this.boxEgg = boxEgg;
        this.penguindex = penguindex;
    }
    
    public static EntityContext getContext(String uid)
            throws PGException
    {
        User user = User.getUser(uid);
        return getContext(user);
    }
    
    public static EntityContext getContext(String uid, String coteID)
            throws PGException
    {
        User user = User.getUser(uid);
        Cote cote = Cote.getCote(uid, coteID);
        return getContext(user, cote);
    }
    
    public static EntityContext getContext(User user)
    {
        Cote cote = Cote.getCote(user.getUid(), user.getLastCote());
        return getContext(user, cote);
    }
    
    public static EntityContext getContext(User user, Cote cote)
    {
        BoxEgg boxEgg = BoxEgg.getBoxEgg(cote.getUid(), cote.getCoteID());
        Penguindex pdex = Penguindex.getPenguindex(user.getUid());
        return getContext(user, cote, boxEgg, pdex);
    }
    
    public static EntityContext getContext(User user, Cote cote, BoxEgg boxEgg,
            Penguindex pdex)
    {
        return new EntityContext(user, cote, boxEgg, pdex);
    }

    public User getUser() {
        return user;
    }

    public Cote getCote() {
        return cote;
    }

    public BoxEgg getBoxEgg() {
        return boxEgg;
    }

    public Penguindex getPenguindex() {
        return penguindex;
    }

    @Override
    public void updateFromDB()
    {
        user.updateFromDB();
        cote.updateFromDB();
        boxEgg.updateFromDB();
    }

    @Override
    public void saveToDB() {
        user.saveToDB();
        cote.saveToDB();
        boxEgg.saveToDB();
    }
    
    public String getUid()
    {
        return user.getUid();
    }
    
    public String getCoteID()
    {
        return cote.getCoteID();
    }
}