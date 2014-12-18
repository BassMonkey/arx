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
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;
import org.deidentifier.arx.framework.check.groupify.IHashGroupify;
import org.deidentifier.arx.framework.data.Data;
import org.deidentifier.arx.framework.data.GeneralizationHierarchy;
import org.deidentifier.arx.framework.lattice.Node;

import com.carrotsearch.hppc.IntIntOpenHashMap;

/**
 * This class provides an efficient implementation of a non-monotonic and
 * non-uniform entropy metric. It avoids a cell-by-cell process by utilizing a
 * three-dimensional array that maps identifiers to their frequency for all
 * quasi-identifiers and generalization levels. It further reduces the overhead
 * induced by subsequent calls by caching the results for previous columns and
 * generalization levels. It takes supressed tuples into account by adding the
 * information loss induced by suppressing the transformed representation of the
 * outliers.
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class MetricNMEntropy extends MetricEntropy {

    /** SVUID. */
    private static final long serialVersionUID = 5789738609326541247L;
    
    /**
     * Creates a new instance.
     */
    protected MetricNMEntropy() {
        super(false, false);
    }
    
    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricEntropy#toString()
     */
    @Override
    public String toString() {
        return "Non-Monotonic Non-Uniform Entropy";
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricEntropy#getInformationLossInternal(org.deidentifier.arx.framework.lattice.Node, org.deidentifier.arx.framework.check.groupify.IHashGroupify)
     */
    @Override
    protected InformationLossWithBound<InformationLossDefault> getInformationLossInternal(final Node node, final IHashGroupify g) {

        // Obtain "standard" value
        final InformationLossDefault originalInfoLossDefault = node.getLowerBound() != null ? (InformationLossDefault)node.getLowerBound() :
                                                               super.getInformationLossInternal(node, g).getInformationLoss();
        
        // Compute loss induced by suppression
        double originalInfoLoss = originalInfoLossDefault.getValue();
        double suppressedTuples = 0;
        double additionalInfoLoss = 0;
        final IntIntOpenHashMap[] original = new IntIntOpenHashMap[node.getTransformation().length];
        for (int i = 0; i < original.length; i++) {
            original[i] = new IntIntOpenHashMap();
        }

        // Compute counts for suppressed values in each column 
        // m.count only counts tuples from the research subset
        HashGroupifyEntry m = g.getFirstEntry();
        while (m != null) {
            if (!m.isNotOutlier && m.count > 0) {
                suppressedTuples += m.count;
                for (int i = 0; i < original.length; i++) {
                    original[i].putOrAdd(m.key[i], m.count, m.count);
                }
            }
            m = m.nextOrdered;
        }

        // Evaluate entropy for suppressed tuples
        if (suppressedTuples != 0){
	        for (int i = 0; i < original.length; i++) {
	            IntIntOpenHashMap map = original[i];
	            for (int j = 0; j < map.allocated.length; j++) {
	                if (map.allocated[j]) {
	                    double count = map.values[j];
	                    additionalInfoLoss += count * MetricEntropy.log2(count / suppressedTuples);
	                }
	            }
	        }
        }
        
        // Return sum of both values
        return new InformationLossDefaultWithBound(round(originalInfoLoss - additionalInfoLoss), originalInfoLossDefault.getValue());
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricEntropy#getLowerBoundInternal(org.deidentifier.arx.framework.lattice.Node)
     */
    @Override
    protected InformationLossDefault getLowerBoundInternal(Node node) {
        return super.getInformationLossInternal(node, null).getInformationLoss();
    }
    
    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricEntropy#getLowerBoundInternal(org.deidentifier.arx.framework.lattice.Node, org.deidentifier.arx.framework.check.groupify.IHashGroupify)
     */
    @Override
    protected InformationLossDefault getLowerBoundInternal(Node node,
                                                           IHashGroupify groupify) {
        return getLowerBoundInternal(node);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.metric.MetricEntropy#initializeInternal(org.deidentifier.arx.DataDefinition, org.deidentifier.arx.framework.data.Data, org.deidentifier.arx.framework.data.GeneralizationHierarchy[], org.deidentifier.arx.ARXConfiguration)
     */
    @Override
    protected void initializeInternal(final DataDefinition definition,
                                      final Data input, 
                                      final GeneralizationHierarchy[] ahierarchies, 
                                      final ARXConfiguration config) {
        super.initializeInternal(definition, input, ahierarchies, config);
    }
}
