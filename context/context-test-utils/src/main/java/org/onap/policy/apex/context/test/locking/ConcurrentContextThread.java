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
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.test.lock.modifier.AlbumModifier;
import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestConcurrentContextThread tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ConcurrentContextThread implements Runnable, Closeable {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextThread.class);
    private final int jvm;
    private final int instance;
    private final ConfigrationProvider configrationProvider;

    /**
     * The Constructor.
     *
     * @param jvm the jvm
     * @param instance the instance
     * @param configrationProvider the configuration provider
     */
    public ConcurrentContextThread(final int jvm, final int instance, final ConfigrationProvider configrationProvider) {
        this.jvm = jvm;
        this.instance = instance;
        this.configrationProvider = configrationProvider;

        new ContextParameters();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.info("running TestConcurrentContextThread_" + jvm + "_" + instance + " . . .");


        final AxArtifactKey distributorKey = new AxArtifactKey("ApexDistributor_" + jvm + "_" + instance, "0.0.1");
        final Distributor distributor = configrationProvider.getDistributor(distributorKey);

        try {
            final long startTime = System.currentTimeMillis();
            final ContextAlbum contextAlbum = configrationProvider.getContextAlbum(distributor);

            final AlbumModifier albumModifier = configrationProvider.getAlbumModifier();
            albumModifier.modifyAlbum(contextAlbum, configrationProvider.getLoopSize(),
                    configrationProvider.getAlbumSize());
            LOGGER.info("Took {} ms to modify album", (System.currentTimeMillis() - startTime));

        } catch (final Exception e) {
            LOGGER.error("Unexpected error occured while processing", e);
        } finally {
            try {
                distributor.shutdown();
            } catch (final ContextException e) {
                LOGGER.error("Unable to shutdown distributor", e);
            }
        }
        LOGGER.info("finished TestConcurrentContextThread_" + jvm + "_" + instance + " . . .");

    }

    @Override
    public void close() {
        LOGGER.info("Shutting down {} thread ...", Thread.currentThread().getName());
    }
}
