/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.executionproperties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import lombok.Getter;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy Apex event consumer for testing event properties.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class DummyApexEventConsumer implements ApexEventConsumer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyApexEventConsumer.class);

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The name for this consumer
    @Getter
    private String name = null;

    // The peer references for this event handler
    private final Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap =
        new EnumMap<>(EventHandlerPeeredMode.class);

    private DummyCarrierTechnologyParameters dummyConsumerProperties = null;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
                     final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof DummyCarrierTechnologyParameters)) {
            String message = "specified consumer properties of type \""
                + consumerParameters.getCarrierTechnologyParameters().getClass().getName()
                + "\" are not applicable to a dummy consumer";
            LOGGER.warn(message);
            throw new ApexEventException(message);
        }

        dummyConsumerProperties = (DummyCarrierTechnologyParameters) consumerParameters
            .getCarrierTechnologyParameters();
    }

    @Override
    public void start() {
        new Thread(new RunTestEventSender()).start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    @Override
    public void stop() {
    }

    private class RunTestEventSender implements Runnable {
        @Override
        public void run() {
            Properties executionProperties = new Properties();
            try {
                executionProperties.load(new FileInputStream(dummyConsumerProperties.getPropertyFileName()));
            } catch (IOException e1) {
                String message = "reading of executor properties for testing failed from file: "
                    + dummyConsumerProperties.getPropertyFileName();
                LOGGER.warn(message);
                throw new ApexEventRuntimeException(message);
            }

            RunTestEvent event = new RunTestEvent();
            event.setTestToRun(dummyConsumerProperties.getTestToRun());
            try {
                eventReceiver.receiveEvent(1, executionProperties, event.toJson());
            } catch (Exception e) {
                String message = "event processing for executor properties testing failed: " + e.getMessage();
                LOGGER.warn(message, e);
                throw new ApexEventRuntimeException(message, e);
            }
        }
    }
}
