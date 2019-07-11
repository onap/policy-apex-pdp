/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
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
 * This class implements an Apex event consumer that receives events from a REST server.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRestClientConsumer implements ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestClientConsumer.class);

    // The amount of time to wait in milliseconds between checks that the consumer thread has stopped
    private static final long REST_CLIENT_WAIT_SLEEP_TIME = 50;

    // The Key for property
    private static final String HTTP_CODE_STATUS = "HTTP_CODE_STATUS";

    // The REST parameters read from the parameter service
    private RestClientCarrierTechnologyParameters restConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The HTTP client that makes a REST call to get an input event for Apex
    private Client client;

    // The name for this consumer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    // The consumer thread and stopping flag
    private Thread consumerThread;
    private boolean stopOrderedFlag = false;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
        final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the REST Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof RestClientCarrierTechnologyParameters)) {
            final String errorMessage = "specified consumer properties are not applicable to REST client consumer ("
                + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        restConsumerProperties = (RestClientCarrierTechnologyParameters) consumerParameters
            .getCarrierTechnologyParameters();

        // Check if the HTTP method has been set
        if (restConsumerProperties.getHttpMethod() == null) {
            restConsumerProperties.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.GET);
        }

        if (!RestClientCarrierTechnologyParameters.HttpMethod.GET.equals(restConsumerProperties.getHttpMethod())) {
            final String errorMessage = "specified HTTP method of \"" + restConsumerProperties.getHttpMethod()
                + "\" is invalid, only HTTP method \"GET\" "
                + "is supported for event reception on REST client consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // check if http Return Code filter has been set
        if (restConsumerProperties.getHttpCodeFilter() == null) {
            restConsumerProperties.setHttpCodeFilter("[2][0-9][0-9]");
        }

        // Initialize the HTTP client
        client = ClientBuilder.newClient();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + ":" + this.name;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
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

                // Get the event we received
                final String eventJsonString = response.readEntity(String.class);

                // Check there is content
                if (eventJsonString == null || eventJsonString.trim().length() == 0) {
                    final String errorMessage = "received an empty event from URL \"" + restConsumerProperties.getUrl()
                        + "\"";
                    throw new ApexEventRuntimeException(errorMessage);
                }

                // Match the return code
                Matcher isPass = Pattern.compile(restConsumerProperties.getHttpCodeFilter())
                    .matcher(String.valueOf(response.getStatus()));

                // Check that status code
                if (!isPass.matches()) {
                    final String errorMessage = "received an invalid status code \"" + response.getStatus() +"\"";
                    LOGGER.warn(errorMessage);
                    throw new ApexEventRuntimeException(errorMessage);
                }

                // build a key and value property in excutionProperties
                Properties executionProperties = new Properties();
                executionProperties.put(HTTP_CODE_STATUS, response.getStatus());

                // Send the event into Apex
                eventReceiver.receiveEvent(executionProperties, eventJsonString);
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
