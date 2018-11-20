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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class creates statistics for the event generator's current status.
 */
public class EventGeneratorStats {
    private final EventBatchStats totalStats;
    private final List<EventBatchStats> batchStatsList = new ArrayList<>();

    /**
     * Create the statistics using the current batch map.
     * @param batchMap the batch map to use
     */
    public EventGeneratorStats(final Map<Integer, EventBatch> batchMap) {
        for (EventBatch eventBatch: batchMap.values()) {
            batchStatsList.add(new EventBatchStats(eventBatch));
        }

        totalStats = new EventBatchStats(batchStatsList);
    }

    /**
     * Get the batch statistics as a JSON string.
     * @return the statistics as a JSON string
     */
    public String getStatsAsJsonString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    public EventBatchStats getTotalStats() {
        return totalStats;
    }
}
