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

package org.deidentifier.arx.gui.view.impl.common.datatable;

import org.deidentifier.arx.RowSet;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator;

/**
 * A label accumulator for the data view.
 *
 * @author Fabian Prasser
 */
public class DataTableConfigLabelAccumulator implements IConfigLabelAccumulator {

    /**  TODO */
    private final DataTableContext context;
    
    /**  TODO */
    private final NatTable         table;

    /**
     * Creates a new instance.
     *
     * @param table
     * @param context
     */
    public DataTableConfigLabelAccumulator(NatTable table, DataTableContext context) {
        this.context = context;
        this.table = table;
    }

    /* (non-Javadoc)
     * @see org.eclipse.nebula.widgets.nattable.layer.cell.IConfigLabelAccumulator#accumulateConfigLabels(org.eclipse.nebula.widgets.nattable.layer.LabelStack, int, int)
     */
    @Override
    public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {

        int[] groups = context.getGroups();
        RowSet rows = context.getRows();

        if (table != null && groups != null) {
            int row = table.getRowIndexByPosition(rowPosition + 1);
            configLabels.addLabel("background" + (groups[row] % 2)); //$NON-NLS-1$
            if (row < groups.length - 1 && groups[row] != groups[row + 1]) {
                configLabels.addLabel(DataTableDecorator.BOTTOM_LINE_BORDER_LABEL);
            }
        }

        if (table != null && rows != null) {
            int column = table.getColumnIndexByPosition(columnPosition + 1);
            if (column == 0) {
                configLabels.addLabel("checkbox"); //$NON-NLS-1$
            }
        }
    }
}
