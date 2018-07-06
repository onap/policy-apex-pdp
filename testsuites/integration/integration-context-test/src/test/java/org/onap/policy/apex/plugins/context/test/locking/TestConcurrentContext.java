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
import static org.onap.policy.apex.context.parameters.DistributorParameters.DEFAULT_DISTRIBUTOR_PLUGIN_CLASS;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TreeSet;

import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.apex.context.impl.distribution.jvmlocal.JVMLocalDistributor;
import org.onap.policy.apex.context.impl.locking.jvmlocal.JVMLocalLockManager;
import org.onap.policy.apex.context.parameters.ContextParameters;
import org.onap.policy.apex.context.parameters.DistributorParameters;
import org.onap.policy.apex.context.parameters.LockManagerParameters;
import org.onap.policy.apex.context.test.locking.ConcurrentContext;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.plugins.context.distribution.hazelcast.HazelcastContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanContextDistributor;
import org.onap.policy.apex.plugins.context.distribution.infinispan.InfinispanDistributorParameters;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManager;
import org.onap.policy.apex.plugins.context.locking.curator.CuratorLockManagerParameters;
import org.onap.policy.apex.plugins.context.locking.hazelcast.HazelcastLockManager;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.hazelcast.config.Config;

/**
 * The Class TestConcurrentContext tests concurrent use of context.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestConcurrentContext {
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

    private NIOServerCnxnFactory zookeeperFactory;

    // We need to increment the Zookeeper port because sometimes the port is not released at the end
    // of the test for a few seconds.
    private static int nextZookeeperPort = ZOOKEEPER_START_PORT;
    private int zookeeperPort;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void configure() throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("hazelcast.config", "src/test/resources/hazelcast/hazelcast.xml");

        // The JGroups IP address must be set to a real (not loopback) IP address for Infinispan to
        // work. IN order to ensure that all
        // the JVMs in a test pick up the same IP address, this function sets the address to be the
        // first non-loopback IPv4 address
        // on a host
        final TreeSet<String> ipAddressSet = new TreeSet<String>();

        final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (final NetworkInterface netint : Collections.list(nets)) {
            final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (final InetAddress inetAddress : Collections.list(inetAddresses)) {
                // Look for real IPv4 internet addresses
                if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                    ipAddressSet.add(inetAddress.getHostAddress());
                }
            }
        }

        if (ipAddressSet.size() == 0) {
            throw new Exception("cound not find real IP address for test");
        }
        System.out.println("For Infinispan, setting jgroups.tcp.address to: " + ipAddressSet.first());
        System.setProperty("jgroups.tcp.address", ipAddressSet.first());

        final Config config = new Config();
        config.getNetworkConfig().setPublicAddress(ipAddressSet.first());
        config.getNetworkConfig().getInterfaces().addInterface(ipAddressSet.first());
    }

    @AfterClass
    public static void teardown() throws IOException {}

    private void startZookeeperServer() throws IOException, InterruptedException {
        final File zookeeperDirectory = folder.newFolder("zookeeperDirectory");

        zookeeperPort = nextZookeeperPort++;

        final ZooKeeperServer server = new ZooKeeperServer(zookeeperDirectory, zookeeperDirectory, 5000);
        zookeeperFactory = new NIOServerCnxnFactory();
        zookeeperFactory.configure(new InetSocketAddress(zookeeperPort), 100);

        zookeeperFactory.startup(server);
    }

    private void stopZookeeperServer() {
        zookeeperFactory.shutdown();
    }

    @Test
    public void testConcurrentContextJVMLocalVarSet() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextJVMLocalVarSet test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getLockManagerParameters().setPluginClass(JVMLocalLockManager.class.getCanonicalName());
        final long result = new ConcurrentContext().testConcurrentContext("JVMLocalVarSet", TEST_JVM_COUNT_SINGLE_JVM,
                TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS, result);

        logger.debug("Ran testConcurrentContextJVMLocalVarSet test");
    }

    @Test
    public void testConcurrentContextJVMLocalNoVarSet() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextJVMLocalNoVarSet test . . .");

        new ContextParameters();
        final long result = new ConcurrentContext().testConcurrentContext("JVMLocalNoVarSet", TEST_JVM_COUNT_SINGLE_JVM,
                TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS, result);

        logger.debug("Ran testConcurrentContextJVMLocalNoVarSet test");
    }

    @Test
    public void testConcurrentContextMultiJVMNoLock() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextMultiJVMNoLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(JVMLocalDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(JVMLocalLockManager.class.getCanonicalName());

        final long result = new ConcurrentContext().testConcurrentContext("testConcurrentContextMultiJVMNoLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        // No concurrent map so result will be zero
        assertEquals(0, result);

        logger.debug("Ran testConcurrentContextMultiJVMNoLock test");
    }

    @Test
    public void testConcurrentContextHazelcastLock() throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextHazelcastLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters().setPluginClass(DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        final long result = new ConcurrentContext().testConcurrentContext("HazelcastLock", TEST_JVM_COUNT_SINGLE_JVM,
                TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS, result);
        logger.debug("Ran testConcurrentContextHazelcastLock test");
    }

    @Test
    public void testConcurrentContextCuratorLock()
            throws ApexModelException, IOException, ApexException, InterruptedException {
        logger.debug("Running testConcurrentContextCuratorLock test . . .");

        startZookeeperServer();

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters()
                .setPluginClass(DistributorParameters.DEFAULT_DISTRIBUTOR_PLUGIN_CLASS);

        final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
        curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
        curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
        contextParameters.setLockManagerParameters(curatorParameters);
        ParameterService.registerParameters(LockManagerParameters.class, curatorParameters);

        final long result = new ConcurrentContext().testConcurrentContext("CuratorLock", TEST_JVM_COUNT_SINGLE_JVM,
                TEST_THREAD_COUNT_SINGLE_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_SINGLE_JVM * TEST_THREAD_COUNT_SINGLE_JVM * TEST_THREAD_LOOPS, result);

        stopZookeeperServer();
        logger.debug("Ran testConcurrentContextCuratorLock test");
    }

    @Test
    public void testConcurrentContextHazelcastMultiJVMHazelcastLock()
            throws ApexModelException, IOException, ApexException {
        logger.debug("Running testConcurrentContextHazelcastMultiJVMHazelcastLock test . . .");

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters()
                .setPluginClass(HazelcastContextDistributor.class.getCanonicalName());
        contextParameters.getLockManagerParameters().setPluginClass(HazelcastLockManager.class.getCanonicalName());
        final long result = new ConcurrentContext().testConcurrentContext("HazelcastMultiHazelcastlock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS, result);
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

        final long result = new ConcurrentContext().testConcurrentContext("InfinispanMultiHazelcastlock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS, result);
        logger.debug("Ran testConcurrentContextInfinispanMultiJVMHazelcastlock test");
    }

    @Test
    public void testConcurrentContextInfinispanMultiJVMCuratorLock()
            throws ApexModelException, IOException, ApexException, InterruptedException {
        logger.debug("Running testConcurrentContextInfinispanMultiJVMCuratorLock test . . .");

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

        final long result = new ConcurrentContext().testConcurrentContext("InfinispanMultiCuratorLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS, result);

        stopZookeeperServer();

        logger.debug("Ran testConcurrentContextInfinispanMultiJVMCuratorLock test");
    }

    @Test
    public void testConcurrentContextHazelcastMultiJVMCuratorLock()
            throws ApexModelException, IOException, ApexException, InterruptedException {
        logger.debug("Running testConcurrentContextHazelcastMultiJVMCuratorLock test . . .");

        startZookeeperServer();

        final ContextParameters contextParameters = new ContextParameters();
        contextParameters.getDistributorParameters()
                .setPluginClass(HazelcastContextDistributor.class.getCanonicalName());

        final CuratorLockManagerParameters curatorParameters = new CuratorLockManagerParameters();
        curatorParameters.setPluginClass(CuratorLockManager.class.getCanonicalName());
        curatorParameters.setZookeeperAddress(ZOOKEEPER_ADDRESS + ":" + zookeeperPort);
        contextParameters.setLockManagerParameters(curatorParameters);
        ParameterService.registerParameters(LockManagerParameters.class, curatorParameters);

        final long result = new ConcurrentContext().testConcurrentContext("HazelcastMultiCuratorLock",
                TEST_JVM_COUNT_MULTI_JVM, TEST_THREAD_COUNT_MULTI_JVM, TEST_THREAD_LOOPS);

        assertEquals(TEST_JVM_COUNT_MULTI_JVM * TEST_THREAD_COUNT_MULTI_JVM * TEST_THREAD_LOOPS, result);

        stopZookeeperServer();
        logger.debug("Ran testConcurrentContextHazelcastMultiJVMCuratorLock test");
    }
}
