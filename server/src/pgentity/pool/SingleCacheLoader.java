/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

/**
 *
 * @author KieuAnh
 */
public interface SingleCacheLoader<K, V> {
    V load(K key);
}
