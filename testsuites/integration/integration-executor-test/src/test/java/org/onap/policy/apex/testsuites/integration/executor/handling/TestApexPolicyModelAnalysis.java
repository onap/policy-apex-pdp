/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyAnalyser;
import org.onap.policy.apex.model.policymodel.handling.PolicyAnalysisResult;

public class TestApexPolicyModelAnalysis {
    @Test
    public void testApexPolicyModelAnalysis() throws Exception {
        final AxPolicyModel model = new TestApexSamplePolicyModelCreator("MVEL").getModel();
        final PolicyAnalysisResult result = new PolicyAnalyser().analyse(model);

        assertNotNull(result);
    }
}
