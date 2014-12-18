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
 * decimal number per quasi-identifier.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class ILMultiDimensionalArithmeticMean extends AbstractILMultiDimensionalReduced {

    /** SVUID. */
    private static final long serialVersionUID = 5142553922311764185L;
   
    /**
     * Creates a new instance. For backwards compatibility
     * 
     * @param value
     */
    ILMultiDimensionalArithmeticMean(double value) {
        super(new double[]{value}, new double[]{1.0d});
    }

    /**
     * Creates a new instance.
     *
     * @param values
     * @param weights
     */
    ILMultiDimensionalArithmeticMean(final double[] values,
                                     final double[] weights) {
        super(values, weights);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.v2.AbstractILMultiDimensionalReduced#clone()
     */
    @Override
    public InformationLoss<double[]> clone() {
        return new ILMultiDimensionalArithmeticMean(getValues(),
                                                    getWeights());
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.v2.AbstractILMultiDimensionalReduced#getAggregate()
     */
    @Override
    protected double getAggregate() {
        double[] values = getValues();
        double[] weights = getWeights();
        double result = 0d;
        for (int i = 0; i < values.length; i++) {
            result += (values[i] / (double) values.length) * weights[i];
        }
        return result;
    }
}
