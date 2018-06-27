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

package org.onap.policy.apex.apps.uservice.test.adapt.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JMSEventSubscriber implements Runnable {
    private final String topic;
    private long eventsReceivedCount = 0;

    private final Thread subscriberThread;
    private final Connection connection;

    public JMSEventSubscriber(final String topic, final String host, final String port, final String userName,
            final String password) throws JMSException {
        this.topic = topic;

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("host", host);
        params.put("port", port);
        final TransportConfiguration config = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        final ConnectionFactory factory =
                (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, config);
        connection = factory.createConnection(userName, password);
        connection.start();

        subscriberThread = new Thread(this);
        subscriberThread.start();
    }

    @Override
    public void run() {
        try {
            final Topic jmsTopic = HornetQJMSClient.createTopic(topic);
            final Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageConsumer jmsConsumer = jmsSession.createConsumer(jmsTopic);

            System.out.println(JMSEventSubscriber.class.getCanonicalName()
                    + ": receiving events from Kafka server on topic " + topic);

            while (subscriberThread.isAlive() && !subscriberThread.isInterrupted()) {
                try {
                    final Message message = jmsConsumer.receive(100);
                    if (message == null) {
                        continue;
                    }

                    if (message instanceof ObjectMessage) {
                        final TestPing testPing = (TestPing) ((ObjectMessage) message).getObject();
                        System.out.println("Received message: " + testPing.toString());
                        testPing.verify();
                    } else if (message instanceof TextMessage) {
                        final String textMessage = ((TextMessage) message).getText();
                        System.out.println("Received message: " + textMessage);
                    } else {
                        throw new ApexEventException("unknowm message \"" + message + "\" of type \""
                                + message.getClass().getCanonicalName() + "\" received");
                    }
                    eventsReceivedCount++;
                } catch (final Exception e) {
                    // Thread interrupted
                    break;
                }
            }

            jmsConsumer.close();
            jmsSession.close();
        } catch (final Exception e) {
            throw new ApexEventRuntimeException("JMS event consumption failed", e);
        }

        System.out.println(JMSEventSubscriber.class.getCanonicalName() + ": event reception completed");
    }

    public long getEventsReceivedCount() {
        return eventsReceivedCount;
    }

    public void shutdown() throws JMSException {
        subscriberThread.interrupt();

        while (subscriberThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        connection.close();
        System.out.println(JMSEventSubscriber.class.getCanonicalName() + ": stopped");
    }


    public static void main(final String[] args) throws JMSException {
        if (args.length != 5) {
            System.err.println("usage JMSEventSubscriber topic host port username password");
            return;
        }
        new JMSEventSubscriber(args[0], args[1], args[2], args[3], args[4]);
    }
}
