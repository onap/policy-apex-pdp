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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
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
 * This class implements an Apex event consumer that issues a REST request and returns the REST response to APEX as an
 * event.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRestRequestorConsumer implements ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestRequestorConsumer.class);

    // The amount of time to wait in milliseconds between checks that the consumer thread has
    // stopped
    private static final long REST_REQUESTOR_WAIT_SLEEP_TIME = 50;

    // The Key for property
    private static final String HTTP_CODE_STATUS = "HTTP_CODE_STATUS";

    // The REST parameters read from the parameter service
    private RestRequestorCarrierTechnologyParameters restConsumerProperties;

    // The timeout for REST requests
    private long restRequestTimeout = RestRequestorCarrierTechnologyParameters.DEFAULT_REST_REQUEST_TIMEOUT;

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

    // Temporary request holder for incoming REST requests
    private final BlockingQueue<ApexRestRequest> incomingRestRequestQueue = new LinkedBlockingQueue<>();

    // Map of ongoing REST request threads indexed by the time they started at
    private final Map<ApexRestRequest, RestRequestRunner> ongoingRestRequestMap = new ConcurrentHashMap<>();

    // The number of events received to date
    private Object eventsReceivedLock = new Object();
    private Integer eventsReceived = 0;

    // The number of the next request runner thread
    private static long nextRequestRunnerThreadNo = 0;

    private String untaggedUrl = null;

    // The pattern for filtering status code
    private Pattern httpCodeFilterPattern = null;

    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the REST Properties
        if (!(consumerParameters
                .getCarrierTechnologyParameters() instanceof RestRequestorCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified consumer properties are not applicable to REST Requestor consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        restConsumerProperties =
                (RestRequestorCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        // Check if we are in peered mode
        if (!consumerParameters.isPeeredMode(EventHandlerPeeredMode.REQUESTOR)) {
            final String errorMessage = "REST Requestor consumer (" + this.name
                    + ") must run in peered requestor mode with a REST Requestor producer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check if the HTTP method has been set
        if (restConsumerProperties.getHttpMethod() == null) {
            restConsumerProperties
                    .setHttpMethod(RestRequestorCarrierTechnologyParameters.DEFAULT_REQUESTOR_HTTP_METHOD);
        }

        // Check if the HTTP URL has been set
        if (restConsumerProperties.getUrl() == null) {
            final String errorMessage = "no URL has been specified on REST Requestor consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check if the HTTP URL is valid
        try {
            new URL(restConsumerProperties.getUrl());
        } catch (final Exception e) {
            final String errorMessage = "invalid URL has been specified on REST Requestor consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage, e);
        }

        this.httpCodeFilterPattern = Pattern.compile(restConsumerProperties.getHttpCodeFilter());

        // Set the requestor timeout
        if (consumerParameters.getPeerTimeout(EventHandlerPeeredMode.REQUESTOR) != 0) {
            restRequestTimeout = consumerParameters.getPeerTimeout(EventHandlerPeeredMode.REQUESTOR);
        }

        // Check if HTTP headers has been set
        if (restConsumerProperties.checkHttpHeadersSet()) {
            LOGGER.debug("REST Requestor consumer has http headers ({}): {}", this.name,
                    Arrays.deepToString(restConsumerProperties.getHttpHeaders()));
        }

        // Initialize the HTTP client
        client = ClientBuilder.newClient();
    }

    /**
     * Receive an incoming REST request from the peered REST Requestor producer and queue it.
     *
     * @param restRequest the incoming rest request to queue
     * @throws ApexEventRuntimeException on queueing errors
     */
    public void processRestRequest(final ApexRestRequest restRequest) {
        // Push the event onto the queue for handling
        try {
            incomingRestRequestQueue.add(restRequest);
        } catch (final Exception requestException) {
            final String errorMessage =
                    "could not queue request \"" + restRequest + "\" on REST Requestor consumer (" + this.name + ")";
            LOGGER.warn(errorMessage, requestException);
            throw new ApexEventRuntimeException(errorMessage);
        }
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
     * Get the number of events received to date.
     *
     * @return the number of events received
     */
    public int getEventsReceived() {
        return eventsReceived;
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
        // The endless loop that receives events using REST calls
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            try {
                // Take the next event from the queue
                final ApexRestRequest restRequest =
                        incomingRestRequestQueue.poll(REST_REQUESTOR_WAIT_SLEEP_TIME, TimeUnit.MILLISECONDS);
                if (restRequest == null) {
                    // Poll timed out, check for request timeouts
                    timeoutExpiredRequests();
                    continue;
                }

                Properties inputExecutionProperties = restRequest.getExecutionProperties();
                untaggedUrl = restConsumerProperties.getUrl();
                if (inputExecutionProperties != null) {
                    Set<String> names = restConsumerProperties.getKeysFromUrl();
                    Set<String> inputProperty = inputExecutionProperties.stringPropertyNames();

                    names.stream().map(Optional::of).forEach(op ->
                        op.filter(inputProperty::contains)
                                .orElseThrow(() -> new ApexEventRuntimeException(
                                        "key\"" + op.get() + "\"specified on url \"" + restConsumerProperties.getUrl()
                                                + "\"not found in execution properties passed by the current policy")));

                    untaggedUrl = names.stream().reduce(untaggedUrl,
                        (acc, str) -> acc.replace("{" + str + "}", (String) inputExecutionProperties.get(str)));
                }

                // Set the time stamp of the REST request
                restRequest.setTimestamp(System.currentTimeMillis());

                // Create a thread to process the REST request and place it on the map of ongoing
                // requests
                final RestRequestRunner restRequestRunner = new RestRequestRunner(restRequest);
                ongoingRestRequestMap.put(restRequest, restRequestRunner);

                // Start execution of the request
                final Thread restRequestRunnerThread = new Thread(restRequestRunner);
                restRequestRunnerThread.setName("RestRequestRunner_" + nextRequestRunnerThreadNo);
                restRequestRunnerThread.start();
            } catch (final InterruptedException e) {
                LOGGER.debug("Thread interrupted, Reason {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        client.close();
    }

    /**
     * This method times out REST requests that have expired.
     */
    private void timeoutExpiredRequests() {
        // Hold a list of timed out requests
        final List<ApexRestRequest> timedoutRequestList = new ArrayList<>();

        // Check for timeouts
        for (final Entry<ApexRestRequest, RestRequestRunner> requestEntry : ongoingRestRequestMap.entrySet()) {
            if (System.currentTimeMillis() - requestEntry.getKey().getTimestamp() > restRequestTimeout) {
                requestEntry.getValue().stop();
                timedoutRequestList.add(requestEntry.getKey());
            }
        }

        // Interrupt timed out requests and remove them from the ongoing map
        for (final ApexRestRequest timedoutRequest : timedoutRequestList) {
            final String errorMessage =
                    "REST Requestor consumer (" + this.name + "), REST request timed out: " + timedoutRequest;
            LOGGER.warn(errorMessage);

            ongoingRestRequestMap.remove(timedoutRequest);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(REST_REQUESTOR_WAIT_SLEEP_TIME);
        }
    }

    /**
     * This class is used to start a thread for each request issued.
     *
     * @author Liam Fallon (liam.fallon@ericsson.com)
     */
    private class RestRequestRunner implements Runnable {
        private static final String APPLICATION_JSON = "application/json";

        // The REST request being processed by this thread
        private final ApexRestRequest request;

        // The thread executing the REST request
        private Thread restRequestThread;

        /**
         * Constructor, initialise the request runner with the request.
         *
         * @param request the request this runner will issue
         */
        private RestRequestRunner(final ApexRestRequest request) {
            this.request = request;
        }

        /**
         * {@inheritDoc}.
         */
        @Override
        public void run() {
            // Get the thread for the request
            restRequestThread = Thread.currentThread();

            try {
                // Execute the REST request
                final Response response = sendEventAsRestRequest(untaggedUrl);

                // Match the return code
                Matcher isPass = httpCodeFilterPattern.matcher(String.valueOf(response.getStatus()));

                // Check that the request worked
                if (!isPass.matches()) {
                    final String errorMessage = "reception of event from URL \"" + restConsumerProperties.getUrl()
                            + "\" failed with status code " + response.getStatus() + " and message \""
                            + response.readEntity(String.class) + "\"";
                    throw new ApexEventRuntimeException(errorMessage);
                }

                // Get the event we received
                final String eventJsonString = response.readEntity(String.class);

                // Check there is content
                if (StringUtils.isBlank(eventJsonString)) {
                    final String errorMessage =
                            "received an empty response to \"" + request + "\" from URL \"" + untaggedUrl + "\"";
                    throw new ApexEventRuntimeException(errorMessage);
                }

                // build a key and value property in excutionProperties
                Properties executionProperties = new Properties();
                executionProperties.put(HTTP_CODE_STATUS, response.getStatus());

                // Send the event into Apex
                eventReceiver.receiveEvent(request.getExecutionId(), executionProperties, eventJsonString);

                synchronized (eventsReceivedLock) {
                    eventsReceived++;
                }
            } catch (final Exception e) {
                LOGGER.warn("error receiving events on thread {}", consumerThread.getName(), e);
            } finally {
                // Remove the request from the map of ongoing requests
                ongoingRestRequestMap.remove(request);
            }
        }

        /**
         * Stop the REST request.
         */
        private void stop() {
            restRequestThread.interrupt();
        }

        /**
         * Execute the REST request.
         *
         *
         * @return the response to the REST request
         */
        public Response sendEventAsRestRequest(String untaggedUrl) {
            Builder headers = client.target(untaggedUrl).request(APPLICATION_JSON)
                    .headers(restConsumerProperties.getHttpHeadersAsMultivaluedMap());
            switch (restConsumerProperties.getHttpMethod()) {
                case GET:
                    return headers.get();

                case PUT:
                    return headers.put(Entity.json(request.getEvent()));

                case POST:
                    return headers.post(Entity.json(request.getEvent()));

                case DELETE:
                    return headers.delete();

                default:
                    break;
            }

            return null;
        }
    }
}
