/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis;

import java.util.Set;
import pgentity.PGEntity;

/**
 *
 * @author KieuAnh
 */
public interface RedisSet extends PGEntity
{
    public Set<String> getAll();
    public void add(String... IDs);
    public void remove(String... IDs);
    public boolean contains(String ID);
    public int size();
}
