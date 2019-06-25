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

package org.onap.policy.apex.plugins.event.carrier.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Apex parameters for Kafka as an event carrier technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Getter
@Setter
public class KafkaCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    /** The label of this carrier technology. */
    public static final String KAFKA_CARRIER_TECHNOLOGY_LABEL = "KAFKA";

    /** The producer plugin class for the Kafka carrier technology. */
    public static final String KAFKA_EVENT_PRODUCER_PLUGIN_CLASS = ApexKafkaProducer.class.getName();

    /** The consumer plugin class for the Kafka carrier technology. */
    public static final String KAFKA_EVENT_CONSUMER_PLUGIN_CLASS = ApexKafkaConsumer.class.getName();

    // Repeated strings in messages
    private static final String SPECIFY_AS_STRING_MESSAGE = "not specified, must be specified as a string";
    private static final String ENTRY = "entry ";
    private static final String KAFKA_PROPERTIES = "kafkaProperties";

    // Default parameter values
    private static final String   DEFAULT_ACKS             = "all";
    private static final String   DEFAULT_BOOT_SERVERS     = "localhost:9092";
    private static final int      DEFAULT_RETRIES          = 0;
    private static final int      DEFAULT_BATCH_SIZE       = 16384;
    private static final int      DEFAULT_LINGER_TIME      = 1;
    private static final long     DEFAULT_BUFFER_MEMORY    = 33554432;
    private static final String   DEFAULT_GROUP_ID         = "default-group-id";
    private static final boolean  DEFAULT_ENABLE_AUTOCMIT  = true;
    private static final int      DEFAULT_AUTO_COMMIT_TIME = 1000;
    private static final int      DEFAULT_SESSION_TIMEOUT  = 30000;
    private static final String   DEFAULT_PROD_TOPIC       = "apex-out";
    private static final int      DEFAULT_CONS_POLL_TIME   = 100;
    private static final String[] DEFAULT_CONS_TOPICLIST   = {"apex-in"};
    private static final String   DEFAULT_STRING_SERZER    = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String   DEFAULT_STRING_DESZER    = "org.apache.kafka.common.serialization.StringDeserializer";
    private static final String   DEFAULT_PARTITIONR_CLASS = DefaultPartitioner.class.getName();

    // Parameter property map tokens
    private static final String PROPERTY_BOOTSTRAP_SERVERS  = "bootstrap.servers";
    private static final String PROPERTY_ACKS               = "acks";
    private static final String PROPERTY_RETRIES            = "retries";
    private static final String PROPERTY_BATCH_SIZE         = "batch.size";
    private static final String PROPERTY_LINGER_TIME        = "linger.ms";
    private static final String PROPERTY_BUFFER_MEMORY      = "buffer.memory";
    private static final String PROPERTY_GROUP_ID           = "group.id";
    private static final String PROPERTY_ENABLE_AUTO_COMMIT = "enable.auto.commit";
    private static final String PROPERTY_AUTO_COMMIT_TIME   = "auto.commit.interval.ms";
    private static final String PROPERTY_SESSION_TIMEOUT    = "session.timeout.ms";
    private static final String PROPERTY_KEY_SERIALIZER     = "key.serializer";
    private static final String PROPERTY_VALUE_SERIALIZER   = "value.serializer";
    private static final String PROPERTY_KEY_DESERIALIZER   = "key.deserializer";
    private static final String PROPERTY_VALUE_DESERIALIZER = "value.deserializer";
    private static final String PROPERTY_PARTITIONER_CLASS  = "partitioner.class";

    // kafka carrier parameters
    private String   bootstrapServers  = DEFAULT_BOOT_SERVERS;
    private String   acks              = DEFAULT_ACKS;
    private int      retries           = DEFAULT_RETRIES;
    private int      batchSize         = DEFAULT_BATCH_SIZE;
    private int      lingerTime        = DEFAULT_LINGER_TIME;
    private long     bufferMemory      = DEFAULT_BUFFER_MEMORY;
    private String   groupId           = DEFAULT_GROUP_ID;
    private boolean  enableAutoCommit  = DEFAULT_ENABLE_AUTOCMIT;
    private int      autoCommitTime    = DEFAULT_AUTO_COMMIT_TIME;
    private int      sessionTimeout    = DEFAULT_SESSION_TIMEOUT;
    private String   producerTopic     = DEFAULT_PROD_TOPIC;
    private int      consumerPollTime  = DEFAULT_CONS_POLL_TIME;
    private String[] consumerTopicList = DEFAULT_CONS_TOPICLIST;
    private String   keySerializer     = DEFAULT_STRING_SERZER;
    private String   valueSerializer   = DEFAULT_STRING_SERZER;
    private String   keyDeserializer   = DEFAULT_STRING_DESZER;
    private String   valueDeserializer = DEFAULT_STRING_DESZER;
    private String   partitionerClass  = DEFAULT_PARTITIONR_CLASS;

    // All Kafka properties can be specified as an array of key-value pairs
    private String[][] kafkaProperties = null;

    // @formatter:on

    /**
     * Constructor to create a kafka carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public KafkaCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the kafka carrier technology
        this.setLabel(KAFKA_CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(KAFKA_EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(KAFKA_EVENT_CONSUMER_PLUGIN_CLASS);
    }

    /**
     * Gets the kafka producer properties.
     *
     * @return the kafka producer properties
     */
    public Properties getKafkaProducerProperties() {
        final Properties returnKafkaProperties = new Properties();

        // Add properties from the Kafka property array
        if (kafkaProperties != null) {
            for (int i = 0; i < kafkaProperties.length; i++) {
                returnKafkaProperties.put(kafkaProperties[i][0], kafkaProperties[i][1]);
            }
        }

        returnKafkaProperties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        returnKafkaProperties.put(PROPERTY_ACKS, acks);
        returnKafkaProperties.put(PROPERTY_RETRIES, retries);
        returnKafkaProperties.put(PROPERTY_BATCH_SIZE, batchSize);
        returnKafkaProperties.put(PROPERTY_LINGER_TIME, lingerTime);
        returnKafkaProperties.put(PROPERTY_BUFFER_MEMORY, bufferMemory);
        returnKafkaProperties.put(PROPERTY_KEY_SERIALIZER, keySerializer);
        returnKafkaProperties.put(PROPERTY_VALUE_SERIALIZER, valueSerializer);
        returnKafkaProperties.put(PROPERTY_PARTITIONER_CLASS, partitionerClass);

        return returnKafkaProperties;
    }

    /**
     * Gets the kafka consumer properties.
     *
     * @return the kafka consumer properties
     */
    public Properties getKafkaConsumerProperties() {
        final Properties returnKafkaProperties = new Properties();

        // Add properties from the Kafka property array
        if (kafkaProperties != null) {
            for (int i = 0; i < kafkaProperties.length; i++) {
                returnKafkaProperties.put(kafkaProperties[i][0], kafkaProperties[i][1]);
            }
        }

        returnKafkaProperties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        returnKafkaProperties.put(PROPERTY_GROUP_ID, groupId);
        returnKafkaProperties.put(PROPERTY_ENABLE_AUTO_COMMIT, enableAutoCommit);
        returnKafkaProperties.put(PROPERTY_AUTO_COMMIT_TIME, autoCommitTime);
        returnKafkaProperties.put(PROPERTY_SESSION_TIMEOUT, sessionTimeout);
        returnKafkaProperties.put(PROPERTY_KEY_DESERIALIZER, keyDeserializer);
        returnKafkaProperties.put(PROPERTY_VALUE_DESERIALIZER, valueDeserializer);

        return returnKafkaProperties;
    }

    /**
     * Gets the consumer topic list.
     *
     * @return the consumer topic list
     */
    public Collection<String> getConsumerTopicListAsCollection() {
        return Arrays.asList(consumerTopicList);
    }

    /**
     * Gets the consumer poll duration.
     * @return The poll duration
     */
    public Duration getConsumerPollDuration() {
        return Duration.ofMillis(consumerPollTime);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        validateStringParameters(result);

        validateNumericParameters(result);

        validateConsumerTopicList(result);

        validateSerializersAndDeserializers(result);

        validateKafkaProperties(result);

        return result;
    }

    /**
     * Validate that string parameters are correct.
     *
     * @param result the result of the validation
     */
    private void validateStringParameters(final GroupValidationResult result) {
        if (StringUtils.isBlank(bootstrapServers)) {
            result.setResult("bootstrapServers", ValidationStatus.INVALID,
                    "not specified, must be specified as a string of form host:port");
        }

        if (StringUtils.isBlank(acks)) {
            result.setResult("acks", ValidationStatus.INVALID,
                    "not specified, must be specified as a string with values [0|1|all]");
        }

        if (StringUtils.isBlank(groupId)) {
            result.setResult("groupId", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }

        if (StringUtils.isBlank(producerTopic)) {
            result.setResult("producerTopic", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }

        if (StringUtils.isBlank(partitionerClass)) {
            result.setResult("partitionerClass", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }
    }

    /**
     * Check if numeric parameters are valid.
     *
     * @param result the result of the validation
     */
    private void validateNumericParameters(final GroupValidationResult result) {
        if (retries < 0) {
            result.setResult(PROPERTY_RETRIES, ValidationStatus.INVALID,
                    "[" + retries + "] invalid, must be specified as retries >= 0");
        }

        if (batchSize < 0) {
            result.setResult("batchSize", ValidationStatus.INVALID,
                    "[" + batchSize + "] invalid, must be specified as batchSize >= 0");
        }

        if (lingerTime < 0) {
            result.setResult("lingerTime", ValidationStatus.INVALID,
                    "[" + lingerTime + "] invalid, must be specified as lingerTime >= 0");
        }

        if (bufferMemory < 0) {
            result.setResult("bufferMemory", ValidationStatus.INVALID,
                    "[" + bufferMemory + "] invalid, must be specified as bufferMemory >= 0");
        }

        if (autoCommitTime < 0) {
            result.setResult("autoCommitTime", ValidationStatus.INVALID,
                    "[" + autoCommitTime + "] invalid, must be specified as autoCommitTime >= 0");
        }

        if (sessionTimeout < 0) {
            result.setResult("sessionTimeout", ValidationStatus.INVALID,
                    "[" + sessionTimeout + "] invalid, must be specified as sessionTimeout >= 0");
        }

        if (consumerPollTime < 0) {
            result.setResult("consumerPollTime", ValidationStatus.INVALID,
                    "[" + consumerPollTime + "] invalid, must be specified as consumerPollTime >= 0");
        }
    }

    /**
     * Validate the serializers and deserializers.
     *
     * @param result the result of the validation.
     */
    private void validateSerializersAndDeserializers(final GroupValidationResult result) {
        if (StringUtils.isBlank(keySerializer)) {
            result.setResult("keySerializer", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }

        if (StringUtils.isBlank(valueSerializer)) {
            result.setResult("valueSerializer", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }

        if (StringUtils.isBlank(keyDeserializer)) {
            result.setResult("keyDeserializer", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }

        if (StringUtils.isBlank(valueDeserializer)) {
            result.setResult("valueDeserializer", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }
    }

    /**
     * Validate the consumer topic list.
     *
     * @param result the result of the validation.
     */
    private void validateConsumerTopicList(final GroupValidationResult result) {
        if (consumerTopicList == null || consumerTopicList.length == 0) {
            result.setResult("consumerTopicList", ValidationStatus.INVALID,
                    "not specified, must be specified as a list of strings");
            return;
        }

        StringBuilder consumerTopicStringBuilder = new StringBuilder();
        for (final String consumerTopic : consumerTopicList) {
            if (StringUtils.isBlank(consumerTopic)) {
                consumerTopicStringBuilder.append(consumerTopic + "/");
            }
        }
        if (consumerTopicStringBuilder.length() > 0) {
            result.setResult("consumerTopicList", ValidationStatus.INVALID,
                    "invalid consumer topic list entries found: /" + consumerTopicStringBuilder.toString());
        }
    }

    /**
     * Validate the kafka properties.
     *
     * @param result the result of the validation.
     */
    private void validateKafkaProperties(final GroupValidationResult result) {
        // Kafka properties are optional
        if (kafkaProperties == null || kafkaProperties.length == 0) {
            return;
        }

        for (int i = 0; i < kafkaProperties.length; i++) {
            if (kafkaProperties[i].length != 2) {
                result.setResult(KAFKA_PROPERTIES, ValidationStatus.INVALID,
                        ENTRY + i + " invalid, kafka properties must be name-value pairs");
            }

            if (StringUtils.isBlank(kafkaProperties[i][0])) {
                result.setResult(KAFKA_PROPERTIES, ValidationStatus.INVALID,
                        ENTRY + i + " invalid, key is null or blank");
            }

            if (StringUtils.isBlank(kafkaProperties[i][1])) {
                result.setResult(KAFKA_PROPERTIES, ValidationStatus.INVALID,
                        ENTRY + i + " invalid, value is null or blank");
            }
        }
    }
}
