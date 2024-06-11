/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2022, 2024 Nordix Foundation.
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.policymodel.handling.SupportApexPolicyModelCreator;

/**
 * Test apex policies.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class PoliciesTest {

    @Test
    void testPolicies() {
        final TreeMap<String, AxState> stateMap = new TreeMap<>();
        final TreeMap<String, AxState> stateMapEmpty = new TreeMap<>();

        assertNotNull(new AxPolicy());
        assertNotNull(new AxPolicy(new AxArtifactKey()));
        assertNotNull(new AxPolicy(new AxArtifactKey(), "PolicyTemplate", stateMapEmpty, "FirstState"));

        final AxPolicy policy = new AxPolicy();

        final AxArtifactKey policyKey = new AxArtifactKey("PolicyName", "0.0.1");

        final AxState firstState = new AxState(new AxReferenceKey(policy.getKey(), "FirstState"));
        final AxState badState = new AxState(new AxReferenceKey(policy.getKey(), "BadState"));
        final AxStateOutput badStateOutput = new AxStateOutput(badState.getKey(), AxArtifactKey.getNullKey(),
                        new AxReferenceKey(policyKey, "BadNextState"));
        badState.getStateOutputs().put(badStateOutput.getKey().getLocalName(), badStateOutput);
        stateMap.put(firstState.getKey().getLocalName(), firstState);

        assertThatThrownBy(() -> policy.setKey(null))
            .hasMessage("key may not be null");
        policy.setKey(policyKey);
        assertEquals("PolicyName:0.0.1", policy.getKey().getId());
        assertEquals("PolicyName:0.0.1", policy.getKeys().get(0).getId());

        assertThatThrownBy(() -> policy.setTemplate(null))
            .hasMessage("template may not be null");
        policy.setTemplate("PolicyTemplate");
        assertEquals("PolicyTemplate", policy.getTemplate());

        assertThatThrownBy(() -> policy.setStateMap(null))
            .hasMessage("stateMap may not be null");
        policy.setStateMap(stateMap);
        assertEquals(stateMap, policy.getStateMap());

        assertThatThrownBy(() -> policy.setFirstState(null))
            .hasMessage("firstState may not be null");
        policy.setFirstState("FirstState");
        assertEquals("FirstState", policy.getFirstState());

        assertEquals("PolicyName:0.0.1", policy.getKeys().get(0).getId());

        final AxPolicy policyPN = new SupportApexPolicyModelCreator().getModel().getPolicies().get("policy");

        AxValidationResult result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxArtifactKey savedPolicyKey = policyPN.getKey();
        policyPN.setKey(AxArtifactKey.getNullKey());
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policyPN.setKey(savedPolicyKey);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final String savedTemplate = policyPN.getTemplate();
        policyPN.setTemplate("");
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.OBSERVATION, result.getValidationResult());

        policyPN.setTemplate(savedTemplate);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final Map<String, AxState> savedStateMap = policyPN.getStateMap();

        policyPN.setStateMap(stateMapEmpty);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policyPN.setStateMap(savedStateMap);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put(AxKey.NULL_KEY_NAME, firstState);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove(AxKey.NULL_KEY_NAME);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put("NullState", null);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove("NullState");
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put("BadStateKey", firstState);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove("BadStateKey");
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        savedStateMap.put(badState.getKey().getLocalName(), badState);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        savedStateMap.remove(badState.getKey().getLocalName());
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final String savedFirstState = policyPN.getFirstState();

        policyPN.setFirstState("");
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policyPN.setFirstState(savedFirstState);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policyPN.setFirstState("NonExistantFirstState");
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policyPN.setFirstState(savedFirstState);
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        final AxState clonedState = new AxState(policyPN.getStateMap().get("state"));
        clonedState.getKey().setLocalName("ClonedState");

        savedStateMap.put(clonedState.getKey().getLocalName(), clonedState);
        policyPN.buildReferences();
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.WARNING, result.getValidationResult());

        savedStateMap.remove(clonedState.getKey().getLocalName());
        result = new AxValidationResult();
        result = policyPN.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policyPN.clean();

        final AxPolicy clonedPolicy = new AxPolicy(policyPN);
        assertEquals("AxPolicy:(key=AxArtifactKey:(name=policy,version=0.0.1),template=FREEFORM,sta",
                        clonedPolicy.toString().substring(0, 77));

        assertNotEquals(0, policyPN.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(policyPN, policyPN); // NOSONAR
        assertEquals(policyPN, clonedPolicy);
        assertNotNull(policyPN);

        Object helloObj = "Hello";
        assertNotEquals(policyPN, helloObj);
        assertNotEquals(policyPN,
                        new AxPolicy(AxArtifactKey.getNullKey(), savedTemplate, savedStateMap, savedFirstState));
        assertNotEquals(policyPN, new AxPolicy(savedPolicyKey, "SomeTemplate", savedStateMap, savedFirstState));
        assertNotEquals(policyPN, new AxPolicy(savedPolicyKey, savedTemplate, stateMapEmpty, savedFirstState));
        assertNotEquals(policyPN, new AxPolicy(savedPolicyKey, savedTemplate, savedStateMap, "SomeFirstState"));
        assertEquals(policyPN, new AxPolicy(savedPolicyKey, savedTemplate, savedStateMap, savedFirstState));

        assertEquals(0, policyPN.compareTo(policyPN));
        assertEquals(0, policyPN.compareTo(clonedPolicy));
        assertNotEquals(0, policyPN.compareTo(new AxArtifactKey()));
        assertNotEquals(0, policyPN.compareTo(null));
        assertNotEquals(0, policyPN.compareTo(
                        new AxPolicy(AxArtifactKey.getNullKey(), savedTemplate, savedStateMap, savedFirstState)));
        assertNotEquals(0, policyPN.compareTo(new AxPolicy(savedPolicyKey,
                "SomeTemplate", savedStateMap, savedFirstState)));
        assertNotEquals(0, policyPN.compareTo(new AxPolicy(savedPolicyKey, savedTemplate,
                stateMapEmpty, savedFirstState)));
        assertNotEquals(0, policyPN.compareTo(new AxPolicy(savedPolicyKey, savedTemplate,
                savedStateMap, "SomeFirstState")));
        assertEquals(0, policyPN.compareTo(new AxPolicy(savedPolicyKey, savedTemplate,
                savedStateMap, savedFirstState)));

        assertNotNull(policyPN.getKeys());

        final AxPolicies policies = new AxPolicies();
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        // Invalid, no events in event map
        policies.setKey(new AxArtifactKey("PoliciesKey", "0.0.1"));
        assertEquals("PoliciesKey:0.0.1", policies.getKey().getId());

        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policies.getPolicyMap().put(savedPolicyKey, policyPN);
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

        policies.getPolicyMap().put(new AxArtifactKey("BadEventKey", "0.0.1"), policyPN);
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.INVALID, result.getValidationResult());

        policies.getPolicyMap().remove(new AxArtifactKey("BadEventKey", "0.0.1"));
        result = new AxValidationResult();
        result = policies.validate(result);
        assertEquals(ValidationResult.VALID, result.getValidationResult());

        policies.clean();

        final AxPolicies clonedPolicies = new AxPolicies(policies);
        assertEquals("AxPolicies:(key=AxArtifactKey:(name=PoliciesKey,version=0.0.",
                        clonedPolicies.toString().substring(0, 60));

        assertNotEquals(0, policies.hashCode());
        // disabling sonar because this code tests the equals() method
        assertEquals(policies, policies); // NOSONAR
        assertEquals(policies, clonedPolicies);
        assertNotNull(policies);
        assertNotEquals(policyPN, helloObj);
        assertNotEquals(policies, new AxPolicies(new AxArtifactKey()));

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

        final AxState secondState = new AxState(policyPN.getStateMap().get("state"));
        secondState.getKey().setLocalName("SecondState");
        policyPN.getStateMap().put("SecondState", secondState);
        policyPN.getStateMap().get("state").getStateOutputs().get("stateOutput0").setNextState(secondState.getKey());

        stateTree = policyPN.getStateTree();
        assertNotNull(stateTree);
        assertNotNull(stateTree.getReferencedStateList());
        assertNotNull(stateTree.getReferencedStateSet());
        assertNotNull(stateTree.getNextStates());

        policyPN.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0")
                        .setNextState(policyPN.getStateMap().get("state").getKey());
        assertThatThrownBy(() -> policyPN.getStateTree())
            .hasMessageContaining("loop detected in state tree for policy policy:0.0.1 state SecondState, "
                + "next state state referenced more than once");
        policyPN.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0")
                        .setNextState(AxReferenceKey.getNullKey());

        final AxState thirdState = new AxState(policyPN.getStateMap().get("state"));
        thirdState.getKey().setLocalName("ThirdState");
        policyPN.getStateMap().put("ThirdState", thirdState);
        policyPN.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0")
                        .setNextState(thirdState.getKey());
        policyPN.getStateMap().get("ThirdState").getStateOutputs().get("stateOutput0")
                        .setNextState(AxReferenceKey.getNullKey());

        stateTree = policyPN.getStateTree();

        final AxStateOutput ssS0Clone = new AxStateOutput(
                        policyPN.getStateMap().get("SecondState").getStateOutputs().get("stateOutput0"));
        ssS0Clone.getKey().setLocalName("ssS0Clone");
        policyPN.getStateMap().get("SecondState").getStateOutputs().put("ssS0Clone", ssS0Clone);

        assertThatThrownBy(() -> policyPN.getStateTree())
            .hasMessageContaining("loop detected in state tree for policy policy:0.0.1 state SecondState, "
                + "next state ThirdState referenced more than once");
        policyPN.getStateMap().get("SecondState").getStateOutputs().remove("ssS0Clone");

        policyPN.getStateMap().get("state").getStateOutputs().get("stateOutput0").setNextState(secondState.getKey());
        secondState.getStateOutputs().get("stateOutput0").setNextState(thirdState.getKey());
        thirdState.getStateOutputs().get("stateOutput0").setNextState(AxReferenceKey.getNullKey());

        stateTree = policyPN.getStateTree();
        assertNotNull(stateTree.getState());

        thirdState.getStateOutputs().get("stateOutput0").setNextState(secondState.getKey());

        assertThatThrownBy(() -> policyPN.getStateTree())
            .hasMessageContaining("loop detected in state tree for policy policy:0.0.1 state ThirdState, "
                            + "next state SecondState referenced more than once");
        thirdState.getStateOutputs().get("stateOutput0").setNextState(AxReferenceKey.getNullKey());

        stateTree = policyPN.getStateTree();

        final AxStateTree otherStateTree = policyPN.getStateTree();
        assertEquals(0, stateTree.compareTo(otherStateTree));

        for (final AxStateTree childStateTree : stateTree.getNextStates()) {
            assertNotEquals(0, stateTree.compareTo(childStateTree));
        }

        otherStateTree.getNextStates().clear();
        assertNotEquals(0, stateTree.compareTo(otherStateTree));
    }
}
