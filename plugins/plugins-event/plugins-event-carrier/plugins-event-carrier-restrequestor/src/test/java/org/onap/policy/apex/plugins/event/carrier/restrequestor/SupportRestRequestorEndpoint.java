/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * The Class TestRestRequestorEndpoint.
 */
@Path("/apex")
public class SupportRestRequestorEndpoint {

    private static final Object counterLock = new Object();
    private static int postMessagesReceived = 0;
    private static int putMessagesReceived = 0;
    private static int statMessagesReceived = 0;
    private static int getMessagesReceived = 0;
    private static int deleteMessagesReceived = 0;

    private static final String EVENT_STRING = "{\n" + "\"nameSpace\": \"org.onap.policy.apex.events\",\n"
        + "\"name\": \"ResponseEvent\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_"
        + getMessagesReceived + "\",\n" + "\"target\": \"apex\",\n" + "\"intPar\": 9080\n" + "}";

    /**
     * Reset counters.
     */
    public static void resetCounters() {
        postMessagesReceived = 0;
        putMessagesReceived = 0;
        statMessagesReceived = 0;
        getMessagesReceived = 0;
        deleteMessagesReceived = 0;
    }

    /**
     * Service get stats.
     *
     * @return the response
     */
    @Path("/event/Stats")
    @GET
    public Response serviceGetStats() {
        synchronized (counterLock) {
            statMessagesReceived++;
        }
        return Response.status(200)
            .entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": "
                + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + ",\"DELETE\": "
                + deleteMessagesReceived + "}")
            .build();
    }

    /**
     * Service get event.
     *
     * @return the response
     */
    @Path("/event/GetEvent")
    @GET
    public Response serviceGetEvent() {
        synchronized (counterLock) {
            getMessagesReceived++;
        }

        return Response.status(200).entity(EVENT_STRING).build();
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
        synchronized (counterLock) {
            postMessagesReceived++;
        }

        @SuppressWarnings("unchecked") final Map<String, Object> jsonMap = new Gson().fromJson(jsonString, Map.class);
        assertTrue(jsonMap.containsKey("name"));
        assertEquals("0.0.1", jsonMap.get("version"));
        assertEquals("org.onap.policy.apex.events", jsonMap.get("nameSpace"));
        assertEquals("apex", jsonMap.get("source"));
        assertEquals("server", jsonMap.get("target"));

        return Response.status(200).entity(EVENT_STRING).build();
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
        synchronized (counterLock) {
            putMessagesReceived++;
        }

        @SuppressWarnings("unchecked") final Map<String, Object> jsonMap = new Gson().fromJson(jsonString, Map.class);
        assertTrue(jsonMap.containsKey("name"));
        assertEquals("0.0.1", jsonMap.get("version"));
        assertEquals("org.onap.policy.apex.events", jsonMap.get("nameSpace"));
        assertEquals("apex", jsonMap.get("source"));
        assertEquals("server", jsonMap.get("target"));

        return Response.status(200).entity(EVENT_STRING).build();
    }

    /**
     * Service delete request.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/event/DeleteEvent")
    @DELETE
    public Response serviceDeleteRequest(final String jsonString) {
        synchronized (counterLock) {
            deleteMessagesReceived++;
        }

        return Response.status(200).entity(EVENT_STRING).build();
    }

    /**
     * Service delete request bad response.
     *
     * @param jsonString the json string
     * @return the response
     */
    @Path("/event/DeleteEventBadResponse")
    @DELETE
    public Response serviceDeleteRequestBadResponse(final String jsonString) {
        return Response.status(400).build();
    }
}
