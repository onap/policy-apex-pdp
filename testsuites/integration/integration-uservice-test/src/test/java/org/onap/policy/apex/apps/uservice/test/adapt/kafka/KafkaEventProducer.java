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

package org.onap.policy.apex.apps.uservice.test.adapt.kafka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.onap.policy.apex.apps.uservice.test.adapt.events.EventGenerator;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KafkaEventProducer implements Runnable {
    private final String topic;
    private final String kafkaServerAddress;
    private final int eventCount;
    private final boolean xmlEvents;
    private final long eventInterval;
    private long eventsSentCount = 0;

    private Producer<String, String> producer;

    private final Thread producerThread;
    private boolean sendEventsFlag = false;
    private boolean stopFlag = false;

    public KafkaEventProducer(final String topic, final String kafkaServerAddress, final int eventCount,
            final boolean xmlEvents, final long eventInterval) {
        this.topic = topic;
        this.kafkaServerAddress = kafkaServerAddress;
        this.eventCount = eventCount;
        this.xmlEvents = xmlEvents;
        this.eventInterval = eventInterval;

        producerThread = new Thread(this);
        producerThread.start();
    }

    @Override
    public void run() {
        final Properties kafkaProducerProperties = new Properties();
        kafkaProducerProperties.put("bootstrap.servers", kafkaServerAddress);
        kafkaProducerProperties.put("acks", "all");
        kafkaProducerProperties.put("retries", 0);
        kafkaProducerProperties.put("batch.size", 16384);
        kafkaProducerProperties.put("linger.ms", 1);
        kafkaProducerProperties.put("buffer.memory", 33554432);
        kafkaProducerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<String, String>(kafkaProducerProperties);

        while (producerThread.isAlive() && !stopFlag) {
            ThreadUtilities.sleep(50);

            if (sendEventsFlag) {
                sendEventsToTopic();
                sendEventsFlag = false;
            }
        }

        producer.close(1000, TimeUnit.MILLISECONDS);
    }

    public void sendEvents() {
        sendEventsFlag = true;
    }

    private void sendEventsToTopic() {
        System.out.println(KafkaEventProducer.class.getCanonicalName() + ": sending events to Kafka server at "
                + kafkaServerAddress + ", event count " + eventCount + ", xmlEvents " + xmlEvents);

        for (int i = 0; i < eventCount; i++) {
            System.out.println(KafkaEventProducer.class.getCanonicalName() + ": waiting " + eventInterval
                    + " milliseconds before sending next event");
            ThreadUtilities.sleep(eventInterval);

            String eventString = null;
            if (xmlEvents) {
                eventString = EventGenerator.xmlEvent();
            } else {
                eventString = EventGenerator.jsonEvent();
            }
            producer.send(new ProducerRecord<String, String>(topic, "Event" + i + "Of" + eventCount, eventString));
            producer.flush();
            eventsSentCount++;
            System.out.println(KafkaEventProducer.class.getCanonicalName() + ": sent event " + eventString);
        }
        System.out.println(KafkaEventProducer.class.getCanonicalName() + ": completed");
    }

    public long getEventsSentCount() {
        return eventsSentCount;
    }

    public void shutdown() {
        System.out.println(KafkaEventProducer.class.getCanonicalName() + ": stopping");

        stopFlag = true;

        while (producerThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        System.out.println(KafkaEventProducer.class.getCanonicalName() + ": stopped");
    }

    public static void main(final String[] args) {
        if (args.length != 5) {
            System.err.println("usage KafkaEventProducer topic kafkaServerAddress #events XML|JSON eventInterval");
            return;
        }

        int eventCount = 0;
        try {
            eventCount = Integer.parseInt(args[2]);
        } catch (final Exception e) {
            System.err.println("usage KafkaEventProducer topic kafkaServerAddress #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        long eventInterval = 0;
        try {
            eventInterval = Long.parseLong(args[4]);
        } catch (final Exception e) {
            System.err.println("usage KafkaEventProducer topic kafkaServerAddress #events XML|JSON eventInterval");
            e.printStackTrace();
            return;
        }

        boolean xmlEvents = false;
        if (args[3].equalsIgnoreCase("XML")) {
            xmlEvents = true;
        } else if (!args[3].equalsIgnoreCase("JSON")) {
            System.err.println("usage KafkaEventProducer topic kafkaServerAddress #events XML|JSON eventInterval");
            return;
        }

        final KafkaEventProducer producer =
                new KafkaEventProducer(args[0], args[1], eventCount, xmlEvents, eventInterval);

        producer.sendEvents();
        producer.shutdown();
    }
}
