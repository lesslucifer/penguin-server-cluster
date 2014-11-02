/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import share.PGException;

/**
 *
 * @author KieuAnh
 */
public class EntityPool {
    private final LockManager<EntityIdentifier> lockManager = new LockManager();
    
    private EntityPool()
    {
        super();
        this.pool = new SingleBlockedPoolCached(buildInnerCache(), buildLocks());
    }
    
    private static final EntityPool inst = new EntityPool();
    
    public static EntityPool inst()
    {
        return inst;
    }
    
    private final SingleBlockedPoolCached<EntityIdentifier, PooledEntity> pool;

    private SingleloadCache<EntityIdentifier, PooledEntity> buildInnerCache()
    {
        Cache<EntityIdentifier, PooledEntity> rootCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .maximumSize(5000)
            .build();
        
        return new SingleloadCache(rootCache, PooledEntityLoader.inst(), 
                this.lockManager, Striped.lock(64));
    }
    
    private Striped<Lock> buildLocks()
    {
        return Striped.lock(256);
    }
    
    public <E extends PooledEntity> E get(Class<E> clazz, Object... params)
    {
        try
        {
            EntityIdentifier eid = EntityIdentifier.getEID(clazz, params);
            return (E) pool.get(eid);
        }
        catch (Exception ex)
        {
            PGException.pgThrow(ex);
        }
        
        return null;
    }
    
    public <E extends PooledEntity> void put(E entity, Class<E> clazz, Object... params)
    {
        try
        {
            EntityIdentifier eid = EntityIdentifier.getEID(clazz, params);
            pool.put(eid, entity);
        }
        catch (Exception ex)
        {
            PGException.pgThrow(ex);
        }
    }
    
    public <E extends PooledEntity> void remove(Class<E> clazz, Object... params)
    {
        try
        {
            EntityIdentifier eid = EntityIdentifier.getEID(clazz, params);
            pool.remove(eid);
        }
        catch (Exception ex)
        {
            PGException.pgThrow(ex);
        }
    }
    
    public void releaseAllThreadResources()
    {
        this.pool.releaseAll();
    }
    
    private static class PooledEntityLoader
        implements SingleCacheLoader<EntityIdentifier, PooledEntity>
    {
        private final Map<Class, Method> factoryMethods = new ConcurrentHashMap();
    
        private PooledEntityLoader()
        {
            super();
        }
        
        private static final PooledEntityLoader inst = new PooledEntityLoader();
        
        public static PooledEntityLoader inst()
        {
            return inst;
        }
        
        @Override
        public PooledEntity load(EntityIdentifier eid) {
            try
            {
                Method factory = factoryMethods.get(eid.getEntityClass());
                if (factory == null)
                {
                    factory = findFactoryMethod(eid.getEntityClass());
                    if (factory != null)
                    {
                        factory.setAccessible(true);
                        factoryMethods.put(eid.getEntityClass(), factory);
                    }
                }
                
                if (factory != null)
                {
                    Object[] params = eid.getEntityParam();
                    Object ret = factory.invoke(null, params);
                    return (PooledEntity) ret;
                }
            }
            catch (Exception ex)
            {
                PGException.pgThrow(ex);
            }
            
            return null;
        }
        
        /**
         * [UNUSED]
         * @param eids
         * @return 
         */
        public List<PooledEntity> loadMulti(List<EntityIdentifier> eids)
        {
            /*
            try
            {
                Class clazz = null;
                for (EntityIdentifier eid : eids) {
                    if (clazz == null)
                    {
                        clazz = eid.getEntityClass();
                    }
                    
                    PGException.Assert(clazz.equals(eid.getEntityClass()),
                            PGError.UNDEFINED, "Cannot load multi entity");
                }
                
                Method factory = facotryMultiMethods.get(clazz);
                if (factory == null)
                {
                    factory = findMultiFactoryMethod(clazz);
                    if (factory != null)
                    {
                        factory.setAccessible(true);
                        facotryMultiMethods.put(clazz, factory);
                    }
                }
                
                if (factory != null)
                {
                    List<Object[]> params = new ArrayList(eids.size());
                    for (EntityIdentifier eid : eids) {
                        params.add(eid.getEntityParam());
                    }
                    
                    Object ret = factory.invoke(null, params);
                    return (List) ret;
                }
            }
            catch (Exception ex)
            {
                PGException.pgThrow(ex);
            }
            
            List<PooledEntity> PEs = new ArrayList(eids.size());
            for (EntityIdentifier eid : eids) {
                PEs.add(load(eid));
            }
            
            return PEs;
            */
            
            return null;
        }
        
        private Method findFactoryMethod(Class clazz)
        {
            EntityFactory eFactAnn = (EntityFactory)
                    clazz.getAnnotation(EntityFactory.class);
            String factorier = eFactAnn.factorier();
            
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (factorier.equals(method.getName()))
                {
                    return method;
                }
            }
            
            return null;
        }
        
        private Method findMultiFactoryMethod(Class clazz)
        {/*
            EntityFactory eFactAnn = (EntityFactory)
                    clazz.getAnnotation(EntityFactory.class);
            String factorier = eFactAnn.factorierMulti();
            
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (factorier.equals(method.getName()))
                {
                    return method;
                }
            }
            
            return null;
         */
            
            return null;
        }
    }
}
