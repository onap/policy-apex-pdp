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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.restserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class TestRestServer.
 */
public class TestRestServer {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestRestServer.class);

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    private static int eventsSent = 0;

    /**
     * Clear relative file root environment variable.
     */
    @Before
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    /**
     * Test rest server put.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRestServerPut() throws MessagingException, ApexException, IOException {
        LOGGER.info("testRestServerPut start");

        final String[] args =
            { "-rfr", "target", "-c", "target/examples/config/SampleDomain/RESTServerJsonEvent.json" };
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response response = null;
        Map<String, Object> jsonMap = null;

        for (int i = 0; i < 20; i++) {
            response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                            .put(Entity.json(getEvent()));

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            jsonMap = new Gson().fromJson(responseString, Map.class);
        }

        apexMain.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
        LOGGER.info("testRestServerPut end");
    }

    /**
     * Test rest server post.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRestServerPost() throws MessagingException, ApexException, IOException {
        final String[] args =
            { "-rfr", "target", "-c", "target/examples/config/SampleDomain/RESTServerJsonEvent.json" };
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response response = null;
        Map<String, Object> jsonMap = null;

        for (int i = 0; i < 20; i++) {
            response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                            .post(Entity.json(getEvent()));

            if (Response.Status.OK.getStatusCode() != response.getStatus()) {
                break;
            }

            final String responseString = response.readEntity(String.class);

            jsonMap = new Gson().fromJson(responseString, Map.class);
        }

        apexMain.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
    }

    /**
     * Test rest server get status.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerGetStatus() throws MessagingException, ApexException, IOException {
        final String[] args =
            { "-rfr", "target", "-c", "target/examples/config/SampleDomain/RESTServerJsonEvent.json" };
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response postResponse = null;
        Response putResponse = null;

        // trigger 10 POST & PUT events
        for (int i = 0; i < 10; i++) {
            postResponse = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                            .request("application/json").post(Entity.json(getEvent()));
            if (Response.Status.OK.getStatusCode() != postResponse.getStatus()) {
                break;
            }
            putResponse = client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                            .put(Entity.json(getEvent()));

            if (Response.Status.OK.getStatusCode() != putResponse.getStatus()) {
                break;
            }
        }

        final Response statResponse = client.target("http://localhost:23324/apex/FirstConsumer/Status")
                        .request("application/json").get();

        final String responseString = statResponse.readEntity(String.class);

        apexMain.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), statResponse.getStatus());

        @SuppressWarnings("unchecked")
        final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
        assertEquals("[FirstConsumer", ((String) jsonMap.get("INPUTS")).substring(0, 14));
        assertEquals(1.0, jsonMap.get("STAT"));
        assertTrue((double) jsonMap.get("POST") >= 10.0);
        assertTrue((double) jsonMap.get("PUT") >= 10.0);

    }

    /**
     * Test rest server multi inputs.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRestServerMultiInputs() throws MessagingException, ApexException, IOException {
        final String[] args =
            { "-rfr", "target", "-c", "target/examples/config/SampleDomain/RESTServerJsonEventMultiIn.json" };
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        Response firstResponse = null;
        Response secondResponse = null;

        Map<String, Object> firstJsonMap = null;
        Map<String, Object> secondJsonMap = null;

        for (int i = 0; i < 20; i++) {
            firstResponse = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                            .request("application/json").post(Entity.json(getEvent()));

            if (Response.Status.OK.getStatusCode() != firstResponse.getStatus()) {
                break;
            }
            
            final String firstResponseString = firstResponse.readEntity(String.class);

            firstJsonMap = new Gson().fromJson(firstResponseString, Map.class);

            secondResponse = client.target("http://localhost:23324/apex/SecondConsumer/EventIn")
                            .request("application/json").post(Entity.json(getEvent()));

            if (Response.Status.OK.getStatusCode() != secondResponse.getStatus()) {
                break;
            }

            final String secondResponseString = secondResponse.readEntity(String.class);

            secondJsonMap = new Gson().fromJson(secondResponseString, Map.class);
        }

        apexMain.shutdown();

        assertEquals(Response.Status.OK.getStatusCode(), firstResponse.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", firstJsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", firstJsonMap.get("TestSlogan"));
        
        assertEquals(Response.Status.OK.getStatusCode(), secondResponse.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", secondJsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", secondJsonMap.get("TestSlogan"));
    }

    /**
     * Test rest server producer standalone.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerProducerStandalone() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventProducerStandalone.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                        "the parameters \"host\", \"port\", and \"standalone\" are illegal on REST Server producer"));
    }

    /**
     * Test rest server producer host.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerProducerHost() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventProducerHost.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(" host is specified only in standalone mode"));
    }

    /**
     * Test rest server producer port.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerProducerPort() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventProducerPort.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(" port is specified only in standalone mode"));
    }

    /**
     * Test rest server consumer standalone no host.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerConsumerStandaloneNoHost() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventConsumerStandaloneNoHost.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("the parameters \"host\" and \"port\" must be defined for REST Server consumer "
                        + "(FirstConsumer) in standalone mode"));
    }

    /**
     * Test rest server consumer standalone no port.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerConsumerStandaloneNoPort() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventConsumerStandaloneNoPort.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("the parameters \"host\" and \"port\" must be defined for REST Server consumer "
                        + "(FirstConsumer) in standalone mode"));
    }

    /**
     * Test rest server producer not sync.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerProducerNotSync() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventProducerNotSync.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("REST Server producer (FirstProducer) must run in synchronous mode "
                        + "with a REST Server consumer"));
    }

    /**
     * Test rest server consumer not sync.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void testRestServerConsumerNotSync() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args =
            { "src/test/resources/prodcons/RESTServerJsonEventConsumerNotSync.json" };

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString
                        .contains("peer \"FirstConsumer for peered mode SYNCHRONOUS does not exist or is not defined "
                                        + "with the same peered mode"));
    }

    /**
     * Gets the event.
     *
     * @return the event
     */
    private String getEvent() {
        final Random rand = new Random();
        final int nextMatchCase = rand.nextInt(4);
        final String nextEventName = "Event0" + rand.nextInt(2) + "00";

        final String eventString = "{\n" + "\"nameSpace\": \"org.onap.policy.apex.sample.events\",\n" + "\"name\": \""
                        + nextEventName + "\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_" + eventsSent++
                        + "\",\n" + "\"target\": \"apex\",\n" + "\"TestSlogan\": \"Test slogan for External Event0\",\n"
                        + "\"TestMatchCase\": " + nextMatchCase + ",\n" + "\"TestTimestamp\": "
                        + System.currentTimeMillis() + ",\n" + "\"TestTemperature\": 9080.866\n" + "}";

        return eventString;
    }
}
