/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024-2025 Nordix Foundation.
 *  Modifications Copyright (C) 2021-2022 Bell Canada. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.utils.resources.PrometheusUtils;

class ApexPolicyStatisticsManagerTest {

    private ApexPolicyStatisticsManager statisticsManager;

    /**
     * Starts the statisticsManager object for tests.
     */
    @BeforeEach
    void setup() {
        statisticsManager = new ApexPolicyStatisticsManager();
    }

    @Test
    void testUpdatePolicyDeployCounter() {
        statisticsManager.updatePolicyDeployCounter(false);
        assertDeploys(1, 0, 1);

        statisticsManager.updatePolicyDeployCounter(true);
        statisticsManager.updatePolicyDeployCounter(true);
        assertDeploys(3, 2, 1);
        checkDeploymentsMetrics("deploy");
    }

    @Test
    void testUpdatePolicyExecutedCounter() {
        statisticsManager.updatePolicyExecutedCounter(true);
        assertExecuted(1, 1, 0);

        statisticsManager.updatePolicyExecutedCounter(false);
        assertExecuted(2, 1, 1);
    }

    @Test
    void testUpdatePolicyUndeployCounter() {
        statisticsManager.updatePolicyUndeployCounter(false);
        assertUndeploys(1, 0, 1);

        statisticsManager.updatePolicyUndeployCounter(true);
        assertUndeploys(2, 1, 1);
        checkDeploymentsMetrics("undeploy");
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

    private void checkDeploymentsMetrics(String operation) {
        PrometheusRegistry registry = new PrometheusRegistry();
        Counter deploymentsCounter = Counter.builder()
            .name("pdpa_policy_deployments_total")
            .help("Total number of policy deployments")
            .labelNames("operation", "status")
            .register(registry);

        String[] statuses = {"TOTAL", "SUCCESS", "FAIL"};
        for (String status : statuses) {
            deploymentsCounter.labelValues(operation, status).inc(getCountForStatus(operation, status));
        }

        double totalCount = deploymentsCounter.labelValues(operation, "TOTAL").get();
        double successCount = deploymentsCounter.labelValues(operation, "SUCCESS").get();
        double failCount = deploymentsCounter.labelValues(operation, "FAIL").get();

        if (PrometheusUtils.DEPLOY_OPERATION.equals(operation)) {
            assertEquals(statisticsManager.getPolicyDeployCount(), (int) totalCount);
            assertEquals(statisticsManager.getPolicyDeploySuccessCount(), (int) successCount);
            assertEquals(statisticsManager.getPolicyDeployFailCount(), (int) failCount);
        } else if (PrometheusUtils.UNDEPLOY_OPERATION.equals(operation)) {
            assertEquals(statisticsManager.getPolicyUndeployCount(), (int) totalCount);
            assertEquals(statisticsManager.getPolicyUndeploySuccessCount(), (int) successCount);
            assertEquals(statisticsManager.getPolicyUndeployFailCount(), (int) failCount);
        }
    }

    private int getCountForStatus(String operation, String status) {
        if (PrometheusUtils.DEPLOY_OPERATION.equals(operation)) {
            switch (status) {
                case "TOTAL": return (int) statisticsManager.getPolicyDeployCount();
                case "SUCCESS": return (int) statisticsManager.getPolicyDeploySuccessCount();
                case "FAIL": return (int) statisticsManager.getPolicyDeployFailCount();
                default: return 0;
            }
        } else if (PrometheusUtils.UNDEPLOY_OPERATION.equals(operation)) {
            switch (status) {
                case "TOTAL": return (int) statisticsManager.getPolicyUndeployCount();
                case "SUCCESS": return (int) statisticsManager.getPolicyUndeploySuccessCount();
                case "FAIL": return (int) statisticsManager.getPolicyUndeployFailCount();
                default: return 0;
            }
        }
        return 0;
    }
}