/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2024 Nordix Foundation.
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

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class KafkaEventSubscriber.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KafkaEventSubscriber implements Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventSubscriber.class);

    private static final Duration POLL_DURATION = Duration.ofMillis(100);

    private final String topic;
    @Getter
    private long eventsReceivedCount = 0;

    KafkaConsumer<String, String> consumer;

    private final Thread subscriberThread;

    /**
     * Instantiates a new kafka event subscriber.
     *
     * @param topic                   the topic
     * @param sharedKafkaTestResource the kafka server address
     */
    public KafkaEventSubscriber(final String topic,
                                final SharedKafkaTestResource sharedKafkaTestResource) {
        this.topic = topic;

        final Properties consumerProperties = new Properties();
        consumerProperties.put("group.id", "test");

        consumer = sharedKafkaTestResource.getKafkaTestUtils().getKafkaConsumer(StringDeserializer.class,
            StringDeserializer.class, consumerProperties);
        consumer.subscribe(Collections.singletonList(topic));

        subscriberThread = new Thread(this);
        subscriberThread.start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        LOGGER.debug("{}: receiving events from Kafka server  on topic {}", KafkaEventSubscriber.class.getName(),
            topic);

        while (subscriberThread.isAlive() && !subscriberThread.isInterrupted()) {
            try {
                final ConsumerRecords<String, String> records = consumer.poll(POLL_DURATION);
                for (final ConsumerRecord<String, String> rec : records) {
                    eventsReceivedCount++;
                    LOGGER.debug("****** Received event No. {} ******\noffset={}\nkey={}\n{}", eventsReceivedCount,
                        rec.offset(), rec.key(), rec.value());
                }
            } catch (final Exception e) {
                // Thread interrupted
                break;
            }
        }

        LOGGER.debug("{}: event reception completed", KafkaEventSubscriber.class.getName());
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        subscriberThread.interrupt();

        while (subscriberThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        consumer.close();
        LOGGER.debug("{} : stopped", KafkaEventSubscriber.class.getName());
    }

    public boolean isAlive() {
        return subscriberThread.isAlive();
    }
}
