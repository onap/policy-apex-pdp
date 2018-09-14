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

package org.onap.policy.apex.testsuites.performance.context.metrics;

import static org.onap.policy.apex.context.parameters.DistributorParameters.DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;
import static org.onap.policy.apex.context.parameters.LockManagerParameters.DEFAULT_LOCK_MANAGER_PLUGIN_CLASS;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.locking.ConcurrentContext;
import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.apex.context.test.utils.ConfigrationProviderImpl;
import org.onap.policy.apex.context.test.utils.ZooKeeperServerServiceProvider;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.plugins.context.distribution.hazelcast.HazelcastContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanDistributorParameters;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManager;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManagerParameters;
import org.onap.policy.apex.plugins.context.locking.hazelcast.HazelcastLockManager;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class concurrentContextMetrics tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ConcurrentContextMetrics {
    private static final int NUM_ARGS = 9;
    private static final int ARG_LABEL = 0;
    private static final int ARG_JVM_COUNT = 1;
    private static final int ARG_THREAD_COUNT = 2;
    private static final int ARG_ITERATIONS = 3;
    private static final int ARG_ARRAY_SIZE = 4;
    private static final int ARG_LOCK_TYPE = 5;
    private static final int ARG_ZOOKEEPER_ADDRESS = 6;
    private static final int ARG_ZOOKEEPER_PORT = 7;
    private static final int ARG_ZOOKEEPER_DIRECTORY = 8;

    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConcurrentContextMetrics.class);

    private String zookeeperAddress = null;
    private final ConfigrationProvider configrationProvider;
    private final File zookeeperDirectory;
    private final int zookeeperPort;

    /**
     * The main method.
     *
     * @param args the args
     * @throws Exception the exception
     */
    public static void main(final String[] args) throws Exception {
        if (args.length != NUM_ARGS) {
            String errorMessage = "Args: " + Arrays.toString(args)
                            + "\nusage: testLabel jvmCount threadCount threadLoops longArraySize lockType "
                            + "zookeeperAddress zookeeperPort zookeeperDirectory";
            LOGGER.info(errorMessage);
            return;
        }

        final ConfigrationProvider configrationProvider = new ConfigrationProviderImpl(args[ARG_LABEL],
                        Integer.valueOf(args[ARG_JVM_COUNT]), Integer.valueOf(args[ARG_THREAD_COUNT]),
                        Integer.valueOf(args[ARG_ITERATIONS]), Integer.valueOf(args[ARG_ARRAY_SIZE]),
                        Integer.valueOf(args[ARG_LOCK_TYPE]));

        final ConcurrentContextMetrics concurrentContextMetrics = new ConcurrentContextMetrics(configrationProvider,
                        args[ARG_ZOOKEEPER_ADDRESS], Integer.valueOf(args[ARG_ZOOKEEPER_PORT]),
                        args[ARG_ZOOKEEPER_DIRECTORY]);

        concurrentContextMetrics.concurrentContextMetricsJvmLocal();
        concurrentContextMetrics.concurrentContextMetricsCurator();
        concurrentContextMetrics.concurrentContextMetricsHazelcast();
        concurrentContextMetrics.concurrentContextMetricsHazelcastMultiJvmHazelcastLock();
        concurrentContextMetrics.concurrentContextMetricsInfinispanMultiJvmHazelcastlock();
        concurrentContextMetrics.concurrentContextMetricsInfinispanMultiJvmCuratorLock();
        concurrentContextMetrics.concurrentContextMetricsHazelcastMultiJvmCuratorLock();
    }

    /**
     * Construct a concurrent context object.
     * 
     * @param configrationProvider Configuration for the context metrics
     * @param zookeeperAddress Zookeeper address
     * @param zookeeperPort Zookeeper port
     * @param zookeeperDirectory Zookeeper directory
     */
    public ConcurrentContextMetrics(final ConfigrationProvider configrationProvider, final String zookeeperAddress,
                    final int zookeeperPort, final String zookeeperDirectory) {
        this.configrationProvider = configrationProvider;
        this.zookeeperAddress = zookeeperAddress;
        this.zookeeperPort = zookeeperPort;
        this.zookeeperDirectory = new File(zookeeperDirectory);
    }

    /**
     * Concurrent context metrics JVM local.
     *
     * @throws ApexModelException the apex model exception
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    private void concurrentContextMetricsJvmLocal() throws IOException, ApexException {
        if (configrationProvider.getJvmCount() != 1) {
            return;
        }

        LOGGER.debug("Running concurrentContextMetricsJVMLocalVarSet metrics . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);
        contextParameters.getLockManagerParameters().setPluginClass(DEFAULT_LOCK_MANAGER_PLUGIN_CLASS);
        runConcurrentContextMetrics("JVMLocal");

        LOGGER.debug("Ran concurrentContextMetricsJVMLocalVarSet metrics");
    }

    /**
     * Concurrent context metrics hazelcast.
     *
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    private void concurrentContextMetricsHazelcast() throws IOException, ApexException {
        if (configrationProvider.getJvmCount() != 1) {
            return;
        }

        LOGGER.debug("Running concurrentContextMetricsHazelcast metrics . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        runConcurrentContextMetrics("Hazelcast");

        LOGGER.debug("Ran concurrentContextMetricsHazelcast metrics");
    }

    /**
     * Concurrent context metrics curator.
     *
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    private void concurrentContextMetricsCurator() throws IOException, ApexException {
        if (configrationProvider.getJvmCount() != 1) {
            return;
        }

        LOGGER.debug("Running concurrentContextMetricsCurator metrics . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);

        final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
        curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
        contextParameters.setLockManagerParameters(curatorParameters);
        curatorParameters.setZookeeperAddress(zookeeperAddress);

        runConcurrentContextMetrics("Curator");

        LOGGER.debug("Ran concurrentContextMetricsCurator metrics");
    }

    /**
     * Concurrent context metrics hazelcast multi JVM hazelcast lock.
     *
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    private void concurrentContextMetricsHazelcastMultiJvmHazelcastLock() throws IOException, ApexException {
        LOGGER.debug("Running concurrentContextMetricsHazelcastMultiJVMHazelcastLock metrics . . .");

        final ContextParameters contextParameters = new ContextParameters();
        final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
        distributorParameters.setPluginClass(HazelcastContextDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        runConcurrentContextMetrics("HazelcastMultiJVMHazelcastLock");

        LOGGER.debug("Ran concurrentContextMetricsHazelcastMultiJVMHazelcastLock metrics");
    }

    /**
     * Concurrent context metrics infinispan multi JVM hazelcastlock.
     *
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    private void concurrentContextMetricsInfinispanMultiJvmHazelcastlock() throws IOException, ApexException {
        LOGGER.debug("Running concurrentContextMetricsInfinispanMultiJVMHazelcastlock metrics . . .");

        final ContextParameters contextParameters = new ContextParameters();
        final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
        distributorParameters.setPluginClass(InfinispanContextDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());

        final InfinispanDistributorParameters infinispanParameters = new InfinispanDistributorParameters();
        contextParameters.setDistributorParameters(infinispanParameters);

        runConcurrentContextMetrics("InfinispanMultiJVMHazelcastlock");

        LOGGER.debug("Ran concurrentContextMetricsInfinispanMultiJVMHazelcastlock metrics");
    }

    /**
     * Concurrent context metrics infinispan multi JVM curator lock.
     *
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     * @throws InterruptedException on interrupts
     */
    private void concurrentContextMetricsInfinispanMultiJvmCuratorLock()
                    throws IOException, ApexException, InterruptedException {

        LOGGER.debug("Running concurrentContextMetricsInfinispanMultiJVMCuratorLock metrics . . .");

        final ZooKeeperServerServiceProvider zooKeeperServerServiceProvider = new ZooKeeperServerServiceProvider(
                        zookeeperDirectory, zookeeperAddress, zookeeperPort);
        try {
            zooKeeperServerServiceProvider.startZookeeperServer();
            final ContextParameters contextParameters = new ContextParameters();
            final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
            distributorParameters.setPluginClass(InfinispanContextDistributor.class.getCanonicalName());

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            contextParameters.setLockManagerParameters(curatorParameters);
            curatorParameters.setZookeeperAddress(zookeeperAddress);

            final InfinispanDistributorParameters infinispanParameters = new InfinispanDistributorParameters();
            contextParameters.setDistributorParameters(infinispanParameters);

            runConcurrentContextMetrics("InfinispanMultiJVMCuratorLock");
        } finally {
            zooKeeperServerServiceProvider.stopZookeeperServer();
        }
        LOGGER.debug("Ran concurrentContextMetricsInfinispanMultiJVMCuratorLock metrics");
    }

    /**
     * Concurrent context metrics hazelcast multi JVM curator lock.
     *
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     * @throws InterruptedException on interrupts
     */
    private void concurrentContextMetricsHazelcastMultiJvmCuratorLock()
                    throws IOException, ApexException, InterruptedException {
        LOGGER.debug("Running concurrentContextMetricsHazelcastMultiJVMCuratorLock metrics . . .");

        final ZooKeeperServerServiceProvider zooKeeperServerServiceProvider = new ZooKeeperServerServiceProvider(
                        zookeeperDirectory, zookeeperAddress, zookeeperPort);

        try {
            zooKeeperServerServiceProvider.startZookeeperServer();
            final ContextParameters contextParameters = new ContextParameters();
            final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
            distributorParameters.setPluginClass(HazelcastContextDistributor.class.getCanonicalName());

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            contextParameters.setLockManagerParameters(curatorParameters);
            curatorParameters.setZookeeperAddress(zookeeperAddress);

            runConcurrentContextMetrics("HazelcastMultiJVMCuratorLock");
        } finally {
            zooKeeperServerServiceProvider.stopZookeeperServer();
        }
        LOGGER.debug("Ran concurrentContextMetricsHazelcastMultiJVMCuratorLock metrics");
    }

    /**
     * Run concurrent context metrics.
     *
     * @param testName the test name
     * @throws IOException the IO exception
     * @throws ApexException the apex exception
     */
    private void runConcurrentContextMetrics(final String testName) throws IOException, ApexException {
        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);

        LOGGER.info("Running {} ...", testName);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        long total = 0;
        for (final Entry<String, TestContextLongItem> entry : result.entrySet()) {
            LOGGER.trace("Album key: {}, value: {}", entry.getKey(), entry.getValue());
            total += entry.getValue().getLongValue();
        }
        LOGGER.info("Album total value after execution: {}", total);

        LOGGER.info("Completed {} ...", testName);
    }

}
