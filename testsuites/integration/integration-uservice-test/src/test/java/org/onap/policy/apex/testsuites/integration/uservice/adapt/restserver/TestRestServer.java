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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.restserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.network.NetworkUtil;
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
     * Before Test.
     */
    @Before
    public void beforeTest() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * After test.
     */
    @After
    public void afterTest() {
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     * Test rest server put.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRestServerPut() throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerPut start");

        final String[] args = {"-rfr", "target", "-p", "target/examples/config/SampleDomain/RESTServerJsonEvent.json"};
        final ApexMain apexMain = new ApexMain(args);
        if (!NetworkUtil.isTcpPortOpen("localhost", 23324, 60, 500L)) {
            throw new IllegalStateException("cannot connect to Apex Rest Server");
        }
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

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
        LOGGER.debug("testRestServerPut end");
    }

    /**
     * Test rest server post.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRestServerPost() throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerPost start");
        final String[] args = {"-rfr", "target", "-p", "target/examples/config/SampleDomain/RESTServerJsonEvent.json"};
        final ApexMain apexMain = new ApexMain(args);
        if (!NetworkUtil.isTcpPortOpen("localhost", 23324, 60, 500L)) {
            throw new IllegalStateException("cannot connect to Apex Rest Server");
        }
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

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
        LOGGER.debug("testRestServerPost end");
    }

    /**
     * Test rest server get status.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @Test
    public void testRestServerGetStatus() throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerGetStatus start");
        final String[] args = {"-rfr", "target", "-p", "target/examples/config/SampleDomain/RESTServerJsonEvent.json"};
        final ApexMain apexMain = new ApexMain(args);
        if (!NetworkUtil.isTcpPortOpen("localhost", 23324, 60, 500L)) {
            throw new IllegalStateException("cannot connect to Apex Rest Server");
        }
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

        final Response statResponse =
            client.target("http://localhost:23324/apex/FirstConsumer/Status").request("application/json").get();

        final String responseString = statResponse.readEntity(String.class);

        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        assertEquals(Response.Status.OK.getStatusCode(), postResponse.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), putResponse.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), statResponse.getStatus());

        @SuppressWarnings("unchecked")
        final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
        assertEquals("[FirstConsumer", ((String) jsonMap.get("INPUTS")).substring(0, 14));
        assertEquals(1.0, jsonMap.get("STAT"));
        assertTrue((double) jsonMap.get("POST") >= 10.0);
        assertTrue((double) jsonMap.get("PUT") >= 10.0);
        LOGGER.debug("testRestServerGetStatus end");
    }

    /**
     * Test rest server multi inputs.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRestServerMultiInputs()
        throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerMultiInputs start");
        final String[] args =
            {"-rfr", "target", "-p", "target/examples/config/SampleDomain/RESTServerJsonEventMultiIn.json"};
        final ApexMain apexMain = new ApexMain(args);
        if (!NetworkUtil.isTcpPortOpen("localhost", 23324, 60, 500L)) {
            throw new IllegalStateException("cannot connect to Apex Rest Server");
        }
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

            secondResponse = client.target("http://localhost:23325/apex/SecondConsumer/EventIn")
                .request("application/json").post(Entity.json(getEvent()));

            if (Response.Status.OK.getStatusCode() != secondResponse.getStatus()) {
                break;
            }

            final String secondResponseString = secondResponse.readEntity(String.class);

            secondJsonMap = new Gson().fromJson(secondResponseString, Map.class);
        }

        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        assertEquals(Response.Status.OK.getStatusCode(), firstResponse.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", firstJsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", firstJsonMap.get("TestSlogan"));

        assertEquals(Response.Status.OK.getStatusCode(), secondResponse.getStatus());
        assertEquals("org.onap.policy.apex.sample.events", secondJsonMap.get("nameSpace"));
        assertEquals("Test slogan for External Event0", secondJsonMap.get("TestSlogan"));
        LOGGER.debug("testRestServerMultiInputs end");
    }

    /**
     * Test rest server producer standalone.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @Test
    public void testRestServerProducerStandalone()
        throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerProducerStandalone start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerStandalone.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();

        assertThat(outString)
            .contains("the parameters \"host\", \"port\", and \"standalone\" are illegal on REST Server producer");
        LOGGER.debug("testRestServerProducerStandalone end");
    }

    /**
     * Test rest server producer host.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @Test
    public void testRestServerProducerHost()
        throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerProducerHost start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerHost.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();
        assertThat(outString).contains(" host is specified only in standalone mode");
        LOGGER.debug("testRestServerProducerHost end");
    }

    /**
     * Test rest server producer port.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException interrupted exception
     */
    @Test
    public void testRestServerProducerPort()
        throws MessagingException, ApexException, IOException, InterruptedException {
        LOGGER.debug("testRestServerProducerPort start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerPort.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();
        assertThat(outString).contains(" port is specified only in standalone mode");
        LOGGER.debug("testRestServerProducerPort end");
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
        LOGGER.debug("testRestServerConsumerStandaloneNoHost start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventConsumerStandaloneNoHost.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();
        assertThat(outString).contains("the parameters \"host\" and \"port\" must be defined for REST Server consumer "
            + "(FirstConsumer) in standalone mode");
        LOGGER.debug("testRestServerConsumerStandaloneNoHost end");
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
        LOGGER.debug("testRestServerConsumerStandaloneNoPort start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventConsumerStandaloneNoPort.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();
        assertThat(outString).contains("the parameters \"host\" and \"port\" must be defined for REST Server consumer "
            + "(FirstConsumer) in standalone mode");
        LOGGER.debug("testRestServerConsumerStandaloneNoPort end");
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
        LOGGER.debug("testRestServerProducerNotSync start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerNotSync.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();

        assertThat(outString).contains(
            "REST Server producer (FirstProducer) must run in synchronous mode " + "with a REST Server consumer");
        LOGGER.debug("testRestServerProducerNotSync end");
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
        LOGGER.debug("testRestServerConsumerNotSync start");
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventConsumerNotSync.json"};

        final ApexMain apexMain = new ApexMain(args);
        apexMain.shutdown();

        await().atMost(10L, TimeUnit.SECONDS).until(() -> !apexMain.isAlive());

        final String outString = outContent.toString();

        assertThat(outString)
            .contains("peer \"FirstConsumer for peered mode SYNCHRONOUS does not exist or is not defined "
                + "with the same peered mode");
        LOGGER.debug("testRestServerConsumerNotSync end");
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
            + nextEventName + "\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_" + eventsSent++ + "\",\n"
            + "\"target\": \"apex\",\n" + "\"TestSlogan\": \"Test slogan for External Event0\",\n"
            + "\"TestMatchCase\": " + nextMatchCase + ",\n" + "\"TestTimestamp\": " + System.currentTimeMillis() + ",\n"
            + "\"TestTemperature\": 9080.866\n" + "}";

        return eventString;
    }
}
