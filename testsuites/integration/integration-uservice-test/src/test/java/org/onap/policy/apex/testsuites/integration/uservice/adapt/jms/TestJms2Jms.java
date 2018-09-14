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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.jms;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestJms2Jms.
 */
public class TestJms2Jms {
    public static final String PORT = "5445";
    public static final String HOST = "localhost";
    public static final String JMS_TOPIC_APEX_IN = "jms/topic/apexIn";
    public static final String JMS_TOPIC_APEX_OUT = "jms/topic/apexOut";

    private static final int SLEEP_TIME = 1500;
    private static final String GROUP_ROLE = "guests";
    private static final String PACKAGE_NAME = "org.onap.policy.apex.testsuites.integration.uservice.adapt.jms";
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "IAmAGuest";
    private static final String URL = "tcp://" + HOST + ":" + PORT;

    private static final String DATA_PARENT_DIR = Paths.get("target", "activemq-data").toString();

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestJms2Jms.class);

    private static final long MAX_TEST_LENGTH = 10000;
    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    private static BrokerService broker;

    public static ActiveMQConnectionFactory connectionFactory;


    /**
     * Setup embedded jms server.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setupEmbeddedJmsServer() throws Exception {
        final ArrayList<BrokerPlugin> plugins = new ArrayList<BrokerPlugin>();
        final BrokerPlugin authenticationPlugin = getAuthenticationBrokerPlugin();
        plugins.add(authenticationPlugin);

        broker = new BrokerService();
        broker.setUseJmx(false);
        broker.setPersistent(false);
        broker.addConnector(URL);
        broker.setDeleteAllMessagesOnStartup(true);
        broker.setPlugins(plugins.toArray(new BrokerPlugin[0]));
        broker.setDataDirectory(DATA_PARENT_DIR);
        broker.start();
        broker.waitUntilStarted();
        connectionFactory = new ActiveMQConnectionFactory(URL);
        connectionFactory.setTrustedPackages(Arrays.asList(PACKAGE_NAME));
    }

    /**
     * Gets the authentication broker plugin.
     *
     * @return the authentication broker plugin
     */
    private static BrokerPlugin getAuthenticationBrokerPlugin() {
        final List<AuthenticationUser> users = new ArrayList<AuthenticationUser>();
        users.add(new AuthenticationUser(USERNAME, PASSWORD, GROUP_ROLE));
        final SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);
        return authenticationPlugin;
    }

    /**
     * Shutdown embedded jms server.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterClass
    public static void shutdownEmbeddedJmsServer() throws IOException {
        try {
            if (broker != null) {
                broker.stop();
            }
        } catch (final Exception e) {
            LOGGER.warn("Failed to stop JMS server", e);
        }

    }

    /**
     * Test jms object events.
     *
     * @throws ApexException the apex exception
     * @throws JMSException the JMS exception
     */
    @Test
    public void testJmsObjectEvents() throws ApexException, JMSException {
        final String[] args = { "src/test/resources/prodcons/JMS2JMSObjectEvent.json" };
        testJmsEvents(args, true);
    }

    /**
     * Test jms json events.
     *
     * @throws ApexException the apex exception
     * @throws JMSException the JMS exception
     */
    @Test
    public void testJmsJsonEvents() throws ApexException, JMSException {
        final String[] args = { "src/test/resources/prodcons/JMS2JMSJsonEvent.json" };
        testJmsEvents(args, false);
    }

    /**
     * Test jms events.
     *
     * @param args the args
     * @param sendObjects the send objects
     * @throws ApexException the apex exception
     * @throws JMSException the JMS exception
     */
    private void testJmsEvents(final String[] args, final Boolean sendObjects) throws ApexException, JMSException {
        final JmsEventSubscriber subscriber =
                new JmsEventSubscriber(JMS_TOPIC_APEX_OUT, connectionFactory, USERNAME, PASSWORD);
        final JmsEventProducer producer = new JmsEventProducer(JMS_TOPIC_APEX_IN, connectionFactory, USERNAME, PASSWORD,
                EVENT_COUNT, sendObjects, EVENT_INTERVAL);

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(3000);

        producer.sendEvents();

        final long testStartTime = System.currentTimeMillis();

        while (isTimedOut(testStartTime) && subscriber.getEventsReceivedCount() < EVENT_COUNT) {
            ThreadUtilities.sleep(EVENT_INTERVAL);
        }

        ThreadUtilities.sleep(SLEEP_TIME);
        apexMain.shutdown();
        subscriber.shutdown();
        producer.shutdown();
        ThreadUtilities.sleep(SLEEP_TIME);

        assertEquals(EVENT_COUNT, producer.getEventsSentCount());
        assertEquals(producer.getEventsSentCount(), subscriber.getEventsReceivedCount());

    }

    /**
     * Checks if is timed out.
     *
     * @param testStartTime the test start time
     * @return true, if is timed out
     */
    private boolean isTimedOut(final long testStartTime) {
        return System.currentTimeMillis() < testStartTime + MAX_TEST_LENGTH;
    }
}
