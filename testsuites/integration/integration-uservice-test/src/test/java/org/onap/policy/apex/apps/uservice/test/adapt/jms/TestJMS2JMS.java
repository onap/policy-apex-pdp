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

package org.onap.policy.apex.apps.uservice.test.adapt.jms;

import static org.junit.Assert.assertTrue;

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

public class TestJMS2JMS {
    private static final String GROUP_ROLE = "guests";
    private static final String PACKAGE_NAME = "org.onap.policy.apex.apps.uservice.test.adapt.jms";
    private static final String USERNAME = "guest";
    private static final String PASSWORD = "IAmAGuest";
    private static final String JMS_TOPIC_APEX_IN = "jms/topic/apexIn";
    private static final String JMS_TOPIC_APEX_OUT = "jms/topic/apexOut";
    private static final String URL = "tcp://localhost:5445";

    private static final String DATA_PARENT_DIR = Paths.get("target", "activemq-data").toString();

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestJMS2JMS.class);

    private static final long MAX_TEST_LENGTH = 10000;
    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    private static BrokerService broker;

    public static ActiveMQConnectionFactory connectionFactory;


    @BeforeClass
    public static void setupEmbeddedJMSServer() throws Exception {
        final ArrayList<BrokerPlugin> plugins = new ArrayList<BrokerPlugin>();
        final BrokerPlugin authenticationPlugin = getAuthenticationBrokerPlugin();
        if (authenticationPlugin != null) {
            plugins.add(authenticationPlugin);
        }

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

    private static BrokerPlugin getAuthenticationBrokerPlugin() {
        final List<AuthenticationUser> users = new ArrayList<AuthenticationUser>();
        users.add(new AuthenticationUser(USERNAME, PASSWORD, GROUP_ROLE));
        final SimpleAuthenticationPlugin authenticationPlugin = new SimpleAuthenticationPlugin(users);
        return authenticationPlugin;
    }

    @AfterClass
    public static void shutdownEmbeddedJMSServer() throws IOException {
        try {
            if (broker != null) {
                broker.stop();
            }
        } catch (final Exception e) {
            LOGGER.warn("Failed to stop JMS server", e);
        }

    }

    @Test
    public void testJMSObjectEvents() throws ApexException, JMSException {
        final String[] args = {"src/test/resources/prodcons/JMS2JMSObjectEvent.json"};
        testJMSEvents(args, true);
    }

    @Test
    public void testJMSJsonEvents() throws ApexException, JMSException {
        final String[] args = {"src/test/resources/prodcons/JMS2JMSJsonEvent.json"};
        testJMSEvents(args, false);
    }

    private void testJMSEvents(final String[] args, final Boolean sendObjects) throws ApexException, JMSException {
        final JMSEventSubscriber subscriber =
                new JMSEventSubscriber(JMS_TOPIC_APEX_OUT, connectionFactory, USERNAME, PASSWORD);
        final JMSEventProducer producer = new JMSEventProducer(JMS_TOPIC_APEX_IN, connectionFactory, USERNAME, PASSWORD,
                EVENT_COUNT, sendObjects, EVENT_INTERVAL);

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(3000);

        producer.sendEvents();

        final long testStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < testStartTime + MAX_TEST_LENGTH
                && subscriber.getEventsReceivedCount() < EVENT_COUNT) {
            ThreadUtilities.sleep(EVENT_INTERVAL);
        }

        ThreadUtilities.sleep(1000);

        System.out.println("sent event count: " + producer.getEventsSentCount());
        System.out.println("received event count: " + subscriber.getEventsReceivedCount());
        assertTrue(subscriber.getEventsReceivedCount() == producer.getEventsSentCount());

        apexMain.shutdown();
        subscriber.shutdown();
        producer.shutdown();
        ThreadUtilities.sleep(1000);
    }
}
