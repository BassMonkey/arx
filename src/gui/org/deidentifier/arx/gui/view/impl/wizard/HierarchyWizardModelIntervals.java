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

import java.util.List;

import org.deidentifier.arx.DataType;
import org.deidentifier.arx.aggregates.HierarchyBuilder;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased;
import org.deidentifier.arx.aggregates.HierarchyBuilderIntervalBased.Range;

/**
 * A model for interval-based builders.
 *
 * @author Fabian Prasser
 * @param <T>
 */
public class HierarchyWizardModelIntervals<T> extends HierarchyWizardModelGrouping<T>{
    
    /** Var. */
    private final String[] data;

    /**
     * Constructor to create an initial definition.
     *
     * @param dataType
     * @param data
     */
    public HierarchyWizardModelIntervals(final DataType<T> dataType, String[] data) {
        super(data, dataType, true);
        this.data = data;
        this.update();
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.gui.view.impl.wizard.HierarchyWizardModelAbstract#getBuilder(boolean)
     */
    @Override
    public HierarchyBuilderIntervalBased<T> getBuilder(boolean serializable) throws Exception{
        HierarchyBuilderIntervalBased<T> builder = HierarchyBuilderIntervalBased.create(super.getDataType(),
                                                    new Range<T>(super.getLowerRange().repeat,
                                                                 super.getLowerRange().snap,
                                                                 super.getLowerRange().label),
                                                    new Range<T>(super.getUpperRange().repeat,
                                                                 super.getUpperRange().snap,
                                                                 super.getUpperRange().label));
        
        builder.setAggregateFunction(this.getDefaultFunction());

        for (HierarchyWizardGroupingInterval<T> interval : super.getIntervals()) {
            builder.addInterval(interval.min, interval.max, interval.function);
        }
        
        int level = 0;
        for (List<HierarchyWizardGroupingGroup<T>> list : super.getModelGroups()) {
            for (HierarchyWizardGroupingGroup<T> group : list){
                builder.getLevel(level).addGroup(group.size, group.function);
            }
            level++;
        }
        
        return builder;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.gui.view.impl.wizard.HierarchyWizardModelAbstract#parse(org.deidentifier.arx.aggregates.HierarchyBuilder)
     */
    @Override
    public void parse(HierarchyBuilder<T> builder) throws IllegalArgumentException {
        
        if (!(builder instanceof HierarchyBuilderIntervalBased)) {
            return;
        }
        super.parse((HierarchyBuilderIntervalBased<T>)builder);
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
        
        HierarchyBuilderIntervalBased<T> builder = null;
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
