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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Setter;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventProducer;
import org.onap.policy.apex.service.parameters.carriertechnology.RestPluginCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.common.endpoints.event.comm.Topic.CommInfrastructure;
import org.onap.policy.common.endpoints.utils.NetLoggerUtil;
import org.onap.policy.common.endpoints.utils.NetLoggerUtil.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of an Apex event producer that sends events using REST.
 *
 * @author Joss Armstrong (joss.armstrong@ericsson.com)
 *
 */
public class ApexRestClientProducer extends ApexPluginsEventProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestClientProducer.class);

    // The HTTP client that makes a REST call with an event from Apex
    @Setter(AccessLevel.PROTECTED)
    private Client client;

    // The REST carrier properties
    private RestClientCarrierTechnologyParameters restProducerProperties;

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
            restProducerProperties.setHttpMethod(RestPluginCarrierTechnologyParameters.HttpMethod.POST);
        }

        if (!RestPluginCarrierTechnologyParameters.HttpMethod.POST.equals(restProducerProperties.getHttpMethod())
                && !RestPluginCarrierTechnologyParameters.HttpMethod.PUT
                        .equals(restProducerProperties.getHttpMethod())) {
            final String errorMessage = "specified HTTP method of \"" + restProducerProperties.getHttpMethod()
                    + "\" is invalid, only HTTP methods \"POST\" and \"PUT\" are supported "
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
    public void sendEvent(final long executionId, final Properties executionProperties, final String eventName,
            final Object event) {
        super.sendEvent(executionId, executionProperties, eventName, event);

        String untaggedUrl = restProducerProperties.getUrl();
        if (executionProperties != null) {
            Set<String> names = restProducerProperties.getKeysFromUrl();
            Set<String> inputProperty = executionProperties.stringPropertyNames();

            // @formatter:off
            names.stream().map(Optional::of).forEach(op ->
                op.filter(inputProperty::contains)
                    .orElseThrow(() -> new ApexEventRuntimeException(
                        "key \"" + op.get() + "\" specified on url \"" + restProducerProperties.getUrl()
                        + "\" not found in execution properties passed by the current policy"))
            );

            untaggedUrl = names.stream().reduce(untaggedUrl,
                (acc, str) -> acc.replace("{" + str + "}", (String) executionProperties.get(str)));
            // @formatter:on
        }

        NetLoggerUtil.log(EventType.OUT, CommInfrastructure.REST, untaggedUrl, event.toString());
        // Send the event as a REST request
        final var response = sendEventAsRestRequest(untaggedUrl, (String) event);

        NetLoggerUtil.log(EventType.IN, CommInfrastructure.REST, untaggedUrl, response.readEntity(String.class));

        // Check that the request worked
        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
            final String errorMessage = "send of event to URL \"" + untaggedUrl + "\" using HTTP \""
                    + restProducerProperties.getHttpMethod() + "\" failed with status code " + response.getStatus();
            throw new ApexEventRuntimeException(errorMessage);
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
        if (RestPluginCarrierTechnologyParameters.HttpMethod.POST.equals(restProducerProperties.getHttpMethod())) {
            return client.target(untaggedUrl).request("application/json")
                    .headers(restProducerProperties.getHttpHeadersAsMultivaluedMap()).post(Entity.json(event));
        } else {
            return client.target(untaggedUrl).request("application/json")
                    .headers(restProducerProperties.getHttpHeadersAsMultivaluedMap()).put(Entity.json(event));
        }
    }
}
