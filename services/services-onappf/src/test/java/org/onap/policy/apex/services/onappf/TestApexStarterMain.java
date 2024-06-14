/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Nordix Foundation
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.services.onappf;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.main.ApexPolicyStatisticsManager;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.exception.ApexStarterRunTimeException;
import org.onap.policy.apex.services.onappf.parameters.CommonTestData;
import org.onap.policy.common.utils.resources.MessageConstants;
import org.onap.policy.common.utils.services.Registry;

/**
 * Class to perform unit test of {@link ApexStarterMain}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class TestApexStarterMain {

    /**
     * Set up.
     */
    @BeforeEach
    void setUp() {
        Registry.newRegistry();
    }

    /**
     * Shuts "main" down.
     *
     * @throws Exception if an error occurs
     */
    @AfterEach
    void tearDown() throws Exception {
        // shut down activator
        final ApexStarterActivator activator =
            Registry.getOrDefault(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, ApexStarterActivator.class, null);
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }
    }

    @Test
    void testApexStarter() throws ApexStarterException {
        final String[] apexStarterConfigParameters = {"-c", "src/test/resources/ApexStarterConfigParametersNoop.json"};
        ApexStarterMain apexStarter = new ApexStarterMain(apexStarterConfigParameters);
        assertTrue(apexStarter.getParameters().isValid());
        assertEquals(CommonTestData.APEX_STARTER_GROUP_NAME, apexStarter.getParameters().getName());

        // ensure items were added to the registry
        assertNotNull(Registry.get(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, ApexStarterActivator.class));
        assertNotNull(
            Registry.get(ApexPolicyStatisticsManager.REG_APEX_PDP_POLICY_COUNTER, ApexPolicyStatisticsManager.class));
        apexStarter.shutdown();
    }

    @Test
    void testApexStarter_NoArguments() {
        final String[] apexStarterConfigParameters = {};
        assertThatThrownBy(() -> new ApexStarterMain(apexStarterConfigParameters))
            .isInstanceOf(ApexStarterRunTimeException.class)
            .hasMessage(String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_APEX_PDP));
    }

    @Test
    void testApexStarter_InvalidArguments() {
        final String[] apexStarterConfigParameters = {"src/test/resources/ApexStarterConfigParameters.json"};
        assertThatThrownBy(() -> new ApexStarterMain(apexStarterConfigParameters))
            .isInstanceOf(ApexStarterRunTimeException.class)
            .hasMessage(String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_APEX_PDP));
    }

    @Test
    void testApexStarter_Help() {
        final String[] apexStarterConfigParameters = {"-h"};
        assertThatCode(() -> ApexStarterMain.main(apexStarterConfigParameters)).doesNotThrowAnyException();
    }

    @Test
    void testApexStarter_InvalidParameters() {
        final String[] apexStarterConfigParameters =
            {"-c", "src/test/resources/ApexStarterConfigParameters_InvalidName.json"};
        assertThatThrownBy(() -> new ApexStarterMain(apexStarterConfigParameters))
            .isInstanceOf(ApexStarterRunTimeException.class)
            .hasMessage(String.format(MessageConstants.START_FAILURE_MSG, MessageConstants.POLICY_APEX_PDP));
    }
}
