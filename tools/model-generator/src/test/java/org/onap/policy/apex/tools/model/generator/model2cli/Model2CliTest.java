/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.tools.model.generator.model2cli;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

/**
 * Test the Model2Cli utility.
 */
class Model2CliTest {
    @Test
    void testModel2Cli() {
        final String[] cliArgs = {"-h"};

        assertThatCode(() -> Model2CliMain.main(cliArgs)).doesNotThrowAnyException();
    }

    @Test
    void testModel2CliNoOptions() {
        final String[] cliArgs = new String[] {};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("gen-model2cli: no '-m' model file given, cannot proceed (try -h for help)"));
    }

    @Test
    void testModel2CliBadOptions() {
        assertThat(runModel2Cli(new String[] {"-zabbu"})).contains("usage: gen-model2cli");
    }

    @Test
    void testModel2CliHelp() {
        assertThat(runModel2Cli(new String[] {"-h"})).contains("usage: gen-model2cli");
    }

    @Test
    void testModel2CliVersion() {
        assertThat(runModel2Cli(new String[] {"-v"})).contains("gen-model2cli").doesNotContain("usage:");
    }

    @Test
    void testModel2CliOverwrite() throws IOException {
        File tempFile = File.createTempFile("AvroModel", ".apex");
        tempFile.deleteOnExit();

        final String[] cliArgs = {"-m", "src/test/resources/models/AvroModel.json", "-o", tempFile.getCanonicalPath()};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("gen-model2cli: error with '-o' option: \"file already exists\""));
    }

    @Test
    void testModel2CliAadm() throws IOException {
        testModel2CliModel("target/examples/models/AADM", "AADMPolicyModel");
    }

    @Test
    void testModel2CliAnomaly() throws IOException {
        testModel2CliModel("target/examples/models/Adaptive", "AnomalyDetectionPolicyModel");
    }

    @Test
    void testModel2CliAutoLearn() throws IOException {
        testModel2CliModel("target/examples/models/Adaptive", "AutoLearnPolicyModel");
    }

    @Test
    void testModel2CliJms() throws IOException {
        testModel2CliModel("target/examples/models/JMS", "JMSTestModel");
    }

    @Test
    void testModel2CliMfp() throws IOException {
        testModel2CliModel("target/examples/models/MyFirstPolicy/2", "MyFirstPolicyModel_0.0.1");
    }

    @Test
    void testModel2CliSample() throws IOException {
        testModel2CliModel("target/examples/models/SampleDomain", "SamplePolicyModelJAVASCRIPT");
    }

    /**
     * Run the application.
     *
     * @param cliArgs the command arguments
     * @return a string containing the command output
     */
    private String runModel2Cli(final String[] cliArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        new Model2CliMain(cliArgs, new PrintStream(baosOut, true), new PrintStream(baosErr, true));

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString;
    }

    /**
     * Test CLI generation.
     *
     * @param modelName the name of the model file
     */
    private void testModel2CliModel(final String modelPath, final String modelName) throws IOException {
        File tempFile = File.createTempFile(modelName, ".apex");
        tempFile.deleteOnExit();

        // @formatter:off
        final String[] cliArgs = {
            "-m",
            modelPath + "/" + modelName + ".json",
            "-o",
            tempFile.getCanonicalPath(),
            "-ow"
        };
        // @formatter:on
        runModel2Cli(cliArgs);

        assertTrue(tempFile.isFile());
        assertTrue(tempFile.length() > 0);
    }
}
