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

import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.ALBUM_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.ALBUM_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.CONTEXT_REFS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.CTX_REFS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.DECLARATION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.DEFAULT_TASK;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.DEFAULT_TASK_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.DEFAULT_VALUE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.DEFINITIONS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.DESCRIPTION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.EVENT_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.EVENT_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FIELDS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FIELD_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FIELD_SCHEMA;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FIELD_SCHEMA_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FINALIZER_LOGICS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FINALIZER_LOGIC_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FIRST_STATE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.FLAVOUR;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.INFIELDS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.LOGIC;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.LOGIC_FLAVOUR;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.NAME_SPACE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.NEXT_STATE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.OPTIONAL;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.OUTFIELDS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.OUTPUTS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.OUTPUT_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.OUTPUT_TYPE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.PARAMS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.PAR_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.POLICY_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.SCHEMA;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.SCHEMA_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.SCHEMA_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.SCOPE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.SOURCE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.STATES;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.STATE_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TARGET;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TASKS;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TASK_LOCAL_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TASK_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TASK_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TEMPLATE;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TRIGGER_NAME;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TRIGGER_VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.TS_LOGIC;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.UUID;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.VERSION;
import static org.onap.policy.apex.auth.clicodegen.CliEditorContants.WRITABLE;

import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * Code generator generating expressions for the APEX CLI Editor.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class CodeGeneratorCliEditor {

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
    public CodeGeneratorCliEditor() {
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

        model.add(NAME, name);
        model.add(VERSION, version);
        model.add(UUID, uuid);
        model.add(DESCRIPTION, description);
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
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(UUID, uuid);
        st.add(DESCRIPTION, description);
        st.add(FLAVOUR, flavour);
        st.add(SCHEMA, schema);
        model.add(DECLARATION, st);
    }

    /**
     * Adds a new context album declaration to the model.
     *
     * @param codeGenCliEditorBuilder The parameters for the context album
     */
    public void addContextAlbumDeclaration(CodeGenCliEditorBuilder codeGenCliEditorBuilder) {
        final ST st = stg.getInstanceOf("ctxAlbumDecl");
        st.add(NAME, codeGenCliEditorBuilder.getName());
        st.add(VERSION, codeGenCliEditorBuilder.getVersion());
        st.add(UUID, codeGenCliEditorBuilder.getUuid());
        st.add(DESCRIPTION, codeGenCliEditorBuilder.getDescription());
        st.add(SCOPE, codeGenCliEditorBuilder.getScope());
        st.add(WRITABLE, codeGenCliEditorBuilder.isWritable());
        st.add(SCHEMA_NAME, codeGenCliEditorBuilder.getSchemaName());
        st.add(SCHEMA_VERSION, codeGenCliEditorBuilder.getSchemaVersion());
        model.add(DECLARATION, st);
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
        st.add(EVENT_NAME, eventName);
        st.add(VERSION, version);
        st.add(FIELD_NAME, fieldName);
        st.add(FIELD_SCHEMA, fieldSchema);
        st.add(FIELD_SCHEMA_VERSION, fieldSchemaVersion);
        st.add(OPTIONAL, optional);
        return st;
    }

    /**
     * Creates a new task logic definition which belongs to a task.
     * 
     * @param taskName the name of the task
     * @param version the task version
     * @param flavour the flavour, e.g. JAVA or JAVASCRIPT
     * @param logic the actual logic (use either a string or a multi-line with <code>LS some code LE</code>
     * @return a CLI command for task definition, logic
     */
    public ST createTaskDefLogic(final String taskName, final String version, final String flavour,
                    final String logic) {
        final ST st = stg.getInstanceOf("taskDefLogic");
        st.add(TASK_NAME, taskName);
        st.add(VERSION, version);
        st.add(FLAVOUR, flavour);
        st.add(LOGIC, logic);
        return st;
    }

    /**
     * Adds a new event declaration to the model.
     *
     * @param eventDeclarationBuilder param object for event declaration
     */
    public void addEventDeclaration(
            EventDeclarationBuilder eventDeclarationBuilder) {
        final ST st = stg.getInstanceOf("eventDecl");
        st.add(NAME, eventDeclarationBuilder.getName());
        st.add(VERSION, eventDeclarationBuilder.getVersion());
        st.add(UUID, eventDeclarationBuilder.getUuid());
        st.add(DESCRIPTION, eventDeclarationBuilder.getDescription());
        st.add(SOURCE, eventDeclarationBuilder.getSource());
        st.add(TARGET, eventDeclarationBuilder.getTarget());
        st.add(FIELDS, eventDeclarationBuilder.getFields());

        if (eventDeclarationBuilder.getNameSpace() != null) {
            st.add(NAME_SPACE, eventDeclarationBuilder.getNameSpace());
        } else if (defaultNamespace != null) {
            st.add(NAME_SPACE, defaultNamespace);
        }

        model.add(DECLARATION, st);
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
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(UUID, uuid);
        st.add(DESCRIPTION, description);
        st.add(INFIELDS, infields);
        st.add(OUTFIELDS, outfields);
        st.add(LOGIC, logic);
        st.add(PARAMS, parameters);
        st.add(CONTEXT_REFS, contextRefs);
        model.add(DECLARATION, st);
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
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(UUID, uuid);
        st.add(DESCRIPTION, description);
        st.add(TEMPLATE, template);
        st.add(FIRST_STATE, firstState);
        st.add(STATES, states);
        model.add(DEFINITIONS, st);
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
        st.add(TASK_NAME, taskName);
        st.add(VERSION, version);
        st.add(FIELD_NAME, fieldName);
        st.add(FIELD_SCHEMA, fieldSchema);
        st.add(FIELD_SCHEMA_VERSION, fieldSchemaVersion);
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
        st.add(TASK_NAME, taskName);
        st.add(VERSION, version);
        st.add(FIELD_NAME, fieldName);
        st.add(FIELD_SCHEMA, fieldSchema);
        st.add(FIELD_SCHEMA_VERSION, fieldSchemaVersion);
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
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(PAR_NAME, parName);
        st.add(DEFAULT_VALUE, defaultValue);
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
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(ALBUM_NAME, albumName);
        st.add(ALBUM_VERSION, albumVersion);
        return st;
    }

    /**
     * Creates a new policy state task definition for a task which belongs to a state which belongs to a policy.
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
                    final String taskLocalName, final String taskName, final String taskVersion,
                    final String outputType, final String outputName) {
        final ST st = stg.getInstanceOf("policyStateTask");
        st.add(POLICY_NAME, policyName);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(TASK_LOCAL_NAME, taskLocalName);
        st.add(TASK_NAME, taskName);
        st.add(TASK_VERSION, taskVersion);
        st.add(OUTPUT_TYPE, outputType);
        st.add(OUTPUT_NAME, outputName);
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
                    final String outputName, final String eventName, final String eventVersion,
                    final String nextState) {
        final ST st = stg.getInstanceOf("policyStateOutput");
        st.add(POLICY_NAME, policyName);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(OUTPUT_NAME, outputName);
        st.add(EVENT_NAME, eventName);
        st.add(EVENT_VERSION, eventVersion);
        st.add(NEXT_STATE, nextState);
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
                    final String defaultTaskVersion, final List<ST> outputs, final List<ST> tasks,
                    final List<ST> tsLogic, final List<ST> finalizerLogics, final List<ST> ctxRefs) {
        final ST st = stg.getInstanceOf("policyStateDef");
        st.add(POLICY_NAME, policyName);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(TRIGGER_NAME, triggerName);
        st.add(TRIGGER_VERSION, triggerVersion);
        st.add(DEFAULT_TASK, defaultTask);
        st.add(DEFAULT_TASK_VERSION, defaultTaskVersion);
        st.add(OUTPUTS, outputs);
        st.add(TASKS, tasks);
        st.add(TS_LOGIC, tsLogic);
        st.add(FINALIZER_LOGICS, finalizerLogics);
        st.add(CTX_REFS, ctxRefs);
        return st;
    }

    /**
     * Creates a new task selection logic definition for a state which belongs to a policy.
     * 
     * @param name the name of the policy
     * @param version the version of the policy
     * @param stateName the name of the state
     * @param logicFlavour the flavour, e.g. JAVA or JAVASCRIPT
     * @param logic the actual logic (use either a string or a multi-line with <code>LS some code LE</code>
     * @return a CLI command for task selection logic definition
     */
    public ST createPolicyStateDefTaskSelLogic(final String name, final String version, final String stateName,
                    final String logicFlavour, final String logic) {
        final ST st = stg.getInstanceOf("policyStateTaskSelectionLogic");
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(LOGIC_FLAVOUR, logicFlavour);
        st.add(LOGIC, logic);
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
     * @param logic the actual logic (use either a string or a multi-line with <code>LS some code LE</code>
     * @return a CLI command for finalizer definition
     */
    public ST createPolicyStateDefFinalizerLogic(final String name, final String version, final String stateName,
                    final String finalizerLogicName, final String logicFlavour, final String logic) {
        final ST st = stg.getInstanceOf("policyStateFinalizerLogic");
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(FINALIZER_LOGIC_NAME, finalizerLogicName);
        st.add(LOGIC_FLAVOUR, logicFlavour);
        st.add(LOGIC, logic);
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
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(ALBUM_NAME, albumName);
        st.add(ALBUM_VERSION, albumVersion);
        return st;
    }

}
