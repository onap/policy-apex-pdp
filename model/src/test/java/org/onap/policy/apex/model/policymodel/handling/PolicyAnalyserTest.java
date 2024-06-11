/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

public class PolicyAnalyserTest {
    @Test
    public void test() {
        final AxPolicyModel apexModel = new SupportApexPolicyModelCreator().getModel();

        final PolicyAnalyser policyAnalyser = new PolicyAnalyser();
        final PolicyAnalysisResult analysisResult = policyAnalyser.analyse(apexModel);

        assertEquals(EXPECTED_ANALYSIS_RESULT, analysisResult.toString());

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
            + " eventContextItem1:0.0.1\n" + "  inEvent:0.0.1\n" + "  outEvent0:0.0.1\n"
            + "  outEvent1:0.0.1\n" + "Context Album usage\n" + " contextAlbum0:0.0.1\n"
            + "  task:0.0.1\n" + "  policy:0.0.1:NULL:state\n" + " contextAlbum1:0.0.1\n" + "  task:0.0.1\n"
            + "  policy:0.0.1:NULL:state\n" + "Event usage\n" + " inEvent:0.0.1\n" + "  policy:0.0.1:NULL:state\n"
            + " outEvent0:0.0.1\n" + "  policy:0.0.1:NULL:state\n" + " outEvent1:0.0.1 (unused)\n" + "Task usage\n"
            + " task:0.0.1\n" + "  policy:0.0.1:NULL:state\n";
}
