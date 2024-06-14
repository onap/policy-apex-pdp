/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.SyncInvoker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.onap.policy.common.endpoints.report.HealthCheckReport;

/**
 * Class to perform unit test of {@link HealthCheckRestControllerV1}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class TestHealthCheckRestControllerV1 extends CommonApexStarterRestServer {

    private static final String HEALTHCHECK_ENDPOINT = "healthcheck";

    @Test
    void testHealthCheckSuccess() throws Exception {
        final Invocation.Builder invocationBuilder = sendRequest(HEALTHCHECK_ENDPOINT);
        final HealthCheckReport report = invocationBuilder.get(HealthCheckReport.class);
        validateHealthCheckReport(report);

        // verify it fails when no authorization info is included
        checkUnauthorizedRequest(HEALTHCHECK_ENDPOINT, SyncInvoker::get);
    }

    private void validateHealthCheckReport(final HealthCheckReport report) {
        assertThat(report.getName()).isNotBlank();
        assertEquals(CommonApexStarterRestServer.SELF, report.getUrl());
        assertTrue(report.isHealthy());
        assertEquals(200, report.getCode());
        assertEquals(CommonApexStarterRestServer.ALIVE, report.getMessage());
    }
}
