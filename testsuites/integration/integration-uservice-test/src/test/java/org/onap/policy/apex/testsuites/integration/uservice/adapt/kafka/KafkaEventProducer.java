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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.kafka;

import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.testsuites.integration.uservice.adapt.events.EventGenerator;

/**
 * The Class KafkaEventProducer.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KafkaEventProducer implements Runnable {
    private final String topic;
    private final SharedKafkaTestResource sharedKafkaTestResource;
    private final int eventCount;
    private final boolean xmlEvents;
    private final long eventInterval;
    private long eventsSentCount = 0;

    private final Thread producerThread;
    private boolean sendEventsFlag = false;
    private boolean stopFlag = false;

    /**
     * Instantiates a new kafka event producer.
     *
     * @param topic the topic
     * @param sharedKafkaTestResource the kafka server address
     * @param eventCount the event count
     * @param xmlEvents the xml events
     * @param eventInterval the event interval
     */
    public KafkaEventProducer(final String topic, final SharedKafkaTestResource sharedKafkaTestResource,
                    final int eventCount, final boolean xmlEvents, final long eventInterval) {
        this.topic = topic;
        this.sharedKafkaTestResource = sharedKafkaTestResource;
        this.eventCount = eventCount;
        this.xmlEvents = xmlEvents;
        this.eventInterval = eventInterval;

        producerThread = new Thread(this);
        producerThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        final Producer<String, String> producer = sharedKafkaTestResource.getKafkaTestUtils()
                        .getKafkaProducer(StringSerializer.class, StringSerializer.class);

        while (producerThread.isAlive() && !stopFlag) {
            ThreadUtilities.sleep(50);

            if (sendEventsFlag) {
                sendEventsToTopic(producer);
                sendEventsFlag = false;
            }
        }

        producer.close(1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Send events.
     */
    public void sendEvents() {
        sendEventsFlag = true;
    }

    /**
     * Send events to topic.
     *
     * @param producer the producer
     */
    private void sendEventsToTopic(final Producer<String, String> producer) {
        System.out.println(KafkaEventProducer.class.getCanonicalName()
                        + ": sending events to Kafka server , event count " + eventCount + ", xmlEvents " + xmlEvents);

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

    /**
     * Gets the events sent count.
     *
     * @return the events sent count
     */
    public long getEventsSentCount() {
        return eventsSentCount;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        System.out.println(KafkaEventProducer.class.getCanonicalName() + ": stopping");

        stopFlag = true;

        while (producerThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        System.out.println(KafkaEventProducer.class.getCanonicalName() + ": stopped");
    }
}
