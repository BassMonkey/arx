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

package org.deidentifier.arx.framework.check.transformer;

import org.deidentifier.arx.ARXConfiguration.ARXConfigurationInternal;
import org.deidentifier.arx.framework.check.distribution.IntArrayDictionary;
import org.deidentifier.arx.framework.data.GeneralizationHierarchy;

/**
 * The class Transformer12.
 * 
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class Transformer12 extends AbstractTransformer {

    /**
     * Instantiates a new transformer.
     *
     * @param data the data
     * @param hierarchies the hierarchies
     * @param sensitiveValues
     * @param dictionarySensValue
     * @param dictionarySensFreq
     * @param config
     */
    public Transformer12(final int[][] data,
                         final GeneralizationHierarchy[] hierarchies,
                         final int[][] sensitiveValues,
                         final IntArrayDictionary dictionarySensValue,
                         final IntArrayDictionary dictionarySensFreq,
                         final ARXConfigurationInternal config) {
        super(data, hierarchies, sensitiveValues, dictionarySensValue, dictionarySensFreq, config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deidentifier.ARX.framework.check.transformer.AbstractTransformer
     * #walkAll()
     */
    @Override
    protected void processAll() {
        for (int i = startIndex; i < stopIndex; i++) {
            intuple = data[i];
            outtuple = buffer[i];
            outtuple[outindex0] = idindex0[intuple[index0]][generalizationindex0];
            outtuple[outindex1] = idindex1[intuple[index1]][generalizationindex1];
            outtuple[outindex2] = idindex2[intuple[index2]][generalizationindex2];
            outtuple[outindex3] = idindex3[intuple[index3]][generalizationindex3];
            outtuple[outindex4] = idindex4[intuple[index4]][generalizationindex4];
            outtuple[outindex5] = idindex5[intuple[index5]][generalizationindex5];
            outtuple[outindex6] = idindex6[intuple[index6]][generalizationindex6];
            outtuple[outindex7] = idindex7[intuple[index7]][generalizationindex7];
            outtuple[outindex8] = idindex8[intuple[index8]][generalizationindex8];
            outtuple[outindex9] = idindex9[intuple[index9]][generalizationindex9];
            outtuple[outindex10] = idindex10[intuple[index10]][generalizationindex10];
            outtuple[outindex11] = idindex11[intuple[index11]][generalizationindex11];

            // Call
            delegate.callAll(outtuple, i);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deidentifier.ARX.framework.check.transformer.AbstractTransformer
     * #walkGroupify ()
     */
    @Override
    protected void processGroupify() {
        int processed = 0;
        while (element != null) {

            intuple = data[element.representant];
            outtuple = buffer[element.representant];
            outtuple[outindex0] = idindex0[intuple[index0]][generalizationindex0];
            outtuple[outindex1] = idindex1[intuple[index1]][generalizationindex1];
            outtuple[outindex2] = idindex2[intuple[index2]][generalizationindex2];
            outtuple[outindex3] = idindex3[intuple[index3]][generalizationindex3];
            outtuple[outindex4] = idindex4[intuple[index4]][generalizationindex4];
            outtuple[outindex5] = idindex5[intuple[index5]][generalizationindex5];
            outtuple[outindex6] = idindex6[intuple[index6]][generalizationindex6];
            outtuple[outindex7] = idindex7[intuple[index7]][generalizationindex7];
            outtuple[outindex8] = idindex8[intuple[index8]][generalizationindex8];
            outtuple[outindex9] = idindex9[intuple[index9]][generalizationindex9];
            outtuple[outindex10] = idindex10[intuple[index10]][generalizationindex10];
            outtuple[outindex11] = idindex11[intuple[index11]][generalizationindex11];

            // Call
            delegate.callGroupify(outtuple, element);

            // Next element
            processed++;
            if (processed == numElements) { return; }
            element = element.nextOrdered;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deidentifier.ARX.framework.check.transformer.AbstractTransformer
     * #walkSnapshot ()
     */
    @Override
    protected void processSnapshot() {
        startIndex *= ssStepWidth;
        stopIndex *= ssStepWidth;

        for (int i = startIndex; i < stopIndex; i += ssStepWidth) {
            intuple = data[snapshot[i]];
            outtuple = buffer[snapshot[i]];
            outtuple[outindex0] = idindex0[intuple[index0]][generalizationindex0];
            outtuple[outindex1] = idindex1[intuple[index1]][generalizationindex1];
            outtuple[outindex2] = idindex2[intuple[index2]][generalizationindex2];
            outtuple[outindex3] = idindex3[intuple[index3]][generalizationindex3];
            outtuple[outindex4] = idindex4[intuple[index4]][generalizationindex4];
            outtuple[outindex5] = idindex5[intuple[index5]][generalizationindex5];
            outtuple[outindex6] = idindex6[intuple[index6]][generalizationindex6];
            outtuple[outindex7] = idindex7[intuple[index7]][generalizationindex7];
            outtuple[outindex8] = idindex8[intuple[index8]][generalizationindex8];
            outtuple[outindex9] = idindex9[intuple[index9]][generalizationindex9];
            outtuple[outindex10] = idindex10[intuple[index10]][generalizationindex10];
            outtuple[outindex11] = idindex11[intuple[index11]][generalizationindex11];

            // Call
            delegate.callSnapshot(outtuple, snapshot, i);
        }
    }
}
