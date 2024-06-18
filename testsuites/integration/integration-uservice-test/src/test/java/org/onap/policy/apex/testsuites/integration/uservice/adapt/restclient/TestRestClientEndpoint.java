/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Copyright (C) 2019, 2023-2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.restclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.Random;

/**
 * The Class TestRestClientEndpoint.
 */
@Path("/apex")
public class TestRestClientEndpoint {
    private static int postMessagesReceived = 0;
    private static int putMessagesReceived = 0;
    private static int statMessagesReceived = 0;
    private static int getMessagesReceived = 0;
    private static int tagUrlToProperUrl = 0;

    /**
     * Service get stats.
     *
     * @return the response
     */
    @Path("/event/Stats")
    @GET
    public Response serviceGetStats() {
        statMessagesReceived++;
        return Response.status(200).entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived
                + ",\"POST\": " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}").build();
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

    /**
     * Service put request codeFilter Set.
     *
     * @return the response
     */
    @Path("/event/CodeFilterSet")
    @PUT
    public Response serviceCodeFilterSet() {
        putMessagesReceived++;
        tagUrlToProperUrl = 1;
        return Response.status(200)
                .entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": "
                        + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "PostProperUrl"
                        + tagUrlToProperUrl + "}")
                .build();
    }

    /**
     * Service put request codeFilter Default 200.
     *
     * @return the response
     */
    @Path("/event/CodeFilterDefault")
    @PUT
    public Response serviceCodeFilterDefault() {
        putMessagesReceived++;
        tagUrlToProperUrl = 2;
        return Response.status(200)
                .entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": "
                        + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "PostProperUrl"
                        + tagUrlToProperUrl + "}")
                .build();
    }

    /**
     * Service put request codeFilter Set.
     *
     * @return the response
     */
    @Path("/event/CodeFilterSet/3")
    @PUT
    public Response serviceCodeFilterSetForMultiTag() {
        putMessagesReceived++;
        tagUrlToProperUrl = 3;
        return Response.status(200)
                .entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": "
                        + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "PostProperUrl"
                        + tagUrlToProperUrl + "}")
                .build();
    }

    /**
     * Service get tagged Url request access status.
     *
     * @return the response
     */
    @Path("/event/GetProperUrl")
    @GET
    public Response serviceGetProperUrl() {
        statMessagesReceived++;
        if (tagUrlToProperUrl == 1) {
            return Response.status(200).entity("{\"PostProperUrl\": " + tagUrlToProperUrl + "}").build();
        } else {
            return Response.status(500).entity("{\"PostProperUrl\": " + tagUrlToProperUrl + "}").build();
        }
    }

    /**
     * Service fetch Http Code event.
     *
     * @return the response
     */
    @Path("/event/FetchHttpCode")
    @GET
    public Response serviceFetchHttpCode() {
        statMessagesReceived++;
        return Response.status(500).entity("{\"testToRun\": " + "FetchHttpCode" + "}").build();
    }
}
