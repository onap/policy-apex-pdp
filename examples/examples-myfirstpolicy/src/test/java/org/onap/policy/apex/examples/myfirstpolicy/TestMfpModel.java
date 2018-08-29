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

package org.onap.policy.apex.examples.myfirstpolicy;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Test MyFirstPolicy Model.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
public class TestMfpModel {

    private static Connection connection;
    private static TestApexModel<AxPolicyModel> testApexModel1;
    private static TestApexModel<AxPolicyModel> testApexModel2;

    /**
     * Setup.
     *
     * @throws Exception if there is an error
     */
    @BeforeClass
    public static void setup() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");
        testApexModel1 = new TestApexModel<>(AxPolicyModel.class, new TestMfpModelCreator.TestMfp1ModelCreator());
        testApexModel2 = new TestApexModel<>(AxPolicyModel.class, new TestMfpModelCreator.TestMfp2ModelCreator());
    }

    /**
     * Teardown.
     *
     * @throws Exception if there is an error
     */
    @AfterClass
    public static void teardown() throws Exception {
        connection.close();
        new File("derby.log").delete();
    }

    /**
     * Test model is valid.
     *
     * @throws Exception if there is an error
     */
    @Test
    public void testModelValid() throws Exception {
        AxValidationResult result = testApexModel1.testApexModelValid();
        assertTrue("Model did not validate cleanly", result.isOk());

        result = testApexModel2.testApexModelValid();
        assertTrue("Model did not validate cleanly", result.isOk());
    }

    /**
     * Test model write and read XML.
     *
     * @throws Exception if there is an error
     */
    @Test
    public void testModelWriteReadXml() throws Exception {
        testApexModel1.testApexModelWriteReadXml();
        testApexModel2.testApexModelWriteReadXml();
    }

    /**
     * Test model write and read JSON.
     *
     * @throws Exception if there is an error
     */
    @Test
    public void testModelWriteReadJson() throws Exception {
        testApexModel1.testApexModelWriteReadJson();
        testApexModel2.testApexModelWriteReadJson();
    }
}
