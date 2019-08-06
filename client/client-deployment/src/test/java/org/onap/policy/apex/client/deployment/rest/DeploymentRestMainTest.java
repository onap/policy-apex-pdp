/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.client.deployment.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;

/**
 * Test the periodic event manager utility.
 */
public class DeploymentRestMainTest {
    @Test
    public void testDeploymentClientBad() throws Exception {
        final String[] eventArgs = {"-z"};
        assertThatCode(() -> ApexDeploymentRestMain.main(eventArgs)).doesNotThrowAnyException();
    }

    @Test
    public void testDeploymentClientOk() {
        final String[] eventArgs = {"-t", "1"};
        assertThatCode(() -> ApexDeploymentRestMain.main(eventArgs)).doesNotThrowAnyException();
    }

    @Test
    public void testDeploymentClientNoOptions() {
        final String[] eventArgs = new String[]
            {};
        assertThat(testApexDeploymentRestMainConstructor(eventArgs)).isEqualTo("*** StdOut ***\n\n*** StdErr ***\n");
    }

    @Test
    public void testDeploymentClientBadOptions() {
        final String[] eventArgs =
            { "-zabbu" };
        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[null], State=STOPPED) "
                        + "parameter error, invalid command line arguments specified "
                        + ": Unrecognized option: -zabbu");
    }

    @Test
    public void testDeploymentClientHelp() {
        final String[] eventArgs =
            { "-h" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "usage: org.onap.policy.apex.client.deployment.rest.ApexDeploymentRestMain [options...]");

    }

    @Test
    public void testDeploymentClientPortBad() {
        final String[] eventArgs =
            { "-p", "hello" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[null], State=STOPPED) "
                        + "parameter error, error parsing argument \"port\" :For input string: \"hello\"");

    }

    @Test
    public void testDeploymentClientPortNegative() {
        final String[] eventArgs =
            { "-p", "-1" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[ApexDeploymentRestParameters: "
                        + "URI=http://localhost:-1/apexservices/, TTL=-1sec], State=STOPPED) parameters invalid, "
                        + "port must be greater than 1023 and less than 65536");

    }

    @Test
    public void testDeploymentClientTtlTooSmall() {
        final String[] eventArgs =
            { "-t", "-2" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[ApexDeploymentRestParameters: "
                        + "URI=http://localhost:18989/apexservices/, TTL=-2sec], State=STOPPED) parameters invalid, "
                        + "time to live must be greater than -1 (set to -1 to wait forever)");

    }

    @Test
    public void testDeploymentClientTooManyPars() {
        final String[] eventArgs =
            { "-t", "10", "-p", "12344", "aaa", "bbb" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[null], State=STOPPED) "
                        + "parameter error, too many command line arguments specified : [aaa, bbb]");
    }

    @Test
    public void testDeploymentClientTtlNotNumber() {
        final String[] eventArgs =
            { "-t", "timetolive" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[null], State=STOPPED) "
                        + "parameter error, error parsing argument \"time-to-live\" :"
                        + "For input string: \"timetolive\"");

    }

    @Test
    public void testDeploymentClientPortTooBig() {
        final String[] eventArgs =
            { "-p", "65536" };

        Throwable thrown = catchThrowable(() -> new ApexDeploymentRestMain(eventArgs, System.out));

        assertThat(thrown).isInstanceOf(Exception.class).hasMessageContaining(
                "Apex Services REST endpoint (ApexDeploymentRestMain: Config=[ApexDeploymentRestParameters: "
                        + "URI=http://localhost:65536/apexservices/, TTL=-1sec], State=STOPPED) parameters invalid, "
                        + "port must be greater than 1023 and less than 65536");
    }

    @Test
    public void testDeploymentClientDefaultPars() {
        assertThatCode(() -> {
            ApexDeploymentRest monRest = new ApexDeploymentRest();
            monRest.shutdown();
        }).doesNotThrowAnyException();

    }

    @Test
    public void testDeploymentOneSecStart() {
        final String[] eventArgs =
            { "-t", "1" };

        assertThatCode(() -> {
            ApexDeploymentRestMain monRestMain = new ApexDeploymentRestMain(eventArgs, System.out);
            monRestMain.init();
            monRestMain.shutdown();
        }).doesNotThrowAnyException();

    }

    @Test
    public void testDeploymentForeverStart() {
        final String[] eventArgs =
            { "-t", "-1" };

        ApexDeploymentRestMain monRestMain = new ApexDeploymentRestMain(eventArgs, System.out);

        Thread monThread = new Thread() {
            @Override
            public void run() {
                monRestMain.init();
            }
        };

        assertThatCode(() -> {
            monThread.start();
            ThreadUtilities.sleep(2000);
            monRestMain.shutdown();
        }).doesNotThrowAnyException();

    }

    /**
     * Run the application.
     *
     * @param eventArgs the command arguments
     * @return a string containing the command output
     */
    private String testApexDeploymentRestMainConstructor(final String[] eventArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        new ApexDeploymentRestMain(eventArgs, new PrintStream(baosOut, true));

        InputStream testInput = new ByteArrayInputStream("Test Data for Input to WS".getBytes());
        System.setIn(testInput);

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString;
    }
}
