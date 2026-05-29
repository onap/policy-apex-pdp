/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2026 OpenInfra Foundation Europe. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.kafka;

import java.time.Duration;
import java.util.Properties;
import lombok.Getter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.testsuites.integration.uservice.adapt.events.EventGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class KafkaEventProducer.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KafkaEventProducer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventProducer.class);

    private final String topic;
    private final String bootstrapServers;
    private final int eventCount;
    private final boolean xmlEvents;
    private final long eventInterval;
    @Getter
    private long eventsSentCount = 0;

    private final Thread producerThread;
    private boolean sendEventsFlag = false;
    private volatile boolean stopFlag = false;

    /**
     * Instantiates a new kafka event producer.
     *
     * @param topic            the topic
     * @param bootstrapServers the kafka bootstrap servers
     * @param eventCount       the event count
     * @param xmlEvents        the xml events
     * @param eventInterval    the event interval
     */
    public KafkaEventProducer(final String topic, final String bootstrapServers,
                              final int eventCount, final boolean xmlEvents, final long eventInterval) {
        this.topic = topic;
        this.bootstrapServers = bootstrapServers;
        this.eventCount = eventCount;
        this.xmlEvents = xmlEvents;
        this.eventInterval = eventInterval;

        producerThread = new Thread(this);
        producerThread.start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        final Producer<String, String> producer = new KafkaProducer<>(props);

        while (producerThread.isAlive() && !stopFlag) {
            ThreadUtilities.sleep(50);

            if (sendEventsFlag) {
                sendEventsToTopic(producer);
                sendEventsFlag = false;
            }
        }

        producer.close(Duration.ofMillis(1000));
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
        LOGGER.debug("{} : sending events to Kafka server, event count {}, xmlEvents {}",
            KafkaEventProducer.class.getName(), eventCount, xmlEvents);

        for (int i = 0; i < eventCount; i++) {
            LOGGER.debug("{} : waiting {} milliseconds before sending next event", KafkaEventProducer.class.getName(),
                eventInterval);
            ThreadUtilities.sleep(eventInterval);

            String eventString;
            if (xmlEvents) {
                eventString = EventGenerator.xmlEvent();
            } else {
                eventString = EventGenerator.jsonEvent();
            }
            producer.send(new ProducerRecord<>(topic, "Event" + i + "Of" + eventCount, eventString));
            producer.flush();
            eventsSentCount++;
            LOGGER.debug("****** Sent event No. {} ******\n{}", eventsSentCount, eventString);
        }
        LOGGER.debug("{}: completed", KafkaEventProducer.class.getName());
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        LOGGER.debug("{} : stopping", KafkaEventProducer.class.getName());

        stopFlag = true;

        while (producerThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        LOGGER.debug("{} : stopped", KafkaEventProducer.class.getName());
    }

    public boolean isAlive() {
        return producerThread.isAlive();
    }
}
