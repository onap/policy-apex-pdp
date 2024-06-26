/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.executor.handling;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.test.TestApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

class TestApexSamplePolicyCreateModelFiles {
    @Test
    void testModelWriteRead() {
        String[] types = {"JAVA", "JAVASCRIPT", "JRUBY", "JYTHON", "MVEL"};
        for (String type: types) {
            final TestApexSamplePolicyModelCreator apexPolicyModelCreator = new TestApexSamplePolicyModelCreator(type);
            final TestApexModel<AxPolicyModel> testApexPolicyModel =
                    new TestApexModel<AxPolicyModel>(AxPolicyModel.class, apexPolicyModelCreator);
            assertThatCode(testApexPolicyModel::testApexModelWriteReadJson).doesNotThrowAnyException();
        }
    }
}
