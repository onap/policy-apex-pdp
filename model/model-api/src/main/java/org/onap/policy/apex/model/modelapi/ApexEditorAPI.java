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

package org.onap.policy.apex.model.modelapi;

/**
 * The Interface ApexEditorAPI is used to manipulate Apex models.
 */
public interface ApexEditorAPI {
    /*
     * Model API Methods
     */

    /**
     * Create model.
     *
     * @param name name of the model
     * @param version version of the model, set to null to use the default version
     * @param uuid model UUID, set to null to generate a UUID
     * @param description model description, set to null to generate a description
     * @return result of the operation
     */
    ApexAPIResult createModel(final String name, final String version, final String uuid, final String description);

    /**
     * Update model.
     *
     * @param name name of the model
     * @param version version of the model, set to null to update the latest version
     * @param uuid key information UUID, set to null to not update
     * @param description policy description, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updateModel(final String name, final String version, final String uuid, final String description);

    /**
     * Get the key of an Apex model.
     *
     * @return the result of the operation
     */
    ApexAPIResult getModelKey();

    /**
     * List an Apex model.
     *
     * @return the result of the operation
     */
    ApexAPIResult listModel();

    /**
     * Delete an Apex model, clear all the concepts in the model.
     *
     * @return the result of the operation
     */
    ApexAPIResult deleteModel();

    /*
     * Key Information API Methods
     */

    /**
     * Create key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to use the default
     *        version
     * @param uuid key information UUID, set to null to generate a UUID
     * @param description key information description, set to null to generate a description
     * @return result of the operation
     */
    ApexAPIResult createKeyInformation(final String name, final String version, final String uuid,
            final String description);

    /**
     * Update key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to update the
     *        latest version
     * @param uuid key information UUID, set to null to not update
     * @param description key information description, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updateKeyInformation(final String name, final String version, final String uuid,
            final String description);

    /**
     * List key information.
     *
     * @param name name of the concept for the key information, set to null to list all
     * @param version starting version of the concept for the key information, set to null to list
     *        all versions
     * @return result of the operation
     */
    ApexAPIResult listKeyInformation(final String name, final String version);

    /**
     * Delete key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to delete all
     *        versions
     * @return result of the operation
     */
    ApexAPIResult deleteKeyInformation(final String name, final String version);

    /**
     * Validate key information.
     *
     * @param name name of the concept for the key information
     * @param version version of the concept for the key information, set to null to validate all
     *        versions
     * @return result of the operation
     */
    ApexAPIResult validateKeyInformation(final String name, final String version);

    /*
     * Context Schema API Methods
     */

    /**
     * Create a context schema.
     *
     * @param name name of the context schema
     * @param version version of the context schema, set to null to use the default version
     * @param schemaFlavour a final String identifying the flavour of this context schema
     * @param schemaDefinition a final String containing the definition of this context schema
     * @param uuid context schema UUID, set to null to generate a UUID
     * @param description context schema description, set to null to generate a description
     * @return result of the operation
     */
    ApexAPIResult createContextSchema(final String name, final String version, final String schemaFlavour,
            final String schemaDefinition, final String uuid, final String description);

    /**
     * Update a context schema.
     *
     * @param name name of the context schema
     * @param version version of the context schema, set to null to update the latest version
     * @param schemaFlavour a final String identifying the flavour of this context schema
     * @param schemaDefinition a final String containing the definition of this context schema
     * @param uuid context schema UUID, set to null to not update
     * @param description context schema description, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updateContextSchema(final String name, final String version, final String schemaFlavour,
            final String schemaDefinition, final String uuid, final String description);

    /**
     * List context schemas.
     *
     * @param name name of the context schema, set to null to list all
     * @param version starting version of the context schema, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult listContextSchemas(final String name, final String version);

    /**
     * Delete a context schema.
     *
     * @param name name of the context schema
     * @param version version of the context schema, set to null to delete all versions
     * @return result of the operation
     */
    ApexAPIResult deleteContextSchema(final String name, final String version);

    /**
     * Validate context schemas.
     *
     * @param name name of the context schema, set to null to list all
     * @param version starting version of the context schema, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult validateContextSchemas(final String name, final String version);

    /*
     * Event API Methods
     */

    /**
     * Create an event.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the default version
     * @param nameSpace of the event, set to null to use the default value
     * @param source of the event, set to null to use the default value
     * @param target of the event, set to null to use the default value
     * @param uuid event UUID, set to null to generate a UUID
     * @param description event description, set to null to generate a description
     * @return result of the operation
     */
    ApexAPIResult createEvent(final String name, final String version, final String nameSpace, final String source,
            final String target, final String uuid, final String description);

