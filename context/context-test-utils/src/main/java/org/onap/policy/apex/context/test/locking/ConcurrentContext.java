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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.Distributor;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
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
    private ContextAlbum ltypeAlbum = null;

    private final ConfigrationProvider configrationProvider;

    public ConcurrentContext(final ConfigrationProvider configrationProvider) {
        this.configrationProvider = configrationProvider;
    }

    /**
     * The method tests concurrent use of context.
     * @return the verified context
     * @throws ApexModelException the exception occurs in model handling
     * @throws IOException the IO exception occurs in handling IO
     * @throws ApexException the Apex exception occurs in handling Apex
     */
    public Map<String, TestContextLongItem> testConcurrentContext()
            throws ApexModelException, IOException, ApexException {

        try {
            setupAndVerifyContext();
        } catch (final Exception exception) {
            LOGGER.error("Error occured while setting up and verifying concurrent context", exception);
            throw exception;
        }

        LOGGER.debug("starting JVMs and threads . . .");

        final ExecutorService executorService = configrationProvider.getExecutorService();

        final List<Closeable> tasks = new ArrayList<>(configrationProvider.getThreadCount());

        addShutDownHook(tasks);

        // Check if we have a single JVM or multiple JVMs
        if (configrationProvider.getJvmCount() == 1) {
            // Run everything in this JVM
            for (int t = 0; t < configrationProvider.getThreadCount(); t++) {
                final ConcurrentContextThread task = new ConcurrentContextThread(0, t, configrationProvider);
                tasks.add(task);
                executorService.execute(task);
            }

        } else {
            // Spawn JVMs to run the tests
            for (int j = 0; j < configrationProvider.getJvmCount(); j++) {
                final ConcurrentContextJVMThread task = new ConcurrentContextJVMThread(j, configrationProvider);
                tasks.add(task);
                executorService.execute(task);
            }
        }

        try {
            executorService.shutdown();
            // wait for threads to finish, if not Timeout
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (final InterruptedException interruptedException) {
            LOGGER.error("Exception while waiting for threads to finish", interruptedException);
            // restore the interrupt status
            Thread.currentThread().interrupt();
        }

        LOGGER.info("Shutting down now ...");
        executorService.shutdownNow();

        return verifyAndClearContext();
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

    /**
     * Setup and verify context.
     *
     * @throws ContextException the context exception
     */
    private void setupAndVerifyContext() throws ContextException {
        contextDistributor = configrationProvider.getDistributor();
        ltypeAlbum = configrationProvider.getContextAlbum(contextDistributor);
        final Map<String, Object> initValues = configrationProvider.getContextAlbumInitValues();

        for (final Entry<String, Object> entry : initValues.entrySet()) {
            ltypeAlbum.put(entry.getKey(), entry.getValue());
        }
    }

    private Map<String, TestContextLongItem> verifyAndClearContext() throws ContextException {
        final Map<String, TestContextLongItem> values = new HashMap<>();
        try {

            for (Entry<String, Object> entry : ltypeAlbum.entrySet()) {
                values.put(entry.getKey(), (TestContextLongItem) entry.getValue());
            }
        } catch (final Exception exception) {
            LOGGER.error("Error: ", exception);
        }
        contextDistributor.clear();
        contextDistributor = null;

        return values;
    }
}
