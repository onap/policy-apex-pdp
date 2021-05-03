/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.service.engine.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the ApexMain class.
 */
public class ApexMainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream stdout = System.out;
    private ApexMain apexMain1;
    private ApexMain apexMain2;

    /**
     * Method for set up before each test.
     *
     * @throws Exception if an error occurs
     */
    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
    }

    /**
     * Method for cleanup after each test.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void teardown() throws Exception {
        if (null != apexMain1) {
            apexMain1.shutdown();
        }
        if (null != apexMain2) {
            apexMain2.shutdown();
        }
        System.setOut(stdout);
    }

    @Test
    public void testNullParameters() throws ApexException {
        ApexMain.main(null);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("Tosca Policy file was not specified as an argument"));
        assertThat(outContent.toString())
            .contains("Tosca Policy file was not specified as an argument");
    }

    @Test
    public void testBadArguments() throws ApexException {
        String[] args = { "-whee" };

        apexMain1 = new ApexMain(args);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("invalid command line arguments specified"));
        assertNotNull(apexMain1);
    }

    @Test
    public void testHelp() throws ApexException {
        String[] args = { "-h" };

        apexMain1 = new ApexMain(args);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
        assertNotNull(apexMain1);
    }

    @Test
    public void testBadParameters() throws ApexException {
        String[] args = { "-p", "src/test/resources/parameters/badParams.json" };

        apexMain1 = new ApexMain(args);
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("item has status INVALID"));
        assertNotNull(apexMain1);
    }

    @Test
    public void testCorrectParameters() throws ApexException {
        String[] args = {"-p", "src/test/resources/parameters/correctParams.json"};

        apexMain1 = new ApexMain(args);
        assertEquals("MyApexEngine", apexMain1.getApexParameters().getEngineServiceParameters().getName());
        await().atMost(200, TimeUnit.MILLISECONDS)
            .until(() -> outContent.toString().contains("Added the action listener to the engine"));
        assertTrue(apexMain1.isAlive());
    }

    @Test
    public void testJavaProperties() throws ApexException {
        String[] args = {"-p", "src/test/resources/parameters/correctParamsJavaProperties.json"};

        apexMain1 = new ApexMain(args);
        assertEquals("MyApexEngine", apexMain1.getApexParameters().getEngineServiceParameters().getName());

        assertEquals("trust-store-file", System.getProperty("javax.net.ssl.trustStore"));
        assertEquals("Pol1cy_0nap", System.getProperty("javax.net.ssl.trustStorePassword"));
        await().atMost(10000, TimeUnit.MILLISECONDS)
            .until(() -> outContent.toString().contains("Added the action listener to the engine"));
    }

    @Test
    public void testCorrectParametersWithMultiplePolicies() throws ApexException {
        String[] args1 = {"-p", "src/test/resources/parameters/correctParams.json"};
        String[] args2 = {"-p", "src/test/resources/parameters/correctParams2.json"};
        apexMain1 = new ApexMain(args1);
        apexMain2 = new ApexMain(args2);
        assertEquals("MyApexEngine", apexMain1.getApexParameters().getEngineServiceParameters().getName());
        assertEquals("MyApexEngine2", apexMain2.getApexParameters().getEngineServiceParameters().getName());
        assertTrue(apexMain1.isAlive());
        assertTrue(apexMain2.isAlive());
        final String outString = outContent.toString();
        assertThat(outString).contains("Added the action listener to the engine")
            .contains("Created apex engine MyApexEngine").contains("Created apex engine MyApexEngine2");
        ModelService.clear();
        ParameterService.clear();
    }

    @Test
    public void testInCorrectParametersWithMultiplePolicies() throws ApexException {
        String[] args = {"-p", "src/test/resources/parameters/correctParams.json"};
        apexMain1 = new ApexMain(args);
        apexMain2 = new ApexMain(args);
        assertTrue(apexMain1.isAlive());
        assertFalse(apexMain2.isAlive());
        final String outString = outContent.toString();
        assertThat(outString).contains("start of Apex service failed because this"
            + " policy has the following duplicate I/O parameters: [TheFileConsumer1]/[FirstProducer]");
    }

    @Test
    public void testInvalidArgsWithMultiplePolicies() throws ApexException {
        String[] args = {"-c", "file1", "-m", "file2"};
        apexMain1 = new ApexMain(args);
        assertFalse(apexMain1.isAlive());
        final String outString = outContent.toString();
        assertThat(outString).contains("Arguments validation failed", "start of Apex service failed");
    }
}
