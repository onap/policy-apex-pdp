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

package org.onap.policy.apex.model.policymodel.handling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.dao.DAOParameters;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

public class TestApexPolicyModel {
    private Connection connection;
    TestApexModel<AxPolicyModel> testApexModel;

    @Before
    public void setup() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        connection = DriverManager.getConnection("jdbc:derby:memory:apex_test;create=true");

        testApexModel = new TestApexModel<AxPolicyModel>(AxPolicyModel.class, new TestApexPolicyModelCreator());
    }

    @After
    public void teardown() throws Exception {
        connection.close();
        new File("derby.log").delete();
    }

    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertTrue(result.toString().equals(VALID_MODEL_STRING));
    }

    @Test
    public void testApexModelVaidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateObservation();
        assertTrue(result.toString().equals(OBSERVATION_MODEL_STRING));
    }


    @Test
    public void testApexModelVaidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateWarning();
        assertTrue(result.toString().equals(WARNING_MODEL_STRING));
    }

    @Test
    public void testModelVaidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateInvalidModel();
        assertEquals(INVALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateMalstructured();
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
        final DAOParameters daoParameters = new DAOParameters();
        daoParameters.setPluginClass("org.onap.policy.apex.model.basicmodel.dao.impl.DefaultApexDao");
        daoParameters.setPersistenceUnit("DAOTest");

        testApexModel.testApexModelWriteReadJPA(daoParameters);
    }

    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String OBSERVATION_MODEL_STRING =
            "\n" + "***observations noted during validation of model***\n"
                    + "AxReferenceKey:(parentKeyName=policy,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=state):org.onap.policy.apex.model.policymodel.concepts.AxState:OBSERVATION:state output stateOutput0 is not used directly by any task\n"
                    + "********************************";

    private static final String WARNING_MODEL_STRING = "\n" + "***warnings issued during validation of model***\n"
            + "AxArtifactKey:(name=policy,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicy:WARNING:state AxReferenceKey:(parentKeyName=policy,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=anotherState) is not referenced in the policy execution tree\n"
            + "********************************";

    private static final String INVALID_MODEL_STRING = "\n" + "***validation of model failed***\n"
            + "AxArtifactKey:(name=contextAlbum0,version=0.0.1):org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum:INVALID:scope is not defined\n"
            + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum:INVALID:scope is not defined\n"
            + "AxReferenceKey:(parentKeyName=policy,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=state):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:task output field AxOutputField:(key=AxReferenceKey:(parentKeyName=task,parentKeyVersion=0.0.1,parentLocalName=outputFields,localName=OE1PAR0),fieldSchemaKey=AxArtifactKey:(name=eventContextItem0,version=0.0.1),optional=false) for task task:0.0.1 not in output event outEvent0:0.0.1\n"
            + "AxReferenceKey:(parentKeyName=policy,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=state):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:task output field AxOutputField:(key=AxReferenceKey:(parentKeyName=task,parentKeyVersion=0.0.1,parentLocalName=outputFields,localName=OE1PAR1),fieldSchemaKey=AxArtifactKey:(name=eventContextItem1,version=0.0.1),optional=false) for task task:0.0.1 not in output event outEvent0:0.0.1\n"
            + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" + "***validation of model failed***\n"
            + "AxArtifactKey:(name=policyModel_KeyInfo,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation:INVALID:keyInfoMap may not be empty\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel_KeyInfo,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel_Schemas,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel_Events,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel_Albums,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel_Tasks,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:key information not found for key AxArtifactKey:(name=policyModel_Policies,version=0.0.1)\n"
            + "AxArtifactKey:(name=policyModel_Schemas,version=0.0.1):org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas:INVALID:contextSchemas may not be empty\n"
            + "AxArtifactKey:(name=policyModel_Events,version=0.0.1):org.onap.policy.apex.model.eventmodel.concepts.AxEvents:INVALID:eventMap may not be empty\n"
            + "AxArtifactKey:(name=policyModel_Albums,version=0.0.1):org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums:OBSERVATION:albums are empty\n"
            + "AxArtifactKey:(name=policyModel_Tasks,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxTasks:INVALID:taskMap may not be empty\n"
            + "AxArtifactKey:(name=policyModel_Policies,version=0.0.1):org.onap.policy.apex.model.policymodel.concepts.AxPolicies:INVALID:policyMap may not be empty\n"
            + "********************************";
}
