/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spnodechecker;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import minaconnection.ClientHandlerFactory;
import minaconnection.MinaAddress;
import minaconnection.SimpleRequestPoolFactory;
import minaconnection.interfaces.IClientHandler;
import minaconnection.interfaces.IRequestPool;
import target.Request;
import target.Response;
import target.Target;

/**
 *
 * @author suaongmattroi
 */
class MinaTarget implements Target {

    private final MinaAddress _address;
    private final IRequestPool _pool;
    
    IClientHandler _cHandler;
    
    private Boolean _isGood;
    
    public MinaTarget(MinaAddress address) {
        
        goodConnection();
        _pool = SimpleRequestPoolFactory.create();
        _pool.registerExceptionCaught((adr, cause) -> {
            System.out.println(adr.getAddress() + ":" + Integer.toString(adr.getPort()));
            badConnection();
        });
        
        this._address = address;
    }
    
    @Override
    public Boolean isGood() {
        return _isGood;
    }
    
    private void goodConnection() {
        _isGood = true;
    }
    
    private void badConnection() {
        _isGood = false;
        
        if(_cHandler != null)
        {
            HashMap<String, Object> data = new HashMap();
            data.put("state", false);
            _cHandler.callback(data);
        }
    }
    
    @Override
    public Object doAMF(Request request) throws InvocationTargetException {
        try {
            _cHandler = ClientHandlerFactory.create();
            _pool.request(_address, request, _cHandler);
            return _cHandler.doReq();
        } catch (Exception ex) {
            throw new InvocationTargetException(ex);
        }
    }

    @Override
    public Object doHTTP(Request request) throws InvocationTargetException {
        try {
            _cHandler = ClientHandlerFactory.create();
            _pool.request(_address, request, _cHandler);
            Response resp = (Response) _cHandler.doReq();
            return resp.getData();
        } catch (Exception ex) {
            throw new InvocationTargetException(ex);
        }
    }
}
