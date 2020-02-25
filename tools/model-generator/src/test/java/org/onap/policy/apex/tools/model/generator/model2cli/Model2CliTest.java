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

package org.onap.policy.apex.tools.model.generator.model2cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Test the Model2Cli utility.
 */
public class Model2CliTest {
    @Test
    public void testModel2Cli() {
        try {
            final String[] cliArgs = {"-h"};

            Model2CliMain.main(cliArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testModel2CliNoOptions() {
        final String[] cliArgs = new String[] {};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("gen-model2cli: no '-m' model file given, cannot proceed (try -h for help)"));
    }

    @Test
    public void testModel2CliBadOptions() {
        final String[] cliArgs = {"-zabbu"};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("usage: gen-model2cli"));
    }

    @Test
    public void testModel2CliHelp() {
        final String[] cliArgs = {"-h"};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("usage: gen-model2cli"));
    }

    @Test
    public void testModel2CliVersion() {
        final String[] cliArgs = {"-v"};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("gen-model2cli"));
    }

    @Test
    public void testModel2CliOverwrite() throws IOException {
        File tempFile = File.createTempFile("AvroModel", ".apex");
        tempFile.deleteOnExit();

        final String[] cliArgs = {"-m", "src/test/resources/models/AvroModel.json", "-o", tempFile.getCanonicalPath()};

        final String outputString = runModel2Cli(cliArgs);

        assertTrue(outputString.contains("gen-model2cli: error with '-o' option: \"file already exists\""));
    }

    @Test
    public void testModel2CliAvro() throws IOException {
        testModel2CliModel("target/examples/models/pcvs/vpnsla", "vpnsla");
    }

    @Test
    public void testModel2CliAadm() throws IOException {
        testModel2CliModel("target/examples/models/AADM", "AADMPolicyModel");
    }

    @Test
    public void testModel2CliAnomaly() {
        testModel2CliModel("target/examples/models/Adaptive", "AnomalyDetectionPolicyModel");
    }

    @Test
    public void testModel2CliAutoLearn() {
        testModel2CliModel("target/examples/models/Adaptive", "AutoLearnPolicyModel");
    }

    @Test
    public void testModel2CliJms() {
        testModel2CliModel("target/examples/models/JMS", "JMSTestModel");
    }

    @Test
    public void testModel2CliMfp() {
        testModel2CliModel("target/examples/models/MyFirstPolicy/2", "MyFirstPolicyModel");
    }

    @Test
    public void testModel2CliSample() {
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
    private void testModel2CliModel(final String modelPath, final String modelName) {
        try {
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
        } catch (Exception e) {
            fail("test should not throw an exception");
        }
    }
}
