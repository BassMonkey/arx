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

package org.deidentifier.arx.metric;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.framework.check.groupify.IHashGroupify;
import org.deidentifier.arx.framework.data.Data;
import org.deidentifier.arx.framework.data.GeneralizationHierarchy;
import org.deidentifier.arx.framework.lattice.Node;

/**
 * This class provides an implementation of a monotonic weighted precision metric.
 * This metric will respect attribute weights defined in the configuration.
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class MetricPrecision extends MetricWeighted<InformationLossDefault> {

    /** SVUID. */
    private static final long serialVersionUID = -7612335677779934529L;

    /** Height. */
    private int[]             maxLevels;

    /**
     * Creates a new instance.
     */
    protected MetricPrecision() {
        super(true, true);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.Metric#createMaxInformationLoss()
     */
    @Override
    public InformationLoss<?> createMaxInformationLoss() {
        return new InformationLossDefault(1d);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.Metric#createMinInformationLoss()
     */
    @Override
    public InformationLoss<?> createMinInformationLoss() {
        return new InformationLossDefault(0d);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.Metric#toString()
     */
    @Override
    public String toString() {
        return "Monotonic Precision";
    }

    /**
     * @return the heights
     */
    protected int[] getHeights() {
        return maxLevels;
    }

    /**
     * Returns the number of cells.
     *
     * @return
     */
    protected double getCells() {
        return 0d;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.Metric#getInformationLossInternal(org.deidentifier.arx.framework.lattice.Node, org.deidentifier.arx.framework.check.groupify.IHashGroupify)
     */
    @Override
    protected InformationLossWithBound<InformationLossDefault> getInformationLossInternal(final Node node, final IHashGroupify g) {

        double result = 0;
        final int[] transformation = node.getTransformation();
        for (int i = 0; i < transformation.length; i++) {
            double weight = weights != null ? weights[i] : 1d;
            double level = (double) transformation[i];
            result += maxLevels[i] == 0 ? 0 : (level / (double) maxLevels[i]) * weight;
        }
        result /= (double) transformation.length;
        return new InformationLossDefaultWithBound(result, result);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricWeighted#getLowerBoundInternal(org.deidentifier.arx.framework.lattice.Node)
     */
    @Override
    protected InformationLossDefault getLowerBoundInternal(Node node) {
        return this.getInformationLossInternal(node, null).getLowerBound();
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricWeighted#getLowerBoundInternal(org.deidentifier.arx.framework.lattice.Node, org.deidentifier.arx.framework.check.groupify.IHashGroupify)
     */
    @Override
    protected InformationLossDefault getLowerBoundInternal(Node node,
                                                           IHashGroupify groupify) {
        return getLowerBoundInternal(node);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricWeighted#initializeInternal(org.deidentifier.arx.DataDefinition, org.deidentifier.arx.framework.data.Data, org.deidentifier.arx.framework.data.GeneralizationHierarchy[], org.deidentifier.arx.ARXConfiguration)
     */
    @Override
    protected void initializeInternal(final DataDefinition definition,
                                      final Data input, 
                                      final GeneralizationHierarchy[] hierarchies, 
                                      final ARXConfiguration config) {
        super.initializeInternal(definition, input, hierarchies, config);

        // Initialize maximum levels
        maxLevels = new int[hierarchies.length];
        for (int j = 0; j < maxLevels.length; j++) {
            maxLevels[j] = hierarchies[j].getArray()[0].length - 1;
        }
    }
}
