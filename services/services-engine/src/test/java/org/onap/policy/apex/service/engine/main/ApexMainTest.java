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

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test the ApexMain class.
 */
public class ApexMainTest {
    private PrintStream stdout = System.out;

    @Test
    public void testNullParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ApexMain.main(null);
        ThreadUtilities.sleep(200);

        final String outString = outContent.toString();

        System.setOut(stdout);

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

        System.setOut(stdout);

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

        System.setOut(stdout);

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

        System.setOut(stdout);

        assertTrue(outString.contains("parameter group has status INVALID"));
    }

    @Test
    public void testCorrectParameters() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-c", "src/test/resources/parameters/correctParams.json" };

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine", apexMain.getParameters().getEngineServiceParameters().getName());
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);

        assertTrue(outString.contains("Added the action listener to the engine"));
    }

    @Test
    public void testJavaProperties() throws ApexException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = { "-c", "src/test/resources/parameters/correctParamsJavaProperties.json" };

        final ApexMain apexMain = new ApexMain(args);
        assertEquals("MyApexEngine", apexMain.getParameters().getEngineServiceParameters().getName());

        assertEquals("trust-store-file", System.getProperty("javax.net.ssl.trustStore"));
        assertEquals("Pol1cy_0nap", System.getProperty("javax.net.ssl.trustStorePassword"));
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        ThreadUtilities.sleep(10000);
        final String outString = outContent.toString();

        System.setOut(stdout);

        assertTrue(outString.contains("Added the action listener to the engine"));
    }
}
