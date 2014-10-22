/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minaconnection;

import java.io.Serializable;
import minaconnection.interfaces.IMinaData;

/**
 *
 * @author suaongmattroi
 */
class MinaData implements IMinaData{
    
    private final long _index;
    
    private final Serializable _data;
    
    public MinaData(long index, Serializable data)
    {
        this._index = index;
        this._data = data;
    }
    
    @Override
    public long index()
    {
        return this._index;
    }
    
    @Override
    public Serializable data()
    {
        return this._data;
    }
}
