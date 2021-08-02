/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019,2021 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.jms;

import java.util.Properties;
import javax.naming.Context;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;

/**
 * Apex parameters for JMS as an event carrier technology.
 *
 * <p>The parameters for this plugin are:
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
@Getter
@Setter
public class JmsCarrierTechnologyParameters extends CarrierTechnologyParameters {
    /** The label of this carrier technology. */
    public static final String JMS_CARRIER_TECHNOLOGY_LABEL = "JMS";

    /** The producer plugin class for the JMS carrier technology. */
    public static final String JMS_EVENT_PRODUCER_PLUGIN_CLASS = ApexJmsProducer.class.getName();

    /** The consumer plugin class for the JMS carrier technology. */
    public static final String JMS_EVENT_CONSUMER_PLUGIN_CLASS = ApexJmsConsumer.class.getName();

    // @formatter:off

    // Default parameter values
    private static final String  DEFAULT_CONNECTION_FACTORY    = "jms/RemoteConnectionFactory";
    private static final String  DEFAULT_INITIAL_CTXT_FACTORY  = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String  DEFAULT_PROVIDER_URL          = null;
    private static final String  DEFAULT_SECURITY_PRINCIPAL    = null;
    private static final String  DEFAULT_SECURITY_CREDENTIALS  = null;
    private static final String  DEFAULT_CONSUMER_TOPIC        = "apex-in";
    private static final String  DEFAULT_PRODUCER_TOPIC        = "apex-out";
    private static final int     DEFAULT_CONSUMER_WAIT_TIME    = 100;
    private static final boolean DEFAULT_TO_OBJECT_MSG_SENDING = true;

    // Parameter property map tokens
    private static final String PROPERTY_INITIAL_CONTEXT_FACTORY  = Context.INITIAL_CONTEXT_FACTORY;
    private static final String PROPERTY_PROVIDER_URL             = Context.PROVIDER_URL;
    private static final String PROPERTY_SECURITY_PRINCIPAL       = Context.SECURITY_PRINCIPAL;
    private static final String PROPERTY_SECURITY_CREDENTIALS     = Context.SECURITY_CREDENTIALS;

    // JMS carrier parameters
    private String  connectionFactory     = DEFAULT_CONNECTION_FACTORY;
    @NotNull @NotBlank
    private String  initialContextFactory = DEFAULT_INITIAL_CTXT_FACTORY;
    @NotNull @NotBlank
    private String  providerUrl           = DEFAULT_PROVIDER_URL;
    @NotNull @NotBlank
    private String  securityPrincipal     = DEFAULT_SECURITY_PRINCIPAL;
    @NotNull @NotBlank
    private String  securityCredentials   = DEFAULT_SECURITY_CREDENTIALS;
    @NotNull @NotBlank
    private String  producerTopic         = DEFAULT_PRODUCER_TOPIC;
    @NotNull @NotBlank
    private String  consumerTopic         = DEFAULT_CONSUMER_TOPIC;
    @Min(0)
    private int     consumerWaitTime      = DEFAULT_CONSUMER_WAIT_TIME;
    private boolean objectMessageSending  = DEFAULT_TO_OBJECT_MSG_SENDING;
    // @formatter:on

    /**
     * Constructor to create a jms carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public JmsCarrierTechnologyParameters() {
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

        if (providerUrl != null) {
            jmsProperties.put(PROPERTY_PROVIDER_URL, providerUrl);
        }

        if (securityPrincipal != null) {
            jmsProperties.put(PROPERTY_SECURITY_PRINCIPAL, securityPrincipal);
        }

        if (securityCredentials != null) {
            jmsProperties.put(PROPERTY_SECURITY_CREDENTIALS, securityCredentials);
        }

        return jmsProperties;
    }
}
