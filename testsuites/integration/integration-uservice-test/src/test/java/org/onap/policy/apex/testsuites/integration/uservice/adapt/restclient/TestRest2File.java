/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.restclient;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.onap.policy.common.utils.resources.TextFileUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestRest2File.
 */
public class TestRest2File {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestRest2File.class);

    private static final int PORT = 32801;
    private static HttpServletServer server;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;
    private ApexMain apexMain;

    /**
     * Before Test.
     */
    @Before
    public void beforeTest() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception {
        server = HttpServletServerFactoryInstance.getServerFactory().build("TestRest2File", false, null, PORT,
            "/TestRest2File", false, false);

        server.addServletClass(null, TestRestClientEndpoint.class.getName());
        server.setSerializationProvider(GsonMessageBodyHandler.class.getName());

        server.start();

        if (!NetworkUtil.isTcpPortOpen("localHost", PORT, 60, 500L)) {
            throw new IllegalStateException("port " + PORT + " is still not in use");
        }
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
    public void tearDown() throws Exception {
        if (null != apexMain) {
            apexMain.shutdown();
        }
        if (server != null) {
            server.stop();
        }
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     * Test rest events in.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestEventsIn() throws MessagingException, ApexException, IOException {
        final String[] args = {"-rfr", "target", "-p", "target/examples/config/SampleDomain/REST2FileJsonEvent.json"};

        apexMain = new ApexMain(args);
        await().atMost(5, TimeUnit.SECONDS).until(() -> apexMain.isAlive());
        apexMain.shutdown();
        final String outputEventText =
            TextFileUtils.getTextFileAsString("target/examples/events/SampleDomain/EventsOut.json");

        checkRequiredString(outputEventText,
            "04\",\n" + "  \"version\": \"0.0.1\",\n" + "  \"nameSpace\": \"org.onap.policy.apex.sample.events\"");
    }

    /**
     * Test file empty events.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEmptyEvents() throws MessagingException, ApexException, IOException {

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEmptyEvents.json"};
        apexMain = new ApexMain(args);
        await().atMost(5, TimeUnit.SECONDS).until(() -> outContent.toString().contains(
            "received an empty event from URL " + "\"http://localhost:32801/TestRest2File/apex/event/GetEmptyEvent\""));
        assertTrue(apexMain.isAlive());
    }

    /**
     * Test file events no url.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEventsNoUrl() throws MessagingException, ApexException, IOException {

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventNoURL.json"};
        apexMain = new ApexMain(args);
        final String outString = outContent.toString();

        checkRequiredString(outString, " no URL has been set for event sending on RESTCLIENT");
    }

    /**
     * Test file events bad url.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEventsBadUrl() throws MessagingException, ApexException, IOException {

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventBadURL.json"};
        apexMain = new ApexMain(args);

        await().atMost(5, TimeUnit.SECONDS).until(() -> outContent.toString().contains("reception of event from URL "
            + "\"http://localhost:32801/TestRest2File/apex/event/Bad\" failed with status code 404"));
        assertTrue(apexMain.isAlive());
    }

    /**
     * Test file events bad http method.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEventsBadHttpMethod() throws MessagingException, ApexException, IOException {

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventBadHTTPMethod.json"};
        apexMain = new ApexMain(args);

        final String outString = outContent.toString();

        checkRequiredString(outString, "specified HTTP method of \"POST\" is invalid, "
            + "only HTTP method \"GET\" is supported for event reception on REST client consumer");
    }

    /**
     * Test file events bad response.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEventsBadResponse() throws MessagingException, ApexException, IOException {

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventBadResponse.json"};
        apexMain = new ApexMain(args);
        await().atMost(5, TimeUnit.SECONDS)
            .until(() -> outContent.toString()
                .contains("reception of event from URL "
                    + "\"http://localhost:32801/TestRest2File/apex/event/GetEventBadResponse\" "
                    + "failed with status code 400 and message \""));
        assertTrue(apexMain.isAlive());
    }

    /**
     * Check if a required string exists in the output.
     *
     * @param outputEventText the text to examine
     * @param requiredString the string to search for
     */
    private void checkRequiredString(String outputEventText, String requiredString) {
        if (!outputEventText.contains(requiredString)) {
            LOGGER.error("\n***output text:\n" + outputEventText + "\n***");
            fail("\n***test output did not contain required string:\n" + requiredString + "\n***");
        }
    }
}
