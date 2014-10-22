/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libCore.config;

import java.util.*;

/**
 *
 * @author quocpt
 */
class CacheByteData implements ICacheData
{
    private byte[] bData;

    public static CacheByteData wrap(byte[] b)
    {
	return new CacheByteData(b);
    }

    public CacheByteData()
    {
	bData = null;
    }

    public CacheByteData(byte[] b)
    {
	bData = b;
    }

    public byte[] getData()
    {
	return bData;
    }

    @Override
    public ICacheData makeNull()
    {
	bData = null;
	return this;
    }

    @Override
    public int getSize()
    {
	if (bData == null)
	    return 0;
	return bData.length;
    }

    @Override
    public int hashCode()
    {
	int hash = 3;
	hash = 79 * hash + Arrays.hashCode(this.bData);
	return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	final CacheByteData other = (CacheByteData) obj;
	return Arrays.equals(this.bData, other.bData);
    }
}