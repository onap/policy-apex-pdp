/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.Topic;
import lombok.Getter;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.testsuites.integration.common.testclasses.PingTestClass;
import org.onap.policy.apex.testsuites.integration.uservice.adapt.events.EventGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JmsEventProducer.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JmsEventProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsEventProducer.class);

    private final String topic;
    private final int eventCount;
    private final boolean sendObjects;
    private final long eventInterval;
    @Getter
    private long eventsSentCount = 0;

    private final Thread producerThread;
    private boolean sendEventsFlag = false;
    private boolean stopFlag = false;
    private final Connection connection;

    /**
     * Instantiates a new jms event producer.
     *
     * @param topic             the topic
     * @param connectionFactory the connection factory
     * @param username          the username
     * @param password          the password
     * @param eventCount        the event count
     * @param sendObjects       the send objects
     * @param eventInterval     the event interval
     * @throws JMSException the JMS exception
     */
    public JmsEventProducer(final String topic, final ConnectionFactory connectionFactory, final String username,
                            final String password, final int eventCount, final boolean sendObjects,
                            final long eventInterval) throws JMSException {
        this.topic = topic;
        this.eventCount = eventCount;
        this.sendObjects = sendObjects;
        this.eventInterval = eventInterval;
        connection = connectionFactory.createConnection(username, password);
        connection.start();

        producerThread = new Thread(this);
        producerThread.start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        final Topic jmsTopic = new ActiveMQTopic(topic);
        try (final Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             final MessageProducer jmsProducer = jmsSession.createProducer(jmsTopic)) {

            while (producerThread.isAlive() && !stopFlag) {
                ThreadUtilities.sleep(50);

                if (sendEventsFlag) {
                    sendEventsToTopic(jmsSession, jmsProducer);
                    sendEventsFlag = false;
                }
            }

        } catch (final Exception e) {
            throw new ApexEventRuntimeException("JMS event consumption failed", e);
        }
    }

    /**
     * Send events.
     */
    public void sendEvents() {
        sendEventsFlag = true;
    }

    /**
     * Send events to topic.
     *
     * @param jmsSession  the jms session
     * @param jmsProducer the jms producer
     * @throws JMSException the JMS exception
     */
    private void sendEventsToTopic(final Session jmsSession, final MessageProducer jmsProducer) throws JMSException {

        LOGGER.debug("{} : sending events to JMS server, event count {}", this.getClass().getName(), eventCount);

        for (int i = 0; i < eventCount; i++) {
            ThreadUtilities.sleep(eventInterval);

            Message jmsMessage;
            if (sendObjects) {
                final PingTestClass pingTestClass = new PingTestClass();
                pingTestClass.setId(i);
                jmsMessage = jmsSession.createObjectMessage(pingTestClass);
            } else {
                jmsMessage = jmsSession.createTextMessage(EventGenerator.jsonEvent());
            }
            jmsProducer.send(jmsMessage);
            eventsSentCount++;
        }
        LOGGER.debug("{} : completed, number of events sent: {}", this.getClass().getName(), eventsSentCount);
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        LOGGER.debug("{} : stopping", this.getClass().getName());
        stopFlag = true;

        while (producerThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }
        LOGGER.debug("{} : stopped", this.getClass().getName());
    }

}
