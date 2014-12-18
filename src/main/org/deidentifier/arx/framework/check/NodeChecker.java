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

package org.deidentifier.arx.framework.check;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXConfiguration.ARXConfigurationInternal;
import org.deidentifier.arx.framework.check.StateMachine.Transition;
import org.deidentifier.arx.framework.check.distribution.IntArrayDictionary;
import org.deidentifier.arx.framework.check.groupify.HashGroupify;
import org.deidentifier.arx.framework.check.groupify.IHashGroupify;
import org.deidentifier.arx.framework.check.history.History;
import org.deidentifier.arx.framework.data.Data;
import org.deidentifier.arx.framework.data.DataManager;
import org.deidentifier.arx.framework.lattice.Lattice;
import org.deidentifier.arx.framework.lattice.Node;
import org.deidentifier.arx.metric.InformationLoss;
import org.deidentifier.arx.metric.InformationLossWithBound;
import org.deidentifier.arx.metric.Metric;

/**
 * This class orchestrates the process of checking a node for k-anonymity.
 *
 * @author Fabian Prasser
 * @author Florian Kohlmayer
 */
public class NodeChecker implements INodeChecker {

    /** The config. */
    private final ARXConfigurationInternal config;

    /** The data. */
    private final Data             data;

    /** The current hash groupify. */
    protected IHashGroupify        currentGroupify;

    /** The history. */
    protected History              history;

    /** The last hash groupify. */
    protected IHashGroupify        lastGroupify;

    /** The metric. */
    protected Metric<?>            metric;

    /** The state machine. */
    protected StateMachine         stateMachine;

    /** The data transformer. */
    protected Transformer          transformer;

