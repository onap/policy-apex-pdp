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

package org.onap.policy.apex.model.contextmodel.handling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;

/**
 * Apex context model tests.
 *
 * @author liam
 */
class ApexContextModelTest {

    private static final String VALID_MODEL_STRING = "***validation of model successful***";
    private static final String POLICY_APEX_BASICMODEL_PACKAGE = "org.onap.policy.apex.model.basicmodel.concepts.";
    private static final String APEX_MODEL_CONTEXTMODEL_PACKAGE = "org.onap.policy.apex.model.contextmodel.concepts.";
    private static final String END_OF_MESSAGE = "********************************";

    private static final String OBSERVATION_MODEL_STRING = "\n"
        + "***observations noted during validation of model***\n"
        + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
        + POLICY_APEX_BASICMODEL_PACKAGE + "AxKeyInfo:OBSERVATION:description is blank\n"
        + END_OF_MESSAGE;

    private static final String WARNING_MODEL_STRING = "\n"
        + "***warnings issued during validation of model***\n"
        + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
        + POLICY_APEX_BASICMODEL_PACKAGE + "AxKeyInfo:WARNING:"
        + "UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n"
        + END_OF_MESSAGE;


    private static final String INVALID_MODEL_STRING = "\n"
        + "***validation of model failed***\n"
        + "AxArtifactKey:(name=StringType,version=0.0.1):"
        + APEX_MODEL_CONTEXTMODEL_PACKAGE + "AxContextSchema:INVALID:"
        + "no schemaDefinition specified, schemaDefinition may not be blank\n"
        + "AxArtifactKey:(name=contextAlbum0,version=0.0.1):"
        + APEX_MODEL_CONTEXTMODEL_PACKAGE + "AxContextAlbum:INVALID:"
        + "scope is not defined\n" + END_OF_MESSAGE;

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n"
        + "***validation of model failed***\nAxArtifactKey:(name=ContextModel,version=0.0.1):"
        + APEX_MODEL_CONTEXTMODEL_PACKAGE + "AxContextModel:INVALID:"
        + "key information not found for key AxArtifactKey:(name=contextAlbum1,version=0.0.2)\n"
        + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
        + APEX_MODEL_CONTEXTMODEL_PACKAGE + "AxContextModel:WARNING:"
        + "key not found for key information entry\nAxArtifactKey:(name=ContextSchemas,version=0.0.1):"
        + APEX_MODEL_CONTEXTMODEL_PACKAGE + "AxContextSchemas:INVALID:"
        + "key on schemas entry AxArtifactKey:(name=MapType,version=0.0.1) "
        + "does not equal entry key AxArtifactKey:(name=MapType,version=0.0.2)\n"
        + "AxArtifactKey:(name=contextAlbums,version=0.0.1):"
        + APEX_MODEL_CONTEXTMODEL_PACKAGE + "AxContextAlbums:INVALID:"
        + "key on context album entry key AxArtifactKey:(name=contextAlbum1,version=0.0.1) "
        + "does not equal context album value key AxArtifactKey:(name=contextAlbum1,version=0.0.2)\n"
        + END_OF_MESSAGE;

    TestApexModel<AxContextModel> testApexModel;

    /**
     * Set up tests.
     *
     * @throws Exception a testing exception
     */
    @BeforeEach
    public void setup() throws Exception {
        testApexModel = new TestApexModel<>(AxContextModel.class, new TestApexContextModelCreator());
    }

    @Test
    void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    void testApexModelValidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateObservation();
        assertEquals(OBSERVATION_MODEL_STRING, result.toString());
    }

    @Test
    void testApexModelValidateWarning() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValidateWarning();
        assertEquals(WARNING_MODEL_STRING, result.toString());
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
