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

package org.onap.policy.apex.model.policymodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicies;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTree;
import org.onap.policy.apex.model.policymodel.handling.TestApexPolicyModelCreator;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestPolicies {

    @Test
    public void testPolicies() {
        final TreeMap<String, AxState> stateMap = new TreeMap<>();
        final TreeMap<String, AxState> stateMapEmpty = new TreeMap<>();

        assertNotNull(new AxPolicy());
        assertNotNull(new AxPolicy(new AxArtifactKey()));
        assertNotNull(new AxPolicy(new AxArtifactKey(), "PolicyTemplate", stateMapEmpty, "FirstState"));

        AxPolicy policy = new AxPolicy();

        final AxArtifactKey policyKey = new AxArtifactKey("PolicyName", "0.0.1");

        final AxState firstState = new AxState(new AxReferenceKey(policy.getKey(), "FirstState"));
        final AxState badState = new AxState(new AxReferenceKey(policy.getKey(), "BadState"));
        final AxStateOutput badSO = new AxStateOutput(badState.getKey(), AxArtifactKey.getNullKey(),
                new AxReferenceKey(policyKey, "BadNextState"));
        badState.getStateOutputs().put(badSO.getKey().getLocalName(), badSO);
        stateMap.put(firstState.getKey().getLocalName(), firstState);

        try {
            policy.setKey(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("key may not be null", e.getMessage());
        }

        policy.setKey(policyKey);
        assertEquals("PolicyName:0.0.1", policy.getKey().getID());
        assertEquals("PolicyName:0.0.1", policy.getKeys().get(0).getID());

        try {
            policy.setTemplate(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("template may not be null", e.getMessage());
        }

        policy.setTemplate("PolicyTemplate");
        assertEquals("PolicyTemplate", policy.getTemplate());

        try {
            policy.setStateMap(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("stateMap may not be null", e.getMessage());
        }

        policy.setStateMap(stateMap);
        assertEquals(stateMap, policy.getStateMap());

        try {
            policy.setFirstState(null);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("firstState may not be null", e.getMessage());
        }

        policy.setFirstState("FirstState");
        assertEquals("FirstState", policy.getFirstState());

        assertEquals("PolicyName:0.0.1", policy.getKeys().get(0).getID());

        policy = new TestApexPolicyModelCreator().getModel().getPolicies().get("policy");

        AxValidationResult result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxArtifactKey savedPolicyKey = policy.getKey();
        policy.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policy.setKey(savedPolicyKey);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final String savedTemplate = policy.getTemplate();
        policy.setTemplate("");
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        policy.setTemplate(savedTemplate);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final Map<String, AxState> savedStateMap = policy.getStateMap();

        policy.setStateMap(stateMapEmpty);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policy.setStateMap(savedStateMap);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put(AxKey.NULL_KEY_NAME, firstState);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove(AxKey.NULL_KEY_NAME);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put("NullState", null);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove("NullState");
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put("BadStateKey", firstState);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove("BadStateKey");
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put(badState.getKey().getLocalName(), badState);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove(badState.getKey().getLocalName());
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final String savedFirstState = policy.getFirstState();

        policy.setFirstState("");
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policy.setFirstState(savedFirstState);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policy.setFirstState("NonExistantFirstState");
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policy.setFirstState(savedFirstState);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxState clonedState = new AxState(policy.getStateMap().get("state"));
        clonedState.getKey().setLocalName("ClonedState");
        clonedState.afterUnmarshal(null, null);

        savedStateMap.put(clonedState.getKey().getLocalName(), clonedState);
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.WARNING, result.getValidationResult());

        savedStateMap.remove(clonedState.getKey().getLocalName());
        result = new AxValidationResult();
        result = policy.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policy.clean();

        final AxPolicy clonedPolicy = new AxPolicy(policy);
        assertEquals("AxPolicy:(key=AxArtifactKey:(name=policy,version=0.0.1),template=FREEFORM,sta",
                clonedPolicy.toString().substring(0, 77));

        assertFalse(policy.hashCode() == 0);

        assertTrue(policy.equals(policy));
        assertTrue(policy.equals(clonedPolicy));
        assertFalse(policy.equals(null));
        assertFalse(policy.equals("Hello"));
        assertFalse(
                policy.equals(new AxPolicy(AxArtifactKey.getNullKey(), savedTemplate, savedStateMap, savedFirstState)));
        assertFalse(policy.equals(new AxPolicy(savedPolicyKey, "SomeTemplate", savedStateMap, savedFirstState)));
        assertFalse(policy.equals(new AxPolicy(savedPolicyKey, savedTemplate, stateMapEmpty, savedFirstState)));
        assertFalse(policy.equals(new AxPolicy(savedPolicyKey, savedTemplate, savedStateMap, "SomeFirstState")));
        assertTrue(policy.equals(new AxPolicy(savedPolicyKey, savedTemplate, savedStateMap, savedFirstState)));

        assertEquals(0, policy.compareTo(policy));
        assertEquals(0, policy.compareTo(clonedPolicy));
        assertNotEquals(0, policy.compareTo(new AxArtifactKey()));
        assertNotEquals(0, policy.compareTo(null));
        assertNotEquals(0, policy
                .compareTo(new AxPolicy(AxArtifactKey.getNullKey(), savedTemplate, savedStateMap, savedFirstState)));
        assertNotEquals(0,
                policy.compareTo(new AxPolicy(savedPolicyKey, "SomeTemplate", savedStateMap, savedFirstState)));
        assertNotEquals(0,
                policy.compareTo(new AxPolicy(savedPolicyKey, savedTemplate, stateMapEmpty, savedFirstState)));
        assertNotEquals(0,
                policy.compareTo(new AxPolicy(savedPolicyKey, savedTemplate, savedStateMap, "SomeFirstState")));
        assertEquals(0, policy.compareTo(new AxPolicy(savedPolicyKey, savedTemplate, savedStateMap, savedFirstState)));

        assertNotNull(policy.getKeys());

        final AxPolicies policies = new AxPolicies();
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        // Invalid, no events in event map
        policies.setKey(new AxArtifactKey("PoliciesKey", "0.0.1"));
        assertEquals("PoliciesKey:0.0.1", policies.getKey().getID());

        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policies.getPolicyMap().put(savedPolicyKey, policy);
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policies.getPolicyMap().put(AxArtifactKey.getNullKey(), null);
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policies.getPolicyMap().remove(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policies.getPolicyMap().put(new AxArtifactKey("NullValueKey", "0.0.1"), null);
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policies.getPolicyMap().remove(new AxArtifactKey("NullValueKey", "0.0.1"));
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policies.getPolicyMap().put(new AxArtifactKey("BadEventKey", "0.0.1"), policy);
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policies.getPolicyMap().remove(new AxArtifactKey("BadEventKey", "0.0.1"));
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policies.clean();
        policies.afterUnmarshal(null, null);

        final AxPolicies clonedPolicies = new AxPolicies(policies);
        assertEquals("AxPolicies:(key=AxArtifactKey:(name=PoliciesKey,version=0.0.",
                clonedPolicies.toString().substring(0, 60));

        assertFalse(policies.hashCode() == 0);

        assertTrue(policies.equals(policies));
        assertTrue(policies.equals(clonedPolicies));
        assertFalse(policies.equals(null));
        assertFalse(policies.equals("Hello"));
        assertFalse(policies.equals(new AxPolicies(new AxArtifactKey())));

        assertEquals(0, policies.compareTo(policies));
        assertEquals(0, policies.compareTo(clonedPolicies));
        assertNotEquals(0, policies.compareTo(null));
        assertNotEquals(0, policies.compareTo(new AxArtifactKey()));
        assertNotEquals(0, policies.compareTo(new AxPolicies(new AxArtifactKey())));

        clonedPolicies.get(savedPolicyKey).setTemplate("AnotherTemplate");
        assertNotEquals(0, policies.compareTo(clonedPolicies));

        assertEquals(policies.getKey(), policies.getKeys().get(0));

        assertEquals("policy", policies.get("policy").getKey().getName());
        assertEquals("policy", policies.get("policy", "0.0.1").getKey().getName());
        assertEquals(1, policies.getAll("policy", "0.0.1").size());
        assertEquals(0, policies.getAll("NonExistantPolicy").size());

        AxStateTree stateTree = policy.getStateTree();
        assertNotNull(stateTree);
        assertNotNull(stateTree.getReferencedStateList());
        assertNotNull(stateTree.getReferencedStateSet());

        final AxState secondState = new AxState(policy.getStateMap().get("state"));
        secondState.getKey().setLocalName("SecondState");
        secondState.afterUnmarshal(null, null);
        policy.getStateMap().put("SecondState", secondState);
        policy.getStateMap().get("state").getStateOutputs().get("stateOutput0").setNextState(secondState.getKey());

        stateTree = policy.getStateTree();
        assertNotNull(stateTree);
        assertNotNull(stateTree.getReferencedStateList());
        assertNotNull(stateTree.getReferencedStateSet());
        assertNotNull(stateTree.getNextStates());

        policy.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0")
                .setNextState(policy.getStateMap().get("state").getKey());
        try {
            policy.getStateTree();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "loop detected in state tree for policy policy:0.0.1 state SecondState, next state state referenced more than once",
                    e.getMessage());
        }

        policy.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0")
                .setNextState(AxReferenceKey.getNullKey());

        final AxState thirdState = new AxState(policy.getStateMap().get("state"));
        thirdState.getKey().setLocalName("ThirdState");
        thirdState.afterUnmarshal(null, null);
        policy.getStateMap().put("ThirdState", thirdState);
        policy.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0").setNextState(thirdState.getKey());
        policy.getStateMap().get("ThirdState").getStateOutputs().get("stateOutput0")
                .setNextState(AxReferenceKey.getNullKey());

        stateTree = policy.getStateTree();

        final AxStateOutput ssS0Clone =
                new AxStateOutput(policy.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0"));
        ssS0Clone.getKey().setLocalName("ssS0Clone");
        policy.getStateMap().get("SecondState").getStateOutputs().put("ssS0Clone", ssS0Clone);

        try {
            policy.getStateTree();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "loop detected in state tree for policy policy:0.0.1 state SecondState, next state ThirdState referenced more than once",
                    e.getMessage());
        }

        policy.getStateMap().get("SecondState").getStateOutputs().remove("ssS0Clone");

        policy.getStateMap().get("state").getStateOutputs().get("stateOutput0").setNextState(secondState.getKey());
        secondState.getStateOutputs().get("stateOutput0").setNextState(thirdState.getKey());
        thirdState.getStateOutputs().get("stateOutput0").setNextState(AxReferenceKey.getNullKey());

        stateTree = policy.getStateTree();
        assertNotNull(stateTree.getState());

        thirdState.getStateOutputs().get("stateOutput0").setNextState(secondState.getKey());

        try {
            policy.getStateTree();
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals(
                    "loop detected in state tree for policy policy:0.0.1 state ThirdState, next state SecondState referenced more than once",
                    e.getMessage());
        }

        thirdState.getStateOutputs().get("stateOutput0").setNextState(AxReferenceKey.getNullKey());

        stateTree = policy.getStateTree();

        final AxStateTree otherStateTree = policy.getStateTree();
        assertEquals(0, stateTree.compareTo(otherStateTree));

        for (final AxStateTree childStateTree : stateTree.getNextStates()) {
            assertNotEquals(0, stateTree.compareTo(childStateTree));
        }

        otherStateTree.getNextStates().clear();
        assertNotEquals(0, stateTree.compareTo(otherStateTree));
    }
}
