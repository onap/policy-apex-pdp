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

import java.io.File;

import javax.jms.JMSException;

import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.jboss.util.file.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class TestJMS2JMS {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestJMS2JMS.class);

    private static final long MAX_TEST_LENGTH = 10000;

    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    private static EmbeddedJMS server;

    @BeforeClass
    public static void setupEmbeddedJMSServer() {
        server = new EmbeddedJMS();
        server.setConfigResourcePath("jmsconfig/hornetq-configuration.xml");
        server.setJmsConfigResourcePath("jmsconfig/hornetq-jms.xml");

        try {
            server.start();
            server.getHornetQServer().getSecurityManager().addUser("guest", "IAmAGuest");
            server.getHornetQServer().getSecurityManager().addRole("guest", "guest");
        } catch (final Exception e) {
            throw new RuntimeException("Failed to start JMS server", e);
        }
    }

    @AfterClass
    public static void shutdownEmbeddedJMSServer() {
        try {
            server.stop();
        } catch (final Exception e) {
            LOGGER.warn("Failed to stop JMS server", e);
        }

        Files.delete(new File("src/test/resources/hornetq_journal/server.lock"));
        Files.delete(new File("src/test/resources/hornetq_journal"));
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
                new JMSEventSubscriber("jms/topic/apexOut", "localhost", "5445", "guest", "IAmAGuest");
        final JMSEventProducer producer = new JMSEventProducer("jms/topic/apexIn", "localhost", "5445", "guest",
                "IAmAGuest", EVENT_COUNT, sendObjects, EVENT_INTERVAL);

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
