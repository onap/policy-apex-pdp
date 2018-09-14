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

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events using JMS.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexJmsProducer implements ApexEventProducer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexJmsProducer.class);

    // Recurring string constants
    private static final String COULD_NOT_SEND_PREFIX = "could not send event \"";
    private static final String FOR_PRODUCER_TAG = "\" for producer (";
    private static final String JMS_MESSAGE_PRODUCER_TAG = "\" on JMS message producer ";

    // The JMS parameters read from the parameter service
    private JmsCarrierTechnologyParameters jmsProducerProperties;

    // The connection to the JMS server
    private Connection connection;

    // The JMS session on which we will send events
    private Session jmsSession;

    // The producer on which we will send events
    private MessageProducer messageProducer;

    // The name for this producer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#init(java.lang.String,
     * org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters)
     */
    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
                    throws ApexEventException {
        this.name = producerName;

        // Check and get the JMS Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof JmsCarrierTechnologyParameters)) {
            final String errorMessage = "specified producer properties are not applicable to a JMS producer ("
                            + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        jmsProducerProperties = (JmsCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        // Look up the JMS connection factory
        InitialContext jmsContext = null;
        ConnectionFactory connectionFactory = null;
        try {
            jmsContext = new InitialContext(jmsProducerProperties.getJmsProducerProperties());
            connectionFactory = (ConnectionFactory) jmsContext.lookup(jmsProducerProperties.getConnectionFactory());

            // Check if we actually got a connection factory
            if (connectionFactory == null) {
                throw new NullPointerException("JMS context lookup of \"" + jmsProducerProperties.getConnectionFactory()
                                + "\" returned null for producer (" + this.name + ")");
            }
        } catch (final Exception e) {
            final String errorMessage = "lookup of JMS connection factory  \""
                            + jmsProducerProperties.getConnectionFactory() + "\" failed for JMS producer properties \""
                            + jmsProducerProperties.getJmsConsumerProperties() + FOR_PRODUCER_TAG + this.name + ")";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Lookup the topic on which we will send events
        Topic jmsOutgoingTopic;
        try {
            jmsOutgoingTopic = (Topic) jmsContext.lookup(jmsProducerProperties.getProducerTopic());

            // Check if we actually got a topic
            if (jmsOutgoingTopic == null) {
                throw new NullPointerException("JMS context lookup of \"" + jmsProducerProperties.getProducerTopic()
                                + "\" returned null for producer (" + this.name + ")");
            }
        } catch (final Exception e) {
            final String errorMessage = "lookup of JMS topic  \"" + jmsProducerProperties.getProducerTopic()
                            + "\" failed for JMS producer properties \""
                            + jmsProducerProperties.getJmsProducerProperties() + FOR_PRODUCER_TAG + this.name + ")";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Create and start a connection to the JMS server
        try {
            connection = connectionFactory.createConnection(jmsProducerProperties.getSecurityPrincipal(),
                            jmsProducerProperties.getSecurityCredentials());
            connection.start();
        } catch (final Exception e) {
            final String errorMessage = "connection to JMS server failed for JMS properties \""
                            + jmsProducerProperties.getJmsConsumerProperties() + FOR_PRODUCER_TAG + this.name + ")";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Create a JMS session for sending events
        try {
            jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (final Exception e) {
            final String errorMessage = "creation of session to JMS server failed for JMS properties \""
                            + jmsProducerProperties.getJmsConsumerProperties() + FOR_PRODUCER_TAG + this.name + ")";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }

        // Create a JMS message producer for sending events
        try {
            messageProducer = jmsSession.createProducer(jmsOutgoingTopic);
        } catch (final Exception e) {
            final String errorMessage = "creation of producer for sending events "
                            + "to JMS server failed for JMS properties \""
                            + jmsProducerProperties.getJmsConsumerProperties() + "\"";
            LOGGER.warn(errorMessage, e);
            throw new ApexEventException(errorMessage, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#setPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#sendEvent(long, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void sendEvent(final long executionId, final String eventname, final Object eventObject) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache = (SynchronousEventCache) peerReferenceMap
                        .get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        // Check if the object to be sent is serializable
        if (!Serializable.class.isAssignableFrom(eventObject.getClass())) {
            final String errorMessage = COULD_NOT_SEND_PREFIX + eventname + JMS_MESSAGE_PRODUCER_TAG + this.name
                            + ", object of type \"" + eventObject.getClass().getCanonicalName()
                            + "\" is not serializable";
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }

        // The JMS message to send is constructed using the JMS session
        Message jmsMessage = null;

        // Check the type of JMS message to send
        if (jmsProducerProperties.isObjectMessageSending()) {
            // We should send a JMS Object Message
            try {
                jmsMessage = jmsSession.createObjectMessage((Serializable) eventObject);
            } catch (final Exception e) {
                final String errorMessage = COULD_NOT_SEND_PREFIX + eventname + JMS_MESSAGE_PRODUCER_TAG
                                + this.name + ", could not create JMS Object Message for object \"" + eventObject;
                LOGGER.warn(errorMessage, e);
                throw new ApexEventRuntimeException(errorMessage);
            }
        } else {
            // We should send a JMS Text Message
            try {
                jmsMessage = jmsSession.createTextMessage(eventObject.toString());
            } catch (final Exception e) {
                final String errorMessage = COULD_NOT_SEND_PREFIX + eventname + JMS_MESSAGE_PRODUCER_TAG
                                + this.name + ", could not create JMS Text Message for object \"" + eventObject;
                LOGGER.warn(errorMessage, e);
                throw new ApexEventRuntimeException(errorMessage);
            }
        }

        try {
            messageProducer.send(jmsMessage);
        } catch (final Exception e) {
            final String errorMessage = COULD_NOT_SEND_PREFIX + eventname + JMS_MESSAGE_PRODUCER_TAG + this.name
                            + ", send failed for object \"" + eventObject;
            LOGGER.warn(errorMessage, e);
            throw new ApexEventRuntimeException(errorMessage);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.producer.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        // Close the message producer
        try {
            messageProducer.close();
        } catch (final Exception e) {
            final String errorMessage = "failed to close JMS message producer " + this.name + " for sending messages";
            LOGGER.warn(errorMessage, e);
        }

        // Close the session
        try {
            jmsSession.close();
        } catch (final Exception e) {
            final String errorMessage = "failed to close the JMS session for  " + this.name + " for sending messages";
            LOGGER.warn(errorMessage, e);
        }

        // Close the connection to the JMS server
        try {
            connection.close();
        } catch (final Exception e) {
            final String errorMessage = "close of connection to the JMS server for  " + this.name + " failed";
            LOGGER.warn(errorMessage, e);
        }
    }
}
