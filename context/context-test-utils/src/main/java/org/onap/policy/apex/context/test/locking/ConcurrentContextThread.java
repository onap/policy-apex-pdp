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

package org.onap.policy.apex.context.test.locking;

import java.io.Closeable;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestConcurrentContextThread tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ConcurrentContextThread implements Runnable, Closeable {
    private static final String VALUE = "testValue";
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextThread.class);
    private final Distributor distributor;
    private final int jvm;
    private final int instance;
    private final int threadLoops;

    /**
     * The Constructor.
     *
     * @param jvm the jvm
     * @param instance the instance
     * @param threadLoops the thread loops
     * @throws ApexException the apex exception
     */
    public ConcurrentContextThread(final int jvm, final int instance, final int threadLoops) throws ApexException {
        this.jvm = jvm;
        this.instance = instance;
        this.threadLoops = threadLoops;

        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor_" + jvm + "_" + instance, "0.0.1");

        new ContextParameters();
        distributor = new DistributorFactory().getDistributor(distributorKey);
        final AxContextModel albumsModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
        distributor.registerModel(albumsModel);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.info("running TestConcurrentContextThread_" + jvm + "_" + instance + " . . .");

        ContextAlbum lTypeAlbum = null;

        try {
            lTypeAlbum = distributor.createContextAlbum(new AxArtifactKey("LTypeContextAlbum", "0.0.1"));
        } catch (final Exception e) {
            LOGGER.error("could not get the test context album", e);
            LOGGER.error("failed TestConcurrentContextThread_" + jvm + "_" + instance);
            return;
        }

        if (lTypeAlbum == null) {
            LOGGER.error("could not find the test context album");
            LOGGER.error("failed TestConcurrentContextThread_" + jvm + "_" + instance);
            return;
        }

        // @formatter:off
        final AxArtifactKey[] usedArtifactStackArray = {new AxArtifactKey("testC-top", "0.0.1"),
                new AxArtifactKey("testC-next", "0.0.1"), new AxArtifactKey("testC-bot", "0.0.1")};
        // @formatter:on

        lTypeAlbum.setUserArtifactStack(usedArtifactStackArray);

        try {
            updateAlbum(lTypeAlbum);
        } catch (final Exception exception) {
            LOGGER.error("could not set the value in the test context album", exception);
            LOGGER.error("failed TestConcurrentContextThread_" + jvm + "_" + instance);
            return;
        }

        try {
            lTypeAlbum.lockForWriting(VALUE);
            final TestContextLongItem item = (TestContextLongItem) lTypeAlbum.get(VALUE);
            final long value = item.getLongValue();
            LOGGER.info("completed TestConcurrentContextThread_" + jvm + "_" + instance + ", value=" + value);
        } catch (final Exception e) {
            LOGGER.error("could not read the value in the test context album", e);
            LOGGER.error("failed TestConcurrentContextThread_" + jvm + "_" + instance);
        } finally {
            try {
                lTypeAlbum.unlockForWriting(VALUE);
                distributor.shutdown();
            } catch (final ContextException e) {
                LOGGER.error("could not unlock test context album item", e);
                LOGGER.error("failed TestConcurrentContextThread_" + jvm + "_" + instance);
            }
        }
    }

    private void updateAlbum(final ContextAlbum lTypeAlbum) throws Exception {
        for (int i = 0; i < threadLoops; i++) {
            try {
                lTypeAlbum.lockForWriting(VALUE);
                TestContextLongItem item = (TestContextLongItem) lTypeAlbum.get(VALUE);
                if (item != null) {
                    long value = item.getLongValue();
                    item.setLongValue(++value);
                } else {
                    item = new TestContextLongItem(0L);
                }
                lTypeAlbum.put(VALUE, item);
            } finally {
                lTypeAlbum.unlockForWriting(VALUE);
            }
        }
    }

    @Override
    public void close() {
        LOGGER.info("Shutting down {} thread ...", Thread.currentThread().getName());
    }
}
