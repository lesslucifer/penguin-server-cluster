/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker;

import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author suaongmattroi
 */
public class SpFrCheckerModel extends AbstractListModel{

    private List<String> addresses;
    
    public SpFrCheckerModel()
    {
        this.addresses = new LinkedList();
    }
    
    @Override
    public int getSize()
    {
        return this.addresses.size();
    }

    @Override
    public Object getElementAt(int index)
    {
        return this.addresses.get(index);
    }
    
    public void addAddress(String address)
    {
        this.addresses.add(address);
        this.fireIntervalAdded(this, this.addresses.size() - 1, this.addresses.size());
    }
    
    public void removeAddress(String address)
    {
        int index = addresses.indexOf(address);
        this.fireIntervalRemoved(this, index, index + 1);
        this.addresses.remove(address);
    }
    
    public void clearAll()
    {
        this.fireIntervalRemoved(this, 0, addresses.size());
        this.addresses.clear();
    }
}
