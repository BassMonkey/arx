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

import org.deidentifier.arx.DataHandle;
import org.deidentifier.arx.DataHandleSubset;
import org.deidentifier.arx.RowSet;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * A data provider based on a data handle.
 *
 * @author Fabian Prasser
 */
public class DataTableHandleDataProvider implements IDataProvider {

    /**  TODO */
    private final DataTableContext context;

    /**
     * Creates a new instance.
     *
     * @param context
     */
    public DataTableHandleDataProvider(final DataTableContext context) {
        this.context = context;
    }

    /* (non-Javadoc)
     * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        DataHandle data = context.getHandle();
        if (data == null || data.isOrphaned()) { return 0; }
        return data.getNumColumns() + (context.getRows() != null ? 1 : 0);
    }

    /* (non-Javadoc)
     * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getDataValue(int, int)
     */
    @Override
    public Object getDataValue(int arg0, int arg1) {
        DataHandle data = context.getHandle();
        if (data == null) { return null; }
        RowSet rows = context.getRows();
        if (rows == null) {
            return data.getValue(arg1, arg0);
        } else if (arg0 == 0) {
            // Remap row index for subset if in subset view
            if (data instanceof DataHandleSubset){
                int[] subset = ((DataHandleSubset)data).getSubset();
                arg1 = subset[arg1];
            }
            return rows.contains(arg1);
        } else {
            return data.getValue(arg1, arg0 - 1);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getRowCount()
     */
    @Override
    public int getRowCount() {
        DataHandle data = context.getHandle();
        if (data == null || data.isOrphaned()) { return 0; }
        return data.getNumRows();
    }

    /* (non-Javadoc)
     * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#setDataValue(int, int, java.lang.Object)
     */
    @Override
    public void setDataValue(final int arg0, final int arg1, final Object arg2) {
        return;
    }
}
