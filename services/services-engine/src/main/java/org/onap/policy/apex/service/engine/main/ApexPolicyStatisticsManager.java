/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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

import io.prometheus.client.Counter;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NoArgsConstructor;
import org.onap.policy.common.utils.services.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class ApexPolicyStatisticsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexPolicyStatisticsManager.class);

    static final Counter POLICY_DEPLOY_REQUESTS_COUNTER = Counter.build()
            .name("policies_deploy_requests_total")
            .help("Total number of TOSCA policies deploy requests.").register();
    static final Counter POLICY_DEPLOY_REQUESTS_SUCCESS_COUNTER = Counter.build()
            .name("policies_deploy_requests_success")
            .help("Total number of TOSCA policies deploy requests that succeeded.").register();
    static final Counter POLICY_DEPLOY_REQUESTS_FAILED_COUNTER = Counter.build()
            .name("policies_deploy_requests_failed")
            .help("Total number of TOSCA policies deploy requests that failed.").register();
    static final Counter POLICY_UNDEPLOY_REQUESTS_COUNTER = Counter.build()
            .name("policies_undeploy_requests_total").help("Total number of TOSCA policies undeploy requests.")
            .register();
    static final Counter POLICY_UNDEPLOY_REQUESTS_SUCCESS_COUNTER = Counter.build()
            .name("policies_undeploy_requests_success")
            .help("Total number of TOSCA policies undeploy requests that succeeded.").register();
    static final Counter POLICY_UNDEPLOY_REQUESTS_FAILED_COUNTER = Counter.build()
            .name("policies_undeploy_requests_failed")
            .help("Total number of TOSCA policies undeploy requests that failed.").register();

    public static final String REG_APEX_PDP_POLICY_COUNTER = "object:pdp/statistics/policy/counter";

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
        this.policyDeployCount.incrementAndGet();
        POLICY_DEPLOY_REQUESTS_COUNTER.inc();
        if (!isSuccessful) {
            this.policyDeployFailCount.incrementAndGet();
            POLICY_DEPLOY_REQUESTS_FAILED_COUNTER.inc();
        } else {
            this.policyDeploySuccessCount.incrementAndGet();
            POLICY_DEPLOY_REQUESTS_SUCCESS_COUNTER.inc();
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
        this.policyUndeployCount.incrementAndGet();
        POLICY_UNDEPLOY_REQUESTS_COUNTER.inc();
        if (isSuccessful) {
            this.policyUndeploySuccessCount.incrementAndGet();
            POLICY_UNDEPLOY_REQUESTS_SUCCESS_COUNTER.inc();
        } else {
            this.policyUndeployFailCount.incrementAndGet();
            POLICY_UNDEPLOY_REQUESTS_FAILED_COUNTER.inc();
        }
    }

    public long getPolicyDeployCount() {
        return Double.valueOf(POLICY_DEPLOY_REQUESTS_COUNTER.get()).longValue();
    }

    public long getPolicyDeployFailCount() {
        return Double.valueOf(POLICY_DEPLOY_REQUESTS_FAILED_COUNTER.get()).longValue();
    }

    public long getPolicyDeploySuccessCount() {
        return Double.valueOf(POLICY_DEPLOY_REQUESTS_SUCCESS_COUNTER.get()).longValue();
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
        return Double.valueOf(POLICY_UNDEPLOY_REQUESTS_COUNTER.get()).longValue();
    }

    public long getPolicyUndeploySuccessCount() {
        return Double.valueOf(POLICY_UNDEPLOY_REQUESTS_SUCCESS_COUNTER.get()).longValue();
    }

    public long getPolicyUndeployFailCount() {
        return Double.valueOf(POLICY_UNDEPLOY_REQUESTS_FAILED_COUNTER.get()).longValue();
    }
}