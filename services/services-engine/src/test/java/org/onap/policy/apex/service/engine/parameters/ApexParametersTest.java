/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test the ApexParameters class.
 */
public class ApexParametersTest {

    @Test
    public void testJavaPropertiesOk() throws ParameterException {
        final String[] args = {"-c", "src/test/resources/parameters/javaPropertiesOK.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertTrue(parameters.checkJavaPropertiesSet());
            assertEquals("property0", parameters.getJavaProperties()[0][0]);
            assertEquals("property0Value", parameters.getJavaProperties()[0][1]);
            assertEquals("property1", parameters.getJavaProperties()[1][0]);
            assertEquals("property1Value", parameters.getJavaProperties()[1][1]);
        } catch (final ParameterException e) {
            fail("This test should not throw an exception");
        }
    }

    @Test
    public void testJavaPropertiesEmpty() throws ParameterException {
        final String[] args = {"-c", "src/test/resources/parameters/javaPropertiesEmpty.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertFalse(parameters.checkJavaPropertiesSet());
        } catch (final ParameterException pe) {
            fail("This test should not throw an exception");
        }
    }

    @Test
    public void testJavaPropertiesBad() throws ParameterException {
        final String[] args = {"-c", "src/test/resources/parameters/javaPropertiesBad.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException pe) {
            assertTrue(pe.getMessage().contains("java properties array entries must have one key and one value"));
            assertTrue(pe.getMessage().contains("java properties key is null or blank"));
            assertTrue(pe.getMessage().contains("java properties value is null or blank"));
            assertTrue(pe.getMessage().contains("java properties array entry is null"));
        }
    }

    @Test
    public void testGettersSetters() {
        ApexParameters pars = new ApexParameters();
        assertNotNull(pars);

        pars.setEngineServiceParameters(null);
        assertNull(pars.getEngineServiceParameters());

        pars.setEventInputParameters(null);
        assertNull(pars.getEventInputParameters());

        pars.setEventOutputParameters(null);
        assertNull(pars.getEventOutputParameters());

        assertFalse(pars.checkJavaPropertiesSet());

        pars.setName("parName");
        assertEquals("parName", pars.getName());
    }
}
