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

import java.util.Properties;

import javax.naming.Context;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Apex parameters for JMS as an event carrier technology.
 * <p>
 * The parameters for this plugin are:
 * <ol>
 * <li>initialContextFactory: JMS uses a naming {@link Context} object to look up the locations of JMS servers and JMS
 * topics. An Initial Context Factory is used to when creating a {@link Context} object that can be used for JMS
 * lookups. The value of this parameter is passed to the {@link Context} with the label
 * {@link Context#INITIAL_CONTEXT_FACTORY}. Its value must be the full canonical path to a class that implements the
 * {@code javax.naming.spi.InitialContextFactory} interface. The parameter defaults to the string value
 * {@code org.jboss.naming.remote.client.InitialContextFactory}.
 * <li>providerURL: The location of the server to use for naming context lookups. The value of this parameter is passed
 * to the {@link Context} with the label {@link Context#PROVIDER_URL}. Its value must be a URL that identifies the JMS
 * naming server. The parameter defaults to the string value {@code remote://localhost:4447}.
 * <li>securityPrincipal: The user name to use for JMS access. The value of this parameter is passed to the
 * {@link Context} with the label {@link Context#SECURITY_PRINCIPAL}. Its value must be the user name of a user defined
 * on the JMS server. The parameter defaults to the string value {@code userid}.
 * <li>securityCredentials:The password to use for JMS access. The value of this parameter is passed to the
 * {@link Context} with the label {@link Context#SECURITY_CREDENTIALS}. Its value must be the password of a suer defined
 * on the JMS server. The parameter defaults to the string value {@code password}.
 * <li>connectionFactory: JMS uses a {@link javax.jms.ConnectionFactory} instance to create connections towards a JMS
 * server. The connection factory to use is held in the JMS {@link Context} object. This parameter specifies the label
 * to use to look up the {@link javax.jms.ConnectionFactory} instance from the JMS {@link Context}.
 * <li>producerTopic: JMS uses a {@link javax.jms.Topic} instance to for sending and receiving messages. The topic to
 * use for sending events to JMS from an Apex producer is held in the JMS {@link Context} object. This parameter
 * specifies the label to use to look up the {@link javax.jms.Topic} instance in the JMS {@link Context} for the JMS
 * server. The topic must, of course, also be defined on the JMS server. The parameter defaults to the string value
 * {@code apex-out}.
 * <li>consumerTopic: The topic to use for receiving events from JMS in an Apex consumer is held in the JMS
 * {@link Context} object. This parameter specifies the label to use to look up the {@link javax.jms.Topic} instance in
 * the JMS {@link Context} for the JMS server. The topic must, of course, also be defined on the JMS server. The
 * parameter defaults to the string value {@code apex-in}.
 * <li>consumerWaitTime: The amount of milliseconds a JMS consumer should wait between checks of its thread execution
 * status. The parameter defaults to the long value {@code 100}.
 * <li>objectMessageSending: A flag that indicates whether Apex producers should send JMS messages as
 * {@link javax.jms.ObjectMessage} instances for java objects (value {@code true}) or as {@link javax.jms.TextMessage}
 * instances for strings (value {@code false}) . The parameter defaults to the boolean value {@code true}.
 * </ol>
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JMSCarrierTechnologyParameters extends CarrierTechnologyParameters {
    /** The label of this carrier technology. */
    public static final String JMS_CARRIER_TECHNOLOGY_LABEL = "JMS";

    /** The producer plugin class for the JMS carrier technology. */
    public static final String JMS_EVENT_PRODUCER_PLUGIN_CLASS = ApexJMSProducer.class.getCanonicalName();

    /** The consumer plugin class for the JMS carrier technology. */
    public static final String JMS_EVENT_CONSUMER_PLUGIN_CLASS = ApexJMSConsumer.class.getCanonicalName();

    // @formatter:off

    // Default parameter values
    private static final String  DEFAULT_CONNECTION_FACTORY        = "jms/RemoteConnectionFactory";
    private static final String  DEFAULT_INITIAL_CONTEXT_FACTORY   = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String  DEFAULT_PROVIDER_URL              = "remote://localhost:4447";
    private static final String  DEFAULT_SECURITY_PRINCIPAL        = "userid";
    private static final String  DEFAULT_SECURITY_CREDENTIALS      = "password";
    private static final String  DEFAULT_CONSUMER_TOPIC            = "apex-in";
    private static final String  DEFAULT_PRODUCER_TOPIC            = "apex-out";
    private static final int     DEFAULT_CONSUMER_WAIT_TIME        = 100;
    private static final boolean DEFAULT_TO_OBJECT_MESSAGE_SENDING = true;

    // Parameter property map tokens
    private static final String PROPERTY_INITIAL_CONTEXT_FACTORY  = Context.INITIAL_CONTEXT_FACTORY;
    private static final String PROPERTY_PROVIDER_URL             = Context.PROVIDER_URL;
    private static final String PROPERTY_SECURITY_PRINCIPAL       = Context.SECURITY_PRINCIPAL;
    private static final String PROPERTY_SECURITY_CREDENTIALS     = Context.SECURITY_CREDENTIALS;

    // JMS carrier parameters
    private String  connectionFactory     = DEFAULT_CONNECTION_FACTORY;
    private String  initialContextFactory = DEFAULT_INITIAL_CONTEXT_FACTORY;
    private String  providerUrl           = DEFAULT_PROVIDER_URL;
    private String  securityPrincipal     = DEFAULT_SECURITY_PRINCIPAL;
    private String  securityCredentials   = DEFAULT_SECURITY_CREDENTIALS;
    private String  producerTopic         = DEFAULT_PRODUCER_TOPIC;
    private String  consumerTopic         = DEFAULT_CONSUMER_TOPIC;
    private int     consumerWaitTime      = DEFAULT_CONSUMER_WAIT_TIME;
    private boolean objectMessageSending  = DEFAULT_TO_OBJECT_MESSAGE_SENDING;
    // @formatter:on

    /**
     * Constructor to create a jms carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public JMSCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the JMS carrier technology
        this.setLabel(JMS_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(JMS_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(JMS_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /**
     * Gets the JMS producer properties.
     *
     * @return the JMS producer properties
     */
    public Properties getJmsProducerProperties() {
        return getJmsProperties();
    }

    /**
     * Gets the jms consumer properties.
     *
     * @return the jms consumer properties
     */
    public Properties getJmsConsumerProperties() {
        return getJmsProperties();
    }

    /**
     * Gets the JMS consumer properties.
     *
     * @return the jms consumer properties
     */
    private Properties getJmsProperties() {
        final Properties jmsProperties = new Properties();

        jmsProperties.put(PROPERTY_INITIAL_CONTEXT_FACTORY, initialContextFactory);
        jmsProperties.put(PROPERTY_PROVIDER_URL, providerUrl);
        jmsProperties.put(PROPERTY_SECURITY_PRINCIPAL, securityPrincipal);
        jmsProperties.put(PROPERTY_SECURITY_CREDENTIALS, securityCredentials);

        return jmsProperties;
    }

    /**
     * Gets the connection factory.
     *
     * @return the connection factory
     */
    public String getConnectionFactory() {
        return connectionFactory;
    }

    /**
     * Gets the initial context factory.
     *
     * @return the initial context factory
     */
    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    /**
     * Gets the provider URL.
     *
     * @return the provider URL
     */
    public String getProviderUrl() {
        return providerUrl;
    }

    /**
     * Gets the security principal.
     *
     * @return the security principal
     */
    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    /**
     * Gets the security credentials.
     *
     * @return the security credentials
     */
    public String getSecurityCredentials() {
        return securityCredentials;
    }

    /**
     * Gets the producer topic.
     *
     * @return the producer topic
     */
    public String getProducerTopic() {
        return producerTopic;
    }

    /**
     * Gets the consumer topic.
     *
     * @return the consumer topic
     */
    public String getConsumerTopic() {
        return consumerTopic;
    }

    /**
     * Gets the consumer wait time.
     *
     * @return the consumer wait time
     */
    public long getConsumerWaitTime() {
        return consumerWaitTime;
    }

    /**
     * Sets the connection factory.
     *
     * @param connectionFactory the connection factory
     */
    public void setConnectionFactory(final String connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Sets the initial context factory.
     *
     * @param initialContextFactory the initial context factory
     */
    public void setInitialContextFactory(final String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    /**
     * Sets the provider URL.
     *
     * @param providerUrl the provider URL
     */
    public void setProviderUrl(final String providerUrl) {
        this.providerUrl = providerUrl;
    }

    /**
     * Sets the security principal.
     *
     * @param securityPrincipal the security principal
     */
    public void setSecurityPrincipal(final String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    /**
     * Sets the security credentials.
     *
     * @param securityCredentials the security credentials
     */
    public void setSecurityCredentials(final String securityCredentials) {
        this.securityCredentials = securityCredentials;
    }

    /**
     * Sets the producer topic.
     *
     * @param producerTopic the producer topic
     */
    public void setProducerTopic(final String producerTopic) {
        this.producerTopic = producerTopic;
    }

    /**
     * Sets the consumer topic.
     *
     * @param consumerTopic the consumer topic
     */
    public void setConsumerTopic(final String consumerTopic) {
        this.consumerTopic = consumerTopic;
    }

    /**
     * Sets the consumer wait time.
     *
     * @param consumerWaitTime the consumer wait time
     */
    public void setConsumerWaitTime(final int consumerWaitTime) {
        this.consumerWaitTime = consumerWaitTime;
    }

    /**
     * Checks if is object message sending.
     *
     * @return true, if checks if is object message sending
     */
    public boolean isObjectMessageSending() {
        return objectMessageSending;
    }

    /**
     * Sets the object message sending.
     *
     * @param objectMessageSending the object message sending
     */
    public void setObjectMessageSending(final boolean objectMessageSending) {
        this.objectMessageSending = objectMessageSending;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (initialContextFactory == null || initialContextFactory.trim().length() == 0) {
            result.setResult("initialContextFactory", ValidationStatus.INVALID,
                            "initialContextFactory must be specified as a string that is a class that implements the "
                                            + "interface org.jboss.naming.remote.client.InitialContextFactory");
        }

        if (providerUrl == null || providerUrl.trim().length() == 0) {
            result.setResult("providerUrl", ValidationStatus.INVALID,
                            "providerUrl must be specified as a URL string that specifies the location of "
                                            + "configuration information for the service provider to use "
                                            + "such as remote://localhost:4447");
        }

        if (securityPrincipal == null || securityPrincipal.trim().length() == 0) {
            result.setResult("securityPrincipal", ValidationStatus.INVALID,
                            "securityPrincipal must be specified the identity of the principal for authenticating "
                                            + "the caller to the service");
        }

        if (securityCredentials == null || securityCredentials.trim().length() == 0) {
            result.setResult("securityCredentials", ValidationStatus.INVALID,
                            "  securityCredentials must be specified as the credentials of the "
                                            + "principal for authenticating the caller to the service");
        }

        if (producerTopic == null || producerTopic.trim().length() == 0) {
            result.setResult("producerTopic", ValidationStatus.INVALID,
                            "  producerTopic must be a string that identifies the JMS topic "
                                            + "on which Apex will send events");
        }

        if (consumerTopic == null || consumerTopic.trim().length() == 0) {
            result.setResult("consumerTopic", ValidationStatus.INVALID,
                            "  consumerTopic must be a string that identifies the JMS topic "
                                            + "on which Apex will recieve events");
        }

        if (consumerWaitTime < 0) {
            result.setResult("consumerWaitTime", ValidationStatus.INVALID,
                            "[" + consumerWaitTime + "] invalid, must be specified as consumerWaitTime >= 0");
        }

        return result;
    }
}
