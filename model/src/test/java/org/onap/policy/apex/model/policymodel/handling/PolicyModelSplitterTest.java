/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

public class PolicyModelSplitterTest {
    @Test
    public void test() throws ApexModelException {
        final AxPolicyModel apexModel = new SupportApexPolicyModelCreator().getModel();

        final Set<AxArtifactKey> requiredPolicySet = new TreeSet<AxArtifactKey>();
        requiredPolicySet.add(new AxArtifactKey("policy", "0.0.1"));

        // There's only one policy so a split of this model on that policy should return the same
        // model
        AxPolicyModel splitApexModel = null;
        splitApexModel = PolicyModelSplitter.getSubPolicyModel(apexModel, requiredPolicySet);

        // The only difference between the models should be that the unused event outEvent1 should
        // not be in the split model
        apexModel.getEvents().getEventMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        apexModel.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        assertEquals(apexModel, splitApexModel);

        final Set<AxArtifactKey> requiredMissingPolicySet = new TreeSet<AxArtifactKey>();
        requiredPolicySet.add(new AxArtifactKey("MissingPolicy", "0.0.1"));

        AxPolicyModel missingSplitApexModel = null;
        missingSplitApexModel = PolicyModelSplitter.getSubPolicyModel(apexModel, requiredMissingPolicySet);
        assertNotNull(missingSplitApexModel);

        splitApexModel = null;
        splitApexModel = PolicyModelSplitter.getSubPolicyModel(apexModel, requiredPolicySet, true);

        // The only difference between the models should be that the unused event outEvent1 should
        // not be in the split model
        apexModel.getEvents().getEventMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        apexModel.getKeyInformation().getKeyInfoMap().remove(new AxArtifactKey("outEvent1", "0.0.1"));
        assertEquals(apexModel, splitApexModel);

        // There's only one policy so a split of this model on that policy should return the same
        // model
        apexModel.getKey().setName("InvalidPolicyModelName");
        assertThatThrownBy(() -> PolicyModelSplitter.getSubPolicyModel(apexModel, requiredPolicySet))
            .hasMessageContaining("source model is invalid: \n***validation of model f");
    }
}
