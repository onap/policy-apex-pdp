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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;

/**
 * Apex parameters for SuperDooper as an event carrier technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class SuperDooperCarrierTechnologyParameters extends CarrierTechnologyParameters {
    // Default parameter values
    private static final String DEFAULT_ACKS = "all";
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final int DEFAULT_RETRIES = 0;
    private static final int DEFAULT_BATCH_SIZE = 16384;
    private static final int DEFAULT_LINGER_TIME = 1;
    private static final long DEFAULT_BUFFER_MEMORY = 33554432;
    private static final String DEFAULT_GROUP_ID = "default-group-id";
    private static final boolean DEFAULT_ENABLE_AUTO_COMMIT = true;
    private static final int DEFAULT_AUTO_COMMIT_TIME = 1000;
    private static final int DEFAULT_SESSION_TIMEOUT = 30000;
    private static final String DEFAULT_PRODUCER_TOPIC = "apex-out";
    private static final int DEFAULT_CONSUMER_POLL_TIME = 100;
    private static final String[] DEFAULT_CONSUMER_TOPIC_LIST = { "apex-in" };
    private static final String DEFAULT_KEY_SERIALIZER = "org.apache.superDooper.common.serialization.StringSerializer";
    private static final String DEFAULT_VALUE_SERIALIZER = "org.apache.superDooper.common.serialization.StringSerializer";
    private static final String DEFAULT_KEY_DESERIALIZER = "org.apache.superDooper.common.serialization.StringDeserializer";
    private static final String DEFAULT_VALUE_DESERIALIZER = "org.apache.superDooper.common.serialization.StringDeserializer";

    // Parameter property map tokens
    private static final String PROPERTY_BOOTSTRAP_SERVERS = "bootstrap.servers";
    private static final String PROPERTY_ACKS = "acks";
    private static final String PROPERTY_RETRIES = "retries";
    private static final String PROPERTY_BATCH_SIZE = "batch.size";
    private static final String PROPERTY_LINGER_TIME = "linger.ms";
    private static final String PROPERTY_BUFFER_MEMORY = "buffer.memory";
    private static final String PROPERTY_GROUP_ID = "group.id";
    private static final String PROPERTY_ENABLE_AUTO_COMMIT = "enable.auto.commit";
    private static final String PROPERTY_AUTO_COMMIT_TIME = "auto.commit.interval.ms";
    private static final String PROPERTY_SESSION_TIMEOUT = "session.timeout.ms";
    private static final String PROPERTY_KEY_SERIALIZER = "key.serializer";
    private static final String PROPERTY_VALUE_SERIALIZER = "value.serializer";
    private static final String PROPERTY_KEY_DESERIALIZER = "key.deserializer";
    private static final String PROPERTY_VALUE_DESERIALIZER = "value.deserializer";

    // superDooper carrier parameters
    private String bootstrapServers = DEFAULT_BOOTSTRAP_SERVERS;
    private String acks = DEFAULT_ACKS;
    private int retries = DEFAULT_RETRIES;
    private int batchSize = DEFAULT_BATCH_SIZE;
    private int lingerTime = DEFAULT_LINGER_TIME;
    private long bufferMemory = DEFAULT_BUFFER_MEMORY;
    private String groupId = DEFAULT_GROUP_ID;
    private boolean enableAutoCommit = DEFAULT_ENABLE_AUTO_COMMIT;
    private int autoCommitTime = DEFAULT_AUTO_COMMIT_TIME;
    private int sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    private String producerTopic = DEFAULT_PRODUCER_TOPIC;
    private int consumerPollTime = DEFAULT_CONSUMER_POLL_TIME;
    private String[] consumerTopicList = DEFAULT_CONSUMER_TOPIC_LIST;
    private String keySerializer = DEFAULT_KEY_SERIALIZER;
    private String valueSerializer = DEFAULT_VALUE_SERIALIZER;
    private String keyDeserializer = DEFAULT_KEY_DESERIALIZER;
    private String valueDeserializer = DEFAULT_VALUE_DESERIALIZER;

    /**
     * Constructor to create a file carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public SuperDooperCarrierTechnologyParameters() {
        super();

        // Set the carrier technology properties for the FILE carrier technology
        this.setLabel("SUPER_DOOPER");
        this.setEventProducerPluginClass(
                        "org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperEventProducer");
        this.setEventConsumerPluginClass(
                        "org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperEventSubscriber");
    }

    /**
     * Gets the superDooper producer properties.
     *
     * @return the superDooper producer properties
     */
    public Properties getSuperDooperProducerProperties() {
        final Properties superDooperProperties = new Properties();

        superDooperProperties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        superDooperProperties.put(PROPERTY_ACKS, acks);
        superDooperProperties.put(PROPERTY_RETRIES, retries);
        superDooperProperties.put(PROPERTY_BATCH_SIZE, batchSize);
        superDooperProperties.put(PROPERTY_LINGER_TIME, lingerTime);
        superDooperProperties.put(PROPERTY_BUFFER_MEMORY, bufferMemory);
        superDooperProperties.put(PROPERTY_KEY_SERIALIZER, keySerializer);
        superDooperProperties.put(PROPERTY_VALUE_SERIALIZER, valueSerializer);

        return superDooperProperties;
    }

    /**
     * Gets the superDooper consumer properties.
     *
     * @return the superDooper consumer properties
     */
    public Properties getSuperDooperConsumerProperties() {
        final Properties superDooperProperties = new Properties();

        superDooperProperties.put(PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
        superDooperProperties.put(PROPERTY_GROUP_ID, groupId);
        superDooperProperties.put(PROPERTY_ENABLE_AUTO_COMMIT, enableAutoCommit);
        superDooperProperties.put(PROPERTY_AUTO_COMMIT_TIME, autoCommitTime);
        superDooperProperties.put(PROPERTY_SESSION_TIMEOUT, sessionTimeout);
        superDooperProperties.put(PROPERTY_KEY_DESERIALIZER, keyDeserializer);
        superDooperProperties.put(PROPERTY_VALUE_DESERIALIZER, valueDeserializer);

        return superDooperProperties;
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

    /**
     * Sets the bootstrap servers.
     *
     * @param bootstrapServers the new bootstrap servers
     */
    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    /**
     * Sets the acks.
     *
     * @param acks the new acks
     */
    public void setAcks(String acks) {
        this.acks = acks;
    }

    /**
     * Sets the retries.
     *
     * @param retries the new retries
     */
    public void setRetries(int retries) {
        this.retries = retries;
    }

    /**
     * Sets the batch size.
     *
     * @param batchSize the new batch size
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Sets the linger time.
     *
     * @param lingerTime the new linger time
     */
    public void setLingerTime(int lingerTime) {
        this.lingerTime = lingerTime;
    }

    /**
     * Sets the buffer memory.
     *
     * @param bufferMemory the new buffer memory
     */
    public void setBufferMemory(long bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    /**
     * Sets the group id.
     *
     * @param groupId the new group id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Sets the enable auto commit.
     *
     * @param enableAutoCommit the new enable auto commit
     */
    public void setEnableAutoCommit(boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    /**
     * Sets the auto commit time.
     *
     * @param autoCommitTime the new auto commit time
     */
    public void setAutoCommitTime(int autoCommitTime) {
        this.autoCommitTime = autoCommitTime;
    }

    /**
     * Sets the session timeout.
     *
     * @param sessionTimeout the new session timeout
     */
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    /**
     * Sets the producer topic.
     *
     * @param producerTopic the new producer topic
     */
    public void setProducerTopic(String producerTopic) {
        this.producerTopic = producerTopic;
    }

    /**
     * Sets the consumer poll time.
     *
     * @param consumerPollTime the new consumer poll time
     */
    public void setConsumerPollTime(int consumerPollTime) {
        this.consumerPollTime = consumerPollTime;
    }

    /**
     * Sets the consumer topic list.
     *
     * @param consumerTopicList the new consumer topic list
     */
    public void setConsumerTopicList(String[] consumerTopicList) {
        this.consumerTopicList = consumerTopicList;
    }

    /**
     * Sets the key serializer.
     *
     * @param keySerializer the new key serializer
     */
    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    /**
     * Sets the value serializer.
     *
     * @param valueSerializer the new value serializer
     */
    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    /**
     * Sets the key deserializer.
     *
     * @param keyDeserializer the new key deserializer
     */
    public void setKeyDeserializer(String keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
    }

    /**
     * Sets the value deserializer.
     *
     * @param valueDeserializer the new value deserializer
     */
    public void setValueDeserializer(String valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.common.parameters.ParameterGroup#getName()
     */
    @Override
    public String getName() {
        return this.getLabel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.apps.uservice.parameters.ApexParameterValidator#validate()
     */
    @Override
    public GroupValidationResult validate() {
        final GroupValidationResult result = super.validate();

        if (bootstrapServers == null || bootstrapServers.trim().length() == 0) {
            result.setResult("bootstrapServers", ValidationStatus.INVALID,
                            "bootstrapServers not specified, must be specified as a string of form host:port");
        }

        if (acks == null || acks.trim().length() == 0) {
            result.setResult("acks", ValidationStatus.INVALID,
                            "acks not specified, must be specified as a string with values [0|1|all]");
        }

        if (retries < 0) {
            result.setResult("retries", ValidationStatus.INVALID, "[" + retries + "] invalid, must be specified as retries >= 0");
        }

        if (batchSize < 0) {
            result.setResult("batchSize", ValidationStatus.INVALID, "[" + batchSize + "] invalid, must be specified as batchSize >= 0");
        }

        if (lingerTime < 0) {
            result.setResult("lingerTime", ValidationStatus.INVALID, "[" + lingerTime + "] invalid, must be specified as lingerTime >= 0");
        }

        if (bufferMemory < 0) {
            result.setResult("bufferMemory", ValidationStatus.INVALID, "[" + bufferMemory + "] invalid, must be specified as bufferMemory >= 0");
        }

        if (groupId == null || groupId.trim().length() == 0) {
            result.setResult("groupId", ValidationStatus.INVALID, "not specified, must be specified as a string");
        }

        if (autoCommitTime < 0) {
            result.setResult("autoCommitTime", ValidationStatus.INVALID, "[" + autoCommitTime
                            + "] invalid, must be specified as autoCommitTime >= 0");
        }

        if (sessionTimeout < 0) {
            result.setResult("sessionTimeout", ValidationStatus.INVALID, "sessionTimeout [" + sessionTimeout
                            + "] invalid, must be specified as sessionTimeout >= 0");
        }

        if (producerTopic == null || producerTopic.trim().length() == 0) {
            result.setResult("producerTopic", ValidationStatus.INVALID, "producerTopic not specified, must be specified as a string");
        }

        if (consumerPollTime < 0) {
            result.setResult("consumerPollTime", ValidationStatus.INVALID, "[" + consumerPollTime
                            + "] invalid, must be specified as consumerPollTime >= 0");
        }

        if (consumerTopicList == null || consumerTopicList.length == 0) {
            result.setResult("consumerTopicList", ValidationStatus.INVALID, "not specified, must be specified as a list of strings");
        }

        StringBuilder consumerTopicMessageBuilder = new StringBuilder();
        for (final String consumerTopic : consumerTopicList) {
            if (consumerTopic == null || consumerTopic.trim().length() == 0) {
                consumerTopicMessageBuilder.append("  invalid consumer topic \"" + consumerTopic
                                + "\" specified on consumerTopicList, consumer topics must be specified as strings");
            }
        }
        
        if (consumerTopicMessageBuilder.length() > 0) {
            result.setResult("consumerTopicList", ValidationStatus.INVALID, consumerTopicMessageBuilder.toString());
        }

        if (keySerializer == null || keySerializer.trim().length() == 0) {
            result.setResult("keySerializer", ValidationStatus.INVALID, "not specified, must be specified as a string");
        }

        if (valueSerializer == null || valueSerializer.trim().length() == 0) {
            result.setResult("valueSerializer", ValidationStatus.INVALID, "not specified, must be specified as a string");
        }

        if (keyDeserializer == null || keyDeserializer.trim().length() == 0) {
            result.setResult("keyDeserializer", ValidationStatus.INVALID, "not specified, must be specified as a string");
        }

        if (valueDeserializer == null || valueDeserializer.trim().length() == 0) {
            result.setResult("valueDeserializer", ValidationStatus.INVALID, "not specified, must be specified as a string");
        }

        return result;
    }
}
