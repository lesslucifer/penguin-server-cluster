/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db.mysql;

import java.util.HashMap;

/**
 *
 * @author salm
 */
public class DataRow extends HashMap<String, String>
{
    public DataRow(int cap)
    {
        super(cap);
    }
}