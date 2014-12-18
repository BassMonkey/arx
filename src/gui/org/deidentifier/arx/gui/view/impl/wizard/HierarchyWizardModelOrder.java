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

package org.deidentifier.arx.gui.view.impl.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.deidentifier.arx.DataType;
import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderOrderBased;

/**
 * A model for order-based builders.
 *
 * @author Fabian Prasser
 * @param <T>
 */
public class HierarchyWizardModelOrder<T> extends HierarchyWizardModelGrouping<T> {
    
    /** Var. */
    private final transient String[] data;

    /** Locale. */
    private Locale                   locale;

    /**
     * Constructor to create an initial definition.
     *
     * @param dataType
     * @param locale
     * @param data
     */
    public HierarchyWizardModelOrder(final DataType<T> dataType, final Locale locale, String[] data) {
        super(data, dataType, false);
        this.data = data;
        this.locale = locale;
        this.internalSort(super.getDataType());
        this.update();
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.gui.view.impl.wizard.HierarchyWizardModelAbstract#getBuilder(boolean)
     */
    @Override
    public HierarchyBuilderOrderBased<T> getBuilder(boolean serializable) throws Exception {
        
        HierarchyBuilderOrderBased<T> builder;
        if (serializable) {
            builder = HierarchyBuilderOrderBased.create(super.getDataType(), data);
        } else {
            builder = HierarchyBuilderOrderBased.create(super.getDataType(), false);
        }
        
        builder.setAggregateFunction(this.getDefaultFunction());
        int level = 0;
        for (List<HierarchyWizardGroupingGroup<T>> list : super.getModelGroups()) {
            for (HierarchyWizardGroupingGroup<T> group : list){
                builder.getLevel(level).addGroup(group.size, group.function);
            }
            level++;
        }
        
        return builder;
    }
    
    /**
     * Returns the current locale.
     *
     * @return
     */
    public Locale getLocale() {
        if (locale == null) {
            return Locale.getDefault();
        } else {
            return locale;
        }
    }
    
    /**
     * Moves an item down.
     *
     * @param index
     * @return
     */
    public boolean moveDown(int index) {
        if (index>=data.length-1 || index<0) return false;
        String temp = data[index+1];
        data[index+1] = data[index];
        data[index] = temp;
        update();
        return true;
    }
    
    /**
     * Moves an item up.
     *
     * @param index
     * @return
     */
    public boolean moveUp(int index) {
        if (index<=0) return false;
        String temp = data[index-1];
        data[index-1] = data[index];
        data[index] = temp;
        update();
        return true;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.gui.view.impl.wizard.HierarchyWizardModelAbstract#parse(org.deidentifier.arx.aggregates.HierarchyBuilder)
     */
    @Override
    public void parse(HierarchyBuilder<T> builder) throws IllegalArgumentException {
        
        if (!(builder instanceof HierarchyBuilderOrderBased)) {
            return;
        }
        super.parse((HierarchyBuilderOrderBased<T>)builder);
    }

    /**
     * Sorts the data.
     *
     * @param type
     * @return
     */
    public boolean sort(DataType<?> type){
        boolean result = internalSort(type);
        update();
        return result;
    }
    

    /**
     * Sort.
     *
     * @param <U>
     * @param type
     * @return successful, or not
     */
    private <U> boolean internalSort(final DataType<U> type) {

        try {
            ArrayList<U> list = new ArrayList<U>();
            for (String s : data) list.add(type.parse(s));
            Collections.sort(list, type);
            for (int i = 0; i < list.size(); i++) {
                data[i] = type.format(list.get(i));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.gui.view.impl.wizard.HierarchyWizardModelAbstract#build()
     */
    @Override
    protected void build() {
        super.hierarchy = null;
        super.error = null;
        super.groupsizes = null;

        if (data==null) return;
        
        HierarchyBuilderOrderBased<T> builder = null;
        try {
            builder = getBuilder(false);
        } catch (Exception e){
            super.error = e.getMessage();
            return;
        }
        
        String error = builder.isValid();
        if (error != null) {
            super.error = error;
            return;
        }
        
        try {
            super.groupsizes = builder.prepare(data);
        } catch(Exception e){
            super.error = e.getMessage();
            return;
        }
        
        try {
            super.hierarchy = builder.build();
        } catch(Exception e){
            super.error = e.getMessage();
            return;
        }
    }
}
