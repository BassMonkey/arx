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

package org.deidentifier.arx.metric.v2;

import org.deidentifier.arx.metric.InformationLoss;

/**
 * This class implements an information loss which can be represented as a
 * decimal number per quasi-identifier. As an aggregate function, the geometric mean
 * is applied. To handle zero values while not violating guarantees required for pruning
 * based on lower bounds, 1d is added to every individual value and 1d is subtracted from the
 * final result.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class ILMultiDimensionalGeometricMean extends AbstractILMultiDimensionalReduced {

    /** SVUID. */
    private static final long serialVersionUID = 621501985571033348L;

    /**
     * Creates a new instance.
     *
     * @param values
     * @param weights
     */
    ILMultiDimensionalGeometricMean(final double[] values,
                                    final double[] weights) {
        super(values, weights);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.v2.AbstractILMultiDimensionalReduced#clone()
     */
    @Override
    public InformationLoss<double[]> clone() {
        return new ILMultiDimensionalGeometricMean(getValues(),
                                                   getWeights());
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.v2.AbstractILMultiDimensionalReduced#getAggregate()
     */
    @Override
    protected double getAggregate() {
        double[] values = getValues();
        double[] weights = getWeights();
        double result = 1.0d;
        for (int i = 0; i < values.length; i++) {
            result *= Math.pow((values[i] + 1d) * weights[i], 1.0d / (double) values.length) - 1d;
        }
        return result;
    }
}
