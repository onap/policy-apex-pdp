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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.command.ActiveMQTopic;
import org.onap.policy.apex.apps.uservice.test.adapt.events.EventGenerator;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JMSEventProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JMSEventProducer.class);

    private final String topic;
    private final int eventCount;
    private final boolean sendObjects;
    private final long eventInterval;
    private long eventsSentCount = 0;

    private final Thread producerThread;
    private boolean sendEventsFlag = false;
    private boolean stopFlag = false;
    private final Connection connection;

    public JMSEventProducer(final String topic, final ConnectionFactory connectionFactory, final String username,
            final String password, final int eventCount, final boolean sendObjects, final long eventInterval)
            throws JMSException {
        this.topic = topic;
        this.eventCount = eventCount;
        this.sendObjects = sendObjects;
        this.eventInterval = eventInterval;
        connection = connectionFactory.createConnection(username, password);
        connection.start();

        producerThread = new Thread(this);
        producerThread.start();
    }

    @Override
    public void run() {
        final Topic jmsTopic = new ActiveMQTopic(topic);
        try (final Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final MessageProducer jmsProducer = jmsSession.createProducer(jmsTopic);) {

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

    public void sendEvents() {
        sendEventsFlag = true;
    }

    private void sendEventsToTopic(final Session jmsSession, final MessageProducer jmsProducer) throws JMSException {

        LOGGER.info("{} : sending events to JMS server, event count {}", this.getClass().getCanonicalName(),
                eventCount);

        for (int i = 0; i < eventCount; i++) {
            ThreadUtilities.sleep(eventInterval);

            Message jmsMessage = null;
            if (sendObjects) {
                jmsMessage = jmsSession.createObjectMessage(new TestPing());
            } else {
                jmsMessage = jmsSession.createTextMessage(EventGenerator.jsonEvent());
            }
            jmsProducer.send(jmsMessage);
            eventsSentCount++;
        }
        LOGGER.info("{} : completed, number of events sent", this.getClass().getCanonicalName(), eventsSentCount);
    }

    public long getEventsSentCount() {
        return eventsSentCount;
    }

    public void shutdown() {
        LOGGER.info("{} : stopping", this.getClass().getCanonicalName());
        stopFlag = true;

        while (producerThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }
        LOGGER.info("{} : stopped", this.getClass().getCanonicalName());
    }

}
