/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minaconnection.interfaces;

import java.io.Serializable;
import minaconnection.MinaAddress;

/**
 *
 * @author suaongmattroi
 */
public interface IRequestPool {
    void request(MinaAddress address, Serializable data, IClientHandler h);
    void registerExceptionCaught(IMinaException exceptionHandler);
}
