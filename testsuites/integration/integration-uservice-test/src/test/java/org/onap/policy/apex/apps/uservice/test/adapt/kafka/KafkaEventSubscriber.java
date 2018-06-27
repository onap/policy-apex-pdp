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

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KafkaEventSubscriber implements Runnable {
    private final String topic;
    private final String kafkaServerAddress;
    private long eventsReceivedCount = 0;

    KafkaConsumer<String, String> consumer;

    Thread subscriberThread;

    public KafkaEventSubscriber(final String topic, final String kafkaServerAddress) throws MessagingException {
        this.topic = topic;
        this.kafkaServerAddress = kafkaServerAddress;

        final Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServerAddress);
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topic));

        subscriberThread = new Thread(this);
        subscriberThread.start();
    }

    @Override
    public void run() {
        System.out.println(KafkaEventSubscriber.class.getCanonicalName() + ": receiving events from Kafka server at "
                + kafkaServerAddress + " on topic " + topic);

        while (subscriberThread.isAlive() && !subscriberThread.isInterrupted()) {
            try {
                final ConsumerRecords<String, String> records = consumer.poll(100);
                for (final ConsumerRecord<String, String> record : records) {
                    System.out.println("******");
                    System.out.println("offset=" + record.offset());
                    System.out.println("key=" + record.key());
                    System.out.println("name=" + record.value());
                    eventsReceivedCount++;
                }
            } catch (final Exception e) {
                // Thread interrupted
                break;
            }
        }

        System.out.println(KafkaEventSubscriber.class.getCanonicalName() + ": event reception completed");
    }

    public long getEventsReceivedCount() {
        return eventsReceivedCount;
    }

    public void shutdown() {
        subscriberThread.interrupt();

        while (subscriberThread.isAlive()) {
            ThreadUtilities.sleep(10);
        }

        consumer.close();
        System.out.println(KafkaEventSubscriber.class.getCanonicalName() + ": stopped");
    }


    public static void main(final String[] args) throws MessagingException {
        if (args.length != 2) {
            System.err.println("usage KafkaEventSubscriber topic kafkaServerAddress");
            return;
        }
        new KafkaEventSubscriber(args[0], args[1]);
    }
}
