/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import com.google.gson.Gson;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.OutputEvent;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class is the REST end point for event simulator REST calls.
 */
@Path("/")
public class EventGeneratorEndpoint {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(EventGeneratorEndpoint.class);

    // Parameters for event generation
    private static EventGeneratorParameters parameters = new EventGeneratorParameters();

    // The map of event batches sent in the test
    private static ConcurrentHashMap<Integer, EventBatch> batchMap = new ConcurrentHashMap<>();

    // Flag indicating that event processing has finished
    private static boolean finished = false;

    // The current HTTP request
    private final Provider<Request> httpRequest;

    /**
     * Inject the HTTP request with a constructor.
     * @param httpRequest the current request
     */
    @Inject
    public EventGeneratorEndpoint(final Provider<Request> httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * Set the parameters for the end point.
     *
     * @param incomingParameters the new parameters
     */
    public static void setParameters(EventGeneratorParameters incomingParameters) {
        synchronized (parameters) {
            parameters = incomingParameters;
        }
    }

    /**
     * Get event generator statistics.
     *
     * @return the response
     */
    @Path("/Stats")
    @GET
    public Response serviceGetStats() {
        return Response.status(200).entity(new EventGeneratorStats(batchMap).getStatsAsJsonString()).build();
    }

    /**
     * Generate a single event.
     *
     * @return the event
     */
    @Path("/GetEvents")
    @GET
    public Response getEvents() {
        ThreadUtilities.sleep(parameters.getDelayBetweenBatches());

        // Check if event generation is finished
        if (isFinished()) {
            return Response.status(204).build();
        }

        // A batch count of 0 means to continue to handle events for ever
        if (parameters.getBatchCount() > 0 && batchMap.size() >= parameters.getBatchCount()) {
            setFinished(true);
            return Response.status(204).build();
        }

        EventBatch batch = new EventBatch(parameters.getBatchSize(), getApexClient());
        batchMap.put(batch.getBatchNumber(), batch);

        return Response.status(200).entity(batch.getBatchAsJsonString()).build();
    }

    /**
     * Get a single response to an event.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/PostEvent")
    @POST
    public Response postEventResponse(final String jsonString) {
        final OutputEvent outputEvent = new Gson().fromJson(jsonString, OutputEvent.class);

        EventBatch batch = batchMap.get(outputEvent.findBatchNumber());

        if (batch == null) {
            String errorMessage = "no input event found for received output event " + outputEvent;
            LOGGER.warn(errorMessage);
            return Response.status(409).build();
        }

        batch.handleResponse(outputEvent);
        return Response.status(200).build();
    }

    /**
     * Get the name, address, and port of the Apex client getting the events.
     *
     * @return the Apex client
     */
    private String getApexClient() {
        return httpRequest.get().getRemoteHost() + '(' + httpRequest.get().getRemoteAddr() + "):"
                        + httpRequest.get().getRemotePort();
    }

    /**
     * Get event generation statistics.
     * @return the statistics on event generation
     */
    protected static String getEventGenerationStats() {
        return new EventGeneratorStats(batchMap).getStatsAsJsonString();
    }

    /**
     * Clear event generation statistics.
     */
    protected static void clearEventGenerationStats() {
        batchMap.clear();
    }

    /**
     * Check if event generation has finished.
     * @return true if event generation has finished
     */
    protected static boolean isFinished() {
        return finished;
    }

    protected static void setFinished(boolean finished) {
        EventGeneratorEndpoint.finished = finished;
    }
}
