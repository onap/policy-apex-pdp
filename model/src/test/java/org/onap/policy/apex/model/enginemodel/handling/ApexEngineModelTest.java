/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.enginemodel.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;

class ApexEngineModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";
    private static final String AX_ARTIFACT_KEY = "AxArtifactKey:(name=AnEngine,version=0.0.1)";
    private static final String ENGINE_MODEL_CLASS = "org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel";

    private static final String INVALID_MODEL_STRING =
        "\n" + "***validation of model failed***\n"
            + AX_ARTIFACT_KEY + ":"
            + ENGINE_MODEL_CLASS + ":INVALID:"
            + "AxEngineModel - state is UNDEFINED\n"
            + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING =
        "\n" + "***validation of model failed***\n"
            + AX_ARTIFACT_KEY + ":"
            + ENGINE_MODEL_CLASS + ":INVALID:"
            + "AxEngineModel - timestamp is not set\n" + AX_ARTIFACT_KEY + ":"
            + ENGINE_MODEL_CLASS + ":INVALID:"
            + "AxEngineModel - state is UNDEFINED\n"
            + "********************************";

    TestApexModel<AxEngineModel> testApexModel;

    /**
     * Set up the test.
     *
     * @throws Exception errors from test setup
     */
    @BeforeEach
    public void setup() throws Exception {
        testApexModel = new TestApexModel<>(AxEngineModel.class, new DummyTestApexEngineModelCreator());
    }

    @Test
    void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    void testModelValidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateInvalidModel();
        assertEquals(INVALID_MODEL_STRING, result.toString());
    }

    @Test
    void testModelValidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateMalstructured();
        assertEquals(INVALID_MODEL_MALSTRUCTURED_STRING, result.toString());
    }

    @Test
    void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
