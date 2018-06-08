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

import java.net.URI;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.onap.policy.apex.core.infrastructure.threading.ApplicationThreadFactory;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events from a REST server.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRestServerConsumer implements ApexEventConsumer, Runnable {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestServerConsumer.class);

    private static final String BASE_URI_TEMPLATE = "http://%s:%d/apex";

    // The amount of time to wait in milliseconds between checks that the consumer thread has stopped
    private static final long REST_SERVER_CONSUMER_WAIT_SLEEP_TIME = 50;

    // The REST parameters read from the parameter service
    private RESTServerCarrierTechnologyParameters restConsumerProperties;

    // The event receiver that will receive events from this consumer
    private ApexEventReceiver eventReceiver;

    // The name for this consumer
    private String name = null;

    // The peer references for this event handler
    private Map<EventHandlerPeeredMode, PeeredReference> peerReferenceMap = new EnumMap<>(EventHandlerPeeredMode.class);

    // The consumer thread and stopping flag
    private Thread consumerThread;
    private boolean stopOrderedFlag = false;

    // The local HTTP server to use for REST call reception if we are running a local Grizzly server
    private HttpServer server;

    // Holds the next identifier for event execution.
    private static AtomicLong nextExecutionID = new AtomicLong(0L);

    /**
     * Private utility to get the next candidate value for a Execution ID. This value will always be unique in a single
     * JVM
     *
     * @return the next candidate value for a Execution ID
     */
    private static synchronized long getNextExecutionID() {
        return nextExecutionID.getAndIncrement();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#init(java.lang.String,
     * org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters,
     * org.onap.policy.apex.service.engine.event.ApexEventReceiver)
     */
    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the REST Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof RESTServerCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified consumer properties are not applicable to REST Server consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }
        restConsumerProperties =
                (RESTServerCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

        // Check if we are in synchronous mode
        if (!consumerParameters.isPeeredMode(EventHandlerPeeredMode.SYNCHRONOUS)) {
            final String errorMessage =
                    "REST Server consumer (" + this.name + ") must run in synchronous mode with a REST Server producer";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // Check if we're in standalone mode
        if (restConsumerProperties.isStandalone()) {
            // Check if host and port are defined
            if (restConsumerProperties.getHost() == null || restConsumerProperties.getPort() == -1) {
                final String errorMessage =
                        "the parameters \"host\" and \"port\" must be defined for REST Server consumer (" + this.name
                                + ") in standalone mode";
                LOGGER.warn(errorMessage);
                throw new ApexEventException(errorMessage);
            }

            // Compose the URI for the standalone server
            final String baseURI = String.format(BASE_URI_TEMPLATE, restConsumerProperties.getHost(),
                    restConsumerProperties.getPort());

            // Instantiate the standalone server
            final ResourceConfig rc = new ResourceConfig(RestServerEndpoint.class);
            server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);

            while (!server.isStarted()) {
                ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);
            }
        }

        // Register this consumer with the REST server end point
        RestServerEndpoint.registerApexRestServerConsumer(this.name, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#start()
     */
    @Override
    public void start() {
        // Configure and start the event reception thread
        final String threadName = this.getClass().getName() + ":" + this.name;
        consumerThread = new ApplicationThreadFactory(threadName).newThread(this);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#getPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode)
     */
    @Override
    public PeeredReference getPeeredReference(final EventHandlerPeeredMode peeredMode) {
        return peerReferenceMap.get(peeredMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.service.engine.event.ApexEventConsumer#setPeeredReference(org.onap.policy.apex.service.
     * parameters. eventhandler.EventHandlerPeeredMode, org.onap.policy.apex.service.engine.event.PeeredReference)
     */
    @Override
    public void setPeeredReference(final EventHandlerPeeredMode peeredMode, final PeeredReference peeredReference) {
        peerReferenceMap.put(peeredMode, peeredReference);
    }

    /**
     * Receive an event for processing in Apex.
     *
     * @param event the event to receive
     * @return the response from Apex
     */
    public Response receiveEvent(final String event) {
        // Get an execution ID for the event
        final long executionId = getNextExecutionID();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(name + ": sending event " + name + '_' + executionId + " to Apex, event=" + event);
        }

        try {
            // Send the event into Apex
            eventReceiver.receiveEvent(executionId, event);
        } catch (final Exception e) {
            final String errorMessage = "error receiving events on event consumer " + name + ", " + e.getMessage();
            LOGGER.warn(errorMessage);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .entity("{'errorMessage', '" + errorMessage + "'}").build();
        }

        final SynchronousEventCache synchronousEventCache =
                (SynchronousEventCache) peerReferenceMap.get(EventHandlerPeeredMode.SYNCHRONOUS);
        // Wait until the event is in the cache of events sent to apex
        do {
            ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);
        } while (!synchronousEventCache.existsEventToApex(executionId));

        // Now wait for the reply or for the event to time put
        do {
            ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);

            // Check if we have received an answer from Apex
            if (synchronousEventCache.existsEventFromApex(executionId)) {
                // We have received a response event, read and remove the response event and remove the sent event from
                // the cache
                final Object responseEvent = synchronousEventCache.removeCachedEventFromApexIfExists(executionId);
                synchronousEventCache.removeCachedEventToApexIfExists(executionId);

                // Return the event as a response to the call
                return Response.status(Response.Status.OK.getStatusCode()).entity(responseEvent.toString()).build();
            }
        } while (synchronousEventCache.existsEventToApex(executionId));

        // The event timed out
        final String errorMessage = "processing of event on event consumer " + name + " timed out, event=" + event;
        LOGGER.warn(errorMessage);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .entity("{'errorMessage', '" + errorMessage + "'}").build();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // Keep the consumer thread alive until it is shut down. We do not currently do anything in the thread but may
        // do supervision in the future
        while (consumerThread.isAlive() && !stopOrderedFlag) {
            ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);
        }

        if (server != null) {
            server.shutdown();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.apps.uservice.consumer.ApexEventConsumer#stop()
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);
        }
    }
}
