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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.salesforce.kafka.test.junit4.SharedKafkaTestResource;

import java.io.File;
import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * The Class TestKafka2Kafka tests Kafka event sending and reception.
 */
public class TestKafka2Kafka {
    private static final long MAX_TEST_LENGTH = 20000;

    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    @ClassRule
    public static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource()
                    // Start a cluster with 1 brokers.
                    .withBrokers(1)
                    // Disable topic auto-creation.
                    .withBrokerProperty("auto.create.topics.enable", "false");

    /**
     * Test json kafka events.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     */
    @Test
    public void testJsonKafkaEvents() throws MessagingException, ApexException {
        final String[] args =
            { "src/test/resources/prodcons/Kafka2KafkaJsonEvent.json" };
        testKafkaEvents(args, false, "json");
    }

    /**
     * Test XML kafka events.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     */
    @Test
    public void testXmlKafkaEvents() throws MessagingException, ApexException {
        final String[] args =
            { "src/test/resources/prodcons/Kafka2KafkaXmlEvent.json" };
        testKafkaEvents(args, true, "xml");
    }

    /**
     * Test kafka events.
     *
     * @param args the args
     * @param xmlEvents the xml events
     * @param topicSuffix the topic suffix
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     */
    private void testKafkaEvents(String[] args, final Boolean xmlEvents, final String topicSuffix)
                    throws MessagingException, ApexException {

        try {
            File tempConfigFile = File.createTempFile("Kafka_", ".json");
            tempConfigFile.deleteOnExit();
            String configAsString = TextFileUtils.getTextFileAsString(args[0]).replaceAll("localhost:39902",
                            sharedKafkaTestResource.getKafkaConnectString());
            TextFileUtils.putStringAsFile(configAsString, tempConfigFile.getCanonicalFile());
            args[0] = tempConfigFile.getCanonicalPath();

        } catch (IOException e) {
            fail("test should not throw an exception");
        }

        sharedKafkaTestResource.getKafkaTestUtils().createTopic("apex-out-" + topicSuffix, 1, (short) 1);
        sharedKafkaTestResource.getKafkaTestUtils().createTopic("apex-in-" + topicSuffix, 1, (short) 1);

        final KafkaEventSubscriber subscriber = new KafkaEventSubscriber("apex-out-" + topicSuffix,
                        sharedKafkaTestResource);

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(3000);

        final KafkaEventProducer producer = new KafkaEventProducer("apex-in-" + topicSuffix, sharedKafkaTestResource,
                        EVENT_COUNT, xmlEvents, EVENT_INTERVAL);

        producer.sendEvents();

        final long testStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < testStartTime + MAX_TEST_LENGTH
                        && subscriber.getEventsReceivedCount() < EVENT_COUNT) {
            ThreadUtilities.sleep(EVENT_INTERVAL);
        }

        ThreadUtilities.sleep(1000);

        assertEquals(producer.getEventsSentCount(), subscriber.getEventsReceivedCount());

        apexMain.shutdown();
        subscriber.shutdown();
        producer.shutdown();
        ThreadUtilities.sleep(1000);
    }
}
