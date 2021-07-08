/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.main;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ApexPolicyStatisticsManagerTest {

    private ApexPolicyStatisticsManager statisticsManager;

    /**
     * Starts the statisticsManager object for tests.
     */
    @Before
    public void setup() {
        statisticsManager = new ApexPolicyStatisticsManager();
    }

    @Test
    public void testUpdatePolicyDeployCounter() {
        statisticsManager.updatePolicyDeployCounter(false);
        assertDeploys(1, 0, 1);

        statisticsManager.updatePolicyDeployCounter(true);
        statisticsManager.updatePolicyDeployCounter(true);
        assertDeploys(3, 2, 1);
    }

    @Test
    public void testUpdatePolicyExecutedCounter() {
        statisticsManager.updatePolicyExecutedCounter(true);
        assertExecuted(1, 1, 0);

        statisticsManager.updatePolicyExecutedCounter(false);
        assertExecuted(2, 1, 1);
    }

    @Test
    public void testUpdatePolicyUndeployCounter() {
        statisticsManager.updatePolicyUndeployCounter(false);
        assertUndeploys(1, 0, 1);

        statisticsManager.updatePolicyUndeployCounter(true);
        assertUndeploys(2, 1, 1);
    }

    @Test
    public void testResetAllStatistics() {
        statisticsManager.updatePolicyDeployCounter(true);
        statisticsManager.updatePolicyDeployCounter(true);
        statisticsManager.updatePolicyDeployCounter(false);
        statisticsManager.updatePolicyUndeployCounter(false);
        statisticsManager.updatePolicyExecutedCounter(true);

        assertDeploys(3, 2, 1);
        assertUndeploys(1, 0, 1);
        assertExecuted(1, 1, 0);

        statisticsManager.resetAllStatistics();

        assertDeploys(0, 0, 0);
        assertUndeploys(0, 0, 0);
        assertExecuted(0, 0, 0);

    }

    private void assertDeploys(long count, long success, long fail) {
        assertEquals(count, statisticsManager.getPolicyDeployCount());
        assertEquals(success, statisticsManager.getPolicyDeploySuccessCount());
        assertEquals(fail, statisticsManager.getPolicyDeployFailCount());
    }

    private void assertUndeploys(long count, long success, long fail) {
        assertEquals(count, statisticsManager.getPolicyUndeployCount());
        assertEquals(success, statisticsManager.getPolicyUndeploySuccessCount());
        assertEquals(fail, statisticsManager.getPolicyUndeployFailCount());
    }

    private void assertExecuted(long count, long success, long fail) {
        assertEquals(count, statisticsManager.getPolicyExecutedCount());
        assertEquals(success, statisticsManager.getPolicyExecutedSuccessCount());
        assertEquals(fail, statisticsManager.getPolicyExecutedFailCount());
    }

}
