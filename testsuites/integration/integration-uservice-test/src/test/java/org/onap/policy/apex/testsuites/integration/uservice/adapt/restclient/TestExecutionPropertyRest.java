/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
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
import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class runs integration tests for execution property in restClient.
 */
public class TestExecutionPropertyRest {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestExecutionPropertyRest.class);

    private static final String BASE_URI = "http://localhost:32801/TestExecutionRest";
    private static HttpServer server;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    /**
     * Compile the policy.
     */
    @BeforeClass
    public static void compilePolicy() {
        // @formatter:off
        final String[] cliArgs = {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesRestTestPolicyModel.apex",
            "-l",
            "target/ExecutionPropertiesRestTestPolicyModel.log",
            "-o",
            "target/ExecutionPropertiesRestTestPolicyModel.json"
        };
        // @formatter:on

        new ApexCommandLineEditorMain(cliArgs);
    }

    /**
     * Sets the up.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        final ResourceConfig rc = new ResourceConfig(TestRestClientEndpoint.class);
        server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        if (NetworkUtil.isTcpPortOpen(BASE_URI, 32801, 1, 1L)) {
            throw new IllegalStateException("port " + 32801 + " is still in use");
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
     * Test Bad Url tag in Parameter .
     */
    @Test
    public void testBadUrl() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = { "src/test/resources/testdata/executionproperties/RESTEventBadUrl.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(500);

        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("testReplaceUrlTag-OUTSTRING=\n" + outString + "\nEnd-TagUrl");
        assertTrue(outString.contains("no proper URL has been set for event sending on REST client"));
    }

    /**
     * Test Not find value for tags in Url .
     */
    @Test
    public void testNoValueSetForTagUrl() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = { "src/test/resources/testdata/executionproperties/RESTEventNoValueSetForTag.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(1000);

        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("testReplaceUrlTag-OUTSTRING=\n" + outString + "\nEnd-TagUrl");
        assertTrue(outString.contains("key\"Number\"specified on url \"http://localhost:32801/TestExecutionRest/apex"
                + "/event/{tagId}/{Number}\"not found in execution properties passed by the current policy"));
    }

    /**
     * Test Bad Http code Filter.
     */
    @Test
    public void testBadCodeFilter() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        final String[] args = { "src/test/resources/testdata/executionproperties/RESTEventBadHttpCodeFilter.json" };
        final ApexMain apexMain = new ApexMain(args);

        ThreadUtilities.sleep(500);

        apexMain.shutdown();

        final String outString = outContent.toString();

        System.setOut(stdout);
        System.setErr(stderr);

        LOGGER.info("testReplaceUrlTag-OUTSTRING=\n" + outString + "\nEnd-TagUrl");
        assertTrue(outString.contains("failed with status code 500 and message \"{\"testToRun\": FetchHttpCode}"));
    }

    /**
     * Test Http code filter set and tag Url are transformed correctly.
     */
    @Test
    public void testReplaceUrlTag() throws Exception {
        final Client client = ClientBuilder.newClient();

        final String[] args =
            { "src/test/resources/testdata/executionproperties/RESTHttpCodeFilterSetToTagUrlOK.json" };
        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(1000);
        apexMain.shutdown();

        final String outString = outContent.toString();
        System.setOut(stdout);
        System.setErr(stderr);

        Response response = null;
        response = client.target("http://localhost:32801/TestExecutionRest/apex/event/GetProperUrl")
                .request("application/json").get();

        LOGGER.info("testReplaceUrlTag-OUTSTRING=\n" + outString + "\nEnd-TagUrl");
        final String responseEntity = response.readEntity(String.class);
        assertTrue(responseEntity.contains("\"PostProperUrl\": 1"));
    }

    /**
     * Test Http code filter set and multi-tag Url are transformed correctly.
     */
    @Test
    public void testReplaceUrlMultiTag() throws Exception {
        final Client client = ClientBuilder.newClient();
        final String[] args =
            { "src/test/resources/testdata/executionproperties/RESTHttpCodeFilterSetToMultiTagUrlOK.json" };
        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(1500);
        apexMain.shutdown();

        System.setOut(stdout);
        System.setErr(stderr);
        Response response = null;
        response = client.target("http://localhost:32801/TestExecutionRest/apex/event/GetProperUrl")
                .request("application/json").get();
        final String responseEntity = response.readEntity(String.class);
        LOGGER.info("testReplaceUrlMultiTag-OUTSTRING=\n" + responseEntity + "\nEnd-MultiTagUrl");
        assertTrue(responseEntity.contains("\"PostProperUrl\": 3"));
    }

}
