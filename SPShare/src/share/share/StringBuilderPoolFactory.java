/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package share;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 *
 * @author KieuAnh
 */
public class StringBuilderPoolFactory
{
    private static class PooledStringBuilderFactory
        extends BasePooledObjectFactory<StringBuilder>
    {
        private PooledStringBuilderFactory()
        {
            super();
        }
        
        private static final PooledStringBuilderFactory inst = new PooledStringBuilderFactory();
        
        public static PooledStringBuilderFactory inst()
        {
            return inst;
        }
        
        @Override
        public StringBuilder create() throws Exception {
            return new StringBuilder();
        }

        @Override
        public PooledObject<StringBuilder> wrap(StringBuilder t) {
            return new DefaultPooledObject<StringBuilder>(t);
        }

        @Override
        public void passivateObject(PooledObject<StringBuilder> p) throws Exception {
            p.getObject().setLength(0);
            super.passivateObject(p);
        }
    }
    
    private StringBuilderPoolFactory()
    {
        super();
    }
    
    public static GenericObjectPool<StringBuilder> makePool()
    {
        return new GenericObjectPool<StringBuilder>
            (PooledStringBuilderFactory.inst());
    }
}
