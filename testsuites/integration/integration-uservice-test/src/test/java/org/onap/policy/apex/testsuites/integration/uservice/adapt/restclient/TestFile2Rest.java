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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestFile2Rest.
 */
public class TestFile2Rest {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestFile2Rest.class);

    private static final String BASE_URI = "http://localhost:32801/TestFile2Rest";
    private static HttpServer server;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final ResourceConfig rc = new ResourceConfig(TestRestClientEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        while (!server.isStarted()) {
            ThreadUtilities.sleep(50);
        }
    }

    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @AfterClass
    public static void tearDown() throws Exception {
        server.shutdown();
    }

    /**
     * Clear relative file root environment variable.
     */
    @Before
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    /**
     * Test file events post.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEventsPost() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "-rfr", "target", "-c", "target/examples/config/SampleDomain/File2RESTJsonEventPost.json" };
        final ApexMain apexMain = new ApexMain(args);

        Response response = null;

        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 100; i++) {
            ThreadUtilities.sleep(100);
            response = client.target("http://localhost:32801/TestFile2Rest/apex/event/Stats")
                            .request("application/json").get();

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            if ((double) jsonMap.get("POST") == 100) {
                break;
            }
        }

        apexMain.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    /**
     * Test file events put.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testFileEventsPut() throws MessagingException, ApexException, IOException {
        final String[] args =
            { "-rfr", "target", "-c", "target/examples/config/SampleDomain/File2RESTJsonEventPut.json" };
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response response = null;

        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 100; i++) {
            ThreadUtilities.sleep(100);
            response = client.target("http://localhost:32801/TestFile2Rest/apex/event/Stats")
                            .request("application/json").get();

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            if ((double) jsonMap.get("PUT") == 100) {
                break;
            }
        }

        apexMain.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
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
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/File2RESTJsonEventNoURL.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("NoUrl-OUTSTRING=\n" + outString + "\nEnd-NoUrl");
        assertTrue(outString.contains(" no URL has been set for event sending on REST client"));
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
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/File2RESTJsonEventBadURL.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(2000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("BadUrl-OUTSTRING=\n" + outString + "\nEnd-BadUrl");
        assertTrue(outString.contains(
                        "send of event to URL \"http://localhost:32801/TestFile2Rest/apex/event/Bad\" using HTTP \"POST\" failed with status code 404"));
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
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/File2RESTJsonEventBadHTTPMethod.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("BadHttpMethod-OUTSTRING=\n" + outString + "\nEnd-BadHttpMethod");
        assertTrue(outString.contains(
                        "specified HTTP method of \"DELETE\" is invalid, only HTTP methods \"POST\" and \"PUT\" "
                                        + "are supproted for event sending on REST client producer"));
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
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/File2RESTJsonEventPostBadResponse.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(2000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("BadResponse-OUTSTRING=\n" + outString + "\nEnd-BadResponse");
        assertTrue(outString.contains(
                        "send of event to URL \"http://localhost:32801/TestFile2Rest/apex/event/PostEventBadResponse\""
                                        + " using HTTP \"POST\" failed with status code 400"));
    }
}
