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

package org.onap.policy.apex.service.engine.event.impl.eventrequestor;

import java.util.EnumMap;
import java.util.Map;

import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
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
 * Concrete implementation of an Apex event producer that sends one or more events to its peered
 * event requestor consumer.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * 
 */
public class EventRequestorProducer implements ApexEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventRequestorProducer.class);

    // The name for this producer
    private String name = null;

    // The peer references for this event handler
    private final Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap =
            new EnumMap<>(EventHandlerPeeredMode.class);

    // The number of events sent
    private int eventsSent = 0;

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#init(java.lang.String,
     * org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters)
     */
    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
            throws ApexEventException {
        this.name = producerName;

        // Check and get the producer Properties
        if (!(producerParameters
                .getCarrierTechnologyParameters() instanceof EventRequestorCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified consumer properties are not applicable to event requestor producer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check if we are in peered mode
        if (!producerParameters.isPeeredMode(EventHandlerPeeredMode.REQUESTOR)) {
            final String errorMessage = "Event Requestor producer (" + this.name
                    + ") must run in peered requestor mode with a Event Requestor consumer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the number of events sent to date
     * 
     * @return the number of events received
     */
    public int getEventsSent() {
        return eventsSent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getPeeredReference(org.onap.
     * policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#setPeeredReference(org.onap.
     * policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode,
     * org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#sendEvent(long, java.lang.
     * String, java.lang.Object)
     */
    @Override
    public void sendEvent(final long executionId, final String eventName, final Object eventObject) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache =
                (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        // Find the peered consumer for this producer
        final PeeredReference peeredRequestorReference = peerReferenceMap.get(EventHandlerPeeredMode.REQUESTOR);
        if (peeredRequestorReference != null) {
            // Find the event Response Consumer that will handle this request
            final ApexEventConsumer consumer = peeredRequestorReference.getPeeredConsumer();
            if (!(consumer instanceof EventRequestorConsumer)) {
                final String errorMessage = "send of event to event consumer \""
                        + peeredRequestorReference.getPeeredConsumer() + "\" failed,"
                        + " event response consumer is not an instance of EventRequestorConsumer\n" + eventObject;
                LOGGER.warn(errorMessage);
                throw new ApexEventRuntimeException(errorMessage);
            }

            // Use the consumer to handle this event
            final EventRequestorConsumer eventRequstConsumer = (EventRequestorConsumer) consumer;
            eventRequstConsumer.processEvent(eventObject);

            eventsSent++;
        } else {
            // No peered consumer defined
            final String errorMessage = "send of event failed, event response consumer is not defined\n" + eventObject;
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        // For event requestor, all the implementation is in the consumer
    }
}
