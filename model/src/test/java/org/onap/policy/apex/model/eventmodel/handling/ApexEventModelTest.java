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

package org.onap.policy.apex.model.eventmodel.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.eventmodel.concepts.AxEventModel;

class ApexEventModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String RESOURCES_FOLDER = "src/test/resources/eventmodel/handling/";
    private static final String OBSERVATION_MODEL_STRING = "ApexEventModelTest_ObservationModel.txt";
    private static final String WARNING_MODEL_STRING = "ApexEventModelTest_WarningModel.txt";
    private static final String INVALID_MODEL_STRING = "ApexEventModelTest_InvalidModel.txt";
    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "ApexEventModelTest_InvalidModelMalStructured.txt";

    TestApexModel<AxEventModel> testApexModel;

    /**
     * Set up the test.
     *
     * @throws Exception exceptions from the test
     */
    @BeforeEach
    public void setup() throws Exception {
        testApexModel = new TestApexModel<>(AxEventModel.class, new DummyTestApexEventModelCreator());
    }

    @Test
    void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    void testApexModelValidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateObservation();
        String expected = loadMessage(OBSERVATION_MODEL_STRING);
        assertEquals(expected, result.toString());
    }

    @Test
    void testApexModelValidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateWarning();
        String expected = loadMessage(WARNING_MODEL_STRING);
        assertEquals(expected, result.toString());
    }

    @Test
    void testModelValidateInvalidModel() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateInvalidModel();
        String expected = loadMessage(INVALID_MODEL_STRING);
        assertEquals(expected, result.toString());
    }

    @Test
    void testModelValidateMalstructured() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateMalstructured();
        String expected = loadMessage(INVALID_MODEL_MALSTRUCTURED_STRING);
        assertEquals(expected, result.toString());
    }

    @Test
    void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }

    private String loadMessage(String file) {
        try {
            Path resource = Paths.get(RESOURCES_FOLDER + file);
            return Files.readString(resource);
        } catch (Exception e) {
            fail(e.getMessage());
            return e.getMessage();
        }
    }
}
