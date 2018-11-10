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

package org.onap.policy.apex.client.editor.rest.handling;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The class represents the root resource exposed at the base URL<br> The url to access this resource would be in the
 * form {@code <baseURL>/rest/<session>/....} <br> For example: a PUT request to the following URL
 * {@code http://localhost:8080/apex/rest/109/ContextSchema/Update}, with a JSON string payload containing the new
 * {@code Schema} in the body, can be explained as: <ul> <li>The server or servlet is running at the base URL
 * {@code http://localhost:8080/apex} <li>This resource {@code ApexRestEditorResource} is used because the path
 * {@code rest/109} matches the {@code Path} filter specification for this Resource ({@code @Path("rest/{session}")}),
 * where the {@code int} path parameter {@code session} is assigned the {@code int} value {@code 109} <li>The path
 * {@code ContextSchema/Update} redirects this call to the method {@link #updateContextSchema(String)}, which should be
 * a {@link javax.ws.rs.PUT}, with a single String in the body/payload which gets mapped to the single String parameter
 * for the method. <li>So, in summary, the REST request updates a {@code ContextSchema} as specified in the payload for
 * {@code session} number {@code 109} </ul>
 *
 * <b>Note:</b> An allocated {@code Session} identifier must be included in (almost) all requests. Models for different
 * {@code Session} identifiers are completely isolated from one another.
 *
 * <b>Note:</b> To create a new {@code Session}, and have a new session ID allocated use {@link javax.ws.rs.GET} request
 * to {@code <baseURL>/rest/-1/Session/Create} (for example: {@code http://localhost:8080/apex/rest/-1/Session/Create} )
 *
 */
@Path("editor/{session}")
@Produces(
    { MediaType.APPLICATION_JSON })
@Consumes(
    { MediaType.APPLICATION_JSON })

public class ApexEditorRestResource implements RestCommandHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEditorRestResource.class);

    // Location of the periodi event template
    private static final String PERIODIC_EVENT_TEMPLATE = "src/main/resources/templates/PeriodicEventTemplate.json";

    // Recurring string constants
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String REST_COMMAND_NOT_RECOGNISED = "REST command not recognised";
    private static final String OK = ": OK";
    private static final String NOT_OK = ": Not OK";
    private static final String SESSION_CREATE = "Session/Create";
    private static final String SESSION_CREATE_NOT_OK = "Session/Create: Not OK";

    // The session handler for sessions on the Apex editor
    private static final RestSessionHandler SESSION_HANDLER = new RestSessionHandler();

    // Handlers for the various parts of an Apex model
    //@formatter:off
    private static final ModelHandler         MODEL_HANDLER          = new ModelHandler();
    private static final KeyInfoHandler       KEY_INFO_HANDLER       = new KeyInfoHandler();
    private static final ContextSchemaHandler CONTEXT_SCHEMA_HANDLER = new ContextSchemaHandler();
    private static final ContextAlbumHandler  CONTEXT_ALBUM_HANDLER  = new ContextAlbumHandler();
    private static final EventHandler         EVENT_HANDLER          = new EventHandler();
    private static final TaskHandler          TASK_HANDLER           = new TaskHandler();
    private static final PolicyHandler        POLICY_HANDLER         = new PolicyHandler();
    //@formatter:on

    // The ID of this session. This gets injected from the URL.
    @PathParam("session")
    private int sessionId = -1;

    /**
     * Creates a new session. Always call this method with sessionID -1, whereby a new sessionID will be allocated. If
     * successful the new sessionID will be available in the first message in the result.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}. This includes the session id
     *         for this session.
     */
    @GET
    @Path("Session/Create")
    public ApexApiResult createSession() {
        if (sessionId != -1) {
            return new ApexApiResult(Result.FAILED, "Session ID must be set to -1 to create sessions: " + sessionId);
        }

        ApexApiResult result = new ApexApiResult();
        SESSION_HANDLER.createSession(result);
        return result;
    }

    /**
     * Load the model from a JSON string for this session.
     *
     * @param jsonString the JSON string to be parsed. The returned value(s) will be similar to {@code AxPolicyModel},
     *        with merged {@code AxKeyInfo} for the root object.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("/Model/Load")
    public ApexApiResult loadFromString(final String jsonString) {
        return processRestCommand(RestCommandType.MODEL, RestCommand.LOAD, jsonString);
    }

    /**
     * Analyse the model and return analysis results. If successful the analysis results will be available in the
     * messages in the result.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Model/Analyse")
    public ApexApiResult analyse() {
        return processRestCommand(RestCommandType.MODEL, RestCommand.ANALYSE);
    }

    /**
     * Validate the model and return validation results. If successful the validation results will be available in the
     * messages in the result.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Model/Validate")
    public ApexApiResult validate() {
        return processRestCommand(RestCommandType.MODEL, RestCommand.VALIDATE);
    }

    /**
     * Creates the new model model for this session.
     *
     * @param jsonString the JSON string to be parsed containing the new model. See {@code BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @POST
    @Path("Model/Create")
    public ApexApiResult createModel(final String jsonString) {
        return processRestCommand(RestCommandType.MODEL, RestCommand.CREATE, jsonString);
    }

    /**
     * Update the model for this session.
     *
     * @param jsonString the JSON string to be parsed containing the updated model. See {@code BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("Model/Update")
    public ApexApiResult updateModel(final String jsonString) {
        return processRestCommand(RestCommandType.MODEL, RestCommand.UPDATE, jsonString);
    }

    /**
     * Gets the key for the model for this session. If successful the model key will be available in the first message
     * in the result. See {@code AxKey}
     *
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Model/GetKey")
    public ApexApiResult getModelKey() {
        return processRestCommand(RestCommandType.MODEL, RestCommand.GET_KEY);
    }

    /**
     * Retrieve the model for this session. If successful the model will be available in the first message in the
     * result. The returned value will be similar to a {@code AxPolicyModel}, with merged {@code AxKeyInfo} for the
     * root object.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Model/Get")
    public ApexApiResult listModel() {
        return processRestCommand(RestCommandType.MODEL, RestCommand.LIST);
    }

    /**
     * Download the model for this session as a String.
     *
     * @return the model represented as a JSON string. See {@code AxPolicyModel}
     */
    @GET
    @Path("Model/Download")
    public String downloadModel() {
        ApexApiResult result = processRestCommand(RestCommandType.MODEL, RestCommand.DOWNLOAD);
        if (result != null && result.isOk()) {
            return result.getMessage();
        } else {
            return null;
        }
    }

    /**
     * Delete the model for this session.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DELETE
    @Path("Model/Delete")
    public ApexApiResult deleteModel() {
        return processRestCommand(RestCommandType.MODEL, RestCommand.DELETE);
    }

    /**
     * List key information with the given key names/versions. If successful the result(s) will be available in the
     * result messages. See {@code AxKeyInfo}
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("KeyInformation/Get")
    public ApexApiResult listKeyInformation(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.KEY_INFO, RestCommand.LIST, name, version);
    }

    /**
     * Creates a context schema with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanContextSchema}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @POST
    @Path("ContextSchema/Create")
    public ApexApiResult createContextSchema(final String jsonString) {
        return processRestCommand(RestCommandType.CONTEXT_SCHEMA, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a context schema with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanContextSchema}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("ContextSchema/Update")
    public ApexApiResult updateContextSchema(final String jsonString) {
        return processRestCommand(RestCommandType.CONTEXT_SCHEMA, RestCommand.UPDATE, jsonString);
    }

    /**
     * List context schemas with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@code AxContextSchema}, with merged
     * {@code AxKeyInfo} for the root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("ContextSchema/Get")
    public ApexApiResult listContextSchemas(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.CONTEXT_SCHEMA, RestCommand.LIST, name, version);
    }

    /**
     * Delete context schemas with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DELETE
    @Path("ContextSchema/Delete")
    public ApexApiResult deleteContextSchema(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.CONTEXT_SCHEMA, RestCommand.DELETE, name, version);
    }

    /**
     * Validate context schemas with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Validate/ContextSchema")
    public ApexApiResult validateContextSchemas(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.CONTEXT_SCHEMA, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates a context album with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @POST
    @Path("ContextAlbum/Create")
    public ApexApiResult createContextAlbum(final String jsonString) {
        return processRestCommand(RestCommandType.CONTEXT_ALBUM, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a context album with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("ContextAlbum/Update")
    public ApexApiResult updateContextAlbum(final String jsonString) {
        return processRestCommand(RestCommandType.CONTEXT_ALBUM, RestCommand.UPDATE, jsonString);
    }

    /**
     * List context albums with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@code AxContextAlbum}, with merged
     * {@code AxKeyInfo} for the root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("ContextAlbum/Get")
    public ApexApiResult listContextAlbums(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.CONTEXT_ALBUM, RestCommand.LIST, name, version);
    }

    /**
     * Delete context albums with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DELETE
    @Path("ContextAlbum/Delete")
    public ApexApiResult deleteContextAlbum(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.CONTEXT_ALBUM, RestCommand.DELETE, name, version);
    }

    /**
     * Validate context albums with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Validate/ContextAlbum")
    public ApexApiResult validateContextAlbums(@QueryParam(NAME) final String name,
                    @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.CONTEXT_ALBUM, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates an event with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @POST
    @Path("Event/Create")
    public ApexApiResult createEvent(final String jsonString) {
        return processRestCommand(RestCommandType.EVENT, RestCommand.CREATE, jsonString);
    }

    /**
     * Update an event with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("Event/Update")
    public ApexApiResult updateEvent(final String jsonString) {
        return processRestCommand(RestCommandType.EVENT, RestCommand.UPDATE, jsonString);
    }

    /**
     * List events with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@code AxEvent}, with merged {@code AxKeyInfo} for the
     * root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Event/Get")
    public ApexApiResult listEvent(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.EVENT, RestCommand.LIST, name, version);
    }

    /**
     * Delete events with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DELETE
    @Path("Event/Delete")
    public ApexApiResult deleteEvent(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.EVENT, RestCommand.DELETE, name, version);
    }

    /**
     * Validate events with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Validate/Event")
    public ApexApiResult validateEvent(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.EVENT, RestCommand.VALIDATE, name, version);
    }

    /**
     * Creates a task with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @POST
    @Path("Task/Create")
    public ApexApiResult createTask(final String jsonString) {
        return processRestCommand(RestCommandType.TASK, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a task with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@code BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("Task/Update")
    public ApexApiResult updateTask(final String jsonString) {
        return processRestCommand(RestCommandType.TASK, RestCommand.UPDATE, jsonString);
    }

    /**
     * List tasks with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@code AxTask}, with merged {@code AxKeyInfo} for the
     * root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Task/Get")
    public ApexApiResult listTask(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.TASK, RestCommand.LIST, name, version);
    }

    /**
     * Delete tasks with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DELETE
    @Path("Task/Delete")
    public ApexApiResult deleteTask(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.TASK, RestCommand.DELETE, name, version);
    }

    /**
     * Validate tasks with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Validate/Task")
    public ApexApiResult validateTask(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.TASK, RestCommand.VALIDATE, name, version);
    }

    // CHECKSTYLE:OFF: MethodLength
    /**
     * Creates a policy with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed See {@code BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @POST
    @Path("Policy/Create")
    public ApexApiResult createPolicy(final String jsonString) {
        return processRestCommand(RestCommandType.POLICY, RestCommand.CREATE, jsonString);
    }

    /**
     * Update a policy with the information in the JSON string passed.
     *
     * @param firstStatePeriodic indicates if periodic event should be created and added to model
     * @param jsonString the JSON string to be parsed. See {@code BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @PUT
    @Path("Policy/Update")
    public ApexApiResult updatePolicy(@QueryParam("firstStatePeriodic") final boolean firstStatePeriodic,
                    final String jsonString) {

        ApexApiResult result = processRestCommand(RestCommandType.POLICY, RestCommand.UPDATE, jsonString);
        if (result != null && result.isOk() && firstStatePeriodic) {
            result = createPeriodicEvent();
        }
        return result;
    }

    /**
     * List policies with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@code AxPolicy}, with merged {@code AxKeyInfo} for the
     * root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @GET
    @Path("Policy/Get")
    public ApexApiResult listPolicy(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.POLICY, RestCommand.LIST, name, version);
    }

    /**
     * Delete policies with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    @DELETE
    @Path("Policy/Delete")
    public ApexApiResult deletePolicy(@QueryParam(NAME) final String name, @QueryParam(VERSION) final String version) {
        return processRestCommand(RestCommandType.POLICY, RestCommand.DELETE, name, version);
    }

    /**
     * This method routes REST commands that take no parameters to their caller.
     * 
     * @param commandType the type of REST command to process
     * @param command the REST command to process
     * @return the result of the REST command
     */
    private ApexApiResult processRestCommand(final RestCommandType commandType, final RestCommand command) {
        LOGGER.entry(commandType);
        try {
            ApexApiResult result = new ApexApiResult();
            RestSession session = SESSION_HANDLER.getSession(sessionId, result);
            if (session == null) {
                return result;
            }
            result = executeRestCommand(session, commandType, command);
            LOGGER.exit(SESSION_CREATE + (result != null && result.isOk() ? OK : NOT_OK));
            return result;
        } catch (final Exception e) {
            LOGGER.catching(e);
            LOGGER.exit(SESSION_CREATE_NOT_OK);
            throw e;
        }
    }

    /**
     * This method routes REST commands that take a JSON string to their caller.
     * 
     * @param commandType the type of REST command to process
     * @param command the REST command to process
     * @param jsonString the JSON string received in the REST request
     * @return the result of the REST command
     */
    private ApexApiResult processRestCommand(final RestCommandType commandType, final RestCommand command,
                    final String jsonString) {
        LOGGER.entry(commandType, jsonString);
        try {
            ApexApiResult result = new ApexApiResult();
            RestSession session = SESSION_HANDLER.getSession(sessionId, result);
            if (session == null) {
                return result;
            }
            result = executeRestCommand(session, commandType, command, jsonString);
            LOGGER.exit(SESSION_CREATE + (result != null && result.isOk() ? OK : NOT_OK));
            return result;
        } catch (final Exception e) {
            LOGGER.catching(e);
            LOGGER.exit(SESSION_CREATE_NOT_OK);
            throw e;
        }
    }

    /**
     * This method routes REST commands that take a name and version to their caller.
     * 
     * @param commandType the type of REST command to process
     * @param command the REST command to process
     * @param name the name received in the REST request
     * @param version the name received in the REST request
     * @return the result of the REST command
     */
    private ApexApiResult processRestCommand(final RestCommandType commandType, final RestCommand command,
                    final String name, final String version) {
        LOGGER.entry(commandType, name, version);
        try {
            ApexApiResult result = new ApexApiResult();
            RestSession session = SESSION_HANDLER.getSession(sessionId, result);
            if (session == null) {
                return result;
            }
            result = executeRestCommand(session, commandType, command, name, version);
            LOGGER.exit(SESSION_CREATE + (result != null && result.isOk() ? OK : NOT_OK));
            return result;
        } catch (final Exception e) {
            LOGGER.catching(e);
            LOGGER.exit(SESSION_CREATE_NOT_OK);
            throw e;
        }
    }

    /**
     * This method invokes callers to run REST commands that take no parameters.
     *
     * @param session the Apex editor session
     * @param commandType the type of REST command to process
     * @param command the REST command to process
     * @return the result of the REST command
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command) {
        switch (commandType) {
            case MODEL:
                return MODEL_HANDLER.executeRestCommand(session, commandType, command);
            case KEY_INFO:
                return KEY_INFO_HANDLER.executeRestCommand(session, commandType, command);
            case CONTEXT_SCHEMA:
                return CONTEXT_SCHEMA_HANDLER.executeRestCommand(session, commandType, command);
            case CONTEXT_ALBUM:
                return CONTEXT_ALBUM_HANDLER.executeRestCommand(session, commandType, command);
            case EVENT:
                return EVENT_HANDLER.executeRestCommand(session, commandType, command);
            case TASK:
                return TASK_HANDLER.executeRestCommand(session, commandType, command);
            case POLICY:
                return POLICY_HANDLER.executeRestCommand(session, commandType, command);
            default:
                return new ApexApiResult(Result.FAILED, REST_COMMAND_NOT_RECOGNISED);
        }
    }

    /**
     * This method invokes callers to run REST commands that take a JSON string.
     * 
     * @param session the Apex editor session
     * @param commandType the type of REST command to process
     * @param command the REST command to process
     * @param jsonString the JSON string received in the REST request
     * @return the result of the REST command
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String jsonString) {
        switch (commandType) {
            case MODEL:
                return MODEL_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            case KEY_INFO:
                return KEY_INFO_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            case CONTEXT_SCHEMA:
                return CONTEXT_SCHEMA_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            case CONTEXT_ALBUM:
                return CONTEXT_ALBUM_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            case EVENT:
                return EVENT_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            case TASK:
                return TASK_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            case POLICY:
                return POLICY_HANDLER.executeRestCommand(session, commandType, command, jsonString);
            default:
                return new ApexApiResult(Result.FAILED, REST_COMMAND_NOT_RECOGNISED);
        }
    }

    /**
     * This method invokes callers to run REST commands that take a name and version.
     * 
     * @param session the Apex editor session
     * @param commandType the type of REST command to process
     * @param command the REST command to process
     * @param name the name received in the REST request
     * @param version the name received in the REST request
     * @return the result of the REST command
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version) {
        switch (commandType) {
            case MODEL:
                return MODEL_HANDLER.executeRestCommand(session, commandType, command, name, version);
            case KEY_INFO:
                return KEY_INFO_HANDLER.executeRestCommand(session, commandType, command, name, version);
            case CONTEXT_SCHEMA:
                return CONTEXT_SCHEMA_HANDLER.executeRestCommand(session, commandType, command, name, version);
            case CONTEXT_ALBUM:
                return CONTEXT_ALBUM_HANDLER.executeRestCommand(session, commandType, command, name, version);
            case EVENT:
                return EVENT_HANDLER.executeRestCommand(session, commandType, command, name, version);
            case TASK:
                return TASK_HANDLER.executeRestCommand(session, commandType, command, name, version);
            case POLICY:
                return POLICY_HANDLER.executeRestCommand(session, commandType, command, name, version);
            default:
                return new ApexApiResult(Result.FAILED, REST_COMMAND_NOT_RECOGNISED);
        }
    }

    /**
     * Create a periodic event from the periodic event template.
     */
    private ApexApiResult createPeriodicEvent() {
        String periodicEventJsonString;
        try {
            periodicEventJsonString = TextFileUtils.getTextFileAsString(PERIODIC_EVENT_TEMPLATE);
        } catch (IOException ioException) {
            String message = "read of periodic event tempalte from " + PERIODIC_EVENT_TEMPLATE + "failed: "
                            + ioException.getMessage();
            LOGGER.debug(message, ioException);
            return new ApexApiResult(Result.FAILED, message);
        }

        return processRestCommand(RestCommandType.EVENT, RestCommand.CREATE, periodicEventJsonString);
    }
    
    /*
     * This method is used only for testing and is used to cause an exception on calls from unit test to test exception
     * handling.
     */
    protected static int createCorruptSession() {
        final ApexEditorRestResource apexEditorRestResource = new ApexEditorRestResource();
        final ApexApiResult result = apexEditorRestResource.createSession();
        final int corruptSessionId = Integer.parseInt(result.getMessages().get(0));

        SESSION_HANDLER.setCorruptSession(corruptSessionId);

        return corruptSessionId;
    }

}
