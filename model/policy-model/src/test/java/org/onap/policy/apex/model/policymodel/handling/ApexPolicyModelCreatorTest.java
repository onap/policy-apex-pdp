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

package org.onap.policy.apex.model.policymodel.handling;

import java.util.UUID;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModelCreator;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;

/**
 * Model creator for model tests.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexPolicyModelCreatorTest implements TestApexModelCreator<AxPolicyModel> {

    @Override
    public AxPolicyModel getModel() {
        final AxContextSchema schema0 = new AxContextSchema(new AxArtifactKey("eventContextItem0", "0.0.1"), "Java",
                        "java.lang.String");
        final AxContextSchema schema1 = new AxContextSchema(new AxArtifactKey("eventContextItem1", "0.0.1"), "Java",
                        "java.lang.Long");
        final AxContextSchema schema2 = new AxContextSchema(new AxArtifactKey("StringType", "0.0.1"), "Java",
                        "org.onap.policy.apex.model.policymodel.concepts.TestContextItem000");
        final AxContextSchema schema3 = new AxContextSchema(new AxArtifactKey("MapType", "0.0.1"), "Java",
                        "org.onap.policy.apex.model.policymodel.concepts.TestContextItem00A");

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("ContextSchemas", "0.0.1"));
        schemas.getSchemasMap().put(schema0.getKey(), schema0);
        schemas.getSchemasMap().put(schema1.getKey(), schema1);
        schemas.getSchemasMap().put(schema2.getKey(), schema2);
        schemas.getSchemasMap().put(schema3.getKey(), schema3);

        final AxContextAlbum album0 = new AxContextAlbum(new AxArtifactKey("contextAlbum0", "0.0.1"), "APPLICATION",
                        true, schema3.getKey());
        final AxContextAlbum album1 = new AxContextAlbum(new AxArtifactKey("contextAlbum1", "0.0.1"), "GLOBAL", false,
                        schema2.getKey());

        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("context", "0.0.1"));
        albums.getAlbumsMap().put(album0.getKey(), album0);
        albums.getAlbumsMap().put(album1.getKey(), album1);

        final AxEvent inEvent = new AxEvent(new AxArtifactKey("inEvent", "0.0.1"),
                        "org.onap.policy.apex.model.policymodel.events", "Source", "Target");
        inEvent.getParameterMap().put("IEPAR0",
                        new AxField(new AxReferenceKey(inEvent.getKey(), "IEPAR0"), schema0.getKey()));
        inEvent.getParameterMap().put("IEPAR1",
                        new AxField(new AxReferenceKey(inEvent.getKey(), "IEPAR1"), schema1.getKey()));

        final AxEvent outEvent0 = new AxEvent(new AxArtifactKey("outEvent0", "0.0.1"),
                        "org.onap.policy.apex.model.policymodel.events", "Source", "Target");
        outEvent0.getParameterMap().put("OE0PAR0",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE0PAR0"), schema0.getKey()));
        outEvent0.getParameterMap().put("OE0PAR1",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE0PAR1"), schema1.getKey()));
        outEvent0.getParameterMap().put("OE1PAR0",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE1PAR0"), schema0.getKey()));
        outEvent0.getParameterMap().put("OE1PAR1",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE1PAR1"), schema1.getKey()));

        final AxEvent outEvent1 = new AxEvent(new AxArtifactKey("outEvent1", "0.0.1"),
                        "org.onap.policy.apex.model.policymodel.events", "Source", "Target");
        outEvent1.getParameterMap().put("OE1PAR0",
                        new AxField(new AxReferenceKey(outEvent1.getKey(), "OE1PAR0"), schema0.getKey()));
        outEvent1.getParameterMap().put("OE1PAR1",
                        new AxField(new AxReferenceKey(outEvent1.getKey(), "OE1PAR1"), schema1.getKey()));

        final AxEvents events = new AxEvents(new AxArtifactKey("events", "0.0.1"));
        events.getEventMap().put(inEvent.getKey(), inEvent);
        events.getEventMap().put(outEvent0.getKey(), outEvent0);
        events.getEventMap().put(outEvent1.getKey(), outEvent1);

        final AxTask task = new AxTask(new AxArtifactKey("task", "0.0.1"));

        for (final AxField field : inEvent.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(),
                            "inputFields", field.getKey().getLocalName());
            final AxInputField inputField = new AxInputField(fieldkey, field.getSchema());
            task.getInputFields().put(inputField.getKey().getLocalName(), inputField);
        }

        for (final AxField field : outEvent0.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(),
                            "outputFields", field.getKey().getLocalName());
            final AxOutputField outputField = new AxOutputField(fieldkey, field.getSchema());
            task.getOutputFields().put(outputField.getKey().getLocalName(), outputField);
        }

        for (final AxField field : outEvent1.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(),
                            "outputFields", field.getKey().getLocalName());
            final AxOutputField outputField = new AxOutputField(fieldkey, field.getSchema());
            task.getOutputFields().put(outputField.getKey().getLocalName(), outputField);
        }

        final AxTaskParameter taskPar0 = new AxTaskParameter(new AxReferenceKey(task.getKey(), "taskParameter0"),
                        "Task parameter 0 value");
        final AxTaskParameter taskPar1 = new AxTaskParameter(new AxReferenceKey(task.getKey(), "taskParameter1"),
                        "Task parameter 1 value");

        task.getTaskParameters().put(taskPar0.getKey().getLocalName(), taskPar0);
        task.getTaskParameters().put(taskPar1.getKey().getLocalName(), taskPar1);
        task.getContextAlbumReferences().add(album0.getKey());
        task.getContextAlbumReferences().add(album1.getKey());

        final AxTaskLogic taskLogic = new AxTaskLogic(new AxReferenceKey(task.getKey(), "taskLogic"), "MVEL",
                        "Some task logic");
        task.setTaskLogic(taskLogic);

        final AxTasks tasks = new AxTasks(new AxArtifactKey("tasks", "0.0.1"));
        tasks.getTaskMap().put(task.getKey(), task);

        final AxPolicy policy = new AxPolicy(new AxArtifactKey("policy", "0.0.1"));
        policy.setTemplate("FREEFORM");

        final AxState state = new AxState(new AxReferenceKey(policy.getKey(), "state"));
        final AxTaskSelectionLogic taskSelectionLogic = new AxTaskSelectionLogic(
                        new AxReferenceKey(state.getKey(), "taskSelectionLogic"), "MVEL", "Some TS logic ");

        state.setTrigger(inEvent.getKey());
        state.getContextAlbumReferences().add(album0.getKey());
        state.getContextAlbumReferences().add(album1.getKey());
        state.setTaskSelectionLogic(taskSelectionLogic);
        state.setDefaultTask(task.getKey());

        final AxStateOutput stateOutput0 = new AxStateOutput(new AxReferenceKey(state.getKey(), "stateOutput0"),
                        outEvent0.getKey(), AxReferenceKey.getNullKey());
        state.getStateOutputs().put(stateOutput0.getKey().getLocalName(), stateOutput0);

        final AxStateTaskReference stateTaskReference = new AxStateTaskReference(
                        new AxReferenceKey(state.getKey(), task.getKey().getName()), AxStateTaskOutputType.DIRECT,
                        stateOutput0.getKey());

        state.getTaskReferences().put(task.getKey(), stateTaskReference);

        policy.getStateMap().put(state.getKey().getLocalName(), state);
        policy.setFirstState(state.getKey().getLocalName());

        final AxPolicies policies = new AxPolicies(new AxArtifactKey("policies", "0.0.1"));
        policies.getPolicyMap().put(policy.getKey(), policy);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKey", "0.0.1"));
        final AxPolicyModel policyModel = new AxPolicyModel(new AxArtifactKey("PolicyModel", "0.0.1"));
        policyModel.setKeyInformation(keyInformation);
        policyModel.setSchemas(schemas);
        policyModel.setAlbums(albums);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setPolicies(policies);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        int uuidIncrementer = 0;
        for (final AxKeyInfo keyInfo : policyModel.getKeyInformation().getKeyInfoMap().values()) {
            final String uuidString = String.format("ce9168c-e6df-414f-9646-6da464b6e%03d", uuidIncrementer++);
            keyInfo.setUuid(UUID.fromString(uuidString));
        }

        final AxValidationResult result = new AxValidationResult();
        policyModel.validate(result);

        return policyModel;
    }

    /**
     * Gets another policy model.
     * 
     * @return the model
     */
    public AxPolicyModel getAnotherModel() {
        final AxContextSchema schema0 = new AxContextSchema(new AxArtifactKey("eventContextItemA0", "0.0.1"), "Java",
                        "java.lang.String");
        final AxContextSchema schema1 = new AxContextSchema(new AxArtifactKey("eventContextItemA1", "0.0.1"), "Java",
                        "java.lang.Long");
        final AxContextSchema schema2 = new AxContextSchema(new AxArtifactKey("StringTypeA", "0.0.1"), "Java",
                        "org.onap.policy.apex.model.policymodel.concepts.TestContextItem000");
        final AxContextSchema schema3 = new AxContextSchema(new AxArtifactKey("MapTypeA", "0.0.1"), "Java",
                        "org.onap.policy.apex.model.policymodel.concepts.TestContextItem00A");

        final AxContextSchemas schemas = new AxContextSchemas(new AxArtifactKey("ContextSchemasA", "0.0.1"));
        schemas.getSchemasMap().put(schema0.getKey(), schema0);
        schemas.getSchemasMap().put(schema1.getKey(), schema1);
        schemas.getSchemasMap().put(schema2.getKey(), schema2);
        schemas.getSchemasMap().put(schema3.getKey(), schema3);

        final AxContextAlbum album0 = new AxContextAlbum(new AxArtifactKey("contextAlbumA0", "0.0.1"), "APPLICATION",
                        true, schema3.getKey());
        final AxContextAlbum album1 = new AxContextAlbum(new AxArtifactKey("contextAlbumA1", "0.0.1"), "GLOBAL", false,
                        schema2.getKey());

        final AxContextAlbums albums = new AxContextAlbums(new AxArtifactKey("contextA", "0.0.1"));
        albums.getAlbumsMap().put(album0.getKey(), album0);
        albums.getAlbumsMap().put(album1.getKey(), album1);

        final AxEvent inEvent = new AxEvent(new AxArtifactKey("inEventA", "0.0.1"),
                        "org.onap.policy.apex.model.policymodel.events", "Source", "Target");
        inEvent.getParameterMap().put("IEPARA0",
                        new AxField(new AxReferenceKey(inEvent.getKey(), "IEPARA0"), schema0.getKey()));
        inEvent.getParameterMap().put("IEPARA1",
                        new AxField(new AxReferenceKey(inEvent.getKey(), "IEPARA1"), schema1.getKey()));

        final AxEvent outEvent0 = new AxEvent(new AxArtifactKey("outEventA0", "0.0.1"),
                        "org.onap.policy.apex.model.policymodel.events", "Source", "Target");
        outEvent0.getParameterMap().put("OE0PARA0",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE0PARA0"), schema0.getKey()));
        outEvent0.getParameterMap().put("OE0PARA1",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE0PARA1"), schema1.getKey()));
        outEvent0.getParameterMap().put("OE1PARA0",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE1PARA0"), schema0.getKey()));
        outEvent0.getParameterMap().put("OE1PARA1",
                        new AxField(new AxReferenceKey(outEvent0.getKey(), "OE1PARA1"), schema1.getKey()));

        final AxEvent outEvent1 = new AxEvent(new AxArtifactKey("outEventA1", "0.0.1"),
                        "org.onap.policy.apex.model.policymodel.events", "Source", "Target");
        outEvent1.getParameterMap().put("OE1PARA0",
                        new AxField(new AxReferenceKey(outEvent1.getKey(), "OE1PARA0"), schema0.getKey()));
        outEvent1.getParameterMap().put("OE1PARA1",
                        new AxField(new AxReferenceKey(outEvent1.getKey(), "OE1PARA1"), schema1.getKey()));

        final AxEvents events = new AxEvents(new AxArtifactKey("eventsA", "0.0.1"));
        events.getEventMap().put(inEvent.getKey(), inEvent);
        events.getEventMap().put(outEvent0.getKey(), outEvent0);
        events.getEventMap().put(outEvent1.getKey(), outEvent1);

        final AxTask task = new AxTask(new AxArtifactKey("taskA", "0.0.1"));

        for (final AxField field : inEvent.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(),
                            "inputFieldsA", field.getKey().getLocalName());
            final AxInputField inputField = new AxInputField(fieldkey, field.getSchema());
            task.getInputFields().put(inputField.getKey().getLocalName(), inputField);
        }

        for (final AxField field : outEvent0.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(),
                            "outputFieldsA", field.getKey().getLocalName());
            final AxOutputField outputField = new AxOutputField(fieldkey, field.getSchema());
            task.getOutputFields().put(outputField.getKey().getLocalName(), outputField);
        }

        for (final AxField field : outEvent1.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(),
                            "outputFieldsA", field.getKey().getLocalName());
            final AxOutputField outputField = new AxOutputField(fieldkey, field.getSchema());
            task.getOutputFields().put(outputField.getKey().getLocalName(), outputField);
        }

        final AxTaskParameter taskPar0 = new AxTaskParameter(new AxReferenceKey(task.getKey(), "taskParameterA0"),
                        "Task parameter 0 value");
        final AxTaskParameter taskPar1 = new AxTaskParameter(new AxReferenceKey(task.getKey(), "taskParameterA1"),
                        "Task parameter 1 value");

        task.getTaskParameters().put(taskPar0.getKey().getLocalName(), taskPar0);
        task.getTaskParameters().put(taskPar1.getKey().getLocalName(), taskPar1);
        task.getContextAlbumReferences().add(album0.getKey());
        task.getContextAlbumReferences().add(album1.getKey());

        final AxTaskLogic taskLogic = new AxTaskLogic(new AxReferenceKey(task.getKey(), "taskLogicA"), "MVEL",
                        "Some task logic");
        task.setTaskLogic(taskLogic);

        final AxTasks tasks = new AxTasks(new AxArtifactKey("tasksA", "0.0.1"));
        tasks.getTaskMap().put(task.getKey(), task);

        final AxPolicy policy = new AxPolicy(new AxArtifactKey("policyA", "0.0.1"));
        policy.setTemplate("FREEFORM");

        final AxState state = new AxState(new AxReferenceKey(policy.getKey(), "stateA"));
        final AxTaskSelectionLogic taskSelectionLogic = new AxTaskSelectionLogic(
                        new AxReferenceKey(state.getKey(), "taskSelectionLogicA"), "MVEL", "Some TS logic ");

        state.setTrigger(inEvent.getKey());
        state.getContextAlbumReferences().add(album0.getKey());
        state.getContextAlbumReferences().add(album1.getKey());
        state.setTaskSelectionLogic(taskSelectionLogic);
        state.setDefaultTask(task.getKey());

        final AxStateOutput stateOutput0 = new AxStateOutput(new AxReferenceKey(state.getKey(), "stateOutputA0"),
                        outEvent0.getKey(), AxReferenceKey.getNullKey());
        state.getStateOutputs().put(stateOutput0.getKey().getLocalName(), stateOutput0);

        final AxStateTaskReference stateTaskReference = new AxStateTaskReference(
                        new AxReferenceKey(state.getKey(), task.getKey().getName()), AxStateTaskOutputType.DIRECT,
                        stateOutput0.getKey());

        state.getTaskReferences().put(task.getKey(), stateTaskReference);

        policy.getStateMap().put(state.getKey().getLocalName(), state);
        policy.setFirstState(state.getKey().getLocalName());

        final AxPolicies policies = new AxPolicies(new AxArtifactKey("policiesA", "0.0.1"));
        policies.getPolicyMap().put(policy.getKey(), policy);

        final AxKeyInformation keyInformation = new AxKeyInformation(new AxArtifactKey("KeyInfoMapKeyA", "0.0.1"));
        final AxPolicyModel policyModel = new AxPolicyModel(new AxArtifactKey("PolicyModelA", "0.0.1"));
        policyModel.setKeyInformation(keyInformation);
        policyModel.setSchemas(schemas);
        policyModel.setAlbums(albums);
        policyModel.setEvents(events);
        policyModel.setTasks(tasks);
        policyModel.setPolicies(policies);
        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        int uuidIncrementer = 0;
        for (final AxKeyInfo keyInfo : policyModel.getKeyInformation().getKeyInfoMap().values()) {
            final String uuidString = String.format("ce9168c-e6df-414f-9646-6da464b6e%03d", uuidIncrementer++);
            keyInfo.setUuid(UUID.fromString(uuidString));
        }

        final AxValidationResult result = new AxValidationResult();
        policyModel.validate(result);

        return policyModel;
    }

    @Override
    public AxPolicyModel getMalstructuredModel() {
        final AxPolicyModel policyModel = new AxPolicyModel(new AxArtifactKey("policyModel", "0.0.1"));
        return policyModel;
    }

    @Override
    public AxPolicyModel getObservationModel() {
        final AxPolicyModel policyModel = getModel();

        final AxState state = policyModel.getPolicies().get("policy").getStateMap().get("state");
        final AxTask task = policyModel.getTasks().get("task");

        final AxStateFinalizerLogic stateFinalizerLogic = new AxStateFinalizerLogic(
                        new AxReferenceKey(state.getKey(), "SFL"), "MVEL", "Some SF logic ");
        state.getStateFinalizerLogicMap().put(stateFinalizerLogic.getKey().getLocalName(), stateFinalizerLogic);
        final AxStateTaskReference stateTaskReference = new AxStateTaskReference(
                        new AxReferenceKey(state.getKey(), task.getKey().getName()), AxStateTaskOutputType.LOGIC,
                        stateFinalizerLogic.getKey());

        state.getTaskReferences().put(task.getKey(), stateTaskReference);

        return policyModel;
    }

    @Override
    public AxPolicyModel getWarningModel() {
        final AxPolicyModel policyModel = getModel();

        final AxState anotherState = new AxState(
                        new AxReferenceKey(new AxArtifactKey("policy", "0.0.1"), "anotherState"));

        final AxEvent inEvent = policyModel.getEvents().getEventMap().get(new AxArtifactKey("inEvent", "0.0.1"));
        final AxEvent outEvent0 = policyModel.getEvents().getEventMap().get(new AxArtifactKey("outEvent0", "0.0.1"));

        final AxTask anotherTask = new AxTask(new AxArtifactKey("anotherTask", "0.0.1"));

        for (final AxField field : inEvent.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(anotherTask.getKey().getName(),
                            anotherTask.getKey().getVersion(), "inputFields", field.getKey().getLocalName());
            final AxInputField inputField = new AxInputField(fieldkey, field.getSchema());
            anotherTask.getInputFields().put(inputField.getKey().getLocalName(), inputField);
        }

        for (final AxField field : outEvent0.getFields()) {
            final AxReferenceKey fieldkey = new AxReferenceKey(anotherTask.getKey().getName(),
                            anotherTask.getKey().getVersion(), "outputFields", field.getKey().getLocalName());
            final AxOutputField outputField = new AxOutputField(fieldkey, field.getSchema());
            anotherTask.getOutputFields().put(outputField.getKey().getLocalName(), outputField);
        }

        final AxTaskParameter taskPar0 = new AxTaskParameter(new AxReferenceKey(anotherTask.getKey(), "taskParameter0"),
                        "Task parameter 0 value");
        final AxTaskParameter taskPar1 = new AxTaskParameter(new AxReferenceKey(anotherTask.getKey(), "taskParameter1"),
                        "Task parameter 1 value");

        anotherTask.getTaskParameters().put(taskPar0.getKey().getLocalName(), taskPar0);
        anotherTask.getTaskParameters().put(taskPar1.getKey().getLocalName(), taskPar1);

        final AxTaskLogic taskLogic = new AxTaskLogic(new AxReferenceKey(anotherTask.getKey(), "taskLogic"), "MVEL",
                        "Some task logic");
        anotherTask.setTaskLogic(taskLogic);
        policyModel.getTasks().getTaskMap().put(anotherTask.getKey(), anotherTask);

        final AxStateOutput anotherStateOutput0 = new AxStateOutput(
                        new AxReferenceKey(anotherState.getKey(), "stateOutput0"), outEvent0.getKey(),
                        AxReferenceKey.getNullKey());
        anotherState.setTrigger(inEvent.getKey());
        anotherState.getStateOutputs().put(anotherStateOutput0.getKey().getLocalName(), anotherStateOutput0);
        anotherState.setDefaultTask(anotherTask.getKey());
        final AxStateTaskReference anotherStateTaskReference = new AxStateTaskReference(
                        new AxReferenceKey(anotherState.getKey(), anotherTask.getKey().getName()),
                        AxStateTaskOutputType.DIRECT, anotherStateOutput0.getKey());
        anotherState.getTaskReferences().put(anotherTask.getKey(), anotherStateTaskReference);

        policyModel.getPolicies().getPolicyMap().get(new AxArtifactKey("policy", "0.0.1")).getStateMap()
                        .put(anotherState.getKey().getLocalName(), anotherState);

        policyModel.getKeyInformation().generateKeyInfo(policyModel);

        return policyModel;
    }

    @Override
    public AxPolicyModel getInvalidModel() {
        final AxPolicyModel policyModel = getModel();

        policyModel.getAlbums().get(new AxArtifactKey("contextAlbum0", "0.0.1")).setScope("UNDEFINED");
        policyModel.getAlbums().get(new AxArtifactKey("contextAlbum1", "0.0.1")).setScope("UNDEFINED");

        final AxEvent outEvent0 = policyModel.getEvents().get("outEvent0");
        outEvent0.getParameterMap().remove("OE1PAR0");
        outEvent0.getParameterMap().remove("OE1PAR1");

        return policyModel;
    }
}
