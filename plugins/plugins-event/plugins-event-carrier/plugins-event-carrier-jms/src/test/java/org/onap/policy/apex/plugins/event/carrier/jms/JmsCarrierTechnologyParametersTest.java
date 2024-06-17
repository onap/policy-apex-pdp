/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019, 2021, 2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import javax.naming.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.parameters.ParameterRuntimeException;
import org.onap.policy.common.parameters.ValidationResult;

class JmsCarrierTechnologyParametersTest {

    JmsCarrierTechnologyParameters jmsCarrierTechnologyParameters = null;
    ValidationResult result = null;

    static final String JMS_CARRIER_TECHNOLOGY_LABEL = "JMS";

    static final String JMS_EVENT_PRODUCER_PLUGIN_CLASS = ApexJmsProducer.class.getName();

    static final String JMS_EVENT_CONSUMER_PLUGIN_CLASS = ApexJmsConsumer.class.getName();

    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_INITIAL_CTXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String DEFAULT_CONSUMER_TOPIC = "apex-in";
    private static final String DEFAULT_PRODUCER_TOPIC = "apex-out";
    private static final int DEFAULT_CONSUMER_WAIT_TIME = 100;
    private static final boolean DEFAULT_TO_OBJECT_MSG_SENDING = true;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @BeforeEach
    void setUp() throws Exception {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
    }

    @Test
    void testValidate() {
        result = jmsCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertFalse(result.getStatus().isValid());

        jmsCarrierTechnologyParameters.setProviderUrl("DUMMYURL");
        jmsCarrierTechnologyParameters.setSecurityPrincipal("DUMMYPRINCIPAL");
        jmsCarrierTechnologyParameters.setSecurityCredentials("DUMMYCREDENTIALS");

        result = jmsCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertTrue(result.getStatus().isValid());
    }

    @Test
    void testJmsCarrierTechnologyParameters() {
        assertNotNull(jmsCarrierTechnologyParameters);
    }

    @Test
    void testGetJmsProducerProperties() {
        Properties producerProperties = jmsCarrierTechnologyParameters.getJmsProducerProperties();
        assertNotNull(producerProperties);

        assertNull(producerProperties.get(Context.PROVIDER_URL));
        assertNull(producerProperties.get(Context.SECURITY_PRINCIPAL));
        assertNull(producerProperties.get(Context.SECURITY_CREDENTIALS));

        jmsCarrierTechnologyParameters.setProviderUrl("DUMMYURL");
        jmsCarrierTechnologyParameters.setSecurityPrincipal("DUMMYPRINCIPAL");
        jmsCarrierTechnologyParameters.setSecurityCredentials("DUMMYCREDENTIALS");

        producerProperties = jmsCarrierTechnologyParameters.getJmsProducerProperties();

        assertEquals("DUMMYURL", producerProperties.get(Context.PROVIDER_URL));
        assertEquals("DUMMYPRINCIPAL", producerProperties.get(Context.SECURITY_PRINCIPAL));
        assertEquals("DUMMYCREDENTIALS", producerProperties.get(Context.SECURITY_CREDENTIALS));

        jmsCarrierTechnologyParameters.setProviderUrl(null);
        jmsCarrierTechnologyParameters.setSecurityPrincipal(null);
        jmsCarrierTechnologyParameters.setSecurityCredentials(null);

        producerProperties = jmsCarrierTechnologyParameters.getJmsProducerProperties();

        assertNull(producerProperties.get(Context.PROVIDER_URL));
        assertNull(producerProperties.get(Context.SECURITY_PRINCIPAL));
        assertNull(producerProperties.get(Context.SECURITY_CREDENTIALS));
    }

    @Test
    void testGetJmsConsumerProperties() {
        Properties consumerProperties = jmsCarrierTechnologyParameters.getJmsConsumerProperties();
        assertNotNull(consumerProperties);
        assertNull(consumerProperties.get(Context.SECURITY_CREDENTIALS));

        jmsCarrierTechnologyParameters.setSecurityCredentials("DUMMY");
        consumerProperties = jmsCarrierTechnologyParameters.getJmsProducerProperties();
        assertEquals("DUMMY", consumerProperties.get(Context.SECURITY_CREDENTIALS));
    }

    @Test
    void testEqualityOfJmsConsumerAndProducerProperties() {
        assertEquals(jmsCarrierTechnologyParameters.getJmsProducerProperties(),
            jmsCarrierTechnologyParameters.getJmsConsumerProperties());
    }

    @Test
    void testGetConnectionFactory() {
        assertEquals(DEFAULT_CONNECTION_FACTORY, jmsCarrierTechnologyParameters.getConnectionFactory());
    }

    @Test
    void testSetConnectionFactory() {
        jmsCarrierTechnologyParameters.setConnectionFactory("QueueConnectionFactory");
        assertNotEquals(DEFAULT_CONNECTION_FACTORY, jmsCarrierTechnologyParameters.getConnectionFactory());
    }

