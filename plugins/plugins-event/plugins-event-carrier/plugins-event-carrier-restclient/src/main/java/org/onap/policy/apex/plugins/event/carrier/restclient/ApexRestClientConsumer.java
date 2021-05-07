/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.parameters.carriertechnology.RestPluginCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events from a REST server.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRestClientConsumer extends ApexPluginsEventConsumer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestClientConsumer.class);

    // The amount of time to wait in milliseconds between checks that the consumer thread has stopped
    private static final long REST_CLIENT_WAIT_SLEEP_TIME = 50;

    // The REST parameters read from the parameter service
    private RestClientCarrierTechnologyParameters restConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The HTTP client that makes a REST call to get an input event for Apex
    private Client client;

    // The pattern for filtering status code
    private Pattern httpCodeFilterPattern = null;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the REST Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof RestClientCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified consumer properties are not applicable to REST client consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        restConsumerProperties =
                (RestClientCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        this.httpCodeFilterPattern = Pattern.compile(restConsumerProperties.getHttpCodeFilter());

        // Check if the HTTP method has been set
        if (restConsumerProperties.getHttpMethod() == null) {
            restConsumerProperties.setHttpMethod(RestPluginCarrierTechnologyParameters.HttpMethod.GET);
        }

        if (!RestPluginCarrierTechnologyParameters.HttpMethod.GET.equals(restConsumerProperties.getHttpMethod())) {
            final String errorMessage = "specified HTTP method of \"" + restConsumerProperties.getHttpMethod()
                    + "\" is invalid, only HTTP method \"GET\" "
                    + "is supported for event reception on REST client consumer (" + this.name + ")";
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
    public void run() {
        // The RequestRunner thread runs the get request for the event
        Thread requestRunnerThread = null;

        // The endless loop that receives events using REST calls
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            // Create a new request if one is not in progress
            if (requestRunnerThread == null || !requestRunnerThread.isAlive()) {
                requestRunnerThread = new Thread(new RequestRunner());
                requestRunnerThread.start();
            }

            ThreadUtilities.sleep(REST_CLIENT_WAIT_SLEEP_TIME);
        }

        client.close();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(REST_CLIENT_WAIT_SLEEP_TIME);
        }
    }

    /**
     * This class is used to start a thread for each request issued.
     *
     * @author Liam Fallon (liam.fallon@ericsson.com)
     */
    private class RequestRunner implements Runnable {
        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {
            try {
                final Response response = client.target(restConsumerProperties.getUrl()).request("application/json")
                        .headers(restConsumerProperties.getHttpHeadersAsMultivaluedMap()).get();

                // Match the return code
                Matcher isPass = httpCodeFilterPattern.matcher(String.valueOf(response.getStatus()));

                // Check that status code
                if (!isPass.matches()) {
                    final String errorMessage = "reception of event from URL \"" + restConsumerProperties.getUrl()
                            + "\" failed with status code " + response.getStatus() + " and message \""
                            + response.readEntity(String.class) + "\"";
                    LOGGER.warn(errorMessage);
                    throw new ApexEventRuntimeException(errorMessage);
                }

                // Get the event we received
                final String eventJsonString = response.readEntity(String.class);

                // Check there is content
                if (StringUtils.isBlank(eventJsonString)) {
                    final String errorMessage =
                            "received an empty event from URL \"" + restConsumerProperties.getUrl() + "\"";
                    throw new ApexEventRuntimeException(errorMessage);
                }

                // Send the event into Apex
                eventReceiver.receiveEvent(new Properties(), eventJsonString);
            } catch (final Exception e) {
                LOGGER.warn("error receiving events on thread {}", consumerThread.getName(), e);
            }
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
