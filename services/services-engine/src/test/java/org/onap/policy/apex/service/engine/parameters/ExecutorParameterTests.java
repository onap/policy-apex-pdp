/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterException;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;

/**
 * Test for an empty parameter file.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ExecutorParameterTests {

    @Test
    public void noParamsTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorNoParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertEquals(0,
                    parameters.getEngineServiceParameters().getEngineParameters().getExecutorParameterMap().size());
        } catch (final ApexParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }

    @Test
    public void badParamsTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorBadParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals(
                    "error reading parameters from \"src/test/resources/parameters/serviceExecutorBadParams.json\"\n"
                            + "(ApexParameterRuntimeException):value of \"executorParameters:ZOOBY\" entry is not "
                            + "a parameter JSON object",
                    e.getMessage());
        }
    }

    @Test
    public void noExecutorParamsTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorNoExecutorParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/serviceExecutorNoExecutorParams.json\"\n"
                    + "(ApexParameterRuntimeException):no \"executorParameters\" entry found in parameters,"
                    + " at least one executor parameter entry must be specified", e.getMessage());
        }
    }

    @Test
    public void emptyParamsTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorEmptyParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/serviceExecutorEmptyParams.json\"\n"
                    + "(ApexParameterRuntimeException):could not find field \"parameterClassName\" "
                    + "in \"executorParameters:ZOOBY\" entry", e.getMessage());
        }
    }

    @Test
    public void badPluginParamNameTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorBadPluginNameParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/serviceExecutorBadPluginNameParams.json\"\n"
                    + "(ApexParameterRuntimeException):could not find field \"parameterClassName\" "
                    + "in \"executorParameters:ZOOBY\" entry", e.getMessage());
        }
    }

    @Test
    public void badPluginParamObjectTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorBadPluginValueObjectParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/serviceExecutorBadPluginValueObjectParams.json\"\n"
                    + "(ApexParameterRuntimeException):value for field \"parameterClassName\" "
                    + "in \"executorParameters:LOOBY\" entry is not a plain string", e.getMessage());
        }
    }

    @Test
    public void badPluginParamBlankTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorBadPluginValueBlankParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("error reading parameters from "
                    + "\"src/test/resources/parameters/serviceExecutorBadPluginValueBlankParams.json\"\n"
                    + "(ApexParameterRuntimeException):value for field \"parameterClassName\" "
                    + "in \"executorParameters:LOOBY\" entry is not specified or is blank", e.getMessage());
        }
    }


    @Test
    public void badPluginParamValueTest() {
        final String[] args = {"-c", "src/test/resources/parameters/serviceExecutorBadPluginValueParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("error reading parameters from"
                    + " \"src/test/resources/parameters/serviceExecutorBadPluginValueParams.json\"\n"
                    + "(ApexParameterRuntimeException):failed to deserialize the parameters "
                    + "for \"executorParameters:LOOBY\" to parameter class \"helloworld\"\n"
                    + "java.lang.ClassNotFoundException: helloworld", e.getMessage());
        }
    }

    @Test
    public void goodParametersTest() {
        final String[] args = {"-c", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
            assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
            assertEquals(45, parameters.getEngineServiceParameters().getId());
            assertEquals(19, parameters.getEngineServiceParameters().getInstanceCount());
            assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());
        } catch (final ApexParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }
}
