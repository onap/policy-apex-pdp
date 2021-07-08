/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020-2021 Nordix Foundation.
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

import java.util.concurrent.atomic.AtomicLong;
import org.onap.policy.common.utils.services.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApexPolicyStatisticsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApexPolicyStatisticsManager.class);
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
     * Constructs the object.
     */
    public ApexPolicyStatisticsManager() {
        super();
    }

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
        this.updatepPolicyDeployCount();
        if (!isSuccessful) {
            this.updatePolicyDeployFailCount();
        } else {
            this.updatePolicyDeploySuccessCount();
        }
    }

    /**
     * Update the policy executed count.
     */
    public void updatePolicyExecutedCounter(final boolean isSuccessful) {
        this.updatePolicyExecutedCount();
        if (isSuccessful) {
            this.updatePolicyExecutedSuccessCount();
        } else {
            this.updatePolicyExecutedFailCount();
        }
    }


    /**
     * Update the policy undeploy count.
     */
    public void updatePolicyUndeployCounter(final boolean isSuccessful) {
        this.policyUndeployCount.incrementAndGet();
        if (isSuccessful) {
            this.policyUndeploySuccessCount.incrementAndGet();
        } else {
            this.policyUndeployFailCount.incrementAndGet();
        }
    }

    /**
     * Method to update the total policy deploy count.
     *
     * @return the updated value of policyDeployCount
     */
    private long updatepPolicyDeployCount() {
        return policyDeployCount.incrementAndGet();
    }

    /**
     * Method to update the total policy deploy failed count.
     *
     * @return the updated value of totalPolicyDeployCount
     */
    private long updatePolicyDeployFailCount() {
        return policyDeployFailCount.incrementAndGet();
    }

    /**
     * Method to update the policy deploy success count.
     *
     * @return the updated value of policyDeploySuccessCount
     */
    private long updatePolicyDeploySuccessCount() {
        return policyDeploySuccessCount.incrementAndGet();
    }


    /**
     * Method to update the total policy executed count.
     *
     * @return the updated value of policyExecutedCount
     */
    private long updatePolicyExecutedCount() {
        return policyExecutedCount.incrementAndGet();
    }

    /**
     * Method to update the policy executed success count.
     *
     * @return the updated value of policyExecutedSuccessCount
     */
    private long updatePolicyExecutedSuccessCount() {
        return policyExecutedSuccessCount.incrementAndGet();
    }

    /**
     * Method to update the policy executed failure count.
     *
     * @return the updated value of policyExecutedFailCount
     */
    private long updatePolicyExecutedFailCount() {
        return policyExecutedFailCount.incrementAndGet();
    }

    /**
     * Reset all the statistics counts to 0.
     */
    public void resetAllStatistics() {
        policyDeployCount.set(0L);
        policyDeployFailCount.set(0L);
        policyDeploySuccessCount.set(0L);
        policyUndeployCount.set(0L);
        policyUndeployFailCount.set(0L);
        policyUndeploySuccessCount.set(0L);
        policyExecutedCount.set(0L);
        policyExecutedSuccessCount.set(0L);
        policyExecutedFailCount.set(0L);
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
