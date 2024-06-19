/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019-2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain;
import org.onap.policy.apex.service.engine.main.ApexMain;

/**
 * This class runs integration tests for execution properties.
 */
class TestExecutionProperties {

    /**
     * Clear relative file root environment variable.
     */
    @BeforeEach
    void clearRelativeFileRoot() {
        System.clearProperty("APEX_RELATIVE_FILE_ROOT");
    }

    /**
     * Test read only execution properties are returned from policy.
     */
    @Test
    void testReadOnly() throws Exception {
        testExecutionProperties("readOnly");
    }

    /**
     * Test where execution properties set in task.
     */
    @Test
    void testEmptyToDefined() throws Exception {
        testExecutionProperties("emptyToDefined");
    }

    /**
     * Test where execution properties cleared in task.
     */
    @Test
    void testDefinedToEmpty() throws Exception {
        testExecutionProperties("definedToEmpty");
    }

    /**
     * Test where an execution properties added in task.
     */
    @Test
    void testAddProperty() throws Exception {
        testExecutionProperties("addProperty");
    }

    /**
     * Test empty properties are transferred correctly.
     */
    @Test
    void testEmptyToEmpty() throws Exception {
        testExecutionProperties("emptyToEmpty");
    }

    private void testExecutionProperties(final String testName) throws Exception {
        // @formatter:off
        final String[] cliArgs = new String[] {
            "-c",
            "src/test/resources/policies/executionproperties/policy/ExecutionPropertiesTestPolicyModel.apex",
            "-l",
            "target/ExecutionPropertiesTestPolicyModel.log",
            "-ac",
            "src/test/resources/testdata/executionproperties/" + testName + "_conf.json",
            "-t",
            "src/test/resources/tosca/ToscaTemplate.json",
            "-ot",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on

        new ApexCliToscaEditorMain(cliArgs);

        File outFile = new File("target/" + testName + "_out.properties");
        outFile.deleteOnExit();

        // @formatter:off
        final String[] args = {
            "-rfr",
            "target/classes",
            "-p",
            "target/classes/APEXPolicy.json"
        };
        // @formatter:on
        final ApexMain apexMain = new ApexMain(args);

        await().atMost(1, TimeUnit.SECONDS).until(apexMain::isAlive);
        await().atMost(10, TimeUnit.SECONDS).until(outFile::exists);
        await().atMost(1, TimeUnit.SECONDS).until(() -> outFile.length() > 0);

        apexMain.shutdown();

        Properties expectedProperties = new Properties();
        expectedProperties.load(new FileInputStream(
            "src/test/resources/testdata/executionproperties/" + testName + "_out_expected.properties"));

        Properties actualProperties = new Properties();
        actualProperties.load(new FileInputStream(outFile));

        assertEquals(expectedProperties, actualProperties);
    }
}
