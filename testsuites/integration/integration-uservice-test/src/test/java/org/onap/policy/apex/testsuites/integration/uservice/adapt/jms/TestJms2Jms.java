/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.jms.JMSException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TestJms2Jms.
 */
class TestJms2Jms {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestJms2Jms.class);

    protected static final String SERVER_NAME = "JmsTestServer";
    protected static final String PORT = "5445";
    protected static final String HOST = "localhost";
    protected static final String JMS_TOPIC_APEX_IN = "jms/topic/apexIn";
    protected static final String JMS_TOPIC_APEX_OUT = "jms/topic/apexOut";
    protected static final String SERVER_URI = "tcp://" + HOST + ":" + PORT;

    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    // Embedded JMS server for testing
    private static JmsServerRunner jmsServerRunner;

    /**
     * Setup embedded JMS server.
     *
     * @throws Exception the exception
     */
    @BeforeAll
    static void setupEmbeddedJmsServer() throws Exception {
        jmsServerRunner = new JmsServerRunner(SERVER_NAME, SERVER_URI);

        await().pollDelay(3L, TimeUnit.SECONDS).until(() -> new AtomicBoolean(true).get());
    }

    /**
     * Clear relative file root environment variable.
     */
    @BeforeEach
    void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    /**
     * Shutdown embedded jms server.
     */
    @AfterAll
    static void shutdownEmbeddedJmsServer() {
        try {
            if (jmsServerRunner != null) {
                jmsServerRunner.stop();
            }
        } catch (final Exception e) {
            LOGGER.warn("Failed to stop JMS server", e);
        }

    }

    /**
     * Test jms object events.
     *
     * @throws ApexException the apex exception
     * @throws JMSException  the JMS exception
     */
    @Test
    void testJmsObjectEvents() throws ApexException, JMSException {
        final String[] args = {
            "-rfr", "target", "-p", "target/examples/config/JMS/JMS2JMSObjectEvent.json"
        };
        testJmsEvents(args, true);
    }

    /**
     * Test jms json events.
     *
     * @throws ApexException the apex exception
     * @throws JMSException  the JMS exception
     */
    @Test
    void testJmsJsonEvents() throws ApexException, JMSException {
        final String[] args = {
            "-rfr", "target", "-p", "target/examples/config/JMS/JMS2JMSJsonEvent.json"
        };
        testJmsEvents(args, false);
    }

    /**
     * Test jms events.
     *
     * @param args        the args
     * @param sendObjects the send objects
     * @throws ApexException the apex exception
     * @throws JMSException  the JMS exception
     */
    private void testJmsEvents(final String[] args, final Boolean sendObjects) throws ApexException, JMSException {
        final JmsEventSubscriber subscriber =
            new JmsEventSubscriber(JMS_TOPIC_APEX_OUT, new ActiveMQConnectionFactory(SERVER_URI), null, null);

        final JmsEventProducer producer =
            new JmsEventProducer(JMS_TOPIC_APEX_IN, new ActiveMQConnectionFactory(SERVER_URI), null, null,
                EVENT_COUNT, sendObjects, EVENT_INTERVAL);

        final ApexMain apexMain = new ApexMain(args);

        await().atMost(3L, TimeUnit.SECONDS).until(apexMain::isAlive);

        producer.sendEvents();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> producer.getEventsSentCount() >= EVENT_COUNT - 1);
        await().atMost(10L, TimeUnit.SECONDS).until(() -> subscriber.getEventsReceivedCount() >= EVENT_COUNT - 1);

        apexMain.shutdown();
        subscriber.shutdown();
        producer.shutdown();

        assertEquals(EVENT_COUNT, producer.getEventsSentCount());
        assertEquals(producer.getEventsSentCount(), subscriber.getEventsReceivedCount());
    }
}