    @Test
    void testSetConsumerTopic() {
        assertEquals(DEFAULT_CONSUMER_TOPIC, jmsCarrierTechnologyParameters.getConsumerTopic());
        jmsCarrierTechnologyParameters.setConsumerTopic(null);
        result = jmsCarrierTechnologyParameters.validate();
        assertFalse(result.getStatus().isValid());
    }

    @Test
    void testSetConsumerWaitTime() {
        assertEquals(DEFAULT_CONSUMER_WAIT_TIME, jmsCarrierTechnologyParameters.getConsumerWaitTime());
        jmsCarrierTechnologyParameters.setConsumerWaitTime(-1);
        assertNotEquals(DEFAULT_CONSUMER_WAIT_TIME, jmsCarrierTechnologyParameters.getConsumerWaitTime());
    }

    @Test
    void testSetEventConsumerPluginClass() {
        assertEquals(JMS_EVENT_CONSUMER_PLUGIN_CLASS, jmsCarrierTechnologyParameters.getEventConsumerPluginClass());
        jmsCarrierTechnologyParameters.setEventConsumerPluginClass("TestEventConsumerPluginClass");
        assertNotEquals(JMS_EVENT_CONSUMER_PLUGIN_CLASS, jmsCarrierTechnologyParameters.getEventConsumerPluginClass());
    }

    @Test
    void testSetEventProducerPluginClass() {
        assertEquals(JMS_EVENT_PRODUCER_PLUGIN_CLASS, jmsCarrierTechnologyParameters.getEventProducerPluginClass());
        jmsCarrierTechnologyParameters.setEventProducerPluginClass("TestEventProducerPluginClass");
        assertNotEquals(JMS_EVENT_PRODUCER_PLUGIN_CLASS, jmsCarrierTechnologyParameters.getEventProducerPluginClass());
    }

    @Test
    void testSetLabel() {
        assertEquals(JMS_CARRIER_TECHNOLOGY_LABEL, jmsCarrierTechnologyParameters.getLabel());
        jmsCarrierTechnologyParameters.setLabel("TestLable");
        assertNotEquals(JMS_CARRIER_TECHNOLOGY_LABEL, jmsCarrierTechnologyParameters.getLabel());

    }

    @Test
    void testSetObjectMessageSending() {
        assertTrue(jmsCarrierTechnologyParameters.isObjectMessageSending());
        jmsCarrierTechnologyParameters.setObjectMessageSending(!DEFAULT_TO_OBJECT_MSG_SENDING);
        assertFalse(jmsCarrierTechnologyParameters.isObjectMessageSending());
    }

    @Test
    void testSetProducerTopic() {
        assertEquals(DEFAULT_PRODUCER_TOPIC, jmsCarrierTechnologyParameters.getProducerTopic());
        jmsCarrierTechnologyParameters.setProducerTopic("");
        result = jmsCarrierTechnologyParameters.validate();
        assertFalse(result.getStatus().isValid());
    }

    @Test
    void testSetProviderUrl() {
        assertNull(jmsCarrierTechnologyParameters.getProviderUrl());
        jmsCarrierTechnologyParameters.setProviderUrl(null);
        result = jmsCarrierTechnologyParameters.validate();
        assertFalse(result.getStatus().isValid());
    }

    @Test
    void testSetSecurityCredentials() {
        assertNull(jmsCarrierTechnologyParameters.getSecurityCredentials());
        jmsCarrierTechnologyParameters.setSecurityCredentials("");
        result = jmsCarrierTechnologyParameters.validate();
        assertFalse(result.getStatus().isValid());
    }

    @Test
    void testSetSecurityPrincipal() {
        assertNull(jmsCarrierTechnologyParameters.getSecurityPrincipal());
        jmsCarrierTechnologyParameters.setSecurityPrincipal(null);
        result = jmsCarrierTechnologyParameters.validate();
        assertFalse(result.getStatus().isValid());
    }

    @Test
    void testSetInitialContextFactory() {

        assertEquals(DEFAULT_INITIAL_CTXT_FACTORY, jmsCarrierTechnologyParameters.getInitialContextFactory());

        jmsCarrierTechnologyParameters.setInitialContextFactory(null);
        result = jmsCarrierTechnologyParameters.validate();
        assertFalse(result.getStatus().isValid());

        jmsCarrierTechnologyParameters.setInitialContextFactory("TestInitialContextFactory");
        assertNotEquals(DEFAULT_INITIAL_CTXT_FACTORY, jmsCarrierTechnologyParameters.getInitialContextFactory());
    }

    @Test
    void testSetName() {
        assertThrows(ParameterRuntimeException.class, () -> jmsCarrierTechnologyParameters.setName("TestName"));
    }
}
