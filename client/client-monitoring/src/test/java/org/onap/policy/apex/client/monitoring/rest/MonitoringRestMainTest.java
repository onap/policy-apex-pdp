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

package org.onap.policy.apex.client.monitoring.rest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 * Test the periodic event manager utility.
 */
public class MonitoringRestMainTest {
    @Test
    public void testMonitoringClientBad() {
        final String[] eventArgs = {"-z"};
        assertThatCode(() -> ApexMonitoringRestMain.main(eventArgs)).doesNotThrowAnyException();
    }

    @Test
    public void testMonitoringClientOk() {
        final String[] eventArgs = {"-t", "1"};
        assertThatCode(() -> ApexMonitoringRestMain.main(eventArgs)).doesNotThrowAnyException();
    }

    @Test
    public void testMonitoringClientNoOptions() {
        final String[] eventArgs = new String[] {};

        final String outputString = testApexMonitoringRestMainConstructor(eventArgs);

        System.err.println(outputString);
        assertEquals("*** StdOut ***\n\n*** StdErr ***\n", outputString);
    }

    @Test
    public void testMonitoringClientBadOptions() {
        final String[] eventArgs = {"-zabbu"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[null], State=STOPPED) "
                + "parameter error, invalid command line arguments specified " + ": Unrecognized option: -zabbu");
    }

    @Test
    public void testMonitoringClientHelp() {
        final String[] eventArgs = {"-h"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("usage: org.onap.policy.apex.client.monitoring.rest."
                    + "ApexMonitoringRestMain [options...]");
    }

    @Test
    public void testMonitoringClientPortBad() {
        final String[] eventArgs = {"-p", "hello"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[null], State=STOPPED) "
                    + "parameter error, error parsing argument \"port\" :For input string: \"hello\"");
    }

    @Test
    public void testMonitoringClientPortNegative() {
        final String[] eventArgs = {"-p", "-1"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[ApexMonitoringRest"
                + "Parameters: URI=http://localhost:-1/apexservices/, TTL=-1sec], State=STOPPED) parameters invalid, "
                + "port must be greater than 1023 and less than 65536");
    }

    @Test
    public void testMonitoringClientTtlTooSmall() {
        final String[] eventArgs = {"-t", "-2"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[ApexMonitoringRest"
                    + "Parameters: URI=http://localhost:18989/apexservices/, TTL=-2sec], State=STOPPED) parameters invalid, "
                    + "time to live must be greater than -1 (set to -1 to wait forever)");
    }

    @Test
    public void testMonitoringClientTooManyPars() {
        final String[] eventArgs = {"-t", "10", "-p", "12344", "aaa", "bbb"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[null], State=STOPPED) "
                    + "parameter error, too many command line arguments specified : [aaa, bbb]");
    }

    @Test
    public void testMonitoringClientTtlNotNumber() {
        final String[] eventArgs = {"-t", "timetolive"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[null], State=STOPPED) "
                    + "parameter error, error parsing argument \"time-to-live\" :" + "For input string: \""
                            + "timetolive\"");
    }

    @Test
    public void testMonitoringClientPortTooBig() {
        final String[] eventArgs = {"-p", "65536"};

        assertThatThrownBy(() -> new ApexMonitoringRestMain(eventArgs, System.out))
            .hasMessageContaining("Apex Services REST endpoint (ApexMonitoringRestMain: Config=[ApexMonitoring"
                + "RestParameters: URI=http://localhost:65536/apexservices/, TTL=-1sec], State=STOPPED) parameters invalid, "
                + "port must be greater than 1023 and less than 65536");
    }

    @Test
    public void testMonitoringClientDefaultPars() {
        ApexMonitoringRest monRest = new ApexMonitoringRest();
        assertNotNull(monRest);
        assertThatCode(() -> monRest.shutdown()).isNull();
    }

    @Test
    public void testMonitoringOneSecStart() {
        final String[] eventArgs = {"-t", "1"};

        ApexMonitoringRestMain monRestMain = new ApexMonitoringRestMain(eventArgs, System.out);
        assertNotNull(monRestMain);
        monRestMain.init();
        assertThatCode(() -> monRestMain.shutdown()).isNull();
    }

    @Test
    public void testMonitoringForeverStart() {
        final String[] eventArgs = {"-t", "-1"};

        ApexMonitoringRestMain monRestMain = new ApexMonitoringRestMain(eventArgs, System.out);

        Thread monThread = new Thread() {
            @Override
            public void run() {
                monRestMain.init();
            }
        };
        assertThatCode(() -> {
            monThread.start();
            await().atMost(6, TimeUnit.SECONDS)
                .until(() -> monRestMain.getState().equals(ApexMonitoringRestMain.ServicesState.RUNNING));
            monRestMain.shutdown();
        }).doesNotThrowAnyException();
    }

    /**
     * Run the application.
     *
     * @param eventArgs the command arguments
     * @return a string containing the command output
     */
    private String testApexMonitoringRestMainConstructor(final String[] eventArgs) {
        final ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
        final ByteArrayOutputStream baosErr = new ByteArrayOutputStream();

        new ApexMonitoringRestMain(eventArgs, new PrintStream(baosOut, true));

        InputStream testInput = new ByteArrayInputStream("Test Data for Input to WS".getBytes());
        System.setIn(testInput);

        String outString = baosOut.toString();
        String errString = baosErr.toString();

        return "*** StdOut ***\n" + outString + "\n*** StdErr ***\n" + errString;
    }
}
