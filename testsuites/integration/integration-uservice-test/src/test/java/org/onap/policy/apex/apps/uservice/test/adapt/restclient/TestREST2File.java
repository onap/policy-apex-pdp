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

package org.onap.policy.apex.apps.uservice.test.adapt.restclient;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;


public class TestREST2File {

    private static final String BASE_URI = "http://localhost:32801/TestRest2File";
    private HttpServer server;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    @Before
    public void setUp() throws Exception {
        final ResourceConfig rc = new ResourceConfig(TestRESTClientEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        while (!server.isStarted()) {
            ThreadUtilities.sleep(50);
        }
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @AfterClass
    public static void deleteTempFiles() {
        new File("src/test/resources/events/EventsOut.json").delete();
    }

    @Test
    public void testRESTEventsIn() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEvent.json"};

        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outputEventText = TextFileUtils.getTextFileAsString("src/test/resources/events/EventsOut.json");
        assertTrue(outputEventText.contains(
                "04\",\n" + "  \"version\": \"0.0.1\",\n" + "  \"nameSpace\": \"org.onap.policy.apex.sample.events\""));
    }

    @Test
    public void testFileEmptyEvents() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEmptyEvents.json"};
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "received an empty event from URL \"http://localhost:32801/TestRest2File/apex/event/GetEmptyEvent\""));
    }

    @Test
    public void testFileEventsNoURL() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventNoURL.json"};
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(" no URL has been set for event sending on REST client"));
    }

    @Test
    public void testFileEventsBadURL() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventBadURL.json"};
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "reception of event from URL \"http://localhost:32801/TestRest2File/apex/event/Bad\" failed with status code 404"));
    }

    @Test
    public void testFileEventsBadHTTPMethod() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventBadHTTPMethod.json"};
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "specified HTTP method of \"POST\" is invalid, only HTTP method \"GET\" is supported for event reception on REST client consumer"));
    }

    @Test
    public void testFileEventsBadResponse() throws MessagingException, ApexException, IOException {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = {"src/test/resources/prodcons/REST2FileJsonEventBadResponse.json"};
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        assertTrue(outString.contains(
                "reception of event from URL \"http://localhost:32801/TestRest2File/apex/event/GetEventBadResponse\" failed with status code 400 and message \"\""));
    }
}
