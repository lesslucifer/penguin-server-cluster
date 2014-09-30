/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.penguin.event;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author suaongmattroi
 */
public interface INotifable {
    
    void register(Object listener, String method);
    void unregister(Object listener, String method);
    
    void notifyEvent() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException ;
    
    Object getParams();
}
