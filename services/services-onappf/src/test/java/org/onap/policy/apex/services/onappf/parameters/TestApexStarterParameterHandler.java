/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2021, 2024 Nordix Foundation.
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

package org.onap.policy.apex.services.onappf.parameters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.services.onappf.ApexStarterCommandLineArguments;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.common.utils.cmd.CommandLineException;
import org.onap.policy.common.utils.coder.CoderException;

/**
 * Class to perform unit test of {@link ApexStarterParameterHandler}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
class TestApexStarterParameterHandler {

    @Test
    void testParameterHandlerNoParameterFile() throws CommandLineException {
        final String[] emptyArgumentString = {"-c", "src/test/resources/NoParametersFile.json"};

        final ApexStarterCommandLineArguments emptyArguments = new ApexStarterCommandLineArguments();
        emptyArguments.parse(emptyArgumentString);

        assertThatThrownBy(() -> new ApexStarterParameterHandler().getParameters(emptyArguments))
            .hasCauseInstanceOf(CoderException.class)
            .hasRootCauseInstanceOf(FileNotFoundException.class);
    }

    @Test
    void testParameterHandlerEmptyParameters() throws CommandLineException {
        final String[] noArgumentString = {"-c", "src/test/resources/NoParameters.json"};

        final ApexStarterCommandLineArguments noArguments = new ApexStarterCommandLineArguments();
        noArguments.parse(noArgumentString);

        assertThatThrownBy(() -> new ApexStarterParameterHandler().getParameters(noArguments))
            .hasMessageContaining("no parameters found");
    }

    @Test
    void testParameterHandlerInvalidParameters() throws CommandLineException {
        final String[] invalidArgumentString = {"-c", "src/test/resources/InvalidParameters.json"};

        final ApexStarterCommandLineArguments invalidArguments = new ApexStarterCommandLineArguments();
        invalidArguments.parse(invalidArgumentString);

        assertThatThrownBy(() -> new ApexStarterParameterHandler().getParameters(invalidArguments))
            .hasMessageStartingWith("error reading parameters from")
            .hasCauseInstanceOf(CoderException.class);
    }

    @Test
    void testParameterHandlerNoParameters() throws CommandLineException {
        final String[] noArgumentString = {"-c", "src/test/resources/EmptyConfigParameters.json"};

        final ApexStarterCommandLineArguments noArguments = new ApexStarterCommandLineArguments();
        noArguments.parse(noArgumentString);

        assertThatThrownBy(() -> new ApexStarterParameterHandler().getParameters(noArguments))
            .hasMessageContaining("is null");
    }

    @Test
    void testApexStarterParameterGroup() throws ApexStarterException, CommandLineException {
        final String[] apexStarterConfigParameters = {"-c", "src/test/resources/ApexStarterConfigParameters.json"};

        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        arguments.parse(apexStarterConfigParameters);

        final ApexStarterParameterGroup parGroup = new ApexStarterParameterHandler().getParameters(arguments);
        assertTrue(arguments.checkSetConfigurationFilePath());
        assertEquals(CommonTestData.APEX_STARTER_GROUP_NAME, parGroup.getName());
    }

    @Test
    void testApexStarterParameterGroup_InvalidName() throws CommandLineException {
        final String[] apexStarterConfigParameters =
            {"-c", "src/test/resources/ApexStarterConfigParameters_InvalidName.json"};

        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        arguments.parse(apexStarterConfigParameters);

        assertThatThrownBy(() -> new ApexStarterParameterHandler().getParameters(arguments))
            .hasMessageContaining("\"name\" value \" \" INVALID, is blank");
    }

    @Test
    void testApexStarterVersion() throws CommandLineException {
        final String[] apexStarterConfigParameters = {"-v"};
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        final String version = arguments.parse(apexStarterConfigParameters);
        assertTrue(version.startsWith("ONAP Policy Framework Apex Starter Service"));
    }

    @Test
    void testApexStarterHelp() throws CommandLineException {
        final String[] apexStarterConfigParameters = {"-h"};
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        final String help = arguments.parse(apexStarterConfigParameters);
        assertTrue(help.startsWith("usage:"));
    }

    @Test
    void testApexStarterInvalidOption() {
        final String[] apexStarterConfigParameters = {"-d"};
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        assertThatThrownBy(() -> arguments.parse(apexStarterConfigParameters))
            .hasMessageStartingWith("invalid command line arguments specified");
    }
}
