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

package org.onap.policy.apex.testsuites.integration.executor.handling;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * The Class TestApexSamplePolicyModel.
 */
public class TestApexSamplePolicyModel {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";
    private Connection connection;
    private TestApexModel<AxPolicyModel> testApexModel;

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");

        testApexModel =
                new TestApexModel<AxPolicyModel>(AxPolicyModel.class, new TestApexSamplePolicyModelCreator("MVEL"));
    }

    /**
     * Teardown.
     *
     * @throws Exception the exception
     */
    @After
    public void teardown() throws Exception {
        connection.close();
    }

    /**
     * Test model valid.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertTrue(result.toString().equals(VALID_MODEL_STRING));
    }

    /**
     * Test model write read xml.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModelWriteReadXml() throws Exception {
        testApexModel.testApexModelWriteReadXml();
    }

    /**
     * Test model write read json.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }

    /**
     * Test model write read jpa.
     *
     * @throws Exception the exception
     */
    @Test
    public void testModelWriteReadJpa() throws Exception {
        final DaoParameters DaoParameters = new DaoParameters();
        DaoParameters.setPluginClass(DefaultApexDao.class.getCanonicalName());
        DaoParameters.setPersistenceUnit("SampleModelTest");

        testApexModel.testApexModelWriteReadJpa(DaoParameters);
    }

    
}
