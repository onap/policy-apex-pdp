/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicyIdentifier;

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

        String[] args = { "-p", "src/test/resources/parameters/correctParams.json" };

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine",
            apexMain.getApexParametersMap().values().iterator().next().getEngineServiceParameters().getName());
        await().atMost(200, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("Added the action listener to the engine"));
        apexMain.shutdown();
    }

    @Test
    public void testJavaProperties() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-p", "src/test/resources/parameters/correctParamsJavaProperties.json" };

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine",
            apexMain.getApexParametersMap().values().iterator().next().getEngineServiceParameters().getName());

        assertEquals("trust-store-file", System.getProperty("javax.net.ssl.trustStore"));
        assertEquals("Pol1cy_0nap", System.getProperty("javax.net.ssl.trustStorePassword"));
        await().atMost(10000, TimeUnit.MILLISECONDS).until(() -> outContent.toString()
                .contains("Added the action listener to the engine"));
        apexMain.shutdown();
    }

    @Test
    public void testCorrectParametersWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Map<ToscaPolicyIdentifier, String[]> argsMap = new HashMap<ToscaPolicyIdentifier, String[]>();
        String[] args = {"-p", "src/test/resources/parameters/correctParams.json"};
        argsMap.put(new ToscaPolicyIdentifier("id1", "v1"), args);
        final ApexMain apexMain = new ApexMain(argsMap);
        ApexParameters apexParam = (ApexParameters) apexMain.getApexParametersMap().values().toArray()[0];
        assertEquals("MyApexEngine", apexParam.getEngineServiceParameters().getName());
        apexMain.shutdown();
        final String outString = outContent.toString();
        assertThat(outString).contains("Added the action listener to the engine");
    }

    @Test
    public void testInCorrectParametersWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Map<ToscaPolicyIdentifier, String[]> argsMap = new HashMap<ToscaPolicyIdentifier, String[]>();
        String[] args = {"-p", "src/test/resources/parameters/correctParams.json"};
        argsMap.put(new ToscaPolicyIdentifier("id1", "v1"), args);
        argsMap.put(new ToscaPolicyIdentifier("id2", "v2"), args);
        final ApexMain apexMain = new ApexMain(argsMap);
        ApexParameters apexParam = (ApexParameters) apexMain.getApexParametersMap().values().toArray()[0];
        assertEquals("MyApexEngine", apexParam.getEngineServiceParameters().getName());
        apexMain.shutdown();
        final String outString = outContent.toString();
        assertThat(outString).contains("I/O Parameters [TheFileConsumer1]/[FirstProducer] for id2:v2 are duplicates. "
            + "So this policy is not executed");
        assertEquals(1, apexMain.getApexParametersMap().size()); // only id1:v1 is kept in the map, id2:v2 failed
    }

    @Test
    public void testInvalidArgsWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Map<ToscaPolicyIdentifier, String[]> argsMap = new HashMap<ToscaPolicyIdentifier, String[]>();
        String[] args = {"-c", "file1", "-m", "file2"};
        argsMap.put(new ToscaPolicyIdentifier("id1", "v1"), args);
        final ApexMain apexMain = new ApexMain(argsMap);
        final String outString = outContent.toString();
        apexMain.shutdown();
        assertThat(outString).contains("Arguments validation failed", "start of Apex service failed");
        assertThat(apexMain.getApexParametersMap()).isEmpty(); // No policy is running in the engine
    }
}
