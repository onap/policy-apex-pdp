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

package org.onap.policy.apex.model.modelapi.impl;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.modelapi.ApexAPIResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class acts as a facade for operations towards a policy model for policy operations.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyFacade {
    private static final String STATE_NAME_MAY_NOT_BE_NULL = "stateName may not be null";
    private static final String DOES_NOT_EXIST_ON_STATE = " does not exist on state ";
    private static final String STATE_FINALIZER_LOGIC = "state finalizer logic ";
    private static final String DO_ES_NOT_EXIST = " do(es) not exist";
    private static final String CONCEPT_S = "concept(s) ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String CONCEPT = "concept ";
    private static final String ALREADY_EXISTS = " already exists";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    // Facade classes for working towards the real Apex model
    private final KeyInformationFacade keyInformationFacade;

    // JSON output on list/delete if set
    private final boolean jsonMode;

    /**
     * Constructor that creates a policy facade for the Apex Model API.
     *
     * @param apexModel the apex model
     * @param apexProperties Properties for the model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     */
    public PolicyFacade(final ApexModel apexModel, final Properties apexProperties, final boolean jsonMode) {
        this.apexModel = apexModel;
        this.apexProperties = apexProperties;
        this.jsonMode = jsonMode;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties, jsonMode);
    }

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
    public ApexAPIResult createPolicy(final String name, final String version, final String template,
            final String firstState, final String uuid, final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            String t = template;
            if (t == null) {
                t = apexProperties.getProperty("DEFAULT_POLICY_TEMPLATE");
            }

            if (apexModel.getPolicyModel().getPolicies().getPolicyMap().containsKey(key)) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS, CONCEPT + key.getID() + ALREADY_EXISTS);
            }

            final AxPolicy policy = new AxPolicy(key);
            policy.setTemplate(t);
            policy.setFirstState(firstState);

            apexModel.getPolicyModel().getPolicies().getPolicyMap().put(key, policy);

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
            } else {
                return keyInformationFacade.createKeyInformation(name, version, uuid, description);
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult updatePolicy(final String name, final String version, final String template,
            final String firstState, final String uuid, final String description) {
        try {
            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (template != null) {
                policy.setTemplate(template);
            }
            if (firstState != null) {
                policy.setFirstState(firstState);
            }

            return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * List policies.
     *
     * @param name name of the policy, set to null to list all
     * @param version starting version of the policy, set to null to list all versions
     * @return result of the operation
     */
    public ApexAPIResult listPolicy(final String name, final String version) {
        try {
            final Set<AxPolicy> policySet = apexModel.getPolicyModel().getPolicies().getAll(name, version);
            if (name != null && policySet.isEmpty()) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            for (final AxPolicy policy : policySet) {
                result.addMessage(
                        new ApexModelStringWriter<AxPolicy>(false).writeString(policy, AxPolicy.class, jsonMode));
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Delete a policy.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @return result of the operation
     */
    public ApexAPIResult deletePolicy(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                final AxPolicy removedPolicy = apexModel.getPolicyModel().getPolicies().getPolicyMap().remove(key);
                if (removedPolicy != null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS, new ApexModelStringWriter<AxPolicy>(false)
                            .writeString(removedPolicy, AxPolicy.class, jsonMode));
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + key.getID() + DOES_NOT_EXIST);
                }
            }

            final Set<AxPolicy> policySet = apexModel.getPolicyModel().getPolicies().getAll(name, version);
            if (policySet.isEmpty()) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            for (final AxPolicy policy : policySet) {
                result.addMessage(
                        new ApexModelStringWriter<AxPolicy>(false).writeString(policy, AxPolicy.class, jsonMode));
                apexModel.getPolicyModel().getPolicies().getPolicyMap().remove(policy.getKey());
                keyInformationFacade.deleteKeyInformation(name, version);
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Validate policies.
     *
     * @param name name of the policy, set to null to list all
     * @param version starting version of the policy, set to null to list all versions
     * @return result of the operation
     */
    public ApexAPIResult validatePolicy(final String name, final String version) {
        try {
            final Set<AxPolicy> policySet = apexModel.getPolicyModel().getPolicies().getAll(name, version);
            if (policySet.isEmpty()) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            for (final AxPolicy policy : policySet) {
                final AxValidationResult validationResult = policy.validate(new AxValidationResult());
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(policy.getKey(),
                        AxArtifactKey.class, jsonMode));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult createPolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(policy.getKey(), stateName);

            if (policy.getStateMap().containsKey(refKey.getLocalName())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                        CONCEPT + refKey.getID() + ALREADY_EXISTS);
            }

            final AxEvent triggerEvent = apexModel.getPolicyModel().getEvents().get(triggerName, triggerVersion);
            if (triggerEvent == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + triggerName + ':' + triggerVersion + DOES_NOT_EXIST);
            }

            final AxTask defaultTask = apexModel.getPolicyModel().getTasks().get(defaultTaskName, defaltTaskVersion);
            if (defaultTask == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + defaultTaskName + ':' + defaltTaskVersion + DOES_NOT_EXIST);
            }

            final AxState state = new AxState(refKey);
            state.setTrigger(triggerEvent.getKey());
            state.setDefaultTask(defaultTask.getKey());

            policy.getStateMap().put(state.getKey().getLocalName(), state);
            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult updatePolicyState(final String name, final String version, final String stateName,
            final String triggerName, final String triggerVersion, final String defaultTaskName,
            final String defaltTaskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (triggerName != null) {
                final AxEvent triggerEvent = apexModel.getPolicyModel().getEvents().get(triggerName, triggerVersion);
                if (triggerEvent == null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + triggerName + ':' + triggerVersion + DOES_NOT_EXIST);
                }
                state.setTrigger(triggerEvent.getKey());
            }

            if (defaultTaskName != null) {
                final AxTask defaultTask =
                        apexModel.getPolicyModel().getTasks().get(defaultTaskName, defaltTaskVersion);
                if (defaultTask == null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + defaultTaskName + ':' + defaltTaskVersion + DOES_NOT_EXIST);
                }
                state.setDefaultTask(defaultTask.getKey());
            }

            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * List policy states.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state, set to null to list all states of the policy
     * @return result of the operation
     */
    public ApexAPIResult listPolicyState(final String name, final String version, final String stateName) {
        try {
            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (stateName != null) {
                final AxState state = policy.getStateMap().get(stateName);
                if (state != null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS,
                            new ApexModelStringWriter<AxState>(false).writeString(state, AxState.class, jsonMode));
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + state + DOES_NOT_EXIST);
                }
            } else {
                if (policy.getStateMap().size() == 0) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "no states defined on policy " + policy.getKey().getID());
                }
                final ApexAPIResult result = new ApexAPIResult();
                for (final AxState state : policy.getStateMap().values()) {
                    result.addMessage(
                            new ApexModelStringWriter<AxState>(false).writeString(state, AxState.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Delete a policy state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state, set to null to delete all states
     * @return result of the operation
     */
    public ApexAPIResult deletePolicyState(final String name, final String version, final String stateName) {
        try {
            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            if (stateName != null) {
                if (policy.getStateMap().containsKey(stateName)) {
                    result.addMessage(new ApexModelStringWriter<AxState>(false)
                            .writeString(policy.getStateMap().get(stateName), AxState.class, jsonMode));
                    policy.getStateMap().remove(stateName);
                    return result;
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + stateName + DOES_NOT_EXIST);
                }
            } else {
                if (policy.getStateMap().size() == 0) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "no states defined on policy " + policy.getKey().getID());
                }
                for (final AxState state : policy.getStateMap().values()) {
                    result.addMessage(
                            new ApexModelStringWriter<AxState>(false).writeString(state, AxState.class, jsonMode));
                }
                policy.getStateMap().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult createPolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName, final String logicFlavour, final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            // There is only one logic item associated with a state so we use a hard coded logic
            // name
            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), "TaskSelectionLogic");

            if (!state.getTaskSelectionLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                        CONCEPT + refKey.getID() + ALREADY_EXISTS);
            }

            state.setTaskSelectionLogic(new AxTaskSelectionLogic(refKey, logicFlavour, logic));
            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult updatePolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName, final String logicFlavour, final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (state.getTaskSelectionLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + state.getTaskSelectionLogic().getKey().getID() + DOES_NOT_EXIST);
            }

            final AxTaskSelectionLogic taskSelectionLogic = state.getTaskSelectionLogic();
            if (logicFlavour != null) {
                taskSelectionLogic.setLogicFlavour(logicFlavour);
            }
            if (logic != null) {
                taskSelectionLogic.setLogic(logic);
            }

            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * List task selection logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @return result of the operation
     */
    public ApexAPIResult listPolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            return new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS,
                    new ApexModelStringWriter<AxTaskSelectionLogic>(false).writeString(state.getTaskSelectionLogic(),
                            AxTaskSelectionLogic.class, jsonMode));
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Delete task selection logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @return result of the operation
     */
    public ApexAPIResult deletePolicyStateTaskSelectionLogic(final String name, final String version,
            final String stateName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (state.getTaskSelectionLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + state.getTaskSelectionLogic().getKey().getID() + DOES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            result.addMessage(new ApexModelStringWriter<AxTaskSelectionLogic>(false)
                    .writeString(state.getTaskSelectionLogic(), AxTaskSelectionLogic.class, jsonMode));
            state.setTaskSelectionLogic(new AxTaskSelectionLogic());
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult createPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName, final String eventName, final String eventVersion, final String nextState) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(outputName, "outputName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        "Policy concept " + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        "State concept " + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), outputName);
            if (state.getStateOutputs().containsKey(refKey.getLocalName())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                        "Output concept " + refKey.getID() + ALREADY_EXISTS);
            }

            final AxEvent event = apexModel.getPolicyModel().getEvents().get(eventName, eventVersion);
            if (event == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        "Event concept " + eventName + ':' + eventVersion + DOES_NOT_EXIST);
            }

            AxReferenceKey nextStateKey = AxReferenceKey.getNullKey();
            if (nextState != null && !(AxReferenceKey.getNullKey().getLocalName().equals(nextState))) {
                if (state.getKey().getLocalName().equals(nextState)) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.FAILED,
                            "next state " + nextState + " of a state cannot be the state itself");
                }
                nextStateKey = new AxReferenceKey(state.getKey().getParentArtifactKey(), nextState);

                if (!policy.getStateMap().containsKey(nextState)) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "Next state concept " + nextStateKey.getID() + DOES_NOT_EXIST);
                }
            }

            state.getStateOutputs().put(refKey.getLocalName(), new AxStateOutput(refKey, event.getKey(), nextStateKey));
            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * List policy state outputs.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param outputName of the state output, set to null to list all outputs of the state
     * @return result of the operation
     */
    public ApexAPIResult listPolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (outputName != null) {
                final AxStateOutput stateOutput = state.getStateOutputs().get(outputName);
                if (stateOutput != null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS,
                            new ApexModelStringWriter<AxStateOutput>(false).writeString(stateOutput,
                                    AxStateOutput.class, jsonMode));
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + state.getKey().getID() + ':' + outputName + DOES_NOT_EXIST);
                }
            } else {
                if (state.getStateOutputs().size() == 0) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "no state output concepts exist for state " + state.getKey().getID());
                }

                final ApexAPIResult result = new ApexAPIResult();

                for (final AxStateOutput stateOutput : state.getStateOutputs().values()) {
                    result.addMessage(new ApexModelStringWriter<AxStateOutput>(false).writeString(stateOutput,
                            AxStateOutput.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Delete a policy state output.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param outputName of the state output, set to null to delete all state outputs
     * @return result of the operation
     */
    public ApexAPIResult deletePolicyStateOutput(final String name, final String version, final String stateName,
            final String outputName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (outputName != null) {
                final AxStateOutput stateOutput = state.getStateOutputs().get(outputName);
                if (stateOutput != null) {
                    final ApexAPIResult result = new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS,
                            new ApexModelStringWriter<AxStateOutput>(false).writeString(stateOutput,
                                    AxStateOutput.class, jsonMode));
                    state.getStateOutputs().remove(outputName);
                    return result;
                } else {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + state.getKey().getID() + ':' + outputName + DOES_NOT_EXIST);
                }
            } else {
                if (state.getStateOutputs().size() == 0) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "no state output concepts exist for state " + state.getKey().getID());
                }

                final ApexAPIResult result = new ApexAPIResult();

                for (final Entry<String, AxStateOutput> stateOutputEntry : state.getStateOutputs().entrySet()) {
                    result.addMessage(new ApexModelStringWriter<AxStateOutput>(false)
                            .writeString(stateOutputEntry.getValue(), AxStateOutput.class, jsonMode));
                }
                state.getStateOutputs().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult createPolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName, final String logicFlavour, final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(finalizerLogicName, "finalizerlogicName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);

            if (state.getStateFinalizerLogicMap().containsKey(refKey.getLocalName())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS,
                        CONCEPT + refKey.getID() + ALREADY_EXISTS);
            }

            state.getStateFinalizerLogicMap().put(finalizerLogicName,
                    new AxStateFinalizerLogic(refKey, logicFlavour, logic));
            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult updatePolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName, final String logicFlavour, final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(finalizerLogicName, "finalizerLogicName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);
            final AxStateFinalizerLogic stateFinalizerLogic =
                    state.getStateFinalizerLogicMap().get(refKey.getKey().getLocalName());
            if (stateFinalizerLogic == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        STATE_FINALIZER_LOGIC + refKey.getID() + DOES_NOT_EXIST);
            }

            if (logicFlavour != null) {
                stateFinalizerLogic.setLogicFlavour(logicFlavour);
            }
            if (logic != null) {
                stateFinalizerLogic.setLogic(logic);
            }

            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * List policy finalizer logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @return result of the operation
     */
    public ApexAPIResult listPolicyStateFinalizerLogic(final String name, final String version, final String stateName,
            final String finalizerLogicName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (finalizerLogicName != null) {
                final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);
                final AxStateFinalizerLogic stateFinalizerLogic =
                        state.getStateFinalizerLogicMap().get(refKey.getKey().getLocalName());
                if (stateFinalizerLogic == null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            STATE_FINALIZER_LOGIC + refKey.getID() + DOES_NOT_EXIST);
                }

                return new ApexAPIResult(ApexAPIResult.RESULT.SUCCESS,
                        new ApexModelStringWriter<AxStateFinalizerLogic>(false).writeString(stateFinalizerLogic,
                                AxStateFinalizerLogic.class, jsonMode));
            } else {
                if (state.getStateFinalizerLogicMap().size() == 0) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "no state finalizer logic defined on state " + state.getKey().getID());
                }
                final ApexAPIResult result = new ApexAPIResult();
                for (final AxStateFinalizerLogic stateFinalizerLogic : state.getStateFinalizerLogicMap().values()) {
                    result.addMessage(new ApexModelStringWriter<AxStateFinalizerLogic>(false)
                            .writeString(stateFinalizerLogic, AxStateFinalizerLogic.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

    /**
     * Delete policy finalizer logic for a state.
     *
     * @param name name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @return result of the operation
     */
    public ApexAPIResult deletePolicyStateFinalizerLogic(final String name, final String version,
            final String stateName, final String finalizerLogicName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (finalizerLogicName != null) {
                final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);
                final AxStateFinalizerLogic stateFinalizerLogic =
                        state.getStateFinalizerLogicMap().get(refKey.getKey().getLocalName());
                if (stateFinalizerLogic == null) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            STATE_FINALIZER_LOGIC + refKey.getID() + DOES_NOT_EXIST);
                }

                final ApexAPIResult result = new ApexAPIResult();
                result.addMessage(new ApexModelStringWriter<AxStateFinalizerLogic>(false)
                        .writeString(stateFinalizerLogic, AxStateFinalizerLogic.class, jsonMode));
                state.getStateFinalizerLogicMap().remove(refKey.getLocalName());
                return result;
            } else {
                if (state.getStateFinalizerLogicMap().size() == 0) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "no state finalizer logic defined on state " + state.getKey().getID());
                }

                final ApexAPIResult result = new ApexAPIResult();
                for (final AxStateFinalizerLogic stateFinalizerLogic : state.getStateFinalizerLogicMap().values()) {
                    result.addMessage(new ApexModelStringWriter<AxStateFinalizerLogic>(false)
                            .writeString(stateFinalizerLogic, AxStateFinalizerLogic.class, jsonMode));
                }
                state.getStateFinalizerLogicMap().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult createPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskLocalName, final String taskName, final String taskVersion, final String outputType,
            final String outputName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(outputName, "outputName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxTask task = apexModel.getPolicyModel().getTasks().get(taskName, taskVersion);
            if (task == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + taskName + ':' + taskVersion + DOES_NOT_EXIST);
            }

            if (state.getTaskReferences().containsKey(task.getKey())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS, "task " + task.getKey().getID()
                        + " already has reference with output " + state.getTaskReferences().get(task.getKey()));
            }

            AxReferenceKey refKey;
            if (taskLocalName == null) {
                refKey = new AxReferenceKey(state.getKey(), state.getKey().getParentKeyName());
            } else {
                refKey = new AxReferenceKey(state.getKey(), taskLocalName);
            }

            // The reference to the output we're using here
            final AxReferenceKey outputRefKey = new AxReferenceKey(state.getKey(), outputName);

            final AxStateTaskOutputType stateTaskOutputType = AxStateTaskOutputType.valueOf(outputType);
            if (stateTaskOutputType.equals(AxStateTaskOutputType.DIRECT)) {
                if (!state.getStateOutputs().containsKey(outputRefKey.getLocalName())) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "state output concept " + outputRefKey.getID() + DOES_NOT_EXIST);
                }
            } else if (stateTaskOutputType.equals(AxStateTaskOutputType.LOGIC)) {
                if (!state.getStateFinalizerLogicMap().containsKey(outputRefKey.getLocalName())) {
                    return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                            "state finalizer logic concept " + outputRefKey.getID() + DOES_NOT_EXIST);
                }
            } else {
                return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, "output type " + outputType + " invalid");
            }

            state.getTaskReferences().put(task.getKey(),
                    new AxStateTaskReference(refKey, stateTaskOutputType, outputRefKey));
            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }
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
    public ApexAPIResult listPolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            boolean found = false;
            for (final Entry<AxArtifactKey, AxStateTaskReference> taskReferenceEntry : state.getTaskReferences()
                    .entrySet()) {
                if (taskName == null) {
                    result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false)
                            .writeString(taskReferenceEntry.getKey(), AxArtifactKey.class, jsonMode));
                    result.addMessage(new ApexModelStringWriter<AxStateTaskReference>(false)
                            .writeString(taskReferenceEntry.getValue(), AxStateTaskReference.class, jsonMode));
                    found = true;
                    continue;
                }
                if (!taskReferenceEntry.getKey().getName().equals(taskName)) {
                    continue;
                }

                if (taskVersion != null && !taskReferenceEntry.getKey().getVersion().equals(taskVersion)) {
                    continue;
                }

                found = true;
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false)
                        .writeString(taskReferenceEntry.getKey(), AxArtifactKey.class, jsonMode));
                result.addMessage(new ApexModelStringWriter<AxStateTaskReference>(false)
                        .writeString(taskReferenceEntry.getValue(), AxStateTaskReference.class, jsonMode));
            }
            if (found) {
                return result;
            } else {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        "no task references found for state " + state.getKey().getID());
            }
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult deletePolicyStateTaskRef(final String name, final String version, final String stateName,
            final String taskName, final String taskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final Set<AxArtifactKey> deleteSet = new TreeSet<>();

            for (final AxArtifactKey taskReferenceKey : state.getTaskReferences().keySet()) {
                if (taskName == null) {
                    deleteSet.add(taskReferenceKey);
                    continue;
                }
                if (!taskReferenceKey.getName().equals(taskName)) {
                    continue;
                }

                if (taskVersion != null && !taskReferenceKey.getVersion().equals(taskVersion)) {
                    continue;
                }
                deleteSet.add(taskReferenceKey);
            }
            if (deleteSet.isEmpty()) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + taskName + ':' + taskVersion + DOES_NOT_EXIST_ON_STATE + state.getKey().getID());
            }

            final ApexAPIResult result = new ApexAPIResult();
            for (final AxArtifactKey keyToDelete : deleteSet) {
                state.getTaskReferences().remove(keyToDelete);
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(keyToDelete,
                        AxArtifactKey.class, jsonMode));
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult createPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxContextAlbum contextAlbum =
                    apexModel.getPolicyModel().getAlbums().get(contextAlbumName, contextAlbumVersion);
            if (contextAlbum == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextAlbumName + ':' + contextAlbumVersion + DOES_NOT_EXIST);
            }

            if (state.getContextAlbumReferences().contains(contextAlbum.getKey())) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_EXISTS, "concept album reference for concept "
                        + contextAlbum.getKey().getID() + " already exists in state");
            }

            state.getContextAlbumReferences().add(contextAlbum.getKey());
            return new ApexAPIResult();
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult listPolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final ApexAPIResult result = new ApexAPIResult();
            boolean found = false;
            for (final AxArtifactKey albumKey : state.getContextAlbumReferences()) {
                if (contextAlbumName == null) {
                    result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(albumKey,
                            AxArtifactKey.class, jsonMode));
                    found = true;
                    continue;
                }

                if (!albumKey.getName().equals(contextAlbumName)) {
                    continue;
                }

                if (contextAlbumVersion == null || albumKey.getVersion().equals(contextAlbumVersion)) {
                    result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(albumKey,
                            AxArtifactKey.class, jsonMode));
                    found = true;
                }
            }
            if (!found) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST, CONCEPT + contextAlbumName + ':'
                        + contextAlbumVersion + DOES_NOT_EXIST_ON_STATE + state.getKey().getID());
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }

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
    public ApexAPIResult deletePolicyStateContextRef(final String name, final String version, final String stateName,
            final String contextAlbumName, final String contextAlbumVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + policy.getKey().getID() + ':' + stateName + DOES_NOT_EXIST);
            }

            final Set<AxArtifactKey> deleteSet = new TreeSet<>();

            for (final AxArtifactKey albumKey : state.getContextAlbumReferences()) {
                if (contextAlbumName == null) {
                    deleteSet.add(albumKey);
                    continue;
                }

                if (!albumKey.getName().equals(contextAlbumName)) {
                    continue;
                }

                if (contextAlbumVersion == null || albumKey.getVersion().equals(contextAlbumVersion)) {
                    deleteSet.add(albumKey);
                }
            }
            if (deleteSet.isEmpty()) {
                return new ApexAPIResult(ApexAPIResult.RESULT.CONCEPT_DOES_NOT_EXIST, CONCEPT + contextAlbumName + ':'
                        + contextAlbumVersion + DOES_NOT_EXIST_ON_STATE + state.getKey().getID());
            }

            final ApexAPIResult result = new ApexAPIResult();
            for (final AxArtifactKey keyToDelete : deleteSet) {
                state.getContextAlbumReferences().remove(keyToDelete);
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(keyToDelete,
                        AxArtifactKey.class, jsonMode));
            }
            return result;
        } catch (final Exception e) {
            return new ApexAPIResult(ApexAPIResult.RESULT.FAILED, e);
        }
    }
}
