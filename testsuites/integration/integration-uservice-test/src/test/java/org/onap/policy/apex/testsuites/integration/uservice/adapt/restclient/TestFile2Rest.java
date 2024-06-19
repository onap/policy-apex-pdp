/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2023-2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestFile2Rest.
 */
class TestFile2Rest {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestFile2Rest.class);

    private static final int PORT = 32801;
    private static HttpServletServer server;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;
    private ApexMain apexMain;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeAll
    static void setUp() throws Exception {
        server = HttpServletServerFactoryInstance.getServerFactory().build("TestFile2Rest", false, null, PORT, false,
            "/TestFile2Rest", false, false);

        server.addServletClass(null, TestRestClientEndpoint.class.getName());
        server.setSerializationProvider(GsonMessageBodyHandler.class.getName());

        server.start();

        if (!NetworkUtil.isTcpPortOpen("localHost", PORT, 60, 500L)) {
            throw new IllegalStateException("port " + PORT + " is still not in use");
        }
    }

    /**
     * Tear down.
     */
    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Before test.
     */
    @BeforeEach
    void beforeTest() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * After test.
     *
     * @throws ApexException the exception.
     */
    @AfterEach
    void afterTest() throws ApexException {
        if (null != apexMain) {
            apexMain.shutdown();
        }
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     * Test file events post.
     *
     * @throws ApexException the apex exception
     */
    @Test
    void testFileEventsPost() throws ApexException {
        final Client client = ClientBuilder.newClient();

        // @formatter:off
        final String[] args = {
            "-rfr",
            "target",
            "-c",
            "target/examples/config/SampleDomain/File2RESTJsonEventPost.json"
        };
        // @formatter:on
        final ApexMain apexMainTemp = new ApexMain(args);

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

            @SuppressWarnings("unchecked") final Map<String, Object> jsonMap =
                new Gson().fromJson(responseString, Map.class);
            if ((double) jsonMap.get("POST") == 100) {
                break;
            }
        }

        apexMainTemp.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        client.close();
    }

    /**
     * Test file events put.
     *
     * @throws ApexException the apex exception
     */
    @Test
    void testFileEventsPut() throws ApexException {
        // @formatter:off
        final String[] args = {
            "-rfr",
            "target",
            "-c",
            "target/examples/config/SampleDomain/File2RESTJsonEventPut.json"
        };
        // @formatter:on
        final ApexMain apexMainTemp = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response response = null;

        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 20; i++) {
            ThreadUtilities.sleep(300);
            response = client.target("http://localhost:32801/TestFile2Rest/apex/event/Stats")
                .request("application/json").get();

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked") final Map<String, Object> jsonMap =
                new Gson().fromJson(responseString, Map.class);
            if ((double) jsonMap.get("PUT") == 20) {
                break;
            }
        }

        apexMainTemp.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        client.close();
    }

    /**
     * Test file events no url.
     */
    @Test
    void testFileEventsNoUrl() {

        final String[] args = {"src/test/resources/prodcons/File2RESTJsonEventNoURL.json"};
        apexMain = new ApexMain(args);
        final String outString = outContent.toString();
        LOGGER.info("NoUrl-OUTSTRING=\n {} \nEnd-NoUrl", outString);
        assertThat(outString).contains(" no URL has been set for event sending on RESTCLIENT");
    }

    /**
     * Test file events bad url.
     */
    @Test
    void testFileEventsBadUrl() {

        final String[] args = {"src/test/resources/prodcons/File2RESTJsonEventBadURL.json"};
        apexMain = new ApexMain(args);
        await().atMost(5, TimeUnit.SECONDS)
            .until(() -> outContent.toString()
                .contains("send of event to URL \"http://localhost:32801/TestFile2Rest/apex/event/Bad\" "
                    + "using HTTP \"POST\" failed with status code 404"));
        assertTrue(apexMain.isAlive());
        LOGGER.info("BadUrl-OUTSTRING=\n {} \nEnd-BadUrl", outContent.toString());
    }

    /**
     * Test file events bad http method.
     */
    @Test
    void testFileEventsBadHttpMethod() {

        final String[] args = {"src/test/resources/prodcons/File2RESTJsonEventBadHTTPMethod.json"};
        apexMain = new ApexMain(args);
        final String outString = outContent.toString();
        LOGGER.info("BadHttpMethod-OUTSTRING=\n {} \nEnd-BadHttpMethod", outString);
        assertThat(outString)
            .contains("specified HTTP method of \"DELETE\" is invalid, only HTTP methods \"POST\" and \"PUT\" "
                + "are supported for event sending on REST client producer");
    }

    /**
     * Test file events bad response.
     */
    @Test
    void testFileEventsBadResponse() {

        final String[] args = {"src/test/resources/prodcons/File2RESTJsonEventPostBadResponse.json"};
        apexMain = new ApexMain(args);

        await().atMost(5, TimeUnit.SECONDS)
            .until(() -> outContent.toString().contains(
                "send of event to URL \"http://localhost:32801/TestFile2Rest/apex/event/PostEventBadResponse\""
                    + " using HTTP \"POST\" failed with status code 400"));
        assertTrue(apexMain.isAlive());
        LOGGER.info("BadResponse-OUTSTRING=\n {} \nEnd-BadResponse", outContent.toString());
    }
}
