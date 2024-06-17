/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019-2021, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexJmsProducerTest {

    private static final String PRODUCER_NAME = "TestApexJmsProducer";
    private ApexJmsConsumer apexJmsConsumer;
    private EventHandlerParameters producerParameters;
    private ApexJmsProducer apexJmsProducer;
    private JmsCarrierTechnologyParameters jmsCarrierTechnologyParameters;
    private static final long DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT = 1000;
    private final Random random = new Random();
    private final PrintStream out = System.out;

    /**
     * Set up testing.
     *
     */
    @BeforeEach
    public void setUp() {
        apexJmsConsumer = new ApexJmsConsumer();
        producerParameters = new EventHandlerParameters();
        apexJmsProducer = Mockito.spy(new ApexJmsProducer());
    }

    @AfterEach
    public void tearDown() {
        // restore system.out
        System.setOut(out);
    }

    @Test
    public void testInitWithNonJmsCarrierTechnologyParameters() {
        producerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {
        });
        assertThatThrownBy(() -> apexJmsProducer.init(PRODUCER_NAME, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitWithJmsCarrierTechnologyParameters() {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        assertThatThrownBy(() -> apexJmsProducer.init(PRODUCER_NAME, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitWithoutConnectionFactory() throws NamingException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        InitialContext context = Mockito.mock(InitialContext.class);
        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(null).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());

        assertThatThrownBy(() -> apexJmsProducer.init(producerName, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitWithoutTopic() throws NamingException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(null).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());

        assertThatThrownBy(() -> apexJmsProducer.init(producerName, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitConnectionCreateError() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doThrow(JMSException.class).when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());

        assertThatThrownBy(() -> apexJmsProducer.init(producerName, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitConnectionStartError() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doThrow(JMSException.class).when(connection).start();

        assertThatThrownBy(() -> apexJmsProducer.init(producerName, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitSessionCreateError() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doThrow(JMSException.class).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);

        assertThatThrownBy(() -> apexJmsProducer.init(producerName, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInitProducerCreateError() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doThrow(JMSException.class).when(session).createProducer(topic);
        assertThatThrownBy(() -> apexJmsProducer.init(producerName, producerParameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void testInit() throws NamingException, JMSException {
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);
        final String producerName = RandomStringUtils.randomAlphabetic(5);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        assertThatCode(() -> apexJmsProducer.init(producerName, producerParameters))
            .doesNotThrowAnyException();
    }

    @Test
    public void testGetName() {
        assertNull(apexJmsProducer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexJmsProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
            apexJmsConsumer, apexJmsProducer);
        apexJmsProducer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        final PeeredReference actual = apexJmsProducer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR);
        assertSame(peeredReference, actual);
    }

    @Test
    public void testSendEventNotSerializable() {
        producerParameters.setCarrierTechnologyParameters(new JmsCarrierTechnologyParameters());
        Object object = new Object();
        final long executionId = random.nextLong();
        assertThatThrownBy(() -> apexJmsProducer.sendEvent(executionId, null, PRODUCER_NAME, object))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testSendEventRemoveCache() {
        producerParameters.setCarrierTechnologyParameters(new JmsCarrierTechnologyParameters());
        final SynchronousEventCache synchronousEventCache =
            Mockito.spy(new SynchronousEventCache(EventHandlerPeeredMode.SYNCHRONOUS,
                apexJmsConsumer, apexJmsProducer, DEFAULT_SYNCHRONOUS_EVENT_TIMEOUT));
        apexJmsProducer.setPeeredReference(EventHandlerPeeredMode.SYNCHRONOUS,
            synchronousEventCache);

        Object object = new Object();
        final long executionId = random.nextLong();

        assertThatThrownBy(() -> apexJmsProducer.sendEvent(executionId, null, PRODUCER_NAME, object))
            .isInstanceOf(ApexEventRuntimeException.class);
        Mockito.verify(synchronousEventCache, Mockito.times(1)).removeCachedEventToApexIfExists(executionId);
    }

    @Test
    public void testSendEventCreateObjectMessageError() throws ApexEventException, NamingException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare sendEvent

        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
            "testSource", "testTarget", "");
        Mockito.doThrow(JMSException.class).when(session).createObjectMessage(apexEvent);

        final long executionId = random.nextLong();


        assertThatThrownBy(() -> apexJmsProducer.sendEvent(executionId, null, PRODUCER_NAME, apexEvent))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testSendEventCreateObjectMessageSendError() throws ApexEventException, NamingException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare sendEvent
        final Message message = Mockito.mock(ObjectMessage.class);
        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
            "testSource", "testTarget", "");
        Mockito.doReturn(message).when(session).createObjectMessage(apexEvent);
        Mockito.doThrow(JMSException.class).when(messageProducer).send(message);

        final long executionId = random.nextLong();


        assertThatThrownBy(
            () -> apexJmsProducer
                .sendEvent(executionId, null, PRODUCER_NAME, apexEvent)
        )
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testSendEventCreateObjectMessageSend() throws ApexEventException, NamingException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare sendEvent
        final Message message = Mockito.mock(ObjectMessage.class);
        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
            "testSource", "testTarget", "");
        Mockito.doReturn(message).when(session).createObjectMessage(apexEvent);
        Mockito.doNothing().when(messageProducer).send(message);

        final long executionId = random.nextLong();


        assertThatCode(
            () -> apexJmsProducer
                .sendEvent(executionId, null, PRODUCER_NAME, apexEvent)
        )
            .doesNotThrowAnyException();
    }

    @Test
    public void testSendEventCreateTextMessageError() throws ApexEventException, NamingException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);
        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare sendEvent
        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
            "testSource", "testTarget", "");

        Mockito.doThrow(JMSException.class).when(session).createTextMessage(apexEvent.toString());

        final long executionId = random.nextLong();

        assertThatThrownBy(
            () -> apexJmsProducer
                .sendEvent(executionId, null, PRODUCER_NAME, apexEvent)
        )
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testSendEventCreateTextMessageSendError() throws ApexEventException, NamingException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare sendEvent
        final Message message = Mockito.mock(TextMessage.class);
        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
            "testSource", "testTarget", "");
        Mockito.doReturn(message).when(session).createTextMessage(apexEvent.toString());
        Mockito.doThrow(JMSException.class).when(messageProducer).send(message);

        final long executionId = random.nextLong();


        assertThatThrownBy(
            () -> apexJmsProducer
                .sendEvent(executionId, null, PRODUCER_NAME, apexEvent)
        )
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    public void testSendEventCreateTextMessageSend() throws ApexEventException, NamingException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare sendEvent
        final Message message = Mockito.mock(TextMessage.class);
        ApexEvent apexEvent = new ApexEvent("testEvent", "testVersion", "testNameSpace",
            "testSource", "testTarget", "");
        Mockito.doReturn(message).when(session).createTextMessage(apexEvent.toString());
        Mockito.doNothing().when(messageProducer).send(message);

        final long executionId = random.nextLong();


        assertThatCode(
            () -> apexJmsProducer.sendEvent(executionId, null, PRODUCER_NAME, apexEvent)
        )
            .doesNotThrowAnyException();
    }

    @Test
    public void testStopProducerException() throws NamingException, ApexEventException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare stop mock
        Mockito.doThrow(JMSException.class).when(messageProducer).close();

        // Prepare system.out
        final ByteArrayOutputStream testBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testBuffer));

        // do the test
        assertThatCode(apexJmsProducer::stop).doesNotThrowAnyException();
        assertThat(testBuffer.toString()).contains("failed to close JMS message producer");
    }

    @Test
    public void testStopCloseSessionException() throws NamingException, ApexEventException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare stop mocks
        Mockito.doNothing().when(messageProducer).close();
        Mockito.doThrow(JMSException.class).when(session).close();

        // Prepare system.out
        final ByteArrayOutputStream testBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testBuffer));

        // do the test
        assertThatCode(apexJmsProducer::stop).doesNotThrowAnyException();
        assertThat(testBuffer.toString()).contains("failed to close the JMS session");
    }

    @Test
    public void testStopCloseConnectionException() throws NamingException, ApexEventException, JMSException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare stop mocks
        Mockito.doNothing().when(messageProducer).close();
        Mockito.doNothing().when(session).close();
        Mockito.doThrow(JMSException.class).when(connection).close();

        // Prepare system.out
        final ByteArrayOutputStream testBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testBuffer));

        // do the test
        assertThatCode(apexJmsProducer::stop).doesNotThrowAnyException();
        assertThat(testBuffer.toString()).contains("close of connection");
    }

    @Test
    public void testStop() throws NamingException, JMSException, ApexEventException {
        // Prepare ApexJmsProducer
        jmsCarrierTechnologyParameters = new JmsCarrierTechnologyParameters();
        jmsCarrierTechnologyParameters.setObjectMessageSending(false);
        producerParameters.setCarrierTechnologyParameters(jmsCarrierTechnologyParameters);

        final InitialContext context = Mockito.mock(InitialContext.class);
        final ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        final Topic topic = Mockito.mock(Topic.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Session session = Mockito.mock(Session.class);
        final MessageProducer messageProducer = Mockito.mock(MessageProducer.class);

        Mockito.doReturn(context).when(apexJmsProducer).getInitialContext();
        Mockito.doReturn(connectionFactory).when(context).lookup(jmsCarrierTechnologyParameters.getConnectionFactory());
        Mockito.doReturn(topic).when(context).lookup(jmsCarrierTechnologyParameters.getProducerTopic());
        Mockito.doReturn(connection)
            .when(connectionFactory)
            .createConnection(jmsCarrierTechnologyParameters.getSecurityPrincipal(),
                jmsCarrierTechnologyParameters.getSecurityCredentials());
        Mockito.doNothing().when(connection).start();
        Mockito.doReturn(session).when(connection).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Mockito.doReturn(messageProducer).when(session).createProducer(topic);

        apexJmsProducer.init(RandomStringUtils.random(5), producerParameters);

        // Prepare stop mocks
        Mockito.doNothing().when(messageProducer).close();
        Mockito.doNothing().when(session).close();
        Mockito.doNothing().when(connection).close();

        // do the test
        assertThatCode(apexJmsProducer::stop).doesNotThrowAnyException();
    }
}