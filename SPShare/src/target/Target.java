/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package target;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author KieuAnh
 */
public interface Target {
    Boolean isGood();
    
    Object doAMF(Request request) throws InvocationTargetException;
    Object doHTTP(Request request) throws InvocationTargetException;
}
