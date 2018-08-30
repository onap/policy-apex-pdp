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

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Apex parameters for Kafka as an event carrier technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class KAFKACarrierTechnologyParameters extends CarrierTechnologyParameters {
    // @formatter:off
    /** The label of this carrier technology. */
    public static final String KAFKA_CARRIER_TECHNOLOGY_LABEL = "KAFKA";

    /** The producer plugin class for the Kafka carrier technology. */
    public static final String KAFKA_EVENT_PRODUCER_PLUGIN_CLASS = ApexKafkaProducer.class.getCanonicalName();

    /** The consumer plugin class for the Kafka carrier technology. */
    public static final String KAFKA_EVENT_CONSUMER_PLUGIN_CLASS = ApexKafkaConsumer.class.getCanonicalName();

    // Repeated strings in messages
    private static final String SPECIFY_AS_STRING_MESSAGE = "not specified, must be specified as a string";

    // Default parameter values
    private static final String   DEFAULT_ACKS                = "all";
    private static final String   DEFAULT_BOOTSTRAP_SERVERS   = "localhost:9092";
    private static final int      DEFAULT_RETRIES             = 0;
    private static final int      DEFAULT_BATCH_SIZE          = 16384;
    private static final int      DEFAULT_LINGER_TIME         = 1;
    private static final long     DEFAULT_BUFFER_MEMORY       = 33554432;
    private static final String   DEFAULT_GROUP_ID            = "default-group-id";
    private static final boolean  DEFAULT_ENABLE_AUTO_COMMIT  = true;
    private static final int      DEFAULT_AUTO_COMMIT_TIME    = 1000;
    private static final int      DEFAULT_SESSION_TIMEOUT     = 30000;
    private static final String   DEFAULT_PRODUCER_TOPIC      = "apex-out";
    private static final int      DEFAULT_CONSUMER_POLL_TIME  = 100;
    private static final String[] DEFAULT_CONSUMER_TOPIC_LIST = {"apex-in"};
    private static final String   DEFAULT_KEY_SERIALIZER      = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String   DEFAULT_VALUE_SERIALIZER    = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String   DEFAULT_KEY_DESERIALIZER    = "org.apache.kafka.common.serialization.StringDeserializer";
    private static final String   DEFAULT_VALUE_DESERIALIZER  = "org.apache.kafka.common.serialization.StringDeserializer";

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

    // kafka carrier parameters
    private String   bootstrapServers  = DEFAULT_BOOTSTRAP_SERVERS;
    private String   acks              = DEFAULT_ACKS;
    private int      retries           = DEFAULT_RETRIES;
    private int      batchSize         = DEFAULT_BATCH_SIZE;
    private int      lingerTime        = DEFAULT_LINGER_TIME;
    private long     bufferMemory      = DEFAULT_BUFFER_MEMORY;
    private String   groupId           = DEFAULT_GROUP_ID;
    private boolean  enableAutoCommit  = DEFAULT_ENABLE_AUTO_COMMIT;
    private int      autoCommitTime    = DEFAULT_AUTO_COMMIT_TIME;
    private int      sessionTimeout    = DEFAULT_SESSION_TIMEOUT;
    private String   producerTopic     = DEFAULT_PRODUCER_TOPIC;
    private int      consumerPollTime  = DEFAULT_CONSUMER_POLL_TIME;
    private String[] consumerTopicList = DEFAULT_CONSUMER_TOPIC_LIST;
    private String   keySerializer     = DEFAULT_KEY_SERIALIZER;
    private String   valueSerializer   = DEFAULT_VALUE_SERIALIZER;
    private String   keyDeserializer   = DEFAULT_KEY_DESERIALIZER;
    private String   valueDeserializer = DEFAULT_VALUE_DESERIALIZER;
    // @formatter:on

    /**
     * Constructor to create a kafka carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public KAFKACarrierTechnologyParameters() {
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
        final Properties kafkaProperties = new Properties();

        kafkaProperties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        kafkaProperties.put(PROPERTY_ACKS, acks);
        kafkaProperties.put(PROPERTY_RETRIES, retries);
        kafkaProperties.put(PROPERTY_BATCH_SIZE, batchSize);
        kafkaProperties.put(PROPERTY_LINGER_TIME, lingerTime);
        kafkaProperties.put(PROPERTY_BUFFER_MEMORY, bufferMemory);
        kafkaProperties.put(PROPERTY_KEY_SERIALIZER, keySerializer);
        kafkaProperties.put(PROPERTY_VALUE_SERIALIZER, valueSerializer);

        return kafkaProperties;
    }

    /**
     * Gets the kafka consumer properties.
     *
     * @return the kafka consumer properties
     */
    public Properties getKafkaConsumerProperties() {
        final Properties kafkaProperties = new Properties();

        kafkaProperties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        kafkaProperties.put(PROPERTY_GROUP_ID, groupId);
        kafkaProperties.put(PROPERTY_ENABLE_AUTO_COMMIT, enableAutoCommit);
        kafkaProperties.put(PROPERTY_AUTO_COMMIT_TIME, autoCommitTime);
        kafkaProperties.put(PROPERTY_SESSION_TIMEOUT, sessionTimeout);
        kafkaProperties.put(PROPERTY_KEY_DESERIALIZER, keyDeserializer);
        kafkaProperties.put(PROPERTY_VALUE_DESERIALIZER, valueDeserializer);

        return kafkaProperties;
    }

    /**
     * Gets the bootstrap servers.
     *
     * @return the bootstrap servers
     */
    public String getBootstrapServers() {
        return bootstrapServers;
    }

    /**
     * Gets the acks.
     *
     * @return the acks
     */
    public String getAcks() {
        return acks;
    }

    /**
     * Gets the retries.
     *
     * @return the retries
     */
    public int getRetries() {
        return retries;
    }

    /**
     * Gets the batch size.
     *
     * @return the batch size
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * Gets the linger time.
     *
     * @return the linger time
     */
    public int getLingerTime() {
        return lingerTime;
    }

    /**
     * Gets the buffer memory.
     *
     * @return the buffer memory
     */
    public long getBufferMemory() {
        return bufferMemory;
    }

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Checks if is enable auto commit.
     *
     * @return true, if checks if is enable auto commit
     */
    public boolean isEnableAutoCommit() {
        return enableAutoCommit;
    }

    /**
     * Gets the auto commit time.
     *
     * @return the auto commit time
     */
    public int getAutoCommitTime() {
        return autoCommitTime;
    }

    /**
     * Gets the session timeout.
     *
     * @return the session timeout
     */
    public int getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * Gets the producer topic.
     *
     * @return the producer topic
     */
    public String getProducerTopic() {
        return producerTopic;
    }

    /**
     * Gets the consumer poll time.
     *
     * @return the consumer poll time
     */
    public long getConsumerPollTime() {
        return consumerPollTime;
    }

    /**
     * Gets the consumer topic list.
     *
     * @return the consumer topic list
     */
    public Collection<String> getConsumerTopicList() {
        return Arrays.asList(consumerTopicList);
    }

    /**
     * Gets the key serializer.
     *
     * @return the key serializer
     */
    public String getKeySerializer() {
        return keySerializer;
    }

    /**
     * Gets the value serializer.
     *
     * @return the value serializer
     */
    public String getValueSerializer() {
        return valueSerializer;
    }

    /**
     * Gets the key deserializer.
     *
     * @return the key deserializer
     */
    public String getKeyDeserializer() {
        return keyDeserializer;
    }

    /**
     * Gets the value deserializer.
     *
     * @return the value deserializer
     */
    public String getValueDeserializer() {
        return valueDeserializer;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (isNullOrBlank(bootstrapServers)) {
            result.setResult("bootstrapServers", ValidationStatus.INVALID,
                            "not specified, must be specified as a string of form host:port");
        }

        if (isNullOrBlank(acks)) {
            result.setResult("acks", ValidationStatus.INVALID,
                            "not specified, must be specified as a string with values [0|1|all]");
        }

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

        if (isNullOrBlank(groupId)) {
            result.setResult("groupId", ValidationStatus.INVALID, SPECIFY_AS_STRING_MESSAGE);
        }

        if (autoCommitTime < 0) {
            result.setResult("autoCommitTime", ValidationStatus.INVALID,
                            "[" + autoCommitTime + "] invalid, must be specified as autoCommitTime >= 0");
        }

        if (sessionTimeout < 0) {
            result.setResult("sessionTimeout", ValidationStatus.INVALID,
                            "[" + sessionTimeout + "] invalid, must be specified as sessionTimeout >= 0");
        }

        if (isNullOrBlank(producerTopic)) {
            result.setResult("producerTopic", ValidationStatus.INVALID,
                            SPECIFY_AS_STRING_MESSAGE);
        }

        if (consumerPollTime < 0) {
            result.setResult("consumerPollTime", ValidationStatus.INVALID,
                            "[" + consumerPollTime + "] invalid, must be specified as consumerPollTime >= 0");
        }

        validateConsumerTopicList(result);

        if (isNullOrBlank(keySerializer)) {
            result.setResult("keySerializer", ValidationStatus.INVALID,
                            SPECIFY_AS_STRING_MESSAGE);
        }

        if (isNullOrBlank(valueSerializer)) {
            result.setResult("valueSerializer", ValidationStatus.INVALID,
                            SPECIFY_AS_STRING_MESSAGE);
        }

        if (isNullOrBlank(keyDeserializer)) {
            result.setResult("keyDeserializer", ValidationStatus.INVALID,
                            SPECIFY_AS_STRING_MESSAGE);
        }

        if (isNullOrBlank(valueDeserializer)) {
            result.setResult("valueDeserializer", ValidationStatus.INVALID,
                            SPECIFY_AS_STRING_MESSAGE);
        }

        return result;
    }
    
    private void validateConsumerTopicList(final GroupValidationResult result) {
        if (consumerTopicList == null || consumerTopicList.length == 0) {
            result.setResult("consumerTopicList", ValidationStatus.INVALID,
                            "not specified, must be specified as a list of strings");
        }

        StringBuilder consumerTopicStringBuilder = new StringBuilder();
        for (final String consumerTopic : consumerTopicList) {
            if (consumerTopic == null || consumerTopic.trim().length() == 0) {
                consumerTopicStringBuilder.append(consumerTopic + "/");
            }
        }
        if (consumerTopicStringBuilder.length() > 0) {
            result.setResult("consumerTopicList", ValidationStatus.INVALID,
                            "invalid consumer topic list entries found: /" + consumerTopicStringBuilder.toString());
        }
    }

    private boolean isNullOrBlank(final String stringValue) {
       return stringValue == null || stringValue.trim().length() == 0;
    }
}
