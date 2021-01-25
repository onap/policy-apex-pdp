/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test the ApexMain class.
 */
public class ApexMainTest {
    private PrintStream stdout = System.out;

    /**
     * Method for cleanup after each test.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void teardown() throws Exception {
        System.setOut(stdout);
    }

    @Test
    public void testNullParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ApexMain.main(null);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("Tosca Policy file was not specified as an argument"));
        assertThat(outContent.toString())
            .contains("Tosca Policy file was not specified as an argument");
    }

    @Test
    public void testBadArguments() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-whee" };

        final ApexMain apexMain = new ApexMain(args);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("invalid command line arguments specified : Unrecognized option: -whee"));
        assertNotNull(apexMain);
        apexMain.shutdown();
    }

    @Test
    public void testHelp() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-h" };

        final ApexMain apexMain = new ApexMain(args);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        assertNotNull(apexMain);
        apexMain.shutdown();
    }

    @Test
    public void testBadParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-p", "src/test/resources/parameters/badParams.json" };

        final ApexMain apexMain = new ApexMain(args);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("parameter group has status INVALID"));
        assertNotNull(apexMain);
        apexMain.shutdown();
    }

    @Test
    public void testCorrectParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"-p", "src/test/resources/parameters/correctParams.json"};

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine", apexMain.getApexParameters().getEngineServiceParameters().getName());
        await().atMost(200, TimeUnit.MILLISECONDS)
            .until(() -> outContent.toString().contains("Added the action listener to the engine"));
        assertTrue(apexMain.isAlive());
        apexMain.shutdown();
    }

    @Test
    public void testJavaProperties() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"-p", "src/test/resources/parameters/correctParamsJavaProperties.json"};

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine", apexMain.getApexParameters().getEngineServiceParameters().getName());

        assertEquals("trust-store-file", System.getProperty("javax.net.ssl.trustStore"));
        assertEquals("Pol1cy_0nap", System.getProperty("javax.net.ssl.trustStorePassword"));
        await().atMost(10000, TimeUnit.MILLISECONDS)
            .until(() -> outContent.toString().contains("Added the action listener to the engine"));
        apexMain.shutdown();
    }

    @Test
    public void testCorrectParametersWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String[] args1 = {"-p", "src/test/resources/parameters/correctParams.json"};
        String[] args2 = {"-p", "src/test/resources/parameters/correctParams2.json"};
        final ApexMain apexMain1 = new ApexMain(args1);
        final ApexMain apexMain2 = new ApexMain(args2);
        assertEquals("MyApexEngine", apexMain1.getApexParameters().getEngineServiceParameters().getName());
        assertEquals("MyApexEngine2", apexMain2.getApexParameters().getEngineServiceParameters().getName());
        final String outString = outContent.toString();
        assertThat(outString).contains("Added the action listener to the engine")
            .contains("Created apex engine MyApexEngine").contains("Created apex engine MyApexEngine2");
        assertTrue(apexMain1.isAlive());
        assertTrue(apexMain2.isAlive());
        apexMain1.shutdown();
        apexMain2.shutdown();
    }

    @Test
    public void testInCorrectParametersWithMultiplePolicies() throws ApexException {
        String[] args = {"-p", "src/test/resources/parameters/correctParams.json"};
        final ApexMain apexMain1 = new ApexMain(args);
        assertThatThrownBy(() -> new ApexMain(args)).hasMessage("start of Apex service failed because this"
            + " policy has the following duplicate I/O parameters: [TheFileConsumer1]/[FirstProducer]");
        apexMain1.shutdown();
    }

    @Test
    public void testInvalidArgsWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String[] args = {"-c", "file1", "-m", "file2"};
        final ApexMain apexMain = new ApexMain(args);
        final String outString = outContent.toString();
        apexMain.shutdown();
        assertThat(outString).contains("Arguments validation failed", "start of Apex service failed");
        assertFalse(apexMain.isAlive()); // No policy is running in the engine
    }
}
