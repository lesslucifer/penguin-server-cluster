/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.amfclient.scenarios;

import PGLogCalculator.PGLogCalculator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author suaongmattroi
 */
public class ScBase {
    protected static Map<String, String> sessions = new ConcurrentHashMap<>();
    protected static Map<String, StringBuilder> logs = new ConcurrentHashMap<>();
    
    protected AtomicBoolean enabled = new AtomicBoolean(true);
    protected int limit;
    protected AtomicInteger counter = new AtomicInteger();
    
    protected void checkCompleteScenario(String uid)
    {
        if(counter.incrementAndGet() >= limit && enabled.get())
        {
            enabled.set(false);
            PGLogCalculator.getInst().push(uid, new Date());
        }
    }
}