    /**
     * Creates a new NodeChecker instance.
     * 
     * @param manager
     *            The manager
     * @param metric
     *            The metric
     * @param config
     *            The anonymization configuration
     * @param historyMaxSize
     *            The history max size
     * @param snapshotSizeDataset
     *            The history threshold
     * @param snapshotSizeSnapshot
     *            The history threshold replacement
     */
    public NodeChecker(final DataManager manager, final Metric<?> metric, final ARXConfigurationInternal config, final int historyMaxSize, final double snapshotSizeDataset, final double snapshotSizeSnapshot) {

        // Initialize all operators
        this.metric = metric;
        this.config = config;
        this.data = manager.getDataQI();
        
        int initialSize = (int) (manager.getDataQI().getDataLength() * 0.01d);
        IntArrayDictionary dictionarySensValue;
        IntArrayDictionary dictionarySensFreq;
        if ((config.getRequirements() & ARXConfiguration.REQUIREMENT_DISTRIBUTION) != 0) {
            dictionarySensValue = new IntArrayDictionary(initialSize);
            dictionarySensFreq = new IntArrayDictionary(initialSize);
        } else {
            // Just to allow byte code instrumentation
            dictionarySensValue = new IntArrayDictionary(0);
            dictionarySensFreq = new IntArrayDictionary(0);
        }

        this.history = new History(manager.getDataQI().getArray().length,
                                   historyMaxSize,
                                   snapshotSizeDataset,
                                   snapshotSizeSnapshot,
                                   config,
                                   dictionarySensValue,
                                   dictionarySensFreq);
        
        this.stateMachine = new StateMachine(history);
        this.currentGroupify = new HashGroupify(initialSize, config);
        this.lastGroupify = new HashGroupify(initialSize, config);
        this.transformer = new Transformer(manager.getDataQI().getArray(),
                                           manager.getHierarchies(),
                                           manager.getDataSE().getArray(),
                                           config,
                                           dictionarySensValue,
                                           dictionarySensFreq);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#check(org.deidentifier.arx.framework.lattice.Node)
     */
    @Override
    public INodeChecker.Result check(final Node node) {
        return check(node, false);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#check(org.deidentifier.arx.framework.lattice.Node, boolean)
     */
    @Override
    public INodeChecker.Result check(final Node node, final boolean forceMeasureInfoLoss) {
        
        // If the result is already know, simply return it
        if (node.getData() != null && node.getData() instanceof INodeChecker.Result) {
            return (INodeChecker.Result)node.getData();
        }

        // Store snapshot from last check
        if (stateMachine.getLastNode() != null) {
            history.store(stateMachine.getLastNode(), currentGroupify, stateMachine.getLastTransition().snapshot);
        }

        // Transition
        final Transition transition = stateMachine.transition(node);

        // Switch groupifies
        final IHashGroupify temp = lastGroupify;
        lastGroupify = currentGroupify;
        currentGroupify = temp;

        // Apply transition
        switch (transition.type) {
        case UNOPTIMIZED:
            currentGroupify = transformer.apply(transition.projection, node.getTransformation(), currentGroupify);
            break;
        case ROLLUP:
            currentGroupify = transformer.applyRollup(transition.projection, node.getTransformation(), lastGroupify, currentGroupify);
            break;
        case SNAPSHOT:
            currentGroupify = transformer.applySnapshot(transition.projection, node.getTransformation(), currentGroupify, transition.snapshot);
            break;
        }
        
        // We are done with transforming and adding
        currentGroupify.analyze(forceMeasureInfoLoss);
        if (forceMeasureInfoLoss && !currentGroupify.isAnonymous() && !config.isSuppressionAlwaysEnabled()) {
            currentGroupify.resetSuppression();
        }
        
        // Compute information loss and lower bound
        InformationLossWithBound<?> result = (currentGroupify.isAnonymous() || forceMeasureInfoLoss) ?
                                          metric.getInformationLoss(node, currentGroupify) : null;
        InformationLoss<?> loss = result != null ? result.getInformationLoss() : null;
        InformationLoss<?> bound = result != null ? result.getLowerBound() : metric.getLowerBound(node, currentGroupify);
        
        // Return result;
        return new INodeChecker.Result(currentGroupify.isAnonymous(), 
                                       currentGroupify.isKAnonymous(),
                                       loss,
                                       bound);
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getBuffer()
     */
    @Override
    public Data getBuffer() {
        return new Data(transformer.getBuffer(), data.getHeader(), data.getMap(), data.getDictionary());
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getConfiguration()
     */
    @Override
    public ARXConfigurationInternal getConfiguration() {
        return config;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getData()
     */
    @Override
    public Data getData() {
        return data;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getGroupify()
     */
    @Override
    public IHashGroupify getGroupify() {
        return currentGroupify;
    }

    /**
     * Returns the checkers history, if any.
     *
     * @return
     */
    public History getHistory() {
        return history;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getInformationLoss(org.deidentifier.arx.framework.lattice.Node)
     */
    @Override
    @Deprecated
    public double getInformationLoss(final Node node) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getMetric()
     */
    @Override
    public Metric<?> getMetric() {
        return metric;
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#getNumberOfGroups()
     */
    @Override
    public int getNumberOfGroups() {
        return currentGroupify.size();
    }

    /* (non-Javadoc)
     * @see org.deidentifier.arx.framework.check.INodeChecker#applyAndSetProperties(org.deidentifier.arx.framework.lattice.Node)
     */
    @Override
    public TransformedData applyAndSetProperties(final Node transformation) {

        // Apply transition and groupify
        currentGroupify = transformer.apply(0L, transformation.getTransformation(), currentGroupify);
        currentGroupify.analyze(true);
        if (!currentGroupify.isAnonymous() && !config.isSuppressionAlwaysEnabled()) {
            currentGroupify.resetSuppression();
        }

        // Determine information loss
        // TODO: This may already be known
        InformationLoss<?> loss = transformation.getInformationLoss();
        if (loss == null) {
            loss = metric.getInformationLoss(transformation, currentGroupify).getInformationLoss();
        }
        
        // Find outliers
        if (config.getAbsoluteMaxOutliers() != 0 || !currentGroupify.isAnonymous()) {
            currentGroupify.markOutliers(transformer.getBuffer());
        }
        
        // Set properties
        Lattice lattice = new Lattice(new Node[][]{{transformation}}, 0);
        lattice.setChecked(transformation, new Result(currentGroupify.isAnonymous(), 
                                                      currentGroupify.isKAnonymous(),
                                                      loss,
                                                      null));
        
        // Return the buffer
        return new TransformedData(getBuffer(), currentGroupify.getGroupStatistics());
    }
}
