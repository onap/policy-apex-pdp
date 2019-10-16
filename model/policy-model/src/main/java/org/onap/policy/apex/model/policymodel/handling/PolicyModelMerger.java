/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import java.util.Set;
import java.util.TreeSet;
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
    private PolicyModelMerger() {}

    /**
     * Get a merged policy model with information from two policy models merged into a larger policy
     * model.
     *
     * @param leftPolicyModel the source Apex Model
     * @param rightPolicyModel the policies to include in sub policy model
     * @param useLeftOnMatches if true, uses concepts from the left model if concepts with common
     *        keys are found, if false it uses the concepts from the right model
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
     * @return the new Destination Model
     * @throws ApexModelException on model transfer errors
     */
    public static AxPolicyModel getMergedPolicyModel(final AxPolicyModel leftPolicyModel,
        final AxPolicyModel rightPolicyModel, final boolean useLeftOnMatches, final boolean ignoreInvalidSource,
        final boolean failOnDuplicateKeys) throws ApexModelException {

        if (!ignoreInvalidSource) {
            validateModels(leftPolicyModel, true);
            validateModels(rightPolicyModel, false);
        }

        // The new policy model uses the favoured copy side as its base
        final AxPolicyModel mergedPolicyModel = (useLeftOnMatches ? new AxPolicyModel(leftPolicyModel)
                : new AxPolicyModel(rightPolicyModel));

        // The Compared to policy model is the unfavoured side
        final AxPolicyModel copyFromPolicyModel = (useLeftOnMatches ? rightPolicyModel : leftPolicyModel);

        //  Get the keys to copy over
        final Set<AxArtifactKey> copyOverKeyInfoKeys =
                new TreeSet<>(copyFromPolicyModel.getKeyInformation().getKeyInfoMap().keySet());
        final Set<AxArtifactKey> copyOverContextSchemaKeys =
                new TreeSet<>(copyFromPolicyModel.getSchemas().getSchemasMap().keySet());
        final Set<AxArtifactKey> copyOverEventKeys =
                new TreeSet<>(copyFromPolicyModel.getEvents().getEventMap().keySet());
        final Set<AxArtifactKey> copyOverContextAlbumKeys =
                new TreeSet<>(copyFromPolicyModel.getAlbums().getAlbumsMap().keySet());
        final Set<AxArtifactKey> copyOverTaskKeys = new TreeSet<>(copyFromPolicyModel.getTasks().getTaskMap().keySet());
        final Set<AxArtifactKey> copyOverPolicyKeys =
                new TreeSet<>(copyFromPolicyModel.getPolicies().getPolicyMap().keySet());

        Map<AxArtifactKey, AxKeyInfo> mergedKeyInfoMap = mergedPolicyModel.getKeyInformation().getKeyInfoMap();
        Map<AxArtifactKey, AxContextSchema> mergedSchemasMap = mergedPolicyModel.getSchemas().getSchemasMap();
        Map<AxArtifactKey, AxEvent> mergedEventMap = mergedPolicyModel.getEvents().getEventMap();
        Map<AxArtifactKey, AxContextAlbum> mergedAlbumsMap = mergedPolicyModel.getAlbums().getAlbumsMap();
        Map<AxArtifactKey, AxTask> mergedTaskMap = mergedPolicyModel.getTasks().getTaskMap();
        Map<AxArtifactKey, AxPolicy> mergedPolicyMap = mergedPolicyModel.getPolicies().getPolicyMap();

        if (failOnDuplicateKeys) {
            checkForDuplicates(mergedPolicyModel, copyFromPolicyModel);
        } else {
            //  Remove keys that already exist
            copyOverKeyInfoKeys.removeAll(mergedKeyInfoMap.keySet());
            copyOverContextSchemaKeys.removeAll(mergedSchemasMap.keySet());
            copyOverEventKeys.removeAll(mergedEventMap.keySet());
            copyOverContextAlbumKeys.removeAll(mergedAlbumsMap.keySet());
            copyOverTaskKeys.removeAll(mergedTaskMap.keySet());
            copyOverPolicyKeys.removeAll(mergedPolicyMap.keySet());
        }
        // Now add all the concepts that must be copied over
        for (final AxArtifactKey keyInfoKey : copyOverKeyInfoKeys) {
            mergedKeyInfoMap.put(keyInfoKey,
                    copyFromPolicyModel.getKeyInformation().getKeyInfoMap().get(keyInfoKey));
        }
        for (final AxArtifactKey contextSchemaKey : copyOverContextSchemaKeys) {
            mergedSchemasMap.put(contextSchemaKey,
                    copyFromPolicyModel.getSchemas().getSchemasMap().get(contextSchemaKey));
        }
        for (final AxArtifactKey eventKey : copyOverEventKeys) {
            mergedEventMap.put(eventKey,
                    copyFromPolicyModel.getEvents().getEventMap().get(eventKey));
        }
        for (final AxArtifactKey contextAlbumKey : copyOverContextAlbumKeys) {
            mergedAlbumsMap.put(contextAlbumKey,
                    copyFromPolicyModel.getAlbums().getAlbumsMap().get(contextAlbumKey));
        }
        for (final AxArtifactKey taskKey : copyOverTaskKeys) {
            mergedTaskMap.put(taskKey,
                    copyFromPolicyModel.getTasks().getTaskMap().get(taskKey));
        }
        for (final AxArtifactKey policyKey : copyOverPolicyKeys) {
            mergedPolicyMap.put(policyKey,
                    copyFromPolicyModel.getPolicies().getPolicyMap().get(policyKey));
        }

        // That's it, return the model
        return mergedPolicyModel;
    }

    private static void checkForDuplicates(final AxPolicyModel mergedPolicyModel,
        final AxPolicyModel copyFromPolicyModel)
        throws ApexModelException {
        StringBuilder errorMessage = new StringBuilder();
        checkForDuplicateContextSchema(mergedPolicyModel, copyFromPolicyModel, errorMessage);
        for ( AxArtifactKey key : copyFromPolicyModel.getEvents().getEventMap().keySet()) {
            if (mergedPolicyModel.getEvents().getEventMap().containsKey(key)) {
                errorMessage.append("\n Duplicate event found - ").append(key.getId());
            }
        }
        checkForDuplicateContextAlbum(mergedPolicyModel, copyFromPolicyModel, errorMessage);
        for (AxArtifactKey key : copyFromPolicyModel.getTasks().getTaskMap().keySet()) {
            if (mergedPolicyModel.getTasks().getTaskMap().containsKey(key)) {
                errorMessage.append("\n Duplicate task found - ").append(key.getId());
            }
        }
        for (AxArtifactKey key : copyFromPolicyModel.getPolicies().getPolicyMap().keySet()) {
            if (mergedPolicyModel.getPolicies().getPolicyMap().containsKey(key)) {
                errorMessage.append("\n Duplicate policy found - ").append(key.getId());
            }
        }
        if (errorMessage.length() > 0) {
            throw new ApexModelException(errorMessage.toString());
        }
    }

    private static void checkForDuplicateContextAlbum(final AxPolicyModel mergedPolicyModel,
        final AxPolicyModel copyFromPolicyModel, StringBuilder errorMessage) {
        for (Entry<AxArtifactKey, AxContextAlbum> entry : copyFromPolicyModel.getAlbums().getAlbumsMap().entrySet()) {
            AxContextAlbum schema = mergedPolicyModel.getAlbums().getAlbumsMap().get(entry.getKey());
            // same context schema name with different definitions cannot occur in multiple policies
            if (null != schema) {
                if (schema.equals(entry.getValue())) {
                    LOGGER
                        .info("Same contextAlbum  - " + entry.getKey().getId() + "is being used by multiple policies.");
                } else {
                    errorMessage.append("\n Same context contextAlbum  - ").append(entry.getKey().getId())
                        .append(" with different definitions used in different policies");
                }
            }
        }
    }

    private static void checkForDuplicateContextSchema(final AxPolicyModel mergedPolicyModel,
        final AxPolicyModel copyFromPolicyModel, StringBuilder errorMessage) {
        for (Entry<AxArtifactKey, AxContextSchema> entry : copyFromPolicyModel.getSchemas().getSchemasMap()
            .entrySet()) {
            AxContextSchema schema = mergedPolicyModel.getSchemas().getSchemasMap().get(entry.getKey());
            // same context schema name with different definitions cannot occur in multiple policies
            if (null != schema) {
                if (schema.equals(entry.getValue())) {
                    LOGGER
                        .info("Same contextSchema - " + entry.getKey().getId() + "is being used by multiple policies.");
                } else {
                    errorMessage.append("\n Same context schema - ").append(entry.getKey().getId())
                        .append(" with different definitions used in different policies");
                }
            }
        }
    }

    private static void validateModels(AxPolicyModel policyModel, boolean isLeft) throws ApexModelException {
        String position = isLeft ? "left" : "right";
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
