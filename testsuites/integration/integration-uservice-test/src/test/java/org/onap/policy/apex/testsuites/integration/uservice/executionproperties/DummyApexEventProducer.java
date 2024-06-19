/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2024 Nordix Foundation.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import lombok.Getter;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.utils.coder.CoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dummy Apex event producer for testing event properties.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class DummyApexEventProducer implements ApexEventProducer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyApexEventProducer.class);

    // The parameters read from the parameter service
    private DummyCarrierTechnologyParameters dummyProducerProperties;

    // The name for this producer
    @Getter
    private String name = null;

    // The peer references for this event handler
    private final Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap =
        new EnumMap<>(EventHandlerPeeredMode.class);

    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
        throws ApexEventException {
        this.name = producerName;

        // Check and get the Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof DummyCarrierTechnologyParameters)) {
            String message = "specified producer properties are not applicable to a dummy producer (" + this.name + ")";
            LOGGER.warn(message);
            throw new ApexEventException(message);
        }
        dummyProducerProperties =
            (DummyCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        new File(dummyProducerProperties.getPropertyFileName()).delete();
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void sendEvent(final long executionId, final Properties executionProperties, final String eventName,
                          final Object eventAsJsonString) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache =
            (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        RunTestEvent testEvent = new RunTestEvent();
        try {
            testEvent.fromJson((String) eventAsJsonString);
        } catch (CoderException ce) {
            String message = "could not decode event from JSON";
            LOGGER.warn(message, ce);
            throw new ApexEventRuntimeException(message, ce);
        }
        if (!dummyProducerProperties.getTestToRun().equals(testEvent.getTestToRun())) {
            String message = "tests in received test event and parameters do not match " + testEvent.getTestToRun()
                + ":" + dummyProducerProperties.getTestToRun();
            LOGGER.warn(message);
            throw new ApexEventRuntimeException(message);
        }

        try {
            executionProperties.store(new FileOutputStream(dummyProducerProperties.getPropertyFileName()),
                "");
        } catch (IOException ioe) {
            String message = "writing of executor properties for testing failed from file: "
                + dummyProducerProperties.getPropertyFileName();
            LOGGER.warn(message, ioe);
            throw new ApexEventRuntimeException(message, ioe);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        // Not used
    }
}
