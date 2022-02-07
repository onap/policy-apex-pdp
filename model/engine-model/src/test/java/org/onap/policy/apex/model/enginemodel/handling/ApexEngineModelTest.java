/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020,2022 Nordix Foundation.
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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;

public class ApexEngineModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String INVALID_MODEL_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=AnEngine,version=0.0.1):"
                    + "org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel:INVALID:"
                    + "AxEngineModel - state is UNDEFINED\n" + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=AnEngine,version=0.0.1):"
                    + "org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel:INVALID:"
                    + "AxEngineModel - timestamp is not set\n" + "AxArtifactKey:(name=AnEngine,version=0.0.1):"
                    + "org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel:INVALID:"
                    + "AxEngineModel - state is UNDEFINED\n" + "********************************";

    TestApexModel<AxEngineModel> testApexModel;

    /**
     * Set up the test.
     *
     * @throws Exception errors from test setup
     */
    @Before
    public void setup() throws Exception {
        testApexModel = new TestApexModel<AxEngineModel>(AxEngineModel.class, new DummyTestApexEngineModelCreator());
    }

    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateInvalidModel();
        assertEquals(INVALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testModelVaidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateMalstructured();
        assertEquals(INVALID_MODEL_MALSTRUCTURED_STRING, result.toString());
    }

    @Test
    public void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
