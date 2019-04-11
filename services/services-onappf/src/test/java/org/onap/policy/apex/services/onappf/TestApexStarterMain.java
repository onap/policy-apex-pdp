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

package org.onap.policy.apex.services.onappf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.services.onappf.ApexStarterActivator;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.ApexStarterMain;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.parameters.CommonTestData;
import org.onap.policy.common.utils.services.Registry;

/**
 * Class to perform unit test of {@link ApexStarterMain}}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestApexStarterMain {
    private ApexStarterMain apexStarter;

    /**
     * Set up.
     */
    @Before
    public void setUp() {
        Registry.newRegistry();
    }

    /**
     * Shuts "main" down.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void tearDown() throws Exception {
        // shut down activator
        final ApexStarterActivator activator = Registry.getOrDefault(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR,
                ApexStarterActivator.class, null);
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }
    }

    @Test
    public void testApexStarter() throws ApexStarterException {
        final String[] apexStarterConfigParameters = { "-c", "src/test/resources/ApexStarterConfigParameters.json",
            "-p", "src/test/resources/topic.properties" };
        apexStarter = new ApexStarterMain(apexStarterConfigParameters);
        assertTrue(apexStarter.getParameters().isValid());
        assertEquals(CommonTestData.APEX_STARTER_GROUP_NAME, apexStarter.getParameters().getName());

        // ensure items were added to the registry
        assertNotNull(Registry.get(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, ApexStarterActivator.class));

        apexStarter.shutdown();
    }

    @Test
    public void testApexStarter_NoArguments() {
        final String[] apexStarterConfigParameters = {};
        apexStarter = new ApexStarterMain(apexStarterConfigParameters);
        assertTrue(apexStarter.getParameters() == null);
    }

    @Test
    public void testApexStarter_InvalidArguments() {
        final String[] apexStarterConfigParameters = { "src/test/resources/ApexStarterConfigParameters.json" };
        apexStarter = new ApexStarterMain(apexStarterConfigParameters);
        assertTrue(apexStarter.getParameters() == null);
    }

    @Test
    public void testApexStarter_Help() {
        final String[] apexStarterConfigParameters = { "-h" };
        ApexStarterMain.main(apexStarterConfigParameters);
    }

    @Test
    public void testApexStarter_InvalidParameters() {
        final String[] apexStarterConfigParameters =
                { "-c", "src/test/resources/ApexStarterConfigParameters_InvalidName.json" };
        apexStarter = new ApexStarterMain(apexStarterConfigParameters);
        assertTrue(apexStarter.getParameters() == null);
    }
}
