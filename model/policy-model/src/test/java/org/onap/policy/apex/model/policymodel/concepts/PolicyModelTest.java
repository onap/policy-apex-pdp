/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.model.policymodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.handling.SupportApexPolicyModelCreator;

/**
 * Test policy models.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyModelTest {

    @Test
    public void testPolicyModel() {
        assertNotNull(new AxPolicyModel());
        assertNotNull(new AxPolicyModel(new AxArtifactKey()));
        assertNotNull(new AxPolicyModel(new AxArtifactKey(), new AxContextSchemas(), new AxKeyInformation(),
                        new AxEvents(), new AxContextAlbums(), new AxTasks(), new AxPolicies()));

        final AxArtifactKey modelKey = new AxArtifactKey("ModelKey", "0.0.1");
        final AxArtifactKey schemasKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxArtifactKey eventsKey = new AxArtifactKey("EventsKey", "0.0.1");
        final AxArtifactKey keyInfoKey = new AxArtifactKey("SchemasKey", "0.0.1");
        final AxArtifactKey albumsKey = new AxArtifactKey("AlbumsKey", "0.0.1");
        final AxArtifactKey tasksKey = new AxArtifactKey("TasksKey", "0.0.1");
        final AxArtifactKey policiesKey = new AxArtifactKey("PoliciesKey", "0.0.1");

        AxPolicyModel model = new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey));

        model.register();

        assertNotNull(model.getContextModel());
        assertEquals("ModelKey:0.0.1", model.getKeys().get(0).getId());

        model.clean();
        assertNotNull(model);
        assertEquals("AxPolicyModel:(AxPolicyModel:(key=AxArtifactKey:(n", model.toString().substring(0, 50));

        final AxPolicyModel clonedModel = new AxPolicyModel(model);

        assertNotEquals(0, model.hashCode());

        // disabling sonar because this code tests the equals() method
        assertEquals(model, model); // NOSONAR
        assertEquals(model, clonedModel);
        assertNotEquals(model, "Hello");
        assertNotEquals(model, new AxPolicyModel(new AxArtifactKey()));
        assertNotEquals(model, new AxPolicyModel(AxArtifactKey.getNullKey(), new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey)));
        assertNotEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(), new AxKeyInformation(keyInfoKey),
                        new AxEvents(eventsKey), new AxContextAlbums(albumsKey), new AxTasks(tasksKey),
                        new AxPolicies(policiesKey)));
        assertNotEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey), new AxKeyInformation(),
                        new AxEvents(eventsKey), new AxContextAlbums(albumsKey), new AxTasks(tasksKey),
                        new AxPolicies(policiesKey)));
        assertNotEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey)));
        assertNotEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey)));
        assertNotEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(), new AxPolicies(policiesKey)));
        assertNotEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies()));
        assertEquals(model, new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey)));

        assertEquals(0, model.compareTo(model));
        assertEquals(0, model.compareTo(clonedModel));
        assertNotEquals(0, model.compareTo(new AxArtifactKey()));
        assertNotEquals(0, model.compareTo(new AxPolicyModel(AxArtifactKey.getNullKey(),
                        new AxContextSchemas(schemasKey), new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey),
                        new AxContextAlbums(albumsKey), new AxTasks(tasksKey), new AxPolicies(policiesKey))));
        assertNotEquals(0, model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey))));
        assertNotEquals(0,
                        model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                                        new AxKeyInformation(), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                                        new AxTasks(tasksKey), new AxPolicies(policiesKey))));
        assertNotEquals(0, model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey))));
        assertNotEquals(0,
                        model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey),
                                        new AxContextAlbums(), new AxTasks(tasksKey), new AxPolicies(policiesKey))));
        assertNotEquals(0,
                        model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey),
                                        new AxContextAlbums(albumsKey), new AxTasks(), new AxPolicies(policiesKey))));
        assertNotEquals(0,
                        model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey),
                                        new AxContextAlbums(albumsKey), new AxTasks(tasksKey), new AxPolicies())));
        assertEquals(0, model.compareTo(new AxPolicyModel(modelKey, new AxContextSchemas(schemasKey),
                        new AxKeyInformation(keyInfoKey), new AxEvents(eventsKey), new AxContextAlbums(albumsKey),
                        new AxTasks(tasksKey), new AxPolicies(policiesKey))));

        model = new SupportApexPolicyModelCreator().getModel();

        AxValidationResult result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxArtifactKey savedPolicyKey = model.getKey();
        model.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.setKey(savedPolicyKey);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxField badField = new AxField(new AxReferenceKey(model.getEvents().get("inEvent").getKey(), "BadField"),
                        new AxArtifactKey("NonExistantSchema", "0.0.1"));
        model.getEvents().get("inEvent").getParameterMap().put(badField.getKey().getLocalName(), badField);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getEvents().get("inEvent").getParameterMap().remove(badField.getKey().getLocalName());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxContextAlbum badAlbum = new AxContextAlbum(new AxArtifactKey("BadAlbum", "0.0.1"), "SomeScope", true,
                        new AxArtifactKey("NonExistantSchema", "0.0.1"));
        model.getAlbums().getAlbumsMap().put(badAlbum.getKey(), badAlbum);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getAlbums().getAlbumsMap().remove(badAlbum.getKey());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxInputField badInField = new AxInputField(
                        new AxReferenceKey(model.getTasks().get("task").getKey(), "BadInField"),
                        new AxArtifactKey("NonExistantSchema", "0.0.1"));
        model.getTasks().get("task").getInputFields().put(badInField.getKey().getLocalName(), badInField);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getTasks().get("task").getInputFields().remove(badInField.getKey().getLocalName());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxOutputField badOutField = new AxOutputField(
                        new AxReferenceKey(model.getTasks().get("task").getKey(), "BadOutField"),
                        new AxArtifactKey("NonExistantSchema", "0.0.1"));
        model.getTasks().get("task").getOutputFields().put(badOutField.getKey().getLocalName(), badOutField);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getTasks().get("task").getOutputFields().remove(badOutField.getKey().getLocalName());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        model.getTasks().get("task").getContextAlbumReferences()
                        .add(new AxArtifactKey("NonExistantContextAlbum", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getTasks().get("task").getContextAlbumReferences()
                        .remove(new AxArtifactKey("NonExistantContextAlbum", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").getContextAlbumReferences()
                        .add(new AxArtifactKey("NonExistantContextAlbum", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").getContextAlbumReferences()
                        .remove(new AxArtifactKey("NonExistantContextAlbum", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxArtifactKey savedTrigger = model.getPolicies().get("policy").getStateMap().get("state").getTrigger();
        model.getPolicies().get("policy").getStateMap().get("state")
                        .setTrigger(new AxArtifactKey("NonExistantEvent", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").setTrigger(savedTrigger);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxArtifactKey savedDefaultTask = model.getPolicies().get("policy").getStateMap().get("state")
                        .getDefaultTask();
        model.getPolicies().get("policy").getStateMap().get("state")
                        .setDefaultTask(new AxArtifactKey("NonExistantTask", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").setDefaultTask(savedDefaultTask);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // It is OK not to have TSL
        final AxTaskSelectionLogic savedTaskSelectionLogic = model.getPolicies().get("policy").getStateMap()
                        .get("state").getTaskSelectionLogic();
        model.getPolicies().get("policy").getStateMap().get("state")
                        .setTaskSelectionLogic(new AxTaskSelectionLogic(AxReferenceKey.getNullKey()));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        model.getTasks().get("task").getInputFields().put(badInField.getKey().getLocalName(), badInField);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getTasks().get("task").getInputFields().remove(badInField.getKey().getLocalName());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").setTaskSelectionLogic(savedTaskSelectionLogic);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxStateOutput badStateOutput = new AxStateOutput(
                        new AxReferenceKey(model.getPolicies().get("policy").getStateMap().get("state").getKey(),
                                        "BadSO"),
                        new AxArtifactKey("NonExistantEvent", "0.0.1"), AxReferenceKey.getNullKey());
        model.getPolicies().get("policy").getStateMap().get("state").getStateOutputs()
                        .put(badStateOutput.getKey().getLocalName(), badStateOutput);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").getStateOutputs()
                        .remove(badStateOutput.getKey().getLocalName());
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxStateTaskReference badTaskReference = new AxStateTaskReference(
                        new AxReferenceKey(model.getPolicies().get("policy").getStateMap().get("state").getKey(),
                                        "NonExistantTask"),
                        AxStateTaskOutputType.LOGIC, badStateOutput.getKey());
        model.getPolicies().get("policy").getStateMap().get("state").getTaskReferences()
                        .put(new AxArtifactKey("NonExistantTask", "0.0.1"), badTaskReference);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        badTaskReference.setStateTaskOutputType(AxStateTaskOutputType.DIRECT);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        model.getPolicies().get("policy").getStateMap().get("state").getTaskReferences()
                        .remove(new AxArtifactKey("NonExistantTask", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxStateTaskReference tr = model.getPolicies().get("policy").getStateMap().get("state").getTaskReferences()
                        .get(new AxArtifactKey("task", "0.0.1"));

        final String savedStateOutputName = tr.getOutput().getLocalName();
        tr.getOutput().setLocalName("NonExistantOutput");
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        tr.getOutput().setLocalName(savedStateOutputName);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxStateOutput so = model.getPolicies().get("policy").getStateMap().get("state").getStateOutputs()
                        .get(savedStateOutputName);

        final AxArtifactKey savedOutEvent = so.getOutgingEvent();
        so.setOutgoingEvent(new AxArtifactKey("NonExistantEvent", "0.0.1"));
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        so.setOutgoingEvent(savedOutEvent);
        result = new AxValidationResult();
        result = model.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());
    }
}
