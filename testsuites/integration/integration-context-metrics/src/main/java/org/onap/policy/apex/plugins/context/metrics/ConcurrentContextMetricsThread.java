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

package org.onap.policy.apex.plugins.context.metrics;

import java.util.Random;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ConcurrentContextMetricsThread gets metrics for concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ConcurrentContextMetricsThread implements Runnable {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextMetricsThread.class);
    private final Distributor contextDistributor;
    private final int jvm;
    private final int instance;
    private final int threadLoops;
    private final int longArraySize;
    private final int lockType;

    /**
     * The Constructor.
     *
     * @param jvm the jvm
     * @param instance the instance
     * @param threadLoops the thread loops
     * @param longArraySize the long array size
     * @param lockType the lock type
     * @throws ApexException the apex exception
     */
    public ConcurrentContextMetricsThread(final int jvm, final int instance, final int threadLoops,
            final int longArraySize, final int lockType) throws ApexException {
        this.jvm = jvm;
        this.instance = instance;
        this.threadLoops = threadLoops;
        this.longArraySize = longArraySize;
        this.lockType = lockType;

        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor_" + jvm + "_" + instance, "0.0.1");
        contextDistributor = new DistributorFactory().getDistributor(distributorKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.info("running ConcurrentContextMetricsThread_" + jvm + "_" + instance + " . . .");

        ContextAlbum lTypeAlbum = null;
        try {
            final AxContextModel axTestContextModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
            contextDistributor.registerModel(axTestContextModel);
            lTypeAlbum = contextDistributor.createContextAlbum(new AxArtifactKey("LTypeContextAlbum", "0.0.1"));
        } catch (final Exception e) {
            LOGGER.error("could not get the test context album", e);
            LOGGER.error("failed ConcurrentContextMetricsThread_" + jvm + "_" + instance);
            return;
        }

        if (lTypeAlbum == null) {
            LOGGER.error("could not find the test context album");
            LOGGER.error("failed ConcurrentContextMetricsThread_" + jvm + "_" + instance);
            return;
        }

        final AxArtifactKey[] usedArtifactStackArray =
                {new AxArtifactKey("testCC-top", "0.0.1"), new AxArtifactKey("testCC-" + instance, "0.0.1")};

        lTypeAlbum.setUserArtifactStack(usedArtifactStackArray);

        final Random rand = new Random();

        for (int i = 0; i < threadLoops; i++) {
            // Get the next random entry to use
            final String nextLongKey = Integer.toString(rand.nextInt(longArraySize));

            if (lockType == 0) {
                final TestContextLongItem item = (TestContextLongItem) lTypeAlbum.get(nextLongKey);
                final long value = item.getLongValue();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("lock type=" + lockType + ", value=" + value);
                }
                continue;
            }

            if (lockType == 1) {
                try {
                    lTypeAlbum.lockForReading(nextLongKey);
                } catch (final ContextException e) {
                    LOGGER.error("could not acquire read lock on context album, key=" + nextLongKey, e);
                    continue;
                }

                final TestContextLongItem item = (TestContextLongItem) lTypeAlbum.get(nextLongKey);
                final long value = item.getLongValue();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("lock type=" + lockType + ", value=" + value);
                }

                try {
                    lTypeAlbum.unlockForReading(nextLongKey);
                } catch (final ContextException e) {
                    LOGGER.error("could not release read lock on context album, key=" + nextLongKey, e);
                }

                continue;
            }

            if (lockType == 2) {
                try {
                    lTypeAlbum.lockForWriting(nextLongKey);
                } catch (final ContextException e) {
                    LOGGER.error("could not acquire write lock on context album, key=" + nextLongKey, e);
                    continue;
                }

                final TestContextLongItem item = (TestContextLongItem) lTypeAlbum.get(nextLongKey);
                long value = item.getLongValue();
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("lock type=" + lockType + ", value=" + value);
                }
                item.setLongValue(++value);
                lTypeAlbum.put(nextLongKey, item);

                try {
                    lTypeAlbum.unlockForWriting(nextLongKey);
                } catch (final ContextException e) {
                    LOGGER.error("could not release write lock on context album, key=" + nextLongKey, e);
                }
                continue;
            }
        }

        LOGGER.info("completed ConcurrentContextMetricsThread_" + jvm + "_" + instance);
    }
}
