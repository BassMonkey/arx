/*
 * ARX: Powerful Data Anonymization
 * Copyright 2012 - 2015 Florian Kohlmayer, Fabian Prasser
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.deidentifier.arx.criteria;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyDistribution;
import org.deidentifier.arx.framework.check.groupify.HashGroupifyDistribution.PrivacyCondition;

/**
 * Abstract class for criteria that ensure that a certain risk measure is lower than or equal to a given threshold
 * 
 * @author Fabian Prasser
 */
public abstract class RiskBasedPrivacyCriterion extends SampleBasedPrivacyCriterion{

    /** SVUID */
    private static final long serialVersionUID = -2711630526630937284L;
    /** The threshold */
    private final double    threshold;

    /**
     * Creates a new instance of this criterion.
     *  
     * @param monotonic
     * @param riskThreshold
     */
    public RiskBasedPrivacyCriterion(boolean monotonic, double riskThreshold){
        super(monotonic);
        this.threshold = riskThreshold;
        if (this.threshold < 0d || this.threshold >= 1d) {
            throw new IllegalArgumentException("Threshold out of range. Must be in [0, 1[");
        }
    }
    
    @Override
    public void enforce(final HashGroupifyDistribution distribution,
                       final int numMaxSuppressedOutliers) {
       
        distribution.suppressWhileNotFulfilledBinary(new PrivacyCondition(){
            public State isFulfilled(HashGroupifyDistribution distribution) {
                boolean fulfilled = RiskBasedPrivacyCriterion.this.isFulfilled(distribution);
                if (!fulfilled && distribution.getNumOfSuppressedTuples() > numMaxSuppressedOutliers) {
                    return State.ABORT;
                } else {
                    return fulfilled ? State.FULFILLED : State.NOT_FULFILLED;
                }
            }
        });
    }
    
    @Override
    public int getRequirements(){
        // Requires only one counter
        return ARXConfiguration.REQUIREMENT_COUNTER;
    }

    /**
     * Returns the risk threshold
     *
     * @return
     */
    public double getRiskThreshold() {
        return threshold;
    }

    /**
     * To be implemented by risk-based criteria
     * @param distribution
     * @return
     */
    protected abstract boolean isFulfilled(HashGroupifyDistribution distribution);
}