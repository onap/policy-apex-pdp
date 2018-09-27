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

package org.onap.policy.apex.tools.simple.wsclient;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.Test;

/**
 * Test the WsClient utility.
 */
public class WsClientTest {
    @Test
    public void testWsClient() {
        try {
            final String[] EventArgs =
                { "-h" };

            WsClientMain.main(EventArgs);
        } catch (Exception exc) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testWsClientNoOptions() {
        final String[] EventArgs = new String[]
            {};

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("ws-client: starting simple event echo"));
    }

    @Test
    public void testWsClientBadOptions() {
        final String[] EventArgs =
            { "-zabbu" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("usage: ws-client"));
    }

    @Test
    public void testWsClientHelp() {
        final String[] EventArgs =
            { "-h" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("usage: ws-client"));
    }

    @Test
    public void testWsClientVersion() {
        final String[] EventArgs =
            { "-v" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("ws-client"));
    }

    @Test
    public void testWsClientNoServerArg() {
        final String[] EventArgs =
            { "-s" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("ws-client"));
    }

    @Test
    public void testWsClientNoPortArg() {
        final String[] EventArgs =
            { "-p" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("ws-client"));
    }

    @Test
    public void testWsClientBadPortArg() {
        final String[] EventArgs =
            { "-p", "hello" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("ws-client"));
    }

    @Test
    public void testWsClientBadServerArg() {
        final String[] EventArgs =
            { "-s", "asdsadadasd:asdasdsadasd@@" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("ws-client"));
    }

    @Test
    public void testWsClientConsole() {
        final String[] EventArgs =
            { "-c", "-s", "AServerThatDoesntExist", "-p", "99999999" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains("terminate the application typing"));
    }

    @Test
    public void testWsClientEcho() {
        final String[] EventArgs =
            { "-s", "AServerThatDoesntExist", "-p", "99999999" };

        final String outputString = runWsClient(EventArgs);

        assertTrue(outputString.contains(
                        "Once started, the application will simply print out all received events to standard out"));
    }

    /**
     * Run the application.
     * 
     * @param eventArgs the command arguments
     * @return a string containing the command output
     */
    private String runWsClient(final String[] eventArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        new WsClientMain(eventArgs, new PrintStream(baosOut, true));

        InputStream testInput = new ByteArrayInputStream("Test Data for Input to WS".getBytes());
        System.setIn(testInput);

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString;
    }
}
