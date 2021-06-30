/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Invocation;
import org.junit.Test;
import org.onap.policy.common.endpoints.report.HealthCheckReport;

/**
 * Class to perform unit test of {@link ApexStarterRestServer}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestHealthCheckRestControllerV1 extends CommonApexStarterRestServer {

    private static final String HEALTHCHECK_ENDPOINT = "healthcheck";

    @Test
    public void testSwagger() throws Exception {
        super.testSwagger(HEALTHCHECK_ENDPOINT);
    }

    @Test
    public void testHealthCheckSuccess() throws Exception {
        final Invocation.Builder invocationBuilder = sendRequest(HEALTHCHECK_ENDPOINT);
        final HealthCheckReport report = invocationBuilder.get(HealthCheckReport.class);
        validateHealthCheckReport(SELF, true, 200, ALIVE, report);

        // verify it fails when no authorization info is included
        checkUnauthRequest(HEALTHCHECK_ENDPOINT, req -> req.get());
    }

    private void validateHealthCheckReport(final String url, final boolean healthy, final int code,
            final String message, final HealthCheckReport report) {
        assertThat(report.getName()).isNotBlank();
        assertEquals(url, report.getUrl());
        assertEquals(healthy, report.isHealthy());
        assertEquals(code, report.getCode());
        assertEquals(message, report.getMessage());
    }
}
