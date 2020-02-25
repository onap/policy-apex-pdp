/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.uservice.taskparameters;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
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
 * This class runs integration tests for taskParameters.
 * Task parameters are read from the ApexConfig, and they can be accessed in task logic.
 * In this case, the taskParameters are used to set values in executionProperties.
 * URL dynamically populated using executionProperties is hit and values get updated in
 * {@link RestClientEndpointForTaskParameters} which acts as a temporary server for requests.
 */
public class TestTaskParameters {

    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TestTaskParameters.class);

    private static HttpServletServer server;
    private static final int PORT = 32801;
    private static final String HOST = "localhost";

    /**
     * Compile the policy.
     */
    @BeforeClass
    public static void compilePolicy() {
        // @formatter:off
        final String[] cliArgs = {
            "-c",
            "src/test/resources/policies/taskparameters/TaskParametersTestPolicyModel.apex",
            "-l",
            "target/TaskParametersTestPolicyModel.log",
            "-o",
            "target/TaskParametersTestPolicyModel.json"
        };
        // @formatter:on

        new ApexCommandLineEditorMain(cliArgs);
    }

    /**
     * Sets up a server for testing.
     *
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        if (NetworkUtil.isTcpPortOpen(HOST, PORT, 3, 50L)) {
            throw new IllegalStateException("port " + PORT + " is still in use");
        }

        server = HttpServletServerFactoryInstance.getServerFactory().build("TestTaskParameters", false, null, PORT,
            "/TestTaskParametersRest", false, false);

        server.addServletClass(null, RestClientEndpointForTaskParameters.class.getName());
        server.setSerializationProvider(GsonMessageBodyHandler.class.getName());

        server.start();

        if (!NetworkUtil.isTcpPortOpen(HOST, PORT, 60, 500L)) {
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
     * Clear relative file root environment variable.
     */
    @Before
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    /**
     * Test taskParameters with no taskIds.
     * When taskIds are provided, all taskParameters provided in config will be updated to all tasks.
     */
    @Test
    public void testTaskParameters_with_noTaskIds() throws Exception {
        String responseEntity = testTaskParameters(
            "src/test/resources/testdata/taskparameters/TaskParameterTestConfig_with_noTaskIds.json");
        assertTrue(responseEntity.contains("{\"closedLoopId\": closedLoopId123,\"serviceId\": serviceId123}"));
    }

    /**
     * Test taskParameters with valid taskIds.
     * When taskIds are provided, the the taskParameter will be updated in that particular task alone.
     */
    @Test
    public void testTaskParameters_with_validTaskIds() throws Exception {
        String responseEntity = testTaskParameters(
            "src/test/resources/testdata/taskparameters/TaskParameterTestConfig_with_validTaskIds.json");
        assertTrue(responseEntity.contains("{\"closedLoopId\": closedLoopIdxyz,\"serviceId\": serviceIdxyz}"));
    }

    /**
     * Test taskParameters with invalid taskIds.
     * When invalid taskIds are provided, or when a taskParameter assigned to a particular taskId is tried to be
     * accessed in a taskLogic of a different task, such taskParameters won't be accessible in the task
     */
    @Test
    public void testTaskParameters_with_invalidTaskIds() throws Exception {
        String responseEntity = testTaskParameters(
            "src/test/resources/testdata/taskparameters/TaskParameterTestConfig_with_invalidTaskIds.json");
        assertTrue(responseEntity.contains("{\"closedLoopId\": INVALID - closedLoopId not available in TaskParameters,"
            + "\"serviceId\": INVALID - serviceId not available in TaskParameters}"));
    }

    private String testTaskParameters(String apexConfigPath) throws ApexException {
        final Client client = ClientBuilder.newClient();
        final String[] args = {apexConfigPath};
        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(1000);
        apexMain.shutdown();
        Response response = null;
        response = client.target("http://localhost:32801/TestTaskParametersRest/apex/event/getDetails")
            .request("application/json").get();
        final String responseEntity = response.readEntity(String.class);
        LOGGER.info("testTaskParameters-OUTSTRING=\n" + responseEntity);
        return responseEntity;
    }
}
