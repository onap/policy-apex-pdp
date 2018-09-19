/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.client.editor.rest.handling;

import java.util.Map;

import org.onap.policy.apex.client.editor.rest.handling.bean.BeanKeyRef;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanLogic;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanPolicy;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanState;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanStateOutput;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanStateTaskRef;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles commands on policies in Apex models.
 */
public class PolicyHandler implements RestCommandHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyHandler.class);

    // Recurring string constants
    private static final String OK = ": OK";
    private static final String NOT_OK = ": Not OK";
    private static final String POLICY_WAS_CREATED = "\". The policy was created, ";
    private static final String POLICY_STATE_CREATED = "\". The policy and state were created, ";
    private static final String POLICY_PARTIALLY_DEFINED = " The policy has only been partially defined.";
    private static final String FOR_POLICY = "\" for policy \"";
    private static final String IN_STATE = "\" in state \"";
    private static final String POLICY_CREATED_STATE_ERROR = POLICY_WAS_CREATED
                    + "but there was an error adding the state.";
    private static final String POLICY_STATE_CREATED_OTHER_ERROR = POLICY_STATE_CREATED
                    + "but there was an error adding the";

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command) {
        return getUnsupportedCommandResultMessage(session, commandType, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String jsonString) {

        if (!RestCommandType.POLICY.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case CREATE:
                return createPolicy(session, jsonString);
            case UPDATE:
                return updatePolicy(session, jsonString);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version) {
        if (!RestCommandType.POLICY.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case LIST:
                return listPolicies(session, name, version);
            case DELETE:
                return deletePolicy(session, name, version);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * Creates a policy with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed See {@linkplain BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    public ApexApiResult createPolicy(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanPolicy jsonbean = RestUtils.getJsonParameters(jsonString, BeanPolicy.class);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().createPolicy(jsonbean.getName(), jsonbean.getVersion(),
                        jsonbean.getTemplate(), jsonbean.getFirstState(), jsonbean.getUuid(),
                        jsonbean.getDescription());

        if (result.isOk()) {
            result = createPolicyContent(session, jsonbean);
        }

        if (result.isOk()) {
            session.commitChanges();
        } else {
            session.discardChanges();
        }

        LOGGER.exit("Policy/Create" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Create the content of the policy.
     * 
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed See {@linkplain BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createPolicyContent(RestSession session, BeanPolicy jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getStates() == null || jsonbean.getStates().isEmpty()) {
            result.setResult(Result.FAILED);
            result.addMessage("Null or empty state map; no states defined for policy \"" + jsonbean.getName() + ":"
                            + jsonbean.getVersion()
                            + "\". The policy was created, but there was an error adding states."
                            + POLICY_PARTIALLY_DEFINED);
            return result;
        }

        // States reference each other so all states must be created before they are populated
        for (final Map.Entry<String, BeanState> stateEntry : jsonbean.getStates().entrySet()) {
            ApexApiResult stateCreateResult = createState(session, jsonbean.getName(), jsonbean.getVersion(),
                            stateEntry.getKey(), stateEntry.getValue());

            if (stateCreateResult.isNok()) {
                result.setResult(stateCreateResult.getResult());
                result.addMessage(stateCreateResult.getMessage());
            }
        }

        // Bale out if the state creation did not work
        if (result.isNok()) {
            return result;
        }

        // Now create the content of each state
        for (final Map.Entry<String, BeanState> stateEntry : jsonbean.getStates().entrySet()) {
            ApexApiResult stateContentCreateResult = createStateContent(session, jsonbean.getName(),
                            jsonbean.getVersion(), stateEntry.getKey(), stateEntry.getValue());

            if (stateContentCreateResult.isNok()) {
                result.setResult(stateContentCreateResult.getResult());
                result.addMessage(stateContentCreateResult.getMessage());
            }
        }

        return result;
    }

    /**
     * Create a state on the policy.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createState(final RestSession session, final String policyName, final String policyVersion,
                    final String stateName, final BeanState stateBean) {

        if (stateBean == null) {
            return new ApexApiResult(Result.FAILED,
                            "Null or invalid state information for state \"" + stateName + FOR_POLICY + policyName + ":"
                                            + policyVersion + POLICY_CREATED_STATE_ERROR + POLICY_PARTIALLY_DEFINED);
        }

        if (stateBean.getTrigger() == null) {
            return new ApexApiResult(Result.FAILED,
                            "Null or invalid state trigger for state \"" + stateName + FOR_POLICY + policyName + ":"
                                            + policyVersion + POLICY_CREATED_STATE_ERROR + POLICY_PARTIALLY_DEFINED);
        }

        if (stateBean.getDefaultTask() == null) {
            return new ApexApiResult(Result.FAILED, "Null or invalid default task for state \"" + stateName + FOR_POLICY
                            + policyName + ":" + policyVersion + POLICY_CREATED_STATE_ERROR + POLICY_PARTIALLY_DEFINED);
        }

        return session.getApexModelEdited().createPolicyState(policyName, policyVersion, stateName,
                        stateBean.getTrigger().getName(), stateBean.getTrigger().getVersion(),
                        stateBean.getDefaultTask().getName(), stateBean.getDefaultTask().getVersion());
    }

    /**
     * Create the content of a state on the policy.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createStateContent(final RestSession session, final String policyName,
                    final String policyVersion, final String stateName, final BeanState stateBean) {

        ApexApiResult ret = createStateTaskSelectionLogic(session, policyName, policyVersion, stateName, stateBean);

        if (ret.isOk()) {
            ret = createStateContextReferences(session, policyName, policyVersion, stateName, stateBean);
        }

        if (ret.isOk()) {
            ret = createStateFinalizers(session, policyName, policyVersion, stateName, stateBean);
        }

        if (ret.isOk()) {
            ret = createStateOutputs(session, policyName, policyVersion, stateName, stateBean);
        }

        if (ret.isOk()) {
            ret = createStateTaskReferences(session, policyName, policyVersion, stateName, stateBean);
        }

        return ret;
    }

    /**
     * Create the task selection logic for the state.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createStateTaskSelectionLogic(final RestSession session, final String policyName,
                    final String policyVersion, final String stateName, final BeanState stateBean) {

        final BeanLogic tsl = stateBean.getTaskSelectionLogic();
        if (tsl == null) {
            return new ApexApiResult();
        }

        ApexApiResult result = session.getApexModelEdited().createPolicyStateTaskSelectionLogic(policyName,
                        policyVersion, stateName, tsl.getLogicFlavour(), tsl.getLogic());

        if (result.isNok()) {
            result.addMessage("Failed to add task selection logic for state \"" + stateName + "\" for" + " policy \""
                            + policyName + ":" + policyVersion + POLICY_WAS_CREATED
                            + "but there was an error adding the task selection logic "
                            + "for the state. The policy has only been partially defined.");
        }
        return result;
    }

    /**
     * Create the context references for the state.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createStateContextReferences(final RestSession session, final String policyName,
                    final String policyVersion, final String stateName, final BeanState stateBean) {

        ApexApiResult result = new ApexApiResult();

        final BeanKeyRef[] contextReferences = stateBean.getContexts();
        if (contextReferences == null || contextReferences.length == 0) {
            return result;
        }

        for (final BeanKeyRef contextReference : contextReferences) {
            if (contextReference == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null or invalid context reference \"" + contextReference + "\" for" + " state \""
                                + stateName + FOR_POLICY + policyName + ":" + policyVersion
                                + "\". The policy was created, but there was an error adding the context "
                                + "reference for the state. The policy has only been partially defined.");
                continue;
            }

            ApexApiResult contextRefResult = session.getApexModelEdited().createPolicyStateContextRef(policyName,
                            policyVersion, stateName, contextReference.getName(), contextReference.getVersion());

            if (contextRefResult.isNok()) {
                result.setResult(contextRefResult.getResult());
                result.addMessage("Failed to add context reference \"" + contextReference + "\" for state \""
                                + stateName + FOR_POLICY + policyName + ":" + policyVersion + POLICY_WAS_CREATED
                                + "but there was an error adding the context reference "
                                + "for the state. The policy has only been partially defined.");
            }
        }

        return result;
    }

    /**
     * Create the state finalizers for the state.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createStateFinalizers(final RestSession session, final String policyName,
                    final String policyVersion, final String stateName, final BeanState stateBean) {

        ApexApiResult result = new ApexApiResult();

        final Map<String, BeanLogic> finalizers = stateBean.getFinalizers();
        if (finalizers == null || finalizers.isEmpty()) {
            return result;
        }

        for (final Map.Entry<String, BeanLogic> finalizerEntry : finalizers.entrySet()) {
            if (finalizerEntry.getKey() == null || finalizerEntry.getValue() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null or invalid finalizer information for finalizer " + "named \""
                                + finalizerEntry.getKey() + IN_STATE + stateName + FOR_POLICY + policyName + ":"
                                + policyVersion + POLICY_STATE_CREATED_OTHER_ERROR + " finalizer. The policy has only "
                                + "been partially defined.");
                continue;
            }

            ApexApiResult finalizerResult = session.getApexModelEdited().createPolicyStateFinalizerLogic(policyName,
                            policyVersion, stateName, finalizerEntry.getKey(),
                            finalizerEntry.getValue().getLogicFlavour(), finalizerEntry.getValue().getLogic());

            if (finalizerResult.isNok()) {
                result.setResult(finalizerResult.getResult());
                result.addMessage("Failed to add finalizer information for finalizer named \"" + finalizerEntry.getKey()
                                + "\" in" + " state \"" + stateName + FOR_POLICY + policyName + ":" + policyVersion
                                + POLICY_STATE_CREATED_OTHER_ERROR
                                + " finalizer. The policy has only been partially defined.");
            }
        }

        return result;
    }

    /**
     * Create the state outputs for the state.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createStateOutputs(final RestSession session, final String policyName,
                    final String policyVersion, final String stateName, final BeanState stateBean) {

        ApexApiResult result = new ApexApiResult();

        final Map<String, BeanStateOutput> stateOutputs = stateBean.getStateOutputs();
        if (stateOutputs == null || stateOutputs.isEmpty()) {
            result.setResult(Result.FAILED);
            result.addMessage("No state outputs have been defined in state \"" + stateName + FOR_POLICY + policyName
                            + ":" + policyVersion
                            + "\". The policy and state were created, but there was an error adding state"
                            + " outputs. The policy has only been partially defined.");
            return result;
        }

        for (final Map.Entry<String, BeanStateOutput> stateOutput : stateOutputs.entrySet()) {
            final String outputName = stateOutput.getKey();
            final BeanStateOutput output = stateOutput.getValue();

            if (outputName == null || output == null || output.getEvent() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null or invalid output information for output named \"" + outputName + IN_STATE
                                + stateName + FOR_POLICY + policyName + ":" + policyVersion
                                + POLICY_STATE_CREATED_OTHER_ERROR
                                + " output. The policy has only been partially defined.");
                continue;
            }

            ApexApiResult outputResult = session.getApexModelEdited().createPolicyStateOutput(policyName, policyVersion,
                            stateName, outputName, output.getEvent().getName(), output.getEvent().getVersion(),
                            output.getNextState());

            if (outputResult.isNok()) {
                result.setResult(outputResult.getResult());
                result.addMessage("Failed to add output information for output named \"" + outputName + IN_STATE
                                + stateName + FOR_POLICY + policyName + ":" + policyVersion + POLICY_STATE_CREATED
                                + "but there was an error adding the output." + POLICY_PARTIALLY_DEFINED);
            }
        }

        return result;
    }

    /**
     * Create the task references for the state.
     * 
     * @param session the Apex model editing session
     * @param policyName the policy name
     * @param policVersion the policy version
     * @param stateName the name of the state
     * @param stateBean the information on the state to create
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createStateTaskReferences(final RestSession session, final String policyName,
                    final String policyVersion, final String stateName, final BeanState stateBean) {

        ApexApiResult result = new ApexApiResult();

        final Map<String, BeanStateTaskRef> taskMap = stateBean.getTasks();
        if (taskMap == null || taskMap.isEmpty()) {
            result.setResult(Result.FAILED);
            result.addMessage("No tasks have been defined in state \"" + stateName + FOR_POLICY + policyName + ":"
                            + policyVersion
                            + "\". The policy and state were created, but there was an error adding tasks."
                            + POLICY_PARTIALLY_DEFINED);
            return result;
        }

        for (final Map.Entry<String, BeanStateTaskRef> taskEntry : taskMap.entrySet()) {
            final String taskLocalName = taskEntry.getKey();
            final BeanStateTaskRef taskReference = taskEntry.getValue();

            if (taskLocalName == null || taskReference == null || taskReference.getTask() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null or invalid task information for task named \"" + taskLocalName + IN_STATE
                                + stateName + "\" for for policy \"" + policyName + ":" + policyVersion
                                + "\". The policy and state were created, but there was an error adding the "
                                + "task. The policy has only been partially defined.");
                continue;
            }

            ApexApiResult taskRefResult = session.getApexModelEdited().createPolicyStateTaskRef(policyName,
                            policyVersion, stateName, taskLocalName, taskReference.getTask().getName(),
                            taskReference.getTask().getVersion(), taskReference.getOutputType(),
                            taskReference.getOutputName());

            if (taskRefResult.isNok()) {
                result.setResult(taskRefResult.getResult());
                result.addMessage("Failed to add task reference \"" + taskEntry + "\" for state \"" + stateName
                                + FOR_POLICY + policyName + ":" + policyVersion + POLICY_WAS_CREATED
                                + "but there was an error adding the task reference for"
                                + " the state. The policy has only been partially defined.");
            }
        }

        return result;
    }

    /**
     * Update a policy with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanPolicy}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult updatePolicy(final RestSession session, final String jsonString) {

        LOGGER.entry(jsonString);

        final BeanPolicy jsonbean = RestUtils.getJsonParameters(jsonString, BeanPolicy.class);

        if (blank2Null(jsonbean.getName()) == null || blank2Null(jsonbean.getVersion()) == null) {
            LOGGER.exit("Task/Update" + NOT_OK);
            return new ApexApiResult(Result.FAILED, "Null/Empty Policy name/version (\"" + jsonbean.getName() + ":"
                            + jsonbean.getVersion() + "\" passed to UpdatePolicy");
        }

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().deletePolicy(jsonbean.getName(), jsonbean.getVersion());

        if (result.isOk()) {
            result = session.getApexModelEdited().createPolicy(jsonbean.getName(), jsonbean.getVersion(),
                            jsonbean.getTemplate(), jsonbean.getFirstState(), jsonbean.getUuid(),
                            jsonbean.getDescription());

            if (result.isOk()) {
                result = createPolicyContent(session, jsonbean);
            }
        }

        if (result.isOk()) {
            session.commitChanges();
        } else {
            session.discardChanges();
        }

        LOGGER.exit("Policy/Update" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;

    }

    /**
     * List policies with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@link AxPolicy}, with merged {@linkplain AxKey Info} for the
     * root object.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult listPolicies(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().listPolicy(blank2Null(name), blank2Null(version));

        LOGGER.exit("Policy/Get" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Delete policies with the given key names/versions.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult deletePolicy(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        session.editModel();

        // all input/output fields, parameters, logic, context references is "owned"/contained
        // in the task, so
        // deleting the task removes all of these
        ApexApiResult result = session.getApexModelEdited().deletePolicy(blank2Null(name), blank2Null(version));

        if (result.isOk()) {
            session.commitChanges();
        } else {
            session.discardChanges();
        }

        LOGGER.exit("Policy/Delete" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

}
