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

package org.onap.policy.apex.model.modelapi.impl;

import java.util.Properties;

import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * This class is an implementation of a facade on an Apex model for editors of Apex models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class ApexModelImpl implements ApexModel {
    // The policy model being acted upon
    private AxPolicyModel policyModel = new AxPolicyModel();

    // The file name for the loaded file
    private String fileName = null;

    // @formatter:off
    private ModelFacade modelFacade;
    private KeyInformationFacade keyInformationFacade;
    private ContextSchemaFacade contextSchemaFacade;
    private EventFacade eventFacade;
    private ContextAlbumFacade contextAlbumFacade;
    private TaskFacade taskFacade;
    private PolicyFacade policyFacade;
    private ModelHandlerFacade modelHandlerFacade;
    // @formatter:on

    private Properties apexProperties;
    private boolean jsonMode;

    /**
     * Create an implementation of the Apex editor and model APIs.
     *
     * @param apexProperties The properties to use for the model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     */
    public ApexModelImpl(final Properties apexProperties, final boolean jsonMode) {
        this.apexProperties = apexProperties;
        this.jsonMode = jsonMode;

        // @formatter:off
        this.modelFacade          = new ModelFacade(this, apexProperties, jsonMode);
        this.keyInformationFacade = new KeyInformationFacade(this, apexProperties, jsonMode);
        this.contextSchemaFacade  = new ContextSchemaFacade(this, apexProperties, jsonMode);
        this.eventFacade          = new EventFacade(this, apexProperties, jsonMode);
        this.contextAlbumFacade   = new ContextAlbumFacade(this, apexProperties, jsonMode);
        this.taskFacade           = new TaskFacade(this, apexProperties, jsonMode);
        this.policyFacade         = new PolicyFacade(this, apexProperties, jsonMode);
        this.modelHandlerFacade   = new ModelHandlerFacade(this, apexProperties, jsonMode);
        // @formatter:on
    }

    /**
     * Constructor, prevents this class being sub-classed.
     */
    private ApexModelImpl() {}

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#clone()
     */
    @Override
    public ApexModel clone() {
        ApexModelImpl ret = new ApexModelImpl();
        // @formatter:off
        ret.policyModel          = new AxPolicyModel(policyModel);
        ret.fileName             = this.fileName;
        ret.apexProperties       = this.apexProperties;
        ret.jsonMode             = this.jsonMode;
        ret.modelFacade          = new ModelFacade(ret, this.apexProperties, this.jsonMode);
        ret.keyInformationFacade = new KeyInformationFacade(ret, this.apexProperties, this.jsonMode);
        ret.contextSchemaFacade  = new ContextSchemaFacade(ret, this.apexProperties, this.jsonMode);
        ret.eventFacade          = new EventFacade(ret, this.apexProperties, this.jsonMode);
        ret.contextAlbumFacade   = new ContextAlbumFacade(ret, this.apexProperties, this.jsonMode);
        ret.taskFacade           = new TaskFacade(ret, this.apexProperties, this.jsonMode);
        ret.policyFacade         = new PolicyFacade(ret, this.apexProperties, this.jsonMode);
        ret.modelHandlerFacade   = new ModelHandlerFacade(ret, this.apexProperties, this.jsonMode);
        // @formatter:on

        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#createModel(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createModel(final String name, final String version, final String uuid,
            final String description) {
        return modelFacade.createModel(name, version, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#updateModel(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updateModel(final String name, final String version, final String uuid,
            final String description) {
        return modelFacade.updateModel(name, version, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexEditorAPI#getModelKey()
     */
    @Override
    public ApexAPIResult getModelKey() {
        return modelFacade.getModelKey();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listModel()
     */
    @Override
    public ApexAPIResult listModel() {
        return modelFacade.listModel();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteModel()
     */
    @Override
    public ApexAPIResult deleteModel() {
        return modelFacade.deleteModel();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createKeyInformation(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createKeyInformation(final String name, final String version, final String uuid,
            final String description) {
        return keyInformationFacade.createKeyInformation(name, version, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updateKeyInformation(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updateKeyInformation(final String name, final String version, final String uuid,
            final String description) {
        return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listKeyInformation(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult listKeyInformation(final String name, final String version) {
        return keyInformationFacade.listKeyInformation(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteKeyInformation(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deleteKeyInformation(final String name, final String version) {
        return keyInformationFacade.deleteKeyInformation(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#validateKeyInformation(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult validateKeyInformation(final String name, final String version) {
        return keyInformationFacade.validateKeyInformation(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createContextSchema(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createContextSchema(final String name, final String version, final String schemaFlavour,
            final String schemaDefinition, final String uuid, final String description) {
        return contextSchemaFacade.createContextSchema(name, version, schemaFlavour, schemaDefinition, uuid,
                description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updateContextSchema(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updateContextSchema(final String name, final String version, final String schemaFlavour,
            final String schemaDefinition, final String uuid, final String description) {
        return contextSchemaFacade.updateContextSchema(name, version, schemaFlavour, schemaDefinition, uuid,
                description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listContextSchemas(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult listContextSchemas(final String name, final String version) {
        return contextSchemaFacade.listContextSchemas(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteContextSchema(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deleteContextSchema(final String name, final String version) {
        return contextSchemaFacade.deleteContextSchema(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#validateContextSchemas(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult validateContextSchemas(final String name, final String version) {
        return contextSchemaFacade.validateContextSchemas(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createEvent(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult createEvent(final String name, final String version, final String nameSpace,
            final String source, final String target, final String uuid, final String description) {
        return eventFacade.createEvent(name, version, nameSpace, source, target, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updateEvent(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult updateEvent(final String name, final String version, final String nameSpace,
            final String source, final String target, final String uuid, final String description) {
        return eventFacade.updateEvent(name, version, nameSpace, source, target, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listEvent(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult listEvent(final String name, final String version) {
        return eventFacade.listEvent(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteEvent(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deleteEvent(final String name, final String version) {
        return eventFacade.deleteEvent(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#validateEvent(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult validateEvent(final String name, final String version) {
        return eventFacade.validateEvent(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.modelapi.ApexEditorAPI#createEventPar(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public ApexAPIResult createEventPar(final String name, final String version, final String parName,
            final String contextSchemaName, final String contextSchemaVersion, final boolean optional) {
        return eventFacade.createEventPar(name, version, parName, contextSchemaName, contextSchemaVersion, optional);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listEventPar(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listEventPar(final String name, final String version, final String parName) {
        return eventFacade.listEventPar(name, version, parName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteEventPar(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deleteEventPar(final String name, final String version, final String parName) {
        return eventFacade.deleteEventPar(name, version, parName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.modelapi.ApexEditorAPI#createContextAlbum(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public ApexAPIResult createContextAlbum(final String name, final String version, final String scope,
            final String writable, final String contextSchemaName, final String contextSchemaVersion, final String uuid,
            final String description) {
        return contextAlbumFacade.createContextAlbum(name, version, scope, writable, contextSchemaName,
                contextSchemaVersion, uuid, description);
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.modelapi.ApexEditorAPI#updateContextAlbum(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    @Override
    public ApexAPIResult updateContextAlbum(final String name, final String version, final String scope,
            final String writable, final String contextSchemaName, final String contextSchemaVersion, final String uuid,
            final String description) {
        return contextAlbumFacade.updateContextAlbum(name, version, scope, writable, contextSchemaName,
                contextSchemaVersion, uuid, description);
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listContextAlbum(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult listContextAlbum(final String name, final String version) {
        return contextAlbumFacade.listContextAlbum(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteContextAlbum(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deleteContextAlbum(final String name, final String version) {
        return contextAlbumFacade.deleteContextAlbum(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#validateContextAlbum(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult validateContextAlbum(final String name, final String version) {
        return contextAlbumFacade.validateContextAlbum(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createTask(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createTask(final String name, final String version, final String uuid,
            final String description) {
        return taskFacade.createTask(name, version, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updateTask(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updateTask(final String name, final String version, final String uuid,
            final String description) {
        return taskFacade.updateTask(name, version, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listTask(java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listTask(final String name, final String version) {
        return taskFacade.listTask(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteTask(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deleteTask(final String name, final String version) {
        return taskFacade.deleteTask(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#validateTask(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult validateTask(final String name, final String version) {
        return taskFacade.validateTask(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createTaskLogic(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic) {
        return taskFacade.createTaskLogic(name, version, logicFlavour, logic);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updateTaskLogic(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updateTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic) {
        return taskFacade.updateTaskLogic(name, version, logicFlavour, logic);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listTaskLogic(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult listTaskLogic(final String name, final String version) {
        return taskFacade.listTaskLogic(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteTaskLogic(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deleteTaskLogic(final String name, final String version) {
        return taskFacade.deleteTaskLogic(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createTaskInputField(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.boolean)
     */
    @Override
    public ApexAPIResult createTaskInputField(final String name, final String version, final String fieldName,
            final String dataTypeName, final String dataTypeVersion, final boolean optional) {
        return taskFacade.createTaskInputField(name, version, fieldName, dataTypeName, dataTypeVersion, optional);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listTaskInputField(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listTaskInputField(final String name, final String version, final String fieldName) {
        return taskFacade.listTaskInputField(name, version, fieldName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteTaskInputField(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deleteTaskInputField(final String name, final String version, final String fieldName) {
        return taskFacade.deleteTaskInputField(name, version, fieldName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createTaskOutputField(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.boolean)
     */
    @Override
    public ApexAPIResult createTaskOutputField(final String name, final String version, final String fieldName,
            final String dataTypeName, final String dataTypeVersion, final boolean optional) {
        return taskFacade.createTaskOutputField(name, version, fieldName, dataTypeName, dataTypeVersion, optional);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listTaskOutputField(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listTaskOutputField(final String name, final String version, final String fieldName) {
        return taskFacade.listTaskOutputField(name, version, fieldName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteTaskOutputField(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deleteTaskOutputField(final String name, final String version, final String fieldName) {
        return taskFacade.deleteTaskOutputField(name, version, fieldName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createTaskParameter(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createTaskParameter(final String name, final String version, final String parName,
            final String defaultValue) {
        return taskFacade.createTaskParameter(name, version, parName, defaultValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listTaskParameter(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listTaskParameter(final String name, final String version, final String parName) {
        return taskFacade.listTaskParameter(name, version, parName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteTaskParameter(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deleteTaskParameter(final String name, final String version, final String parName) {
        return taskFacade.deleteTaskParameter(name, version, parName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createTaskContextRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        return taskFacade.createTaskContextRef(name, version, contextAlbumName, contextAlbumVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listTaskContextRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        return taskFacade.listTaskContextRef(name, version, contextAlbumName, contextAlbumVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deleteTaskContextRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deleteTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        return taskFacade.deleteTaskContextRef(name, version, contextAlbumName, contextAlbumVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createPolicy(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createPolicy(final String name, final String version, final String template,
            final String firstState, final String uuid, final String description) {
        return policyFacade.createPolicy(name, version, template, firstState, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updatePolicy(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updatePolicy(final String name, final String version, final String template,
            final String firstState, final String uuid, final String description) {
        return policyFacade.updatePolicy(name, version, template, firstState, uuid, description);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listPolicy(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicy(final String name, final String version) {
        return policyFacade.listPolicy(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deletePolicy(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicy(final String name, final String version) {
        return policyFacade.deletePolicy(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#validatePolicy(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult validatePolicy(final String name, final String version) {
        return policyFacade.validatePolicy(name, version);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createPolicyState(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult createPolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion) {
        return policyFacade.createPolicyState(name, version, stateName, triggerName, triggerVersion, defaultTaskName,
                defaltTaskVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#updatePolicyState(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult updatePolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion) {
        return policyFacade.updatePolicyState(name, version, stateName, triggerName, triggerVersion, defaultTaskName,
                defaltTaskVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listPolicyState(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicyState(final String name, final String version, final String stateName) {
        return policyFacade.listPolicyState(name, version, stateName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deletePolicyState(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicyState(final String name, final String version, final String stateName) {
        return policyFacade.deletePolicyState(name, version, stateName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.auth.api.ApexEditorAPI#createPolicyStateTaskSelectionLogic(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createPolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName, final String logicFlavour, final String logic) {
        return policyFacade.createPolicyStateTaskSelectionLogic(name, version, stateName, logicFlavour, logic);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.auth.api.ApexEditorAPI#updatePolicyStateTaskSelectionLogic(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult updatePolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName, final String logicFlavour, final String logic) {
        return policyFacade.updatePolicyStateTaskSelectionLogic(name, version, stateName, logicFlavour, logic);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listPolicyStateTaskSelectionLogic(java.lang.
     * String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName) {
        return policyFacade.listPolicyStateTaskSelectionLogic(name, version, stateName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.auth.api.ApexEditorAPI#deletePolicyStateTaskSelectionLogic(java.lang.
     * String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName) {
        return policyFacade.deletePolicyStateTaskSelectionLogic(name, version, stateName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#createPolicyStateOutput(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult createPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName, final String eventName, final String eventVersion, final String nextState) {
        return policyFacade.createPolicyStateOutput(name, version, stateName, outputName, eventName, eventVersion,
                nextState);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listPolicyStateOutput(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName) {
        return policyFacade.listPolicyStateOutput(name, version, stateName, outputName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deletePolicyStateOutput(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName) {
        return policyFacade.deletePolicyStateOutput(name, version, stateName, outputName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.modelapi.ApexEditorAPI#createPolicyStateFinalizerLogic(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult createPolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName, final String logicFlavour, final String logic) {
        return policyFacade.createPolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName, logicFlavour,
                logic);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.modelapi.ApexEditorAPI#updatePolicyStateFinalizerLogic(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ApexAPIResult updatePolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName, final String logicFlavour, final String logic) {
        return policyFacade.updatePolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName, logicFlavour,
                logic);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.modelapi.ApexEditorAPI#listPolicyStateFinalizerLogic(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName) {
        return policyFacade.listPolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.modelapi.ApexEditorAPI#deletePolicyStateFinalizerLogic(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName) {
        return policyFacade.deletePolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.modelapi.ApexEditorAPI#createPolicyStateTaskRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public ApexAPIResult createPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskLocalName, final String taskName, final String taskVersion, final String outputType,
            final String outputName) {
        return policyFacade.createPolicyStateTaskRef(name, version, stateName, taskLocalName, taskName, taskVersion,
                outputType, outputName);
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listPolicyStateTaskRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion) {
        return policyFacade.listPolicyStateTaskRef(name, version, stateName, taskName, taskVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#deletePolicyStateTaskRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion) {
        return policyFacade.deletePolicyStateTaskRef(name, version, stateName, taskName, taskVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.auth.api.ApexEditorAPI#createPolicyStateContextRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult createPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        return policyFacade.createPolicyStateContextRef(name, version, stateName, contextAlbumName,
                contextAlbumVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexEditorAPI#listPolicyStateContextRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult listPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        return policyFacade.listPolicyStateContextRef(name, version, stateName, contextAlbumName, contextAlbumVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.auth.api.ApexEditorAPI#deletePolicyStateContextRef(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult deletePolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        return policyFacade.deletePolicyStateContextRef(name, version, stateName, contextAlbumName,
                contextAlbumVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#loadFromString(java.lang.String)
     */
    @Override
    public ApexAPIResult loadFromString(final String modelString) {
        return modelHandlerFacade.loadFromString(modelString);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#loadFromFile(java.lang.String)
     */
    @Override
    // CHECKSTYLE:OFF: checkstyle:HiddenField
    public ApexAPIResult loadFromFile(final String fileName) {
        this.fileName = fileName;
        return modelHandlerFacade.loadFromFile(fileName);
    }
    // CHECKSTYLE:ON: checkstyle:HiddenField

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#saveToFile(java.lang.String, boolean)
     */
    @Override
    public ApexAPIResult saveToFile(final String saveFileName, final boolean xmlFlag) {
        if (saveFileName == null) {
            return modelHandlerFacade.saveToFile(fileName, xmlFlag);
        } else {
            return modelHandlerFacade.saveToFile(saveFileName, xmlFlag);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.modelapi.ApexModel#loadFromDatabase(java.lang.String,
     * java.lang.String, org.onap.policy.apex.model.basicmodel.dao.DaoParameters)
     */
    @Override
    public ApexAPIResult loadFromDatabase(final String modelName, final String modelVersion,
            final DaoParameters DaoParameters) {
        return modelHandlerFacade.loadFromDatabase(modelName, modelVersion, DaoParameters);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.modelapi.ApexModel#saveToDatabase(org.onap.policy.apex.model.
     * basicmodel. dao.DaoParameters)
     */
    @Override
    public ApexAPIResult saveToDatabase(final DaoParameters DaoParameters) {
        return modelHandlerFacade.saveToDatabase(DaoParameters);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#readFromURL(java.lang.String)
     */
    @Override
    public ApexAPIResult readFromURL(final String urlString) {
        return modelHandlerFacade.readFromURL(urlString);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#writeToURL(java.lang.String, boolean)
     */
    @Override
    public ApexAPIResult writeToURL(final String urlString, final boolean xmlFlag) {
        return modelHandlerFacade.writeToURL(urlString, xmlFlag);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#analyse()
     */
    @Override
    public ApexAPIResult analyse() {
        return modelHandlerFacade.analyse();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#validate()
     */
    @Override
    public ApexAPIResult validate() {
        return modelHandlerFacade.validate();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#compare(java.lang.String, boolean, boolean)
     */
    @Override
    public ApexAPIResult compare(final String otherModelFileName, final boolean diffsOnly, final boolean keysOnly) {
        return modelHandlerFacade.compare(otherModelFileName, diffsOnly, keysOnly);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#compareWithString(java.lang.String,
     * boolean, boolean)
     */
    @Override
    public ApexAPIResult compareWithString(final String otherModelString, final boolean diffsOnly,
            final boolean keysOnly) {
        return modelHandlerFacade.compareWithString(otherModelString, diffsOnly, keysOnly);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#split(java.lang.String, java.lang.String)
     */
    @Override
    public ApexAPIResult split(final String targetModelFileName, final String splitOutPolicies) {
        return modelHandlerFacade.split(targetModelFileName, splitOutPolicies);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#split(java.lang.String)
     */
    @Override
    public ApexAPIResult split(final String splitOutPolicies) {
        return modelHandlerFacade.split(splitOutPolicies);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#merge(java.lang.String, boolean)
     */
    @Override
    public ApexAPIResult merge(final String mergeInModelFileName, final boolean keepOriginal) {
        return modelHandlerFacade.merge(mergeInModelFileName, keepOriginal);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#mergeWithString(java.lang.String, boolean)
     */
    @Override
    public ApexAPIResult mergeWithString(final String otherModelString, final boolean keepOriginal) {
        return modelHandlerFacade.mergeWithString(otherModelString, keepOriginal);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.auth.api.ApexModel#getModel()
     */
    @Override
    public AxPolicyModel getPolicyModel() {
        return policyModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.auth.api.ApexModel#setPolicyModel(org.onap.policy.apex.core.policymodel.
     * concepts.AxPolicyModel)
     */
    @Override
    public void setPolicyModel(final AxPolicyModel policyModel) {
        this.policyModel = policyModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.modelapi.ApexModel#build()
     */
    @Override
    public AxPolicyModel build() {
        return policyModel;
    }

}
