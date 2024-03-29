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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextModel;

/**
 * Apex context model tests.
 *
 * @author liam
 *
 */
public class ApexContextModelTest {

    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    private static final String OBSERVATION_MODEL_STRING = "\n"
                    + "***observations noted during validation of model***\n"
                    + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
                    + "org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo:OBSERVATION:description is blank\n"
                    + "********************************";

    private static final String WARNING_MODEL_STRING = "\n" + "***warnings issued during validation of model***\n"
                    + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
                    + "org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo:WARNING:"
                    + "UUID is a zero UUID: 00000000-0000-0000-0000-000000000000\n"
                    + "********************************";

    private static final String INVALID_MODEL_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=StringType,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema:INVALID:"
                    + "no schemaDefinition specified, schemaDefinition may not be blank\n"
                    + "AxArtifactKey:(name=contextAlbum0,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum:INVALID:"
                    + "scope is not defined\n" + "********************************";

    private static final String INVALID_MODEL_MALSTRUCTURED_STRING = "\n" + "***validation of model failed***\n"
                    + "AxArtifactKey:(name=ContextModel,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextModel:INVALID:"
                    + "key information not found for key AxArtifactKey:(name=contextAlbum1,version=0.0.2)\n"
                    + "AxArtifactKey:(name=contextAlbum1,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextModel:WARNING:"
                    + "key not found for key information entry\n" + "AxArtifactKey:(name=ContextSchemas,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas:INVALID:"
                    + "key on schemas entry AxArtifactKey:(name=MapType,version=0.0.1) "
                    + "does not equal entry key AxArtifactKey:(name=MapType,version=0.0.2)\n"
                    + "AxArtifactKey:(name=contextAlbums,version=0.0.1):"
                    + "org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbums:INVALID:"
                    + "key on context album entry key AxArtifactKey:(name=contextAlbum1,version=0.0.1) "
                    + "does not equal context album value key AxArtifactKey:(name=contextAlbum1,version=0.0.2)\n"
                    + "********************************";

    TestApexModel<AxContextModel> testApexModel;

    /**
     * Set up tests.
     *
     * @throws Exception a testing exception
     */
    @Before
    public void setup() throws Exception {
        testApexModel = new TestApexModel<AxContextModel>(AxContextModel.class, new TestApexContextModelCreator());
    }

    @Test
    public void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    public void testApexModelVaidateObservation() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelVaidateObservation();
        assertEquals(OBSERVATION_MODEL_STRING, result.toString());
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
    public void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
