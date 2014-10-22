/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import java.util.List;

/**
 *
 * @author KieuAnh
 */
public interface MultiCacheLoader<K, V> extends SingleCacheLoader<K, V> {
    List<V> loadMulti(List<K> keys);
}
