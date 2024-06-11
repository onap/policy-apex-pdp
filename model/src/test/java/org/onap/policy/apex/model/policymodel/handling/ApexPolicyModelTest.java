/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
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
import org.onap.policy.common.utils.resources.ResourceUtils;

class ApexPolicyModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String OBSERVATION_MODEL_STRING = "policymodel/handling/ApexPolicyModel_ObservationModel.txt";
    private static final String WARNING_MODEL_STRING = "policymodel/handling/ApexPolicyModel_WarningModel.txt";
    private static final String INVALID_MODEL_STRING = "policymodel/handling/ApexPolicyModelTest_InvalidModel.txt";
    private static final String INVALID_MODEL_MALSTRUCTURED_STRING =
        "policymodel/handling/ApexPolicyModelTest_InvalidMalStructuredModel.txt";

    TestApexModel<AxPolicyModel> testApexModel;

    /**
     * Set up the policy model tests.
     *
     * @throws Exception on setup errors
     */
    @BeforeEach
    public void setup() throws Exception {
        testApexModel = new TestApexModel<>(AxPolicyModel.class, new SupportApexPolicyModelCreator());
    }

    @Test
    void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    void testApexModelValidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateObservation();
        assertEquals(ResourceUtils.getResourceAsString(OBSERVATION_MODEL_STRING), result.toString());
    }

    @Test
    void testApexModelValidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateWarning();
        assertEquals(ResourceUtils.getResourceAsString(WARNING_MODEL_STRING), result.toString());
    }

    @Test
    void testModelValidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateInvalidModel();
        assertEquals(ResourceUtils.getResourceAsString(INVALID_MODEL_STRING), result.toString());
    }

    @Test
    void testModelValidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateMalstructured();
        assertEquals(ResourceUtils.getResourceAsString(INVALID_MODEL_MALSTRUCTURED_STRING), result.toString());
    }

    @Test
    void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
