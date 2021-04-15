/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.Before;
import org.junit.Test;
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

public class ApexJmsConsumerTest {

    ApexJmsConsumer apexJmsConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventProducer apexJmsProducer = null;
    JmsCarrierTechnologyParameters jmsCarrierTechnologyParameters = null;

    /**
     * Set up testing.
     */
    @Before
    public void setUp() {
        apexJmsConsumer = Mockito.spy(new ApexJmsConsumer());
        consumerParameters = new EventHandlerParameters();
        apexJmsProducer = new ApexJmsProducer();
    }

    @Test
    public void testInitWithNonJmsCarrierTechnologyParameters() {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {
        });
        assertThatThrownBy(
            () -> apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver)
        )
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitWithJmsCarrierTechnologyParameters() {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        assertThatThrownBy(
            () -> apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver)
        )
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitNoConnectionFactory() throws NamingException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        Mockito.doReturn(null).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(
            () -> apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver)
        )
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitNoConsumerTopic() throws NamingException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(null).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(
            () -> apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver)
        )
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitNoConnection() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doThrow(JMSException.class)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());

        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(
            () -> apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver)
        )
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitConnectionError() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doThrow(JMSException.class).when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        assertThatThrownBy(
            () -> apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver)
        )
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInit() throws NamingException, JMSException, ApexEventException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver);

        Mockito.verify(connection, Mockito.times(1)).start();
    }

    @Test
    public void testStart() {
        assertThatCode(apexJmsConsumer::start).doesNotThrowAnyException();
    }

    @Test
    public void testGetName() {
        assertNull(apexJmsConsumer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexJmsConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
            apexJmsConsumer, apexJmsProducer);
        apexJmsConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexJmsConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testRun() {
        assertThatThrownBy(apexJmsConsumer::run).isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testOnMessageUninitialized() {
        Message jmsMessage = null;
        assertThatThrownBy(() -> apexJmsConsumer.onMessage(jmsMessage))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testOnMessage() throws JMSException, NamingException, ApexEventException {
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
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver);

        final Message message = Mockito.mock(Message.class);

        apexJmsConsumer.onMessage(message);
        Mockito
            .verify(incomingEventReceiver, Mockito.times(1))
            .receiveEvent(ArgumentMatchers.any(Properties.class), ArgumentMatchers.eq(message));
    }

    @Test
    public void testConnectionError() throws NamingException, JMSException, ApexEventException {
        // prepare ApexJmsConsumer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver);

        Mockito.doThrow(JMSException.class).when(connection).close();

        // do tests
        apexJmsConsumer.start();
        assertThatCode(() -> apexJmsConsumer.stop()).doesNotThrowAnyException();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void testStop() throws NamingException, JMSException, ApexEventException {
        // prepare ApexJmsConsumer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getConsumerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(context).when(apexJmsConsumer).getInitialContext();

        apexJmsConsumer.init("TestApexJmsConsumer", consumerParameters, incomingEventReceiver);

        Mockito.doNothing().when(connection).close();

        apexJmsConsumer.start();

        // do tests
        apexJmsConsumer.stop();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void testStopNoJmsProperties() {
        assertThatThrownBy(apexJmsConsumer::stop).isInstanceOf(NullPointerException.class);
    }
}
