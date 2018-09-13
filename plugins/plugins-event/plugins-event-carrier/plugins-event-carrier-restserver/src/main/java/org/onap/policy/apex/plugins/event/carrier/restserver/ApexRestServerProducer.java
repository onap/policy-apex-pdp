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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import java.util.EnumMap;
import java.util.Map;

import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events using REST.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 *
 */
public class ApexRestServerProducer implements ApexEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestServerProducer.class);

    // The name for this producer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

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

        // Check and get the REST Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof RestServerCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified producer properties are not applicable to REST Server producer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        
        // The REST carrier properties
        RestServerCarrierTechnologyParameters restProducerProperties =
                (RestServerCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        // Check if host and port are defined
        if (restProducerProperties.getHost() != null || restProducerProperties.getPort() != -1
                || restProducerProperties.isStandalone()) {
            final String errorMessage =
                    "the parameters \"host\", \"port\", and \"standalone\" are illegal on REST Server producer ("
                            + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check if we are in synchronous mode
        if (!producerParameters.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS)) {
            final String errorMessage =
                    "REST Server producer (" + this.name + ") must run in synchronous mode with a REST Server consumer";
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

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#getPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#setPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#sendEvent(long, java.lang. String,
     * java.lang.Object)
     */
    @Override
    public void sendEvent(final long executionId, final String eventName, final Object event) {
        if (LOGGER.isDebugEnabled()) {
            String message = name + ": event " + executionId + ':' + eventName + " recevied from Apex, event=" + event;
            LOGGER.debug(message);
        }

        // If we are not synchronized, then exit
        final SynchronousEventCache synchronousEventCache =
                (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache == null) {
            return;
        }

        // We see all events on the receiver, even those that are not replies to events sent by the synchronized
        // consumer of this producer, ignore those
        // events
        if (!synchronousEventCache.existsEventToApex(executionId)) {
            return;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(name + ": event " + executionId + ':' + eventName + " is a reply to a REST server call from "
                    + name);
        }

        // Add the event to the received event cache
        synchronousEventCache.cacheSynchronizedEventFromApex(executionId, event);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        // Implementation not required on this class
    }
}
