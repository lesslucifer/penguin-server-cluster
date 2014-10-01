/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package target;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author KieuAnh
 */
public interface RemoteTarget extends Remote {
    public Object doHTTP(Request request) throws RemoteException;
    public Object doAMF(Request request) throws RemoteException;
}
