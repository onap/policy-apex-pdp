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

import java.util.Map;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * The Class AaiAndGuardSimEndpoint.
 */
@Path("/sim")
public class AaiAndGuardSimEndpointTest {

    private static int postMessagesReceived = 0;
    private static int putMessagesReceived = 0;
    private static int statMessagesReceived = 0;
    private static int getMessagesReceived = 0;

    /**
     * Service get stats.
     *
     * @return the response
     */
    @Path("/pdp/api/Stats")
    @GET
    public Response serviceGetStats() {
        statMessagesReceived++;
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
        postMessagesReceived++;

        if (postMessagesReceived % 2 == 0) {
            return Response.status(200).entity("{\"decision\": \"PERMIT\", \"details\": \"Decision Permit. OK!\"}")
                    .build();
        } else {
            return Response.status(200).entity("{\"decision\": \"DENY\", \"details\": \"Decision Denied. NOK :-(\"}")
                    .build();
        }
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

        getMessagesReceived++;

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
        postMessagesReceived++;

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
        putMessagesReceived++;

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
