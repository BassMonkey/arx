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

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry;

/**
 * The k-anonymity criterion
 * Published in:
 * Sweeney L. 
 * k-anonymity: A model for protecting privacy. 
 * International Journal of Uncertainty, Fuzziness and Knowledge-Based Systems. 2002;10(5):557 - 570. 
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class KAnonymity extends ImplicitPrivacyCriterion{

    /**  TODO */
    private static final long serialVersionUID = -8370928677928140572L;
    
    /** The parameter k. */
    private final int k;
    
    /**
     * Creates a new instance of the k-anonymity criterion as proposed in
     * Sweeney L. k-Anonymity: A model for protecting privacy. 
     * International Journal of Uncertainty, Fuzziness and Knowledge-Based Systems. 2002;10(5):557 - 570. 
     * @param k
     */
    public KAnonymity(int k){
        super(true);
        this.k = k;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.criteria.PrivacyCriterion#getRequirements()
     */
    @Override
    public int getRequirements(){
        // Requires only one counter
        return ARXConfiguration.REQUIREMENT_COUNTER;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.criteria.PrivacyCriterion#isAnonymous(org.deidentifier.arx.framework.check.groupify.HashGroupifyEntry)
     */
    @Override
    public boolean isAnonymous(HashGroupifyEntry entry) {
        throw new RuntimeException("This should never be called!");
    }

    /**
     * Returns the parameter k.
     *
     * @return
     */
    public int getK() {
        return k;
    }
    
	/* (non-Javadoc)
	 * @see org.deidentifier.arx.criteria.PrivacyCriterion#toString()
	 */
	@Override
	public String toString() {
		return k+"-anonymity";
	}
}