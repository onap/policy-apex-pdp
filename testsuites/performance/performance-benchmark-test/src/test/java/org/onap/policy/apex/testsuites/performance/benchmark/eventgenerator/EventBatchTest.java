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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.InputEvent;

/**
 * Test the EventBatch class.
 *
 */
public class EventBatchTest {

    @Test
    public void testEventBatch() {
        EventBatch batch = new EventBatch(1, "TheApexClient");
        assertNotNull(batch);

        assertEquals("\"nameSpace\": \"org.onap.policy.apex.sample.events\"", batch.getBatchAsJsonString().substring(4, 53));

        EventBatchStats stats = batch.getStats();
        assertEquals(1, stats.getBatchSize());

        InputEvent ie = batch.getInputEvent(0);
        assertEquals("org.onap.policy.apex.sample.events",ie.getNameSpace());
    }
}
