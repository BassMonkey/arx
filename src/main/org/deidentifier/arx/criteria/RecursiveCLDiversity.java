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

import java.util.Arrays;

import org.deidentifier.arx.framework.check.distribution.Distribution;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;

/**
 * The recursive-(c,l)-diversity criterion.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class RecursiveCLDiversity extends LDiversity{

    /**  TODO */
    private static final long serialVersionUID = -5893481096346270328L;

    /** The parameter c. */
    private final double c;
    
    /**
     * Creates a new instance of the recursive-(c,l)-diversity criterion as proposed in:
     * Machanavajjhala A, Kifer D, Gehrke J.
     * l-diversity: Privacy beyond k-anonymity.
     * Transactions on Knowledge Discovery from Data (TKDD). 2007;1(1):3.
     *
     * @param attribute
     * @param c
     * @param l
     */
    public RecursiveCLDiversity(String attribute, double c, int l){
        super(attribute, l, false);
        this.c = c;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.criteria.PrivacyCriterion#isAnonymous(org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry)
     */
    @Override
    public boolean isAnonymous(HashGroupifyEntry entry) {

        Distribution d = entry.distributions[index];
        
        // if less than l values are present skip
        if (d.size() < minSize) { return false; }

        // Copy and pack
        int[] buckets = d.getBuckets();
        final int[] frequencyCopy = new int[d.size()];
        int count = 0;
        for (int i = 0; i < buckets.length; i += 2) {
            if (buckets[i] != -1) { // bucket not empty
                frequencyCopy[count++] = buckets[i + 1];
            }
        }

        // Sort
        Arrays.sort(frequencyCopy);
        
        // Compute threshold
        double threshold = 0;
        for (int i = frequencyCopy.length - minSize; i >= 0; i--) { // minSize=(int)l;
            threshold += frequencyCopy[i];
        }
        threshold *= c;

        // Check
        return frequencyCopy[frequencyCopy.length - 1] < threshold;
    }

    /**
     * Returns the parameter c.
     *
     * @return
     */
    public double getC() {
        return c;
    }
    
	/* (non-Javadoc)
	 * @see org.deidentifier.arx.criteria.PrivacyCriterion#toString()
	 */
	@Override
	public String toString() {
		return "recursive-("+c+","+minSize+")-diversity for attribute '"+attribute+"'";
	}
}
