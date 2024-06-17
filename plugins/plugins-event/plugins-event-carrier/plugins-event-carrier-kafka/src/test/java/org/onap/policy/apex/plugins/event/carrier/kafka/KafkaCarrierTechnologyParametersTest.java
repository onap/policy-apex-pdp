/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

class KafkaCarrierTechnologyParametersTest {
    @Test
    void testKafkaCarrierTechnologyParameters() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();
        assertNotNull(kafkaCarrierTechnologyParameters);

        assertEquals("localhost:9092", kafkaCarrierTechnologyParameters.getBootstrapServers());
    }

    @Test
    void testGetKafkaProducerProperties() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();

        Properties kafkaProducerProperties = kafkaCarrierTechnologyParameters.getKafkaProducerProperties();
        assertNotNull(kafkaProducerProperties);
        assertEquals("localhost:9092", kafkaProducerProperties.get("bootstrap.servers"));
        assertEquals("1", kafkaProducerProperties.get("linger.ms"));
        assertNull(kafkaProducerProperties.get("group.id"));
        assertNull(kafkaProducerProperties.get("Property0"));
        assertNull(kafkaProducerProperties.get("Property1"));
        assertNull(kafkaProducerProperties.get("Property2"));

        // @formatter:off
        String[][] kafkaProperties = {
            {
                "Property0", "Value0"
            },
            {
                "Property1", "Value1"
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaProperties);
        kafkaProducerProperties = kafkaCarrierTechnologyParameters.getKafkaProducerProperties();
        assertNotNull(kafkaProducerProperties);
        assertEquals("localhost:9092", kafkaProducerProperties.get("bootstrap.servers"));
        assertEquals("1", kafkaProducerProperties.get("linger.ms"));
        assertNull(kafkaProducerProperties.get("group.id"));
        assertEquals("Value0", kafkaProducerProperties.get("Property0"));
        assertEquals("Value1", kafkaProducerProperties.get("Property1"));
        assertNull(kafkaProducerProperties.get("Property2"));
    }

    @Test
    void testGetKafkaConsumerProperties() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();

        Properties kafkaConsumerProperties = kafkaCarrierTechnologyParameters.getKafkaConsumerProperties();
        assertNotNull(kafkaConsumerProperties);
        assertEquals("localhost:9092", kafkaConsumerProperties.get("bootstrap.servers"));
        assertEquals("default-group-id", kafkaConsumerProperties.get("group.id"));
        assertNull(kafkaConsumerProperties.get("linger.ms"));
        assertNull(kafkaConsumerProperties.get("Property0"));
        assertNull(kafkaConsumerProperties.get("Property1"));
        assertNull(kafkaConsumerProperties.get("Property2"));

        // @formatter:off
        String[][] kafkaProperties = {
            {
                "Property0", "Value0"
            },
            {
                "Property1", "Value1"
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaProperties);
        kafkaConsumerProperties = kafkaCarrierTechnologyParameters.getKafkaConsumerProperties();
        assertNotNull(kafkaConsumerProperties);
        assertEquals("localhost:9092", kafkaConsumerProperties.get("bootstrap.servers"));
        assertEquals("default-group-id", kafkaConsumerProperties.get("group.id"));
        assertNull(kafkaConsumerProperties.get("linger.ms"));
        assertEquals("Value0", kafkaConsumerProperties.get("Property0"));
        assertEquals("Value1", kafkaConsumerProperties.get("Property1"));
        assertNull(kafkaConsumerProperties.get("Property2"));
    }

    @Test
    void testValidate() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();
        assertNotNull(kafkaCarrierTechnologyParameters);

        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        assertValidateStringProperties(kafkaCarrierTechnologyParameters);

        assertValidateNumberProperties(kafkaCarrierTechnologyParameters);

        assertValidateTopicList(kafkaCarrierTechnologyParameters);

        assertValidateKafkaProperties(kafkaCarrierTechnologyParameters);
    }

    @Test
    void testExplicitImplicit() {
        KafkaCarrierTechnologyParameters kafkaCtp = new KafkaCarrierTechnologyParameters();
        assertNotNull(kafkaCtp);

        assertTrue(kafkaCtp.validate().isValid());

        // @formatter:off
        assertEquals("localhost:9092",   kafkaCtp.getKafkaConsumerProperties().get("bootstrap.servers"));
        assertEquals("all",              kafkaCtp.getKafkaProducerProperties().get("acks"));
        assertEquals("0",                kafkaCtp.getKafkaProducerProperties().get("retries"));
        assertEquals("16384",            kafkaCtp.getKafkaProducerProperties().get("batch.size"));
        assertEquals("1",                kafkaCtp.getKafkaProducerProperties().get("linger.ms"));
        assertEquals("33554432",         kafkaCtp.getKafkaProducerProperties().get("buffer.memory"));
        assertEquals("default-group-id", kafkaCtp.getKafkaConsumerProperties().get("group.id"));
        assertEquals("true",             kafkaCtp.getKafkaConsumerProperties().get("enable.auto.commit"));
        assertEquals("1000",             kafkaCtp.getKafkaConsumerProperties().get("auto.commit.interval.ms"));
        assertEquals("30000",            kafkaCtp.getKafkaConsumerProperties().get("session.timeout.ms"));
        // @formatter:on

        assertEquals("org.apache.kafka.common.serialization.StringSerializer",
                kafkaCtp.getKafkaProducerProperties().get("key.serializer"));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer",
                kafkaCtp.getKafkaProducerProperties().get("value.serializer"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer",
                kafkaCtp.getKafkaConsumerProperties().get("key.deserializer"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer",
                kafkaCtp.getKafkaConsumerProperties().get("value.deserializer"));

        // @formatter:off
        String[][] kafkaProperties0 = {
            {
                "bootstrap.servers", "localhost:9092"
            }
        };
        // @formatter:on

        kafkaCtp.setBootstrapServers(null);
        kafkaCtp.setKafkaProperties(kafkaProperties0);
        assertEquals("localhost:9092", kafkaCtp.getKafkaConsumerProperties().get("bootstrap.servers"));

        // @formatter:off
        String[][] kafkaProperties1 = {
            {
                "bootstrap.servers", "localhost:9999"
            }
        };
        // @formatter:on

        kafkaCtp = new KafkaCarrierTechnologyParameters();
        kafkaCtp.setKafkaProperties(kafkaProperties1);
        assertEquals("localhost:9999", kafkaCtp.getKafkaConsumerProperties().get("bootstrap.servers"));

        // @formatter:off
        String[][] kafkaProperties2 = {
            {
                "bootstrap.servers", "localhost:8888"
            }
        };
        // @formatter:on

        kafkaCtp = new KafkaCarrierTechnologyParameters();
        kafkaCtp.setBootstrapServers("localhost:9092");
        kafkaCtp.setKafkaProperties(kafkaProperties2);
        assertEquals("localhost:8888", kafkaCtp.getKafkaConsumerProperties().get("bootstrap.servers"));

        // @formatter:off
        String[][] kafkaProperties3 = {
            {
                "bootstrap.servers", "localhost:5555"
            }
        };
        // @formatter:on

        kafkaCtp = new KafkaCarrierTechnologyParameters();
        kafkaCtp.setBootstrapServers("localhost:7777");
        kafkaCtp.setKafkaProperties(kafkaProperties3);
        assertEquals("localhost:7777", kafkaCtp.getKafkaConsumerProperties().get("bootstrap.servers"));
    }

    private static void assertValidateStringProperties(KafkaCarrierTechnologyParameters kafkaParameters) {
        String origStringValue = kafkaParameters.getBootstrapServers();
        kafkaParameters.setBootstrapServers(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setBootstrapServers(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getAcks();
        kafkaParameters.setAcks(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setAcks(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getGroupId();
        kafkaParameters.setGroupId(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setGroupId(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getProducerTopic();
        kafkaParameters.setProducerTopic(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setProducerTopic(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getKeySerializer();
        kafkaParameters.setKeySerializer(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setKeySerializer(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getValueSerializer();
        kafkaParameters.setValueSerializer(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setValueSerializer(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getKeyDeserializer();
        kafkaParameters.setKeyDeserializer(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setKeyDeserializer(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());

        origStringValue = kafkaParameters.getValueDeserializer();
        kafkaParameters.setValueDeserializer(" ");
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setValueDeserializer(origStringValue);
        assertTrue(kafkaParameters.validate().isValid());
    }

    private static void assertValidateTopicList(KafkaCarrierTechnologyParameters kafkaParameters) {
        String[] origConsumerTopicList = kafkaParameters.getConsumerTopicList();
        kafkaParameters.setConsumerTopicList(null);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setConsumerTopicList(origConsumerTopicList);
        assertTrue(kafkaParameters.validate().isValid());

        kafkaParameters.setConsumerTopicList(new String[0]);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setConsumerTopicList(origConsumerTopicList);
        assertTrue(kafkaParameters.validate().isValid());

        String[] blankStringList = { null, "" };
        kafkaParameters.setConsumerTopicList(blankStringList);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setConsumerTopicList(origConsumerTopicList);
        assertTrue(kafkaParameters.validate().isValid());
    }

    private static void assertValidateNumberProperties(KafkaCarrierTechnologyParameters kafkaParameters) {
        int origIntValue = kafkaParameters.getRetries();
        kafkaParameters.setRetries(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setRetries(origIntValue);
        assertTrue(kafkaParameters.validate().isValid());

        origIntValue = kafkaParameters.getBatchSize();
        kafkaParameters.setBatchSize(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setBatchSize(origIntValue);
        assertTrue(kafkaParameters.validate().isValid());

        origIntValue = kafkaParameters.getLingerTime();
        kafkaParameters.setLingerTime(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setLingerTime(origIntValue);
        assertTrue(kafkaParameters.validate().isValid());

        long origLongValue = kafkaParameters.getBufferMemory();
        kafkaParameters.setBufferMemory(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setBufferMemory(origLongValue);
        assertTrue(kafkaParameters.validate().isValid());

        origIntValue = kafkaParameters.getAutoCommitTime();
        kafkaParameters.setAutoCommitTime(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setAutoCommitTime(origIntValue);
        assertTrue(kafkaParameters.validate().isValid());

        origIntValue = kafkaParameters.getSessionTimeout();
        kafkaParameters.setSessionTimeout(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setSessionTimeout(origIntValue);
        assertTrue(kafkaParameters.validate().isValid());

        origIntValue = kafkaParameters.getConsumerPollTime();
        kafkaParameters.setConsumerPollTime(-1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setConsumerPollTime(origIntValue);
        assertTrue(kafkaParameters.validate().isValid());
    }

    private static void assertValidateKafkaProperties(KafkaCarrierTechnologyParameters kafkaParameters) {
        String[][] origKafkaProperties = kafkaParameters.getKafkaProperties();
        kafkaParameters.setKafkaProperties(null);
        assertTrue(kafkaParameters.validate().isValid());
        kafkaParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaParameters.validate().isValid());

        kafkaParameters.setKafkaProperties(new String[0][0]);
        assertTrue(kafkaParameters.validate().isValid());
        kafkaParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties0 = {
            {
                null, "Value0"
            }
        };
        // @formatter:on

        kafkaParameters.setKafkaProperties(kafkaProperties0);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties1 = {
            {
                "Property1", null
            }
        };
        // @formatter:on

        kafkaParameters.setKafkaProperties(kafkaProperties1);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties2 = {
            {
                "Property1", null
            }
        };
        // @formatter:on

        kafkaParameters.setKafkaProperties(kafkaProperties2);
        assertFalse(kafkaParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaPropertiesWithEmptyValue = {
            {
                "Property1", ""
            }
        };
        // @formatter:on

        kafkaParameters.setKafkaProperties(kafkaPropertiesWithEmptyValue);
        assertTrue(kafkaParameters.validate().isValid());

        kafkaParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties3 = {
            {
                "Property1", "Value0", "Value1"
            }
        };
        // @formatter:on

        kafkaParameters.setKafkaProperties(kafkaProperties3);
        assertFalse(kafkaParameters.validate().isValid());
        kafkaParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaParameters.validate().isValid());
    }
}
