/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020-2022 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxModel;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;

public class SupportApexBasicModelTest {
    // As there are no real concepts in a basic model, this is as near to a valid model as we can get
    private static final String VALID_MODEL_STRING = "\n" + "***warnings issued during validation of model***\n"
                    + "AxArtifactKey:(name=FloatKIKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts"
                    + ".AxModel:WARNING:key not found for key information entry\n"
                    + "AxArtifactKey:(name=IntegerKIKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts"
                    + ".AxModel:WARNING:key not found for key information entry\n" + "********************************";

    private static final String WARNING_MODEL_STRING = "\n" + "***warnings issued during validation of model***\n"
                    + "AxArtifactKey:(name=FloatKIKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts"
                    + ".AxModel:WARNING:key not found for key information entry\n"
                    + "AxArtifactKey:(name=IntegerKIKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts"
                    + ".AxModel:WARNING:key not found for key information entry\n"
                    + "AxArtifactKey:(name=Unref0,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts"
                    + ".AxModel:WARNING:key not found for key information entry\n"
                    + "AxArtifactKey:(name=Unref1,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts"
                    + ".AxModel:WARNING:key not found for key information entry\n" + "********************************";

    private static final String INVALID_MODEL_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=BasicModelKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts."
                    + "AxKeyInfo:WARNING:UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n"
                    + "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts."
                    + "AxKeyInfo:OBSERVATION:description is blank\n"
                    + "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts."
                    + "AxKeyInfo:WARNING:UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n"
                    + "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts."
                    + "AxKeyInformation:INVALID:duplicate UUID found on keyInfoMap entry AxArtifactKey:"
                    + "(name=KeyInfoMapKey,version=0.0.1):00000000-0000-0000-0000-000000000000\n"
                    + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=BasicModelKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts."
                    + "AxKeyInfo:WARNING:UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n"
                    + "AxArtifactKey:(name=BasicModelKey,version=0.0.1):org.onap.policy.apex.model.basicmodel.concepts."
                    + "AxModel:INVALID:key information not found for key "
                    + "AxArtifactKey:(name=KeyInfoMapKey,version=0.0.1)\n" + "********************************";

    TestApexModel<AxModel> testApexModel;

    /**
     * Set up the test.
     *
     * @throws Exception any exception thrown by the test
     */
    @Before
    public void setup() throws Exception {
        testApexModel = new TestApexModel<AxModel>(AxModel.class, new DummyApexBasicModelCreator());
    }

    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testApexModelVaidateObservation() throws Exception {
        assertThatThrownBy(testApexModel::testApexModelVaidateObservation)
            .hasMessage("model should have observations");
    }

    @Test
    public void testApexModelVaidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateWarning();
        assertEquals(WARNING_MODEL_STRING, result.toString());
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
    public void testModelWriteReadXml() throws Exception {
        testApexModel.testApexModelWriteReadXml();
    }

    @Test
    public void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
