/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.parameters.dummyclasses;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.common.parameters.BeanValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.parameters.annotations.Min;
import org.onap.policy.common.parameters.annotations.NotBlank;
import org.onap.policy.common.parameters.annotations.NotNull;
import org.onap.policy.models.base.Validated;

/**
 * Apex parameters for SuperDooper as an event carrier technology.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NotNull
@NotBlank
@Getter
@Setter
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
    private static final String[] DEFAULT_CONSUMER_TOPIC_LIST = {"apex-in"};
    private static final String DEFAULT_KEYSERZER = "org.apache.superDooper.common.serialization.StringSerializer";
    private static final String DEFAULT_VALSERZER = "org.apache.superDooper.common.serialization.StringSerializer";
    private static final String DEFAULT_KEYDESZER = "org.apache.superDooper.common.serialization.StringDeserializer";
    private static final String DEFAULT_VALDESZER = "org.apache.superDooper.common.serialization.StringDeserializer";

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
    private @Min(0) int retries = DEFAULT_RETRIES;
    private @Min(0) int batchSize = DEFAULT_BATCH_SIZE;
    private @Min(0) int lingerTime = DEFAULT_LINGER_TIME;
    private @Min(0) long bufferMemory = DEFAULT_BUFFER_MEMORY;
    private String groupId = DEFAULT_GROUP_ID;
    private boolean enableAutoCommit = DEFAULT_ENABLE_AUTO_COMMIT;
    private @Min(0) int autoCommitTime = DEFAULT_AUTO_COMMIT_TIME;
    private int sessionTimeout = DEFAULT_SESSION_TIMEOUT;
    private String producerTopic = DEFAULT_PRODUCER_TOPIC;
    private @Min(0) int consumerPollTime = DEFAULT_CONSUMER_POLL_TIME;
    private String[] consumerTopicList = DEFAULT_CONSUMER_TOPIC_LIST;
    private String keySerializer = DEFAULT_KEYSERZER;
    private String valueSerializer = DEFAULT_VALSERZER;
    private String keyDeserializer = DEFAULT_KEYDESZER;
    private String valueDeserializer = DEFAULT_VALDESZER;

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
     * Gets the consumer topic list.
     *
     * @return the consumer topic list
     */
    public Collection<String> getConsumerTopicList() {
        return Arrays.asList(consumerTopicList);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return this.getLabel();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public BeanValidationResult validate() {
        final BeanValidationResult result = super.validate();

        if (consumerTopicList == null) {
            return result;
        }

        if (consumerTopicList.length == 0) {
            result.addResult("consumerTopicList", consumerTopicList, ValidationStatus.INVALID,
                    "not specified, must be specified as a list of strings");
            return result;
        }

        BeanValidationResult result2 = new BeanValidationResult("consumerTopicList", consumerTopicList);
        int item = 0;
        for (final String consumerTopic : consumerTopicList) {
            if (StringUtils.isBlank(consumerTopic)) {
                result2.addResult("entry " + item, consumerTopic, ValidationStatus.INVALID, Validated.IS_BLANK);
            }

            ++item;
        }

        result.addResult(result2);

        return result;
    }
}
