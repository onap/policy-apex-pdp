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
import jakarta.jms.MessageConsumer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import lombok.Getter;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.testsuites.integration.common.testclasses.PingTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class JmsEventSubscriber.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JmsEventSubscriber implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsEventSubscriber.class);

    private final String topic;
    @Getter
    private long eventsReceivedCount = 0;

    private final Thread subscriberThread;
    private final Connection connection;

    /**
     * Instantiates a new jms event subscriber.
     *
     * @param topic the topic
     * @param connectionFactory the connection factory
     * @param username the username
     * @param password the password
     * @throws JMSException the JMS exception
     */
    public JmsEventSubscriber(final String topic, final ConnectionFactory connectionFactory, final String username,
            final String password) throws JMSException {
        this.topic = topic;
        connection = connectionFactory.createConnection(username, password);
        connection.start();

        subscriberThread = new Thread(this);
        subscriberThread.start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        final Topic jmsTopic = new ActiveMQTopic(topic);
        try (final Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final MessageConsumer jmsConsumer = jmsSession.createConsumer(jmsTopic)) {

            while (subscriberThread.isAlive() && !subscriberThread.isInterrupted()) {
                try {
                    final Message message = jmsConsumer.receive(100);
                    if (message == null) {
                        continue;
                    }

                    if (message instanceof ObjectMessage) {
                        final PingTestClass testPing = (PingTestClass) ((ObjectMessage) message).getObject();
                        testPing.verify();
                    } else if (message instanceof TextMessage) {
                        ((TextMessage) message).getText();
                    } else {
                        throw new ApexEventException("unknown message \"" + message + "\" of type \""
                                + message.getClass().getName() + "\" received");
                    }
                    eventsReceivedCount++;
                } catch (final Exception e) {
                    if (!(e.getCause() instanceof InterruptedException)) {
                        throw new ApexEventRuntimeException("JMS message reception failed", e);
                    }
                }
            }

        } catch (final Exception e) {
            throw new ApexEventRuntimeException("JMS event consumption failed", e);
        }

        LOGGER.info("{} : event reception completed, {} events received", this.getClass().getName(),
                eventsReceivedCount);
    }

    /**
     * Shutdown.
     *
     * @throws JMSException the JMS exception
     */
    public void shutdown() throws JMSException {
        LOGGER.info("{} : stopping...", this.getClass().getName());

        subscriberThread.interrupt();

        while (subscriberThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        connection.close();
        LOGGER.info("{} : stopped", this.getClass().getName());
    }

}
