package PGLogCalculator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author suaongmattroi
 */
public class PGLogCalculator {
    
    private Map<String, TestContent> dict;
    private int limit;
    private AtomicInteger counter = new AtomicInteger();
    private String scenario;
    
    private static PGLogCalculator inst;
    static 
    {
        inst = new PGLogCalculator();
    }
    
    public static PGLogCalculator getInst()
    {
        return inst;
    }
    
    class TestContent
    {
        private Date reqTime;
        private Date respTime;

        public TestContent(Date reqTime) {
            this.reqTime = reqTime;
        }
        
        public void setRespTime(Date respTime) {
            this.respTime = respTime;
        }
        
        public long elapsed()
        {
            return respTime.getTime() - reqTime.getTime();
        }
    }
    
    private PGLogCalculator()
    {
        this.dict = new ConcurrentHashMap();
    }
    
    public void serve(int n)
    {
        this.limit = n;
    }
    
    public void setScenario(String scenario)
    {
        this.scenario = scenario;
    }
    
    public void push(String uid, Date time)
    {
        if(!dict.containsKey(uid))
            dict.put(uid, new TestContent(time));
        else
        {
            TestContent c = dict.get(uid);
            c.setRespTime(time);
            handleCounter();
        }
    }
    
    private void handleCounter()
    {
        if(counter.incrementAndGet() >= limit)
        {
            save();
        }
    }
    
    public void save()
    {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("./log/" + scenario + ".csv", "UTF-8");
            for(Map.Entry<String, TestContent> entry : dict.entrySet())
            {
                writer.write(entry.getKey() + "," + entry.getValue().elapsed() + ",\n");
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(PGLogCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }
}
