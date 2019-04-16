/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.onap.policy.apex.context.parameters.DistributorParameters.DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;
import static org.onap.policy.apex.testsuites.integration.context.utils.Constants.TEST_VALUE;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.SortedSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JvmLocalDistributor;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JvmLocalLockManager;
import org.onap.policy.apex.context.impl.schema.java.JavaSchemaHelperParameters;
import org.onap.policy.apex.context.parameters.ContextParameterConstants;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.context.test.concepts.TestContextLongItem;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.plugins.context.distribution.hazelcast.HazelcastContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanDistributorParameters;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManager;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManagerParameters;
import org.onap.policy.apex.plugins.context.locking.hazelcast.HazelcastLockManager;
import org.onap.policy.apex.testsuites.integration.context.lock.modifier.LockType;
import org.onap.policy.apex.testsuites.integration.context.locking.ConcurrentContext;
import org.onap.policy.apex.testsuites.integration.context.utils.ConfigrationProvider;
import org.onap.policy.apex.testsuites.integration.context.utils.ConfigrationProviderImpl;
import org.onap.policy.apex.testsuites.integration.context.utils.Constants;
import org.onap.policy.apex.testsuites.integration.context.utils.NetworkUtils;
import org.onap.policy.apex.testsuites.integration.context.utils.ZooKeeperServerServiceProvider;
import org.onap.policy.common.parameters.ParameterService;
import org.onap.policy.common.utils.resources.ResourceUtils;
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

    private static SchemaParameters schemaParameters;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private ZooKeeperServerServiceProvider zooKeeperServerServiceProvider;

    /**
     * Configure.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void configure() throws Exception {
        System.setProperty(JAVA_NET_PREFER_IPV4_STACK, "true");
        final String hazelCastfileLocation = ResourceUtils.getFilePath4Resource(HAZELCAST_XML_FILE);
        System.setProperty(HAZELCAST_CONFIG, hazelCastfileLocation);

        final SortedSet<String> ipAddressSet = NetworkUtils.getIPv4NonLoopAddresses();

        if (ipAddressSet.size() == 0) {
            throw new Exception("cound not find real IP address for test");
        }
        logger.info("For Infinispan, setting jgroups.tcp.address to: {}", ipAddressSet.first());
        System.setProperty("jgroups.tcp.address", ipAddressSet.first());

        schemaParameters = new SchemaParameters();

        schemaParameters.setName(ContextParameterConstants.SCHEMA_GROUP_NAME);
        schemaParameters.getSchemaHelperParameterMap().put("JAVA", new JavaSchemaHelperParameters());

        ParameterService.register(schemaParameters, true);
    }

    /**
     * Clear configuration.
     */
    @AfterClass
    public static void clear() {
        ParameterService.deregister(schemaParameters);
    }

    /**
     * Start zookeeper server.
     *
     * @throws Exception the exception
     */
    private void startZookeeperServer() throws Exception {
        final File zookeeperDirectory = folder.newFolder("zookeeperDirectory");

        zookeeperPort = nextZookeeperPort++;
        final InetSocketAddress addr = new InetSocketAddress(MessagingUtils.findPort(zookeeperPort));
        zooKeeperServerServiceProvider = new ZooKeeperServerServiceProvider(zookeeperDirectory, addr);
        zooKeeperServerServiceProvider.startZookeeperServer();
    }

    /**
     * Stop zookeeper server.
     */
    private void stopZookeeperServer() {
        if (zooKeeperServerServiceProvider != null) {
            zooKeeperServerServiceProvider.stopZookeeperServer();
        }
    }

    /**
     * Test concurrent context jvm local var set.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextJvmLocalVarSet() throws Exception {
        logger.debug("Running testConcurrentContextJVMLocalVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters().setPluginClass(JvmLocalLockManager.class.getCanonicalName());
        setContextParmetersInParameterService(contextParameters);

        final ConfigrationProvider configrationProvider = getConfigrationProvider("JVMLocalVarSet",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        assertFalse(result.isEmpty());

        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        clearContextParmetersInParameterService(contextParameters);

        logger.debug("Ran testConcurrentContextJVMLocalVarSet test");
    }

    /**
     * Test concurrent context jvm local no var set.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextJvmLocalNoVarSet() throws Exception {
        logger.debug("Running testConcurrentContextJVMLocalNoVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        setContextParmetersInParameterService(contextParameters);

        final ConfigrationProvider configrationProvider = getConfigrationProvider("JVMLocalNoVarSet",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(Constants.TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        clearContextParmetersInParameterService(contextParameters);
        logger.debug("Ran testConcurrentContextJVMLocalNoVarSet test");
    }

    /**
     * Test concurrent context multi jvm no lock.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextMultiJvmNoLock() throws Exception {
        logger.debug("Running testConcurrentContextMultiJVMNoLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(JvmLocalDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(JvmLocalLockManager.class.getCanonicalName());
        setContextParmetersInParameterService(contextParameters);

        final ConfigrationProvider configrationProvider = getConfigrationProvider("testConcurrentContextMultiJVMNoLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        // No concurrent map so result will be zero
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(0, actual.getLongValue());

        clearContextParmetersInParameterService(contextParameters);
        logger.debug("Ran testConcurrentContextMultiJVMNoLock test");
    }

    /**
     * Test concurrent context hazelcast lock.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextHazelcastLock() throws Exception {
        logger.debug("Running testConcurrentContextHazelcastLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        setContextParmetersInParameterService(contextParameters);

        final ConfigrationProvider configrationProvider = getConfigrationProvider("HazelcastLock",
                TEST_JVM_COUNT_SINGLE_JVM, TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        clearContextParmetersInParameterService(contextParameters);
        logger.debug("Ran testConcurrentContextHazelcastLock test");
    }

    /**
     * Test concurrent context curator lock.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextCuratorLock() throws Exception {
        logger.debug("Running testConcurrentContextCuratorLock test . . .");
        final ContextParameters contextParameters = new ContextParameters();
        try {
            startZookeeperServer();
            final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
            distributorParameters.setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
            contextParameters.setLockManagerParameters(curatorParameters);
            setContextParmetersInParameterService(contextParameters);

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
            clearContextParmetersInParameterService(contextParameters);
        }
    }

    /**
     * Test concurrent context hazelcast multi jvm hazelcast lock.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextHazelcastMultiJvmHazelcastLock() throws Exception {
        logger.debug("Running testConcurrentContextHazelcastMultiJVMHazelcastLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        final DistributorParameters distributorParameters = contextParameters.getDistributorParameters();
        distributorParameters.setPluginClass(HazelcastContextDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        setContextParmetersInParameterService(contextParameters);

        final ConfigrationProvider configrationProvider = getConfigrationProvider("HazelcastMultiHazelcastlock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        clearContextParmetersInParameterService(contextParameters);
        logger.debug("Ran testConcurrentContextHazelcastMultiJVMHazelcastLock test");
    }

    /**
     * Test concurrent context infinispan multi jvm hazelcastlock.
     *
     * @throws ApexModelException the apex model exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ApexException the apex exception
     */
    @Test
    public void testConcurrentContextInfinispanMultiJvmHazelcastlock()
            throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextInfinispanMultiJVMHazelcastlock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        final InfinispanDistributorParameters infinispanParameters = new InfinispanDistributorParameters();
        infinispanParameters.setPluginClass(InfinispanContextDistributor.class.getCanonicalName());
        infinispanParameters.setConfigFile("infinispan/infinispan-context-test.xml");
        contextParameters.setDistributorParameters(infinispanParameters);
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        setContextParmetersInParameterService(contextParameters);

        final ConfigrationProvider configrationProvider = getConfigrationProvider("InfinispanMultiHazelcastlock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        final ConcurrentContext concurrentContext = new ConcurrentContext(configrationProvider);
        final Map<String, TestContextLongItem> result = concurrentContext.testConcurrentContext();

        final int expected = TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS;
        final TestContextLongItem actual = result.get(TEST_VALUE);
        assertNotNull(actual);
        assertEquals(expected, actual.getLongValue());

        clearContextParmetersInParameterService(contextParameters);
        logger.debug("Ran testConcurrentContextInfinispanMultiJVMHazelcastlock test");
    }

    /**
     * Test concurrent context infinispan multi jvm curator lock.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextInfinispanMultiJvmCuratorLock() throws Exception {
        logger.debug("Running testConcurrentContextInfinispanMultiJVMCuratorLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        try {
            startZookeeperServer();

            final InfinispanDistributorParameters infinispanParameters = new InfinispanDistributorParameters();
            infinispanParameters.setPluginClass(InfinispanContextDistributor.class.getCanonicalName());
            infinispanParameters.setConfigFile("infinispan/infinispan-context-test.xml");
            contextParameters.setDistributorParameters(infinispanParameters);

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
            contextParameters.setLockManagerParameters(curatorParameters);
            setContextParmetersInParameterService(contextParameters);

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
            clearContextParmetersInParameterService(contextParameters);
        }

        logger.debug("Ran testConcurrentContextInfinispanMultiJVMCuratorLock test");
    }

    /**
     * Test concurrent context hazelcast multi jvm curator lock.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConcurrentContextHazelcastMultiJvmCuratorLock() throws Exception {
        logger.debug("Running testConcurrentContextHazelcastMultiJVMCuratorLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        try {
            startZookeeperServer();

            contextParameters.getDistributorParameters()
                    .setPluginClass(HazelcastContextDistributor.class.getCanonicalName());

            final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
            curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
            curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
            contextParameters.setLockManagerParameters(curatorParameters);
            setContextParmetersInParameterService(contextParameters);

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
            clearContextParmetersInParameterService(contextParameters);
        }
        logger.debug("Ran testConcurrentContextHazelcastMultiJVMCuratorLock test");
    }

    /**
     * Gets the configration provider.
     *
     * @param testType the test type
     * @param jvmCount the jvm count
     * @param threadCount the thread count
     * @param threadLoops the thread loops
     * @return the configration provider
     */
    ConfigrationProvider getConfigrationProvider(final String testType, final int jvmCount, final int threadCount,
            final int threadLoops) {
        return new ConfigrationProviderImpl(testType, jvmCount, threadCount, threadLoops, 16,
                LockType.WRITE_LOCK_SINGLE_VALUE_UPDATE.getValue()) {
            @Override
            public Map<String, Object> getContextAlbumInitValues() {
                final Map<String, Object> initValues = super.getContextAlbumInitValues();
                initValues.put(TEST_VALUE, new TestContextLongItem(0L));
                return initValues;
            }

        };
    }

    /**
     * Set the context parameters in the parameter service.
     *
     * @param contextParameters The parameters to set.
     */
    private void setContextParmetersInParameterService(final ContextParameters contextParameters) {
        ParameterService.register(contextParameters);
        ParameterService.register(contextParameters.getDistributorParameters());
        ParameterService.register(contextParameters.getLockManagerParameters());
        ParameterService.register(contextParameters.getPersistorParameters());
    }

    /**
     * Clear the context parameters in the parameter service.
     *
     * @param contextParameters The parameters to set.
     */
    private void clearContextParmetersInParameterService(final ContextParameters contextParameters) {
        ParameterService.deregister(contextParameters.getPersistorParameters());
        ParameterService.deregister(contextParameters.getLockManagerParameters());
        ParameterService.deregister(contextParameters.getDistributorParameters());
        ParameterService.deregister(contextParameters);

    }
}
