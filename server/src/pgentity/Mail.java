/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import db.DBContext;
import db.mysql.DataRow;
import db.mysql.DataTable;
import db.mysql.DeleteExecutor;
import db.mysql.SQLBackgroundExecutor;
import db.mysql.SQLBatchStatement;
import db.mysql.SQLUtils;
import db.mysql.SelectExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import pgentity.pool.LockManager;
import pgentity.pool.MultiCacheLoader;
import pgentity.pool.MultiloadCache;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */
public class Mail
{
    private static final MultiloadCache<String, Mail> MAIL_CACHE;
    private static final String MAIL_TABLE = "MAILS";
    
    private static final Mail ERROR_MAIL;
    
    static
    {
        Cache<String, Mail> innerCache = CacheBuilder.newBuilder()
                .expireAfterAccess(30, TimeUnit.SECONDS)
                .maximumSize(10000)
                .build();
        MAIL_CACHE = new MultiloadCache(innerCache, MailLoader.inst(),
                new LockManager(), Striped.lazyWeakLock(64));
        
        ERROR_MAIL = new Mail("");
        ERROR_MAIL.sendTime = 0L;
        ERROR_MAIL.sender = "";
        ERROR_MAIL.content = "";
    }
    
    private final String mailID;
    
    private Long sendTime;
    private String sender;
    private String content;
    
    private Mail(String mailID) {
        this.mailID = mailID;
    }
    
    public static Mail getMail(String mailID)
    {
        return MAIL_CACHE.get(mailID);
    }
    
    public static List<Mail> getMails(String... mailIDs)
    {
        return getMails(Arrays.asList(mailIDs));
    }
    
    public static List<Mail> getMails(List<String> mailIDs)
    {
        return MAIL_CACHE.getMulti(mailIDs);
    }
    
    public static void destroys(List<String> mailIDs)
    {
        if (mailIDs.size() > 0)
        {
            String where = PGMacro.MAIL_ID + " in " + SQLUtils.buildListData((List) mailIDs);
            DBContext.SQL().execute(DeleteExecutor.fromDesc(MAIL_TABLE, where));
            
            for (String mailID : mailIDs) {
                MAIL_CACHE.remove(mailID);
            }
        }
    }
    
    public static Mail newMail(final String mailID, final String sender,
            final String receiver, final String content, final long now)
    {
        DBContext.SQL().executeBackground(new SQLBackgroundExecutor() {
            private static final String queryToken = "INSERT INTO " + 
                    MAIL_TABLE + " VALUES (?, ?, ?, ?, ?)";
            
            @Override
            public String queryToken() {
                return queryToken;
            }

            @Override
            public int batchSizeLimit() {
                return 1000;
            }

            @Override
            public void prepare(SQLBatchStatement.Manager manager) {
                SQLBatchStatement stm = manager.createStatement();

                stm.setString(1, mailID);
                stm.setString(2, sender);
                stm.setString(3, receiver);
                stm.setLong(4, now);
                stm.setString(5, content);
            }
        });
        
        Mail mail = new Mail(mailID);
        mail.sendTime = now;
        mail.sender = sender;
        mail.content = content;
        
        MAIL_CACHE.put(mailID, mail);
        
        return mail;
    }
    
    public void updateFromRow(DataRow row)
    {
        this.sendTime = Long.valueOf(row.get(PGMacro.MAIL_SEND_TIME));
        this.sender = row.get(PGMacro.MAIL_SENDER);
        this.content = row.get(PGMacro.MAIL_CONTENT);
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMailID() {
        return mailID;
    }
    
    public Object dump()
    {
        Map<String, Object> data = new HashMap();
        data.put(PGMacro.MAIL_SEND_TIME, sendTime);
        data.put(PGMacro.MAIL_CONTENT, content);
        data.put(PGMacro.MAIL_SENDER, sender);
        
        return data;
    }
    
    private static class MailLoader implements MultiCacheLoader<String, Mail>
    {
        private MailLoader()
        {
            super();
        }
        
        private static final MailLoader inst = new MailLoader();
        
        public static MailLoader inst()
        {
            return inst;
        }
    
        @Override
        public List<Mail> loadMulti(List<String> keys) {
            DataTable db = query(keys);
            Map<String, DataRow> mailRows = new HashMap(db.size());
            for (DataRow row : db) {
                mailRows.put(row.get(PGMacro.MAIL_ID), row);
            }

            List<Mail> mails = new ArrayList(db.size());
            int dbRow = 0;
            for (int i = 0; i < keys.size(); ++i) {
                String mailID = keys.get(i);
                if (mailRows.containsKey(mailID))
                {
                    Mail mail = new Mail(mailID);
                    mail.updateFromRow(db.get(dbRow++));
                    mails.add(mail);
                }
                else
                {
                    mails.add(Mail.ERROR_MAIL);
                }
            }

            return mails;
        }

        @Override
        public Mail load(String key) {
            DataTable db = query(Arrays.asList(new String[] {key}));

            if (db.isEmpty())
            {
                return null;
            }
            
            Mail mail = new Mail(key);
            mail.updateFromRow(db.get(0));
            return mail;
        }
    
        private static DataTable query(List<String> mailIDs)
        {
            String where = "`" + PGMacro.MAIL_ID + "` in " + SQLUtils.buildListData((List) mailIDs);
            return (DataTable) DBContext.SQL().execute(SelectExecutor
                    .fromDesc(MAIL_TABLE, where,
                            PGMacro.MAIL_ID,
                            PGMacro.MAIL_CONTENT,
                            PGMacro.MAIL_SENDER,
                            PGMacro.MAIL_SEND_TIME));
        }
    }
}