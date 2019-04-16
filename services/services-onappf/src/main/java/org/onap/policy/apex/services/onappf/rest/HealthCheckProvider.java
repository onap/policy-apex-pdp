/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.rest;

import org.onap.policy.apex.services.onappf.ApexStarterActivator;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.common.endpoints.report.HealthCheckReport;
import org.onap.policy.common.utils.services.Registry;

/**
 * Class to fetch health check of ApexStarter service.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class HealthCheckProvider {

    private static final String NOT_ALIVE = "not alive";
    private static final String ALIVE = "alive";
    private static final String URL = "self";
    private static final String NAME = "Policy PDP-A";

    /**
     * Performs the health check of PAP service.
     *
     * @return Report containing health check status
     */
    public HealthCheckReport performHealthCheck() {
        final HealthCheckReport report = new HealthCheckReport();
        report.setName(NAME);
        report.setUrl(URL);

        final boolean alive =
                Registry.get(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, ApexStarterActivator.class).isAlive();

        report.setHealthy(alive);
        report.setCode(alive ? 200 : 500);
        report.setMessage(alive ? ALIVE : NOT_ALIVE);
        return report;
    }
}
