/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.context.impl.distribution;

import java.util.Timer;
import java.util.TimerTask;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.PersistorParameters;
import org.onap.policy.common.parameters.ParameterService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is used to periodically flush a context distributor.
 *
 * @author eeilfn
 */
public class DistributorFlushTimerTask extends TimerTask {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(DistributorFlushTimerTask.class);

    // The timer for flushing
    private Timer timer = null;

    // The context distributor to flush
    private final Distributor contextDistributor;

    // Timing information
    private long period = 0;
    private long flushCount = 0;

    /**
     * Constructor, save a reference to the event stream handler.
     *
     * @param contextDistributor the distributor that this timer task is flushing
     * @throws ContextException On flush setup errors
     */
    public DistributorFlushTimerTask(final Distributor contextDistributor) throws ContextException {
        // Save the context distributor and period
        this.contextDistributor = contextDistributor;

        // Set the period for persistence flushing
        final PersistorParameters persistorParameters = ParameterService
                        .get(ContextParameterConstants.PERSISTENCE_GROUP_NAME);
        period = persistorParameters.getFlushPeriod();

        // Set up the timer
        timer = new Timer(DistributorFlushTimerTask.class.getSimpleName(), true);
        timer.schedule(this, period, period);

        LOGGER.debug("context distributor " + contextDistributor.getKey().getId() + " flushing set up with interval: "
                        + period + "ms");
    }

    /**
     * Flush the context distributor.
     */
    @Override
    public void run() {
        // Increment the flush counter
        flushCount++;

        LOGGER.debug("context distributor " + contextDistributor.getKey().getId() + " flushing: period=" + period
                        + ": count=" + flushCount);
        try {
            contextDistributor.flush();
            LOGGER.debug("context distributor " + contextDistributor.getKey().getId() + " flushed: period=" + period
                            + ": count=" + flushCount);
        } catch (final ContextException e) {
            LOGGER.error("flush error on context distributor " + contextDistributor.getKey().getId() + ": period="
                            + period + ": count=" + flushCount, e);
        }
    }

    /**
     * Cancel the timer.
     *
     * @return true, if cancel
     */
    @Override
    public boolean cancel() {
        // Cancel the timer
        if (timer != null) {
            timer.cancel();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ContextDistributorFlushTimerTask [period=" + period + ", flushCount=" + flushCount + "]";
    }
}
