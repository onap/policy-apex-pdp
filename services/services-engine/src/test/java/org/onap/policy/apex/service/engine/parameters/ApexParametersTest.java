/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.service.engine.parameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test the ApexParameters class.
 */
class ApexParametersTest {

    @Test
    void testJavaPropertiesOk() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/javaPropertiesOK.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertTrue(parameters.checkJavaPropertiesSet());
        assertEquals("property0", parameters.getJavaProperties()[0][0]);
        assertEquals("property0Value", parameters.getJavaProperties()[0][1]);
        assertEquals("property1", parameters.getJavaProperties()[1][0]);
        assertEquals("property1Value", parameters.getJavaProperties()[1][1]);

    }

    @Test
    void testJavaPropertiesEmpty() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/javaPropertiesEmpty.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertFalse(parameters.checkJavaPropertiesSet());

    }

    @Test
    void testJavaPropertiesBad() {
        final String[] args = {"-p", "src/test/resources/parameters/javaPropertiesBad.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("\"javaProperties\"")
            .hasMessageContaining("entry 0", "entry 1", "entry 2", "entry 3", "entry 4", "entry 5")
            .hasMessageContaining("must have one key and one value")
            .hasMessageContaining("\"key\" value \"null\" INVALID, is blank")
            .hasMessageContaining("\"value\" value \"null\" INVALID, is blank");
    }

    @Test
    void testPolicyModelFromMetadata() throws ParameterException {
        // Policy Models provided only in metadata.
        final String[] args = {"-p", "src/test/resources/parameters/policyModelFromMetadata.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertThat(parameters.getEngineServiceParameters().getPolicyModel()).isNotEmpty();
        assertThat(parameters.getEngineServiceParameters().getPolicyModel())
            .contains("{\"key\":{\"name\":\"dummy key1 provided in metadata\",\"version\":\"0.0.1\"},\"keyInformation\""
                + ":{\"key\":{\"name\":\"dummy key2 provided in metadata\",\"version\":\"0.0.1\"}},"
                + "\"threshold\":3.15,\"state\":\"passive\"}");
    }

    @Test
    void testPolicyModelFromProperties() throws ParameterException {
        // Policy models provided in properties under EngineServiceParameters for backward compatibility
        final String[] args = {"-p", "src/test/resources/parameters/policyModelFromProperties.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertThat(parameters.getEngineServiceParameters().getPolicyModel()).isNotEmpty();
        assertThat(parameters.getEngineServiceParameters().getPolicyModel())
            .contains("{\"key\":{\"name\":\"dummy key1 provided in properties\",\"version\":\"0.0.1\"},"
                + "\"keyInformation\":{\"key\":{\"name\":\"dummy key2 provided in properties\","
                + "\"version\":\"0.0.1\"}},\"threshold\":3.15,\"state\":\"passive\"}");
    }

    @Test
    void testPolicyModelFromPropertiesAndMetadata() throws ParameterException {
        // Policy models provided in both properties and in metadata. policyModels in metadata takes precedence
        final String[] args = {"-p", "src/test/resources/parameters/policyModelMultiple.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertThat(parameters.getEngineServiceParameters().getPolicyModel()).isNotEmpty();
        assertThat(parameters.getEngineServiceParameters().getPolicyModel())
            .contains("{\"key\":{\"name\":\"dummy key1 provided in metadata\",\"version\":\"0.0.1\"},"
                + "\"keyInformation\":{\"key\":{\"name\":\"dummy key2 provided in metadata\","
                + "\"version\":\"0.0.1\"}},\"threshold\":3.15,\"state\":\"passive\"}");
    }

    @Test
    void testGettersSetters() {
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
