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

package org.onap.policy.apex.core.deployment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Test the periodic event manager utility.
 */
public class PeriodicEventManagerTest {
    @Test
    public void testPeroidicEventManager() {
        try {
            final String[] EventArgs =
                { "-h" };

            PeriodicEventManager.main(EventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testPeroidicEventManagerNoOptions() {
        final String[] EventArgs = new String[]
            {};

        final String outputString = runPeriodicEventManager(EventArgs);

        assertTrue(outputString
                        .contains("usage: Deployer <server address> <port address> <start/stop> <periods in ms>"));
    }

    @Test
    public void testPeroidicEventManagerBadOptions() {
        final String[] EventArgs =
            { "-zabbu" };

        final String outputString = runPeriodicEventManager(EventArgs);

        assertTrue(outputString
                        .contains("usage: Deployer <server address> <port address> <start/stop> <periods in ms>"));
    }

    @Test
    public void testPeroidicEventManagerNonNumeric3() {
        final String[] EventArgs =
            { "aaa", "bbb", "ccc", "ddd" };

        try {
            runPeriodicEventManager(EventArgs);
        } catch (NumberFormatException nfe) {
            assertEquals("For input string: \"bbb\"", nfe.getMessage());
        }
    }

    @Test
    public void testPeroidicEventManagerNonNumeric2() {
        final String[] EventArgs =
            { "aaa", "12345", "ccc", "1000" };

        try {
            runPeriodicEventManager(EventArgs);
        } catch (NumberFormatException nfe) {
            assertEquals("For input string: \"ddd\"", nfe.getMessage());
        }
    }

    @Test
    public void testPeroidicEventManagerStart() {
        final String[] EventArgs =
            { "localhost", "12345", "start", "1000" };

        final String outputString = runPeriodicEventManager(EventArgs);

        assertTrue(outputString.contains("\n*** StdErr ***\n\n*** exception ***"));
    }


    @Test
    public void testPeroidicEventManagerStop() {
        final String[] EventArgs =
            { "localhost", "12345", "stop", "1000" };

        final String outputString = runPeriodicEventManager(EventArgs);

        assertTrue(outputString.contains("\n*** StdErr ***\n\n*** exception ***"));
    }

    /**
     * Run the application.
     * 
     * @param eventArgs the command arguments
     * @return a string containing the command output
     */
    private String runPeriodicEventManager(final String[] eventArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        PeriodicEventManager peManager = new PeriodicEventManager(eventArgs, new PrintStream(baosOut, true));

        String exceptionString = "";
        try {
            peManager.init();
        } catch (ApexDeploymentException ade) {
            exceptionString = ade.getCascadedMessage();
        }

        InputStream testInput = new ByteArrayInputStream("Test Data for Input to WS".getBytes());
        System.setIn(testInput);

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString + "\n*** exception ***\n"
                        + exceptionString;
    }
}
