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
abstract class JSONMapArray<E extends JSONable> extends JSONMap<Integer, E>
{
    @Override
    protected Integer keyFromString(String o)
    {
        return Integer.parseInt(o);
    }
}
