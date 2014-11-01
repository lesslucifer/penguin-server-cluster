/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

/**
 *
 * @author KieuAnh
 */
abstract class JSONMapString<E extends JSONable> extends JSONMap<String,E>
{
    @Override
    protected String keyFromString(String s)
    {
        return s;
    }
}
