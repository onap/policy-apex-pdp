/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events using JMS.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexJmsConsumer extends ApexPluginsEventConsumer implements MessageListener {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexJmsConsumer.class);

    // The Apex and JMS parameters read from the parameter service
    private JmsCarrierTechnologyParameters jmsConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The connection to the JMS server
    private Connection connection;

    // The topic on which we receive events from JMS
    private Topic jmsIncomingTopic;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;

        this.name = consumerName;

        // Check and get the JMS Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof JmsCarrierTechnologyParameters)) {
            final String errorMessage = "specified consumer properties of type \""
                    + consumerParameters.getCarrierTechnologyParameters().getClass().getName()
                    + "\" are not applicable to a JMS consumer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        jmsConsumerProperties = (JmsCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        // Look up the JMS connection factory
        InitialContext jmsContext = null;
        ConnectionFactory connectionFactory = null;
        try {
            jmsContext = new InitialContext(jmsConsumerProperties.getJmsConsumerProperties());
            connectionFactory = (ConnectionFactory) jmsContext.lookup(jmsConsumerProperties.getConnectionFactory());

            // Check if we actually got a connection factory
            if (connectionFactory == null) {
                throw new IllegalArgumentException(
                        "JMS context lookup of \"" + jmsConsumerProperties.getConnectionFactory() + "\" returned null");
            }
        } catch (final Exception e) {
            final String errorMessage = "lookup of JMS connection factory  \""
                    + jmsConsumerProperties.getConnectionFactory() + "\" failed for JMS consumer properties \""
                    + jmsConsumerProperties.getJmsConsumerProperties() + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Lookup the topic on which we will receive events
        try {
            jmsIncomingTopic = (Topic) jmsContext.lookup(jmsConsumerProperties.getConsumerTopic());

            // Check if we actually got a topic
            if (jmsIncomingTopic == null) {
                throw new IllegalArgumentException(
                        "JMS context lookup of \"" + jmsConsumerProperties.getConsumerTopic() + "\" returned null");
            }
        } catch (final Exception e) {
            final String errorMessage = "lookup of JMS topic  \"" + jmsConsumerProperties.getConsumerTopic()
                    + "\" failed for JMS consumer properties \"" + jmsConsumerProperties.getJmsConsumerProperties()
                    + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Create and start a connection to the JMS server
        try {
            connection = connectionFactory.createConnection(jmsConsumerProperties.getSecurityPrincipal(),
                    jmsConsumerProperties.getSecurityCredentials());
            connection.start();
        } catch (final Exception e) {
            final String errorMessage = "connection to the JMS server failed for JMS properties \""
                    + jmsConsumerProperties.getJmsConsumerProperties() + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        // JMS session and message consumer for receiving messages
        try (final Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            // Create a message consumer for reception of messages and set this class as a message listener
            createMessageConsumer(jmsSession);
        } catch (final Exception e) {
            final String errorMessage = "failed to create a JMS session towards the JMS server for receiving messages";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }
        // Everything is now set up
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("event receiver " + this.getClass().getName() + ":" + this.name + " subscribed to JMS topic: "
                    + jmsConsumerProperties.getConsumerTopic());
        }
    }

    /**
     * The helper function to create a message consumer from a given JMS session.
     *
     * @param jmsSession a JMS session
     */
    private void createMessageConsumer(final Session jmsSession) {
        try (final MessageConsumer messageConsumer = jmsSession.createConsumer(jmsIncomingTopic)) {
            messageConsumer.setMessageListener(this);

            // The endless loop that receives events over JMS
            while (consumerThread.isAlive() && !stopOrderedFlag) {
                ThreadUtilities.sleep(jmsConsumerProperties.getConsumerWaitTime());
            }
        } catch (final Exception e) {
            final String errorMessage = "failed to create a JMS message consumer for receiving messages";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void onMessage(final Message jmsMessage) {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("event received for {} for forwarding to Apex engine : {} {}",
                        this.getClass().getName() + ":" + this.name, jmsMessage.getJMSMessageID(),
                        jmsMessage.getJMSType());
            }

            eventReceiver.receiveEvent(new Properties(), jmsMessage);
        } catch (final Exception e) {
            final String errorMessage = "failed to receive message from JMS";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(jmsConsumerProperties.getConsumerWaitTime());
        }

        // Close the connection to the JMS server
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (final Exception e) {
            final String errorMessage = "close of connection to the JMS server failed";
            LOGGER.warn(errorMessage, e);
        }
    }

}
