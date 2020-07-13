/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

package org.onap.policy.apex.service.engine.main;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test Apex Command Line Arguments.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexCommandLineArgumentsTest {
    @After
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    @Test
    public void testCommandLineArguments() throws ApexException {
        final ApexCommandLineArguments apexArguments = new ApexCommandLineArguments();

        final String[] args00 =
            { "" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args00);
            apexArguments.validate();
        }).hasMessage("Apex configuration file was not specified as an argument");
        final String[] args01 =
            { "-h" };

        final String result = apexArguments.parse(args01);
        assertTrue(result.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));

        final String[] args02 =
            { "-v" };
        final String result02 = apexArguments.parse(args02);
        assertTrue(result02.startsWith("Apex Adaptive Policy Engine"));

        final String[] args03 =
            { "-v", "-h" };

        final String result03 = apexArguments.parse(args03);
        assertTrue(result03.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));

        final String[] args04 =
            { "-h", "-v" };

        final String result04 = apexArguments.parse(args04);
        assertTrue(result04.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));

        final String[] args05 =
            { "-a" };
        assertThatThrownBy(() -> apexArguments.parse(args05))
            .hasMessage("invalid command line arguments specified : Unrecognized option: -a");
        final String[] args06 =
            { "-c", "hello", "-m", "goodbye", "-h", "-v" };
        final String result06 = apexArguments.parse(args06);
        assertTrue(result06.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));

        final String[] args07 =
            { "-c", "hello", "-m", "goodbye", "-h", "aaa" };
        assertThatThrownBy(() -> {
            final String result07 = apexArguments.parse(args07);
            assertTrue(result07.startsWith("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        }).hasMessage("too many command line arguments specified : [-c, hello, -m, goodbye, -h, aaa]");
    }

    @Test
    public void testCommandLineFileParameters() throws ApexException {
        final ApexCommandLineArguments apexArguments = new ApexCommandLineArguments();

        final String[] args00 =
            { "-c", "zooby" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args00);
            apexArguments.validate();
        }).hasMessage("Apex configuration file \"zooby\" does not exist");
        final String[] args01 =
            { "-c" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args01);
            apexArguments.validate();
        }).hasMessage("invalid command line arguments specified : Missing argument for option: c");
        final String[] args02 =
            { "-c", "src/test/resources/parameters/goodParams.json" };
        apexArguments.parse(args02);
        apexArguments.validate();

        final String[] args03 =
            { "-c", "src/test/resources/parameters/goodParams.json", "-m", "zooby" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args03);
            apexArguments.validate();
        }).hasMessage("Apex model file \"zooby\" does not exist");
        final String[] args04 =
            { "-m" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args04);
            apexArguments.validate();
        }).hasMessage("invalid command line arguments specified : Missing argument for option: m");
        final String[] args05 =
            { "-c", "src/test/resources/parameters/goodParams.json", "-m" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args05);
            apexArguments.validate();
        }).hasMessage("invalid command line arguments specified : Missing argument for option: m");
        final String[] args06 =
            { "-c", "src/test/resources/parameters/goodParams.json", "-m",
                "src/test/resources/main/DummyModelFile.json" };
        apexArguments.parse(args06);
        apexArguments.validate();

        final String[] args07 =
            { "-c", "parameters/goodParams.json", "-m", "main/DummyModelFile.json" };

        apexArguments.parse(args07);
        apexArguments.validate();
    }

    @Test
    public void testCommandLineRelativeRootParameters() throws ApexException {
        final ApexCommandLineArguments apexArguments = new ApexCommandLineArguments();

        final String[] args00 =
            { "-c", "src/test/resources/parameters/goodParams.json", "-rfr", "zooby" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args00);
            apexArguments.validate();
        }).hasMessageContaining("zooby\" does not exist or is not a directory");
        final String[] args01 =
            { "-rfr" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args01);
            apexArguments.validate();
        }).hasMessage("invalid command line arguments specified : Missing argument for option: rfr");
        final String[] args02 =
            { "-c", "src/test/resources/parameters/goodParams.json", "-rfr", "pom.xml" };
        assertThatThrownBy(() -> {
            apexArguments.parse(args02);
            apexArguments.validate();
        }).hasMessageContaining("pom.xml\" does not exist or is not a directory");
        final String[] args03 =
            { "-c", "src/test/resources/parameters/goodParams.json", "-rfr", "target" };

        apexArguments.parse(args03);
        apexArguments.validate();

        final String[] args04 =
            { "-c", "src/test/resources/parameters/goodParamsRelative.json", "-rfr", "src/test/resources" };

        apexArguments.parse(args04);
        apexArguments.validate();

    }
}
