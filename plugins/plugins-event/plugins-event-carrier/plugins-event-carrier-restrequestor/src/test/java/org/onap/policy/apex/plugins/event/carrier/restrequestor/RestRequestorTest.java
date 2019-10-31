/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.restrequestor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;

/**
 * The Class TestRestRequestor.
 */
public class RestRequestorTest {
    private static final String BASE_URI = "http://localhost:32801/TestRESTRequestor";
    private static final int PORT = 32801;
    private static HttpServletServer server;

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        server = HttpServletServerFactoryInstance.getServerFactory().build(
            null, false, null, PORT, "/TestRESTRequestor", false, false);

        server.addServletClass(null, SupportRestRequestorEndpoint.class.getName());
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
    @AfterClass
    public static void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }

        new File("src/test/resources/events/EventsOut.json").delete();
        new File("src/test/resources/events/EventsOutMulti0.json").delete();
        new File("src/test/resources/events/EventsOutMulti1.json").delete();
    }

    /**
     * Reset counters.
     */
    @Before
    public void resetCounters() {
        SupportRestRequestorEndpoint.resetCounters();
    }

    /**
     * Test rest requestor get.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorGet() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FileGet.json" };
        final ApexMain apexMain = new ApexMain(args);

        Response response = null;

        // Wait for the required amount of events to be received or for 10 seconds
        Double getsSoFar = 0.0;
        for (int i = 0; i < 40; i++) {
            ThreadUtilities.sleep(100);

            response = client.target("http://localhost:32801/TestRESTRequestor/apex/event/Stats")
                            .request("application/json").get();

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            getsSoFar = Double.valueOf(jsonMap.get("GET").toString());

            if (getsSoFar >= 50.0) {
                break;
            }
        }

        apexMain.shutdown();
        client.close();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(Double.valueOf(50.0), getsSoFar);
    }

    /**
     * Test rest requestor get empty.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorGetEmpty() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FileGetEmpty.json" };
        final ApexMain apexMain = new ApexMain(args);

        Response response = null;

        // Wait for the required amount of events to be received or for 10 seconds
        Double getsSoFar = 0.0;
        for (int i = 0; i < 40; i++) {
            ThreadUtilities.sleep(100);

            response = client.target("http://localhost:32801/TestRESTRequestor/apex/event/Stats")
                            .request("application/json").get();

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            getsSoFar = Double.valueOf(jsonMap.get("GET").toString());

            if (getsSoFar >= 50.0) {
                break;
            }
        }

        apexMain.shutdown();
        client.close();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    /**
     * Test REST requestor put.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorPut() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FilePut.json" };
        final ApexMain apexMain = new ApexMain(args);

        // Wait for the required amount of events to be received or for 10 seconds
        Double putsSoFar = 0.0;

        Response response = null;
        for (int i = 0; i < 40; i++) {
            ThreadUtilities.sleep(100);

            response = client.target("http://localhost:32801/TestRESTRequestor/apex/event/Stats")
                            .request("application/json").get();

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            putsSoFar = Double.valueOf(jsonMap.get("PUT").toString());

            if (putsSoFar >= 50.0) {
                break;
            }
        }

        apexMain.shutdown();
        client.close();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Double.valueOf(50.0), putsSoFar);
    }

    /**
     * Test REST requestor post.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorPost() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FilePost.json" };
        final ApexMain apexMain = new ApexMain(args);

        // Wait for the required amount of events to be received or for 10 seconds
        Double postsSoFar = 0.0;
        for (int i = 0; i < 40; i++) {
            ThreadUtilities.sleep(100);

            final Response response = client.target("http://localhost:32801/TestRESTRequestor/apex/event/Stats")
                            .request("application/json").get();

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            postsSoFar = Double.valueOf(jsonMap.get("POST").toString());

            if (postsSoFar >= 50.0) {
                break;
            }
        }

        apexMain.shutdown();
        client.close();

        assertEquals(Double.valueOf(50.0), postsSoFar);
    }

    /**
     * Test REST requestor delete.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorDelete() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FileDelete.json" };
        final ApexMain apexMain = new ApexMain(args);

        // Wait for the required amount of events to be received or for 10 seconds
        Double deletesSoFar = 0.0;
        for (int i = 0; i < 40; i++) {
            ThreadUtilities.sleep(100);

            final Response response = client.target("http://localhost:32801/TestRESTRequestor/apex/event/Stats")
                            .request("application/json").get();

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            deletesSoFar = Double.valueOf(jsonMap.get("DELETE").toString());

            if (deletesSoFar >= 50.0) {
                break;
            }
        }

        apexMain.shutdown();
        client.close();

        assertEquals(Double.valueOf(50.0), deletesSoFar);
    }

    /**
     * Test REST requestor multi inputs.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorMultiInputs() throws MessagingException, ApexException, IOException {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FileGetMulti.json" };
        final ApexMain apexMain = new ApexMain(args);

        // Wait for the required amount of events to be received or for 10 seconds
        Double getsSoFar = 0.0;
        for (int i = 0; i < 40; i++) {
            ThreadUtilities.sleep(100);

            final Response response = client.target("http://localhost:32801/TestRESTRequestor/apex/event/Stats")
                            .request("application/json").get();

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            getsSoFar = Double.valueOf(jsonMap.get("GET").toString());

            if (getsSoFar >= 8.0) {
                break;
            }
        }

        apexMain.shutdown();
        client.close();

        assertEquals(Double.valueOf(8.0), getsSoFar);

        ThreadUtilities.sleep(1000);
    }

    /**
     * Test REST requestor producer alone.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorProducerAlone() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FileGetProducerAlone.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("REST Requestor producer (RestRequestorProducer) "
                        + "must run in peered requestor mode with a REST Requestor consumer"));
    }

    /**
     * Test REST requestor consumer alone.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestRequestorConsumerAlone() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/File2RESTRequest2FileGetConsumerAlone.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("peer \"RestRequestorProducer for peered mode REQUESTOR "
                        + "does not exist or is not defined with the same peered mode"));
    }
}
