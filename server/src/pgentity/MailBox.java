/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import db.DBContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pgentity.pool.EntityFactory;
import pgentity.pool.EntityPool;
import pgentity.redis.PGRedisListEntity;
import share.PGHelper;
import db.PGKeys;
import share.PGMacro;
import db.RedisKey;

/**
 *
 * @author KieuAnh
 */
@EntityFactory(factorier = "loadMailBox")
public class MailBox extends PGRedisListEntity
{
    private final String uid;
    private final RedisKey mailListRK;
    private final RedisKey mailDataRK;

    private MailBox(String uid) {
        this.uid = uid;
        this.mailListRK = PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_MAILS);
        this.mailDataRK = PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_MAILS).getChild(PGMacro.MAIL_CONTENT);
    }
    
    public static MailBox getMailBoxOf(String uid)
    {
        return EntityPool.inst().get(MailBox.class, uid);
    }
    
    private static MailBox loadMailBox(String uid)
    {
        return new MailBox(uid);
    }
    
    /**
     * Use MailService.destroy instead
     * @param uid
     * @deprecated
     */
    @Deprecated
    public static void destroyMailBox(String uid)
    {
        RedisKey mailListRK = PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_MAILS);
        RedisKey  mailDataRK = PGKeys.USERS.getChild(uid)
                .getChild(PGKeys.FD_MAILS).getChild(PGMacro.MAIL_CONTENT);
        
        DBContext.Redis().del(mailListRK);
        DBContext.Redis().del(mailDataRK);
        
        EntityPool.inst().remove(MailBox.class, uid);
    }
    
    @Override
    protected RedisKey redisKey() {
        return mailListRK;
    }
    
    public Map<Integer, String> mailInRange(int offset, int length)
    {
        List<String> mailIDs = this.in(offset - length, offset);
        
        Map<Integer, String> mails = new HashMap();
        
        if (mailIDs != null)
        {
            int trueOffset = this.trueOffset(offset);
            for (int i = 0; i < mailIDs.size(); i++) {
                String mailID = mailIDs.get(i);
                mails.put(trueOffset - mailIDs.size() + i + 1, mailID);
            }
        }
        
        return mails;
    }

    @Override
    public long append(String id) {
        DBContext.Redis().hincrby(mailDataRK, PGMacro.NUMBER_NEW_MAILS, 1);
        return super.append(id);
    }
    
    private int trueOffset(int offset)
    {
        if (offset < 0)
        {
            return this.length() + offset;
        }
        
        return offset;
    }

    public String getUid() {
        return uid;
    }
    
    public int getUnreadMail()
    {
        return PGHelper.toInteger(DBContext.Redis()
                .hget(mailDataRK, PGMacro.NUMBER_NEW_MAILS));
    }
    
    public void resetUnreadMail()
    {
        DBContext.Redis().hdel(mailDataRK, PGMacro.NUMBER_NEW_MAILS);
    }
    
    @Deprecated
    /**
     * Use MailServices.clearAll instead of
     */
    public void clearAllMails()
    {
        DBContext.Redis().del(mailListRK);
        this.resetUnreadMail();
    }
    
    /**
     * Use MailServices.dumpMails instead
     * @return
     */
    @Deprecated
    public Object dump()
    {
        return this.getAll();
    }
}