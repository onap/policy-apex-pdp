/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2022-2024 Nordix Foundation.
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

package org.onap.policy.apex.domains.onap.vcpe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.common.gson.InstantAsMillisTypeAdapter;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class AaiAndGuardSimEndpoint.
 */
@Path("/sim")
public class OnapVCpeSimEndpoint {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(OnapVCpeSimEndpoint.class);

    private static BlockingQueue<String> appcResponseQueue = new LinkedBlockingQueue<>();

    private static AtomicInteger guardMessagesReceived = new AtomicInteger();
    private static AtomicInteger postMessagesReceived = new AtomicInteger();
    private static AtomicInteger putMessagesReceived = new AtomicInteger();
    private static AtomicInteger statMessagesReceived = new AtomicInteger();
    private static AtomicInteger getMessagesReceived = new AtomicInteger();

    private static final Random randomDelayInc = new Random();

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantAsMillisTypeAdapter()).setPrettyPrinting().create();

    private static final AtomicInteger nextVnfId = new AtomicInteger(0);
    private static Boolean nextControlLoopMessageIsOnset = true;

    /**
     * Service get stats.
     *
     * @return the response
     */
    @Path("/pdp/api/Stats")
    @GET
    public Response serviceGetStats() {
        statMessagesReceived.incrementAndGet();
        String returnString = "{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": "
            + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}";

        return Response.status(200).entity(prettifyJsonString(returnString)).build();
    }

    /**
     * Service guard post request.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/pdp/api/getDecision")
    @POST
    public Response serviceGuardPostRequest(final String jsonString) {
        LOGGER.info("\n*** GUARD REQUEST START ***\n{}\n *** GUARD REQUEST END ***", jsonString);

        String target = jsonString.substring(jsonString.indexOf("00000000"));
        target = target.substring(0, target.indexOf('"'));

        int thisGuardMessageNumber = guardMessagesReceived.incrementAndGet();
        postMessagesReceived.incrementAndGet();

        String responseJsonString;
        if (thisGuardMessageNumber % 2 == 0) {
            responseJsonString = "{\"decision\": \"PERMIT\", \"details\": \"Decision Permit. OK!\"}";
        } else {
            responseJsonString = "{\"decision\": \"DENY\", \"details\": \"Decision Denied. NOK :-(\"}";
        }

        responseJsonString = prettifyJsonString(responseJsonString);

        LOGGER.info("\n*** GUARD RESPONSE START ***\n{}\n{}\n*** GUARD RESPONSE END ***", target, responseJsonString);

        return Response.status(200).entity(responseJsonString).build();
    }

    /**
     * AAI named query search request.
     * http://localhost:54321/aai/v16/search/nodes-query?search-node-type=vserver&filter=vserver-name:EQUALS:
     *
     * @param searchNodeType the node type to search for
     * @param filter         the filter to apply in the search
     * @return the response
     * @throws IOException on I/O errors
     */
    @Path("aai/v16/search/nodes-query")
    @GET
    public Response aaiNamedQuerySearchRequest(@QueryParam("search-node-type") final String searchNodeType,
                                               @QueryParam("filter") final String filter) throws IOException {
        getMessagesReceived.incrementAndGet();

        LOGGER.info("\n*** AAI NODE QUERY GET START ***\nsearchNodeType={}\nfilter={}\n *** AAI REQUEST END ***",
            searchNodeType, filter);

        String adjustedVserverUuid =
            "b4fe00ac-1da6-4b00-ac0d-8e8300db" + String.format("%04d", nextVnfId.getAndIncrement());

        String responseJsonString =
            TextFileUtils.getTextFileAsString("src/test/resources/aai/SearchNodeTypeResponse.json")
                .replaceAll("b4fe00ac-1da6-4b00-ac0d-8e8300db0007", adjustedVserverUuid);

        responseJsonString = prettifyJsonString(responseJsonString);

        LOGGER.info("\n*** AAI RESPONSE START ***\n{}\n *** AAI RESPONSE END ***", responseJsonString);

        return Response.status(200).entity(responseJsonString).build();
    }

    /**
     * AAI named query request on a particular resource.
     * http://localhost:54321/OnapVCpeSim/sim/aai/v16/query?format=resource
     *
     * @param format     the format of the request
     * @param jsonString the body of the request
     * @return the response
     * @throws IOException on I/O errors
     */
    @Path("aai/v16/query")
    @PUT
    public Response aaiNamedQueryResourceRequest(@QueryParam("format") final String format, final String jsonString)
        throws IOException {
        putMessagesReceived.incrementAndGet();

        LOGGER.info("\n*** AAI NODE RESOURCE POST QUERY START ***\\nformat={}\njson={}\n *** AAI REQUEST END ***",
            format, jsonString);

        int beginIndex =
            jsonString.indexOf("b4fe00ac-1da6-4b00-ac0d-8e8300db") + "b4fe00ac-1da6-4b00-ac0d-8e8300db".length();
        String nextVnfIdUrlEnding = jsonString.substring(beginIndex, beginIndex + 4);
        String responseJsonString = TextFileUtils.getTextFileAsString("src/test/resources/aai/NodeQueryResponse.json")
            .replaceAll("bbb3cefd-01c8-413c-9bdd-2b92f9ca3d38",
                "00000000-0000-0000-0000-00000000" + nextVnfIdUrlEnding);

        responseJsonString = prettifyJsonString(responseJsonString);

        LOGGER.info("\n*** AAI RESPONSE START ***\n{}\n *** AAI RESPONSE END ***", responseJsonString);

        return Response.status(200).entity(responseJsonString).build();
    }

    /**
     * DCAE input of events (simulation of DMaaP).
     *
     * @param timeout the timeout to wait for
     * @return the response
     * @throws IOException on I/O errors
     */
    @Path("events/unauthenticated.DCAE_CL_OUTPUT/APEX/1")
    @GET
    public Response dcaeClOutput(@QueryParam("timeout") final int timeout) throws IOException {
        getMessagesReceived.incrementAndGet();

        ThreadUtilities.sleep(timeout - 500);

        if (nextControlLoopMessageIsOnset) {
            nextControlLoopMessageIsOnset = false;

            String clOnsetEvent = TextFileUtils
                .getTextFileAsString("src/main/resources/examples/events/ONAPvCPEStandalone/CLOnsetEvent.json");
            LOGGER.info("\n*** CONTROL LOOP ONSET START ***\n{}\n *** CONTROL LOOP ONSET END ***", clOnsetEvent);

            return Response.status(200).entity(clOnsetEvent).build();
        } else {
            nextControlLoopMessageIsOnset = true;

            String clAbatedEvent = TextFileUtils
                .getTextFileAsString("src/main/resources/examples/events/ONAPvCPEStandalone/CLAbatedEvent.json");
            LOGGER.info("\n*** CONTROL LOOP ABATED START ***\n{}\n *** CONTROL LOOP ABATED END ***", clAbatedEvent);

            return Response.status(200).entity(clAbatedEvent).build();
        }
    }

    /**
     * APPC response events (simulation of DMaaP).
     *
     * @param timeout the timeout to wait for
     * @return the response
     * @throws InterruptedException on queue interrupts
     */
    @Path("events/APPC_LCM_WRITE/APEX/1")
    @GET
    public Response appcResponseOutput(@QueryParam("timeout") final int timeout) throws InterruptedException {
        getMessagesReceived.incrementAndGet();

        int timeLeft = timeout - 500;

        do {
            String appcResponse = appcResponseQueue.poll(100, TimeUnit.MILLISECONDS);

            if (appcResponse != null) {
                LOGGER.info("\n*** CONTROLLER RESPONSE START ***");
                LOGGER.info("\n*** CONTROLLER RESPONSE END ***");

                return Response.status(200).entity(appcResponse).build();
            }
            timeLeft -= 100;
        } while (timeLeft > 0);

        return Response.status(200).build();
    }

    /**
     * Post to Policy management log (Simulation of DMaaP).
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/events/POLICY_CL_MGT")
    @POST
    public Response policyLogRequest(final String jsonString) {
        postMessagesReceived.incrementAndGet();

        String logJsonString = prettifyJsonString(jsonString);

        LOGGER.info("\n*** POLICY LOG ENTRY START ***\n{}\n *** POLICY LOG ENTRY END ***", logJsonString);

        return Response.status(200).build();
    }

    /**
     * Post to APPC LCM (Simulation of DMaaP).
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/events/APPC-LCM-READ")
    @POST
    public Response appcRequest(final String jsonString) {
        postMessagesReceived.incrementAndGet();

        String appcJsonString = prettifyJsonString(jsonString);

        LOGGER.info("\n*** CONTROLLER REQUEST START ***\n{}\n *** CONTROLLER REQUEST END ***", appcJsonString);

        new AppcResponseCreator(appcResponseQueue, appcJsonString, 10000 + randomDelayInc.nextInt(10000));

        return Response.status(200).build();
    }

    /**
     * Post to BLACK WHITE LIST READ (Simulation of DMaaP).
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/events/BLACK-WHITE-LIST-READ")
    @POST
    public Response blackWhiteListRead(final String jsonString) {
        postMessagesReceived.incrementAndGet();

        String bwJsonString = prettifyJsonString(jsonString);

        LOGGER.info("\n*** BLACK WHITE LIST START ***\n{}\n *** BLACK WHITE LIST END ***", bwJsonString);

        return Response.status(200).build();
    }

    /**
     * Service get event.
     *
     * @return the response
     */
    @Path("/event/GetEvent")
    @GET
    public Response serviceGetEvent() {
        final Random rand = new Random();
        final int nextMatchCase = rand.nextInt(4);
        final String nextEventName = "Event0" + rand.nextInt(2) + "00";

        final String eventString = "{\n" + "\"nameSpace\": \"org.onap.policy.apex.sample.events\",\n" + "\"name\": \""
            + nextEventName + "\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_" + getMessagesReceived
            + "\",\n" + "\"target\": \"apex\",\n" + "\"TestSlogan\": \"Test slogan for External Event0\",\n"
            + "\"TestMatchCase\": " + nextMatchCase + ",\n" + "\"TestTimestamp\": " + System.currentTimeMillis()
            + ",\n" + "\"TestTemperature\": 9080.866\n" + "}";

        getMessagesReceived.incrementAndGet();

        return Response.status(200).entity(eventString).build();
    }

    /**
     * Service get empty event.
     *
     * @return the response
     */
    @Path("/event/GetEmptyEvent")
    @GET
    public Response serviceGetEmptyEvent() {
        return Response.status(200).build();
    }

    /**
     * Service get event bad response.
     *
     * @return the response
     */
    @Path("/event/GetEventBadResponse")
    @GET
    public Response serviceGetEventBadResponse() {
        return Response.status(400).build();
    }

    /**
     * Service post request.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/event/PostEvent")
    @POST
    public Response servicePostRequest(final String jsonString) {
        postMessagesReceived.incrementAndGet();

        @SuppressWarnings("unchecked") final Map<String, Object> jsonMap = gson.fromJson(jsonString, Map.class);
        assertTrue(jsonMap.containsKey("name"));
        assertEquals("0.0.1", jsonMap.get("version"));
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Act", jsonMap.get("source"));
        assertEquals("Outside", jsonMap.get("target"));

        return Response.status(200).entity("{\"GET\": , " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived
            + ",\"POST\": , " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}").build();
    }

    /**
     * Service post request bad response.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/event/PostEventBadResponse")
    @POST
    public Response servicePostRequestBadResponse(final String jsonString) {
        return Response.status(400).build();
    }

    /**
     * Service put request.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/event/PutEvent")
    @PUT
    public Response servicePutRequest(final String jsonString) {
        putMessagesReceived.incrementAndGet();

        @SuppressWarnings("unchecked") final Map<String, Object> jsonMap = gson.fromJson(jsonString, Map.class);
        assertTrue(jsonMap.containsKey("name"));
        assertEquals("0.0.1", jsonMap.get("version"));
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Act", jsonMap.get("source"));
        assertEquals("Outside", jsonMap.get("target"));

        return Response.status(200).entity("{\"GET\": , " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived
            + ",\"POST\": , " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}").build();
    }

    private static String prettifyJsonString(final String uglyJsonString) {
        JsonElement je = JsonParser.parseString(uglyJsonString);
        return gson.toJson(je);
    }
}
