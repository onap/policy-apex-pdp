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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import java.util.EnumMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

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
 * Concrete implementation of an Apex event producer that sends events using REST.
 *
 * @author Joss Armstrong (joss.armstrong@ericsson.com)
 *
 */
public class ApexRestClientProducer implements ApexEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestClientProducer.class);

    // The HTTP client that makes a REST call with an event from Apex
    private Client client;

    // The REST carrier properties
    private RestClientCarrierTechnologyParameters restProducerProperties;

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
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof RestClientCarrierTechnologyParameters)) {
            final String errorMessage = "specified consumer properties are not applicable to REST client producer ("
                            + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        restProducerProperties = (RestClientCarrierTechnologyParameters) producerParameters
                        .getCarrierTechnologyParameters();

        // Check if the HTTP method has been set
        if (restProducerProperties.getHttpMethod() == null) {
            restProducerProperties.setHttpMethod(RestClientCarrierTechnologyParameters.DEFAULT_PRODUCER_HTTP_METHOD);
        }

        if (!restProducerProperties.getHttpMethod().equalsIgnoreCase("POST")
                        && !restProducerProperties.getHttpMethod().equalsIgnoreCase("PUT")) {
            final String errorMessage = "specified HTTP method of \"" + restProducerProperties.getHttpMethod()
                            + "\" is invalid, only HTTP methods \"POST\" and \"PUT\" are supproted "
                            + "for event sending on REST client producer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Initialize the HTTP client
        client = ClientBuilder.newClient();
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
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache = (SynchronousEventCache) peerReferenceMap
                        .get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        // Send the event as a REST request
        final Response response = sendEventAsRestRequest((String) event);

        // Check that the request worked
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            final String errorMessage = "send of event to URL \"" + restProducerProperties.getUrl() + "\" using HTTP \""
                            + restProducerProperties.getHttpMethod() + "\" failed with status code "
                            + response.getStatus() + " and message \"" + response.readEntity(String.class)
                            + "\", event:\n" + event;
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("event sent from engine using {} to URL {} with HTTP {} : {} and response {} ", this.name,
                            restProducerProperties.getUrl(), restProducerProperties.getHttpMethod(), event, response);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventProducer#stop()
     */
    @Override
    public void stop() {
        // Close the HTTP session
        client.close();
    }

    /**
     * Send the event as a JSON string as a REST request.
     *
     * @param event the event to send
     * @return the response tot he JSON request
     */
    public Response sendEventAsRestRequest(final String event) {
        // We have already checked that it is a PUT or POST request
        if (restProducerProperties.getHttpMethod().equalsIgnoreCase("POST")) {
            return client.target(restProducerProperties.getUrl()).request("application/json").post(Entity.json(event));
        } else {
            return client.target(restProducerProperties.getUrl()).request("application/json").put(Entity.json(event));
        }
    }
}
