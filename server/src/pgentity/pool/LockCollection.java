/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.pool;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

/**
 * Not thread-safe, used in BlockingCache with ThreadLocal
 * @author KieuAnh
 */
class LockCollection
{
    private final Set<Lock> locks = new HashSet();
    
    public void add(Lock lock)
    {
        if (this.locks.contains(lock))
        {
            return;
        }
        
        if (!lock.tryLock())
        {
            Set<Lock> heldLocks = this.releaseAll();
            
            lock.lock();
            // waits...
            this.locks.add(lock);
            
            for (Lock heldLock : heldLocks) {
                this.add(heldLock);
            }
        }
        else
        {
            this.locks.add(lock);
        }
    }
    
    public void clear()
    {
        this.releaseAll();
    }
    
    public void release(Lock lock)
    {
        if (this.locks.contains(lock))
        {
            lock.unlock();
            this.locks.remove(lock);
        }
    }
    
    private Set<Lock> releaseAll()
    {
        if (!this.locks.isEmpty())
        {
            Set<Lock> copy = new HashSet(this.locks);
            this.locks.clear();

            for (Lock lock : copy) {
                lock.unlock();
            }

            return copy;
        }
        
        return Collections.EMPTY_SET;
    }
}