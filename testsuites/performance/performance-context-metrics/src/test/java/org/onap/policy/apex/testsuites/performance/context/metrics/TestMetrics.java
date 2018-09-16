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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.SortedSet;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.onap.policy.apex.context.test.utils.NetworkUtils;
import org.onap.policy.apex.testsuites.performance.context.metrics.ConcurrentContextMetrics;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestMetrics.
 */
public class TestMetrics {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestMetrics.class);
    private static final String HAZELCAST_CONFIG = "hazelcast.config";

    private static final String JAVA_NET_PREFER_IPV4_STACK = "java.net.preferIPv4Stack";
    private static final String HAZELCAST_XML_FILE = "src/test/resources/hazelcast/hazelcast.xml";

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

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
        LOGGER.info("For Infinispan, setting jgroups.tcp.address to: {}", ipAddressSet.first());
        System.setProperty("jgroups.tcp.address", ipAddressSet.first());

    }

    /**
     * Gets the single jvm metrics.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void getSingleJvmMetrics() throws IOException {
        final File zookeeperDirectory = folder.newFolder("zookeeperDirectory");
        final String[] args = {"singleJVMTestNL", "1", "32", "1000", "65536", "0", "localhost", "62181",
                zookeeperDirectory.getAbsolutePath()};

        LOGGER.info("Starting with args: {}", Arrays.toString(args));
        try {
            ConcurrentContextMetrics.main(args);
        } catch (final Exception exception) {
            LOGGER.error("Unexpected error", exception);
            fail("Metrics test failed");
        }
    }
}
