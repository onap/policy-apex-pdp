/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2022, 2024 Nordix Foundation.
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

package org.onap.policy.apex.model.modelapi;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.TextFileUtils;

class RealModelTest {

    @Test
    void testRealModel() throws Exception {

        final String modelString = TextFileUtils.getTextFileAsString("src/test/resources/models/PolicyModel.json");

        AxPolicyModel policyModel = new ApexModelReader<AxPolicyModel>(AxPolicyModel.class).read(modelString);

        AxValidationResult result = new AxValidationResult();
        assertTrue(policyModel.validate(result).isValid());
    }
}
