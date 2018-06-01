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

package org.onap.policy.apex.auth.clicodegen;

import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * Code generator generating expressions for the APEX CLI Editor.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class CGCliEditor {
    // CHECKSTYLE:OFF: ParameterNumber

    /** The name of the STG file for the code generator. */
    public static final String STG_FILE = "org/onap/policy/apex/auth/clicodegen/cli-editor.stg";

    /** The String Template Group, taken from the context. */
    private final STGroupFile stg;

    /** The ST for the model, loaded from the STG. */
    private final ST model;

    /** A default name space, set from specification. */
    private String defaultNamespace;

    /**
     * Creates a new code generator.
     */
    public CGCliEditor() {
        stg = new STGroupFile(STG_FILE);
        stg.registerRenderer(String.class, new CgStringRenderer(), true);
        model = stg.getInstanceOf("policyModel");
    }

    /**
     * Adds model parameters to the template.
     * 
     * @param name the name of the mode, must not be blank
     * @param version a version, can be null
     * @param uuid a UUID, can be null
     * @param description a description, must not be blank
     */
    public void addModelParams(final String name, final String version, final String uuid, final String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("model name should not be blank");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("model description should not be blank");
        }

        model.add("name", name);
        model.add("version", version);
        model.add("uuid", uuid);
        model.add("description", description);
    }

    /**
     * Returns the model.
     * 
     * @return the model
     */
    public ST getModel() {
        return model;
    }

    /**
     * Sets the default name space.
     * 
     * @param nameSpace new name space, ignored if blank
     */
    public void setDefaultNamespace(final String nameSpace) {
        if (nameSpace != null && !nameSpace.isEmpty()) {
            defaultNamespace = nameSpace;
        }
    }

    /**
     * Adds a new schema declaration to the model.
     * 
     * @param name the name of the schema
     * @param version the version of the declaration
     * @param uuid the UUID for the declaration
     * @param description a description of the schema
     * @param flavour the flavour of the schema declaration, e.g. Java or Avro
     * @param schema the actual schema declaration, either a string or as <code>LS schema LE</code>
     */
    public void addSchemaDeclaration(final String name, final String version, final String uuid,
            final String description, final String flavour, final String schema) {
        final ST st = stg.getInstanceOf("schemaDecl");
        st.add("name", name);
        st.add("version", version);
        st.add("uuid", uuid);
        st.add("description", description);
        st.add("flavour", flavour);
        st.add("schema", schema);
        model.add("declarations", st);
    }

    /**
     * Adds a new context album declaration to the model.
     * 
     * @param name the name of the context album
     * @param version the version of the context album
     * @param uuid a UUID for the declaration
     * @param description a description for the context album
     * @param scope the scope
     * @param writable a flag for writable context
     * @param schemaName the name of the schema
     * @param schemaVersion the version of the declaration
     */
    public void addContextAlbumDeclaration(final String name, final String version, final String uuid,
            final String description, final String scope, final boolean writable, final String schemaName,
            final String schemaVersion) {
        final ST st = stg.getInstanceOf("ctxAlbumDecl");
        st.add("name", name);
        st.add("version", version);
        st.add("uuid", uuid);
        st.add("description", description);
        st.add("scope", scope);
        st.add("writable", writable);
        st.add("schemaName", schemaName);
        st.add("schemaVersion", schemaVersion);
        model.add("declarations", st);
    }

    /**
     * Creates a new event field definition which belongs to an event.
     * 
     * @param eventName the event name
     * @param version the event version
     * @param fieldName the name for the field in the event
     * @param fieldSchema the schema of the field
     * @param fieldSchemaVersion the version of the schema
     * @param optional a flag for optional fields
     * @return a CLI command for event field definition
     */
    public ST createEventFieldDefinition(final String eventName, final String version, final String fieldName,
            final String fieldSchema, final String fieldSchemaVersion, final boolean optional) {
        final ST st = stg.getInstanceOf("eventDefField");
        st.add("eventName", eventName);
        st.add("version", version);
        st.add("fieldName", fieldName);
        st.add("fieldSchema", fieldSchema);
        st.add("fieldSchemaVersion", fieldSchemaVersion);
        st.add("optional", optional);
        return st;
    }

    /**
     * Creates a new task logic definition which belongs to a task.
     * 
     * @param taskName the name of the task
     * @param version the task version
     * @param flavour the flavour, e.g. JAVA or JAVASCRIPT
     * @param logic the actual logic (use either a string or a multi-line with
     *        <code>LS some code LE</code>
     * @return a CLI command for task definition, logic
     */
    public ST createTaskDefLogic(final String taskName, final String version, final String flavour,
            final String logic) {
        final ST st = stg.getInstanceOf("taskDefLogic");
        st.add("taskName", taskName);
        st.add("version", version);
        st.add("flavour", flavour);
        st.add("logic", logic);
        return st;
    }

    /**
     * Adds a new event declaration to the model.
     * 
     * @param name the event name
     * @param version the event version
     * @param uuid a UUID for the definition
     * @param description a description of the event
     * @param nameSpace the name space for the event
     * @param source a source sending the event
     * @param target a target receiving the event
     * @param fields any event fields
     */
    public void addEventDeclaration(final String name, final String version, final String uuid,
            final String description, final String nameSpace, final String source, final String target,
            final List<ST> fields) {
        final ST st = stg.getInstanceOf("eventDecl");
        st.add("name", name);
        st.add("version", version);
        st.add("uuid", uuid);
        st.add("description", description);
        st.add("source", source);
        st.add("target", target);
        st.add("fields", fields);

        if (nameSpace != null) {
            st.add("nameSpace", nameSpace);
        } else if (defaultNamespace != null) {
            st.add("nameSpace", defaultNamespace);
        }

        model.add("declarations", st);
    }

    /**
     * Adds a new task declaration to the model.
     * 
     * @param name the name of the task
     * @param version the version of the task
     * @param uuid a UUID for the definition
     * @param description a description of the task
     * @param infields all infields for the task
     * @param outfields all outfields for the task
     * @param logic the logic for the task
     * @param parameters any task parameter
     * @param contextRefs any context reference
     */
    public void addTaskDeclaration(final String name, final String version, final String uuid, final String description,
            final List<ST> infields, final List<ST> outfields, final ST logic, final List<ST> parameters,
            final List<ST> contextRefs) {
        final ST st = stg.getInstanceOf("taskDecl");
        st.add("name", name);
        st.add("version", version);
        st.add("uuid", uuid);
        st.add("description", description);
        st.add("infields", infields);
        st.add("outfields", outfields);
        st.add("logic", logic);
        st.add("parameters", parameters);
        st.add("contextRefs", contextRefs);
        model.add("declarations", st);
    }

    /**
     * Adds a new policy definition to the model.
     * 
     * @param name the name of the policy
     * @param version the version of the policy
     * @param uuid a UUID for the definition
     * @param description a description of the policy
     * @param template the template type for this policy
     * @param firstState the first state of the policy
     * @param states all policy states
     */
    public void addPolicyDefinition(final String name, final String version, final String uuid,
            final String description, final String template, final String firstState, final List<ST> states) {
        final ST st = stg.getInstanceOf("policyDef");
        st.add("name", name);
        st.add("version", version);
        st.add("uuid", uuid);
        st.add("description", description);
        st.add("template", template);
        st.add("firstState", firstState);
        st.add("states", states);
        model.add("definitions", st);
    }

    /**
     * Creates a new task infield definition.
     * 
     * @param taskName the name of the task
     * @param version the version of the task
     * @param fieldName the name of the infield
     * @param fieldSchema the schema for the infield
     * @param fieldSchemaVersion the version of the schema
     * @return a CLI command for task infield definition
     */
    public ST createTaskDefinitionInfields(final String taskName, final String version, final String fieldName,
            final String fieldSchema, final String fieldSchemaVersion) {
        final ST st = stg.getInstanceOf("taskDefInputFields");
        st.add("taskName", taskName);
        st.add("version", version);
        st.add("fieldName", fieldName);
        st.add("fieldSchema", fieldSchema);
        st.add("fieldSchemaVersion", fieldSchemaVersion);
        return st;
    }

    /**
     * Creates a new task outfield definition.
     * 
     * @param taskName the name of the task
     * @param version the version of the task
     * @param fieldName the name of the outfield
     * @param fieldSchema the schema for the outfield
     * @param fieldSchemaVersion the version of the schema
     * @return a CLI command for task outfield definition
     */
    public ST createTaskDefinitionOutfields(final String taskName, final String version, final String fieldName,
            final String fieldSchema, final String fieldSchemaVersion) {
        final ST st = stg.getInstanceOf("taskDefOutputFields");
        st.add("taskName", taskName);
        st.add("version", version);
        st.add("fieldName", fieldName);
        st.add("fieldSchema", fieldSchema);
        st.add("fieldSchemaVersion", fieldSchemaVersion);
        return st;
    }

    /**
     * Creates a new task parameter definition belonging to a task.
     * 
     * @param name the name of the task
     * @param version the version of the task
     * @param parName the parameter name
     * @param defaultValue a default value for the parameter
     * @return a CLI command for a task parameter definition
     */
    public ST createTaskDefinitionParameters(final String name, final String version, final String parName,
            final String defaultValue) {
        final ST st = stg.getInstanceOf("taskDefParameter");
        st.add("name", name);
        st.add("version", version);
        st.add("parName", parName);
        st.add("defaultValue", defaultValue);
        return st;
    }

    /**
     * Creates a new task definition context reference which belongs to a task.
     * 
     * @param name the name of the task
     * @param version the version of the task
     * @param albumName the name of the context album
     * @param albumVersion the version of the context album
     * @return a CLI command for a task context reference definition
     */
    public ST createTaskDefinitionContextRef(final String name, final String version, final String albumName,
            final String albumVersion) {
        final ST st = stg.getInstanceOf("taskDefCtxRef");
        st.add("name", name);
        st.add("version", version);
        st.add("albumName", albumName);
        st.add("albumVersion", albumVersion);
        return st;
    }

    /**
     * Creates a new policy state task definition for a task which belongs to a state which belongs
     * to a policy.
     * 
     * @param policyName the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the new state
     * @param taskLocalName the local (in policy and state) name of the task
     * @param taskName the identifier of the task (previously defined as a task)
     * @param taskVersion the version of the task definition
     * @param outputType the output type
     * @param outputName the output name
     * @return a CLI command for a policy state task definition
     */
    public ST createPolicyStateTask(final String policyName, final String version, final String stateName,
            final String taskLocalName, final String taskName, final String taskVersion, final String outputType,
            final String outputName) {
        final ST st = stg.getInstanceOf("policyStateTask");
        st.add("policyName", policyName);
        st.add("version", version);
        st.add("stateName", stateName);
        st.add("taskLocalName", taskLocalName);
        st.add("taskName", taskName);
        st.add("taskVersion", taskVersion);
        st.add("outputType", outputType);
        st.add("outputName", outputName);
        return st;
    }

    /**
     * Creates a new policy state output definition for a state which belongs to a policy.
     * 
     * @param policyName the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the new state
     * @param outputName the name of the new output
     * @param eventName the event name for the output
     * @param eventVersion the version of the event for the output
     * @param nextState the next state if any
     * @return a CLI command for a state output definition
     */
    public ST createPolicyStateOutput(final String policyName, final String version, final String stateName,
            final String outputName, final String eventName, final String eventVersion, final String nextState) {
        final ST st = stg.getInstanceOf("policyStateOutput");
        st.add("policyName", policyName);
        st.add("version", version);
        st.add("stateName", stateName);
        st.add("outputName", outputName);
        st.add("eventName", eventName);
        st.add("eventVersion", eventVersion);
        st.add("nextState", nextState);
        return st;
    }

    /**
     * Creates a new policy state definition for a state which belongs to a policy.
     * 
     * @param policyName the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the new state
     * @param triggerName the name of the trigger event
     * @param triggerVersion the version of the trigger event
     * @param defaultTask the identifier of the default task
     * @param defaultTaskVersion the version of the default task
     * @param outputs the output definitions of the state
     * @param tasks the task definition of the state
     * @param tsLogic the task selection logic of the state
     * @param finalizerLogics the finalizer logics for the state
     * @param ctxRefs any context reference for the state
     * @return a CLI command for a policy state definition
     */
    public ST createPolicyStateDef(final String policyName, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTask,
            final String defaultTaskVersion, final List<ST> outputs, final List<ST> tasks, final List<ST> tsLogic,
            final List<ST> finalizerLogics, final List<ST> ctxRefs) {
        final ST st = stg.getInstanceOf("policyStateDef");
        st.add("policyName", policyName);
        st.add("version", version);
        st.add("stateName", stateName);
        st.add("triggerName", triggerName);
        st.add("triggerVersion", triggerVersion);
        st.add("defaultTask", defaultTask);
        st.add("defaultTaskVersion", defaultTaskVersion);
        st.add("outputs", outputs);
        st.add("tasks", tasks);
        st.add("tsLogic", tsLogic);
        st.add("finalizerLogics", finalizerLogics);
        st.add("ctxRefs", ctxRefs);
        return st;
    }

    /**
     * Creates a new task selection logic definition for a state which belongs to a policy.
     * 
     * @param name the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the state
     * @param logicFlavour the flavour, e.g. JAVA or JAVASCRIPT
     * @param logic the actual logic (use either a string or a multi-line with
     *        <code>LS some code LE</code>
     * @return a CLI command for task selection logic definition
     */
    public ST createPolicyStateDefTaskSelLogic(final String name, final String version, final String stateName,
            final String logicFlavour, final String logic) {
        final ST st = stg.getInstanceOf("policyStateTaskSelectionLogic");
        st.add("name", name);
        st.add("version", version);
        st.add("stateName", stateName);
        st.add("logicFlavour", logicFlavour);
        st.add("logic", logic);
        return st;
    }

    /**
     * Creates a new state finalizer definition for a state which belongs to a policy.
     * 
     * @param name the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the state
     * @param finalizerLogicName name of the finalizer logic
     * @param logicFlavour the flavour, e.g. JAVA or JAVASCRIPT
     * @param logic the actual logic (use either a string or a multi-line with
     *        <code>LS some code LE</code>
     * @return a CLI command for finalizer definition
     */
    public ST createPolicyStateDefFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName, final String logicFlavour, final String logic) {
        final ST st = stg.getInstanceOf("policyStateFinalizerLogic");
        st.add("name", name);
        st.add("version", version);
        st.add("stateName", stateName);
        st.add("finalizerLogicName", finalizerLogicName);
        st.add("logicFlavour", logicFlavour);
        st.add("logic", logic);
        return st;
    }

    /**
     * Creates a new policy state context reference for a state which belongs to a policy.
     * 
     * @param name the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the state
     * @param albumName the name of the album
     * @param albumVersion the version of the album
     * @return a CLI command for state context reference
     */
    public ST createPolicyStateDefContextRef(final String name, final String version, final String stateName,
            final String albumName, final String albumVersion) {
        final ST st = stg.getInstanceOf("policyStateContextRef");
        st.add("name", name);
        st.add("version", version);
        st.add("stateName", stateName);
        st.add("albumName", albumName);
        st.add("albumVersion", albumVersion);
        return st;
    }

}
