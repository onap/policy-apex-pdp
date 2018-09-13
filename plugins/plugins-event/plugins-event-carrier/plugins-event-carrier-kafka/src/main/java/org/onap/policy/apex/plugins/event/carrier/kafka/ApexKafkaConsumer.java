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

import java.util.EnumMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events using Kafka.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexKafkaConsumer implements ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexKafkaConsumer.class);

    // The Kafka parameters read from the parameter service
    private KafkaCarrierTechnologyParameters kafkaConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The Kafka consumer used to receive events using Kafka
    private KafkaConsumer<String, String> kafkaConsumer;

    // The name for this consumer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    // The consumer thread and stopping flag
    private Thread consumerThread;
    private boolean stopOrderedFlag = false;

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#init(java.lang.String,
     * org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters,
     * org.onap.policy.apex.service.engine.event.ApexEventReceiver)
     */
    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the Kafka Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof KafkaCarrierTechnologyParameters)) {
            LOGGER.warn("specified consumer properties of type \""
                    + consumerParameters.getCarrierTechnologyParameters().getClass().getCanonicalName()
                    + "\" are not applicable to a Kafka consumer");
            throw new ApexEventException("specified consumer properties of type \""
                    + consumerParameters.getCarrierTechnologyParameters().getClass().getCanonicalName()
                    + "\" are not applicable to a Kafka consumer");
        }
        kafkaConsumerProperties =
                (KafkaCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        // Kick off the Kafka consumer
        kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProperties.getKafkaConsumerProperties());
        kafkaConsumer.subscribe(kafkaConsumerProperties.getConsumerTopicList());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("event receiver for " + this.getClass().getName() + ":" + this.name + " subscribed to topics: "
                    + kafkaConsumerProperties.getConsumerTopicList());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#start()
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + ":" + this.name;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.policy.apex.service.
     * parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.policy.apex.service.
     * parameters.eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // Kick off the Kafka consumer
        kafkaConsumer = new KafkaConsumer<>(kafkaConsumerProperties.getKafkaConsumerProperties());
        kafkaConsumer.subscribe(kafkaConsumerProperties.getConsumerTopicList());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("event receiver for " + this.getClass().getName() + ":" + this.name + " subscribed to topics: "
                    + kafkaConsumerProperties.getConsumerTopicList());
        }

        // The endless loop that receives events over Kafka
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            try {
                final ConsumerRecords<String, String> records =
                        kafkaConsumer.poll(kafkaConsumerProperties.getConsumerPollTime());
                for (final ConsumerRecord<String, String> record : records) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("event received for {} for forwarding to Apex engine : {} {}",
                                this.getClass().getName() + ":" + this.name, record.key(), record.value());
                    }
                    eventReceiver.receiveEvent(record.value());
                }
            } catch (final Exception e) {
                LOGGER.warn("error receiving events on thread {}", consumerThread.getName(), e);
            }
        }

        if (!consumerThread.isInterrupted()) {
            kafkaConsumer.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#stop()
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(kafkaConsumerProperties.getConsumerPollTime());
        }
    }
}
