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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * This class finds and holds the usage of context schemas, context albums, events, and tasks by the policies in a
 * policy model.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyAnalysisResult {
    // Usage of context schemas
    private final Map<AxArtifactKey, Set<AxKey>> contextSchemaUsage = new TreeMap<>();

    // Usage of context maps
    private final Map<AxArtifactKey, Set<AxKey>> contextAlbumUsage = new TreeMap<>();

    // Usage of events
    private final Map<AxArtifactKey, Set<AxKey>> eventUsage = new TreeMap<>();

    // Usage of tasks
    private final Map<AxArtifactKey, Set<AxKey>> taskUsage = new TreeMap<>();

    /**
     * This constructor creates a {@link PolicyAnalysisResult} instance that holds maps that contain the usage of
     * context schemas, contxt albums, events, and tasks by all policies in a policy model.
     *
     * @param policyModel the policy model to analyse
     */
    public PolicyAnalysisResult(final AxPolicyModel policyModel) {
        for (final AxArtifactKey contextSchemaKey : policyModel.getSchemas().getSchemasMap().keySet()) {
            contextSchemaUsage.put(contextSchemaKey, new TreeSet<AxKey>());
        }

        for (final Entry<AxArtifactKey, AxContextAlbum> contextAlbumEntry : policyModel.getAlbums().getAlbumsMap()
                        .entrySet()) {
            contextAlbumUsage.put(contextAlbumEntry.getKey(), new TreeSet<AxKey>());
        }

        for (final AxArtifactKey eventKey : policyModel.getEvents().getEventMap().keySet()) {
            eventUsage.put(eventKey, new TreeSet<AxKey>());
        }

        for (final AxArtifactKey taskKey : policyModel.getTasks().getTaskMap().keySet()) {
            taskUsage.put(taskKey, new TreeSet<AxKey>());
        }
    }

    /**
     * Gets the context schemas used by policies in the policy model.
     *
     * @return the context schemas used by policies in the policy model
     */
    public Map<AxArtifactKey, Set<AxKey>> getContextSchemaUsage() {
        return contextSchemaUsage;
    }

    /**
     * Gets the context albums used by policies in the policy model.
     *
     * @return the context albums used by policies in the policy model
     */
    public Map<AxArtifactKey, Set<AxKey>> getContextAlbumUsage() {
        return contextAlbumUsage;
    }

    /**
     * Gets the events used by policies in the policy model.
     *
     * @return the events used by policies in the policy model
     */
    public Map<AxArtifactKey, Set<AxKey>> getEventUsage() {
        return eventUsage;
    }

    /**
     * Gets the tasks used by policies in the policy model.
     *
     * @return the tasks used by policies in the policy model
     */
    public Map<AxArtifactKey, Set<AxKey>> getTaskUsage() {
        return taskUsage;
    }

    /**
     * Gets the context schemas used by policies in the policy model.
     *
     * @return the context schemas used by policies in the policy model
     */
    public Set<AxArtifactKey> getUsedContextSchemas() {
        return getUsedKeySet(contextSchemaUsage);
    }

    /**
     * Gets the context albums used by policies in the policy model.
     *
     * @return the context albums used by policies in the policy model
     */
    public Set<AxArtifactKey> getUsedContextAlbums() {
        return getUsedKeySet(contextAlbumUsage);
    }

    /**
     * Gets the events used by policies in the policy model.
     *
     * @return the events used by policies in the policy model
     */
    public Set<AxArtifactKey> getUsedEvents() {
        return getUsedKeySet(eventUsage);
    }

    /**
     * Gets the tasks used by policies in the policy model.
     *
     * @return the tasks used by policies in the policy model
     */
    public Set<AxArtifactKey> getUsedTasks() {
        return getUsedKeySet(taskUsage);
    }

    /**
     * Gets the context schemas in the policy model that were not used by any policies in the policy model.
     *
     * @return the unused context schemas
     */
    public Set<AxArtifactKey> getUnusedContextSchemas() {
        return getUnusedKeySet(contextSchemaUsage);
    }

    /**
     * Gets the context albums in the policy model that were not used by any policies in the policy model.
     *
     * @return the unused context albums
     */
    public Set<AxArtifactKey> getUnusedContextAlbums() {
        return getUnusedKeySet(contextAlbumUsage);
    }

    /**
     * Gets the events in the policy model that were not used by any policies in the policy model.
     *
     * @return the unused events
     */
    public Set<AxArtifactKey> getUnusedEvents() {
        return getUnusedKeySet(eventUsage);
    }

    /**
     * Gets the tasks in the policy model that were not used by any policies in the policy model.
     *
     * @return the unused tasks
     */
    public Set<AxArtifactKey> getUnusedTasks() {
        return getUnusedKeySet(taskUsage);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append(getUsageMapString("Context Schema usage", contextSchemaUsage));
        builder.append(getUsageMapString("Context Album usage", contextAlbumUsage));
        builder.append(getUsageMapString("Event usage", eventUsage));
        builder.append(getUsageMapString("Task usage", taskUsage));

        return builder.toString();
    }

    /**
     * Gets the usage map string.
     *
     * @param header the header
     * @param usageMap the usage map
     * @return the usage map string
     */
    private String getUsageMapString(final String header, final Map<? extends AxKey, Set<AxKey>> usageMap) {
        final StringBuilder builder = new StringBuilder();

        builder.append(header);
        builder.append('\n');
        for (final Entry<? extends AxKey, Set<AxKey>> usageEntry : usageMap.entrySet()) {
            builder.append(" ");
            builder.append(usageEntry.getKey().getId());
            if (usageEntry.getValue().isEmpty()) {
                builder.append(" (unused)\n");
                continue;
            }

            builder.append('\n');
            for (final AxKey usageKey : usageEntry.getValue()) {
                builder.append("  ");
                builder.append(usageKey.getId());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * Gets the used key set.
     *
     * @param usageMap the usage map
     * @return the used key set
     */
    private Set<AxArtifactKey> getUsedKeySet(final Map<AxArtifactKey, Set<AxKey>> usageMap) {
        final Set<AxArtifactKey> usedKeySet = new TreeSet<>();

        for (final Entry<AxArtifactKey, Set<AxKey>> usageEntry : usageMap.entrySet()) {
            if (!usageEntry.getValue().isEmpty()) {
                usedKeySet.add(usageEntry.getKey());
            }
        }

        return usedKeySet;
    }

    /**
     * Gets the unused key set.
     *
     * @param usageMap the usage map
     * @return the unused key set
     */
    private Set<AxArtifactKey> getUnusedKeySet(final Map<AxArtifactKey, Set<AxKey>> usageMap) {
        final Set<AxArtifactKey> usedKeySet = new TreeSet<>();

        for (final Entry<AxArtifactKey, Set<AxKey>> usageEntry : usageMap.entrySet()) {
            if (usageEntry.getValue().isEmpty()) {
                usedKeySet.add(usageEntry.getKey());
            }
        }

        return usedKeySet;
    }
}
