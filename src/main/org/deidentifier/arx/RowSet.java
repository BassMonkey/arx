/*
 * ARX: Powerful Data Anonymization
 * Copyright (C) 2012 - 2014 Florian Kohlmayer, Fabian Prasser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.deidentifier.arx;

import java.io.Serializable;

/**
 * A set of rows.
 *
 * @author Fabian Prasser
 */
public class RowSet implements Serializable, Cloneable {

    /**  TODO */
    private static final long serialVersionUID = 1492499772279795327L;
    
    /**  TODO */
    private static final int   ADDRESS_BITS_PER_UNIT = 6;
    
    /**  TODO */
    private static final int   BIT_INDEX_MASK        = 63;
    
    /**
     * 
     *
     * @param data
     * @return
     */
    public static RowSet create(Data data){
        return new RowSet(data);
    }
    
    /**  TODO */
    private final long[]       array;
    
    /**  TODO */
    private final int          length;

    /**  TODO */
    private int                size;
    
    /**
     * 
     *
     * @param data
     */
    private RowSet(Data data) {
        this.length = data.getHandle().getNumRows();
        int chunks = (int) (Math.ceil((double) this.length / 64d));
        array = new long[chunks];
    }
    
    /**
     * 
     *
     * @param length
     */
    private RowSet(int length) {
        this.length = length;
        int chunks = (int) (Math.ceil((double) this.length / 64d));
        array = new long[chunks];
    }

    /**
     * 
     *
     * @param rowIndex
     */
    public void add(int rowIndex) {
        int offset = rowIndex >> ADDRESS_BITS_PER_UNIT;
        long temp = array[offset];
        array[offset] |= 1L << (rowIndex & BIT_INDEX_MASK);
        size += array[offset] != temp ? 1 : 0; 
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public RowSet clone() {
        RowSet set = new RowSet(this.length);
        set.size = this.size;
        System.arraycopy(this.array, 0, set.array, 0, this.array.length);
        return set;
    }

    /**
     * 
     *
     * @param rowIndex
     * @return
     */
    public boolean contains(int rowIndex) {
        return ((array[rowIndex >> ADDRESS_BITS_PER_UNIT] & (1L << (rowIndex & BIT_INDEX_MASK))) != 0);
    }

    /**
     * 
     *
     * @return
     */
    public int length() {
        return this.length;
    }
    
    /**
     * 
     *
     * @param rowIndex
     */
    public void remove(int rowIndex){
        int offset = rowIndex >> ADDRESS_BITS_PER_UNIT;
        long temp = array[offset];
        array[offset] &= ~(1L << (rowIndex & BIT_INDEX_MASK));
        size -= array[offset] != temp ? 1 : 0; 
    }

    /**
     * 
     *
     * @return
     */
    public int size() {
        return this.size;
    }
    
    /**
     * 
     *
     * @param rowIndex1
     * @param rowIndex2
     */
    public void swap(int rowIndex1, int rowIndex2) {
        
        final boolean temp1 = contains(rowIndex1);
        final boolean temp2 = contains(rowIndex2);
        
        if (temp2) {
            add(rowIndex1);
        } else {
            remove(rowIndex1);
        }
        if (temp1) {
            add(rowIndex2);
        } else {
            remove(rowIndex2);
        }
    }
}
