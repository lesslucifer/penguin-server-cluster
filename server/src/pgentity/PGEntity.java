/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity;

import pgentity.pool.PooledEntity;

/**
 * Provide an layer for get/set info from database
 * An entity is a map from database object into programming environment
 * NOTE: Implementers should use static factory for auto-update from db rather
 * than normal constructor
 * @author KieuAnh
 */
public interface PGEntity extends PooledEntity
{

    /**
     * Get data from database (sync)
     */
    void updateFromDB();
    
    /**
     * Set data into database (sync)
     */
    void saveToDB();
    
    /**
     * Some entity have <i>isExist</i> method
     * Use for check if entity is existed in database or not
     * This method is safe - but ugly, general used by entity which
     * created by constructor not static factory (which are recommended)
     */
    // boolean isExist();
    
    /**
     * Some entity have <i>destroy</i> method - use for remove
     * this entity from database SAFELY (must be safe)
     */
    // void destroy();
}