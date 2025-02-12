/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021, 2025 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
 *  Modifications Copyright (C) 2021 Bell Canada Intellectual Property. All rights reserved.
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

import io.prometheus.metrics.core.metrics.Counter;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NoArgsConstructor;
import org.onap.policy.common.utils.resources.PrometheusUtils;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.enums.PdpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class ApexPolicyStatisticsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexPolicyStatisticsManager.class);

    static final Counter POLICY_DEPLOYMENTS_COUNTER =
        Counter.builder()
            .name(PrometheusUtils.PdpType.PDPA.getNamespace() + "_" + PrometheusUtils.POLICY_DEPLOYMENTS_METRIC)
            .labelNames(PrometheusUtils.OPERATION_METRIC_LABEL, PrometheusUtils.STATUS_METRIC_LABEL)
            .help(PrometheusUtils.POLICY_DEPLOYMENT_HELP).register();

    public static final String REG_APEX_PDP_POLICY_COUNTER = "object:pdp/statistics/policy/counter";
    private static final String PROMETHEUS_TOTAL_LABEL_VALUE = "TOTAL";

    private final AtomicLong policyDeployCount = new AtomicLong(0);
    private final AtomicLong policyDeploySuccessCount = new AtomicLong(0);
    private final AtomicLong policyDeployFailCount = new AtomicLong(0);
    private final AtomicLong policyUndeployCount = new AtomicLong(0);
    private final AtomicLong policyUndeploySuccessCount = new AtomicLong(0);
    private final AtomicLong policyUndeployFailCount = new AtomicLong(0);
    private final AtomicLong policyExecutedCount = new AtomicLong(0);
    private final AtomicLong policyExecutedSuccessCount = new AtomicLong(0);
    private final AtomicLong policyExecutedFailCount = new AtomicLong(0);

    /**
     * To get the ApexPolicyStatisticsManager in Registry.
     *
     * @return ApexPolicyStatisticsManager The obj in Registry.
     */
    public static ApexPolicyStatisticsManager getInstanceFromRegistry() {
        ApexPolicyStatisticsManager instance = null;
        try {
            instance = Registry.get(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER);
        } catch (IllegalArgumentException e) {
            LOGGER.debug("ApexPolicyStatisticsManager is not registered yet");
        }
        return instance;
    }

    /**
     * Update the policy deploy count.
     */
    public void updatePolicyDeployCounter(final boolean isSuccessful) {
        POLICY_DEPLOYMENTS_COUNTER.labelValues(PrometheusUtils.DEPLOY_OPERATION, PROMETHEUS_TOTAL_LABEL_VALUE).inc();
        this.policyDeployCount.incrementAndGet();
        if (!isSuccessful) {
            POLICY_DEPLOYMENTS_COUNTER.labelValues(PrometheusUtils.DEPLOY_OPERATION,
                PdpResponseStatus.FAIL.name()).inc();
            this.policyDeployFailCount.incrementAndGet();
        } else {
            POLICY_DEPLOYMENTS_COUNTER.labelValues(PrometheusUtils.DEPLOY_OPERATION,
                PdpResponseStatus.SUCCESS.name()).inc();
            this.policyDeploySuccessCount.incrementAndGet();
        }
    }

    /**
     * Update the policy executed count.
     */
    public void updatePolicyExecutedCounter(final boolean isSuccessful) {
        this.policyExecutedCount.incrementAndGet();
        if (isSuccessful) {
            this.policyExecutedSuccessCount.incrementAndGet();
        } else {
            this.policyExecutedFailCount.incrementAndGet();
        }
    }

    /**
     * Update the policy undeploy count.
     */
    public void updatePolicyUndeployCounter(final boolean isSuccessful) {
        POLICY_DEPLOYMENTS_COUNTER.labelValues(PrometheusUtils.UNDEPLOY_OPERATION, PROMETHEUS_TOTAL_LABEL_VALUE).inc();
        this.policyUndeployCount.incrementAndGet();
        if (isSuccessful) {
            POLICY_DEPLOYMENTS_COUNTER.labelValues(PrometheusUtils.UNDEPLOY_OPERATION,
                PdpResponseStatus.SUCCESS.name()).inc();
            this.policyUndeploySuccessCount.incrementAndGet();
        } else {
            POLICY_DEPLOYMENTS_COUNTER.labelValues(PrometheusUtils.UNDEPLOY_OPERATION,
                PdpResponseStatus.FAIL.name()).inc();
            this.policyUndeployFailCount.incrementAndGet();
        }
    }

    public long getPolicyDeployCount() {
        return policyDeployCount.get();
    }

    public long getPolicyDeployFailCount() {
        return policyDeployFailCount.get();
    }

    public long getPolicyDeploySuccessCount() {
        return policyDeploySuccessCount.get();
    }

    public long getPolicyExecutedCount() {
        return policyExecutedCount.get();
    }

    public long getPolicyExecutedSuccessCount() {
        return policyExecutedSuccessCount.get();
    }

    public long getPolicyExecutedFailCount() {
        return policyExecutedFailCount.get();
    }

    public long getPolicyUndeployCount() {
        return policyUndeployCount.get();
    }

    public long getPolicyUndeploySuccessCount() {
        return policyUndeploySuccessCount.get();
    }

    public long getPolicyUndeployFailCount() {
        return policyUndeployFailCount.get();
    }
}