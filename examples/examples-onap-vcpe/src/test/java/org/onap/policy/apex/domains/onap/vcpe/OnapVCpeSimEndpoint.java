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

package org.onap.policy.apex.domains.onap.vcpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.onap.policy.aai.AaiNqGenericVnf;
import org.onap.policy.aai.AaiNqInventoryResponseItem;
import org.onap.policy.aai.AaiNqRequest;
import org.onap.policy.aai.AaiNqResponse;
import org.onap.policy.aai.AaiNqVfModule;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
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

    /**
     * Service get stats.
     *
     * @return the response
     */
    @Path("/pdp/api/Stats")
    @GET
    public Response serviceGetStats() {
        statMessagesReceived.incrementAndGet();

        return Response.status(200).entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived
                        + ",\"POST\": " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}").build();
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
        LOGGER.info("\n*** GUARD REQUEST START ***\n" + jsonString + "\n *** GUARD REQUEST END ***");

        String target = jsonString.substring(jsonString.indexOf("b4fe00ac"));
        target = target.substring(0, target.indexOf('"'));

        int thisGuardMessageNumber = guardMessagesReceived.incrementAndGet();
        postMessagesReceived.incrementAndGet();

        String responseJsonString = null;
        if (thisGuardMessageNumber % 2 == 0) {
            responseJsonString = "{\"decision\": \"PERMIT\", \"details\": \"Decision Permit. OK!\"}";
        } else {
            responseJsonString = "{\"decision\": \"DENY\", \"details\": \"Decision Denied. NOK :-(\"}";
        }

        LOGGER.info("\n*** GUARD RESPONSE START ***\n" + target + "\n" + responseJsonString
                        + "\n*** GUARD RESPONSE END ***");

        return Response.status(200).entity(responseJsonString).build();
    }

    /**
     * AAI named query request.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("aai/search/named-query")
    @POST
    public Response aaiNamedQueryRequest(final String jsonString) {
        postMessagesReceived.incrementAndGet();

        LOGGER.info("\n*** AAI REQUEST START ***\n" + jsonString + "\n *** AAI REQUEST END ***");

        AaiNqRequest request = new Gson().fromJson(jsonString, AaiNqRequest.class);
        String vnfId = request.getInstanceFilters().getInstanceFilter().iterator().next().get("generic-vnf")
                        .get("vnf-id");
        String vnfSuffix = vnfId.substring(vnfId.length() - 4);

        AaiNqInventoryResponseItem responseItem = new AaiNqInventoryResponseItem();
        responseItem.setModelName("vCPE");

        AaiNqGenericVnf genericVnf = new AaiNqGenericVnf();
        genericVnf.setResourceVersion("1");
        genericVnf.setVnfName("vCPEInfraVNF" + vnfSuffix);
        genericVnf.setProvStatus("PREPROV");
        genericVnf.setIsClosedLoopDisabled(false);
        genericVnf.setVnfType("vCPEInfraService10/vCPEInfraService10 0");
        genericVnf.setInMaint(false);
        genericVnf.setServiceId("5585fd2c-ad0d-4050-b0cf-dfe4a03bd01f");
        genericVnf.setVnfId(vnfId);

        responseItem.setGenericVnf(genericVnf);

        AaiNqVfModule vfModule = new AaiNqVfModule();
        vfModule.setOrchestrationStatus("Created");

        responseItem.setVfModule(vfModule);

        AaiNqResponse response = new AaiNqResponse();
        response.getInventoryResponseItems().add(responseItem);

        String responseJsonString = new GsonBuilder().setPrettyPrinting().create().toJson(response);

        LOGGER.info("\n*** AAI RESPONSE START ***\n" + responseJsonString + "\n *** AAI RESPONSE END ***");

        return Response.status(200).entity(responseJsonString).build();
    }

    /**
     * DCAE input of events (simulation of DMaaP).
     *
     * @param timeout the timeout to wait for
     * @return the response
     */
    @Path("events/unauthenticated.DCAE_CL_OUTPUT/APEX/1")
    @GET
    public Response dcaeClOutput(@QueryParam("timeout") final int timeout) {
        getMessagesReceived.incrementAndGet();

        ThreadUtilities.sleep(timeout - 500);

        return Response.status(200).build();
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
                LOGGER.info("\n*** APPC RESPONSE START ***");
                System.err.println(appcResponse);
                LOGGER.info("\n*** APPC RESPONSE END ***");

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

        LOGGER.info("\n*** POLICY LOG ENTRY START ***\n" + jsonString + "\n *** POLICY LOG ENTRY END ***");

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

        LOGGER.info("\n*** APPC REQUEST START ***\n" + jsonString + "\n *** APPC REQUEST END ***");

        new AppcResponseCreator(appcResponseQueue, jsonString, 10000);

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
                        + nextEventName + "\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_"
                        + getMessagesReceived + "\",\n" + "\"target\": \"apex\",\n"
                        + "\"TestSlogan\": \"Test slogan for External Event0\",\n" + "\"TestMatchCase\": "
                        + nextMatchCase + ",\n" + "\"TestTimestamp\": " + System.currentTimeMillis() + ",\n"
                        + "\"TestTemperature\": 9080.866\n" + "}";

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

        @SuppressWarnings("unchecked")
        final Map<String, Object> jsonMap = new Gson().fromJson(jsonString, Map.class);
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

        @SuppressWarnings("unchecked")
        final Map<String, Object> jsonMap = new Gson().fromJson(jsonString, Map.class);
        assertTrue(jsonMap.containsKey("name"));
        assertEquals("0.0.1", jsonMap.get("version"));
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Act", jsonMap.get("source"));
        assertEquals("Outside", jsonMap.get("target"));

        return Response.status(200).entity("{\"GET\": , " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived
                        + ",\"POST\": , " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}").build();
    }
}
