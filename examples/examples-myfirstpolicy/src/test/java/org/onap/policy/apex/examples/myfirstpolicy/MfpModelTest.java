/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.examples.myfirstpolicy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Test MyFirstPolicy Model.
 *
 * @author John Keeney (john.keeney@ericsson.com)
 */
class MfpModelTest {
    private static TestApexModel<AxPolicyModel> testApexModel1;
    private static TestApexModel<AxPolicyModel> testApexModel2;

    /**
     * Setup.
     */
    @BeforeAll
    static void setup() {
        testApexModel1 = new TestApexModel<>(AxPolicyModel.class, new TestMfpModelCreator.TestMfp1ModelCreator());
        testApexModel2 = new TestApexModel<>(AxPolicyModel.class, new TestMfpModelCreator.TestMfp2ModelCreator());
    }

    /**
     * Test model is valid.
     *
     * @throws Exception if there is an error
     */
    @Test
    void testModelValid() throws Exception {
        AxValidationResult result = testApexModel1.testApexModelValid();
        assertTrue(result.isOk(), "Model did not validate cleanly");

        result = testApexModel2.testApexModelValid();
        assertTrue(result.isOk(), "Model did not validate cleanly");
    }

    /**
     * Test model write and read JSON.
     *
     * @throws Exception if there is an error
     */
    @Test
    void testModelWriteReadJson() throws Exception {
        testApexModel1.testApexModelWriteReadJson();
        testApexModel2.testApexModelWriteReadJson();
    }
}
