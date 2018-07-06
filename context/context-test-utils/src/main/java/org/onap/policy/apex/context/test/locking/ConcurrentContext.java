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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.impl.distribution.DistributorFactory;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.factory.TestContextAlbumFactory;
import org.onap.policy.apex.context.test.utils.IntegrationThreadFactory;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestConcurrentContext tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ConcurrentContext {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContext.class);

    // The context distributor and map used by each test
    private Distributor contextDistributor = null;
    private ContextAlbum lTypeAlbum = null;

    /**
     * Test concurrent context.
     *
     * @param testType the test type
     * @param jvmCount the jvm count
     * @param threadCount the thread count
     * @param threadLoops the thread loops
     * @return the long
     * @throws ApexModelException the apex model exception
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    public long testConcurrentContext(final String testType, final int jvmCount, final int threadCount,
            final int threadLoops) throws ApexModelException, IOException, ApexException {
        final ConcurrentContext concurrentContext = new ConcurrentContext();

        try {
            concurrentContext.setupAndVerifyContext();
        } catch (final Exception exception) {
            LOGGER.error("Error occured while setting up and verifying concurrent context", exception);
            throw exception;
        }

        LOGGER.debug("starting JVMs and threads . . .");

        final String name = getThreadFactoryName(jvmCount, testType);
        final IntegrationThreadFactory threadFactory = new IntegrationThreadFactory(name);
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount, threadFactory);

        final List<Closeable> tasks = new ArrayList<>(threadCount);

        // Check if we have a single JVM or multiple JVMs
        if (jvmCount == 1) {
            // Run everything in this JVM
            for (int t = 0; t < threadCount; t++) {
                final ConcurrentContextThread task = new ConcurrentContextThread(0, t, threadLoops);
                tasks.add(task);
                executorService.execute(task);
            }

        } else {
            // Spawn JVMs to run the tests
            for (int j = 0; j < jvmCount; j++) {
                final ConcurrentContextJVMThread task =
                        new ConcurrentContextJVMThread(testType, j, threadCount, threadLoops);
                tasks.add(task);
                executorService.execute(task);
            }
        }

        addShutDownHook(tasks);

        try {
            executorService.shutdown();
            // wait for threads to finish, if not Timeout
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException interruptedException) {
            LOGGER.error("Exception while waiting for threads to finish", interruptedException);
        }

        LOGGER.info("Shutting down now ...");
        executorService.shutdownNow();

        return concurrentContext.verifyAndClearContext(jvmCount, threadCount, threadLoops);
    }


    private void addShutDownHook(final List<Closeable> tasks) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.info("Shutting down ...");
                for (final Closeable task : tasks) {
                    try {
                        task.close();
                    } catch (final IOException ioException) {
                        LOGGER.error("Unable to close task ... ", ioException);
                    }
                }
            }
        });
    }


    private String getThreadFactoryName(final int jvmCount, final String testType) {
        return jvmCount == 1 ? testType + ":TestConcurrentContextThread_0_"
                : testType + ":TestConcurrentContextJVMThread_";
    }

    /**
     * Setup and verify context.
     *
     * @throws ContextException the context exception
     */
    private void setupAndVerifyContext() throws ContextException {
        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor", "0.0.1");
        contextDistributor = new DistributorFactory().getDistributor(distributorKey);

        // @formatter:off
        final AxArtifactKey[] usedArtifactStackArray = {
                new AxArtifactKey("testC-top", "0.0.1"),
                new AxArtifactKey("testC-next", "0.0.1"),
                new AxArtifactKey("testC-bot", "0.0.1")
                };
        // @formatter:on

        final AxContextModel albumsModel = TestContextAlbumFactory.createMultiAlbumsContextModel();
        contextDistributor.registerModel(albumsModel);

        lTypeAlbum = contextDistributor.createContextAlbum(new AxArtifactKey("LTypeContextAlbum", "0.0.1"));
        assert (lTypeAlbum != null);
        lTypeAlbum.setUserArtifactStack(usedArtifactStackArray);

        // CHECKSTYLE:OFF: checkstyle:magicNumber
        lTypeAlbum.put("lTypeValue0", new TestContextLongItem(0xFFFFFFFFFFFFFFFFL));
        lTypeAlbum.put("lTypeValue1", new TestContextLongItem(0xFFFFFFFFFFFFFFFEL));
        lTypeAlbum.put("lTypeValue2", new TestContextLongItem(0xFFFFFFFFFFFFFFFDL));
        lTypeAlbum.put("lTypeValue3", new TestContextLongItem(0xFFFFFFFFFFFFFFFCL));
        lTypeAlbum.put("lTypeValue4", new TestContextLongItem(0xFFFFFFFFFFFFFFFBL));
        lTypeAlbum.put("lTypeValue5", new TestContextLongItem(0xFFFFFFFFFFFFFFFAL));
        lTypeAlbum.put("lTypeValue6", new TestContextLongItem(0xFFFFFFFFFFFFFFF9L));
        lTypeAlbum.put("lTypeValue7", new TestContextLongItem(0xFFFFFFFFFFFFFFF8L));
        lTypeAlbum.put("lTypeValue8", new TestContextLongItem(0xFFFFFFFFFFFFFFF7L));
        lTypeAlbum.put("lTypeValue9", new TestContextLongItem(0xFFFFFFFFFFFFFFF6L));
        lTypeAlbum.put("lTypeValueA", new TestContextLongItem(0xFFFFFFFFFFFFFFF5L));
        lTypeAlbum.put("lTypeValueB", new TestContextLongItem(0xFFFFFFFFFFFFFFF4L));
        lTypeAlbum.put("lTypeValueC", new TestContextLongItem(0xFFFFFFFFFFFFFFF3L));
        lTypeAlbum.put("lTypeValueD", new TestContextLongItem(0xFFFFFFFFFFFFFFF2L));
        lTypeAlbum.put("lTypeValueE", new TestContextLongItem(0xFFFFFFFFFFFFFFF1L));
        lTypeAlbum.put("lTypeValueF", new TestContextLongItem(0xFFFFFFFFFFFFFFF0L));
        LOGGER.debug(lTypeAlbum.toString());
        assert (lTypeAlbum.size() >= 16);
        // CHECKSTYLE:ON: checkstyle:magicNumber

        // The initial value for concurrent testing
        final TestContextLongItem item = new TestContextLongItem(0L);
        lTypeAlbum.put("testValue", item);

    }

    /**
     * Verify and clear context.
     *
     * @param jvmCount the jvm count
     * @param threadCount the thread count
     * @param threadLoops the thread loops
     * @return the long
     * @throws ContextException the context exception
     */
    private long verifyAndClearContext(final int jvmCount, final int threadCount, final int threadLoops)
            throws ContextException {
        try {
            LOGGER.debug("threads finished, end value is {}",
                    ((TestContextLongItem) lTypeAlbum.get("testValue")).getLongValue());
        } catch (final Exception exception) {
            LOGGER.error("Error: ", exception);
        }
        final long total = ((TestContextLongItem) lTypeAlbum.get("testValue")).getLongValue();

        contextDistributor.clear();
        contextDistributor = null;

        return total;
    }
}
