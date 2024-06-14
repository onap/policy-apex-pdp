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
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
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
class ContextParameterTests {

    @Test
    void testNoParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextNoParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from \"src/test/resources/parameters/serviceContextNoParams.json\"\n"
                + "(ParameterRuntimeException):could not find field \"parameterClassName\" in "
                + "\"contextParameters\" entry");
    }

    @Test
    void testBadParamsTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadParams.json\"")
            .hasMessageContaining("class " + "\"hello\"\njava.lang.ClassNotFoundException: hello");
    }

    @Test
    void testBadPluginParamNameTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadPluginNameParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadPluginNameParams.json\"\n"
                + "(ParameterRuntimeException):could not find field \"parameterClassName\" in "
                + "\"contextParameters\" entry");
    }

    @Test
    void testBadClassParamTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadClassParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessageContaining("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadClassParams.json\"")
            .hasMessageContaining("(ParameterRuntimeException):failed to deserialize the parameters for "
                + "\"contextParameters\" to parameter class \"java.lang.Integer\"\ncom.google.gson.JsonSyntaxException"
                + ": java.lang.IllegalStateException: Expected NUMBER but was BEGIN_OBJECT at path $");
    }

    @Test
    void testBadPluginClassTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadPluginClassParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadPluginClassParams.json\""
                + "\n(ClassCastException):class org.onap.policy.apex.service.engine.parameters."
                + "dummyclasses.SuperDooperExecutorParameters"
                + " cannot be cast to class org.onap.policy.apex.context.parameters.ContextParameters "
                + "(org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperExecutorParameters and "
                + "org.onap.policy.apex.context.parameters.ContextParameters are "
                + "in unnamed module of loader 'app')");
    }

    @Test
    void testOkFlushParamTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextOKFlushParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters().getClass().getName());
        assertEquals(123456, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
            .getPersistorParameters().getFlushPeriod());

    }

    @Test
    void testOkDefaultParamTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextOKDefaultParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters().getClass().getName());
        assertEquals(300000, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
            .getPersistorParameters().getFlushPeriod());

    }

    @Test
    void testOkDistParamTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextOKDistParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters().getClass().getName());
        assertEquals("org.onap.policy.apex.context.parameters.DistributorParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                .getDistributorParameters().getClass().getName());

    }

    @Test
    void testOkFullDefaultParamTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters().getClass().getName());
        assertEquals("org.onap.policy.apex.context.parameters.DistributorParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                .getDistributorParameters().getClass().getName());
        assertEquals("org.onap.policy.apex.context.parameters.LockManagerParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                .getLockManagerParameters().getClass().getName());
        assertEquals("org.onap.policy.apex.context.parameters.PersistorParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                .getPersistorParameters().getClass().getName());
        assertEquals(300000, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
            .getPersistorParameters().getFlushPeriod());

    }

    @Test
    void testOkFullParamTest() throws ParameterException {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextOKFullParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);
        assertEquals("org.onap.policy.apex.context.parameters.ContextParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters().getClass().getName());
        assertEquals("org.onap.policy.apex.context.parameters.LockManagerParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                .getLockManagerParameters().getClass().getName());
        assertEquals("org.onap.policy.apex.context.parameters.PersistorParameters",
            parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
                .getPersistorParameters().getClass().getName());
        assertEquals(123456, parameters.getEngineServiceParameters().getEngineParameters().getContextParameters()
            .getPersistorParameters().getFlushPeriod());

        final SuperDooperDistributorParameters infinispanParameters = (SuperDooperDistributorParameters) parameters
            .getEngineServiceParameters().getEngineParameters().getContextParameters().getDistributorParameters();
        assertEquals("org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperDistributorParameters",
            infinispanParameters.getClass().getName());
        assertEquals("my/lovely/configFile.xml", infinispanParameters.getConfigFile());
        assertEquals("holy/stone.xml", infinispanParameters.getJgroupsFile());
        assertFalse(infinispanParameters.isPreferIPv4Stack());
        assertEquals("fatherted", infinispanParameters.getJgroupsBindAddress());

    }

    @Test
    void testBadClassDistParamTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadClassDistParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadClassDistParams.json\"\n"
                + "(ClassCastException):class "
                + "org.onap.policy.apex.context.parameters.ContextParameters cannot be cast to class"
                + " org.onap.policy.apex.context.parameters.DistributorParameters (org.onap.policy.apex.context."
                + "parameters.ContextParameters and org.onap.policy.apex.context.parameters.DistributorParameters "
                + "are in unnamed module of loader 'app')");
    }

    @Test
    void testBadClassLockParamTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadClassLockParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadClassLockParams.json\"\n"
                + "(ClassCastException):class "
                + "org.onap.policy.apex.context.parameters.ContextParameters cannot be cast to class "
                + "org.onap.policy.apex.context.parameters.LockManagerParameters (org.onap.policy.apex.context."
                + "parameters.ContextParameters and org.onap.policy.apex.context.parameters.LockManagerParameters "
                + "are in unnamed module of loader 'app')");
    }

    @Test
    void testBadClassPersistParamTest() {
        final String[] args = {"-p", "src/test/resources/parameters/serviceContextBadClassPersistParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        assertThatThrownBy(() -> new ApexParameterHandler().getParameters(arguments))
            .hasMessage("error reading parameters from "
                + "\"src/test/resources/parameters/serviceContextBadClassPersistParams.json\"\n"
                + "(ClassCastException):class "
                + "org.onap.policy.apex.context.parameters.ContextParameters cannot be cast to class "
                + "org.onap.policy.apex.context.parameters.PersistorParameters (org.onap.policy.apex.context."
                + "parameters.ContextParameters and org.onap.policy.apex.context.parameters.PersistorParameters "
                + "are in unnamed module of loader 'app')");
    }
}
