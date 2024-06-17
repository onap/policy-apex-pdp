/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019-2021, 2023-2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Topic;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class ApexJmsConsumerTest {

    private static final String CONSUMER_NAME = "TestApexJmsConsumer";
    private ApexJmsConsumer apexJmsConsumer = null;
    private EventHandlerParameters consumerParameters = null;
    private ApexEventReceiver incomingEventReceiver = null;
    private ApexEventProducer apexJmsProducer = null;
    private JmsCarrierTechnologyParameters jmsCarrierTechnologyParameters = null;

    AutoCloseable closeable;

    /**
     * Set up testing.
     */
    @BeforeEach
    void setUp() {
        apexJmsConsumer = Mockito.spy(new ApexJmsConsumer());
        consumerParameters = new EventHandlerParameters();
        apexJmsProducer = new ApexJmsProducer();
    }

    @Test
    void testInitWithNonJmsCarrierTechnologyParameters() {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {
        });
        assertThatThrownBy(() -> apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void testInitWithJmsCarrierTechnologyParameters() {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        assertThatThrownBy(() -> apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void testInitNoConnectionFactory() throws NamingException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        Mockito.doReturn(null).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(() -> apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void testInitNoConsumerTopic() throws NamingException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(null).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(() -> apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void testInitNoConnection() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        closeable = Mockito.doThrow(JMSException.class).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());

        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(() -> apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void testInitConnectionError() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        closeable = Mockito.doReturn(connection).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doThrow(JMSException.class).when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(() -> apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    void testInit() throws NamingException, JMSException, ApexEventException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        closeable = Mockito.doReturn(connection).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);

        Mockito.verify(connection, Mockito.times(1)).start();
    }

    @Test
    void testStart() {
        assertThatCode(apexJmsConsumer::start).doesNotThrowAnyException();
    }

    @Test
    void testGetName() {
        assertNull(apexJmsConsumer.getName());
    }

    @Test
    void testGetPeeredReference() {
        assertNull(apexJmsConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
            apexJmsConsumer, apexJmsProducer);
        apexJmsConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexJmsConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    void testRun() {
        assertThatThrownBy(apexJmsConsumer::run).isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void testOnMessageUninitialized() {
        Message jmsMessage = null;
        assertThatThrownBy(() -> apexJmsConsumer.onMessage(jmsMessage))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void testOnMessage() throws JMSException, NamingException, ApexEventException {
        // prepare ApexJmsConsumer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        incomingEventReceiver = Mockito.mock(ApexEventReceiver.class);


        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        closeable = Mockito.doReturn(connection).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);

        final Message message = Mockito.mock(Message.class);

        apexJmsConsumer.onMessage(message);
        Mockito
            .verify(incomingEventReceiver, Mockito.times(1))
            .receiveEvent(ArgumentMatchers.any(Properties.class), ArgumentMatchers.eq(message));
    }

    @Test
    void testConnectionError() throws NamingException, JMSException, ApexEventException {
        // prepare ApexJmsConsumer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        closeable = Mockito.doReturn(connection).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);

        Mockito.doThrow(JMSException.class).when(connection).close();

        // do tests
        apexJmsConsumer.start();
        assertThatCode(() -> apexJmsConsumer.stop()).doesNotThrowAnyException();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    void testStop() throws NamingException, JMSException, ApexEventException {
        // prepare ApexJmsConsumer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        closeable = Mockito.doReturn(connection).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);

        Mockito.doNothing().when(connection).close();

        apexJmsConsumer.start();

        // do tests
        apexJmsConsumer.stop();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    void testStopNoJmsProperties() {
        assertThatThrownBy(apexJmsConsumer::stop).isInstanceOf(NullPointerException.class);
    }

    @AfterEach
    void after() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }
}
