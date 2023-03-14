/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2023 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.ApexPluginsEventConsumer;
import org.onap.policy.apex.service.engine.event.SynchronousEventCache;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements an Apex event consumer that receives events from a REST server.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexRestServerConsumer extends ApexPluginsEventConsumer {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexRestServerConsumer.class);

    // The amount of time to wait in milliseconds between checks that the consumer thread has stopped
    private static final long REST_SERVER_CONSUMER_WAIT_SLEEP_TIME = 50;

    // The event receiver that will receive events from this consumer
    @Setter(AccessLevel.PACKAGE)
    private ApexEventReceiver eventReceiver;

    // The local HTTP server to use for REST call reception if we are running a local Grizzly server
    @Getter(AccessLevel.PACKAGE)
    private HttpServletServer server;

    // Holds the next identifier for event execution.
    private static AtomicLong nextExecutionID = new AtomicLong(0L);

    /**
     * Private utility to get the next candidate value for a Execution ID. This value will always be unique in a single
     * JVM
     *
     * @return the next candidate value for a Execution ID
     */
    private static synchronized long getNextExecutionId() {
        return nextExecutionID.getAndIncrement();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void init(final String consumerName, final EventHandlerParameters consumerParameters,
            final ApexEventReceiver incomingEventReceiver) throws ApexEventException {
        this.eventReceiver = incomingEventReceiver;
        this.name = consumerName;

        // Check and get the REST Properties
        if (!(consumerParameters.getCarrierTechnologyParameters() instanceof RestServerCarrierTechnologyParameters)) {
            final String errorMessage =
                    "specified consumer properties are not applicable to REST Server consumer (" + this.name + ")";
            LOGGER.warn(errorMessage);
            throw new ApexEventException(errorMessage);
        }

        // The REST parameters read from the parameter service
        RestServerCarrierTechnologyParameters restConsumerProperties =
                (RestServerCarrierTechnologyParameters) consumerParameters.getCarrierTechnologyParameters();

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

            // Instantiate the standalone server
            LOGGER.info("Creating the Apex Rest Server");
            createServer(restConsumerProperties);
            server.start();
            while (!server.isAlive()) {
                ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);
            }
        }

        // Register this consumer with the REST server end point
        RestServerEndpoint.registerApexRestServerConsumer(this.name, this);
    }

    private void createServer(RestServerCarrierTechnologyParameters restConsumerProperties) {

        server = HttpServletServerFactoryInstance.getServerFactory().build(
            restConsumerProperties.getName(),
            restConsumerProperties.isHttps(),
            restConsumerProperties.getHost(),
            restConsumerProperties.getPort(),
            restConsumerProperties.isSniHostCheck(),
            null,
            true,
            false
        );

        if (restConsumerProperties.isAaf()) {
            server.addFilterClass(null, ApexRestServerAafFilter.class.getName());
        }
        server.addServletClass(null, RestServerEndpoint.class.getName());
        server.addServletClass(null, AccessControlFilter.class.getName());
        server.setSerializationProvider(GsonMessageBodyHandler.class.getName());
        if (null != restConsumerProperties.getUserName()
            && null != restConsumerProperties.getPassword()) {
            server.setBasicAuthentication(restConsumerProperties.getUserName(),
                restConsumerProperties.getPassword(), null);
        }
    }

    /**
     * Receive an event for processing in Apex.
     *
     * @param event the event to receive
     * @return the response from Apex
     */
    public Response receiveEvent(final String event) {
        // Get an execution ID for the event
        final long executionId = getNextExecutionId();

        if (LOGGER.isDebugEnabled()) {
            String message = name + ": sending event " + name + '_' + executionId + " to Apex, event=" + event;
            LOGGER.debug(message);
        }

        try {
            // Send the event into Apex
            eventReceiver.receiveEvent(executionId, new Properties(), event);
        } catch (final Exception e) {
            final String errorMessage = "error receiving events on event consumer " + name;
            LOGGER.warn(errorMessage, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                    .entity("{'errorMessage', '" + errorMessage + ", " + e.getMessage() + "'}").build();
        }

        final var synchronousEventCache =
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

    /**
     * {@inheritDoc}.
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stop() {
        stopOrderedFlag = true;

        while (consumerThread.isAlive()) {
            ThreadUtilities.sleep(REST_SERVER_CONSUMER_WAIT_SLEEP_TIME);
        }
        if (server != null) {
            server.stop();
        }
    }
}
