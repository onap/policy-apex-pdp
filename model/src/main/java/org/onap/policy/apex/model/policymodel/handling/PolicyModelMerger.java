/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

import java.util.Map;
import java.util.Map.Entry;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Helper class used to merge information from two policy models together into a single policy
 * model.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public final class PolicyModelMerger {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(PolicyModelMerger.class);

    /**
     * Private constructor used to prevent sub class instantiation.
     */
    private PolicyModelMerger() {
    }

    /**
     * Get a merged policy model with information from two policy models merged into a larger policy
     * model.
     *
     * @param leftPolicyModel the source Apex Model
     * @param rightPolicyModel the policies to include in sub policy model
     * @param useLeftOnMatches if true, uses concepts from the left model if concepts with common
     *        keys are found, if false it uses the concepts from the right model
     * @param failOnDuplicateKeys whether to fail or not on the occurence of duplicate concept keys
     * @return the new Destination Model
     * @throws ApexModelException on model transfer errors
     */
    public static AxPolicyModel getMergedPolicyModel(final AxPolicyModel leftPolicyModel,
        final AxPolicyModel rightPolicyModel, final boolean useLeftOnMatches, final boolean failOnDuplicateKeys)
        throws ApexModelException {
        return getMergedPolicyModel(leftPolicyModel, rightPolicyModel, useLeftOnMatches, false, failOnDuplicateKeys);
    }

    /**
     * Get a merged policy model with information from two policy models merged into a larger policy
     * model.
     *
     * @param leftPolicyModel the source Apex Model
     * @param rightPolicyModel the policies to include in sub policy model
     * @param useLeftOnMatches if true, uses concepts from the left model if concepts with common
     *        keys are found, if false it uses the concepts from the right model
     * @param ignoreInvalidSource Ignore errors on the source model, do the best you can
     * @param failOnDuplicateKeys whether to fail or not on the occurence of duplicate concept keys
     * @return the new Destination Model
     * @throws ApexModelException on model transfer errors
     */
    public static AxPolicyModel getMergedPolicyModel(final AxPolicyModel leftPolicyModel,
        final AxPolicyModel rightPolicyModel, final boolean useLeftOnMatches, final boolean ignoreInvalidSource,
        final boolean failOnDuplicateKeys) throws ApexModelException {

        if (!ignoreInvalidSource) {
            validateModels(leftPolicyModel, "left");
            validateModels(rightPolicyModel, "right");
        }

        // The new policy model uses the favoured copy side as its base
        final AxPolicyModel mergedPolicyModel =
            (useLeftOnMatches ? new AxPolicyModel(leftPolicyModel) : new AxPolicyModel(rightPolicyModel));

        // The Compared to policy model is the unfavoured side
        final AxPolicyModel copyFromPolicyModel =
            (useLeftOnMatches ? new AxPolicyModel(rightPolicyModel) : new AxPolicyModel(leftPolicyModel));

        Map<AxArtifactKey, AxKeyInfo> mergedKeyInfoMap = mergedPolicyModel.getKeyInformation().getKeyInfoMap();
        Map<AxArtifactKey, AxContextSchema> mergedSchemasMap = mergedPolicyModel.getSchemas().getSchemasMap();
        Map<AxArtifactKey, AxEvent> mergedEventMap = mergedPolicyModel.getEvents().getEventMap();
        Map<AxArtifactKey, AxContextAlbum> mergedAlbumsMap = mergedPolicyModel.getAlbums().getAlbumsMap();
        Map<AxArtifactKey, AxTask> mergedTaskMap = mergedPolicyModel.getTasks().getTaskMap();
        Map<AxArtifactKey, AxPolicy> mergedPolicyMap = mergedPolicyModel.getPolicies().getPolicyMap();

        Map<AxArtifactKey, AxKeyInfo> copyOverKeyInfoMap = copyFromPolicyModel.getKeyInformation().getKeyInfoMap();
        Map<AxArtifactKey, AxContextSchema> copyOverSchemasMap = copyFromPolicyModel.getSchemas().getSchemasMap();
        Map<AxArtifactKey, AxEvent> copyOverEventMap = copyFromPolicyModel.getEvents().getEventMap();
        Map<AxArtifactKey, AxContextAlbum> copyOverAlbumsMap = copyFromPolicyModel.getAlbums().getAlbumsMap();
        Map<AxArtifactKey, AxTask> copyOverTaskMap = copyFromPolicyModel.getTasks().getTaskMap();
        Map<AxArtifactKey, AxPolicy> copyOverPolicyMap = copyFromPolicyModel.getPolicies().getPolicyMap();

        if (failOnDuplicateKeys) {
            StringBuilder errorMessage = new StringBuilder();
            checkForDuplicateItem(mergedSchemasMap, copyOverSchemasMap, errorMessage, "schema");
            checkForDuplicateItem(mergedEventMap, copyOverEventMap, errorMessage, "event");
            checkForDuplicateItem(mergedAlbumsMap, copyOverAlbumsMap, errorMessage, "album");
            checkForDuplicateItem(mergedTaskMap, copyOverTaskMap, errorMessage, "task");
            checkForDuplicateItem(mergedPolicyMap, copyOverPolicyMap, errorMessage, "policy");
            if (errorMessage.length() > 0) {
                throw new ApexModelException(errorMessage.toString());
            }
        } else {
            //  Remove entries that already exist
            copyOverKeyInfoMap.keySet().removeIf(mergedKeyInfoMap::containsKey);
            copyOverSchemasMap.keySet().removeIf(mergedSchemasMap::containsKey);
            copyOverEventMap.keySet().removeIf(mergedEventMap::containsKey);
            copyOverAlbumsMap.keySet().removeIf(mergedAlbumsMap::containsKey);
            copyOverTaskMap.keySet().removeIf(mergedTaskMap::containsKey);
            copyOverPolicyMap.keySet().removeIf(mergedPolicyMap::containsKey);
        }
        // Now add all the concepts that must be copied over
        mergedKeyInfoMap.putAll(copyOverKeyInfoMap);
        mergedSchemasMap.putAll(copyOverSchemasMap);
        mergedEventMap.putAll(copyOverEventMap);
        mergedAlbumsMap.putAll(copyOverAlbumsMap);
        mergedTaskMap.putAll(copyOverTaskMap);
        mergedPolicyMap.putAll(copyOverPolicyMap);

        // That's it, return the model
        return mergedPolicyModel;
    }

    /**
     * Method to check for duplicate items.
     *
     * @param <V>  the concept type
     * @param mergedItemsMap the map to which items are copied
     * @param copyOverItemsMap the map from where items are copied
     * @param errorMessage error message in case of any duplicate concepts
     * @param itemType the type of concept to specify distinguished error messages
     */
    public static <V> void checkForDuplicateItem(Map<AxArtifactKey, V> mergedItemsMap,
        Map<AxArtifactKey, V> copyOverItemsMap, StringBuilder errorMessage, String itemType) {
        for (Entry<AxArtifactKey, V> entry : copyOverItemsMap.entrySet()) {
            V item = mergedItemsMap.get(entry.getKey());
            // same item with different definitions cannot occur in multiple policies
            if (null != item) {
                if (item.equals(entry.getValue())) {
                    LOGGER.info("Same {} - {} is used by multiple policies.", itemType, entry.getKey().getId());
                } else {
                    errorMessage.append("\n Same " + itemType + " - ").append(entry.getKey().getId())
                        .append(" with different definitions used in different policies");
                }
            }
        }
    }

    private static void validateModels(AxPolicyModel policyModel, String position) throws ApexModelException {
        // Validate the model
        final AxValidationResult validationResult = new AxValidationResult();
        policyModel.validate(validationResult);
        if (!validationResult.isValid()) {
            String message = position + " model is invalid: " + validationResult.toString();
            LOGGER.warn(message);
            throw new ApexModelException(message);
        }
    }
}
