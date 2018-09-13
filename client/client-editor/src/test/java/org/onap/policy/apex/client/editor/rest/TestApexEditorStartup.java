/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.client.editor.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Test;
import org.onap.policy.apex.client.editor.rest.ApexEditorMain.EditorState;

/**
 * Test Apex Editor Startup.
 */
public class TestApexEditorStartup {
    // CHECKSTYLE:OFF: MagicNumber

    /**
     * Test no args.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testNoArgs() throws IOException, InterruptedException {
        final String[] args = new String[] {};

        final String outString = runEditor(args);
        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=-1sec], "
                + "State=READY) starting at http://localhost:18989/apexservices/"));
        assertTrue(outString.contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=-1sec], "
                + "State=RUNNING) started at http://localhost:18989/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=-1sec],"
                + " State=STOPPED) shut down "));
    }

    /**
     * Test bad arg 0.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadArg0() throws IOException, InterruptedException {
        final String[] args = new String[] { "12321" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getLocalizedMessage().startsWith(
                    "Apex Editor REST endpoint (ApexEditorMain: Config=[null], State=STOPPED) parameter error,"
                    + " too many command line arguments specified : [12321]"));
        }
    }

    /**
     * Test bad arg 1.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadArg1() throws IOException, InterruptedException {
        final String[] args = new String[] { "12321 12322 12323" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getLocalizedMessage().startsWith(
                    "Apex Editor REST endpoint (ApexEditorMain: Config=[null], State=STOPPED) parameter error,"
                    + " too many command line arguments specified : [12321 12322 12323]"));
        }
    }

    /**
     * Test bad arg 2.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadArg2() throws IOException, InterruptedException {
        final String[] args = new String[] { "-z" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getLocalizedMessage().startsWith(
                    "Apex Editor REST endpoint (ApexEditorMain: Config=[null], State=STOPPED) parameter error,"
                    + " invalid command line arguments specified : Unrecognized option: -z"));
        }
    }

    /**
     * Test bad arg 3.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadArg3() throws IOException, InterruptedException {
        final String[] args = new String[] { "--hello" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getLocalizedMessage().startsWith(
                    "Apex Editor REST endpoint (ApexEditorMain: Config=[null], State=STOPPED) parameter error,"
                    + " invalid command line arguments specified : Unrecognized option: --hello"));
        }
    }


    /**
     * Test bad arg 4.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadArg4() throws IOException, InterruptedException {
        final String[] args = new String[] { "-l", "+++++" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getLocalizedMessage()
                    .startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                            + "Config=[ApexEditorParameters: URI=http://+++++:18989/apexservices/, TTL=-1sec], "
                            + "State=STOPPED) parameters invalid, listen address is not valid. "
                            + "Illegal character in hostname at index 7: http://+++++:18989/apexservices/"));
        }
    }

    /**
     * Test help 0.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testHelp0() throws IOException, InterruptedException {
        final String[] args = new String[] { "--help" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage()
                    .startsWith("usage: org.onap.policy.apex.client.editor.rest.ApexEditorMain [options...]"));
        }
    }

    /**
     * Test help 1.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testHelp1() throws IOException, InterruptedException {
        final String[] args = new String[] { "-h" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage()
                    .startsWith("usage: org.onap.policy.apex.client.editor.rest.ApexEditorMain [options...]"));
        }
    }

    /**
     * Test port arg.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testPortArgShJo() throws IOException, InterruptedException {
        final String[] args = new String[] { "-p12321" };

        final String outString = runEditor(args);

        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=-1sec], "
                + "State=READY) starting at http://localhost:12321/apexservices/"));
        assertTrue(outString.contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=-1sec], "
                + "State=RUNNING) started at http://localhost:12321/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=-1sec],"
                + " State=STOPPED) shut down "));
    }

    /**
     * Test port arg.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testPortArgShSe() throws IOException, InterruptedException {
        final String[] args = new String[] { "-p", "12321" };

        final String outString = runEditor(args);

        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=-1sec], "
                + "State=READY) starting at http://localhost:12321/apexservices/"));
        assertTrue(outString.contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=-1sec], "
                + "State=RUNNING) started at http://localhost:12321/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("(ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=-1sec],"
                + " State=STOPPED) shut down "));
    }


    /**
     * Test port arg.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testPortArgSpace() throws IOException, InterruptedException {
        final String[] args = new String[] { "-p 12321" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage().startsWith(
                    "Apex Editor REST endpoint (ApexEditorMain: Config=[null], State=STOPPED) parameter error,"
                    + " error parsing argument \"port\" :For input string: \" 12321\""));
        }
    }

    /**
     * Test bad port arg 0.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadPortArgs0() throws IOException, InterruptedException {
        final String[] args = new String[] { "-p0" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage()
                    .startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                            + "Config=[ApexEditorParameters: URI=http://localhost:0/apexservices/, TTL=-1sec], "
                            + "State=STOPPED) parameters invalid, port must be between 1024 and 65535"));
        }
    }

    /**
     * Test bad port arg 1023.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadPortArgs1023() throws IOException, InterruptedException {
        final String[] args = new String[] { "-p1023" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage()
                    .startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                            + "Config=[ApexEditorParameters: URI=http://localhost:1023/apexservices/, TTL=-1sec], "
                            + "State=STOPPED) parameters invalid, port must be between 1024 and 65535"));
        }
    }

    /**
     * Test bad port arg 65536.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testBadPortArgs65536() throws IOException, InterruptedException {
        final String[] args = new String[] { "-p65536" };

        try {
            runEditor(args);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertTrue(e.getMessage()
                    .startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                            + "Config=[ApexEditorParameters: URI=http://localhost:65536/apexservices/, TTL=-1sec], "
                            + "State=STOPPED) parameters invalid, port must be between 1024 and 65535"));
        }
    }

    /**
     * Test TTL arg 0.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testTtlArg0() throws IOException, InterruptedException {
        final String[] args = new String[] { "-t10" };

        final String outString = runEditor(args);

        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=10sec], "
                + "State=READY) starting at http://localhost:18989/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=10sec], State=RUNNING)"
                + " started"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=10sec], State=STOPPED)"
                + " shut down "));
    }

    /**
     * Test TTL arg 10.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testTtlArg1() throws IOException, InterruptedException {
        final String[] args = new String[] { "-t", "10", "-l", "localhost" };

        final String outString = runEditor(args);

        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=10sec], "
                + "State=READY) starting at http://localhost:18989/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=10sec], State=RUNNING)"
                + " started"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:18989/apexservices/, TTL=10sec], State=STOPPED)"
                + " shut down "));
    }

    /**
     * Test port TTL arg 0.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testPortTtlArg0() throws IOException, InterruptedException {
        final String[] args = new String[] { "-t", "10", "-p", "12321" };

        final String outString = runEditor(args);

        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=10sec], "
                + "State=READY) starting at http://localhost:12321/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=10sec], State=RUNNING)"
                + " started"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://localhost:12321/apexservices/, TTL=10sec], State=STOPPED)"
                + " shut down "));
    }


    /**
     * Test port TTL arg 10.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException if the test is interrupted
     */
    @Test
    public void testPortTtlArg1() throws IOException, InterruptedException {
        final String[] args = new String[] { "--time-to-live", "10", "--port", "12321", "--listen", "127.0.0.1" };

        final String outString = runEditor(args);

        assertTrue(outString.startsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://127.0.0.1:12321/apexservices/, TTL=10sec], "
                + "State=READY) starting at http://127.0.0.1:12321/apexservices/"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").contains("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://127.0.0.1:12321/apexservices/, TTL=10sec], State=RUNNING)"
                + " started"));
        assertTrue(outString.replaceAll("[\\r?\\n]+", " ").endsWith("Apex Editor REST endpoint (ApexEditorMain: "
                + "Config=[ApexEditorParameters: URI=http://127.0.0.1:12321/apexservices/, TTL=10sec], State=STOPPED)"
                + " shut down "));
    }

    /**
     * Run the editor for tests.
     *
     * @param args the args
     * @return the output string
     * @throws InterruptedException if the test is interrupted
     */
    private String runEditor(final String[] args) throws InterruptedException {
        final ByteArrayOutputStream outBaStream = new ByteArrayOutputStream();
        final PrintStream outStream = new PrintStream(outBaStream);

        final ApexEditorMain editorMain = new ApexEditorMain(args, outStream);

        // This test must be started in a thread because we want to intercept the output in cases where the editor is
        // started infinitely
        final Runnable testThread = new Runnable() {
            @Override
            public void run() {
                editorMain.init();
            }
        };
        new Thread(testThread).start();
        while (editorMain.getState().equals(EditorState.READY)
                || editorMain.getState().equals(EditorState.INITIALIZING)) {
            Thread.sleep(100);
        }

        editorMain.shutdown();
        final String outString = outBaStream.toString();
        System.out.println(outString);
        return outString;
    }
}