    /**
     * Update an event.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the latest version
     * @param nameSpace of the event, set to null to not update
     * @param source of the event, set to null to not update
     * @param target of the event, set to null to not update
     * @param uuid event UUID, set to null to not update
     * @param description event description, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updateEvent(final String name, final String version, final String nameSpace, final String source,
            final String target, final String uuid, final String description);

    /**
     * List events.
     *
     * @param name name of the event, set to null to list all
     * @param version starting version of the event, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult listEvent(final String name, final String version);

    /**
     * Delete an event.
     *
     * @param name name of the event
     * @param version version of the event, set to null to delete all versions
     * @return result of the operation
     */
    ApexAPIResult deleteEvent(final String name, final String version);

    /**
     * Validate events.
     *
     * @param name name of the event, set to null to list all
     * @param version starting version of the event, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult validateEvent(final String name, final String version);

    /**
     * Create an event parameter.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the latest version
     * @param parName of the parameter
     * @param contextSchemaName name of the parameter context schema
     * @param contextSchemaVersion version of the parameter context schema, set to null to use the
     *        latest version
     * @param optional true if the event parameter is optional, false otherwise
     * @return result of the operation
     */
    ApexAPIResult createEventPar(final String name, final String version, final String parName,
            final String contextSchemaName, final String contextSchemaVersion, boolean optional);

    /**
     * List event parameters.
     *
     * @param name name of the event
     * @param version version of the event, set to null to list latest version
     * @param parName name of the parameter, set to null to list all parameters of the event
     * @return result of the operation
     */
    ApexAPIResult listEventPar(final String name, final String version, final String parName);

    /**
     * Delete an event parameter.
     *
     * @param name name of the event
     * @param version version of the event, set to null to use the latest version
     * @param parName of the parameter, set to null to delete all parameters
     * @return result of the operation
     */
    ApexAPIResult deleteEventPar(final String name, final String version, final String parName);

    /*
     * Context Album API Methods
     */

    /**
     * Create a context album.
     *
     * @param name name of the context album
     * @param version version of the context album, set to null to use the default version
     * @param scope of the context album
     * @param writable "true" or "t" if the context album is writable, set to null or any other
     *        value for a read-only album
     * @param contextSchemaName name of the parameter context schema
     * @param contextSchemaVersion version of the parameter context schema, set to null to use the
     *        latest version
     * @param uuid context album UUID, set to null to generate a UUID
     * @param description context album description, set to null to generate a description
     * @return result of the operation
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    ApexAPIResult createContextAlbum(final String name, final String version, final String scope, final String writable,
            final String contextSchemaName, final String contextSchemaVersion, final String uuid,
            final String description);
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * Update a context album.
     *
     * @param name name of the context album
     * @param version version of the context album, set to null to use the default version
     * @param scope of the context album
     * @param writable "true" or "t" if the context album is writable, set to null or any other
     *        value for a read-only album
     * @param contextSchemaName name of the parameter context schema
     * @param contextSchemaVersion version of the parameter context schema, set to null to use the
     *        latest version
     * @param uuid context album UUID, set to null to generate a UUID
     * @param description context album description, set to null to generate a description
     * @return result of the operation
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    ApexAPIResult updateContextAlbum(final String name, final String version, final String scope, final String writable,
            final String contextSchemaName, final String contextSchemaVersion, final String uuid,
            final String description);
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * List context albums.
     *
     * @param name name of the context album, set to null to list all
     * @param version starting version of the context album, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult listContextAlbum(final String name, final String version);

    /**
     * Delete a context album.
     *
     * @param name name of the context album
     * @param version version of the context album, set to null to delete versions
     * @return result of the operation
     */
    ApexAPIResult deleteContextAlbum(final String name, final String version);

    /**
     * Validate context albums.
     *
     * @param name name of the context album, set to null to list all
     * @param version starting version of the context album, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult validateContextAlbum(final String name, final String version);

    /*
     * Task API Methods
     */

    /**
     * Create a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the default version
     * @param uuid task UUID, set to null to generate a UUID
     * @param description task description, set to null to generate a description
     * @return result of the operation
     */
    ApexAPIResult createTask(final String name, final String version, final String uuid, final String description);

