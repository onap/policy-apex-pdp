/*
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

package org.onap.apex.model.basicmodel.handling;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.apex.model.basicmodel.concepts.ApexException;
import org.onap.apex.model.basicmodel.concepts.AxModel;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.apex.model.basicmodel.dao.DAOParameters;
import org.onap.apex.model.basicmodel.test.TestApexModel;

public class TestApexBasicModel {
    private Connection connection;
    TestApexModel<AxModel> testApexModel;

    @Before
    public void setup() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");

        testApexModel = new TestApexModel<AxModel>(AxModel.class, new TestApexBasicModelCreator());
    }

    @After
    public void teardown() throws Exception {
        connection.close();
        new File("derby.log").delete();
    }

    @Test
    public void testModelValid() throws Exception {
        AxValidationResult result = testApexModel.testApexModelValid();
        assertTrue(result.toString().equals(VALID_MODEL_STRING));
    }

    @Test
    public void testApexModelVaidateObservation() throws Exception {
        try {
            testApexModel.testApexModelVaidateObservation();
        }
        catch (ApexException e) {
            assertEquals("model should have observations", e.getMessage());
        }
    }

    @Test
    public void testApexModelVaidateWarning() throws Exception {
        AxValidationResult result = testApexModel.testApexModelVaidateWarning();
        assertTrue(result.toString().equals(WARNING_MODEL_STRING));
    }

    @Test
    public void testModelVaidateInvalidModel() throws Exception {
        AxValidationResult result = testApexModel.testApexModelVaidateInvalidModel();
        assertTrue(result.toString().equals(INVALID_MODEL_STRING));
    }

    @Test
    public void testModelVaidateMalstructured() throws Exception {
        AxValidationResult result = testApexModel.testApexModelVaidateMalstructured();
        assertTrue(result.toString().equals(INVALID_MODEL_MALSTRUCTURED_STRING));
    }

    @Test
    public void testModelWriteReadXML() throws Exception {
        testApexModel.testApexModelWriteReadXML();
    }

    @Test
    public void testModelWriteReadJSON() throws Exception {
        testApexModel.testApexModelWriteReadJSON();
    }

    @Test
    public void testModelWriteReadJPA() throws Exception {
        DAOParameters daoParameters = new DAOParameters();
        daoParameters.setPluginClass("org.onap.apex.model.basicmodel.dao.impl.DefaultApexDao");
        daoParameters.setPersistenceUnit("DAOTest");

        testApexModel.testApexModelWriteReadJPA(daoParameters);
    }

    // As there are no real concepts in a basic model, this is as near to a valid model as we can get
    private static final String VALID_MODEL_STRING = "\n" +
            "***warnings issued during validation of model***\n" +
            "AxArtifactKey:(name=FloatKIKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:WARNING:key not found for key information entry\n" +
            "AxArtifactKey:(name=IntegerKIKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:WARNING:key not found for key information entry\n" +
            "********************************";

    private static final String WARNING_MODEL_STRING = "\n" +
            "***warnings issued during validation of model***\n" +
            "AxArtifactKey:(name=FloatKIKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:WARNING:key not found for key information entry\n" +
            "AxArtifactKey:(name=IntegerKIKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:WARNING:key not found for key information entry\n" +
            "AxArtifactKey:(name=Unref0,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:WARNING:key not found for key information entry\n" +
            "AxArtifactKey:(name=Unref1,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:WARNING:key not found for key information entry\n" +
            "********************************";

    private static final String INVALID_MODEL_STRING = "\n" +
            "***validation of model failed***\n" +
            "AxArtifactKey:(name=BasicModelKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxKeyInfo:WARNING:UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n" +
            "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxKeyInfo:OBSERVATION:description is blank\n" +
            "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxKeyInfo:WARNING:UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n" +
            "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxKeyInformation:INVALID:duplicate UUID found on keyInfoMap entry AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):00000000-0000-0000-0000-000000000000\n" +
            "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" +
            "***validation of model failed***\n" +
            "AxArtifactKey:(name=BasicModelKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxKeyInfo:WARNING:UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n" +
            "AxArtifactKey:(name=BasicModelKey,version=0.0.1):org.onap.apex.model.basicmodel.concepts.AxModel:INVALID:key information not found for key AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1)\n" +
            "********************************";
}
