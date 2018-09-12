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

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Helper class used to extract information from a policy model into a policy model that is a subset
 * of the original policy model.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class PolicyModelSplitter {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyModelSplitter.class);

    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private PolicyModelSplitter() {}

    /**
     * Get a sub policy model with only the information required for the specified policies from a
     * larger policy model.
     *
     * @param sourcePolicyModel the source Apex Model
     * @param subPolicies the policies to include in sub policy model
     * @return the new Destination Model
     * @throws ApexModelException on model transfer errors
     */
    public static AxPolicyModel getSubPolicyModel(final AxPolicyModel sourcePolicyModel,
            final Collection<AxArtifactKey> subPolicies) throws ApexModelException {
        return getSubPolicyModel(sourcePolicyModel, subPolicies, false);
    }

    /**
     * Get a sub policy model with only the information required for the specified policies from a
     * larger policy model.
     *
     * @param sourcePolicyModel the source Apex Model
     * @param subPolicies the policies to include in sub policy model
     * @param ignoreInvalidSource Ignore errors on the source model, do the best you can
     * @return the new Destination Model
     * @throws ApexModelException on model transfer errors
     */
    public static AxPolicyModel getSubPolicyModel(final AxPolicyModel sourcePolicyModel,
            final Collection<AxArtifactKey> subPolicies, final boolean ignoreInvalidSource) throws ApexModelException {
        // Validate the source model
        if (!ignoreInvalidSource) {
            final AxValidationResult sourceValidationResult = new AxValidationResult();
            sourcePolicyModel.validate(sourceValidationResult);
            if (!sourceValidationResult.isValid()) {
                String message = "source model is invalid: " + sourceValidationResult.toString();
                LOGGER.warn(message);
                throw new ApexModelException(message);
            }
        }

        // The new policy model
        final AxPolicyModel newPolicyModel = new AxPolicyModel(sourcePolicyModel.getKey());
        newPolicyModel.getKeyInformation().setKey(sourcePolicyModel.getKeyInformation().getKey());
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(sourcePolicyModel.getKey()));
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getKeyInformation().getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap()
                        .get(sourcePolicyModel.getKeyInformation().getKey()));

        //  Get the events, tasks, context maps, and data types used by each policy
        final Set<AxArtifactKey> contextSchemaSet = new TreeSet<>();
        final Set<AxArtifactKey> eventSet = new TreeSet<>();
        final Set<AxArtifactKey> contextAlbumSet = new TreeSet<>();
        final Set<AxArtifactKey> taskSet = new TreeSet<>();

        newPolicyModel.getPolicies().setKey(sourcePolicyModel.getPolicies().getKey());
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getPolicies().getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(sourcePolicyModel.getPolicies().getKey()));
        for (final AxArtifactKey subPolicyKey : subPolicies) {
            final AxPolicy subPolicy = sourcePolicyModel.getPolicies().getPolicyMap().get(subPolicyKey);
            if (subPolicy == null) {
                LOGGER.warn("source sub policy not found: {}", subPolicyKey);
                continue;
            }

            // Transfer the policy across
            newPolicyModel.getPolicies().getPolicyMap().put(subPolicyKey, subPolicy);
            newPolicyModel.getKeyInformation().getKeyInfoMap().put(subPolicyKey,
                    sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(subPolicyKey));

            // Get the references for this policy
            final PolicyAnalysisResult analysisResult = new PolicyAnalyser().analyse(sourcePolicyModel, subPolicy);
            contextSchemaSet.addAll(analysisResult.getUsedContextSchemas());
            eventSet.addAll(analysisResult.getUsedEvents());
            contextAlbumSet.addAll(analysisResult.getUsedContextAlbums());
            taskSet.addAll(analysisResult.getUsedTasks());

        }

        // Now add all the referenced data types, events, context maps, and tasks to the policy
        // model
        newPolicyModel.getSchemas().setKey(sourcePolicyModel.getSchemas().getKey());
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getSchemas().getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(sourcePolicyModel.getSchemas().getKey()));
        for (final AxArtifactKey contextSchemaKey : contextSchemaSet) {
            newPolicyModel.getSchemas().getSchemasMap().put(contextSchemaKey,
                    sourcePolicyModel.getSchemas().getSchemasMap().get(contextSchemaKey));
            newPolicyModel.getKeyInformation().getKeyInfoMap().put(contextSchemaKey,
                    sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(contextSchemaKey));
        }
        newPolicyModel.getEvents().setKey(sourcePolicyModel.getEvents().getKey());
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getEvents().getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(sourcePolicyModel.getEvents().getKey()));
        for (final AxArtifactKey eventKey : eventSet) {
            newPolicyModel.getEvents().getEventMap().put(eventKey,
                    sourcePolicyModel.getEvents().getEventMap().get(eventKey));
            newPolicyModel.getKeyInformation().getKeyInfoMap().put(eventKey,
                    sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(eventKey));
        }
        newPolicyModel.getAlbums().setKey(sourcePolicyModel.getAlbums().getKey());
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getAlbums().getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(sourcePolicyModel.getAlbums().getKey()));
        for (final AxArtifactKey contextAlbumKey : contextAlbumSet) {
            newPolicyModel.getAlbums().getAlbumsMap().put(contextAlbumKey,
                    sourcePolicyModel.getAlbums().getAlbumsMap().get(contextAlbumKey));
            newPolicyModel.getKeyInformation().getKeyInfoMap().put(contextAlbumKey,
                    sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(contextAlbumKey));
        }
        newPolicyModel.getTasks().setKey(sourcePolicyModel.getTasks().getKey());
        newPolicyModel.getKeyInformation().getKeyInfoMap().put(sourcePolicyModel.getTasks().getKey(),
                sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(sourcePolicyModel.getTasks().getKey()));
        for (final AxArtifactKey taskKey : taskSet) {
            newPolicyModel.getTasks().getTaskMap().put(taskKey, sourcePolicyModel.getTasks().getTaskMap().get(taskKey));
            newPolicyModel.getKeyInformation().getKeyInfoMap().put(taskKey,
                    sourcePolicyModel.getKeyInformation().getKeyInfoMap().get(taskKey));
        }

        // That's it, return the model
        return newPolicyModel;
    }
}
