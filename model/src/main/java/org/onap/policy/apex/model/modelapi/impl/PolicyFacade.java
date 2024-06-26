/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.model.modelapi.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.common.utils.validation.Assertions;

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

    /**
     * Constructor that creates a policy facade for the Apex Model API.
     *
     * @param apexModel      the apex model
     * @param apexProperties Properties for the model
     */
    public PolicyFacade(final ApexModel apexModel, final Properties apexProperties) {
        this.apexModel = apexModel;
        this.apexProperties = apexProperties;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties);
    }

    /**
     * Create a policy.
     *
     * @param name        name of the policy
     * @param version     version of the policy, set to null to use the default version
     * @param template    template used to create the policy, set to null to use the default template
     * @param firstState  the first state of the policy
     * @param uuid        policy UUID, set to null to generate a UUID
     * @param description policy description, set to null to generate a description
     * @return result of the operation
     */
    public ApexApiResult createPolicy(final String name, final String version, final String template,
                                      final String firstState, final String uuid, final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            String templateString = template;
            if (templateString == null) {
                templateString = apexProperties.getProperty("DEFAULT_POLICY_TEMPLATE");
            }

            if (apexModel.getPolicyModel().getPolicies().getPolicyMap().containsKey(key)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, CONCEPT + key.getId() + ALREADY_EXISTS);
            }

            final AxPolicy policy = new AxPolicy(key);
            policy.setTemplate(templateString);
            policy.setFirstState(firstState);

            apexModel.getPolicyModel().getPolicies().getPolicyMap().put(key, policy);

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
            } else {
                return keyInformationFacade.createKeyInformation(name, version, uuid, description);
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update a policy.
     *
     * @param name        name of the policy
     * @param version     version of the policy, set to null to use the latest version
     * @param template    template used to create the policy, set to null to not update
     * @param firstState  the first state of the policy
     * @param uuid        policy UUID, set to null to not update
     * @param description policy description, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updatePolicy(final String name, final String version, final String template,
                                      final String firstState, final String uuid, final String description) {
        try {
            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
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
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List policies.
     *
     * @param name    name of the policy, set to null to list all
     * @param version starting version of the policy, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult listPolicy(final String name, final String version) {
        try {
            final Set<AxPolicy> policySet = apexModel.getPolicyModel().getPolicies().getAll(name, version);
            if (name != null && policySet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxPolicy policy : policySet) {
                result.addMessage(new ApexModelStringWriter<AxPolicy>(false).writeString(policy, AxPolicy.class));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a policy.
     *
     * @param name    name of the policy
     * @param version version of the policy, set to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult deletePolicy(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                final AxPolicy removedPolicy = apexModel.getPolicyModel().getPolicies().getPolicyMap().remove(key);
                if (removedPolicy != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                        new ApexModelStringWriter<AxPolicy>(false).writeString(removedPolicy, AxPolicy.class));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + key.getId() + DOES_NOT_EXIST);
                }
            }

            final Set<AxPolicy> policySet = apexModel.getPolicyModel().getPolicies().getAll(name, version);
            if (policySet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxPolicy policy : policySet) {
                result.addMessage(new ApexModelStringWriter<AxPolicy>(false).writeString(policy, AxPolicy.class));
                apexModel.getPolicyModel().getPolicies().getPolicyMap().remove(policy.getKey());
                keyInformationFacade.deleteKeyInformation(name, version);
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Validate policies.
     *
     * @param name    name of the policy, set to null to list all
     * @param version starting version of the policy, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult validatePolicy(final String name, final String version) {
        try {
            final Set<AxPolicy> policySet = apexModel.getPolicyModel().getPolicies().getAll(name, version);
            if (policySet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxPolicy policy : policySet) {
                final AxValidationResult validationResult = policy.validate(new AxValidationResult());
                result.addMessage(
                    new ApexModelStringWriter<AxArtifactKey>(false).writeString(policy.getKey(), AxArtifactKey.class));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a policy state.
     *
     * @param name               name of the policy
     * @param version            version of the policy, set to null to use the latest version
     * @param stateName          of the state
     * @param triggerName        name of the trigger event for this state
     * @param triggerVersion     version of the trigger event for this state, set to null to use the
     *                           latest version
     * @param defaultTaskName    the default task name
     * @param defaultTaskVersion the default task version, set to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult createPolicyState(final String name, final String version, final String stateName,
                                           final String triggerName, final String triggerVersion,
                                           final String defaultTaskName,
                                           final String defaultTaskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(policy.getKey(), stateName);

            if (policy.getStateMap().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                    CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            final AxEvent triggerEvent = apexModel.getPolicyModel().getEvents().get(triggerName, triggerVersion);
            if (triggerEvent == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + triggerName + ':' + triggerVersion + DOES_NOT_EXIST);
            }

            final AxTask defaultTask = apexModel.getPolicyModel().getTasks().get(defaultTaskName, defaultTaskVersion);
            if (defaultTask == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + defaultTaskName + ':' + defaultTaskVersion + DOES_NOT_EXIST);
            }

            final AxState state = new AxState(refKey);
            state.setTrigger(triggerEvent.getKey());
            state.setDefaultTask(defaultTask.getKey());

            policy.getStateMap().put(state.getKey().getLocalName(), state);
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update a policy state.
     *
     * @param name               name of the policy
     * @param version            version of the policy, set to null to use the latest version
     * @param stateName          of the state
     * @param triggerName        name of the trigger event for this state, set to null to not update
     * @param triggerVersion     version of the trigger event for this state, set to use latest version
     *                           of trigger event
     * @param defaultTaskName    the default task name, set to null to not update
     * @param defaultTaskVersion the default task version, set to use latest version of default task
     * @return result of the operation
     */
    public ApexApiResult updatePolicyState(final String name, final String version, final String stateName,
                                           final String triggerName, final String triggerVersion,
                                           final String defaultTaskName,
                                           final String defaultTaskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (triggerName != null) {
                final AxEvent triggerEvent = apexModel.getPolicyModel().getEvents().get(triggerName, triggerVersion);
                if (triggerEvent == null) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + triggerName + ':' + triggerVersion + DOES_NOT_EXIST);
                }
                state.setTrigger(triggerEvent.getKey());
            }

            if (defaultTaskName != null) {
                final AxTask defaultTask =
                    apexModel.getPolicyModel().getTasks().get(defaultTaskName, defaultTaskVersion);
                if (defaultTask == null) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + defaultTaskName + ':' + defaultTaskVersion + DOES_NOT_EXIST);
                }
                state.setDefaultTask(defaultTask.getKey());
            }

            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List policy states.
     *
     * @param name      name of the policy
     * @param version   version of the policy, set to null to use the latest version
     * @param stateName of the state, set to null to list all states of the policy
     * @return result of the operation
     */
    public ApexApiResult listPolicyState(final String name, final String version, final String stateName) {
        try {
            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (stateName != null) {
                final AxState state = policy.getStateMap().get(stateName);
                if (state != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                        new ApexModelStringWriter<AxState>(false).writeString(state, AxState.class));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + ':' + state + DOES_NOT_EXIST);
                }
            } else {
                if (policy.getStateMap().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "no states defined on policy " + policy.getKey().getId());
                }
                final ApexApiResult result = new ApexApiResult();
                for (final AxState state : policy.getStateMap().values()) {
                    result.addMessage(new ApexModelStringWriter<AxState>(false).writeString(state, AxState.class));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a policy state.
     *
     * @param name      name of the policy
     * @param version   version of the policy, set to null to use the latest version
     * @param stateName of the state, set to null to delete all states
     * @return result of the operation
     */
    public ApexApiResult deletePolicyState(final String name, final String version, final String stateName) {
        try {
            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            if (stateName != null) {
                if (policy.getStateMap().containsKey(stateName)) {
                    result.addMessage(new ApexModelStringWriter<AxState>(false)
                        .writeString(policy.getStateMap().get(stateName), AxState.class));
                    policy.getStateMap().remove(stateName);
                    return result;
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + ':' + stateName + DOES_NOT_EXIST);
                }
            } else {
                if (policy.getStateMap().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "no states defined on policy " + policy.getKey().getId());
                }
                for (final AxState state : policy.getStateMap().values()) {
                    result.addMessage(new ApexModelStringWriter<AxState>(false).writeString(state, AxState.class));
                }
                policy.getStateMap().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create task selection logic for a state.
     *
     * @param name         name of the policy
     * @param version      version of the policy, set to null to use the latest version
     * @param stateName    of the state
     * @param logicFlavour the task selection logic flavour for the state, set to null to use the
     *                     default task logic flavour
     * @param logic        the source code for the logic of the state
     * @return result of the operation
     */
    public ApexApiResult createPolicyStateTaskSelectionLogic(final String name, final String version,
                                                             final String stateName, final String logicFlavour,
                                                             final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            // There is only one logic item associated with a state, so we use a hard coded logic name
            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), "TaskSelectionLogic");

            if (!state.getTaskSelectionLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                    CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            state.setTaskSelectionLogic(new AxTaskSelectionLogic(refKey, logicFlavour, logic));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update task selection logic for a state.
     *
     * @param name         name of the policy
     * @param version      version of the policy, set to null to use the latest version
     * @param stateName    of the state
     * @param logicFlavour the task selection logic flavour for the state, set to null to not update
     * @param logic        the source code for the logic of the state, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updatePolicyStateTaskSelectionLogic(final String name, final String version,
                                                             final String stateName, final String logicFlavour,
                                                             final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (state.getTaskSelectionLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + state.getTaskSelectionLogic().getKey().getId() + DOES_NOT_EXIST);
            }

            final AxTaskSelectionLogic taskSelectionLogic = state.getTaskSelectionLogic();
            if (logicFlavour != null) {
                taskSelectionLogic.setLogicFlavour(logicFlavour);
            }
            if (logic != null) {
                taskSelectionLogic.setLogic(logic);
            }

            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List task selection logic for a state.
     *
     * @param name      name of the policy
     * @param version   version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @return result of the operation
     */
    public ApexApiResult listPolicyStateTaskSelectionLogic(final String name, final String version,
                                                           final String stateName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                new ApexModelStringWriter<AxTaskSelectionLogic>(false).writeString(state.getTaskSelectionLogic(),
                    AxTaskSelectionLogic.class));
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete task selection logic for a state.
     *
     * @param name      name of the policy
     * @param version   version of the policy, set to null to use the latest version
     * @param stateName of the state
     * @return result of the operation
     */
    public ApexApiResult deletePolicyStateTaskSelectionLogic(final String name, final String version,
                                                             final String stateName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (state.getTaskSelectionLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + state.getTaskSelectionLogic().getKey().getId() + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            result.addMessage(new ApexModelStringWriter<AxTaskSelectionLogic>(false)
                .writeString(state.getTaskSelectionLogic(), AxTaskSelectionLogic.class));
            state.setTaskSelectionLogic(new AxTaskSelectionLogic());
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a policy state output.
     *
     * @param name         name of the policy
     * @param version      version of the policy, set to null to use the latest version
     * @param stateName    of the state
     * @param outputName   of the state output
     * @param eventName    name of the output event for this state output
     * @param eventVersion version of the output event for this state output, set to null to use the
     *                     latest version
     * @param nextState    for this state to transition to, set to null if this is the last state that
     *                     the policy transitions to on this branch
     * @return result of the operation
     */
    public ApexApiResult createPolicyStateOutput(final String name, final String version, final String stateName,
                                                 final String outputName, final String eventName,
                                                 final String eventVersion, final String nextState) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(outputName, "outputName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    "Policy concept " + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    "State concept " + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), outputName);
            // There can be multiple state outputs only when the current state is the final state
            if (nextState != null && !AxReferenceKey.getNullKey().getLocalName().equals(nextState)
                && state.getStateOutputs().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                    "Output concept " + refKey.getId() + ALREADY_EXISTS);
            }

            final AxEvent event = apexModel.getPolicyModel().getEvents().get(eventName, eventVersion);
            if (event == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    "Event concept " + eventName + ':' + eventVersion + DOES_NOT_EXIST);
            }

            AxReferenceKey nextStateKey = AxReferenceKey.getNullKey();
            if (nextState != null && !(AxReferenceKey.getNullKey().getLocalName().equals(nextState))) {
                if (state.getKey().getLocalName().equals(nextState)) {
                    return new ApexApiResult(ApexApiResult.Result.FAILED,
                        "next state " + nextState + " of a state cannot be the state itself");
                }
                nextStateKey = new AxReferenceKey(state.getKey().getParentArtifactKey(), nextState);

                if (!policy.getStateMap().containsKey(nextState)) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "Next state concept " + nextStateKey.getId() + DOES_NOT_EXIST);
                }
            }

            populateStateOuputInfo(nextState, state, refKey, event, nextStateKey);
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    private void populateStateOuputInfo(final String nextState, final AxState state, final AxReferenceKey refKey,
                                        final AxEvent event, AxReferenceKey nextStateKey) {
        // nextState is null. There could be multiple events coming out of the state
        if ((nextState == null || AxReferenceKey.getNullKey().getLocalName().equals(nextState))
            && state.getStateOutputs().containsKey(refKey.getLocalName())) {
            AxStateOutput existingStateOutput = state.getStateOutputs().get(refKey.getLocalName());
            if (null == existingStateOutput.getOutgoingEventSet()
                || existingStateOutput.getOutgoingEventSet().isEmpty()) {
                Set<AxArtifactKey> eventSet = new TreeSet<>();
                eventSet.add(existingStateOutput.getOutgoingEvent());
                existingStateOutput.setOutgoingEventSet(eventSet);
            }
            existingStateOutput.getOutgoingEventSet().add(event.getKey());
        } else {
            AxStateOutput axStateOutput = new AxStateOutput(refKey, event.getKey(), nextStateKey);
            Set<AxArtifactKey> eventSet = new TreeSet<>();
            eventSet.add(axStateOutput.getOutgoingEvent());
            axStateOutput.setOutgoingEventSet(eventSet);
            state.getStateOutputs().put(refKey.getLocalName(), axStateOutput);
        }
    }

    /**
     * List policy state outputs.
     *
     * @param name       name of the policy
     * @param version    version of the policy, set to null to use the latest version
     * @param stateName  of the state
     * @param outputName of the state output, set to null to list all outputs of the state
     * @return result of the operation
     */
    public ApexApiResult listPolicyStateOutput(final String name, final String version, final String stateName,
                                               final String outputName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (outputName != null) {
                final AxStateOutput stateOutput = state.getStateOutputs().get(outputName);
                if (stateOutput != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                        new ApexModelStringWriter<AxStateOutput>(false).writeString(stateOutput, AxStateOutput.class));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + state.getKey().getId() + ':' + outputName + DOES_NOT_EXIST);
                }
            } else {
                if (state.getStateOutputs().isEmpty()) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "no state output concepts exist for state " + state.getKey().getId());
                }

                final ApexApiResult result = new ApexApiResult();

                for (final AxStateOutput stateOutput : state.getStateOutputs().values()) {
                    result.addMessage(
                        new ApexModelStringWriter<AxStateOutput>(false).writeString(stateOutput, AxStateOutput.class));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a policy state output.
     *
     * @param name       name of the policy
     * @param version    version of the policy, set to null to use the latest version
     * @param stateName  of the state
     * @param outputName of the state output, set to null to delete all state outputs
     * @return result of the operation
     */
    public ApexApiResult deletePolicyStateOutput(final String name, final String version, final String stateName,
                                                 final String outputName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (outputName != null) {
                final AxStateOutput stateOutput = state.getStateOutputs().get(outputName);
                if (stateOutput != null) {
                    final ApexApiResult result = new ApexApiResult(ApexApiResult.Result.SUCCESS,
                        new ApexModelStringWriter<AxStateOutput>(false).writeString(stateOutput, AxStateOutput.class));
                    state.getStateOutputs().remove(outputName);
                    return result;
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + state.getKey().getId() + ':' + outputName + DOES_NOT_EXIST);
                }
            } else {
                if (state.getStateOutputs().isEmpty()) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "no state output concepts exist for state " + state.getKey().getId());
                }

                final ApexApiResult result = new ApexApiResult();

                for (final Entry<String, AxStateOutput> stateOutputEntry : state.getStateOutputs().entrySet()) {
                    result.addMessage(new ApexModelStringWriter<AxStateOutput>(false)
                        .writeString(stateOutputEntry.getValue(), AxStateOutput.class));
                }
                state.getStateOutputs().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create policy finalizer logic for a state.
     *
     * @param name               name of the policy
     * @param version            version of the policy, set to null to use the latest version
     * @param stateName          of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @param logicFlavour       the policy finalizer logic flavour for the state, set to null to use the
     *                           default task logic flavour
     * @param logic              the source code for the logic of the state
     * @return result of the operation
     */
    public ApexApiResult createPolicyStateFinalizerLogic(final String name, final String version,
                                                         final String stateName, final String finalizerLogicName,
                                                         final String logicFlavour, final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(finalizerLogicName, "finalizerlogicName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);

            if (state.getStateFinalizerLogicMap().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                    CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            state.getStateFinalizerLogicMap().put(finalizerLogicName,
                new AxStateFinalizerLogic(refKey, logicFlavour, logic));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update policy finalizer logic for a state.
     *
     * @param name               name of the policy
     * @param version            version of the policy, set to null to use the latest version
     * @param stateName          of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @param logicFlavour       the policy finalizer logic flavour for the state, set to null to not
     *                           update
     * @param logic              the source code for the logic of the state, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updatePolicyStateFinalizerLogic(final String name, final String version,
                                                         final String stateName, final String finalizerLogicName,
                                                         final String logicFlavour, final String logic) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(finalizerLogicName, "finalizerLogicName may not be null");

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);
            final AxStateFinalizerLogic stateFinalizerLogic =
                state.getStateFinalizerLogicMap().get(refKey.getKey().getLocalName());
            if (stateFinalizerLogic == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    STATE_FINALIZER_LOGIC + refKey.getId() + DOES_NOT_EXIST);
            }

            if (logicFlavour != null) {
                stateFinalizerLogic.setLogicFlavour(logicFlavour);
            }
            if (logic != null) {
                stateFinalizerLogic.setLogic(logic);
            }

            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List policy finalizer logic for a state.
     *
     * @param name               name of the policy
     * @param version            version of the policy, set to null to use the latest version
     * @param stateName          of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @return result of the operation
     */
    public ApexApiResult listPolicyStateFinalizerLogic(final String name, final String version, final String stateName,
                                                       final String finalizerLogicName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (finalizerLogicName != null) {
                final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);
                final AxStateFinalizerLogic stateFinalizerLogic =
                    state.getStateFinalizerLogicMap().get(refKey.getKey().getLocalName());
                if (stateFinalizerLogic == null) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        STATE_FINALIZER_LOGIC + refKey.getId() + DOES_NOT_EXIST);
                }

                return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                    new ApexModelStringWriter<AxStateFinalizerLogic>(false).writeString(stateFinalizerLogic,
                        AxStateFinalizerLogic.class));
            } else {
                if (state.getStateFinalizerLogicMap().isEmpty()) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "no state finalizer logic defined on state " + state.getKey().getId());
                }
                final ApexApiResult result = new ApexApiResult();
                for (final AxStateFinalizerLogic stateFinalizerLogic : state.getStateFinalizerLogicMap().values()) {
                    result.addMessage(new ApexModelStringWriter<AxStateFinalizerLogic>(false)
                        .writeString(stateFinalizerLogic, AxStateFinalizerLogic.class));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete policy finalizer logic for a state.
     *
     * @param name               name of the policy
     * @param version            version of the policy, set to null to use the latest version
     * @param stateName          of the state
     * @param finalizerLogicName name of the state finalizer logic
     * @return result of the operation
     */
    public ApexApiResult deletePolicyStateFinalizerLogic(final String name, final String version,
                                                         final String stateName, final String finalizerLogicName) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            if (finalizerLogicName != null) {
                final AxReferenceKey refKey = new AxReferenceKey(state.getKey(), finalizerLogicName);
                final AxStateFinalizerLogic stateFinalizerLogic =
                    state.getStateFinalizerLogicMap().get(refKey.getKey().getLocalName());
                if (stateFinalizerLogic == null) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        STATE_FINALIZER_LOGIC + refKey.getId() + DOES_NOT_EXIST);
                }

                final ApexApiResult result = new ApexApiResult();
                result.addMessage(new ApexModelStringWriter<AxStateFinalizerLogic>(false)
                    .writeString(stateFinalizerLogic, AxStateFinalizerLogic.class));
                state.getStateFinalizerLogicMap().remove(refKey.getLocalName());
                return result;
            } else {
                ApexApiResult result = new ApexApiResult();

                if (state.getStateFinalizerLogicMap().isEmpty()) {
                    result = new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "no state finalizer logic defined on state " + state.getKey().getId());
                } else {
                    for (final AxStateFinalizerLogic stateFinalizerLogic : state.getStateFinalizerLogicMap().values()) {
                        result.addMessage(new ApexModelStringWriter<AxStateFinalizerLogic>(false)
                            .writeString(stateFinalizerLogic, AxStateFinalizerLogic.class));
                    }
                    state.getStateFinalizerLogicMap().clear();
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a policy state task reference.
     *
     * @param builder builder for the state task reference
     * @return result of the operation
     */
    public ApexApiResult createPolicyStateTaskRef(CreatePolicyStateTaskRef builder) {
        try {
            Assertions.argumentNotNull(builder.getStateName(), STATE_NAME_MAY_NOT_BE_NULL);
            Assertions.argumentNotNull(builder.getOutputName(), "outputName may not be null");

            final AxPolicy policy =
                apexModel.getPolicyModel().getPolicies().get(builder.getName(), builder.getVersion());
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + builder.getName() + ':' + builder.getVersion() + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(builder.getStateName());
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + builder.getStateName() + DOES_NOT_EXIST);
            }

            final AxTask task =
                apexModel.getPolicyModel().getTasks().get(builder.getTaskName(), builder.getTaskVersion());
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + builder.getTaskName() + ':' + builder.getTaskVersion() + DOES_NOT_EXIST);
            }

            if (state.getTaskReferences().containsKey(task.getKey())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, "task " + task.getKey().getId()
                    + " already has reference with output " + state.getTaskReferences().get(task.getKey()));
            }

            AxReferenceKey refKey;
            if (builder.getTaskLocalName() == null) {
                refKey = new AxReferenceKey(state.getKey(), state.getKey().getParentKeyName());
            } else {
                refKey = new AxReferenceKey(state.getKey(), builder.getTaskLocalName());
            }

            // The reference to the output we're using here
            final AxReferenceKey outputRefKey = new AxReferenceKey(state.getKey(), builder.getOutputName());

            final AxStateTaskOutputType stateTaskOutputType = AxStateTaskOutputType.valueOf(builder.getOutputType());
            if (stateTaskOutputType.equals(AxStateTaskOutputType.DIRECT)) {
                if (!state.getStateOutputs().containsKey(outputRefKey.getLocalName())) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "state output concept " + outputRefKey.getId() + DOES_NOT_EXIST);
                }
            } else if (stateTaskOutputType.equals(AxStateTaskOutputType.LOGIC)) {
                if (!state.getStateFinalizerLogicMap().containsKey(outputRefKey.getLocalName())) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        "state finalizer logic concept " + outputRefKey.getId() + DOES_NOT_EXIST);
                }
            } else {
                return new ApexApiResult(ApexApiResult.Result.FAILED,
                    "output type " + builder.getOutputType() + " invalid");
            }

            String outputRefName = outputRefKey.getLocalName();
            // in case of SFL, outgoing event will be same for all state outputs that are part of SFL.So, take any entry
            if (AxStateTaskOutputType.LOGIC.equals(stateTaskOutputType)) {
                outputRefName = state.getStateOutputs().keySet().iterator().next();
            }

            // add input and output events to the task based on state definition
            if (state.getStateOutputs().containsKey(outputRefName)) {
                populateIoEventsToTask(state, task, outputRefName);
            }

            state.getTaskReferences().put(task.getKey(),
                new AxStateTaskReference(refKey, stateTaskOutputType, outputRefKey));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    private void populateIoEventsToTask(final AxState state, final AxTask task, final String outputRefName) {
        AxEvent triggerEvent = apexModel.getPolicyModel().getEvents().get(state.getTrigger());
        task.setInputEvent(triggerEvent);
        Map<String, AxEvent> outputEvents = new TreeMap<>();
        if (state.getNextStateSet().isEmpty()
            || state.getNextStateSet().contains(AxReferenceKey.getNullKey().getLocalName())) {
            state.getStateOutputs().get(outputRefName).getOutgoingEventSet().forEach(outgoingEventKey -> outputEvents
                .put(outgoingEventKey.getName(), apexModel.getPolicyModel().getEvents().get(outgoingEventKey)));
        } else {
            AxArtifactKey outgoingEventKey = state.getStateOutputs().get(outputRefName).getOutgoingEvent();
            outputEvents.put(outgoingEventKey.getName(), apexModel.getPolicyModel().getEvents().get(outgoingEventKey));
        }
        task.setOutputEvents(outputEvents);
    }

    /**
     * List policy state task references.
     *
     * @param name        name of the policy
     * @param version     version of the policy, set to null to use the latest version
     * @param stateName   of the state
     * @param taskName    name of the task, set to null to list all task references
     * @param taskVersion version of the task, set to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult listPolicyStateTaskRef(final String name, final String version, final String stateName,
                                                final String taskName, final String taskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            boolean found = false;
            final Map<AxArtifactKey, AxStateTaskReference> taskReferences = state.getTaskReferences();
            for (final Entry<AxArtifactKey, AxStateTaskReference> taskReferenceEntry : taskReferences.entrySet()) {
                final AxArtifactKey key = taskReferenceEntry.getKey();
                final AxStateTaskReference value = taskReferenceEntry.getValue();
                if ((taskName != null && !key.getName().equals(taskName))
                    || (taskVersion != null && !key.getVersion().equals(taskVersion))) {
                    continue;
                }

                found = true;
                result
                    .addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(key, AxArtifactKey.class));
                result.addMessage(new ApexModelStringWriter<AxStateTaskReference>(false).writeString(value,
                    AxStateTaskReference.class));
            }
            if (found) {
                return result;
            } else {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    "no task references found for state " + state.getKey().getId());
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a policy state task reference.
     *
     * @param name        name of the policy
     * @param version     version of the policy, set to null to use the latest version
     * @param stateName   of the state
     * @param taskName    name of the task, set to null to delete all task references
     * @param taskVersion version of the task, set to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult deletePolicyStateTaskRef(final String name, final String version, final String stateName,
                                                  final String taskName, final String taskVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final Set<AxArtifactKey> deleteSet = new TreeSet<>();

            for (final AxArtifactKey taskReferenceKey : state.getTaskReferences().keySet()) {
                if ((taskName != null && !taskReferenceKey.getName().equals(taskName))
                    || (taskVersion != null && !taskReferenceKey.getVersion().equals(taskVersion))) {
                    continue;
                }
                deleteSet.add(taskReferenceKey);
            }
            if (deleteSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + taskName + ':' + taskVersion + DOES_NOT_EXIST_ON_STATE + state.getKey().getId());
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxArtifactKey keyToDelete : deleteSet) {
                state.getTaskReferences().remove(keyToDelete);
                result.addMessage(
                    new ApexModelStringWriter<AxArtifactKey>(false).writeString(keyToDelete, AxArtifactKey.class));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a policy state context album reference.
     *
     * @param name                name of the policy
     * @param version             version of the policy, set to null to use the latest version
     * @param stateName           of the state
     * @param contextAlbumName    name of the context album for the context album reference
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *                            to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult createPolicyStateContextRef(final String name, final String version, final String stateName,
                                                     final String contextAlbumName, final String contextAlbumVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final AxContextAlbum contextAlbum =
                apexModel.getPolicyModel().getAlbums().get(contextAlbumName, contextAlbumVersion);
            if (contextAlbum == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + contextAlbumName + ':' + contextAlbumVersion + DOES_NOT_EXIST);
            }

            if (state.getContextAlbumReferences().contains(contextAlbum.getKey())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, "concept album reference for concept "
                    + contextAlbum.getKey().getId() + " already exists in state");
            }

            state.getContextAlbumReferences().add(contextAlbum.getKey());
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List policy state context album references.
     *
     * @param name                name of the policy
     * @param version             version of the policy, set to null to use the latest version
     * @param stateName           of the state
     * @param contextAlbumName    name of the context album for the context album reference, set to
     *                            null to list all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *                            to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult listPolicyStateContextRef(final String name, final String version, final String stateName,
                                                   final String contextAlbumName, final String contextAlbumVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            boolean found = false;
            for (final AxArtifactKey albumKey : state.getContextAlbumReferences()) {
                if ((contextAlbumName != null && !albumKey.getName().equals(contextAlbumName))
                    || (contextAlbumVersion != null && !albumKey.getVersion().equals(contextAlbumVersion))) {
                    continue;
                }
                result.addMessage(
                    new ApexModelStringWriter<AxArtifactKey>(false).writeString(albumKey, AxArtifactKey.class));
                found = true;
            }
            if (!found) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, CONCEPT + contextAlbumName + ':'
                    + contextAlbumVersion + DOES_NOT_EXIST_ON_STATE + state.getKey().getId());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a policy state context album reference.
     *
     * @param name                name of the policy
     * @param version             version of the policy, set to null to use the default version
     * @param stateName           of the state
     * @param contextAlbumName    name of the context album for the context album reference, set to
     *                            null to delete all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *                            to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult deletePolicyStateContextRef(final String name, final String version, final String stateName,
                                                     final String contextAlbumName, final String contextAlbumVersion) {
        try {
            Assertions.argumentNotNull(stateName, STATE_NAME_MAY_NOT_BE_NULL);

            final AxPolicy policy = apexModel.getPolicyModel().getPolicies().get(name, version);
            if (policy == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxState state = policy.getStateMap().get(stateName);
            if (state == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                    CONCEPT + policy.getKey().getId() + ':' + stateName + DOES_NOT_EXIST);
            }

            final Set<AxArtifactKey> deleteSet = new TreeSet<>();

            for (final AxArtifactKey albumKey : state.getContextAlbumReferences()) {

                if ((contextAlbumName != null && !albumKey.getName().equals(contextAlbumName))
                    || (contextAlbumVersion != null && !albumKey.getVersion().equals(contextAlbumVersion))) {

                    continue;
                }
                deleteSet.add(albumKey);
            }
            if (deleteSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST, CONCEPT + contextAlbumName + ':'
                    + contextAlbumVersion + DOES_NOT_EXIST_ON_STATE + state.getKey().getId());
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxArtifactKey keyToDelete : deleteSet) {
                state.getContextAlbumReferences().remove(keyToDelete);
                result.addMessage(
                    new ApexModelStringWriter<AxArtifactKey>(false).writeString(keyToDelete, AxArtifactKey.class));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
}