    /**
     * Update a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param uuid task UUID, set to null to not update
     * @param description task description, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updateTask(final String name, final String version, final String uuid, final String description);

    /**
     * List tasks.
     *
     * @param name name of the task, set to null to list all
     * @param version starting version of the task, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult listTask(final String name, final String version);

    /**
     * Delete a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult deleteTask(final String name, final String version);

    /**
     * Validate tasks.
     *
     * @param name name of the task, set to null to list all
     * @param version starting version of the task, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult validateTask(final String name, final String version);

    /**
     * Create logic for a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param logicFlavour the task logic flavour for the task, set to null to use the default task
     *        logic flavour
     * @param logic the source code for the logic of the task
     * @return result of the operation
     */
    ApexAPIResult createTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic);

    /**
     * Update logic for a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param logicFlavour the task logic flavour for the task, set to null to not update
     * @param logic the source code for the logic of the task, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updateTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic);

    /**
     * List task logic.
     *
     * @param name name of the task
     * @param version version of the task, set to null to list the latest version
     * @return result of the operation
     */
    ApexAPIResult listTaskLogic(final String name, final String version);

    /**
     * Delete logic for a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult deleteTaskLogic(final String name, final String version);

    /**
     * Create a task input field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the input field
     * @param contextSchemaName name of the input field context schema
     * @param contextSchemaVersion version of the input field context schema, set to null to use the
     *        latest version
     * @param optional true if the task field is optional, false otherwise
     * @return result of the operation
     */
    ApexAPIResult createTaskInputField(final String name, final String version, final String fieldName,
            final String contextSchemaName, final String contextSchemaVersion, boolean optional);

    /**
     * List task input fields.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName field name of the input field, set to null to list all input fields of the
     *        task
     * @return result of the operation
     */
    ApexAPIResult listTaskInputField(final String name, final String version, final String fieldName);

    /**
     * Delete a task input field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the input field, set to null to delete all input fields
     * @return result of the operation
     */
    ApexAPIResult deleteTaskInputField(final String name, final String version, final String fieldName);

    /**
     * Create a task output field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the output field
     * @param contextSchemaName name of the output field context schema
     * @param contextSchemaVersion version of the output field context schema, set to null to use
     *        the latest version
     * @param optional true if the task field is optional, false otherwise
     * @return result of the operation
     */
    ApexAPIResult createTaskOutputField(final String name, final String version, final String fieldName,
            final String contextSchemaName, final String contextSchemaVersion, boolean optional);

    /**
     * List task output fields.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName field name of the output field, set to null to list all output fields of the
     *        task
     * @return result of the operation
     */
    ApexAPIResult listTaskOutputField(final String name, final String version, final String fieldName);

    /**
     * Delete a task output field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the output field, set to null to delete all output fields
     * @return result of the operation
     */
    ApexAPIResult deleteTaskOutputField(final String name, final String version, final String fieldName);

    /**
     * Create a task parameter.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param parName of the parameter
     * @param defaultValue of the parameter
     * @return result of the operation
     */
    ApexAPIResult createTaskParameter(final String name, final String version, final String parName,
            final String defaultValue);

    /**
     * List task parameters.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param parName name of the parameter, set to null to list all parameters of the task
     * @return result of the operation
     */
    ApexAPIResult listTaskParameter(final String name, final String version, final String parName);

    /**
     * Delete a task parameter.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param parName of the parameter, set to null to delete all task parameters
     * @return result of the operation
     */
    ApexAPIResult deleteTaskParameter(final String name, final String version, final String parName);

    /**
     * Create a task context album reference.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param contextAlbumName name of the context album for the context album reference
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult createTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion);

    /**
     * List task context album references.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param contextAlbumName name of the context album for the context album reference, set to
     *        null to list all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult listTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion);

    /**
     * Delete a task context album reference.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param contextAlbumName name of the context album for the context album reference, set to
     *        null to delete all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult deleteTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion);

    /*
     * Policy API Methods
     */

    /**
     * Create a policy.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the default version
     * @param template template used to create the policy, set to null to use the default template
     * @param firstState the first state of the policy
     * @param uuid policy UUID, set to null to generate a UUID
     * @param description policy description, set to null to generate a description
     * @return result of the operation
     */
    ApexAPIResult createPolicy(final String name, final String version, final String template, final String firstState,
            final String uuid, final String description);

    /**
     * Update a policy.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param template template used to create the policy, set to null to not update
     * @param firstState the first state of the policy
     * @param uuid policy UUID, set to null to not update
     * @param description policy description, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updatePolicy(final String name, final String version, final String template, final String firstState,
            final String uuid, final String description);

    /**
     * List policies.
     *
     * @param name name of the policy, set to null to list all
     * @param version starting version of the policy, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult listPolicy(final String name, final String version);

    /**
     * Delete a policy.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult deletePolicy(final String name, final String version);

    /**
     * Validate policies.
     *
     * @param name name of the policy, set to null to list all
     * @param version starting version of the policy, set to null to list all versions
     * @return result of the operation
     */
    ApexAPIResult validatePolicy(final String name, final String version);

    /**
     * Create a policy state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param triggerName name of the trigger event for this state
     * @param triggerVersion version of the trigger event for this state, set to null to use the
     *        latest version
     * @param defaultTaskName the default task name
     * @param defaltTaskVersion the default task version, set to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult createPolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion);

    /**
     * Update a policy state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param triggerName name of the trigger event for this state, set to null to not update
     * @param triggerVersion version of the trigger event for this state, set to use latest version
     *        of trigger event
     * @param defaultTaskName the default task name, set to null to not update
     * @param defaltTaskVersion the default task version, set to use latest version of default task
     * @return result of the operation
     */
    ApexAPIResult updatePolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion);

    /**
     * List policy states.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state, set to null to list all states of the policy
     * @return result of the operation
     */
    ApexAPIResult listPolicyState(final String name, final String version, final String stateName);

    /**
     * Delete a policy state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state, set to null to delete all states
     * @return result of the operation
     */
    ApexAPIResult deletePolicyState(final String name, final String version, final String stateName);

    /**
     * Create task selection logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param logicFlavour the task selection logic flavour for the state, set to null to use the
     *        default task logic flavour
     * @param logic the source code for the logic of the state
     * @return result of the operation
     */
    ApexAPIResult createPolicyStateTaskSelectionLogic(final String name, final String version, final String stateName,
            final String logicFlavour, final String logic);

    /**
     * Update task selection logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param logicFlavour the task selection logic flavour for the state, set to null to not update
     * @param logic the source code for the logic of the state, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updatePolicyStateTaskSelectionLogic(final String name, final String version, final String stateName,
            final String logicFlavour, final String logic);

    /**
     * List task selection logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @return result of the operation
     */
    ApexAPIResult listPolicyStateTaskSelectionLogic(final String name, final String version, final String stateName);

    /**
     * Delete task selection logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @return result of the operation
     */
    ApexAPIResult deletePolicyStateTaskSelectionLogic(final String name, final String version, final String stateName);

    /**
     * Create a policy state output.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param outputName of the state output
     * @param eventName name of the output event for this state output
     * @param eventVersion version of the output event for this state output, set to null to use the
     *        latest version
     * @param nextState for this state to transition to, set to null if this is the last state that
     *        the policy transitions to on this branch
     * @return result of the operation
     */
    ApexAPIResult createPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName, final String eventName, final String eventVersion, final String nextState);

    /**
     * List policy state outputs.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param outputName of the state output, set to null to list all outputs of the state
     * @return result of the operation
     */
    ApexAPIResult listPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName);

    /**
     * Delete a policy state output.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param outputName of the state output, set to null to delete all state outputs
     * @return result of the operation
     */
    ApexAPIResult deletePolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName);

    /**
     * Create policy finalizer logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @param logicFlavour the policy finalizer logic flavour for the state, set to null to use the
     *        default task logic flavour
     * @param logic the source code for the logic of the state
     * @return result of the operation
     */
    ApexAPIResult createPolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName, final String logicFlavour, final String logic);

    /**
     * Update policy finalizer logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @param logicFlavour the policy finalizer logic flavour for the state, set to null to not
     *        update
     * @param logic the source code for the logic of the state, set to null to not update
     * @return result of the operation
     */
    ApexAPIResult updatePolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName, final String logicFlavour, final String logic);

    /**
     * List policy finalizer logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @return result of the operation
     */
    ApexAPIResult listPolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName);

    /**
     * Delete policy finalizer logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @return result of the operation
     */
    ApexAPIResult deletePolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName);

    /**
     * Create a policy state task reference.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param taskLocalName the task local name
     * @param taskName name of the task
     * @param taskVersion version of the task, set to null to use the latest version
     * @param outputType Type of output for the task, must be DIRECT for direct output to a state
     *        output or LOGIC for output to state finalizer logic
     * @param outputName the name of the state output or state state finalizer logic to handle the
     *        task output
     * @return result of the operation
     */
    // CHECKSTYLE:OFF: checkstyle:parameterNumber
    ApexAPIResult createPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskLocalName, final String taskName, final String taskVersion, final String outputType,
            final String outputName);
    // CHECKSTYLE:ON: checkstyle:parameterNumber

    /**
     * List policy state task references.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param taskName name of the task, set to null to list all task references
     * @param taskVersion version of the task, set to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult listPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion);

    /**
     * Delete a policy state task reference.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param taskName name of the task, set to null to delete all task references
     * @param taskVersion version of the task, set to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult deletePolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion);

    /**
     * Create a policy state context album reference.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param contextAlbumName name of the context album for the context album reference
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult createPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion);

    /**
     * List policy state context album references.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param contextAlbumName name of the context album for the context album reference, set to
     *        null to list all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult listPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion);

    /**
     * Delete a policy state context album reference.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the default version
     * @param stateName of the state
     * @param contextAlbumName name of the context album for the context album reference, set to
     *        null to delete all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    ApexAPIResult deletePolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion);
}
