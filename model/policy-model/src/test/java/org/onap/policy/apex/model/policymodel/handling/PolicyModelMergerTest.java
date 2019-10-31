/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Test model merging.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class PolicyModelMergerTest {

    @Test
    public void testPolicyModelMerger() throws ApexModelException {
        final AxPolicyModel leftPolicyModel = new SupportApexPolicyModelCreator().getModel();
        final AxPolicyModel rightPolicyModel = new SupportApexPolicyModelCreator().getModel();

        AxPolicyModel mergedPolicyModel =
            PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, false);
        assertEquals(leftPolicyModel, mergedPolicyModel);
        assertEquals(rightPolicyModel, mergedPolicyModel);

        leftPolicyModel.setKey(new AxArtifactKey("LeftPolicyModel", "0.0.1"));
        assertThatThrownBy(
            () -> PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, false))
                .hasMessageContaining("left model is invalid: \n***validation of model failed");

        leftPolicyModel.setKey(new AxArtifactKey("LeftPolicyModel", "0.0.1"));
        assertNotNull(PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, true, false));

        leftPolicyModel.getKeyInformation().generateKeyInfo(leftPolicyModel);
        mergedPolicyModel = PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, true, false);
        assertNotNull(mergedPolicyModel);

        rightPolicyModel.setKey(new AxArtifactKey("RightPolicyModel", "0.0.1"));
        assertThatThrownBy(
            () -> PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, false))
                .hasMessageContaining("right model is invalid: \n***validation of model failed");

        rightPolicyModel.setKey(new AxArtifactKey("RightPolicyModel", "0.0.1"));
        assertNotNull(PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, true, false));

        rightPolicyModel.getKeyInformation().generateKeyInfo(rightPolicyModel);
        mergedPolicyModel = PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, false);
        assertNotNull(mergedPolicyModel);

        final AxPolicyModel rightPolicyModel2 = new SupportApexPolicyModelCreator().getAnotherModel();
        mergedPolicyModel = PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel2, true, false);
        assertNotNull(mergedPolicyModel);

        mergedPolicyModel = PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel2, true, true);
        assertNotNull(mergedPolicyModel);

        final AxPolicyModel rightPolicyModel3 = new SupportApexPolicyModelCreator().getModel();
        assertThatThrownBy(
            () -> PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel3, true, true))
                .hasMessageContaining("Duplicate policy found");
    }
}
