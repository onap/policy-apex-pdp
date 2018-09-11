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

import java.util.Map.Entry;
import java.util.Set;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class analyses a policy model and shows what the usage of each context album, context item, data type, and event
 * is.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyAnalyser {
    /**
     * Perform an analysis on a policy model.
     *
     * @param policyModel The policy model
     * @return the analysis result of the policy model
     */
    public PolicyAnalysisResult analyse(final AxPolicyModel policyModel) {
        Assertions.argumentNotNull(policyModel, "policyModel may not be null");

        final PolicyAnalysisResult result = new PolicyAnalysisResult(policyModel);

        for (final AxPolicy policy : policyModel.getPolicies().getPolicyMap().values()) {
            for (final AxState state : policy.getStateMap().values()) {
                analyseState(state, result);
            }
        }

        for (final AxTask task : policyModel.getTasks().getTaskMap().values()) {
            analyseTask(task, result);
        }

        for (final AxEvent event : policyModel.getEvents().getEventMap().values()) {
            analyseEvent(event, result);
        }

        for (final AxContextAlbum contextAlbum : policyModel.getAlbums().getAll(null)) {
            result.getContextSchemaUsage().get(contextAlbum.getItemSchema()).add(contextAlbum.getKey());
        }

        return result;
    }

    /**
     * Perform an analysis on a single policy in a policy model.
     *
     * @param policyModel The policy model
     * @param policy The policy
     * @return the analysis result of the policy model
     */
    public PolicyAnalysisResult analyse(final AxPolicyModel policyModel, final AxPolicy policy) {
        Assertions.argumentNotNull(policyModel, "policyModel may not be null");
        Assertions.argumentNotNull(policy, "policy may not be null");

        final PolicyAnalysisResult result = new PolicyAnalysisResult(policyModel);

        for (final AxState state : policy.getStateMap().values()) {
            analyseState(state, result);
        }

        // Only analyse tasks used by this policy
        for (final Entry<AxArtifactKey, Set<AxKey>> taskUsageEntry : result.getTaskUsage().entrySet()) {
            // If the usage set is empty, then we skip the task as its not used in the policy
            if (!taskUsageEntry.getValue().isEmpty()) {
                analyseTask(policyModel.getTasks().getTaskMap().get(taskUsageEntry.getKey()), result);
            }
        }

        // Only analyse events used by this policy, same approach as for tasks
        for (final Entry<AxArtifactKey, Set<AxKey>> eventUsageEntry : result.getEventUsage().entrySet()) {
            if (!eventUsageEntry.getValue().isEmpty()) {
                analyseEvent(policyModel.getEvents().getEventMap().get(eventUsageEntry.getKey()), result);
            }
        }

        // Only analyse context albums used by this policy, same approach as for tasks
        for (final Entry<AxArtifactKey, Set<AxKey>> contextAlbumUsageEntry : result.getContextAlbumUsage().entrySet()) {
            if (!contextAlbumUsageEntry.getValue().isEmpty()) {
                final AxContextAlbum contextAlbum = policyModel.getAlbums().get(contextAlbumUsageEntry.getKey());
                result.getContextSchemaUsage().get(contextAlbum.getItemSchema()).add(contextAlbum.getKey());
            }
        }

        for (final AxEvent event : policyModel.getEvents().getEventMap().values()) {
            analyseEvent(event, result);
        }

        for (final AxContextAlbum contextAlbum : policyModel.getAlbums().getAll(null)) {
            result.getContextSchemaUsage().get(contextAlbum.getItemSchema()).add(contextAlbum.getKey());
        }

        return result;
    }

    /**
     * Analyse the usage of concepts by a state.
     *
     * @param state the state to analyse
     * @param result the result
     */
    private void analyseState(final AxState state, final PolicyAnalysisResult result) {
        // Event usage by state
        result.getEventUsage().get(state.getTrigger()).add(state.getKey());
        for (final AxStateOutput stateOutput : state.getStateOutputs().values()) {
            result.getEventUsage().get(stateOutput.getOutgingEvent()).add(state.getKey());
        }

        // State Context Usage
        for (final AxArtifactKey contextAlbumKey : state.getContextAlbumReferences()) {
            result.getContextAlbumUsage().get(contextAlbumKey).add(state.getKey());
        }

        // Task usage by state
        for (final AxArtifactKey task : state.getTaskReferences().keySet()) {
            result.getTaskUsage().get(task).add(state.getKey());
        }
    }

    /**
     * Analyse the usage of concepts by a task.
     *
     * @param task the task to analyse
     * @param result the result
     */
    private void analyseTask(final AxTask task, final PolicyAnalysisResult result) {
        // Task Context Usage
        for (final AxArtifactKey contextAlbumKey : task.getContextAlbumReferences()) {
            result.getContextAlbumUsage().get(contextAlbumKey).add(task.getKey());
        }

        // Task data type usage
        for (final AxInputField inputField : task.getInputFields().values()) {
            result.getContextSchemaUsage().get(inputField.getSchema()).add(task.getKey());
        }
        for (final AxOutputField outputField : task.getOutputFields().values()) {
            result.getContextSchemaUsage().get(outputField.getSchema()).add(task.getKey());
        }
    }

    /**
     * Analyse the usage of concepts by an event.
     *
     * @param event the event to analyse
     * @param result the result of the analysis
     */
    private void analyseEvent(final AxEvent event, final PolicyAnalysisResult result) {
        // Event data type usage
        for (final AxField eventField : event.getFields()) {
            result.getContextSchemaUsage().get(eventField.getSchema()).add(event.getKey());
        }
    }
}
