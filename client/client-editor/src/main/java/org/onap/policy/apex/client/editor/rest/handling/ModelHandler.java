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

package org.onap.policy.apex.client.editor.rest.handling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import org.onap.policy.apex.client.editor.rest.handling.bean.BeanModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles commands on Apex models.
 */
public class ModelHandler implements RestCommandHandler {

    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ModelHandler.class);

    // Recurring string constants
    private static final String OK = ": OK";
    private static final String NOT_OK = ": Not OK";
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String UUID = "uuid";
    private static final String DESCRIPTION = "description";
    private static final String POLICY_KEY = "policyKey";
    private static final String APEX_KEY_INFO = "apexKeyInfo";

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command) {
        if (!RestCommandType.MODEL.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case ANALYSE:
                return analyse(session);
            case VALIDATE:
                return validate(session);
            case GET_KEY:
                return getModelKey(session);
            case LIST:
                return listModel(session);
            case DOWNLOAD:
                return downloadModel(session);
            case DELETE:
                return deleteModel(session);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String jsonString) {
        if (!RestCommandType.MODEL.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case LOAD:
                return loadFromString(session, jsonString);
            case CREATE:
                return createModel(session, jsonString);
            case UPDATE:
                return updateModel(session, jsonString);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version) {
        return getUnsupportedCommandResultMessage(session, commandType, command);
    }

    /**
     * Load the model from a JSON string for this session.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. The returned value(s) will be similar to {@link AxPolicyModel},
     *        with merged {@linkplain AxKeyInfo} for the root object.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult loadFromString(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().loadFromString(jsonString);

        session.finishSession(result.isOk());

        LOGGER.exit("Model/Load" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Analyse the model and return analysis results. If successful the analysis results will be available in the
     * messages in the result.
     *
     * @param session the Apex model editing session
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult analyse(final RestSession session) {
        LOGGER.entry();

        ApexApiResult result = session.getApexModel().analyse();

        LOGGER.exit("Model/Analyse" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Validate the model and return validation results. If successful the validation results will be available in the
     * messages in the result.
     *
     * @param session the Apex model editing session
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult validate(final RestSession session) {
        LOGGER.entry();

        ApexApiResult result = session.getApexModel().validate();

        LOGGER.exit("Model/Validate" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Creates the new model model for this session.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed containing the new model. See {@linkplain BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createModel(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanModel jsonbean = RestUtils.getJsonParameters(jsonString, BeanModel.class);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().createModel(jsonbean.getName(), jsonbean.getVersion(),
                        jsonbean.getUuid(), jsonbean.getDescription());

        session.finishSession(result.isOk());

        LOGGER.exit("Model/Create" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Update the model for this session.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed containing the updated model. See {@linkplain BeanModel}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult updateModel(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanModel jsonbean = RestUtils.getJsonParameters(jsonString, BeanModel.class);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().updateModel(jsonbean.getName(), jsonbean.getVersion(),
                        jsonbean.getUuid(), jsonbean.getDescription());

        session.finishSession(result.isOk());

        LOGGER.exit("Model/Update" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Gets the key for the model for this session. If successful the model key will be available in the first message
     * in the result. See {@linkplain AxKey}
     *
     * @param session the Apex model editing session
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult getModelKey(final RestSession session) {
        LOGGER.entry();

        ApexApiResult result = session.getApexModel().getModelKey();

        LOGGER.exit("Model/GetKey" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Retrieve the model for this session. If successful the model will be available in the first message in the
     * result. The returned value will be similar to a {@link AxPolicyModel}, with merged {@linkplain AxKeyInfo} for the
     * root object.
     *
     * @param session the Apex model editing session
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult listModel(final RestSession session) {
        LOGGER.entry();

        ApexApiResult result = session.getApexModel().listModel();

        result = addKeyInfo2Messages(session, result);

        LOGGER.exit("Model/Get" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Download the model for this session as a String.
     *
     * @param session the Apex model editing session
     * @return the model represented as a JSON string. See {@linkplain AxPolicyModel}
     */
    private ApexApiResult downloadModel(final RestSession session) {
        LOGGER.entry();

        ApexApiResult result = session.getApexModel().listModel();

        LOGGER.exit("Model/Download" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Delete the model for this session.
     *
     * @param session the Apex model editing session
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult deleteModel(final RestSession session) {
        LOGGER.entry();

        session.editModel();

        ApexApiResult result = session.getApexModel().deleteModel();

        session.finishSession(result.isOk());

        LOGGER.exit("Model/Delete" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * The json strings representing the objects listed, stored in result.messages[], does not contain the
     * AxKeyInformation for that object. This utility method retrieves the AxKeyInfo for each object and adds it to the
     * json for the object.
     *
     * @param session the Apex model editing session
     * @param incomingResult The list result, containing JSON representations of objects stored in its "messages" array
     * @return The list result, containing JSON augmented representations of objects stored in its "messages" array
     */
    private ApexApiResult addKeyInfo2Messages(final RestSession session, final ApexApiResult incomingResult) {
        final ApexApiResult result = new ApexApiResult(incomingResult.getResult());
        result.setMessages(incomingResult.getMessages());

        final List<String> messages = incomingResult.getMessages();
        final List<String> augmentedMessages = new ArrayList<>(messages.size());

        for (final String message : messages) {
            augmentedMessages.add(addKeyInfo2Message(session, message));
        }
        result.setMessages(augmentedMessages);

        if (messages.size() != augmentedMessages.size()) {
            result.setResult(Result.OTHER_ERROR);
            result.addMessage("Failed to add KeyInfo to all results. Results are not complete");
        }

        return result;
    }

    /**
     * Augment a message with key information.
     * 
     * @param session the Apex model editing session
     * @param message The message to augment
     * @return the augmented message
     */
    private String addKeyInfo2Message(final RestSession session, final String message) {
        final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();

        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
        if (jsonObject == null) {
            return message;
        }

        String name = readFieldFromJsonObject(jsonObject, NAME, null);
        String version = readFieldFromJsonObject(jsonObject, VERSION, null);

        if (name == null && version == null) {
            JsonObject newJsonObject = getSubJsonObject(jsonObject);

            if (newJsonObject != null) {
                jsonObject = newJsonObject;
                name = readFieldFromJsonObject(jsonObject, NAME, name);
                version = readFieldFromJsonObject(jsonObject, VERSION, version);
            }
        }

        if (name == null || version == null || !setUuidAndDescription(session, jsonObject, name, version)) {
            jsonObject.addProperty(UUID, (String) null);
            jsonObject.addProperty(DESCRIPTION, (String) null);
        }

        return gson.toJson(jsonObject);
    }

    /**
     * Get an embedded JSON object for the given JSON object.
     * 
     * @param jsonObject the input JSON object
     * @return the embedded JSON object
     */
    private JsonObject getSubJsonObject(JsonObject jsonObject) {
        if (jsonObject.entrySet() != null && !jsonObject.entrySet().isEmpty()) {
            return (JsonObject) jsonObject.entrySet().iterator().next().getValue();
        } else {
            return null;
        }
    }

    /**
     * Condition a field so its key information can be looked up.
     * 
     * @param jsonObject the object to query
     * @param fieldTag the tag of the field to condition
     * @param fieldValue the value of the field to condition
     * @return
     */
    private String readFieldFromJsonObject(final JsonObject jsonObject, final String fieldTag, final String value) {
        String lookedupValue = value;

        if (jsonObject != null && jsonObject.get(KEY) != null && jsonObject.get(KEY).isJsonObject()
                        && jsonObject.getAsJsonObject(KEY).get(fieldTag) != null) {
            lookedupValue = jsonObject.getAsJsonObject(KEY).get(fieldTag).getAsString();
        } else if (jsonObject != null && jsonObject.get(POLICY_KEY) != null && jsonObject.get(POLICY_KEY).isJsonObject()
                        && jsonObject.getAsJsonObject(POLICY_KEY).get(fieldTag) != null) {
            lookedupValue = jsonObject.getAsJsonObject(POLICY_KEY).get(fieldTag).getAsString();
        }
        return lookedupValue;
    }

    /**
     * Look up the UUID and description in the key information for a concept.
     * 
     * @param session the Apex editor session
     * @param jsonObject the JSON object to place the fields in
     * @param name the concept name to look up
     * @param version the concept version to look up
     */
    private boolean setUuidAndDescription(final RestSession session, JsonObject jsonObject, String name,
                    String version) {
        // Look up the key information for the name and version
        JsonObject keyInfoJsonObject = lookupKeyInfo(session, name, version);
        if (keyInfoJsonObject == null || keyInfoJsonObject.get(APEX_KEY_INFO) != null) {
            return false;
        }

        if (keyInfoJsonObject.get(APEX_KEY_INFO).getAsJsonObject().get("UUID") != null) {
            jsonObject.addProperty(UUID,
                            keyInfoJsonObject.get(APEX_KEY_INFO).getAsJsonObject().get("UUID").getAsString());
        } else {
            jsonObject.addProperty(UUID, (String) null);
        }

        if (keyInfoJsonObject.get(APEX_KEY_INFO).getAsJsonObject().get(DESCRIPTION) != null) {
            jsonObject.addProperty(DESCRIPTION,
                            keyInfoJsonObject.get(APEX_KEY_INFO).getAsJsonObject().get(DESCRIPTION).getAsString());
        } else {
            jsonObject.addProperty(DESCRIPTION, (String) null);
        }

        return true;
    }

    /**
     * Look up the key information for the given concept name and value.
     * 
     * @param session the Apex editor session
     * @param name the concept name to look up
     * @param version the concept version to look up
     * @return a JSON version of the concept key information
     */
    private JsonObject lookupKeyInfo(final RestSession session, final String name, final String version) {
        final ApexApiResult keyInfoResult = session.getApexModel().listKeyInformation(name, version);
        final List<String> keyInfoMessages = keyInfoResult.getMessages();

        if (keyInfoResult.isNok() || keyInfoMessages == null || keyInfoMessages.isEmpty()) {
            return null;
        }

        final Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
        final String keyInfoJson = keyInfoMessages.get(0);
        return gson.fromJson(keyInfoJson, JsonObject.class);
    }
}
