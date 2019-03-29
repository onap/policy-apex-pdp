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

package org.onap.policy.apex.starter.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.onap.policy.apex.starter.ApexStarterCommandLineArguments;
import org.onap.policy.apex.starter.exception.ApexStarterException;
import org.onap.policy.common.utils.coder.CoderException;

/**
 * Class to perform unit test of {@link ApexStarterParameterHandler}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestApexStarterParameterHandler {

    @Test
    public void testParameterHandlerNoParameterFile() throws ApexStarterException {
        final String[] emptyArgumentString = { "-c", "src/test/resources/NoParametersFile.json" };

        final ApexStarterCommandLineArguments emptyArguments = new ApexStarterCommandLineArguments();
        emptyArguments.parse(emptyArgumentString);

        try {
            new ApexStarterParameterHandler().getParameters(emptyArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getCause() instanceof CoderException);
            assertTrue(e.getCause().getCause() instanceof FileNotFoundException);
        }
    }

    @Test
    public void testParameterHandlerEmptyParameters() throws ApexStarterException {
        final String[] noArgumentString = { "-c", "src/test/resources/NoParameters.json" };

        final ApexStarterCommandLineArguments noArguments = new ApexStarterCommandLineArguments();
        noArguments.parse(noArgumentString);

        try {
            new ApexStarterParameterHandler().getParameters(noArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("no parameters found"));
        }
    }

    @Test
    public void testParameterHandlerInvalidParameters() throws ApexStarterException {
        final String[] invalidArgumentString = { "-c", "src/test/resources/InvalidParameters.json" };

        final ApexStarterCommandLineArguments invalidArguments = new ApexStarterCommandLineArguments();
        invalidArguments.parse(invalidArgumentString);

        try {
            new ApexStarterParameterHandler().getParameters(invalidArguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().startsWith("error reading parameters from"));
            assertTrue(e.getCause() instanceof CoderException);
        }
    }

    @Test
    public void testParameterHandlerNoParameters() throws ApexStarterException {
        final String[] noArgumentString = { "-c", "src/test/resources/EmptyConfigParameters.json" };

        final ApexStarterCommandLineArguments noArguments = new ApexStarterCommandLineArguments();
        noArguments.parse(noArgumentString);

        try {
            new ApexStarterParameterHandler().getParameters(noArguments);
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("is null"));
        }
    }

    @Test
    public void testApexStarterParameterGroup() throws ApexStarterException {
        final String[] apexStarterConfigParameters = { "-c", "src/test/resources/ApexStarterConfigParameters.json" };

        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        arguments.parse(apexStarterConfigParameters);

        final ApexStarterParameterGroup parGroup = new ApexStarterParameterHandler().getParameters(arguments);
        assertTrue(arguments.checkSetConfigurationFilePath());
        assertEquals(CommonTestData.APEX_STARTER_GROUP_NAME, parGroup.getName());
    }

    @Test
    public void testApexStarterParameterGroup_InvalidName() throws ApexStarterException {
        final String[] apexStarterConfigParameters =
                { "-c", "src/test/resources/ApexStarterConfigParameters_InvalidName.json" };

        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        arguments.parse(apexStarterConfigParameters);

        try {
            new ApexStarterParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains(
                    "field \"name\" type \"java.lang.String\" value \" \" INVALID, must be a non-blank string"));
        }
    }

    @Test
    public void testApexStarterVersion() throws ApexStarterException {
        final String[] apexStarterConfigParameters = { "-v" };
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        final String version = arguments.parse(apexStarterConfigParameters);
        assertTrue(version.startsWith("ONAP Policy Framework Apex Starter Service"));
    }

    @Test
    public void testApexStarterHelp() throws ApexStarterException {
        final String[] apexStarterConfigParameters = { "-h" };
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        final String help = arguments.parse(apexStarterConfigParameters);
        assertTrue(help.startsWith("usage:"));
    }

    @Test
    public void testApexStarterInvalidOption() throws ApexStarterException {
        final String[] apexStarterConfigParameters = { "-d" };
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        try {
            arguments.parse(apexStarterConfigParameters);
        } catch (final Exception exp) {
            assertTrue(exp.getMessage().startsWith("invalid command line arguments specified"));
        }
    }
}
