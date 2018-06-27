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
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.onap.policy.apex.apps.uservice.test.adapt.events.EventGenerator;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JMSEventProducer implements Runnable {
    private final String topic;
    private final int eventCount;
    private final boolean sendObjects;
    private final long eventInterval;
    private long eventsSentCount = 0;

    private final Thread producerThread;
    private boolean sendEventsFlag = false;
    private boolean stopFlag = false;
    private final Connection connection;

    public JMSEventProducer(final String topic, final String host, final String port, final String userName,
            final String password, final int eventCount, final boolean sendObjects, final long eventInterval)
            throws JMSException {
        this.topic = topic;
        this.eventCount = eventCount;
        this.sendObjects = sendObjects;
        this.eventInterval = eventInterval;

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("host", host);
        params.put("port", port);
        final TransportConfiguration config = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
        final ConnectionFactory factory =
                (ConnectionFactory) HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, config);
        connection = factory.createConnection(userName, password);
        connection.start();

        producerThread = new Thread(this);
        producerThread.start();
    }

    @Override
    public void run() {
        try {
            final Topic jmsTopic = HornetQJMSClient.createTopic(topic);
            final Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final MessageProducer jmsProducer = jmsSession.createProducer(jmsTopic);

            while (producerThread.isAlive() && !stopFlag) {
                ThreadUtilities.sleep(50);

                if (sendEventsFlag) {
                    sendEventsToTopic(jmsSession, jmsProducer);
                    sendEventsFlag = false;
                }
            }

            jmsProducer.close();
            jmsSession.close();
        } catch (final Exception e) {
            throw new ApexEventRuntimeException("JMS event consumption failed", e);
        }
    }

    public void sendEvents() {
        sendEventsFlag = true;
    }

    private void sendEventsToTopic(final Session jmsSession, final MessageProducer jmsProducer) throws JMSException {
        System.out.println(JMSEventProducer.class.getCanonicalName() + ": sending events to JMS server, event count "
                + eventCount);

        for (int i = 0; i < eventCount; i++) {
            System.out.println(JMSEventProducer.class.getCanonicalName() + ": waiting " + eventInterval
                    + " milliseconds before sending next event");
            ThreadUtilities.sleep(eventInterval);

            Message jmsMessage = null;
            if (sendObjects) {
                jmsMessage = jmsSession.createObjectMessage(new TestPing());
            } else {
                jmsMessage = jmsSession.createTextMessage(EventGenerator.jsonEvent());
            }
            jmsProducer.send(jmsMessage);
            eventsSentCount++;
            System.out.println(JMSEventProducer.class.getCanonicalName() + ": sent event " + jmsMessage.toString());
        }
        System.out.println(JMSEventProducer.class.getCanonicalName() + ": completed");
    }

    public long getEventsSentCount() {
        return eventsSentCount;
    }

    public void shutdown() {
        System.out.println(JMSEventProducer.class.getCanonicalName() + ": stopping");

        stopFlag = true;

        while (producerThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        System.out.println(JMSEventProducer.class.getCanonicalName() + ": stopped");
    }

    public static void main(final String[] args) throws JMSException {
        if (args.length != 8) {
            System.err.println(
                    "usage JMSEventProducer topic host port username password #events sendObjects eventInterval");
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[5]);
        } catch (final Exception e) {
            System.err.println(
                    "usage JMSEventProducer topic host port username password #events sendObjects eventInterval");
            e.printStackTrace();
            return;
        }

        long eventInterval = 0;
        try {
            eventInterval = Long.parseLong(args[7]);
        } catch (final Exception e) {
            System.err.println(
                    "usage JMSEventProducer topic host port username password #events sendObjects eventInterval");
            e.printStackTrace();
            return;
        }

        boolean sendObjects = false;
        if (args[6].equalsIgnoreCase("true")) {
            sendObjects = true;
        } else if (!args[3].equalsIgnoreCase("JSON")) {
            System.err.println(
                    "usage JMSEventProducer topic host port username password #events sendObjects eventInterval");
            return;
        }

        final JMSEventProducer producer = new JMSEventProducer(args[0], args[1], args[2], args[3], args[4], eventCount,
                sendObjects, eventInterval);

        producer.sendEvents();
        producer.shutdown();
    }
}
