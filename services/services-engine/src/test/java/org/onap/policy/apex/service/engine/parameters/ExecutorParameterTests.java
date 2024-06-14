/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.parameters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test for an empty parameter file.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
class ExecutorParameterTests {

    @AfterEach
    void resetRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @Test
    void testNoParamsTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorNoParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals(0,
            parameters.getEngineServiceParameters().getEngineParameters().getExecutorParameterMap().size());

    }

    @Test
    void testBadParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorBadParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceExecutorBadParams.json\"\n"
                + "(ParameterRuntimeException):value of \"executorParameters:ZOOBY\" entry is not "
                + "a parameter JSON object");
    }

    @Test
    void testNoExecutorParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorNoExecutorParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceExecutorNoExecutorParams.json\"\n"
                + "(ParameterRuntimeException):no \"executorParameters\" entry found in parameters,"
                + " at least one executor parameter entry must be specified");
    }

    @Test
    void testEmptyParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorEmptyParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceExecutorEmptyParams.json\"\n"
                + "(ParameterRuntimeException):could not find field \"parameterClassName\" "
                + "in \"executorParameters:ZOOBY\" entry");
    }

    @Test
    void testBadPluginParamNameTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorBadPluginNameParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceExecutorBadPluginNameParams.json\"\n"
                + "(ParameterRuntimeException):could not find field \"parameterClassName\" "
                + "in \"executorParameters:ZOOBY\" entry");
    }

    @Test
    void testBadPluginParamObjectTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorBadPluginValueObjectParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceExecutorBadPluginValueObjectParams.json\"\n"
                + "(ParameterRuntimeException):value for field \"parameterClassName\" "
                + "of \"executorParameters:LOOBY\" entry is not a plain string");
    }

    @Test
    void testBadPluginParamBlankTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorBadPluginValueBlankParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceExecutorBadPluginValueBlankParams.json\"\n"
                + "(ParameterRuntimeException):value for field \"parameterClassName\" "
                + "in \"executorParameters:LOOBY\" entry is not specified or is blank");
    }

    @Test
    void testBadPluginParamValueTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceExecutorBadPluginValueParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("error reading parameters from"
                + " \"src/test/resources/parameters/serviceExecutorBadPluginValueParams.json\"\n")
            .hasMessageContaining("(ParameterRuntimeException):failed to deserialize the parameters "
                + "for \"executorParameters:LOOBY\" to parameter class \"helloworld\"\n"
                + "java.lang.ClassNotFoundException: helloworld");
    }

    @Test
    void testGoodParametersTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
        assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
        assertEquals(45, parameters.getEngineServiceParameters().getId());
        assertEquals(19, parameters.getEngineServiceParameters().getInstanceCount());
        assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());

    }

    @Test
    void testRelativeParametersTest() throws ParameterException {
        // @formatter:off
        final String[] args = {
            "-rfr",
            "src/test/resources",
            "-p",
            "src/test/resources/parameters/goodParamsRelative.json"
        };
        // @formatter:on
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

        assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
        assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
        assertEquals(45, parameters.getEngineServiceParameters().getId());
        assertEquals(19, parameters.getEngineServiceParameters().getInstanceCount());
        assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());
    }
}
