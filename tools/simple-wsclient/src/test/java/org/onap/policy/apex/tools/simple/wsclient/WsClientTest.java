/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2024 Nordix Foundation.
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

package org.onap.policy.apex.tools.simple.wsclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

/**
 * Test the WsClient utility.
 */
class WsClientTest {

    @Test
    void testWsClient() {
        final String[] EventArgs =
            {"-h"};

        assertThatCode(() -> WsClientMain.main(EventArgs)).doesNotThrowAnyException();
    }

    @Test
    void testWsClientNoOptions() {
        assertThat(runWsClient(new String[] {})).contains("ws-client: starting simple event echo");
    }

    @Test
    void testWsClientBadOptions() {
        assertThat(runWsClient(new String[] {"-zabbu"})).contains("usage: ws-client");
    }

    @Test
    void testWsClientHelp() {
        assertThat(runWsClient(new String[] {"-h"})).contains("usage: ws-client");
    }

    @Test
    void testWsClientVersion() {
        assertThat(runWsClient(new String[] {"-v"})).contains("ws-client").doesNotContain("usage:");
    }

    @Test
    void testWsClientNoServerArg() {
        assertThat(runWsClient(new String[] {"-s"})).contains("ws-client");
    }

    @Test
    void testWsClientNoPortArg() {
        assertThat(runWsClient(new String[] {"-p"})).contains("usage: ws-client");
    }

    @Test
    void testWsClientBadPortArg() {
        assertThat(runWsClient(new String[] {"-p", "hello"})).contains("ws-client");
    }

    @Test
    void testWsClientBadServerArg() {
        assertThat(runWsClient(new String[] {"-s", "asdsadadasd:asdasdsadasd"})).contains("ws-client");
    }

    @Test
    void testWsClientConsole() {
        assertThat(runWsClient(new String[] {"-c", "-s", "AServerThatDoesntExist", "-p", "99999999"}))
            .contains("terminate the application typing");
    }

    @Test
    void testWsClientEcho() {
        assertThat(runWsClient(new String[] {"-s", "AServerThatDoesntExist", "-p", "99999999"})).contains(
            "Once started, the application will simply print out all received events to standard out");
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
