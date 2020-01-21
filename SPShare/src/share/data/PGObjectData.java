/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share.data;

/**
 *
 * @author suaongmattroi
 */
public class PGObjectData implements IPGData {

    private final String _caller;
    
    private final String _method;
    
    private final Object _data;
    
    private final long _time;
    
    private final PGDataType _type;
    
    public PGObjectData(String caller, String method, Object data, long time, PGDataType type)
    {
        this._caller = caller;
        this._method = method;
        this._data = data;
        this._time = time;
        this._type = type;
    }
    
    @Override
    public String caller() {
        return _caller;
    }
    
    @Override
    public String method() {
        return _method;
    }

    @Override
    public Object data() {
        return _data;
    }

    @Override
    public long now() {
        return _time;
    }

    @Override
    public PGDataType type() {
        return _type;
    }
}
