/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ObjectValidationResult;
import org.onap.policy.common.parameters.ValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.models.base.Validated;

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
    @NotBlank
    private String   bootstrapServers  = DEFAULT_BOOT_SERVERS;
    @NotBlank
    private String   acks              = DEFAULT_ACKS;
    @Min(value = 0)
    private int      retries           = DEFAULT_RETRIES;
    @Min(value = 0)
    private int      batchSize         = DEFAULT_BATCH_SIZE;
    @Min(value = 0)
    private int      lingerTime        = DEFAULT_LINGER_TIME;
    @Min(value = 0)
    private long     bufferMemory      = DEFAULT_BUFFER_MEMORY;
    @NotBlank
    private String   groupId           = DEFAULT_GROUP_ID;
    private boolean  enableAutoCommit  = DEFAULT_ENABLE_AUTOCMIT;
    @Min(value = 0)
    private int      autoCommitTime    = DEFAULT_AUTO_COMMIT_TIME;
    @Min(value = 0)
    private int      sessionTimeout    = DEFAULT_SESSION_TIMEOUT;
    @NotBlank
    private String   producerTopic     = DEFAULT_PROD_TOPIC;
    @Min(value = 0)
    private int      consumerPollTime  = DEFAULT_CONS_POLL_TIME;
    private String[] consumerTopicList = DEFAULT_CONS_TOPICLIST;
    @NotBlank
    private String   keySerializer     = DEFAULT_STRING_SERZER;
    @NotBlank
    private String   valueSerializer   = DEFAULT_STRING_SERZER;
    @NotBlank
    private String   keyDeserializer   = DEFAULT_STRING_DESZER;
    @NotBlank
    private String   valueDeserializer = DEFAULT_STRING_DESZER;
    @NotBlank
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
        final var retKafkaProps = new Properties();

        // Add properties from the Kafka property array
        if (kafkaProperties != null) {
            for (var i = 0; i < kafkaProperties.length; i++) {
                retKafkaProps.setProperty(kafkaProperties[i][0], kafkaProperties[i][1]);
            }
        }

        // @formatter:off
        putExplicitProperty(retKafkaProps, PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers, DEFAULT_BOOT_SERVERS);
        putExplicitProperty(retKafkaProps, PROPERTY_ACKS,              acks,             DEFAULT_ACKS);
        putExplicitProperty(retKafkaProps, PROPERTY_RETRIES,           retries,          DEFAULT_RETRIES);
        putExplicitProperty(retKafkaProps, PROPERTY_BATCH_SIZE,        batchSize,        DEFAULT_BATCH_SIZE);
        putExplicitProperty(retKafkaProps, PROPERTY_LINGER_TIME,       lingerTime,       DEFAULT_LINGER_TIME);
        putExplicitProperty(retKafkaProps, PROPERTY_BUFFER_MEMORY,     bufferMemory,     DEFAULT_BUFFER_MEMORY);
        putExplicitProperty(retKafkaProps, PROPERTY_KEY_SERIALIZER,    keySerializer,    DEFAULT_STRING_SERZER);
        putExplicitProperty(retKafkaProps, PROPERTY_VALUE_SERIALIZER,  valueSerializer,  DEFAULT_STRING_SERZER);
        putExplicitProperty(retKafkaProps, PROPERTY_PARTITIONER_CLASS, partitionerClass, DEFAULT_PARTITIONR_CLASS);
        // @formatter:on

        return retKafkaProps;
    }

    /**
     * Gets the kafka consumer properties.
     *
     * @return the kafka consumer properties
     */
    public Properties getKafkaConsumerProperties() {
        final var retKafkaProps = new Properties();

        // Add properties from the Kafka property array
        if (kafkaProperties != null) {
            for (var i = 0; i < kafkaProperties.length; i++) {
                retKafkaProps.setProperty(kafkaProperties[i][0], kafkaProperties[i][1]);
            }
        }

        // @formatter:off
        putExplicitProperty(retKafkaProps, PROPERTY_BOOTSTRAP_SERVERS,  bootstrapServers, DEFAULT_BOOT_SERVERS);
        putExplicitProperty(retKafkaProps, PROPERTY_GROUP_ID,           groupId,          DEFAULT_GROUP_ID);
        putExplicitProperty(retKafkaProps, PROPERTY_ENABLE_AUTO_COMMIT, enableAutoCommit, DEFAULT_ENABLE_AUTOCMIT);
        putExplicitProperty(retKafkaProps, PROPERTY_AUTO_COMMIT_TIME,   autoCommitTime,   DEFAULT_AUTO_COMMIT_TIME);
        putExplicitProperty(retKafkaProps, PROPERTY_SESSION_TIMEOUT,    sessionTimeout,   DEFAULT_SESSION_TIMEOUT);
        putExplicitProperty(retKafkaProps, PROPERTY_KEY_DESERIALIZER,   keyDeserializer,  DEFAULT_STRING_DESZER);
        putExplicitProperty(retKafkaProps, PROPERTY_VALUE_DESERIALIZER, valueDeserializer, DEFAULT_STRING_DESZER);
        // @formatter:on

        return retKafkaProps;
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = super.validate();

        result.addResult(validateConsumerTopicList());

        result.addResult(validateKafkaProperties());

        return result;
    }

    /**
     * Validate the consumer topic list.
     */
    private ValidationResult validateConsumerTopicList() {
        if (consumerTopicList == null || consumerTopicList.length == 0) {
            return new ObjectValidationResult("consumerTopicList", consumerTopicList, ValidationStatus.INVALID,
                    "not specified, must be specified as a list of strings");
        }

        var result = new BeanValidationResult("consumerTopicList", consumerTopicList);
        var item = 0;
        for (final String consumerTopic : consumerTopicList) {
            if (StringUtils.isBlank(consumerTopic)) {
                result.addResult(ENTRY + item, consumerTopic, ValidationStatus.INVALID, Validated.IS_BLANK);
            }

            ++item;
        }

        return result;
    }

    /**
     * Validate the kafka properties.
     */
    private ValidationResult validateKafkaProperties() {
        // Kafka properties are optional
        if (kafkaProperties == null || kafkaProperties.length == 0) {
            return null;
        }

        var result = new BeanValidationResult(KAFKA_PROPERTIES, kafkaProperties);

        for (var i = 0; i < kafkaProperties.length; i++) {
            final String label = ENTRY + i;
            final String[] kafkaProperty = kafkaProperties[i];
            final List<String> value = (kafkaProperty == null ? null : Arrays.asList(kafkaProperty));
            final var result2 = new BeanValidationResult(label, value);

            if (kafkaProperty == null) {
                // note: add to result, not result2
                result.addResult(label, value, ValidationStatus.INVALID, Validated.IS_NULL);

            } else if (kafkaProperty.length != 2) {
                // note: add to result, not result2
                result.addResult(label, Arrays.asList(kafkaProperty), ValidationStatus.INVALID,
                        "kafka properties must be name-value pairs");

            } else if (StringUtils.isBlank(kafkaProperty[0])) {
                result2.addResult("key", kafkaProperty[0], ValidationStatus.INVALID, Validated.IS_BLANK);

            } else if (null == kafkaProperty[1]) {
                // the value of a property has to be specified as empty in some cases, but should never be null.
                result2.addResult("value", kafkaProperty[1], ValidationStatus.INVALID, Validated.IS_NULL);
            }

            result.addResult(result2);
        }

        return result;
    }

    /**
     * Put a property into the properties if it is not already defined and is not the default value.
     *
     * @param returnKafkaProperties the properties to set the value in
     * @param property the property to put
     * @param value the value of the property to put
     * @param defaultValue the default value of the property to put
     */
    private void putExplicitProperty(final Properties returnKafkaProperties, final String property,
            final Object value, final Object defaultValue) {

        // Check if the property is already in the properties
        if (!returnKafkaProperties.containsKey(property)) {
            // Not found, so add it
            returnKafkaProperties.setProperty(property, value.toString());
        } else {
            // Found, only overwrite if the property does not have the default value
            if (value == null) {
                returnKafkaProperties.setProperty(property, defaultValue.toString());
            } else if (!value.toString().contentEquals(defaultValue.toString())) {
                returnKafkaProperties.setProperty(property, value.toString());
            }
        }
    }
}
