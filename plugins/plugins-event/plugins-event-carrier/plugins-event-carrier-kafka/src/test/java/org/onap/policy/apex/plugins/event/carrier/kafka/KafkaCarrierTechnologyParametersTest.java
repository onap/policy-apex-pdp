/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019,2023 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;
import org.junit.Test;

public class KafkaCarrierTechnologyParametersTest {
    @Test
    public void testKafkaCarrierTechnologyParameters() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();
        assertNotNull(kafkaCarrierTechnologyParameters);

        assertEquals("localhost:9092", kafkaCarrierTechnologyParameters.getBootstrapServers());
    }

    @Test
    public void testGetKafkaProducerProperties() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();

        Properties kafkaProducerProperties = kafkaCarrierTechnologyParameters.getKafkaProducerProperties();
        assertNotNull(kafkaProducerProperties);
        assertEquals("localhost:9092", kafkaProducerProperties.get("bootstrap.servers"));
        assertEquals("1", kafkaProducerProperties.get("linger.ms"));
        assertEquals(null, kafkaProducerProperties.get("group.id"));
        assertEquals(null, kafkaProducerProperties.get("Property0"));
        assertEquals(null, kafkaProducerProperties.get("Property1"));
        assertEquals(null, kafkaProducerProperties.get("Property2"));

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
        assertEquals(null, kafkaProducerProperties.get("group.id"));
        assertEquals("Value0", kafkaProducerProperties.get("Property0"));
        assertEquals("Value1", kafkaProducerProperties.get("Property1"));
        assertEquals(null, kafkaProducerProperties.get("Property2"));
    }

    @Test
    public void testGetKafkaConsumerProperties() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();

        Properties kafkaConsumerProperties = kafkaCarrierTechnologyParameters.getKafkaConsumerProperties();
        assertNotNull(kafkaConsumerProperties);
        assertEquals("localhost:9092", kafkaConsumerProperties.get("bootstrap.servers"));
        assertEquals("default-group-id", kafkaConsumerProperties.get("group.id"));
        assertEquals(null, kafkaConsumerProperties.get("linger.ms"));
        assertEquals(null, kafkaConsumerProperties.get("Property0"));
        assertEquals(null, kafkaConsumerProperties.get("Property1"));
        assertEquals(null, kafkaConsumerProperties.get("Property2"));

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
        assertEquals(null, kafkaConsumerProperties.get("linger.ms"));
        assertEquals("Value0", kafkaConsumerProperties.get("Property0"));
        assertEquals("Value1", kafkaConsumerProperties.get("Property1"));
        assertEquals(null, kafkaConsumerProperties.get("Property2"));
    }

    @Test
    public void testValidate() {
        KafkaCarrierTechnologyParameters kafkaCarrierTechnologyParameters = new KafkaCarrierTechnologyParameters();
        assertNotNull(kafkaCarrierTechnologyParameters);

        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        String origStringValue = kafkaCarrierTechnologyParameters.getBootstrapServers();
        kafkaCarrierTechnologyParameters.setBootstrapServers(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setBootstrapServers(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getAcks();
        kafkaCarrierTechnologyParameters.setAcks(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setAcks(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getGroupId();
        kafkaCarrierTechnologyParameters.setGroupId(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setGroupId(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getProducerTopic();
        kafkaCarrierTechnologyParameters.setProducerTopic(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setProducerTopic(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        int origIntValue = kafkaCarrierTechnologyParameters.getRetries();
        kafkaCarrierTechnologyParameters.setRetries(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setRetries(origIntValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origIntValue = kafkaCarrierTechnologyParameters.getBatchSize();
        kafkaCarrierTechnologyParameters.setBatchSize(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setBatchSize(origIntValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origIntValue = kafkaCarrierTechnologyParameters.getLingerTime();
        kafkaCarrierTechnologyParameters.setLingerTime(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setLingerTime(origIntValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        long origLongValue = kafkaCarrierTechnologyParameters.getBufferMemory();
        kafkaCarrierTechnologyParameters.setBufferMemory(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setBufferMemory(origLongValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origIntValue = kafkaCarrierTechnologyParameters.getAutoCommitTime();
        kafkaCarrierTechnologyParameters.setAutoCommitTime(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setAutoCommitTime(origIntValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origIntValue = kafkaCarrierTechnologyParameters.getSessionTimeout();
        kafkaCarrierTechnologyParameters.setSessionTimeout(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setSessionTimeout(origIntValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origIntValue = kafkaCarrierTechnologyParameters.getConsumerPollTime();
        kafkaCarrierTechnologyParameters.setConsumerPollTime(-1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setConsumerPollTime(origIntValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getKeySerializer();
        kafkaCarrierTechnologyParameters.setKeySerializer(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKeySerializer(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getValueSerializer();
        kafkaCarrierTechnologyParameters.setValueSerializer(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setValueSerializer(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getKeyDeserializer();
        kafkaCarrierTechnologyParameters.setKeyDeserializer(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKeyDeserializer(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        origStringValue = kafkaCarrierTechnologyParameters.getValueDeserializer();
        kafkaCarrierTechnologyParameters.setValueDeserializer(" ");
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setValueDeserializer(origStringValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        String[] origConsumerTopcList = kafkaCarrierTechnologyParameters.getConsumerTopicList();
        kafkaCarrierTechnologyParameters.setConsumerTopicList(null);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setConsumerTopicList(origConsumerTopcList);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        kafkaCarrierTechnologyParameters.setConsumerTopicList(new String[0]);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setConsumerTopicList(origConsumerTopcList);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        String[] blankStringList = { null, "" };
        kafkaCarrierTechnologyParameters.setConsumerTopicList(blankStringList);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setConsumerTopicList(origConsumerTopcList);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        String[][] origKafkaProperties = kafkaCarrierTechnologyParameters.getKafkaProperties();
        kafkaCarrierTechnologyParameters.setKafkaProperties(null);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        kafkaCarrierTechnologyParameters.setKafkaProperties(new String[0][0]);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        // @formatter:offkafkaCarrierTechnologyParameters
        String[][] kafkaProperties0 = {
            {
                null, "Value0"
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaProperties0);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties1 = {
            {
                "Property1", null
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaProperties1);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties2 = {
            {
                "Property1", null
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaProperties2);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaPropertiesWithEmptyValue = {
            {
                "Property1", ""
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaPropertiesWithEmptyValue);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        kafkaCarrierTechnologyParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());

        // @formatter:off
        String[][] kafkaProperties3 = {
            {
                "Property1", "Value0", "Value1"
            }
        };
        // @formatter:on

        kafkaCarrierTechnologyParameters.setKafkaProperties(kafkaProperties3);
        assertFalse(kafkaCarrierTechnologyParameters.validate().isValid());
        kafkaCarrierTechnologyParameters.setKafkaProperties(origKafkaProperties);
        assertTrue(kafkaCarrierTechnologyParameters.validate().isValid());
    }

    @Test
    public void testExplicitImplicit() {
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
}
