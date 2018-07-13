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

package org.onap.policy.apex.client.editor.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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

import org.onap.policy.apex.client.editor.rest.bean.BeanContextAlbum;
import org.onap.policy.apex.client.editor.rest.bean.BeanContextSchema;
import org.onap.policy.apex.client.editor.rest.bean.BeanEvent;
import org.onap.policy.apex.client.editor.rest.bean.BeanField;
import org.onap.policy.apex.client.editor.rest.bean.BeanKeyRef;
import org.onap.policy.apex.client.editor.rest.bean.BeanLogic;
import org.onap.policy.apex.client.editor.rest.bean.BeanModel;
import org.onap.policy.apex.client.editor.rest.bean.BeanPolicy;
import org.onap.policy.apex.client.editor.rest.bean.BeanState;
import org.onap.policy.apex.client.editor.rest.bean.BeanStateOutput;
import org.onap.policy.apex.client.editor.rest.bean.BeanStateTaskRef;
import org.onap.policy.apex.client.editor.rest.bean.BeanTask;
import org.onap.policy.apex.client.editor.rest.bean.BeanTaskParameter;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.modelapi.ApexAPIResult.RESULT;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.modelapi.ApexModelFactory;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The class represents the root resource exposed at the base URL<br>
 * The url to access this resource would be in the form {@code <baseURL>/rest/<session>/....} <br>
 * For example: a PUT request to the following URL {@code http://localhost:8080/apex/rest/109/ContextSchema/Update},
 * with a JSON string payload containing the new {@code Schema} in the body, can be explained as:
 * <ul>
 * <li>The server or servlet is running at the base URL {@code http://localhost:8080/apex}
 * <li>This resource {@code ApexRestEditorResource} is used because the path {@code rest/109} matches the {@code Path}
 * filter specification for this Resource ({@code @Path("rest/{session}")}), where the {@code int} path parameter
 * {@code session} is assigned the {@code int} value {@code 109}
 * <li>The path {@code ContextSchema/Update} redirects this call to the method {@link #updateContextSchema(String)},
 * which should be a {@link javax.ws.rs.PUT}, with a single String in the body/payload which gets mapped to the single
 * String parameter for the method.
 * <li>So, in summary, the REST request updates a {@code ContextSchema} as specified in the payload for {@code session}
 * number {@code 109}
 * </ul>
 *
 * <b>Note:</b> An allocated {@code Session} identifier must be included in (almost) all requests. Models for different
 * {@code Session} identifiers are completely isolated from one another.
 *
 * <b>Note:</b> To create a new {@code Session}, and have a new session ID allocated use {@link javax.ws.rs.GET} request
 * to {@code <baseURL>/rest/-1/Session/Create} (for example: {@code http://localhost:8080/apex/rest/-1/Session/Create} )
 *
 */
@Path("editor/{session}")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })

public class ApexEditorRestResource {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApexEditorRestResource.class);

    // The next session will have this number, stating at 0
    private static int nextSession = 0;

    // All REST editor sessions being handled by the server
    private static final Map<Integer, ApexModel> SESSIONMODELMAP = new TreeMap<>();

    // The ID of this session. This gets injected from the URL.
    @PathParam("session")
    private int sessionID = -1;

    // The Apex model for the session
    private ApexModel sessionApexModel = null;

    /**
     * This method sets the Apex model for the current editor session. Don't forget to call {@link #commitChanges()}
     * when finished! This makes requests atomic.
     *
     * @return the result of finding the session Apex model and setting it
     */
    private ApexAPIResult initialiseSessionForChanges() {
        if (sessionID < 0) {
            return new ApexAPIResult(RESULT.FAILED, "Session ID  \"" + sessionID + "\" is negative");
        }

        if (!SESSIONMODELMAP.containsKey(sessionID)) {
            return new ApexAPIResult(RESULT.FAILED, "A session with session ID \"" + sessionID + "\" does not exist");
        }

        if (sessionApexModel == null) {
            sessionApexModel = SESSIONMODELMAP.get(sessionID).clone();
        }
        return new ApexAPIResult();
    }

    /**
     * This method sets the Apex model for the current editor session. Don't make any changes to the model.
     *
     * @return the result of finding the session Apex model and setting it
     */
    private ApexAPIResult initialiseSessionForReadOnly() {
        if (sessionID < 0) {
            return new ApexAPIResult(RESULT.FAILED, "Session ID  \"" + sessionID + "\" is negative");
        }

        if (!SESSIONMODELMAP.containsKey(sessionID)) {
            return new ApexAPIResult(RESULT.FAILED, "A session with session ID \"" + sessionID + "\" does not exist");
        }

        if (sessionApexModel == null) {
            sessionApexModel = SESSIONMODELMAP.get(sessionID);
        }
        return new ApexAPIResult();
    }

    /**
     * This method commits changes to the Apex model for the current editor session. This should only be called once, at
     * the end of a successful change to the model for this session
     *
     * @return the result of committing the session Apex model
     */
    private ApexAPIResult commitChanges() {

        if (sessionApexModel == null) {
            return new ApexAPIResult(RESULT.FAILED, "Cannot commit a changes for Session ID  \"" + sessionID
                    + "\", because it has not been initialised / started");
        }

        SESSIONMODELMAP.put(sessionID, sessionApexModel);

        return new ApexAPIResult();
    }

    /**
     * Creates a new session. Always call this method with sessionID -1, whereby a new sessionID will be allocated. If
     * successful the new sessionID will be available in the first message in the result.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}. This includes the session id
     *         for this session.
     */
    @GET
    @Path("Session/Create")
    public ApexAPIResult createSession() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {
            if (sessionID != -1) {
                ret = new ApexAPIResult(RESULT.FAILED, "Session ID must be set to -1 to create sessions: " + sessionID);
                return ret;
            }

            final int newSessionID = nextSession;

            if (SESSIONMODELMAP.containsKey(newSessionID)) {
                ret = new ApexAPIResult(RESULT.FAILED, "Session already exists for session: " + newSessionID);
                return ret;
            }

            SESSIONMODELMAP.put(newSessionID, new ApexModelFactory().createApexModel(null, true));
            nextSession++;

            ret = new ApexAPIResult(RESULT.SUCCESS, Integer.toString(newSessionID));
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Session/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Load the model from a JSON string for this session.
     *
     * @param jsonString the JSON string to be parsed. The returned value(s) will be similar to {@link AxPolicyModel},
     *        with merged {@linkplain AxKeyInfo} for the root object.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("/Model/Load")
    public ApexAPIResult loadFromString(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.loadFromString(jsonString);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Load" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Analyse the model and return analysis results. If successful the analysis results will be available in the
     * messages in the result.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Model/Analyse")
    public ApexAPIResult analyse() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.analyse();
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Analyse" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Validate the model and return validation results. If successful the validation results will be available in the
     * messages in the result.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Model/Validate")
    public ApexAPIResult validate() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.validate();

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Validate" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Creates the new model model for this session.
     *
     * @param jsonString the JSON string to be parsed containing the new model. See {@linkplain BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @POST
    @Path("Model/Create")
    public ApexAPIResult createModel(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanModel jsonbean = RestUtils.getJSONParameters(jsonString, BeanModel.class);
            ret = sessionApexModel.createModel(jsonbean.getName(), jsonbean.getVersion(), jsonbean.getUuid(),
                    jsonbean.getDescription());

            if (ret.isOK()) {
                ret = addKeyInfo2Messages(ret);
            }
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Update the model for this session.
     *
     * @param jsonString the JSON string to be parsed containing the updated model. See {@linkplain BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("Model/Update")
    public ApexAPIResult updateModel(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanModel jsonbean = RestUtils.getJSONParameters(jsonString, BeanModel.class);
            ret = sessionApexModel.updateModel(jsonbean.getName(), jsonbean.getVersion(), jsonbean.getUuid(),
                    jsonbean.getDescription());
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Update" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Gets the key for the model for this session. If successful the model key will be available in the first message
     * in the result. See {@linkplain AxKey}
     *
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Model/GetKey")
    public ApexAPIResult getModelKey() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.getModelKey();
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/GetKey" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Retrieve the model for this session. If successful the model will be available in the first message in the
     * result. The returned value will be similar to a {@link AxPolicyModel}, with merged {@linkplain AxKeyInfo} for the
     * root object.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Model/Get")
    public ApexAPIResult listModel() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.listModel();
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Download the model for this session as a String.
     *
     * @return the model represented as a JSON string. See {@linkplain AxPolicyModel}
     */
    @GET
    @Path("Model/Download")
    public String downloadModel() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {

            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                throw new IllegalStateException("Cannot download file: " + ret.getMessage());
            }

            ret = sessionApexModel.listModel();
            if (ret.isNOK()) {
                throw new IllegalStateException("Cannot download file: " + ret.getMessage());
            }

            return ret.getMessage();
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit(ret.isOK());
            LOGGER.info("Model/Download" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Delete the model for this session.
     *
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @DELETE
    @Path("Model/Delete")
    public ApexAPIResult deleteModel() {
        ApexAPIResult ret = null;
        LOGGER.entry();
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.deleteModel();
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Model/Delete" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * List key information with the given key names/versions. If successful the result(s) will be available in the
     * result messages. See {@linkplain AxKeyInfo}
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("KeyInformation/Get")
    public ApexAPIResult listKeyInformation(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listKeyInformation(name1, version1);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("KeyInformation/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Creates a context schema with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanContextSchema}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @POST
    @Path("ContextSchema/Create")
    public ApexAPIResult createContextSchema(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanContextSchema jsonbean = RestUtils.getJSONParameters(jsonString, BeanContextSchema.class);
            ret = sessionApexModel.createContextSchema(jsonbean.getName(), jsonbean.getVersion(),
                    jsonbean.getSchemaFlavour(), jsonbean.getSchemaDefinition(), jsonbean.getUuid(),
                    jsonbean.getDescription());
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextSchema/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Update a context schema with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanContextSchema}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("ContextSchema/Update")
    public ApexAPIResult updateContextSchema(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanContextSchema jsonbean = RestUtils.getJSONParameters(jsonString, BeanContextSchema.class);

            ret = sessionApexModel.updateContextSchema(jsonbean.getName(), jsonbean.getVersion(),
                    jsonbean.getSchemaFlavour(), jsonbean.getSchemaDefinition(), jsonbean.getUuid(),
                    jsonbean.getDescription());
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextSchema/Update" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * List context schemas with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@link AxContextSchema}, with merged
     * {@linkplain AxKeyInfo} for the root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("ContextSchema/Get")
    public ApexAPIResult listContextSchemas(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listContextSchemas(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextSchema/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Delete context schemas with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @DELETE
    @Path("ContextSchema/Delete")
    public ApexAPIResult deleteContextSchema(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.deleteContextSchema(name1, version1);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextSchema/Delete" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Validate context schemas with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Validate/ContextSchema")
    public ApexAPIResult validateContextSchemas(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.validateContextSchemas(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Validate/ContextSchema" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Creates a context album with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @POST
    @Path("ContextAlbum/Create")
    public ApexAPIResult createContextAlbum(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanContextAlbum jsonbean = RestUtils.getJSONParameters(jsonString, BeanContextAlbum.class);

            ret = sessionApexModel.createContextAlbum(jsonbean.getName(), jsonbean.getVersion(), jsonbean.getScope(),
                    Boolean.toString(jsonbean.getWriteable()), jsonbean.getItemSchema().getName(),
                    jsonbean.getItemSchema().getVersion(), jsonbean.getUuid(), jsonbean.getDescription());
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextAlbum/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Update a context album with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanContextAlbum}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("ContextAlbum/Update")
    public ApexAPIResult updateContextAlbum(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanContextAlbum jsonbean = RestUtils.getJSONParameters(jsonString, BeanContextAlbum.class);

            ret = sessionApexModel.updateContextAlbum(jsonbean.getName(), jsonbean.getVersion(), jsonbean.getScope(),
                    Boolean.toString(jsonbean.getWriteable()), jsonbean.getItemSchema().getName(),
                    jsonbean.getItemSchema().getVersion(), jsonbean.getUuid(), jsonbean.getDescription());
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextAlbum/Update" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * List context albums with the given key names/versions. If successful the result(s) will be available in the
     * result messages. The returned value(s) will be similar to {@link AxContextAlbum}, with merged
     * {@linkplain AxKeyInfo} for the root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("ContextAlbum/Get")
    public ApexAPIResult listContextAlbums(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listContextAlbum(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextAlbum/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Delete context albums with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @DELETE
    @Path("ContextAlbum/Delete")
    public ApexAPIResult deleteContextAlbum(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.deleteContextAlbum(name1, version1);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("ContextAlbum/Delete" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Validate context albums with the given key names/versions. The result(s) will be available in the result
     * messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Validate/ContextAlbum")
    public ApexAPIResult validateContextAlbums(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.listContextAlbum(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Validate/ContextAlbum" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Creates an event with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @POST
    @Path("Event/Create")
    public ApexAPIResult createEvent(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanEvent jsonbean = RestUtils.getJSONParameters(jsonString, BeanEvent.class);

            ret = sessionApexModel.createEvent(jsonbean.getName(), jsonbean.getVersion(), jsonbean.getNameSpace(),
                    jsonbean.getSource(), jsonbean.getTarget(), jsonbean.getUuid(), jsonbean.getDescription());
            if (ret.isNOK()) {
                return ret;
            }
            if (jsonbean.getParameters() != null) {
                for (final Entry<String, BeanField> p : jsonbean.getParameters().entrySet()) {
                    if (p.getValue() == null) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Null event parameter information for parameter \""
                                + p.getKey() + "\" in event " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The event was created, but there was an error adding the event parameters. The event has only been partially defined.");
                        return ret;
                    }
                    final ApexAPIResult rettmp =
                            sessionApexModel.createEventPar(jsonbean.getName(), jsonbean.getVersion(), p.getKey(),
                                    p.getValue().getName(), p.getValue().getVersion(), p.getValue().getOptional());
                    if (rettmp.isNOK()) {
                        rettmp.addMessage("Failed to add event parameter information for parameter \"" + p.getKey()
                                + "\" in event " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The event was created, but there was an error adding the event parameters. The event has only been partially defined.");
                        ret = rettmp;
                        return ret;
                    }
                }
            }
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Event/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Update an event with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanEvent}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("Event/Update")
    public ApexAPIResult updateEvent(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanEvent jsonbean = RestUtils.getJSONParameters(jsonString, BeanEvent.class);

            if (jsonbean.getName() == null || jsonbean.getName().equals("") || jsonbean.getVersion() == null
                    || jsonbean.getVersion().equals("")) {
                ret = new ApexAPIResult(RESULT.FAILED, "Null/Empty event name/version (\"" + jsonbean.getName() + ":"
                        + jsonbean.getVersion() + "\" passed to UpdateEvent");
                return ret;
            }

            ret = sessionApexModel.deleteEvent(jsonbean.getName(), jsonbean.getVersion());
            if (ret.isNOK()) {
                return ret;
            }

            ret = createEvent(jsonString);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Event/Update" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * List events with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@link AxEvent}, with merged {@linkplain AxKeyInfo} for the
     * root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Event/Get")
    public ApexAPIResult listEvent(@QueryParam("name") final String name, @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listEvent(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Event/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Delete events with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @DELETE
    @Path("Event/Delete")
    public ApexAPIResult deleteEvent(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            ret = sessionApexModel.deleteEvent(name1, version1);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Event/Delete" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Validate events with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Validate/Event")
    public ApexAPIResult validateEvent(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listEvent(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Validate/Event" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Creates a task with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @POST
    @Path("Task/Create")
    public ApexAPIResult createTask(final String jsonString) {
        ApexAPIResult ret = null;
        ApexAPIResult tempres = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanTask jsonbean = RestUtils.getJSONParameters(jsonString, BeanTask.class);

            ret = sessionApexModel.createTask(jsonbean.getName(), jsonbean.getVersion(), jsonbean.getUuid(),
                    jsonbean.getDescription());
            if (ret.isNOK()) {
                return ret;
            }
            if (jsonbean.getInputFields() != null) {
                for (final Entry<String, BeanField> fin : jsonbean.getInputFields().entrySet()) {
                    if (fin.getValue() == null) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Null task input field information for field \""
                                + fin.getKey() + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the input fields. The task has only been partially defined.");
                        return ret;
                    }
                    if (fin.getKey() == null || !fin.getKey().equals(fin.getValue().getLocalName())) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Invalid task input field information for field \""
                                + fin.getKey() + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The localName of the field (\"" + fin.getValue().getLocalName()
                                + "\") is not the same as the field name. "
                                + "The task was created, but there was an error adding the input fields. The task has only been partially defined.");
                        return ret;
                    }
                    tempres = sessionApexModel.createTaskInputField(jsonbean.getName(), jsonbean.getVersion(),
                            fin.getKey(), fin.getValue().getName(), fin.getValue().getVersion(),
                            fin.getValue().getOptional());
                    if (tempres.isNOK()) {
                        tempres.addMessage("Failed to add task input field information for field \"" + fin.getKey()
                                + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the input fields. The task has only been partially defined.");
                        ret = tempres;
                        return ret;
                    }
                }
            }
            if (jsonbean.getOutputFields() != null) {
                for (final Entry<String, BeanField> fout : jsonbean.getOutputFields().entrySet()) {
                    if (fout.getValue() == null) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Null task output field information for field \""
                                + fout.getKey() + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the output fields. The task has only been partially defined.");
                        return ret;
                    }
                    if (fout.getKey() == null || !fout.getKey().equals(fout.getValue().getLocalName())) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Invalid task output field information for field \""
                                + fout.getKey() + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The localName of the field (\"" + fout.getValue().getLocalName()
                                + "\") is not the same as the field name. "
                                + "The task was created, but there was an error adding the output fields. The task has only been partially defined.");
                        return ret;
                    }
                    tempres = sessionApexModel.createTaskOutputField(jsonbean.getName(), jsonbean.getVersion(),
                            fout.getKey(), fout.getValue().getName(), fout.getValue().getVersion(),
                            fout.getValue().getOptional());
                    if (tempres.isNOK()) {
                        tempres.addMessage("Failed to add task output field information for field \"" + fout.getKey()
                                + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the output fields. The task has only been partially defined.");
                        ret = tempres;
                        return ret;
                    }
                }
            }
            if (jsonbean.getTaskLogic() != null) {
                final BeanLogic logic = jsonbean.getTaskLogic();
                tempres = sessionApexModel.createTaskLogic(jsonbean.getName(), jsonbean.getVersion(),
                        logic.getLogicFlavour(), logic.getLogic());
                if (tempres.isNOK()) {
                    tempres.addMessage("Failed to add task logic in task " + jsonbean.getName() + ":"
                            + jsonbean.getVersion()
                            + ". The task was created, but there was an error adding the logic. The task has only been partially defined.");
                    ret = tempres;
                    return ret;
                }
            }
            if (jsonbean.getParameters() != null) {
                for (final Entry<String, BeanTaskParameter> param : jsonbean.getParameters().entrySet()) {
                    if (param.getKey() == null || param.getValue() == null
                            || !param.getKey().equals(param.getValue().getParameterName())) {
                        ret = new ApexAPIResult(RESULT.FAILED,
                                "Null or invalid task parameter information for parameter \"" + param.getKey()
                                        + "\" in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                                        + ". The task was created, but there was an error adding the parameters. The task has only been partially defined.");
                        return ret;
                    }
                    tempres = sessionApexModel.createTaskParameter(jsonbean.getName(), jsonbean.getVersion(),
                            param.getValue().getParameterName(), param.getValue().getDefaultValue());
                    if (tempres.isNOK()) {
                        tempres.addMessage("Failed to add task parameter \"" + param.getKey() + "\" in task "
                                + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the parameters. The task has only been partially defined.");
                        ret = tempres;
                        return ret;
                    }
                }
            }
            if (jsonbean.getContexts() != null) {
                for (final BeanKeyRef contextalbum : jsonbean.getContexts()) {
                    if (contextalbum.getName() == null || contextalbum.getVersion() == null) {
                        ret = new ApexAPIResult(RESULT.FAILED,
                                "Null or invalid context album reference information in task " + jsonbean.getName()
                                        + ":" + jsonbean.getVersion()
                                        + ". The task was created, but there was an error adding the"
                                        + " context album reference. The task has only been partially defined.");
                        return ret;
                    }
                    tempres = sessionApexModel.createTaskContextRef(jsonbean.getName(), jsonbean.getVersion(),
                            contextalbum.getName(), contextalbum.getVersion());
                    if (tempres.isNOK()) {
                        ret = new ApexAPIResult(RESULT.FAILED,
                                "Failed to add context album reference information in task " + jsonbean.getName() + ":"
                                        + jsonbean.getVersion()
                                        + ". The task was created, but there was an error adding the"
                                        + " context album reference. The task has only been partially defined.");
                        return ret;
                    }
                }
            }
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Task/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Update a task with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("Task/Update")
    public ApexAPIResult updateTask(final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanTask jsonbean = RestUtils.getJSONParameters(jsonString, BeanTask.class);

            if (jsonbean.getName() == null || jsonbean.getName().equals("") || jsonbean.getVersion() == null
                    || jsonbean.getVersion().equals("")) {
                ret = new ApexAPIResult(RESULT.FAILED, "Null/Empty task name/version (\"" + jsonbean.getName() + ":"
                        + jsonbean.getVersion() + "\" passed to UpdateTask");
                return ret;
            }

            ret = sessionApexModel.deleteTask(jsonbean.getName(), jsonbean.getVersion());
            if (ret.isNOK()) {
                return ret;
            }

            ret = createTask(jsonString);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Task/Update" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * List tasks with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@link AxTask}, with merged {@linkplain AxKeyInfo} for the
     * root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Task/Get")
    public ApexAPIResult listTask(@QueryParam("name") final String name, @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listTask(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Task/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Delete tasks with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @DELETE
    @Path("Task/Delete")
    public ApexAPIResult deleteTask(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            // all input/output fields, parameters, logic, context references is "owned"/contained in the task, so
            // deleting the task removes all of these
            ret = sessionApexModel.deleteTask(name1, version1);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Task/Delete" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Validate tasks with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Validate/Task")
    public ApexAPIResult validateTask(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listTask(name1, version1);
            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Validate/Task" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    // CHECKSTYLE:OFF: MethodLength
    /**
     * Creates a policy with the information in the JSON string passed.
     *
     * @param jsonString the JSON string to be parsed See {@linkplain BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @POST
    @Path("Policy/Create")
    public ApexAPIResult createPolicy(final String jsonString) {

        ApexAPIResult ret = null;
        ApexAPIResult tempres = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanPolicy jsonbean = RestUtils.getJSONParameters(jsonString, BeanPolicy.class);
            final String policyname = jsonbean.getName();
            final String policyversion = jsonbean.getVersion();

            ret = sessionApexModel.createPolicy(policyname, policyversion, jsonbean.getTemplate(),
                    jsonbean.getFirstState(), jsonbean.getUuid(), jsonbean.getDescription());
            if (ret.isNOK()) {
                return ret;
            }

            if (jsonbean.getStates() == null || jsonbean.getStates().isEmpty()) {
                ret = new ApexAPIResult(RESULT.FAILED, "Null or empty state map; no states defined for policy \""
                        + policyname + ":" + policyversion
                        + "\". The policy was created, but there was an error adding states. The policy has only been partially defined.");
                return ret;
            }

            final Map<String, BeanState> statemap = jsonbean.getStates();
            // need to create all the states first, before populating them
            for (final Map.Entry<String, BeanState> e : statemap.entrySet()) {
                final String statename = e.getKey();
                final BeanState state = e.getValue();
                if (state == null) {
                    ret = new ApexAPIResult(RESULT.FAILED, "Null or invalid state information for state \"" + statename
                            + "\" for policy \"" + policyname + ":" + policyversion
                            + "\". The policy was created, but there was an error adding the state. The policy has only been partially defined.");
                    return ret;
                }
                if (state.getTrigger() == null) {
                    ret = new ApexAPIResult(RESULT.FAILED, "Null or invalid state trigger for state \"" + statename
                            + "\" for policy \"" + policyname + ":" + policyversion
                            + "\". The policy was created, but there was an error adding the state. The policy has only been partially defined.");
                    return ret;
                }
                if (state.getDefaultTask() == null) {
                    ret = new ApexAPIResult(RESULT.FAILED, "Null or invalid default task for state \"" + statename
                            + "\" for policy \"" + policyname + ":" + policyversion
                            + "\". The policy was created, but there was an error adding the state. The policy has only been partially defined.");
                    return ret;
                }
                tempres = sessionApexModel.createPolicyState(policyname, policyversion, statename,
                        state.getTrigger().getName(), state.getTrigger().getVersion(), state.getDefaultTask().getName(),
                        state.getDefaultTask().getVersion());
                if (tempres.isNOK()) {
                    ret = tempres;
                    return ret;
                }
            }

            for (final Map.Entry<String, BeanState> e : statemap.entrySet()) {
                final String statename = e.getKey();
                final BeanState state = e.getValue();

                final BeanLogic tsl = state.getTaskSelectionLogic();
                if (tsl != null) {
                    tempres = sessionApexModel.createPolicyStateTaskSelectionLogic(policyname, policyversion, statename,
                            tsl.getLogicFlavour(), tsl.getLogic());
                    if (tempres.isNOK()) {
                        tempres.addMessage("Failed to add task selection logic for state \"" + statename + "\" for"
                                + " policy \"" + policyname + ":" + policyversion
                                + "\". The policy was created, but there was an error adding the task selection logic for "
                                + "the state. The policy has only been partially defined.");
                        ret = tempres;
                        return ret;
                    }
                }

                final BeanKeyRef[] contexts = state.getContexts();
                if (contexts != null) {
                    for (final BeanKeyRef c : contexts) {
                        if (c == null) {
                            ret = new ApexAPIResult(RESULT.FAILED, "Null or invalid context reference \"" + c + "\" for"
                                    + " state \"" + statename + "\" for policy \"" + policyname + ":" + policyversion
                                    + "\". The policy was created, but there was an error adding the context reference for the state."
                                    + " The policy has only been partially defined.");
                            return ret;
                        }
                        tempres = sessionApexModel.createPolicyStateContextRef(policyname, policyversion, statename,
                                c.getName(), c.getVersion());
                        if (tempres.isNOK()) {
                            tempres.addMessage("Failed to add context reference \"" + c + "\" for state \"" + statename
                                    + "\" for policy \"" + policyname + ":" + policyversion
                                    + "\". The policy was created, but there was an error adding the context reference for the state."
                                    + " The policy has only been partially defined.");
                            ret = tempres;
                            return ret;
                        }
                    }
                }

                final Map<String, BeanLogic> finalizers = state.getFinalizers();
                if (finalizers != null) {
                    for (final Map.Entry<String, BeanLogic> f : finalizers.entrySet()) {
                        final String finalizername = f.getKey();
                        final BeanLogic finalizer = f.getValue();
                        if (finalizername == null || finalizer == null) {
                            ret = new ApexAPIResult(RESULT.FAILED,
                                    "Null or invalid finalizer information for finalizer " + "named \"" + finalizername
                                            + "\" in state \"" + statename + "\" for policy \"" + policyname + ":"
                                            + policyversion
                                            + "\". The policy and state were created, but there was an error adding the finalizer."
                                            + " The policy has only been partially defined.");
                            return ret;
                        }
                        tempres = sessionApexModel.createPolicyStateFinalizerLogic(policyname, policyversion, statename,
                                finalizername, finalizer.getLogicFlavour(), finalizer.getLogic());
                        if (tempres.isNOK()) {
                            tempres.addMessage("Failed to add finalizer information for finalizer named \""
                                    + finalizername + "\" in" + " state \"" + statename + "\" for policy \""
                                    + policyname + ":" + policyversion
                                    + "\". The policy and state were created, but there was an error adding the finalizer."
                                    + " The policy has only been partially defined.");
                            ret = tempres;
                            return ret;
                        }
                    }
                }
                final Map<String, BeanStateOutput> outputs = state.getStateOutputs();
                if (outputs == null || outputs.isEmpty()) {
                    ret = new ApexAPIResult(RESULT.FAILED, "No state outputs have been defined in state \"" + statename
                            + "\" for policy \"" + policyname + ":" + policyversion
                            + "\". The policy and state were created, but there was an error adding state outputs."
                            + " The policy has only been partially defined.");
                    return ret;
                }
                for (final Map.Entry<String, BeanStateOutput> o : outputs.entrySet()) {
                    final String outputname = o.getKey();
                    final BeanStateOutput output = o.getValue();
                    if (outputname == null || output == null || output.getEvent() == null) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Null or invalid output information for output named \""
                                + outputname + "\" in state \"" + statename + "\" for policy \"" + policyname + ":"
                                + policyversion
                                + "\". The policy and state were created, but there was an error adding the output."
                                + " The policy has only been partially defined.");
                        return ret;
                    }
                    tempres = sessionApexModel.createPolicyStateOutput(policyname, policyversion, statename, outputname,
                            output.getEvent().getName(), output.getEvent().getVersion(), output.getNextState());
                    if (tempres.isNOK()) {
                        tempres.addMessage("Failed to add output information for output named \"" + outputname
                                + "\" in state \"" + statename + "\" for policy \"" + policyname + ":" + policyversion
                                + "\". The policy and state were created, but there was an error adding the output."
                                + " The policy has only been partially defined.");
                        ret = tempres;
                        return ret;
                    }
                }

                final Map<String, BeanStateTaskRef> taskmap = state.getTasks();
                if (taskmap == null || taskmap.isEmpty()) {
                    ret = new ApexAPIResult(RESULT.FAILED,
                            "No tasks have been defined in state \"" + statename + "\" for policy \"" + policyname + ":"
                                    + policyversion
                                    + "\". The policy and state were created, but there was an error adding tasks."
                                    + " The policy has only been partially defined.");
                    return ret;
                }
                for (final Map.Entry<String, BeanStateTaskRef> t : taskmap.entrySet()) {
                    final String tasklocalname = t.getKey();
                    final BeanStateTaskRef taskref = t.getValue();
                    if (tasklocalname == null || taskref == null || taskref.getTask() == null) {
                        ret = new ApexAPIResult(RESULT.FAILED, "Null or invalid task information for task named \""
                                + tasklocalname + "\" in state \"" + statename + "\" for for policy \"" + policyname
                                + ":" + policyversion
                                + "\". The policy and state were created, but there was an error adding the task. "
                                + "The policy has only been partially defined.");
                        return ret;
                    }
                    tempres = sessionApexModel.createPolicyStateTaskRef(policyname, policyversion, statename,
                            tasklocalname, taskref.getTask().getName(), taskref.getTask().getVersion(),
                            taskref.getOutputType(), taskref.getOutputName());
                    if (tempres.isNOK()) {
                        tempres.addMessage("Failed to add task reference \"" + t + "\" for state \"" + statename
                                + "\" for policy \"" + policyname + ":" + policyversion
                                + "\". The policy was created, but there was an error adding the task reference for the state."
                                + " The policy has only been partially defined.");
                        ret = tempres;
                        return ret;
                    }
                }

            }
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Policy/Create" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }
    // CHECKSTYLE:ON: MethodLength

    /**
     * Update a policy with the information in the JSON string passed.
     *
     * @param firstStatePeriodic indicates if periodic event should be created and added to model
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @PUT
    @Path("Policy/Update")
    public ApexAPIResult updatePolicy(@QueryParam("firstStatePeriodic") final boolean firstStatePeriodic,
            final String jsonString) {
        ApexAPIResult ret = null;
        LOGGER.entry(jsonString);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            final BeanPolicy jsonbean = RestUtils.getJSONParameters(jsonString, BeanPolicy.class);

            if (jsonbean.getName() == null || jsonbean.getName().equals("") || jsonbean.getVersion() == null
                    || jsonbean.getVersion().equals("")) {
                ret = new ApexAPIResult(RESULT.FAILED, "Null/Empty Policy name/version (\"" + jsonbean.getName() + ":"
                        + jsonbean.getVersion() + "\" passed to UpdatePolicy");
                return ret;
            }

            ret = sessionApexModel.deletePolicy(jsonbean.getName(), jsonbean.getVersion());
            if (ret.isNOK()) {
                return ret;
            }
            if (firstStatePeriodic) {
                final ApexAPIResult existingPeriodicEvent = sessionApexModel.listEvent("PeriodicEvent", null);
                if (existingPeriodicEvent.isNOK()) {
                    final String periodicEventString =
                            "{\"name\":\"PeriodicEvent\",\"version\":\"0.0.1\",\"uuid\":\"44236da1-3d47-4988-8033-b6fee9d6a0f4\",\"description\":\"Generated description for concept referred to by key 'PeriodicEvent:0.0.1'\",\"source\":\"System\",\"target\":\"Apex\",\"nameSpace\":\"org.onap.policy.apex.domains.aadm.events\",\"parameters\":{}}";
                    ret = createEvent(periodicEventString);
                    if (ret.isNOK()) {
                        return ret;
                    }
                }
            }
            ret = createPolicy(jsonString);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Policy/Update" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * List policies with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@link AxPolicy}, with merged {@linkplain AxKey Info} for the
     * root object.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @GET
    @Path("Policy/Get")
    public ApexAPIResult listPolicy(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForReadOnly();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            ret = sessionApexModel.listPolicy(name1, version1);

            if (ret.isNOK()) {
                return ret;
            }

            ret = addKeyInfo2Messages(ret);
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Policy/Get" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * Delete policies with the given key names/versions.
     *
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexAPIResult#isOK()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexAPIResult#getMessages()}
     */
    @DELETE
    @Path("Policy/Delete")
    public ApexAPIResult deletePolicy(@QueryParam("name") final String name,
            @QueryParam("version") final String version) {
        ApexAPIResult ret = null;
        String name1 = name;
        String version1 = version;
        LOGGER.entry(name1, version1);
        try {
            ret = initialiseSessionForChanges();
            if (ret.isNOK()) {
                return ret;
            }

            if (name1 == null || name1.equals("")) {
                name1 = null;
            }
            if (version1 == null || version1.equals("")) {
                version1 = null;
            }

            // all input/output fields, parameters, logic, context references is "owned"/contained in the task, so
            // deleting the task removes all of these
            ret = sessionApexModel.deletePolicy(name1, version1);
            if (ret.isOK()) {
                commitChanges();
            }
            return ret;
        } catch (final Exception e) {
            LOGGER.catching(e);
            throw e;
        } finally {
            LOGGER.exit((ret == null ? false : ret.isOK()));
            LOGGER.info("Policy/Delete" + (ret != null && ret.isOK() ? ": OK" : ": Not OK"));
        }
    }

    /**
     * The json strings representing the objects listed, stored in result.messages[], does not contain the
     * AxKeyInformation for that object. This utility method retrieves the AxKeyInfo for each object and adds it to the
     * json for the object.
     *
     * @param result The list result, containing json representations of objects stored in its "messages" array
     * @return The list result, containing json augmented representations of objects stored in its "messages" array
     */
    private ApexAPIResult addKeyInfo2Messages(final ApexAPIResult result) {
        if (result.isNOK()) {
            return result;
        }

        final ApexAPIResult ret = new ApexAPIResult(result.getResult());
        ret.setMessages(result.getMessages());

        final List<String> messages = result.getMessages();
        final List<String> augmessages = new ArrayList<>(messages.size());
        final GsonBuilder gb = new GsonBuilder();
        gb.serializeNulls().enableComplexMapKeySerialization();
        final Gson gson = gb.create();
        for (final String message : messages) {
            try {
                final JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
                JsonObject objecttochange = jsonObject;
                String name = null;
                if (jsonObject != null && jsonObject.get("key") != null && jsonObject.get("key").isJsonObject()
                        && jsonObject.getAsJsonObject("key").get("name") != null) {
                    name = jsonObject.getAsJsonObject("key").get("name").getAsString();
                } else if (jsonObject != null && jsonObject.get("policyKey") != null
                        && jsonObject.get("policyKey").isJsonObject()
                        && jsonObject.getAsJsonObject("policyKey").get("name") != null) {
                    name = jsonObject.getAsJsonObject("policyKey").get("name").getAsString();
                }
                String version = null;
                if (jsonObject != null && jsonObject.get("key") != null && jsonObject.get("key").isJsonObject()
                        && jsonObject.getAsJsonObject("key").get("version") != null) {
                    version = jsonObject.getAsJsonObject("key").get("version").getAsString();
                } else if (jsonObject != null && jsonObject.get("policyKey") != null
                        && jsonObject.get("policyKey").isJsonObject()
                        && jsonObject.getAsJsonObject("policyKey").get("version") != null) {
                    version = jsonObject.getAsJsonObject("policyKey").get("version").getAsString();
                }

                if (name == null && version == null && jsonObject.entrySet() != null
                        && jsonObject.entrySet().size() > 0) {
                    objecttochange = (JsonObject) jsonObject.entrySet().iterator().next().getValue();
                    if (objecttochange != null && objecttochange.get("key") != null
                            && objecttochange.get("key").isJsonObject()
                            && objecttochange.getAsJsonObject("key").get("name") != null) {
                        name = objecttochange.getAsJsonObject("key").get("name").getAsString();
                    } else if (objecttochange != null && objecttochange.get("policyKey") != null
                            && objecttochange.get("policyKey").isJsonObject()
                            && objecttochange.getAsJsonObject("policyKey").get("name") != null) {
                        name = objecttochange.getAsJsonObject("policyKey").get("name").getAsString();
                    }
                    if (objecttochange != null && objecttochange.get("key") != null
                            && objecttochange.get("key").isJsonObject()
                            && objecttochange.getAsJsonObject("key").get("version") != null) {
                        version = objecttochange.getAsJsonObject("key").get("version").getAsString();
                    } else if (objecttochange != null && objecttochange.get("policyKey") != null
                            && objecttochange.get("policyKey").isJsonObject()
                            && objecttochange.getAsJsonObject("policyKey").get("version") != null) {
                        version = objecttochange.getAsJsonObject("policyKey").get("version").getAsString();
                    }
                }

                String uuid = null;
                String desc = null;

                if (name != null && version != null) {
                    final ApexAPIResult keyInfoResult = sessionApexModel.listKeyInformation(name, version);
                    final List<String> keyInfoMessages = keyInfoResult.getMessages();
                    if (keyInfoResult.isOK() && keyInfoMessages != null && keyInfoMessages.size() > 0) {
                        final String keyInfoJson = keyInfoMessages.get(0);
                        final JsonObject keyInfoJsonObject = gson.fromJson(keyInfoJson, JsonObject.class);
                        if (keyInfoJsonObject != null && keyInfoJsonObject.get("apexKeyInfo") != null
                                && keyInfoJsonObject.get("apexKeyInfo").getAsJsonObject().get("UUID") != null) {
                            uuid = keyInfoJsonObject.get("apexKeyInfo").getAsJsonObject().get("UUID").getAsString();
                        }
                        if (keyInfoJsonObject != null && keyInfoJsonObject.get("apexKeyInfo") != null
                                && keyInfoJsonObject.get("apexKeyInfo").getAsJsonObject().get("description") != null) {
                            desc = keyInfoJsonObject.get("apexKeyInfo").getAsJsonObject().get("description")
                                    .getAsString();
                        }
                    }
                }
                objecttochange.addProperty("uuid", uuid);
                objecttochange.addProperty("description", desc);
                augmessages.add(gson.toJson(jsonObject));
            } catch (final Exception e) {
                augmessages.add(message);
            }
        }
        ret.setMessages(augmessages);

        if (messages.size() != augmessages.size()) {
            ret.setResult(RESULT.OTHER_ERROR);
            ret.addMessage("Failed to add KeyInfo to all results. Results are not complete");
        }

        return ret;
    }

    /*
     * This method is used only for testing and is used to cause an exception on calls from unit test to test exception
     * handling.
     */
    protected static int createCorruptSession() {
        final ApexEditorRestResource apexEditorRestResource = new ApexEditorRestResource();
        final ApexAPIResult result = apexEditorRestResource.createSession();
        final int corruptSessionId = new Integer(result.getMessages().get(0));

        SESSIONMODELMAP.put(corruptSessionId, null);

        return corruptSessionId;
    }
}
