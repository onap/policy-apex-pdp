/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventProducer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events using Kafka.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexKafkaProducer extends ApexPluginsEventProducer {

    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexKafkaProducer.class);

    // The Kafka parameters read from the parameter service
    private KafkaCarrierTechnologyParameters kafkaProducerProperties;

    // The Kafka Producer used to send events using Kafka
    private Producer<String, Object> kafkaProducer;

    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
            throws ApexEventException {
        this.name = producerName;

        // Check and get the Kafka Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof KafkaCarrierTechnologyParameters)) {
            String message = "specified producer properties are not applicable to a Kafka producer (" + this.name + ")";
            LOGGER.warn(message);
            throw new ApexEventException(message);
        }
        kafkaProducerProperties =
                (KafkaCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void sendEvent(final long executionId, final Properties executionProperties, final String eventName,
            final Object event) {
        super.sendEvent(executionId, executionProperties, eventName, event);

        // Kafka producer must be started in the same thread as it is stopped, so we must start it here
        if (kafkaProducer == null) {
            // Kick off the Kafka producer
            kafkaProducer = new KafkaProducer<>(kafkaProducerProperties.getKafkaProducerProperties());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("event producer " + this.name + " is ready to send to topics: "
                        + kafkaProducerProperties.getProducerTopic());
            }
        }

        kafkaProducer.send(new ProducerRecord<String, Object>(kafkaProducerProperties.getProducerTopic(), name, event));
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("event sent from engine using {} to topic {} : {} ", this.name,
                    kafkaProducerProperties.getProducerTopic(), event);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        if (kafkaProducer != null) {
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }
}
