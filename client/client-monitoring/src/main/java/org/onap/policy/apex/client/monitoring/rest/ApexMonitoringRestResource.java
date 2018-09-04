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

package org.onap.policy.apex.client.monitoring.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.onap.policy.apex.core.deployment.ApexDeploymentException;
import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The class represents the root resource exposed at the base URL<br>
 * The url to access this resource would be in the form {@code <baseURL>/rest/....} <br>
 * For example: a GET request to the following URL
 * {@code http://localhost:18989/apexservices/rest/?hostName=localhost&port=12345}
 *
 * <b>Note:</b> An allocated {@code hostName} and {@code port} query parameter must be included in all requests.
 * Datasets for different {@code hostName} are completely isolated from one another.
 *
 */
@Path("monitoring/")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })

public class ApexMonitoringRestResource {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexMonitoringRestResource.class);

    // Set the maximum number of stored data entries to be stored for each engine
    private static final int maxCachedEntries = 50;

    // Set up a map separated by host and engine for the data
    private static final HashMap<String, HashMap<String, List<Counter>>> cache =
            new HashMap<String, HashMap<String, List<Counter>>>();

    // Set up a map separated by host for storing the state of periodic events
    private static final HashMap<String, Boolean> periodicEventsStateCache = new HashMap<String, Boolean>();

    /**
     * Constructor, a new resource director is created for each request.
     */
    public ApexMonitoringRestResource() {}

    /**
     * Query the engine service for data.
     *
     * @param hostName the host name of the engine service to connect to.
     * @param port the port number of the engine service to connect to.
     * @return a Response object containing the engines service, status and context data in JSON
     */
    @GET
    public Response createSession(@QueryParam("hostName") final String hostName, @QueryParam("port") final int port) {
        final Gson gson = new Gson();
        final String host = hostName + ":" + port;
        final EngineServiceFacade engineServiceFacade = new EngineServiceFacade(hostName, port);

        try {
            engineServiceFacade.init();
        } catch (final ApexDeploymentException e) {
            final String errorMessage = "Error connecting to Apex Engine Service at " + host;
            LOGGER.warn(errorMessage + "<br>", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + e.getMessage())
                    .build();
        }

        final JsonObject responseObject = new JsonObject();

        // Engine Service data
        responseObject.addProperty("engine_id", engineServiceFacade.getKey().getId());
        responseObject.addProperty("model_id",
                engineServiceFacade.getApexModelKey() != null ? engineServiceFacade.getApexModelKey().getId()
                        : "Not Set");
        responseObject.addProperty("server", hostName);
        responseObject.addProperty("port", Integer.toString(port));
        responseObject.addProperty("periodic_events", getPeriodicEventsState(host));

        // Engine Status data
        final JsonArray engineStatusList = new JsonArray();

        for (final AxArtifactKey engineKey : engineServiceFacade.getEngineKeyArray()) {
            try {
                final JsonObject engineStatusObject = new JsonObject();
                final AxEngineModel axEngineModel = engineServiceFacade.getEngineStatus(engineKey);
                engineStatusObject.addProperty("timestamp", axEngineModel.getTimeStampString());
                engineStatusObject.addProperty("id", engineKey.getId());
                engineStatusObject.addProperty("status", axEngineModel.getState().toString());
                engineStatusObject.addProperty("last_message", axEngineModel.getStats().getTimeStampString());
                engineStatusObject.addProperty("up_time", axEngineModel.getStats().getUpTime() / 1000L);
                engineStatusObject.addProperty("policy_executions", axEngineModel.getStats().getEventCount());
                engineStatusObject.addProperty("last_policy_duration",
                        gson.toJson(
                                getValuesFromCache(host, engineKey.getId() + "_last_policy_duration",
                                        axEngineModel.getTimestamp(), axEngineModel.getStats().getLastExecutionTime()),
                                List.class));
                engineStatusObject.addProperty("average_policy_duration",
                        gson.toJson(getValuesFromCache(host, engineKey.getId() + "_average_policy_duration",
                                axEngineModel.getTimestamp(),
                                (long) axEngineModel.getStats().getAverageExecutionTime()), List.class));
                engineStatusList.add(engineStatusObject);
            } catch (final ApexException e) {
                LOGGER.warn("Error getting status of engine with ID " + engineKey.getId() + "<br>", e);
            }
        }
        responseObject.add("status", engineStatusList);

        // Engine context data
        final JsonArray engineContextList = new JsonArray();
        for (final AxArtifactKey engineKey : engineServiceFacade.getEngineKeyArray()) {
            try {
                final String engineInfo = engineServiceFacade.getEngineInfo(engineKey);
                if (engineInfo != null && !engineInfo.trim().isEmpty()) {
                    final JsonObject engineContextObject = new JsonObject();
                    engineContextObject.addProperty("id", engineKey.getId());
                    engineContextObject.addProperty("engine_info", engineInfo);
                    engineContextList.add(engineContextObject);
                }
            } catch (final ApexException e) {
                LOGGER.warn("Error getting runtime information of engine with ID " + engineKey.getId() + "<br>", e);
            }
        }
        responseObject.add("context", engineContextList);

        return Response.ok(responseObject.toString(), MediaType.APPLICATION_JSON).build();
    }

    /**
     * Start/Stop and Apex engine.
     *
     * @param hostName the host name of the engine service to connect to.
     * @param port the port number of the engine service to connect to.
     * @param engineId the id of the engine to be started/stopped.
     * @param startStop the parameter to start/stop the engine. Expects either "Start" or "Stop"
     * @return a Response object of type 200
     */
    @GET
    @Path("startstop/")
    public Response startStop(@QueryParam("hostName") final String hostName, @QueryParam("port") final int port,
            @QueryParam("engineId") final String engineId, @QueryParam("startstop") final String startStop) {
        final EngineServiceFacade engineServiceFacade = new EngineServiceFacade(hostName, port);

        try {
            engineServiceFacade.init();
        } catch (final ApexDeploymentException e) {
            final String errorMessage = "Error connecting to Apex Engine Service at " + hostName + ":" + port;
            LOGGER.warn(errorMessage + "<br>", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + e.getMessage())
                    .build();
        }

        try {
            final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
            parameterMap.put("hostname", new String[] { hostName });
            parameterMap.put("port", new String[] { Integer.toString(port) });
            parameterMap.put("AxArtifactKey#" + engineId, new String[] { startStop });
            final AxArtifactKey engineKey = ParameterCheck.getEngineKey(parameterMap);
            if (startStop.equals("Start")) {
                engineServiceFacade.startEngine(engineKey);
            } else if (startStop.equals("Stop")) {
                engineServiceFacade.stopEngine(engineKey);
            }
        } catch (final Exception e) {
            final String errorMessage = "Error calling " + startStop + " on Apex Engine: " + engineId;
            LOGGER.warn(errorMessage + "<br>", e);
            final StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + sw.toString())
                    .build();
        }

        return Response.ok("{}").build();
    }

    /**
     * Start/Stop and Apex engine.
     *
     * @param hostName the host name of the engine service to connect to.
     * @param port the port number of the engine service to connect to.
     * @param engineId the id of the engine to be started/stopped.
     * @param startStop the parameter to start/stop the engine. Expects either "Start" or "Stop"
     * @param period the time between each event in milliseconds
     * @return a Response object of type 200
     */
    @GET
    @Path("periodiceventstartstop/")
    public Response periodiceventStartStop(@QueryParam("hostName") final String hostName,
            @QueryParam("port") final int port, @QueryParam("engineId") final String engineId,
            @QueryParam("startstop") final String startStop, @QueryParam("period") final long period) {
        final EngineServiceFacade engineServiceFacade = new EngineServiceFacade(hostName, port);
        final String host = hostName + ":" + port;
        try {
            engineServiceFacade.init();
            final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
            parameterMap.put("hostname", new String[] { hostName });
            parameterMap.put("port", new String[] { Integer.toString(port) });
            parameterMap.put("AxArtifactKey#" + engineId, new String[] { startStop });
            parameterMap.put("period", new String[] { Long.toString(period) });
            final AxArtifactKey engineKey = ParameterCheck.getEngineKey(parameterMap);
            if (startStop.equals("Start")) {
                engineServiceFacade.startPerioidicEvents(engineKey, period);
                setPeriodicEventsState(host, true);
            } else if (startStop.equals("Stop")) {
                engineServiceFacade.stopPerioidicEvents(engineKey);
                setPeriodicEventsState(host, false);
            }
        } catch (final ApexDeploymentException e) {
            final String errorMessage = "Error connecting to Apex Engine Service at " + host;
            LOGGER.warn(errorMessage + "<br>", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorMessage + "\n" + e.getMessage())
                    .build();
        }

        return Response.ok("{}").build();
    }

    /**
     * Check if periodic events are running.
     *
     * @param host the engine's host url
     * @return a boolean stating if periodic events are running for a given host
     */
    private Boolean getPeriodicEventsState(final String host) {
        return periodicEventsStateCache.containsKey(host) ? periodicEventsStateCache.get(host) : false;
    }

    /**
     * Sets the state of periodic events for a host.
     *
     * @param host the engine's host url
     * @param boolean that states if periodic events have been started or stopped
     */
    private void setPeriodicEventsState(final String host, final Boolean isRunning) {
        periodicEventsStateCache.put(host, isRunning);
    }

    /**
     * This method takes in the latest data entry for an engine, adds it to an existing data set and returns the full
     * map for that host and engine.
     *
     * @param host the engine's host url
     * @param id the engines id
     * @param timestamp the timestamp of the latest data entry
     * @param latestValue the value of the latest data entry
     * @return a list of {@code Counter} objects for that engine
     */
    private List<Counter> getValuesFromCache(final String host, final String id, final long timestamp,
            final long latestValue) {
        SlidingWindowList<Counter> valueList;

        if (!cache.containsKey(host)) {
            cache.put(host, new HashMap<String, List<Counter>>());
        }

        if (cache.get(host).containsKey(id)) {
            valueList = (SlidingWindowList<Counter>) cache.get(host).get(id);
        } else {
            valueList = new SlidingWindowList<Counter>(maxCachedEntries);
        }
        valueList.add(new Counter(timestamp, latestValue));

        cache.get(host).put(id, valueList);

        return valueList;
    }

    /**
     * A list of values that uses a FIFO sliding window of a fixed size.
     */
    public class SlidingWindowList<V> extends LinkedList<V> {
        private static final long serialVersionUID = -7187277916025957447L;

        private final int maxEntries;

        public SlidingWindowList(final int maxEntries) {
            this.maxEntries = maxEntries;
        }

        @Override
        public boolean add(final V elm) {
            if (this.size() > (maxEntries - 1)) {
                this.removeFirst();
            }
            return super.add(elm);
        }

    }

    /**
     * A class used to storing a single data entry for an engine.
     */
    public class Counter {
        private long timestamp;
        private long value;

        public Counter(final long timestamp, final long value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getValue() {
            return value;
        }
    }
}
