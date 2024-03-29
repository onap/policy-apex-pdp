/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Samsung Electronics Co., Ltd.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import lombok.Getter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * Code generator generating expressions for the APEX CLI Editor.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class CodeGeneratorCliEditor {

    // CHECKSTYLE:OFF: ParameterNumber

    /**
     * The name of the STG file for the code generator.
     */
    public static final String STG_FILE = "org/onap/policy/apex/auth/clicodegen/cli-editor.stg";

    /**
     * The String Template Group, taken from the context.
     */
    private final STGroupFile stg;

    /**
     * The ST for the model, loaded from the STG.
     */
    @Getter
    private final ST model;

    /**
     * A default name space, set from specification.
     */
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
        final var st = stg.getInstanceOf("schemaDecl");
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
     * @param codeGenCliEditor The parameters for the context album
     */
    public void addContextAlbumDeclaration(CodeGenCliEditor codeGenCliEditor) {
        final var st = stg.getInstanceOf("ctxAlbumDecl");
        st.add(NAME, codeGenCliEditor.getName());
        st.add(VERSION, codeGenCliEditor.getVersion());
        st.add(UUID, codeGenCliEditor.getUuid());
        st.add(DESCRIPTION, codeGenCliEditor.getDescription());
        st.add(SCOPE, codeGenCliEditor.getScope());
        st.add(WRITABLE, codeGenCliEditor.isWritable());
        st.add(SCHEMA_NAME, codeGenCliEditor.getSchemaName());
        st.add(SCHEMA_VERSION, codeGenCliEditor.getSchemaVersion());
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
        final var st = stg.getInstanceOf("eventDefField");
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
        final var st = stg.getInstanceOf("taskDefLogic");
        st.add(TASK_NAME, taskName);
        st.add(VERSION, version);
        st.add(FLAVOUR, flavour);
        st.add(LOGIC, logic);
        return st;
    }

    /**
     * Adds a new event declaration to the model.
     *
     * @param eventDeclaration param object for event declaration
     */
    public void addEventDeclaration(EventDeclaration eventDeclaration) {
        final var st = stg.getInstanceOf("eventDecl");
        st.add(NAME, eventDeclaration.getName());
        st.add(VERSION, eventDeclaration.getVersion());
        st.add(UUID, eventDeclaration.getUuid());
        st.add(DESCRIPTION, eventDeclaration.getDescription());
        st.add(SOURCE, eventDeclaration.getSource());
        st.add(TARGET, eventDeclaration.getTarget());
        st.add(FIELDS, eventDeclaration.getFields());

        if (eventDeclaration.getNameSpace() != null) {
            st.add(NAME_SPACE, eventDeclaration.getNameSpace());
        } else if (defaultNamespace != null) {
            st.add(NAME_SPACE, defaultNamespace);
        }

        model.add(DECLARATION, st);
    }

    /**
     * Adds a new task declaration to the model.
     *
     * @param taskDeclaration task declaration parameters
     */
    public void addTaskDeclaration(TaskDeclaration taskDeclaration) {
        final var st = stg.getInstanceOf("taskDecl");
        st.add(NAME, taskDeclaration.getName());
        st.add(VERSION, taskDeclaration.getVersion());
        st.add(UUID, taskDeclaration.getUuid());
        st.add(DESCRIPTION, taskDeclaration.getDescription());
        st.add(INFIELDS, taskDeclaration.getInfields());
        st.add(OUTFIELDS, taskDeclaration.getOutfields());
        st.add(LOGIC, taskDeclaration.getLogic());
        st.add(PARAMS, taskDeclaration.getParameters());
        st.add(CONTEXT_REFS, taskDeclaration.getContextRefs());
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
        final var st = stg.getInstanceOf("policyDef");
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
        final var st = stg.getInstanceOf("taskDefInputFields");
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
        final var st = stg.getInstanceOf("taskDefOutputFields");
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
        final var st = stg.getInstanceOf("taskDefParameter");
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
        final var st = stg.getInstanceOf("taskDefCtxRef");
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(ALBUM_NAME, albumName);
        st.add(ALBUM_VERSION, albumVersion);
        return st;
    }

    /**
     * Creates a new policy state task definition for a task which belongs to a state which belongs to a policy.
     *
     * @param policyStateTask state task parameters
     * @return a CLI command for a policy state task definition
     */
    public ST createPolicyStateTask(PolicyStateTask policyStateTask) {
        final var st = stg.getInstanceOf("policyStateTask");
        st.add(POLICY_NAME, policyStateTask.getPolicyName());
        st.add(VERSION, policyStateTask.getVersion());
        st.add(STATE_NAME, policyStateTask.getStateName());
        st.add(TASK_LOCAL_NAME, policyStateTask.getTaskLocalName());
        st.add(TASK_NAME, policyStateTask.getTaskName());
        st.add(TASK_VERSION, policyStateTask.getTaskVersion());
        st.add(OUTPUT_TYPE, policyStateTask.getOutputType());
        st.add(OUTPUT_NAME, policyStateTask.getOutputName());
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
        final var st = stg.getInstanceOf("policyStateOutput");
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
     * @param policyStateDef state definition parameters
     * @return a CLI command for a policy state definition
     */
    public ST createPolicyStateDef(PolicyStateDef policyStateDef) {
        final var st = stg.getInstanceOf("policyStateDef");
        st.add(POLICY_NAME, policyStateDef.getPolicyName());
        st.add(VERSION, policyStateDef.getVersion());
        st.add(STATE_NAME, policyStateDef.getStateName());
        st.add(TRIGGER_NAME, policyStateDef.getTriggerName());
        st.add(TRIGGER_VERSION, policyStateDef.getTriggerVersion());
        st.add(DEFAULT_TASK, policyStateDef.getDefaultTask());
        st.add(DEFAULT_TASK_VERSION, policyStateDef.getDefaultTaskVersion());
        st.add(OUTPUTS, policyStateDef.getOutputs());
        st.add(TASKS, policyStateDef.getTasks());
        st.add(TS_LOGIC, policyStateDef.getTsLogic());
        st.add(FINALIZER_LOGICS, policyStateDef.getFinalizerLogics());
        st.add(CTX_REFS, policyStateDef.getCtxRefs());
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
        final var st = stg.getInstanceOf("policyStateTaskSelectionLogic");
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
        final var st = stg.getInstanceOf("policyStateFinalizerLogic");
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
        final var st = stg.getInstanceOf("policyStateContextRef");
        st.add(NAME, name);
        st.add(VERSION, version);
        st.add(STATE_NAME, stateName);
        st.add(ALBUM_NAME, albumName);
        st.add(ALBUM_VERSION, albumVersion);
        return st;
    }

}
