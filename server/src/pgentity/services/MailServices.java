/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pgentity.Mail;
import pgentity.MailBox;
import db.PGKeys;
import pgentity.User;
import share.PGMacro;

/**
 *
 * @author KieuAnh
 */
public class MailServices {
    private MailServices()
    {
        super();
    }
    
    private static final MailServices inst = new MailServices();
    
    public static MailServices inst()
    {
        return inst;
    }
    
    private static final String SYSTEM_NAME = "Hệ Thống";
    private void systemSendMail(String to, String content, long now)
    {
        final String mailID = PGKeys.randomKey();
        Mail.newMail(mailID, SYSTEM_NAME, to, content, now);
        
        MailBox mailBox = MailBox.getMailBoxOf(to);
        mailBox.append(mailID);
    }
    
    public void sendStealEggMail(User thief, String stolenUser, String eggKind,
            long now)
    {
        String content = String.format(
                  "<text color=\"0xffffff\">"
                +   "<link type=\"user\" data=\"%s\">%s</link>"
                + "</text>"
                + "<text> vừa trộm của bạn 1 trứng </text>"
                + "<img src=\"%s\" /> <text>:(</text>",
                thief.getUid(), thief.getName(), eggKind);
        this.systemSendMail(stolenUser, content, now);
    }
    
    public void sendHelpFishMail(User helper, String helped, int nFish,
            long now)
    {
        String content = String.format(
                  "<text color=\"0xffffff\">"
                +   "<link type=\"user\" data=\"%s\">%s</link>"
                + "</text>"
                + "<text> vừa giúp bạn %d cá </text>",
                helper.getUid(), helper.getName(), nFish);
        this.systemSendMail(helped, content, now);
    }
    
    public Map<String, Object> buildMailAMF(Mail mail)
    {
        Map<String, Object> data = new HashMap();
        data.put(PGMacro.MAIL_ID, mail.getMailID());
        data.put(PGMacro.MAIL_SEND_TIME, mail.getSendTime());
        data.put(PGMacro.MAIL_SENDER, mail.getSender());
        data.put(PGMacro.MAIL_CONTENT, mail.getContent());
        
        return data;
    }
    
    public Map<String, Object> buildMailBox(MailBox mailBox, int off, int len)
    {
        Map<Integer, String> mailIDs = mailBox.mailInRange(off, len);
        
        Map<String, Integer> mailIndices = new HashMap(mailIDs.size());
        List<String> lstMailIDs = new ArrayList(mailIDs.size());
        for (Map.Entry<Integer, String> mailEntry : mailIDs.entrySet()) {
            mailIndices.put(mailEntry.getValue(), mailEntry.getKey());
            lstMailIDs.add(mailEntry.getValue());
        }
        
        List<Mail> lstMails = Mail.getMails(lstMailIDs);
        
        Map<String, Object> data = new HashMap(lstMails.size());
        for (Mail mail : lstMails) {
            Integer index = mailIndices.get(mail.getMailID());
            if (index != null)
            {
                data.put(String.valueOf(index), this.buildMailAMF(mail));
            }
        }
        
        return data;
    }
    
    public void clearAll(MailBox mailBox)
    {
        List<String> mailIDs = mailBox.getAll();
        Mail.destroys(mailIDs);
        
        mailBox.clearAllMails();
    }
    
    public void destroyMail(String uid)
    {
        try
        {
            MailBox mailBox = MailBox.getMailBoxOf(uid);
            clearAll(mailBox);

            MailBox.destroyMailBox(uid);
        }
        catch (Exception ex)
        {
        }
    }
    
    public Object dumpMails(String uid)
    {
        MailBox mailBox = MailBox.getMailBoxOf(uid);
        List<String> mailIDs = mailBox.getAll();
        List<Mail> mails = Mail.getMails(mailIDs);
        
        List<Object> data = new ArrayList(mails.size());
        for (Mail mail : mails) {
            data.add(mail.dump());
        }
        
        return data;
    }
}