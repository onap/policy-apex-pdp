/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020,2022 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

public class ApexPolicyModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String OBSERVATION_MODEL_STRING = "\n"
                    + "***observations noted during validation of model***\n"
                    + "AxReferenceKey:(parentKeyName=policy,parentKeyVersion=0.0.1,parentLocalName=NULL,"
                    + "localName=state):org.onap.policy.apex.model.policymodel.concepts.AxState:OBSERVATION:"
                    + "state output stateOutput0 is not used directly by any task\n"
                    + "********************************";

    private static final String WARNING_MODEL_STRING = "\n" + "***warnings issued during validation of model***\n"
                    + "AxArtifactKey:(name=policy,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicy:WARNING:state AxReferenceKey:"
                    + "(parentKeyName=policy,parentKeyVersion=0.0.1,parentLocalName=NULL,localName=anotherState) "
                    + "is not referenced in the policy execution tree\n" + "********************************";

    private static final String INVALID_MODEL_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=contextAlbum0,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum:INVALID:scope is not defined\n"
                    + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum:INVALID:scope is not defined\n"
                    + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=policyModel_KeyInfo,version=0.0.1):"
                    + "org.onap.policy.apex.model.basicmodel.concepts.AxKeyInformation:INVALID:"
                    + "keyInfoMap may not be empty\n" + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel_KeyInfo,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel_Schemas,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel_Events,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel_Albums,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel_Tasks,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=policyModel_Policies,version=0.0.1)\n"
                    + "AxArtifactKey:(name=policyModel_Schemas,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas:INVALID:"
                    + "contextSchemas may not be empty\n" + "AxArtifactKey:(name=policyModel_Events,version=0.0.1):"
                    + "org.onap.policy.apex.model.eventmodel.concepts.AxEvents:INVALID:eventMap may not be empty\n"
                    + "AxArtifactKey:(name=policyModel_Albums,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums:OBSERVATION:albums are empty\n"
                    + "AxArtifactKey:(name=policyModel_Tasks,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxTasks:INVALID:taskMap may not be empty\n"
                    + "AxArtifactKey:(name=policyModel_Policies,version=0.0.1)"
                    + ":org.onap.policy.apex.model.policymodel.concepts.AxPolicies:INVALID:policyMap may not be empty\n"
                    + "********************************";

    TestApexModel<AxPolicyModel> testApexModel;

    /**
     * Set up the policy model tests.
     *
     * @throws Exception on setup errors
     */
    @BeforeEach
    public void setup() throws Exception {
        testApexModel = new TestApexModel<AxPolicyModel>(AxPolicyModel.class, new SupportApexPolicyModelCreator());
    }

    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testApexModelVaidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateObservation();
        assertEquals(OBSERVATION_MODEL_STRING, result.toString());
    }

    @Test
    public void testApexModelVaidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateWarning();
        assertEquals(WARNING_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateInvalidModel();
        assertEquals(INVALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateMalstructured();
        assertEquals(INVALID_MODEL_MALSTRUCTURED_STRING, result.toString());
    }

    @Test
    public void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
