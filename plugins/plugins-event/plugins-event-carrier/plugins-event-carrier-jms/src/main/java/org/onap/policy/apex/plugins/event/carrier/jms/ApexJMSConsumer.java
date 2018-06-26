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

package org.onap.policy.apex.plugins.event.carrier.jms;

import java.util.EnumMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events using JMS.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexJMSConsumer implements MessageListener, ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexJMSConsumer.class);

    // The Apex and JMS parameters read from the parameter service
    private JMSCarrierTechnologyParameters jmsConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The consumer thread and stopping flag
    private Thread consumerThread;
    private boolean stopOrderedFlag = false;

    // The connection to the JMS server
    private Connection connection;

    // The topic on which we receive events from JMS
    private Topic jmsIncomingTopic;

    // The name for this consumer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;

        this.name = consumerName;

        // Check and get the JMS Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof JMSCarrierTechnologyParameters)) {
            final String errorMessage = "specified consumer properties of type \""
                    + consumerParameters.getCarrierTechnologyParameters().getClass().getCanonicalName()
                    + "\" are not applicable to a JMS consumer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        jmsConsumerProperties = (JMSCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        // Look up the JMS connection factory
        InitialContext jmsContext = null;
        ConnectionFactory connectionFactory = null;
        try {
            jmsContext = new InitialContext(jmsConsumerProperties.getJMSConsumerProperties());
            connectionFactory = (ConnectionFactory) jmsContext.lookup(jmsConsumerProperties.getConnectionFactory());

            // Check if we actually got a connection factory
            if (connectionFactory == null) {
                throw new NullPointerException(
                        "JMS context lookup of \"" + jmsConsumerProperties.getConnectionFactory() + "\" returned null");
            }
        } catch (final Exception e) {
            final String errorMessage = "lookup of JMS connection factory  \""
                    + jmsConsumerProperties.getConnectionFactory() + "\" failed for JMS consumer properties \""
                    + jmsConsumerProperties.getJMSConsumerProperties() + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Lookup the topic on which we will receive events
        try {
            jmsIncomingTopic = (Topic) jmsContext.lookup(jmsConsumerProperties.getConsumerTopic());

            // Check if we actually got a topic
            if (jmsIncomingTopic == null) {
                throw new NullPointerException(
                        "JMS context lookup of \"" + jmsConsumerProperties.getConsumerTopic() + "\" returned null");
            }
        } catch (final Exception e) {
            final String errorMessage = "lookup of JMS topic  \"" + jmsConsumerProperties.getConsumerTopic()
                    + "\" failed for JMS consumer properties \"" + jmsConsumerProperties.getJMSConsumerProperties()
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
                    + jmsConsumerProperties.getJMSConsumerProperties() + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#start()
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + ":" + this.name;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // JMS session and message consumer for receiving messages
        Session jmsSession = null;
        MessageConsumer messageConsumer = null;

        // Create a session to the JMS server
        try {
            jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (final Exception e) {
            final String errorMessage = "failed to create a JMS session towards the JMS server for receiving messages";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        } finally {
            if(jmsSession != null) {
                try {
                    jmsSession.close();
                } catch (Exception e) {
                    final String errorMessage = "failed to close the JMS session for receiving messages";
                    LOGGER.warn(errorMessage, e);
                }                
            }
        }     

        // Create a message consumer for reception of messages and set this class as a message listener
        try {
            messageConsumer = jmsSession.createConsumer(jmsIncomingTopic);
            messageConsumer.setMessageListener(this);
        } catch (final Exception e) {
            final String errorMessage = "failed to create a JMS message consumer for receiving messages";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        } finally {
            if(messageConsumer != null) {
                try {
                    messageConsumer.close();
                } catch (Exception e) {
                    final String errorMessage = "failed to close the JMS message consumer for receiving messages";
                    LOGGER.warn(errorMessage, e);
                }
            }
        }

        // Everything is now set up
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("event receiver " + this.getClass().getName() + ":" + this.name + " subscribed to JMS topic: "
                    + jmsConsumerProperties.getConsumerTopic());
        }

        // The endless loop that receives events over JMS
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            ThreadUtilities.sleep(jmsConsumerProperties.getConsumerWaitTime());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(final Message jmsMessage) {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("event received for {} for forwarding to Apex engine : {} {}",
                        this.getClass().getName() + ":" + this.name, jmsMessage.getJMSMessageID(),
                        jmsMessage.getJMSType());
            }

            eventReceiver.receiveEvent(jmsMessage);
        } catch (final Exception e) {
            final String errorMessage = "failed to receive message from JMS";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#stop()
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
