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

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.dao.DaoParameters;
import org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * The Class TestApexSamplePolicyDbWrite.
 */
public class TestApexSamplePolicyDbWrite {
    private Connection connection;
    TestApexModel<AxPolicyModel> testApexModel;

    /**
     * Setup.
     *
     * @throws Exception the exception
     */
    @Before
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");

        final TestApexSamplePolicyModelCreator apexPolicyModelCreator = new TestApexSamplePolicyModelCreator("MVEL");
        testApexModel = new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
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
