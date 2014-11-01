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
interface EntityLoader
{
    PooledEntity load(EntityIdentifier eid);
    List<PooledEntity> loadMulti(List<EntityIdentifier> eids);
}
