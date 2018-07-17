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

package org.onap.policy.apex.plugins.context.test.locking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.onap.policy.apex.context.parameters.DistributorParameters.DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;
import static org.onap.policy.apex.context.test.utils.Constants.TEST_VALUE;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JVMLocalDistributor;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JVMLocalLockManager;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.context.test.lock.modifier.LockType;
import org.onap.policy.apex.context.test.locking.ConcurrentContext;
import org.onap.policy.apex.context.test.utils.ConfigrationProvider;
import org.onap.policy.apex.context.test.utils.ConfigrationProviderImpl;
import org.onap.policy.apex.context.test.utils.Constants;
import org.onap.policy.apex.context.test.utils.NetworkUtils;
import org.onap.policy.apex.context.test.utils.ZooKeeperServerServiceProvider;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.model.utilities.ResourceUtils;
import org.onap.policy.apex.plugins.context.distribution.hazelcast.HazelcastContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanDistributorParameters;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManager;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManagerParameters;
import org.onap.policy.apex.plugins.context.locking.hazelcast.HazelcastLockManager;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestConcurrentContext tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestConcurrentContext {
    private static final String HAZELCAST_CONFIG = "hazelcast.config";

    private static final String JAVA_NET_PREFER_IPV4_STACK = "java.net.preferIPv4Stack";
    private static final String HAZELCAST_XML_FILE = "src/test/resources/hazelcast/hazelcast.xml";

    // Logger for this class
    private static final XLogger logger = XLoggerFactory.getXLogger(TestConcurrentContext.class);

    // Test parameters
    private static final String ZOOKEEPER_ADDRESS = "127.0.0.1";
    private static final int ZOOKEEPER_START_PORT = 62181;
    private static final int TEST_JVM_COUNT_SINGLE_JVM = 1;
    private static final int TEST_JVM_COUNT_MULTI_JVM = 3;
    private static final int TEST_THREAD_COUNT_SINGLE_JVM = 64;
    private static final int TEST_THREAD_COUNT_MULTI_JVM = 20;
    private static final int TEST_THREAD_LOOPS = 100;

    // We need to increment the Zookeeper port because sometimes the port is not released at the end
    // of the test for a few seconds.
    private static int nextZookeeperPort = ZOOKEEPER_START_PORT;
    private int zookeeperPort;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private ZooKeeperServerServiceProvider zooKeeperServerServiceProvider;

    @BeforeClass
    public static void configure() throws Exception {
        System.setProperty(JAVA_NET_PREFER_IPV4_STACK, "true");
        final String hazelCastfileLocation = ResourceUtils.getFilePath4Resource(HAZELCAST_XML_FILE);
        System.setProperty(HAZELCAST_CONFIG, hazelCastfileLocation);

        final TreeSet<String> ipAddressSet = NetworkUtils.getIPv4NonLoopAddresses();

        if (ipAddressSet.size() == 0) {
            throw new Exception("cound not find real IP address for test");
        }
        logger.info("For Infinispan, setting jgroups.tcp.address to: {}", ipAddressSet.first());
        System.setProperty("jgroups.tcp.address", ipAddressSet.first());

    }

    private void startZookeeperServer() throws Exception {
        final File zookeeperDirectory = folder.newFolder("zookeeperDirectory");

        zookeeperPort = nextZookeeperPort++;
        final InetSocketAddress addr = new InetSocketAddress(MessagingUtils.findPort(zookeeperPort));
        zooKeeperServerServiceProvider = new ZooKeeperServerServiceProvider(zookeeperDirectory, addr);
        zooKeeperServerServiceProvider.startZookeeperServer();
    }

    private void stopZookeeperServer() {
        if (zooKeeperServerServiceProvider != null) {
            zooKeeperServerServiceProvider.stopZookeeperServer();
        }
    }

    @Test
    public void testConcurrentContextJVMLocalVarSet() throws Exception {
        logger.debug("Running testConcurrentContextJVMLocalVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters().setPluginClass(JVMLocalLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("JVMLocalVarSet",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        assertFalse(result.isEmpty());

        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        logger.debug("Ran testConcurrentContextJVMLocalVarSet test");
    }

    @Test
    public void testConcurrentContextJVMLocalNoVarSet() throws Exception {
        logger.debug("Running testConcurrentContextJVMLocalNoVarSet test . . .");

        new ContextParameters();
        final ConfigrationProvider configrationProvider = getConfigrationProvider("JVMLocalNoVarSet",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(Constants.TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        logger.debug("Ran testConcurrentContextJVMLocalNoVarSet test");
    }

    @Test
    public void testConcurrentContextMultiJVMNoLock() throws Exception {
        logger.debug("Running testConcurrentContextMultiJVMNoLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(JVMLocalDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(JVMLocalLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("testConcurrentContextMultiJVMNoLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        // No concurrent map so result will be zero
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(0, actual.getLongValue());

        logger.debug("Ran testConcurrentContextMultiJVMNoLock test");
    }

    @Test
    public void testConcurrentContextHazelcastLock() throws Exception {
        logger.debug("Running testConcurrentContextHazelcastLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("HazelcastLock",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        logger.debug("Ran testConcurrentContextHazelcastLock test");
    }

    @Test
    public void testConcurrentContextCuratorLock() throws Exception {
        logger.debug("Running testConcurrentContextCuratorLock test . . .");
        try {
            startZookeeperServer();
            final ContextParameters contextParameters = new ContextParameters();
            final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
            distributorParameters.setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
            contextParameters.setLockManagerParameters(curatorParameters);
            ParameterService.registerParameters(LockManagerParameters.class, curatorParameters);

            final ConfigrationProvider configrationProvider = getConfigrationProvider("CuratorLock",
                    TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

            final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
            final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

            final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
            final TestContextLongItem actual = result.get(TEST_VALUE);
            assertNotNull(actual);
            assertEquals(expected, actual.getLongValue());
            logger.debug("Ran testConcurrentContextCuratorLock test");
        } finally {
            stopZookeeperServer();
        }
    }

    @Test
    public void testConcurrentContextHazelcastMultiJVMHazelcastLock() throws Exception {
        logger.debug("Running testConcurrentContextHazelcastMultiJVMHazelcastLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
        distributorParameters.setPluginClass(HazelcastContextDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("HazelcastMultiHazelcastlock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());
        logger.debug("Ran testConcurrentContextHazelcastMultiJVMHazelcastLock test");
    }

    @Test
    public void testConcurrentContextInfinispanMultiJVMHazelcastlock()
            throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextInfinispanMultiJVMHazelcastlock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        final InfinispanDistributorParameters infinispanParameters = new InfinispanDistributorParameters();
        infinispanParameters.setPluginClass(InfinispanContextDistributor.class.getCanonicalName());
        infinispanParameters.setConfigFile("infinispan/infinispan-context-test.xml");
        contextParameters.setDistributorParameters(infinispanParameters);
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());

        final ConfigrationProvider configrationProvider = getConfigrationProvider("InfinispanMultiHazelcastlock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());
        logger.debug("Ran testConcurrentContextInfinispanMultiJVMHazelcastlock test");
    }

    @Test
    public void testConcurrentContextInfinispanMultiJVMCuratorLock() throws Exception {
        logger.debug("Running testConcurrentContextInfinispanMultiJVMCuratorLock test . . .");

        try {
            startZookeeperServer();

            final ContextParameters contextParameters = new ContextParameters();
            final InfinispanDistributorParameters infinispanParameters = new InfinispanDistributorParameters();
            infinispanParameters.setPluginClass(InfinispanContextDistributor.class.getCanonicalName());
            infinispanParameters.setConfigFile("infinispan/infinispan-context-test.xml");
            contextParameters.setDistributorParameters(infinispanParameters);

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
            contextParameters.setLockManagerParameters(curatorParameters);
            ParameterService.registerParameters(LockManagerParameters.class, curatorParameters);

            final ConfigrationProvider configrationProvider = getConfigrationProvider("InfinispanMultiCuratorLock",
                    TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

            final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
            final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

            final int expected = TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS;
            final TestContextLongItem actual = result.get(TEST_VALUE);
            assertNotNull(actual);
            assertEquals(expected, actual.getLongValue());
        } finally {
            stopZookeeperServer();
        }

        logger.debug("Ran testConcurrentContextInfinispanMultiJVMCuratorLock test");
    }

    @Test
    public void testConcurrentContextHazelcastMultiJVMCuratorLock() throws Exception {
        logger.debug("Running testConcurrentContextHazelcastMultiJVMCuratorLock test . . .");

        try {
            startZookeeperServer();

            final ContextParameters contextParameters = new ContextParameters();
            contextParameters.getDistributorParameters()
                    .setPluginClass(HazelcastContextDistributor.class.getCanonicalName());

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
            contextParameters.setLockManagerParameters(curatorParameters);
            ParameterService.registerParameters(LockManagerParameters.class, curatorParameters);

            final ConfigrationProvider configrationProvider = getConfigrationProvider("HazelcastMultiCuratorLock",
                    TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);
            final Map<String, TestContextLongItem> result =
                    new ConcurrentContext(configrationProvider).testConcurrentContext();

            final int expected = TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS;
            final TestContextLongItem actual = result.get(TEST_VALUE);
            assertNotNull(actual);
            assertEquals(expected, actual.getLongValue());
        } finally {
            stopZookeeperServer();
        }
        logger.debug("Ran testConcurrentContextHazelcastMultiJVMCuratorLock test");
    }

    ConfigrationProvider getConfigrationProvider(final String testType, final int jvmCount, final int threadCount,
            final int threadLoops) {
        return new ConfigrationProviderImpl(testType, jvmCount, threadCount, threadLoops, 16,
                LockType.WRITE_LOCK_SINGLE_VALUE_UPDATE.getValue()) {
            @Override
            public Map<String, Object> getContextAlbumInitValues() {
                final Map<String, Object> initValues = super.getContextAlbumInitValues();
                initValues.put(TEST_VALUE, new TestContextLongItem(0l));
                return initValues;
            }

        };
    }
}
