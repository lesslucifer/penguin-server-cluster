/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share.data;

import java.util.Map;

/**
 *
 * @author suaongmattroi
 */
public class PGMapData extends PGObjectData{
    
    public PGMapData(String caller, String method, Map data, long time, PGDataType type)
    {
        super(caller, method, data, time, type);
    }
}
