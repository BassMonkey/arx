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

package org.deidentifier.arx.criteria;

import org.deidentifier.arx.framework.check.distribution.Distribution;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;

/**
 * The entropy l-diversity criterion.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class EntropyLDiversity extends LDiversity {

    /**  TODO */
    private static final long   serialVersionUID = -354688551915634000L;

    /** Helper. */
    private final double        logL;
    
    /** Helper. */
    private static final double log2             = Math.log(2);

    /**
     * Creates a new instance of the entropy l-diversity criterion as proposed in:
     * Machanavajjhala A, Kifer D, Gehrke J.
     * l-diversity: Privacy beyond k-anonymity.
     * Transactions on Knowledge Discovery from Data (TKDD). 2007;1(1):3.
     *
     * @param attribute
     * @param l
     */
    public EntropyLDiversity(String attribute, double l){
        super(attribute, l, false);
        logL = Math.log(l) / Math.log(2d);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.criteria.PrivacyCriterion#isAnonymous(org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry)
     */
    @Override
    public boolean isAnonymous(HashGroupifyEntry entry) {

        Distribution d = entry.distributions[index];

        // If less than l values are present skip
        if (d.size() < minSize) { return false; }

        // Sum of the frequencies in distribution (=number of elements)
        double total = 0;
        double sum1 = 0d;

        final int[] buckets = d.getBuckets();
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                final double frequency = buckets[i + 1];
                sum1 += frequency * log2(frequency);
                total += frequency;
            }
        }

        final double val = -((sum1 / total) - log2(total));

        // check
        return val >= logL;
    }

    /**
     * Computes log 2.
     *
     * @param num
     * @return
     */
    private final double log2(final double num) {
        return Math.log(num) / log2;
    }
    
	/* (non-Javadoc)
	 * @see org.deidentifier.arx.criteria.PrivacyCriterion#toString()
	 */
	@Override
	public String toString() {
		return "entropy-"+l+"-diversity for attribute '"+attribute+"'";
	}
}
