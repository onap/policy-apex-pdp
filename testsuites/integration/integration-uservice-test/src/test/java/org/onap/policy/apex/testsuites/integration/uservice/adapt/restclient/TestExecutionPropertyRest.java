/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020-2021 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.endpoints.http.server.HttpServletServerFactoryInstance;
import org.onap.policy.common.gson.GsonMessageBodyHandler;
import org.onap.policy.common.utils.network.NetworkUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class runs integration tests for execution property in restClient.
 */
public class TestExecutionPropertyRest {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestExecutionPropertyRest.class);

    private static HttpServletServer server;
    private static final int PORT = 32801;

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
    @BeforeClass
    public static void setUp() throws Exception {
        if (NetworkUtil.isTcpPortOpen("localHost", PORT, 3, 50L)) {
            throw new IllegalStateException("port " + PORT + " is still in use");
        }

        server = HttpServletServerFactoryInstance.getServerFactory().build("TestExecutionPropertyRest", false, null,
            PORT, "/TestExecutionRest", false, false);

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
    @AfterClass
    public static void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Before test.
     */
    @Before
    public void beforeTest() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    /**
     * After test.
     * @throws ApexException the exception.
     */
    @After
    public void afterTest() throws ApexException {
        if (null != apexMain) {
            apexMain.shutdown();
        }
        System.setOut(stdout);
        System.setErr(stderr);
    }

    /**
     * Test Bad Url tag in Parameter .
     */
    @Test
    public void testBadUrl() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesRestTestPolicyModel.apex",
            "-o",
            "target/ExecutionPropertiesRestTestPolicyModel.json",
            "-ac",
            "src/test/resources/testdata/executionproperties/RESTEventBadUrl.json",
            "-t",
            "src/test/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        // @formatter:off
        final String[] args = {
            "-p",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        apexMain = new ApexMain(args);

        final String outString = outContent.toString();

        LOGGER.info("testReplaceUrlTag-OUTSTRING=\n" + outString + "\nEnd-TagUrl");
        assertThat(outString).contains("item \"url\" "
                        + "value \"http://localhost:32801/TestExecutionRest/apex/event/tagId}\" INVALID, "
                        + "invalid URL has been set for event sending on RESTCLIENT");
    }

    /**
     * Test Not find value for tags in Url .
     */
    @Test
    public void testNoValueSetForTagUrl() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesRestTestPolicyModel.apex",
            "-o",
            "target/ExecutionPropertiesRestTestPolicyModel.json",
            "-ac",
            "src/test/resources/testdata/executionproperties/RESTEventNoValueSetForTag.json",
            "-t",
            "src/test/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        // @formatter:off
        final String[] args = {
            "-p",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        apexMain = new ApexMain(args);

        await().atMost(5, TimeUnit.SECONDS)
            .until(() -> outContent.toString()
                .contains("key \"Number\" specified on url \"http://localhost:32801/TestExecutionRest/apex"
                    + "/event/{tagId}/{Number}\" not found in execution properties passed by the current policy"));
        assertTrue(apexMain.isAlive());
        LOGGER.info("testNoValueSetForTagUrl-OUTSTRING=\n" + outContent.toString() + "\nEnd-TagUrl");
    }

    /**
     * Test Bad Http code Filter.
     */
    @Test
    public void testBadCodeFilter() throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesRestTestPolicyModel.apex",
            "-o",
            "target/ExecutionPropertiesRestTestPolicyModel.json",
            "-ac",
            "src/test/resources/testdata/executionproperties/RESTEventBadHttpCodeFilter.json",
            "-t",
            "src/test/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        // @formatter:off
        final String[] args = {
            "-p",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        apexMain = new ApexMain(args);

        await().atMost(5, TimeUnit.SECONDS).until(() -> outContent.toString()
            .contains("failed with status code 500 and message \"{\"testToRun\": FetchHttpCode}"));
        assertTrue(apexMain.isAlive());
        LOGGER.info("testBadCodeFilter-OUTSTRING=\n" + outContent.toString() + "\nEnd-TagUrl");
    }

    /**
     * Test Http code filter set and multi-tag Url are transformed correctly.
     */
    @Test
    public void testReplaceUrlMultiTag() throws Exception {
        final Client client = ClientBuilder.newClient();
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesRestTestPolicyModel.apex",
            "-o",
            "target/ExecutionPropertiesRestTestPolicyModel.json",
            "-ac",
            "src/test/resources/testdata/executionproperties/RESTHttpCodeFilterSetToMultiTagUrlOK.json",
            "-t",
            "src/test/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        // @formatter:off
        final String[] args = {
            "-p",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on
        apexMain = new ApexMain(args);

        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            Response response = client.target("http://localhost:32801/TestExecutionRest/apex/event/GetProperUrl")
                .request("application/json").get();
            return response.readEntity(String.class).contains("\"PostProperUrl\": 3");
        });
        assertTrue(apexMain.isAlive());
        LOGGER.info("testReplaceUrlMultiTag-OUTSTRING=\n" + outContent.toString() + "\nEnd-MultiTagUrl");
    }

}
