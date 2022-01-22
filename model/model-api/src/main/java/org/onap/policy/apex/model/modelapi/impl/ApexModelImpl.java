/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Samsung Electronics Co., Ltd.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of a facade on an Apex model for editors of Apex models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApexModelImpl implements ApexModel {

    public static final String FIELDS_DEPRECATED_WARN_MSG =
        "inputFields and outputFields are deprecated from Task definition and will be removed. "
            + "Instead, inputEvent and outputEvents are automatically populated to Tasks based on State definition";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApexModelImpl.class);

    // The policy model being acted upon
    @Getter
    @Setter
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
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise set to false
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
     * {@inheritDoc}.
     */
    @Override
    public ApexModel getCopy() {
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createModel(final String name, final String version, final String uuid,
            final String description) {
        return modelFacade.createModel(name, version, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updateModel(final String name, final String version, final String uuid,
            final String description) {
        return modelFacade.updateModel(name, version, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult getModelKey() {
        return modelFacade.getModelKey();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listModel() {
        return modelFacade.listModel();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteModel() {
        return modelFacade.deleteModel();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createKeyInformation(final String name, final String version, final String uuid,
            final String description) {
        return keyInformationFacade.createKeyInformation(name, version, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updateKeyInformation(final String name, final String version, final String uuid,
            final String description) {
        return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listKeyInformation(final String name, final String version) {
        return keyInformationFacade.listKeyInformation(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteKeyInformation(final String name, final String version) {
        return keyInformationFacade.deleteKeyInformation(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validateKeyInformation(final String name, final String version) {
        return keyInformationFacade.validateKeyInformation(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createContextSchema(final String name, final String version, final String schemaFlavour,
            final String schemaDefinition, final String uuid, final String description) {
        return contextSchemaFacade.createContextSchema(name, version, schemaFlavour, schemaDefinition, uuid,
                description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updateContextSchema(final String name, final String version, final String schemaFlavour,
            final String schemaDefinition, final String uuid, final String description) {
        return contextSchemaFacade.updateContextSchema(name, version, schemaFlavour, schemaDefinition, uuid,
                description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listContextSchemas(final String name, final String version) {
        return contextSchemaFacade.listContextSchemas(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteContextSchema(final String name, final String version) {
        return contextSchemaFacade.deleteContextSchema(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validateContextSchemas(final String name, final String version) {
        return contextSchemaFacade.validateContextSchemas(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createEvent(final String name, final String version, final String nameSpace,
            final String source, final String target, final String uuid, final String description,
            final String toscaPolicyState) {
        return eventFacade.createEvent(name, version, nameSpace, source, target, uuid, description, toscaPolicyState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updateEvent(final String name, final String version, final String nameSpace,
            final String source, final String target, final String uuid, final String description,
            final String toscaPolicyState) {
        return eventFacade.updateEvent(name, version, nameSpace, source, target, uuid, description, toscaPolicyState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listEvent(final String name, final String version) {
        return eventFacade.listEvent(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteEvent(final String name, final String version) {
        return eventFacade.deleteEvent(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validateEvent(final String name, final String version) {
        return eventFacade.validateEvent(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createEventPar(final String name, final String version, final String parName,
            final String contextSchemaName, final String contextSchemaVersion, final boolean optional) {
        return eventFacade.createEventPar(name, version, parName, contextSchemaName, contextSchemaVersion, optional);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listEventPar(final String name, final String version, final String parName) {
        return eventFacade.listEventPar(name, version, parName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteEventPar(final String name, final String version, final String parName) {
        return eventFacade.deleteEventPar(name, version, parName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public ApexApiResult createContextAlbum(final String name, final String version, final String scope,
            final String writable, final String contextSchemaName, final String contextSchemaVersion, final String uuid,
            final String description) {
        return contextAlbumFacade.createContextAlbum(ContextAlbum.builder().name(name).version(version)
                .scope(scope).writable(writable).contextSchemaName(contextSchemaName)
                .contextSchemaVersion(contextSchemaVersion).uuid(uuid).description(description).build());
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * {@inheritDoc}.
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    @Override
    public ApexApiResult updateContextAlbum(final String name, final String version, final String scope,
            final String writable, final String contextSchemaName, final String contextSchemaVersion, final String uuid,
            final String description) {
        return contextAlbumFacade.updateContextAlbum(ContextAlbum.builder().name(name).version(version)
                .scope(scope).writable(writable).contextSchemaName(contextSchemaName)
                .contextSchemaVersion(contextSchemaVersion).uuid(uuid).description(description).build());
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listContextAlbum(final String name, final String version) {
        return contextAlbumFacade.listContextAlbum(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteContextAlbum(final String name, final String version) {
        return contextAlbumFacade.deleteContextAlbum(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validateContextAlbum(final String name, final String version) {
        return contextAlbumFacade.validateContextAlbum(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createTask(final String name, final String version, final String uuid,
            final String description) {
        return taskFacade.createTask(name, version, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updateTask(final String name, final String version, final String uuid,
            final String description) {
        return taskFacade.updateTask(name, version, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listTask(final String name, final String version) {
        return taskFacade.listTask(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteTask(final String name, final String version) {
        return taskFacade.deleteTask(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validateTask(final String name, final String version) {
        return taskFacade.validateTask(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic) {
        return taskFacade.createTaskLogic(name, version, logicFlavour, logic);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updateTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic) {
        return taskFacade.updateTaskLogic(name, version, logicFlavour, logic);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listTaskLogic(final String name, final String version) {
        return taskFacade.listTaskLogic(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteTaskLogic(final String name, final String version) {
        return taskFacade.deleteTaskLogic(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createTaskField(final String name, final String version, final String fieldName,
            final String dataTypeName, final String dataTypeVersion, final boolean optional) {
        LOGGER.warn(FIELDS_DEPRECATED_WARN_MSG);
        return new ApexApiResult(Result.SUCCESS, FIELDS_DEPRECATED_WARN_MSG);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult handleTaskField(final String name, final String version, final String fieldName) {
        LOGGER.warn(FIELDS_DEPRECATED_WARN_MSG);
        return new ApexApiResult(Result.SUCCESS, FIELDS_DEPRECATED_WARN_MSG);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createTaskParameter(final String name, final String version, final String parName,
            final String defaultValue) {
        return taskFacade.createTaskParameter(name, version, parName, defaultValue);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listTaskParameter(final String name, final String version, final String parName) {
        return taskFacade.listTaskParameter(name, version, parName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteTaskParameter(final String name, final String version, final String parName) {
        return taskFacade.deleteTaskParameter(name, version, parName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        return taskFacade.createTaskContextRef(name, version, contextAlbumName, contextAlbumVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        return taskFacade.listTaskContextRef(name, version, contextAlbumName, contextAlbumVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deleteTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        return taskFacade.deleteTaskContextRef(name, version, contextAlbumName, contextAlbumVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createPolicy(final String name, final String version, final String template,
            final String firstState, final String uuid, final String description) {
        return policyFacade.createPolicy(name, version, template, firstState, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updatePolicy(final String name, final String version, final String template,
            final String firstState, final String uuid, final String description) {
        return policyFacade.updatePolicy(name, version, template, firstState, uuid, description);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicy(final String name, final String version) {
        return policyFacade.listPolicy(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicy(final String name, final String version) {
        return policyFacade.deletePolicy(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validatePolicy(final String name, final String version) {
        return policyFacade.validatePolicy(name, version);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createPolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion) {
        return policyFacade.createPolicyState(name, version, stateName, triggerName, triggerVersion, defaultTaskName,
                defaltTaskVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updatePolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion) {
        return policyFacade.updatePolicyState(name, version, stateName, triggerName, triggerVersion, defaultTaskName,
                defaltTaskVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicyState(final String name, final String version, final String stateName) {
        return policyFacade.listPolicyState(name, version, stateName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicyState(final String name, final String version, final String stateName) {
        return policyFacade.deletePolicyState(name, version, stateName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createPolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName, final String logicFlavour, final String logic) {
        return policyFacade.createPolicyStateTaskSelectionLogic(name, version, stateName, logicFlavour, logic);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updatePolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName, final String logicFlavour, final String logic) {
        return policyFacade.updatePolicyStateTaskSelectionLogic(name, version, stateName, logicFlavour, logic);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName) {
        return policyFacade.listPolicyStateTaskSelectionLogic(name, version, stateName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName) {
        return policyFacade.deletePolicyStateTaskSelectionLogic(name, version, stateName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName, final String eventName, final String eventVersion, final String nextState) {
        return policyFacade.createPolicyStateOutput(name, version, stateName, outputName, eventName, eventVersion,
                nextState);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName) {
        return policyFacade.listPolicyStateOutput(name, version, stateName, outputName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName) {
        return policyFacade.deletePolicyStateOutput(name, version, stateName, outputName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createPolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName, final String logicFlavour, final String logic) {
        return policyFacade.createPolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName, logicFlavour,
                logic);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult updatePolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName, final String logicFlavour, final String logic) {
        return policyFacade.updatePolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName, logicFlavour,
                logic);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName) {
        return policyFacade.listPolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName) {
        return policyFacade.deletePolicyStateFinalizerLogic(name, version, stateName, finalizerLogicName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    public ApexApiResult createPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskLocalName, final String taskName, final String taskVersion, final String outputType,
            final String outputName) {
        return policyFacade.createPolicyStateTaskRef(CreatePolicyStateTaskRef.builder().name(name)
                .version(version).stateName(stateName).taskLocalName(taskLocalName).taskName(taskName)
                .taskVersion(taskVersion).outputType(outputType).outputName(outputName).build());
    }
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion) {
        return policyFacade.listPolicyStateTaskRef(name, version, stateName, taskName, taskVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion) {
        return policyFacade.deletePolicyStateTaskRef(name, version, stateName, taskName, taskVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult createPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        return policyFacade.createPolicyStateContextRef(name, version, stateName, contextAlbumName,
                contextAlbumVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult listPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        return policyFacade.listPolicyStateContextRef(name, version, stateName, contextAlbumName, contextAlbumVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult deletePolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        return policyFacade.deletePolicyStateContextRef(name, version, stateName, contextAlbumName,
                contextAlbumVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult loadFromString(final String modelString) {
        return modelHandlerFacade.loadFromString(modelString);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    // CHECKSTYLE:OFF: checkstyle:HiddenField
    public ApexApiResult loadFromFile(final String fileName) {
        this.fileName = fileName;
        return modelHandlerFacade.loadFromFile(fileName);
    }
    // CHECKSTYLE:ON: checkstyle:HiddenField

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult saveToFile(final String saveFileName, final boolean xmlFlag) {
        if (saveFileName == null) {
            return modelHandlerFacade.saveToFile(fileName, xmlFlag);
        } else {
            return modelHandlerFacade.saveToFile(saveFileName, xmlFlag);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult loadFromDatabase(final String modelName, final String modelVersion,
            final DaoParameters daoParameters) {
        return modelHandlerFacade.loadFromDatabase(modelName, modelVersion, daoParameters);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult saveToDatabase(final DaoParameters daoParameters) {
        return modelHandlerFacade.saveToDatabase(daoParameters);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult readFromUrl(final String urlString) {
        return modelHandlerFacade.readFromUrl(urlString);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult writeToUrl(final String urlString, final boolean xmlFlag) {
        return modelHandlerFacade.writeToUrl(urlString, xmlFlag);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult analyse() {
        return modelHandlerFacade.analyse();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult validate() {
        return modelHandlerFacade.validate();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult compare(final String otherModelFileName, final boolean diffsOnly, final boolean keysOnly) {
        return modelHandlerFacade.compare(otherModelFileName, diffsOnly, keysOnly);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult compareWithString(final String otherModelString, final boolean diffsOnly,
            final boolean keysOnly) {
        return modelHandlerFacade.compareWithString(otherModelString, diffsOnly, keysOnly);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult split(final String targetModelFileName, final String splitOutPolicies) {
        return modelHandlerFacade.split(targetModelFileName, splitOutPolicies);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult split(final String splitOutPolicies) {
        return modelHandlerFacade.split(splitOutPolicies);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult merge(final String mergeInModelFileName, final boolean keepOriginal) {
        return modelHandlerFacade.merge(mergeInModelFileName, keepOriginal);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexApiResult mergeWithString(final String otherModelString, final boolean keepOriginal) {
        return modelHandlerFacade.mergeWithString(otherModelString, keepOriginal);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxPolicyModel build() {
        return policyModel;
    }

}