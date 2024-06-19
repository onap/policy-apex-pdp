/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2022,2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.examples.aadm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

class AadmModelTest {
    private static final String VALID_MODEL_STRING = "***validation of model successful***";

    TestApexModel<AxPolicyModel> testApexModel;

    /**
     * Sets up embedded Derby database and the AADM model for the tests.
     */
    @BeforeEach
    void setup() {
        testApexModel = new TestApexModel<>(AxPolicyModel.class, new TestAadmModelCreator());
    }

    @Test
    void testModelValid() throws Exception {
        final AxValidationResult result = testApexModel.testApexModelValid();
        assertEquals(VALID_MODEL_STRING, result.toString());
    }

    @Test
    void testModelWriteReadJson() throws Exception {
        testApexModel.testApexModelWriteReadJson();
    }
}
