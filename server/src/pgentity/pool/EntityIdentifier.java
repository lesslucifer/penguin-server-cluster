/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import java.util.Arrays;

/**
 *
 * @author KieuAnh
 */
class EntityIdentifier
{
    private final Class entityClass;
    private final Object[] entityParam;

    private EntityIdentifier(Class clazz, Object[] params) {
        this.entityClass = clazz;
        this.entityParam = params;
    }
    public static EntityIdentifier getEID(Class clazz, Object[] params)
    {
        return new EntityIdentifier(clazz, params);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.entityClass != null ? this.entityClass.hashCode() : 0);
        hash = 43 * hash + (this.entityParam != null ? Arrays.hashCode(entityParam) : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EntityIdentifier other = (EntityIdentifier) obj;
        if (this.entityClass != other.entityClass && (this.entityClass == null || !this.entityClass.equals(other.entityClass))) {
            return false;
        }
        if (!Arrays.deepEquals(this.entityParam, other.entityParam)) {
            return false;
        }
        return true;
    }

    public Class getEntityClass() {
        return entityClass;
    }

    public Object[] getEntityParam() {
        return entityParam;
    }

    @Override
    public String toString() {
        return "EntityIdentifier{" + "entityClass=" + entityClass + ", entityParam=" + Arrays.toString(entityParam) + '}';
    }
}
