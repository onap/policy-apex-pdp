/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

/**
 * Test policy states.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class StateTest {

    @Test
    public void testState() {
        final TreeMap<String, AxStateOutput> soEmptyMap = new TreeMap<>();
        final TreeSet<AxArtifactKey> ctxtEmptySet = new TreeSet<>();
        final TreeMap<String, AxStateFinalizerLogic> sflEmptyMap = new TreeMap<>();
        final TreeMap<AxArtifactKey, AxStateTaskReference> trEmptyMap = new TreeMap<>();

        final TreeMap<String, AxStateOutput> soMap = new TreeMap<>();
        final TreeSet<AxArtifactKey> ctxtSet = new TreeSet<>();
        final TreeMap<String, AxStateFinalizerLogic> sflMap = new TreeMap<>();
        final TreeMap<AxArtifactKey, AxStateTaskReference> trMap = new TreeMap<>();

        assertNotNull(new AxState());
        assertNotNull(new AxState(new AxReferenceKey()));
        assertNotNull(new AxState(new AxStateParamsBuilder().key(new AxReferenceKey()).trigger(new AxArtifactKey())
                        .stateOutputs(soEmptyMap).contextAlbumReferenceSet(ctxtEmptySet)
                        .taskSelectionLogic(new AxTaskSelectionLogic()).stateFinalizerLogicMap(sflEmptyMap)
                        .defaultTask(new AxArtifactKey()).taskReferenceMap(trEmptyMap)));

        final AxState state = new AxState();

        final AxReferenceKey stateKey = new AxReferenceKey("PolicyName", "0.0.1", "StateName");
        final AxReferenceKey stateKeyNext = new AxReferenceKey("PolicyName", "0.0.1", "StateNameNext");
        final AxReferenceKey stateKeyBad = new AxReferenceKey("PolicyName", "0.0.1", "BadStateName");
        final AxArtifactKey triggerKey = new AxArtifactKey("TriggerName", "0.0.1");
        final AxTaskSelectionLogic tsl = new AxTaskSelectionLogic(stateKey, "TSL", "LogicFlavour", "Some Logic");
        final AxArtifactKey defTaskKey = new AxArtifactKey("TaskName", "0.0.1");
        final AxArtifactKey taskKey1 = new AxArtifactKey("Task1", "0.0.1");
        final AxArtifactKey taskKey2 = new AxArtifactKey("Task2", "0.0.1");
        final AxArtifactKey taskKeyBad = new AxArtifactKey("TaskBad", "0.0.1");

        assertThatThrownBy(() -> state.setKey(null))
            .hasMessageContaining("key may not be null");
        state.setKey(stateKey);
        assertEquals("PolicyName:0.0.1:NULL:StateName", state.getKey().getId());
        assertEquals("PolicyName:0.0.1:NULL:StateName", state.getKeys().get(0).getId());

        final AxStateOutput so0 = new AxStateOutput(new AxReferenceKey(stateKey, "SO0"), triggerKey,
                        new AxReferenceKey());
        final AxStateOutput soU = new AxStateOutput(new AxReferenceKey(stateKey, "SOU"), triggerKey, stateKeyNext);
        final AxStateOutput soSame = new AxStateOutput(new AxReferenceKey(stateKey, "SOU"), triggerKey, stateKey);
        final AxArtifactKey cr0 = new AxArtifactKey("ContextReference", "0.0.1");
        final AxStateFinalizerLogic sfl = new AxStateFinalizerLogic(stateKey, "SFLogicName", "LogicFlavour", "Logic");
        final AxStateFinalizerLogic sflU = new AxStateFinalizerLogic(stateKey, "UnusedSFLogicName", "LogicFlavour",
                        "Logic");
        final AxStateTaskReference str0 = new AxStateTaskReference(new AxReferenceKey(stateKey, "STR0"),
                        AxStateTaskOutputType.DIRECT, so0.getKey());
        final AxStateTaskReference str1 = new AxStateTaskReference(new AxReferenceKey(stateKey, "STR1"),
                        AxStateTaskOutputType.DIRECT, so0.getKey());
        final AxStateTaskReference str2 = new AxStateTaskReference(new AxReferenceKey(stateKey, "STR2"),
                        AxStateTaskOutputType.LOGIC, sfl.getKey());

        final AxStateTaskReference strBadState = new AxStateTaskReference(new AxReferenceKey(stateKeyBad, "STR2"),
                        AxStateTaskOutputType.LOGIC, sfl.getKey());
        final AxStateTaskReference strBadStateOutput = new AxStateTaskReference(new AxReferenceKey(stateKey, "STR2"),
                        AxStateTaskOutputType.UNDEFINED, sfl.getKey());
        final AxStateTaskReference strBadStateFinalizerLogic = new AxStateTaskReference(
                        new AxReferenceKey(stateKeyBad, "STR2"), AxStateTaskOutputType.LOGIC,
                        new AxReferenceKey(stateKey, "SomeSFL"));

        soMap.put(so0.getKey().getLocalName(), so0);
        ctxtSet.add(cr0);
        sflMap.put(sfl.getKey().getLocalName(), sfl);
        trMap.put(defTaskKey.getKey(), str0);
        trMap.put(taskKey1.getKey(), str1);
        trMap.put(taskKey2.getKey(), str2);

        assertThatThrownBy(() -> state.setTrigger(null))
            .hasMessageContaining("trigger may not be null");
        state.setTrigger(triggerKey);
        assertEquals(triggerKey, state.getTrigger());

        assertThatThrownBy(() -> state.setStateOutputs(null))
            .hasMessageContaining("stateOutputs may not be null");
        state.setStateOutputs(soMap);
        assertEquals(soMap, state.getStateOutputs());

        assertThatThrownBy(() -> state.setContextAlbumReferences(null))
            .hasMessageContaining("contextAlbumReferenceSet may not be null");
        state.setContextAlbumReferences(ctxtSet);
        assertEquals(ctxtSet, state.getContextAlbumReferences());

        assertThatThrownBy(() -> state.setTaskSelectionLogic(null))
            .hasMessageContaining("taskSelectionLogic may not be null");
        assertEquals(false, state.checkSetTaskSelectionLogic());
        state.setTaskSelectionLogic(tsl);
        assertEquals(tsl, state.getTaskSelectionLogic());
        assertEquals(true, state.checkSetTaskSelectionLogic());

        assertThatThrownBy(() -> state.setStateFinalizerLogicMap(null))
            .hasMessageContaining("stateFinalizerLogic may not be null");
        state.setStateFinalizerLogicMap(sflMap);
        assertEquals(sflMap, state.getStateFinalizerLogicMap());

        assertThatThrownBy(() -> state.setDefaultTask(null))
            .hasMessageContaining("defaultTask may not be null");
        state.setDefaultTask(defTaskKey);
        assertEquals(defTaskKey, state.getDefaultTask());

        assertThatThrownBy(() -> state.setTaskReferences(null))
            .hasMessageContaining("taskReferenceMap may not be null");
        state.setTaskReferences(trMap);
        assertEquals(trMap, state.getTaskReferences());

        state.afterUnmarshal(null, null);
        assertEquals(state.getKey(), state.getKeys().get(0));
        state.getTaskSelectionLogic().getKey().setLocalName(AxKey.NULL_KEY_NAME);
        state.afterUnmarshal(null, null);
        assertEquals(state.getKey(), state.getKeys().get(0));

        final Set<String> stateSet = state.getNextStateSet();
        assertEquals(1, stateSet.size());

        AxValidationResult result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        state.setKey(stateKey);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setTrigger(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        state.setTrigger(triggerKey);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setStateOutputs(soEmptyMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        state.setStateOutputs(soMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        so0.getKey().setParentLocalName("Zooby");
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        so0.getKey().setParentLocalName("StateName");
        state.setStateOutputs(soMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        soMap.put("NullOutput", null);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        soMap.remove("NullOutput");
        state.setStateOutputs(soMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        soMap.put("DupOutput", so0);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        soMap.remove("DupOutput");
        state.setStateOutputs(soMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        soMap.put("UnusedOutput", soU);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        soMap.remove("UnusedOutput");
        state.setStateOutputs(soMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        soMap.put("OutputToSameState", soSame);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        soMap.remove("OutputToSameState");
        state.setStateOutputs(soMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Empty context reference set is OK
        state.setContextAlbumReferences(ctxtEmptySet);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setContextAlbumReferences(ctxtSet);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        ctxtSet.add(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        ctxtSet.remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        // Null TSL is OK
        state.getTaskSelectionLogic().setKey(AxReferenceKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.getTaskSelectionLogic().setKey(new AxReferenceKey(stateKey, "TSL"));
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setDefaultTask(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        state.setDefaultTask(defTaskKey);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setTaskReferences(trEmptyMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        state.setTaskReferences(trMap);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        trMap.put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        trMap.remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        trMap.put(AxArtifactKey.getNullKey(), str0);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        trMap.remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        trMap.put(taskKeyBad, strBadStateOutput);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        trMap.remove(taskKeyBad);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        trMap.put(taskKeyBad, strBadState);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        trMap.remove(taskKeyBad);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        trMap.put(taskKeyBad, strBadStateFinalizerLogic);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        trMap.remove(taskKeyBad);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.setDefaultTask(new AxArtifactKey("NonExistantTask", "0.0.1"));
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        state.setDefaultTask(defTaskKey);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        sflMap.put("NullSFL", null);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        sflMap.remove("NullSFL");
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        sflMap.put(sflU.getKey().getLocalName(), sflU);
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        sflMap.remove(sflU.getKey().getLocalName());
        result = new AxValidationResult();
        result = state.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        state.clean();

        final AxState clonedState = new AxState(state);
        assertEquals("AxState:(stateKey=AxReferenceKey:(parent", clonedState.toString().substring(0, 40));

        assertFalse(state.hashCode() == 0);

        assertTrue(state.equals(state));
        assertTrue(state.equals(clonedState));
        assertFalse(state.equals(null));
        assertFalse(state.equals((Object) "Hello"));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(new AxReferenceKey()).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(new AxArtifactKey())
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soEmptyMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtEmptySet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet)
                        .taskSelectionLogic(new AxTaskSelectionLogic()).stateFinalizerLogicMap(sflMap)
                        .defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflEmptyMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(new AxArtifactKey()).taskReferenceMap(trMap))));
        assertFalse(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trEmptyMap))));
        assertTrue(state.equals(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));

        assertEquals(0, state.compareTo(state));
        assertEquals(0, state.compareTo(clonedState));
        assertNotEquals(0, state.compareTo(new AxArtifactKey()));
        assertNotEquals(0, state.compareTo(null));
        assertNotEquals(0,
                        state.compareTo(new AxState(new AxStateParamsBuilder().key(new AxReferenceKey())
                                        .trigger(triggerKey).stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet)
                                        .taskSelectionLogic(tsl).stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey)
                                        .taskReferenceMap(trMap))));
        assertNotEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey)
                        .trigger(new AxArtifactKey()).stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet)
                        .taskSelectionLogic(tsl).stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey)
                        .taskReferenceMap(trMap))));
        assertNotEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soEmptyMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertNotEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtEmptySet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertNotEquals(0,
                        state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet)
                                        .taskSelectionLogic(new AxTaskSelectionLogic()).stateFinalizerLogicMap(sflMap)
                                        .defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertNotEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflEmptyMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));
        assertNotEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(new AxArtifactKey()).taskReferenceMap(trMap))));
        assertNotEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trEmptyMap))));
        assertEquals(0, state.compareTo(new AxState(new AxStateParamsBuilder().key(stateKey).trigger(triggerKey)
                        .stateOutputs(soMap).contextAlbumReferenceSet(ctxtSet).taskSelectionLogic(tsl)
                        .stateFinalizerLogicMap(sflMap).defaultTask(defTaskKey).taskReferenceMap(trMap))));

        assertNotNull(state.getKeys());
    }
}
