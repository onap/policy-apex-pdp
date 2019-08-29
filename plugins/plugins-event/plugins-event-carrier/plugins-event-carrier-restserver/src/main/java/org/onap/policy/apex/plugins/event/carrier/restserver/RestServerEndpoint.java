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

package org.onap.policy.apex.plugins.event.carrier.restserver;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.BasicAuthDefinition;
import io.swagger.annotations.Info;
import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class RestServerEndpoint is the end point servlet class for handling REST requests and responses to and from
 * Apex.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@Path("/apex/{eventInput}")
@Api(value = "APEX REST SERVER API")
@Produces(
    { MediaType.APPLICATION_JSON })
@Consumes(
    { MediaType.APPLICATION_JSON })
@SwaggerDefinition(
        info = @Info(description =
                 "APEX RestServer that handles  REST requests and responses to and from Apex.", version = "v1.0",
                     title = "APEX RESTSERVER"),
        consumes = {MediaType.APPLICATION_JSON},
        produces = {MediaType.APPLICATION_JSON},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS},
        tags = {@Tag(name = "APEX RESTSERVER", description = "APEX RESTSERVER")},
        securityDefinition = @SecurityDefinition(basicAuthDefinitions = {@BasicAuthDefinition(key = "basicAuth")}))
public class RestServerEndpoint {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RestServerEndpoint.class);

    public static final String AUTHORIZATION_TYPE = "basicAuth";

    public static final int AUTHENTICATION_ERROR_CODE = HttpURLConnection.HTTP_UNAUTHORIZED;
    public static final int AUTHORIZATION_ERROR_CODE = HttpURLConnection.HTTP_FORBIDDEN;
    public static final int SERVER_ERROR_CODE = HttpURLConnection.HTTP_INTERNAL_ERROR;

    public static final String AUTHENTICATION_ERROR_MESSAGE = "Authentication Error";
    public static final String AUTHORIZATION_ERROR_MESSAGE = "Authorization Error";
    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";

    // Statistics on the amount of HTTP messages handled
    private static int getMessagesReceived = 0;
    private static int postEventMessagesReceived = 0;
    private static int putEventMessagesReceived = 0;

    // This map is used to hold all the REST server event inputs. This is used to determine which consumer to send input
    // events to
    private static Map<String, ApexRestServerConsumer> consumerMap = new LinkedHashMap<>();

    // The ID of this event input. This gets injected from the URL.
    @PathParam("eventInput")
    private String eventInputId = null;

    /**
     * Register an Apex consumer with the REST server end point.
     *
     * @param consumerEventInputId The event input ID that indicates this consumer shoud be used
     * @param consumer The consumer to register
     */
    public static void registerApexRestServerConsumer(final String consumerEventInputId,
                    final ApexRestServerConsumer consumer) {
        consumerMap.put(consumerEventInputId, consumer);
    }

    /**
     * Get statistics on apex REST event handling.
     *
     * @return the response
     */
    @Path("/Status")
    @GET
    @ApiOperation(
        value = "Get Statistics",
        notes = "Get statistics on apex REST event handlin",
        authorizations = @Authorization(value = AUTHORIZATION_TYPE))
    @ApiResponses(
        value = {@ApiResponse(code = AUTHENTICATION_ERROR_CODE, message = AUTHENTICATION_ERROR_MESSAGE),
            @ApiResponse(code = AUTHORIZATION_ERROR_CODE, message = AUTHORIZATION_ERROR_MESSAGE),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_MESSAGE)})
    public Response serviceGetStats() {
        incrementGetMessages();
        return Response.status(Response.Status.OK.getStatusCode())
                        .entity("{\n" + "\"INPUTS\": \"" + consumerMap.keySet() + "\",\n" + "\"STAT\": "
                                        + getMessagesReceived + ",\n" + "\"POST\": " + postEventMessagesReceived + ",\n"
                                        + "\"PUT\":  " + putEventMessagesReceived + "\n}")
                        .build();
    }

    /**
     * Service post request, an incoming event over REST to Apex.
     *
     * @param jsonString the JSON string containing the data coming in on the REST call
     * @return the response event to the request
     */
    @Path("/EventIn")
    @POST
    @ApiOperation(
        value = "Post Event",
        notes = "Service post request, an incoming event over REST to Apex",
        authorizations = @Authorization(value = AUTHORIZATION_TYPE))
    @ApiResponses(
        value = {@ApiResponse(code = AUTHENTICATION_ERROR_CODE, message = AUTHENTICATION_ERROR_MESSAGE),
            @ApiResponse(code = AUTHORIZATION_ERROR_CODE, message = AUTHORIZATION_ERROR_MESSAGE),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_MESSAGE)})
    public Response servicePostRequest(final String jsonString) {
        incrementPostEventMessages();

        if (LOGGER.isDebugEnabled()) {
            String message = "event input " + eventInputId + ", received POST of event \"" + jsonString + "\"";
            LOGGER.debug(message);
        }

        // Common handler method for POST and PUT requests
        return handleEvent(jsonString);
    }

    /**
     * Service put request, an incoming event over REST to Apex.
     *
     * @param jsonString the JSON string containing the data coming in on the REST call
     * @return the response event to the request
     */
    @Path("/EventIn")
    @PUT
    @ApiOperation(
        value = "Put Event",
        notes = "Service put request, an incoming event over REST to Apex",
        authorizations = @Authorization(value = AUTHORIZATION_TYPE))
    @ApiResponses(
        value = {@ApiResponse(code = AUTHENTICATION_ERROR_CODE, message = AUTHENTICATION_ERROR_MESSAGE),
            @ApiResponse(code = AUTHORIZATION_ERROR_CODE, message = AUTHORIZATION_ERROR_MESSAGE),
            @ApiResponse(code = SERVER_ERROR_CODE, message = SERVER_ERROR_MESSAGE)})
    public Response servicePutRequest(final String jsonString) {
        incrementPutEventMessages();

        if (LOGGER.isDebugEnabled()) {
            String message = "event input \"" + eventInputId + "\", received PUT of event \"" + jsonString + "\"";
            LOGGER.debug(message);
        }

        // Common handler method for POST and PUT requests
        return handleEvent(jsonString);
    }

    /**
     * Common event handler for events received on POST and PUT messages.
     *
     * @param jsonString the JSON string containing the data coming in on the REST call
     * @return the response event to the request
     */
    private Response handleEvent(final String jsonString) {
        // Find the correct consumer for this REST message
        final ApexRestServerConsumer eventConsumer = consumerMap.get(eventInputId);
        if (eventConsumer == null) {
            final String errorMessage = "event input " + eventInputId
                            + " is not defined in the Apex configuration file";
            LOGGER.warn(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode())
                            .entity("{'errorMessage', '" + errorMessage + "'}").build();
        }

        return eventConsumer.receiveEvent(jsonString);
    }

    /**
     * Increment number of get messages received.
     */
    private static void incrementGetMessages() {
        getMessagesReceived++;
    }

    /**
     * Increment number of get messages received.
     */
    private static void incrementPutEventMessages() {
        putEventMessagesReceived++;
    }

    /**
     * Increment number of get messages received.
     */
    private static void incrementPostEventMessages() {
        postEventMessagesReceived++;
    }
}
