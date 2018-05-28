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

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.utilities.comparison.KeyComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyDifference;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapComparer;
import org.onap.policy.apex.model.utilities.comparison.KeyedMapDifference;

/**
 * This class compares two policy models {@link AxPolicyModel} and holds the result of that
 * comparison. It compares policy models on their keys, their context schema differences, their
 * event differences, their context album differences, their task differences, their policy
 * differences, and their key information differences.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyModelComparer {
    // Comparison of policy model keys
    private final KeyDifference<AxArtifactKey> policyModelsKeyDifference;

    // Comparison of context schemas
    private final KeyDifference<AxArtifactKey> contextSchemasKeyDifference;
    private KeyedMapDifference<AxArtifactKey, AxContextSchema> contextSchemaComparisonResult =
            new KeyedMapDifference<>();

    // Comparison of events
    private final KeyDifference<AxArtifactKey> eventsKeyDifference;
    private KeyedMapDifference<AxArtifactKey, AxEvent> eventComparisonResult = new KeyedMapDifference<>();

    // Comparison of context albums
    private final KeyDifference<AxArtifactKey> contextAlbumKeyDifference;
    private KeyedMapDifference<AxArtifactKey, AxContextAlbum> contextAlbumComparisonResult = new KeyedMapDifference<>();

    // Comparison of tasks
    private final KeyDifference<AxArtifactKey> tasksKeyDifference;
    private KeyedMapDifference<AxArtifactKey, AxTask> taskComparisonResult = new KeyedMapDifference<>();

    // Comparison of policies
    private final KeyDifference<AxArtifactKey> policiesKeyDifference;
    private KeyedMapDifference<AxArtifactKey, AxPolicy> policyComparisonResult = new KeyedMapDifference<>();

    // Comparison of key information
    private final KeyDifference<AxArtifactKey> keyInformationKeyDifference;
    private KeyedMapDifference<AxArtifactKey, AxKeyInfo> keyInfoComparisonResult = new KeyedMapDifference<>();

    /**
     * The Constructor.
     *
     * @param left the left
     * @param right the right
     */
    public PolicyModelComparer(final AxPolicyModel left, final AxPolicyModel right) {
        // @formatter:off
        policyModelsKeyDifference   = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());
        contextSchemasKeyDifference = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());
        eventsKeyDifference         = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());
        contextAlbumKeyDifference   = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());
        tasksKeyDifference          = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());
        policiesKeyDifference       = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());
        keyInformationKeyDifference = new KeyComparer<AxArtifactKey>().compareKeys(left.getKey(), right.getKey());

        contextSchemaComparisonResult = new KeyedMapComparer<AxArtifactKey, AxContextSchema>().compareMaps(
                left.getSchemas().getSchemasMap(), right.getSchemas().getSchemasMap());
        eventComparisonResult         = new KeyedMapComparer<AxArtifactKey, AxEvent>().compareMaps(
                left.getEvents().getEventMap(), right.getEvents().getEventMap());
        contextAlbumComparisonResult  = new KeyedMapComparer<AxArtifactKey, AxContextAlbum>().compareMaps(
                left.getAlbums().getAlbumsMap(), right.getAlbums().getAlbumsMap());
        taskComparisonResult          = new KeyedMapComparer<AxArtifactKey, AxTask>().compareMaps(left.getTasks().getTaskMap(), right.getTasks().getTaskMap());
        policyComparisonResult        = new KeyedMapComparer<AxArtifactKey, AxPolicy>().compareMaps(
                left.getPolicies().getPolicyMap(), right.getPolicies().getPolicyMap());
        keyInfoComparisonResult       = new KeyedMapComparer<AxArtifactKey, AxKeyInfo>().compareMaps(
                left.getKeyInformation().getKeyInfoMap(), right.getKeyInformation().getKeyInfoMap());
        // @formatter:on
    }

    /**
     * Gets the difference between policy model keys on the two models.
     *
     * @return the difference between policy model keys
     */
    public KeyDifference<AxArtifactKey> getPolicyModelsKeyDifference() {
        return policyModelsKeyDifference;
    }

    /**
     * Gets the difference between context schema keys on the two models.
     *
     * @return the difference between context schema keys
     */
    public KeyDifference<AxArtifactKey> getContextSchemaKeyDifference() {
        return contextSchemasKeyDifference;
    }

    /**
     * Gets the difference between context schemas on the two models.
     *
     * @return the difference between context schemas
     */
    public KeyedMapDifference<AxArtifactKey, AxContextSchema> getContextSchemaComparisonResult() {
        return contextSchemaComparisonResult;
    }

    /**
     * Gets the difference between event keys on the two models.
     *
     * @return the difference between event keys
     */
    public KeyDifference<AxArtifactKey> getEventKeyDifference() {
        return eventsKeyDifference;
    }

    /**
     * Gets the difference between the events on the two models.
     *
     * @return the difference between the events
     */
    public KeyedMapDifference<AxArtifactKey, AxEvent> getEventComparisonResult() {
        return eventComparisonResult;
    }

    /**
     * Gets the difference between context album keys on the two models.
     *
     * @return the difference between context album keys
     */
    public KeyDifference<AxArtifactKey> getContextAlbumKeyDifference() {
        return contextAlbumKeyDifference;
    }

    /**
     * Gets the difference between the context albums on the two models.
     *
     * @return the difference between the context albums
     */
    public KeyedMapDifference<AxArtifactKey, AxContextAlbum> getContextAlbumComparisonResult() {
        return contextAlbumComparisonResult;
    }

    /**
     * Gets the difference between task keys on the two models.
     *
     * @return the difference between task keys
     */
    public KeyDifference<AxArtifactKey> getTaskKeyDifference() {
        return tasksKeyDifference;
    }

    /**
     * Gets the difference between the tasks on the two models.
     *
     * @return the difference between the tasks
     */
    public KeyedMapDifference<AxArtifactKey, AxTask> getTaskComparisonResult() {
        return taskComparisonResult;
    }

    /**
     * Gets the difference between policy keys on the two models.
     *
     * @return the difference between policy keys
     */
    public KeyDifference<AxArtifactKey> getPolicykeyDifference() {
        return policiesKeyDifference;
    }

    /**
     * Gets the difference between the policies on the two models.
     *
     * @return the difference between the policies
     */
    public KeyedMapDifference<AxArtifactKey, AxPolicy> getPolicyComparisonResult() {
        return policyComparisonResult;
    }

    /**
     * Gets the difference between key information keys on the two models.
     *
     * @return the difference between key information keys
     */
    public KeyDifference<AxArtifactKey> getKeyInformationKeyDifference() {
        return keyInformationKeyDifference;
    }

    /**
     * Gets the difference between the key information on the two models.
     *
     * @return the difference between the key information
     */
    public KeyedMapDifference<AxArtifactKey, AxKeyInfo> getKeyInfoComparisonResult() {
        return keyInfoComparisonResult;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return asString(true, true);
    }

    /**
     * As string.
     *
     * @param diffsOnly the diffs only
     * @param keysOnly the keys only
     * @return the string
     */
    public String asString(final boolean diffsOnly, final boolean keysOnly) {
        final StringBuilder builder = new StringBuilder();

        builder.append("****** policy map differences ******\n");
        builder.append(policyModelsKeyDifference.asString(diffsOnly));

        builder.append("*** context schema differences ***\n");
        builder.append(contextSchemasKeyDifference.asString(diffsOnly));
        builder.append(contextSchemaComparisonResult.asString(diffsOnly, keysOnly));

        builder.append("*** event differences ***\n");
        builder.append(eventsKeyDifference.asString(diffsOnly));
        builder.append(eventComparisonResult.asString(diffsOnly, keysOnly));

        builder.append("*** context album differences ***\n");
        builder.append(contextAlbumKeyDifference.asString(diffsOnly));
        builder.append(contextAlbumComparisonResult.asString(diffsOnly, keysOnly));

        builder.append("*** task differences ***\n");
        builder.append(tasksKeyDifference.asString(diffsOnly));
        builder.append(taskComparisonResult.asString(diffsOnly, keysOnly));

        builder.append("*** policy differences ***\n");
        builder.append(policiesKeyDifference.asString(diffsOnly));
        builder.append(policyComparisonResult.asString(diffsOnly, keysOnly));

        builder.append("*** key information differences ***\n");
        builder.append(keyInformationKeyDifference.asString(diffsOnly));
        builder.append(keyInfoComparisonResult.asString(diffsOnly, keysOnly));

        builder.append("***********************************\n");

        return builder.toString();
    }
}
