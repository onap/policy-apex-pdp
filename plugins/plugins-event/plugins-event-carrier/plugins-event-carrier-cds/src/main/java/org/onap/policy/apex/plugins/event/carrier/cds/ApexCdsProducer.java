/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.cds;

import io.grpc.Status;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.onap.ccsdk.cds.controllerblueprints.common.api.EventType;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceInput;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceOutput;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events using CDS.
 *
 * @author Liam Fallon (liam.fallon@est.tech)
 */
public class ApexCdsProducer implements ApexEventProducer, CdsProcessingListener {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexCdsProducer.class);

    // Recurring string constants
    private static final String EVENT_PRODUCER = "event producer ";

    // The CDS parameters read from the parameter service
    private CdsCarrierTechnologyParameters cdsProducerProperties;

    // The name for this producer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    // The asynchronous repose to a CDS message
    private final AtomicReference<ExecutionServiceOutput> cdsResponse = new AtomicReference<>();

    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
            throws ApexEventException {
        this.name = producerName;

        // Check and get the CDS Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof CdsCarrierTechnologyParameters)) {
            String message = "specified producer properties are not applicable to a CDS producer (" + this.name + ")";
            LOGGER.warn(message);
            throw new ApexEventException(message);
        }
        cdsProducerProperties = (CdsCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendEvent(final long executionId, final String eventName, final Object event) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache =
                (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        // The incoming event should be an ExecutionServiceInput object and nothing else
        ExecutionServiceInput executionServiceInput = null;
        try {
            executionServiceInput = (ExecutionServiceInput) event;
        } catch (Exception ex) {
            String errorMessage = EVENT_PRODUCER + this.name
                    + " received an invalid input event, CDS events must be ExecutionServiceInput objects";
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage, ex);
        }

        // CDS client is invoked to send the APEX event

        try (CdsProcessingClient cdsClient = new CdsProcessingClient(this, cdsProducerProperties)) {
            CountDownLatch countDownLatch = cdsClient.sendRequest(executionServiceInput);
            countDownLatch.await(cdsProducerProperties.getTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            String errorMessage = EVENT_PRODUCER + this.name + " failed to send event \"" + eventName + "\" to CDS";
            LOGGER.warn(errorMessage);
            Thread.currentThread().interrupt();
        }

        if (cdsResponse .get() == null) {
            String errorMessage =
                    EVENT_PRODUCER + this.name + " send if event \"" + eventName + "\" to CDS timed out";
            LOGGER.warn(errorMessage);
        }

        if (!EventType.EVENT_COMPONENT_EXECUTED.equals(cdsResponse.get().getStatus().getEventType())) {
            String errorMessage =
                    EVENT_PRODUCER + this.name + " send if event \"" + eventName + "\" to CDS failed, "
                    + "response from CDS:\n" + cdsResponse.get();
            LOGGER.warn(errorMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        // TODO: Tear down the CDS request
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessage(ExecutionServiceOutput message) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Received notification from CDS: {}", message);
        }

        cdsResponse.set(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable throwable) {
        String errorMessage = EVENT_PRODUCER + this.name + " failed to send event to CDS, status: "
                + Status.fromThrowable(throwable);
        LOGGER.warn(errorMessage, throwable);
    }
}
