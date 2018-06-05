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

package org.onap.policy.apex.plugins.context.distribution.infinispan;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.model.utilities.ResourceUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class InfinispanManager holds the Infinispan cache manager for a JVM.
 */
public class InfinispanManager {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(InfinispanManager.class);

    // The Infinispan Cache Manager
    private EmbeddedCacheManager cacheManager;

    /**
     * Constructor, set up an Infinispan cache manager.
     *
     * @param infinispanDistributorParameters the infinispan distributor parameters
     * @throws ContextException On errors connecting to Infinispan
     */
    public InfinispanManager(final InfinispanDistributorParameters infinispanDistributorParameters)
            throws ContextException {
        LOGGER.entry("Creating Infinispan Manager: " + infinispanDistributorParameters);

        setSystemProperties(infinispanDistributorParameters);

        // First, try and open a local input stream for Infinispan configuration
        InputStream infinispanConfigStream =
                getLocalInfinispanConfigurationStream(infinispanDistributorParameters.getConfigFile());

        // Check if a local file was found, if not then go to the class path
        if (infinispanConfigStream == null) {
            // If a local file is not specified, then check for an infinispan configuration file on
            // the class path
            infinispanConfigStream =
                    getClasspathInfinispanConfigurationStream(infinispanDistributorParameters.getConfigFile());
        }

        // Check if we found configuration for Infinispan
        if (infinispanConfigStream == null) {
            final String errorMessage =
                    "failed to start infinispan cache manager, no infinispan configuration found on local file system or in classpath, "
                            + "try setting Infinspan \"configFile\" parameter";
            LOGGER.error(errorMessage);
            throw new ContextException(errorMessage);
        }

        try {
            LOGGER.debug("starting infinispan cache manager using specified configuration . . .");
            cacheManager = new DefaultCacheManager(infinispanConfigStream);
            LOGGER.debug("started infinispan cache manager using specified configuration");
        } catch (final Exception e) {
            LOGGER.error("failed to start infinispan cache manager using specified configuration", e);
            throw new ContextException("failed to start infinispan cache manager using specified configuration", e);
        }

        // Start the cache manager
        cacheManager.start();

        Runtime.getRuntime().addShutdownHook(new InfinspanManagerShutdownHook());

        LOGGER.exit("Created Infinispan Manager: " + infinispanDistributorParameters);
    }

    /**
     * Shutdown the manager.
     */
    public void shutdown() {
        if (cacheManager == null) {
            return;
        }

        cacheManager.stop();
        cacheManager = null;
    }

    /**
     * Get the cache manager.
     *
     * @return the infinispan cache manager
     */
    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Set system properties used by Infinispan.
     *
     * @param infinispanDistributorParameters The parameter values to set are passed as properties
     */
    private void setSystemProperties(final InfinispanDistributorParameters infinispanDistributorParameters) {
        System.setProperty("java.net.preferIPv4Stack",
                Boolean.toString(infinispanDistributorParameters.preferIPv4Stack()));
        System.setProperty("jgroups.bind_addr", infinispanDistributorParameters.getjGroupsBindAddress());
    }

    /**
     * Get an Infinispan configuration stream from the local file system.
     *
     * @param infinispanConfigFileName The file name to open
     * @return The file opened as a stream
     * @throws ContextException If the local file could not be found or is invalid
     */
    private InputStream getLocalInfinispanConfigurationStream(final String infinispanConfigFileName)
            throws ContextException {
        LOGGER.debug("checking infinispan configuration file exists at \"" + infinispanConfigFileName + "\". . .");

        // Check if the file exists
        final File infinispanConfigFile = new File(infinispanConfigFileName);
        if (!infinispanConfigFile.exists()) {
            return null;
        }

        // Check the file
        if (!infinispanConfigFile.isFile() || !infinispanConfigFile.canRead()) {
            LOGGER.error("infinispan configuration file at \"" + infinispanConfigFileName
                    + "\" does not exist or is invalid");
            throw new ContextException("infinispan configuration file at \"" + infinispanConfigFileName
                    + "\" does not exist or is invalid");
        }

        try {
            final InputStream infinispanConfigStream = new FileInputStream(infinispanConfigFile);
            LOGGER.debug("infinispan configuration file exists at \"" + infinispanConfigFileName + "\"");
            return infinispanConfigStream;
        } catch (final Exception e) {
            LOGGER.error("infinispan configuration file at \"" + infinispanConfigFileName
                    + "\" does not exist or is invalid", e);
            throw new ContextException("infinispan configuration file at \"" + infinispanConfigFileName
                    + "\" does not exist or is invalid", e);
        }
    }

    /**
     * Get an Infinispan configuration stream from the class path.
     *
     * @param apexInfinispanConfigFile the apex infinispan config file
     * @return The file opened as a stream
     */
    private InputStream getClasspathInfinispanConfigurationStream(final String apexInfinispanConfigFile) {
        LOGGER.debug(
                "checking infinispan configuration file exists at resource \"" + apexInfinispanConfigFile + "\". . .");
        final InputStream infinispanConfigStream = ResourceUtils.getResourceAsStream(apexInfinispanConfigFile);

        if (infinispanConfigStream != null) {
            LOGGER.debug("infinispan configuration file exists at resource \"" + apexInfinispanConfigFile + "\"");
        } else {
            LOGGER.debug("infinispan configuration file at resource \"" + apexInfinispanConfigFile + "\" not found");
        }
        return infinispanConfigStream;
    }

    /**
     * Private class to implement the shutdown hook for this infinispan manager.
     */
    public class InfinspanManagerShutdownHook extends Thread {
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            shutdown();
        }
    }
}
