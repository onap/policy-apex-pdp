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

package org.onap.policy.apex.testsuites.integration.uservice.executionproperties;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * This class runs integration tests for execution properties.
 */
public class TestExecutionProperties {
    /**
     * Compile the policy.
     */
    @BeforeClass
    public static void compilePolicy() {
        // @formatter:off
        final String[] cliArgs = {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesTestPolicyModel.apex",
            "-l",
            "target/ExecutionPropertiesTestPolicyModel.log",
            "-o",
            "target/ExecutionPropertiesTestPolicyModel.json"
        };
        // @formatter:on

        new ApexCommandLineEditorMain(cliArgs);
    }

    /**
     * Clear relative file root environment variable.
     */
    @Before
    public void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    /**
     * Test read only execution properties are returned from policy.
     */
    @Test
    public void testReadOnly() throws Exception {
        testExecutionProperties("readOnly");
    }

    /**
     * Test where execution properties set in task.
     */
    @Test
    public void testEmptyToDefined() throws Exception {
        testExecutionProperties("emptyToDefined");
    }

    /**
     * Test where execution properties cleared in task.
     */
    @Test
    public void testDefinedToEmpty() throws Exception {
        testExecutionProperties("definedToEmpty");
    }

    /**
     * Test where an execution properties added in task.
     */
    @Test
    public void testAddProperty() throws Exception {
        testExecutionProperties("addProperty");
    }

    /**
     * Test empty properties are transferred correctly.
     */
    @Test
    public void testEmptyToEmpty() throws Exception {
        testExecutionProperties("emptyToEmpty");
    }

    private void testExecutionProperties(final String testName) throws Exception {
        File compiledPolicyFile = new File("target/ExecutionPropertiesTestPolicyModel.json");
        assertTrue(compiledPolicyFile.exists());

        new File("target/" + testName + "_out.properties").delete();

        // @formatter:off
        final String[] args = {
            "-rfr",
            "target",
            "-c",
            "src/test/resources/testdata/executionproperties/" + testName + "_conf.json"
        };
        // @formatter:on
        final ApexMain apexMain = new ApexMain(args);

        // TODO: Set back to 10 seconds
        await().atMost(10000, TimeUnit.SECONDS)
                .until(() -> new File("target/" + testName + "_out.properties").exists());

        apexMain.shutdown();

        Properties expectedProperties = new Properties();
        expectedProperties.load(new FileInputStream(
                new File("src/test/resources/testdata/executionproperties/" + testName + "_out_expected.properties")));

        Properties actualProperties = new Properties();
        actualProperties.load(new FileInputStream(new File("target/" + testName + "_out.properties")));

        assertEquals(expectedProperties, actualProperties);
    }
}
