/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.salesforce.kafka.test.junit5.SharedKafkaTestResource;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.resources.TextFileUtils;

/**
 * The Class TestKafka2Kafka tests Kafka event sending and reception.
 */
@ExtendWith(SharedKafkaTestResource.class)
class TestKafka2Kafka {
    private static final long MAX_TEST_LENGTH = 300000;

    private static final int EVENT_COUNT = 25;
    private static final int EVENT_INTERVAL = 20;

    /**
     * Clear relative file root environment variable.
     */
    @BeforeEach
    void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @RegisterExtension
    @Order(1)
    static final SharedKafkaTestResource sharedKafkaTestResource = new SharedKafkaTestResource()
        // Start a cluster with 1 broker.
        .withBrokers(1)
        // Disable topic auto-creation.
        .withBrokerProperty("auto.create.topics.enable", "false");

    /**
     * Test json kafka events.
     *
     * @throws Exception the apex exception
     */
    @Test
    void testJsonKafkaEvents() throws Exception {
        final String conditionedConfigFile = getConditionedConfigFile(
            "target" + File.separator + "examples/config/SampleDomain/Kafka2KafkaJsonEvent.json");
        final String[] args = {"-rfr", "target", "-p", conditionedConfigFile};
        testKafkaEvents(args);
    }

    /**
     * Test kafka events.
     *
     * @param args the args
     * @throws Exception on errors
     */
    private void testKafkaEvents(String[] args) throws Exception {

        sharedKafkaTestResource.getKafkaTestUtils().createTopic("apex-out-" + "json", 1, (short) 1);
        sharedKafkaTestResource.getKafkaTestUtils().createTopic("apex-in-" + "json", 1, (short) 1);

        final KafkaEventSubscriber subscriber =
            new KafkaEventSubscriber("apex-out-" + "json", sharedKafkaTestResource);

        await().atMost(30, TimeUnit.SECONDS).until(subscriber::isAlive);

        final ApexMain apexMain = new ApexMain(args);
        await().atMost(10, TimeUnit.SECONDS).until(apexMain::isAlive);

        long initWaitEndTIme = System.currentTimeMillis() + 10000;

        await().atMost(12, TimeUnit.SECONDS).until(() -> initWaitEndTIme < System.currentTimeMillis());

        final KafkaEventProducer producer = new KafkaEventProducer("apex-in-" + "json", sharedKafkaTestResource,
            EVENT_COUNT, false, EVENT_INTERVAL);

        await().atMost(30, TimeUnit.SECONDS).until(producer::isAlive);

        producer.sendEvents();

        // Wait for the producer to send all its events
        await().atMost(MAX_TEST_LENGTH, TimeUnit.MILLISECONDS)
            .until(() -> producer.getEventsSentCount() >= EVENT_COUNT);

        await().atMost(MAX_TEST_LENGTH, TimeUnit.MILLISECONDS)
            .until(() -> subscriber.getEventsReceivedCount() >= EVENT_COUNT);

        apexMain.shutdown();
        await().atMost(30, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        subscriber.shutdown();
        await().atMost(30, TimeUnit.SECONDS).until(() -> !subscriber.isAlive());

        producer.shutdown();
        await().atMost(30, TimeUnit.SECONDS).until(() -> !producer.isAlive());

        assertEquals(producer.getEventsSentCount(), subscriber.getEventsReceivedCount());
    }

    private String getConditionedConfigFile(final String configurationFileName) {
        try {
            File tempConfigFile = File.createTempFile("Kafka_", ".json");
            tempConfigFile.deleteOnExit();
            String configAsString = TextFileUtils.getTextFileAsString(configurationFileName)
                .replaceAll("localhost:39902", sharedKafkaTestResource.getKafkaConnectString());
            TextFileUtils.putStringAsFile(configAsString, tempConfigFile.getCanonicalFile());

            return tempConfigFile.getCanonicalPath();
        } catch (IOException e) {
            fail("test should not throw an exception");
            return null;
        }
    }
}
