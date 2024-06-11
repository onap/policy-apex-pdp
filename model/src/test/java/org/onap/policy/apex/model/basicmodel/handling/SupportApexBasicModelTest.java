/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.handling;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

class SupportApexBasicModelTest {


    // As there are no real concepts in a basic model, this is as near to a valid model as we can get
    private static final String VALID_MODEL_STRING = "basicmodel/handling/SupportApexBasicModelTest_ValidModel.txt";
    private static final String WARNING_MODEL_STRING = "basicmodel/handling/SupportApexBasicModelTest_WarningModel.txt";
    private static final String INVALID_MODEL_STRING = "basicmodel/handling/SupportApexBasicModelTest_InvalidModel.txt";
    private static final String INVALID_MODEL_MALSTRUCTURED_STRING =
        "basicmodel/handling/SupportApexBasicModelTest_InvalidModelMalStructured.txt";

    TestApexModel<AxModel> testApexModel;

    /**
     * Set up the test.
     */
    @BeforeEach
    void setup() {
        testApexModel = new TestApexModel<>(AxModel.class, new DummyApexBasicModelCreator());
    }

    @Test
    void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        var expectedResult = ResourceUtils.getResourceAsString(VALID_MODEL_STRING);
        assertEquals(expectedResult, result.toString());
    }

    @Test
    void testApexModelValidateObservation() {
        assertThatThrownBy(testApexModel::testApexModelValidateObservation)
            .hasMessage("model should have observations");
    }

    @Test
    void testApexModelValidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateWarning();
        var expectedResult = ResourceUtils.getResourceAsString(WARNING_MODEL_STRING);
        assertEquals(expectedResult, result.toString());
    }

    @Test
    void testModelValidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateInvalidModel();
        var expectedResult = ResourceUtils.getResourceAsString(INVALID_MODEL_STRING);
        assertEquals(expectedResult, result.toString());
    }

    @Test
    void testModelValidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateMalstructured();
        var expectedResult = ResourceUtils.getResourceAsString(INVALID_MODEL_MALSTRUCTURED_STRING);
        assertEquals(expectedResult, result.toString());
    }

    @Test
    void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
