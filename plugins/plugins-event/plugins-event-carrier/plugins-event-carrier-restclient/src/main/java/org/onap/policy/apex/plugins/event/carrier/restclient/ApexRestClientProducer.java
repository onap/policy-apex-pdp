/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import java.util.Properties;
import java.util.Set;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterException;
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final String producerName, final EventHandlerParameters producerParameters)
            throws ApexEventException {
        this.name = producerName;

        // Check and get the REST Properties
        if (!(producerParameters.getCarrierTechnologyParameters() instanceof RestClientCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified producer properties are not applicable to REST client producer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        restProducerProperties =
                (RestClientCarrierTechnologyParameters) producerParameters.getCarrierTechnologyParameters();

        // Check if the HTTP method has been set
        if (restProducerProperties.getHttpMethod() == null) {
            restProducerProperties.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.POST);
        }

        if (!RestClientCarrierTechnologyParameters.HttpMethod.POST.equals(restProducerProperties.getHttpMethod())
                && !RestClientCarrierTechnologyParameters.HttpMethod.PUT
                        .equals(restProducerProperties.getHttpMethod())) {
            final String errorMessage = "specified HTTP method of \"" + restProducerProperties.getHttpMethod()
                    + "\" is invalid, only HTTP methods \"POST\" and \"PUT\" are supproted "
                    + "for event sending on REST client producer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Initialize the HTTP client
        client = ClientBuilder.newClient();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return name;
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
            final Object event) {
        // Check if this is a synchronized event, if so we have received a reply
        final SynchronousEventCache synchronousEventCache =
                (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        if (synchronousEventCache != null) {
            synchronousEventCache.removeCachedEventToApexIfExists(executionId);
        }

        String untaggedUrl = restProducerProperties.getUrl();
        if (executionProperties != null) {
            Set<String> names = restProducerProperties.getKeysFromUrl();
            Set<String> inputProperty = executionProperties.stringPropertyNames();

            names.stream().map(key -> Optional.of(key)).forEach(op -> {
                op.filter(str -> inputProperty.contains(str))
                    .orElseThrow(() -> new ApexEventRuntimeException(
                        "key\"" + op.get() + "\"specified on url \"" + restProducerProperties.getUrl() +
                        "\"not found in execution properties passed by the current policy"));
            });

            untaggedUrl = names.stream().reduce(untaggedUrl,
                (acc, str) -> acc.replace("{" + str + "}", (String) executionProperties.get(str)));
        }

        // Send the event as a REST request
        final Response response = sendEventAsRestRequest(untaggedUrl, (String) event);

        // Check that the request worked
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            final String errorMessage = "send of event to URL \"" + untaggedUrl + "\" using HTTP \""
                    + restProducerProperties.getHttpMethod() + "\" failed with status code " + response.getStatus()
                    + " and message \"" + response.readEntity(String.class) + "\", event:\n" + event;
            LOGGER.warn(errorMessage);
            throw new ApexEventRuntimeException(errorMessage);
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("event sent from engine using {} to URL {} with HTTP {} : {} and response {} ", this.name,
                untaggedUrl, restProducerProperties.getHttpMethod(), event, response);
        }
    }

    /**
     * {@inheritDoc}.
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
     * @return the response to the JSON request
     */
    private Response sendEventAsRestRequest(final String untaggedUrl, final String event) {
        // We have already checked that it is a PUT or POST request
        if (RestClientCarrierTechnologyParameters.HttpMethod.POST.equals(restProducerProperties.getHttpMethod())) {
            return client.target(untaggedUrl).request("application/json")
                    .headers(restProducerProperties.getHttpHeadersAsMultivaluedMap()).post(Entity.json(event));
        } else {
            return client.target(untaggedUrl).request("application/json")
                    .headers(restProducerProperties.getHttpHeadersAsMultivaluedMap()).put(Entity.json(event));
        }
    }

    /**
     * Hook for unit test mocking of HTTP client.
     *
     * @param client the mocked client
     */
    protected void setClient(final Client client) {
        this.client = client;
    }
}
