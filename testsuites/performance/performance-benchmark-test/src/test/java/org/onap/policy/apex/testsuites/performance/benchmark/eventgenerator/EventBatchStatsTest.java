/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test the EventBatchStats class.
 */
class EventBatchStatsTest {

    @Test
    void test() {
        EventBatchStats stats = new EventBatchStats(new EventBatch(1, "Label"));
        assertNotNull(stats);

        assertTrue(stats.getBatchNumber() >= 0);
        assertEquals(1, stats.getBatchSize());
        assertEquals("Label", stats.getApexClient());

        List<EventBatchStats> statsList = new ArrayList<>();
        statsList.add(stats);

        EventBatchStats totalStats = new EventBatchStats(statsList);
        assertEquals(stats.getBatchSize(), totalStats.getBatchSize());

        List<EventBatchStats> emptyStatsList = new ArrayList<>();
        EventBatchStats emptyStats = new EventBatchStats(emptyStatsList);
        assertEquals(0, emptyStats.getBatchSize());
    }
}
