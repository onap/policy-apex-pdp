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

package org.onap.policy.apex.apps.uservice.test.adapt.restserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;

import com.google.gson.Gson;


public class TestRESTServer {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    private static int eventsSent = 0;

    @Test
    public void testRESTServerPut() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEvent.json"};
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 20; i++) {
            ThreadUtilities.sleep(100);

            final Response response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                    .request("application/json").put(Entity.json(getEvent()));

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
            assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
        }

        apexMain.shutdown();
    }
    
    @Test
    public void testRESTServerPost() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEvent.json"};
        final ApexMain apexMain = new ApexMain(args);
        
        final Client client = ClientBuilder.newClient();
        
        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 20; i++) {
            ThreadUtilities.sleep(100);
            
            final Response response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                    .request("application/json").post(Entity.json(getEvent()));
            
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String responseString = response.readEntity(String.class);
            
            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
            assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
        }
        
        apexMain.shutdown();
    }    

    @Test    
    public void testRESTServerGetStatus() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEvent.json"};
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        // trigger 10 POST & PUT events
        for (int i = 0; i < 10; i++) {
            ThreadUtilities.sleep(100);
            client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                    .post(Entity.json(getEvent()));
            client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                    .put(Entity.json(getEvent()));
        }
        
        //one more PUT
        ThreadUtilities.sleep(100);
        client.target("http://localhost:23324/apex/FirstConsumer/EventIn").request("application/json")
                .put(Entity.json(getEvent()));
        
        final Response response = client.target("http://localhost:23324/apex/FirstConsumer/Status")
                .request("application/json").get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        final String responseString = response.readEntity(String.class);
        System.out.println(responseString);
        @SuppressWarnings("unchecked")
        final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
        assertEquals("[FirstConsumer]", jsonMap.get("INPUTS"));            
        assertEquals(1.0, jsonMap.get("STAT"));
        assertEquals(10.0, jsonMap.get("POST"));
        assertEquals(11.0, jsonMap.get("PUT"));
        
        apexMain.shutdown();
    }

    @Test
    public void testRESTServerMultiInputs() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventMultiIn.json"};
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 20; i++) {
            ThreadUtilities.sleep(100);

            final Response firstResponse = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                    .request("application/json").post(Entity.json(getEvent()));

            assertEquals(Response.Status.OK.getStatusCode(), firstResponse.getStatus());
            final String firstResponseString = firstResponse.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> firstJsonMap = new Gson().fromJson(firstResponseString, Map.class);
            assertEquals("org.onap.policy.apex.sample.events", firstJsonMap.get("nameSpace"));
            assertEquals("Test slogan for External Event0", firstJsonMap.get("TestSlogan"));

            final Response secondResponse = client.target("http://localhost:23324/apex/SecondConsumer/EventIn")
                    .request("application/json").post(Entity.json(getEvent()));

            assertEquals(Response.Status.OK.getStatusCode(), secondResponse.getStatus());
            final String secondResponseString = secondResponse.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> secondJsonMap = new Gson().fromJson(secondResponseString, Map.class);
            assertEquals("org.onap.policy.apex.sample.events", secondJsonMap.get("nameSpace"));
            assertEquals("Test slogan for External Event0", secondJsonMap.get("TestSlogan"));
        }

        apexMain.shutdown();
    }

    @Test
    public void testRESTServerProducerStandalone() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerStandalone.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString
                .contains("the parameters \"host\", \"port\", and \"standalone\" are illegal on REST Server producer"));
    }

    @Test
    public void testRESTServerProducerHost() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerHost.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("  host and port are specified only in standalone mode"));
    }

    @Test
    public void testRESTServerProducerPort() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerPort.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains("  host and port are specified only in standalone mode"));
    }

    @Test
    public void testRESTServerConsumerStandaloneNoHost() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventConsumerStandaloneNoHost.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "the parameters \"host\" and \"port\" must be defined for REST Server consumer (FirstConsumer) in standalone mode"));
    }

    @Test
    public void testRESTServerConsumerStandaloneNoPort() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventConsumerStandaloneNoPort.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "the parameters \"host\" and \"port\" must be defined for REST Server consumer (FirstConsumer) in standalone mode"));
    }

    @Test
    public void testRESTServerProducerNotSync() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventProducerNotSync.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "REST Server producer (FirstProducer) must run in synchronous mode with a REST Server consumer"));
    }

    @Test
    public void testRESTServerConsumerNotSync() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventConsumerNotSync.json"};

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(200);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "event output for peered mode \"SYNCHRONOUS\": peer \"FirstConsumer\" for event handler \"FirstProducer\" does not exist or is not defined as being synchronous"));
    }

    @Test
    public void testRESTServerDivideByZero() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/RESTServerJsonEventDivideByZero.json"};
        final ApexMain apexMain = new ApexMain(args);

        final Client client = ClientBuilder.newClient();

        // Wait for the required amount of events to be received or for 10 seconds
        for (int i = 0; i < 20; i++) {
            ThreadUtilities.sleep(100);

            final Response response = client.target("http://localhost:23324/apex/FirstConsumer/EventIn")
                    .request("application/json").put(Entity.json(getEvent()));

            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            final String responseString = response.readEntity(String.class);

            @SuppressWarnings("unchecked")
            final Map<String, Object> jsonMap = new Gson().fromJson(responseString, Map.class);
            assertEquals("org.onap.policy.apex.sample.events", jsonMap.get("nameSpace"));
            assertEquals("Test slogan for External Event0", jsonMap.get("TestSlogan"));
            assertTrue(((String) jsonMap.get("exceptionMessage")).contains("caused by: / by zero"));

        }


        apexMain.shutdown();
    }

    private String getEvent() {
        final Random rand = new Random();
        final int nextMatchCase = rand.nextInt(4);
        final String nextEventName = "Event0" + rand.nextInt(2) + "00";

        final String eventString = "{\n" + "\"nameSpace\": \"org.onap.policy.apex.sample.events\",\n" + "\"name\": \""
                + nextEventName + "\",\n" + "\"version\": \"0.0.1\",\n" + "\"source\": \"REST_" + eventsSent++ + "\",\n"
                + "\"target\": \"apex\",\n" + "\"TestSlogan\": \"Test slogan for External Event0\",\n"
                + "\"TestMatchCase\": " + nextMatchCase + ",\n" + "\"TestTimestamp\": " + System.currentTimeMillis()
                + ",\n" + "\"TestTemperature\": 9080.866\n" + "}";

        return eventString;
    }
}
