/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
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
        ThreadUtilities.sleep(200);

        final String outString = outContent.toString();

        assertTrue(outString.contains("Apex configuration file was not specified as an argument"));
    }

    @Test
    public void testBadArguments() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-whee" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        assertTrue(outString.contains("invalid command line arguments specified : Unrecognized option: -whee"));
    }

    @Test
    public void testHelp() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-h" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        assertTrue(outString.contains("usage: org.onap.policy.apex.service.engine.main.ApexMain [options...]"));
    }

    @Test
    public void testBadParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-c", "src/test/resources/parameters/badParams.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        assertTrue(outString.contains("parameter group has status INVALID"));
    }

    @Test
    public void testCorrectParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-c", "src/test/resources/parameters/correctParams.json" };

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine",
            apexMain.getApexParametersMap().values().iterator().next().getEngineServiceParameters().getName());
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        assertTrue(outString.contains("Added the action listener to the engine"));
    }

    @Test
    public void testJavaProperties() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-c", "src/test/resources/parameters/correctParamsJavaProperties.json" };

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine",
            apexMain.getApexParametersMap().values().iterator().next().getEngineServiceParameters().getName());

        assertEquals("trust-store-file", System.getProperty("javax.net.ssl.trustStore"));
        assertEquals("Pol1cy_0nap", System.getProperty("javax.net.ssl.trustStorePassword"));
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        ThreadUtilities.sleep(10000);
        final String outString = outContent.toString();

        assertTrue(outString.contains("Added the action listener to the engine"));
    }

    @Test
    public void testCorrectParametersWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Map<ToscaPolicyIdentifier, String[]> argsMap = new HashMap<ToscaPolicyIdentifier, String[]>();
        String[] args = {"-c", "src/test/resources/parameters/correctParams.json", "-m",
            "src/test/resources/policymodels/SmallModel.json"};
        argsMap.put(new ToscaPolicyIdentifier("id1", "v1"), args);
        final ApexMain apexMain = new ApexMain(argsMap);
        ApexParameters apexParam = (ApexParameters) apexMain.getApexParametersMap().values().toArray()[0];
        assertEquals("MyApexEngine", apexParam.getEngineServiceParameters().getName());
        apexMain.shutdown();
        final String outString = outContent.toString();
        assertTrue(outString.contains("Added the action listener to the engine"));
    }

    @Test
    public void testInCorrectParametersWithMultiplePolicies() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Map<ToscaPolicyIdentifier, String[]> argsMap = new HashMap<ToscaPolicyIdentifier, String[]>();
        String[] args = {"-c", "src/test/resources/parameters/correctParams.json", "-m",
            "src/test/resources/policymodels/SmallModel.json"};
        argsMap.put(new ToscaPolicyIdentifier("id1", "v1"), args);
        argsMap.put(new ToscaPolicyIdentifier("id2", "v2"), args);
        final ApexMain apexMain = new ApexMain(argsMap);
        ApexParameters apexParam = (ApexParameters) apexMain.getApexParametersMap().values().toArray()[0];
        assertEquals("MyApexEngine", apexParam.getEngineServiceParameters().getName());
        apexMain.shutdown();
        final String outString = outContent.toString();
        assertTrue(outString.contains("I/O Parameters for id2:v2 has duplicates. So this policy is not executed"));
        assertTrue(apexMain.getApexParametersMap().size() == 1); // only id1:v1 is kept in the map, id2:v2 failed
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
        assertTrue(
            outString.contains("Arguments validation failed") && outString.contains("start of Apex service failed"));
        assertTrue(apexMain.getApexParametersMap().isEmpty()); // No policy is running in the engine
    }
}
