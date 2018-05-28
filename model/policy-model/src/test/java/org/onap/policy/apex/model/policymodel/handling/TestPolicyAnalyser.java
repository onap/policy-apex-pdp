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

package org.onap.policy.apex.model.policymodel.handling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyAnalyser;
import org.onap.policy.apex.model.policymodel.handling.PolicyAnalysisResult;

public class TestPolicyAnalyser {
    @Test
    public void test() {
        final AxPolicyModel apexModel = new TestApexPolicyModelCreator().getModel();

        final PolicyAnalyser policyAnalyser = new PolicyAnalyser();
        final PolicyAnalysisResult analysisResult = policyAnalyser.analyse(apexModel);

        assertTrue(analysisResult.toString().equals(EXPECTED_ANALYSIS_RESULT));

        assertNotNull(analysisResult.getUsedContextAlbums());
        assertNotNull(analysisResult.getUsedContextSchemas());
        assertNotNull(analysisResult.getUsedEvents());
        assertNotNull(analysisResult.getUsedTasks());
        assertNotNull(analysisResult.getUnusedContextAlbums());
        assertNotNull(analysisResult.getUnusedContextSchemas());
        assertNotNull(analysisResult.getUnusedEvents());
        assertNotNull(analysisResult.getUnusedTasks());
    }

    private static final String EXPECTED_ANALYSIS_RESULT = "" + "Context Schema usage\n" + " MapType:0.0.1\n"
            + "  contextAlbum0:0.0.1\n" + " StringType:0.0.1\n" + "  contextAlbum1:0.0.1\n"
            + " eventContextItem0:0.0.1\n" + "  inEvent:0.0.1\n" + "  outEvent0:0.0.1\n" + "  outEvent1:0.0.1\n"
            + "  task:0.0.1\n" + " eventContextItem1:0.0.1\n" + "  inEvent:0.0.1\n" + "  outEvent0:0.0.1\n"
            + "  outEvent1:0.0.1\n" + "  task:0.0.1\n" + "Context Album usage\n" + " contextAlbum0:0.0.1\n"
            + "  task:0.0.1\n" + "  policy:0.0.1:NULL:state\n" + " contextAlbum1:0.0.1\n" + "  task:0.0.1\n"
            + "  policy:0.0.1:NULL:state\n" + "Event usage\n" + " inEvent:0.0.1\n" + "  policy:0.0.1:NULL:state\n"
            + " outEvent0:0.0.1\n" + "  policy:0.0.1:NULL:state\n" + " outEvent1:0.0.1 (unused)\n" + "Task usage\n"
            + " task:0.0.1\n" + "  policy:0.0.1:NULL:state\n";
}
