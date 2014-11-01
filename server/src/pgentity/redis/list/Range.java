/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pgentity.redis.list;

/**
 *
 * @author KieuAnh
 */
class Range {
    private int min, max;

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
    
    public int getLength()
    {
        return this.max - this.min;
    }
    
    public void setLength(int len)
    {
        this.max = this.min + len;
    }
    
    public boolean contains(int index)
    {
        return min <= index && max > index;
    }
    
    public boolean contains(int smin, int smax)
    {
        return min <= smin && max >= smax;
    }
    
    public int transform(int index)
    {
        if (min <= index && max >= index)
        {
            return index - min;
        }
        
        return -1;
    }

    @Override
    public String toString() {
        return "Range{" + "min=" + min + ", max=" + max + '}';
    }
}
