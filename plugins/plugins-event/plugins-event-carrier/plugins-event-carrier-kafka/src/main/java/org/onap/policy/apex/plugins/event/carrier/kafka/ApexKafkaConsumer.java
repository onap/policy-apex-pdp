/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events using Kafka.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexKafkaConsumer extends ApexPluginsEventConsumer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexKafkaConsumer.class);

    // The Kafka parameters read from the parameter service
    private KafkaCarrierTechnologyParameters kafkaConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
        final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the Kafka Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof KafkaCarrierTechnologyParameters)) {
            throw new ApexEventException("specified consumer properties of type \""
                + consumerParameters.getCarrierTechnologyParameters().getClass().getName()
                + "\" are not applicable to a Kafka consumer");
        }
        kafkaConsumerProperties =
            (KafkaCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void run() {
        // Kick off the Kafka consumer
        try (KafkaConsumer<String, String> kafkaConsumer =
            new KafkaConsumer<>(kafkaConsumerProperties.getKafkaConsumerProperties())) {
            kafkaConsumer.subscribe(kafkaConsumerProperties.getConsumerTopicListAsCollection());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("event receiver for {}:{} subscribed to topics: {}", this.getClass().getName(), this.name,
                    kafkaConsumerProperties.getConsumerTopicList());
            }

            // The endless loop that receives events over Kafka
            while (consumerThread.isAlive() && !stopOrderedFlag) {
                try {
                    final ConsumerRecords<String, String> records =
                        kafkaConsumer.poll(kafkaConsumerProperties.getConsumerPollDuration());
                    for (final ConsumerRecord<String, String> dataRecord : records) {
                        traceIfTraceEnabled(dataRecord);
                        eventReceiver.receiveEvent(new Properties(), dataRecord.value());
                    }
                } catch (final Exception e) {
                    LOGGER.debug("error receiving events on thread {}", consumerThread.getName(), e);
                }
            }
        }
    }

    /**
     * Trace a record if trace is enabled.
     *
     * @param dataRecord the record to trace
     */
    private void traceIfTraceEnabled(final ConsumerRecord<String, String> dataRecord) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("event received for {} for forwarding to Apex engine : {} {}",
                this.getClass().getName() + ":" + this.name, dataRecord.key(), dataRecord.value());
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(kafkaConsumerProperties.getConsumerPollTime());
        }
    }
}
