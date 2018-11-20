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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelSplitter;

public class PolicyModelSplitterTest {
    @Test
    public void test() {
        final AxPolicyModel apexModel = new ApexPolicyModelCreatorTest().getModel();

        final Set<AxArtifactKey> requiredPolicySet = new TreeSet<AxArtifactKey>();
        requiredPolicySet.add(new AxArtifactKey("policy", "0.0.1"));

        // There's only one policy so a split of this model on that policy should return the same
        // model
        AxPolicyModel splitApexModel = null;
        try {
            splitApexModel = PolicyModelSplitter.getSubPolicyModel(apexModel, requiredPolicySet);
        } catch (final ApexModelException e) {
            fail(e.getMessage());
        }

        // The only difference between the models should be that the unused event outEvent1 should
        // not be in the split model
        apexModel.getEvents().getEventMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        apexModel.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        assertTrue(apexModel.equals(splitApexModel));

        final Set<AxArtifactKey> requiredMissingPolicySet = new TreeSet<AxArtifactKey>();
        requiredPolicySet.add(new AxArtifactKey("MissingPolicy", "0.0.1"));

        AxPolicyModel missingSplitApexModel = null;
        try {
            missingSplitApexModel = PolicyModelSplitter.getSubPolicyModel(apexModel, requiredMissingPolicySet);
        } catch (final ApexModelException e) {
            fail(e.getMessage());
        }
        assertNotNull(missingSplitApexModel);

        splitApexModel = null;
        try {
            splitApexModel = PolicyModelSplitter.getSubPolicyModel(apexModel, requiredPolicySet, true);
        } catch (final ApexModelException e) {
            fail(e.getMessage());
        }

        // The only difference between the models should be that the unused event outEvent1 should
        // not be in the split model
        apexModel.getEvents().getEventMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        apexModel.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        assertTrue(apexModel.equals(splitApexModel));

        // There's only one policy so a split of this model on that policy should return the same
        // model
        try {
            apexModel.getKey().setName("InvalidPolicyModelName");
            PolicyModelSplitter.getSubPolicyModel(apexModel, requiredPolicySet);
            fail("test should throw an exception here");
        } catch (final Exception e) {
            assertEquals("source model is invalid: \n***validation of model f", e.getMessage().substring(0, 50));
        }

    }
}
