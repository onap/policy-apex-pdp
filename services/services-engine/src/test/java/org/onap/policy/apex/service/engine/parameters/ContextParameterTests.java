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
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperDistributorParameters;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test for an empty parameter file.
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ContextParameterTests {

    @Test
    public void noParamsTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextNoParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from \"src/test/resources/parameters/serviceContextNoParams.json\"\n"
                            + "(ParameterRuntimeException):could not find field \"parameterClassName\" in "
                            + "\"contextParameters\" entry", e.getMessage());
        }
    }

    @Test
    public void badParamsTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from \"src/test/resources/parameters/serviceContextBadParams.json\""
                            + "\n(ParameterRuntimeException):failed to deserialize the parameters for "
                            + "\"contextParameters\" to parameter class "
                            + "\"hello\"\njava.lang.ClassNotFoundException: hello", e.getMessage());
        }
    }

    @Test
    public void badPluginParamNameTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadPluginNameParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                            + "\"src/test/resources/parameters/serviceContextBadPluginNameParams.json\"\n"
                            + "(ParameterRuntimeException):could not find field \"parameterClassName\" in "
                            + "\"contextParameters\" entry", e.getMessage());
        }
    }

    @Test
    public void badClassParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadClassParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                            + "\"src/test/resources/parameters/serviceContextBadClassParams.json\""
                            + "\n(ParameterRuntimeException):failed to deserialize "
                            + "the parameters for \"contextParameters\""
                            + " to parameter class \"java.lang.Integer\"\ncom.google.gson.JsonSyntaxException: "
                            + "java.lang.IllegalStateException: Expected NUMBER but was BEGIN_OBJECT at path $",
                            e.getMessage());
        }
    }

    @Test
    public void badPluginClassTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadPluginClassParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                            + "\"src/test/resources/parameters/serviceContextBadPluginClassParams.json\""
                            + "\n(ClassCastException):org.onap.policy.apex.service.engine.parameters.dummyclasses."
                            + "SuperDooperExecutorParameters"
                            + " cannot be cast to org.onap.policy.apex.context.parameters.ContextParameters",
                            e.getMessage());
        }
    }

    @Test
    public void okFlushParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextOKFlushParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getClass().getCanonicalName());
            assertEquals(123456, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                            .getPersistorParameters().getFlushPeriod());
        } catch (final ParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }

    @Test
    public void okDefaultParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextOKDefaultParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getClass().getCanonicalName());
            assertEquals(300000, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                            .getPersistorParameters().getFlushPeriod());
        } catch (final ParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }

    @Test
    public void okDistParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextOKDistParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getClass().getCanonicalName());
            assertEquals("org.onap.policy.apex.context.parameters.DistributorParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getDistributorParameters().getClass().getCanonicalName());
        } catch (final ParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }

    @Test
    public void okFullDefaultParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/goodParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getClass().getCanonicalName());
            assertEquals("org.onap.policy.apex.context.parameters.DistributorParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getDistributorParameters().getClass().getCanonicalName());
            assertEquals("org.onap.policy.apex.context.parameters.LockManagerParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getLockManagerParameters().getClass().getCanonicalName());
            assertEquals("org.onap.policy.apex.context.parameters.PersistorParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getPersistorParameters().getClass().getCanonicalName());
            assertEquals(300000, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                            .getPersistorParameters().getFlushPeriod());
        } catch (final ParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }

    @Test
    public void okFullParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextOKFullParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
            assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getClass().getCanonicalName());
            assertEquals("org.onap.policy.apex.context.parameters.LockManagerParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getLockManagerParameters().getClass().getCanonicalName());
            assertEquals("org.onap.policy.apex.context.parameters.PersistorParameters",
                            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                                            .getPersistorParameters().getClass().getCanonicalName());
            assertEquals(123456, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                            .getPersistorParameters().getFlushPeriod());

            final SuperDooperDistributorParameters infinispanParameters = (SuperDooperDistributorParameters) parameters
                            .getEngineServiceParameters().getEngineParameters().getContextParameters()
                            .getDistributorParameters();
            assertEquals("org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperDistributorParameters",
                            infinispanParameters.getClass().getCanonicalName());
            assertEquals("my/lovely/configFile.xml", infinispanParameters.getConfigFile());
            assertEquals("holy/stone.xml", infinispanParameters.getJgroupsFile());
            assertEquals(false, infinispanParameters.isPreferIPv4Stack());
            assertEquals("fatherted", infinispanParameters.getJgroupsBindAddress());

        } catch (final ParameterException e) {
            fail("This test should not throw any exception: " + e.getMessage());
        }
    }

    @Test
    public void badClassDistParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadClassDistParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                            + "\"src/test/resources/parameters/serviceContextBadClassDistParams.json\"\n"
                            + "(ClassCastException):"
                            + "org.onap.policy.apex.context.parameters.ContextParameters cannot be cast to"
                            + " org.onap.policy.apex.context.parameters.DistributorParameters", e.getMessage());
        }
    }

    @Test
    public void badClassLockParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadClassLockParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                            + "\"src/test/resources/parameters/serviceContextBadClassLockParams.json\"\n"
                            + "(ClassCastException):"
                            + "org.onap.policy.apex.context.parameters.ContextParameters cannot be cast to"
                            + " org.onap.policy.apex.context.parameters.LockManagerParameters", e.getMessage());
        }
    }

    @Test
    public void badClassPersistParamTest() {
        final String[] args =
            { "-c", "src/test/resources/parameters/serviceContextBadClassPersistParams.json" };
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ParameterException e) {
            assertEquals("error reading parameters from "
                            + "\"src/test/resources/parameters/serviceContextBadClassPersistParams.json\"\n"
                            + "(ClassCastException):"
                            + "org.onap.policy.apex.context.parameters.ContextParameters cannot be cast to"
                            + " org.onap.policy.apex.context.parameters.PersistorParameters", e.getMessage());
        }
    }
}
