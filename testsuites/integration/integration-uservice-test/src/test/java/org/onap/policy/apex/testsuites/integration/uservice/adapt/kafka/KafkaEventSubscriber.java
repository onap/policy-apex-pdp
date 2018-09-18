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

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

/**
 * The Class KafkaEventSubscriber.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KafkaEventSubscriber implements Runnable {
    private static final Duration POLL_DURATION = Duration.ofMillis(100);

    private final String topic;
    private long eventsReceivedCount = 0;

    KafkaConsumer<String, String> consumer;

    Thread subscriberThread;

    /**
     * Instantiates a new kafka event subscriber.
     *
     * @param topic the topic
     * @param sharedKafkaTestResource the kafka server address
     * @throws MessagingException the messaging exception
     */
    public KafkaEventSubscriber(final String topic, final SharedKafkaTestResource sharedKafkaTestResource)
                    throws MessagingException {
        this.topic = topic;


        final Properties consumerProperties = new Properties();
        consumerProperties.put("group.id", "test");


        consumer = sharedKafkaTestResource.getKafkaTestUtils().getKafkaConsumer(StringDeserializer.class,
                        StringDeserializer.class, consumerProperties);
        consumer.subscribe(Arrays.asList(topic));

        subscriberThread = new Thread(this);
        subscriberThread.start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        System.out.println(KafkaEventSubscriber.class.getCanonicalName()
                        + ": receiving events from Kafka server  on topic " + topic);

        while (subscriberThread.isAlive() && !subscriberThread.isInterrupted()) {
            try {
                final ConsumerRecords<String, String> records = consumer.poll(POLL_DURATION.toMillis());
                for (final ConsumerRecord<String, String> record : records) {
                    eventsReceivedCount++;
                    System.out.println("****** Received event No. " + eventsReceivedCount + " ******");
                    System.out.println("offset=" + record.offset());
                    System.out.println("key=" + record.key());
                }
            } catch (final Exception e) {
                // Thread interrupted
                break;
            }
        }

        System.out.println(KafkaEventSubscriber.class.getCanonicalName() + ": event reception completed");
    }

    /**
     * Gets the events received count.
     *
     * @return the events received count
     */
    public long getEventsReceivedCount() {
        return eventsReceivedCount;
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
        System.out.println(KafkaEventSubscriber.class.getCanonicalName() + ": stopped");
    }
}
