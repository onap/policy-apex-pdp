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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelException;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.handling.PolicyModelMerger;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TestPolicyModelMerger {

    @Test
    public void testPolicyModelMerger() {
        final AxPolicyModel leftPolicyModel = new TestApexPolicyModelCreator().getModel();
        AxPolicyModel rightPolicyModel = new TestApexPolicyModelCreator().getModel();

        try {
            final AxPolicyModel mergedPolicyModel =
                    PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false);
            assertEquals(leftPolicyModel, mergedPolicyModel);
            assertEquals(rightPolicyModel, mergedPolicyModel);
        } catch (final ApexModelException e) {
            fail("test should not throw an exception");
        }

        leftPolicyModel.setKey(new AxArtifactKey("LeftPolicyModel", "0.0.1"));
        try {
            PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false);
            fail("test should throw an exception here");
        } catch (final ApexModelException e) {
            assertEquals("left model is invalid: \n***validation of model fai", e.getMessage().substring(0, 50));
        }

        leftPolicyModel.setKey(new AxArtifactKey("LeftPolicyModel", "0.0.1"));
        try {
            assertNotNull(PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, true));
        } catch (final ApexModelException e) {
            fail("test should not throw an exception");
        }

        leftPolicyModel.getKeyInformation().generateKeyInfo(leftPolicyModel);
        try {
            final AxPolicyModel mergedPolicyModel =
                    PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, true);
            assertNotNull(mergedPolicyModel);
        } catch (final ApexModelException e) {
            fail("test should not throw an exception");
        }

        rightPolicyModel.setKey(new AxArtifactKey("RightPolicyModel", "0.0.1"));
        try {
            PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false);
            fail("test should throw an exception here");
        } catch (final ApexModelException e) {
            assertEquals("right model is invalid: \n***validation of model fa", e.getMessage().substring(0, 50));
        }

        rightPolicyModel.setKey(new AxArtifactKey("RightPolicyModel", "0.0.1"));
        try {
            assertNotNull(PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false, true));
        } catch (final ApexModelException e) {
            fail("test should not throw an exception");
        }

        rightPolicyModel.getKeyInformation().generateKeyInfo(rightPolicyModel);
        try {
            final AxPolicyModel mergedPolicyModel =
                    PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, false);
            assertNotNull(mergedPolicyModel);
        } catch (final ApexModelException e) {
            fail("test should not throw an exception");
        }

        rightPolicyModel = new TestApexPolicyModelCreator().getAnotherModel();
        try {
            final AxPolicyModel mergedPolicyModel =
                    PolicyModelMerger.getMergedPolicyModel(leftPolicyModel, rightPolicyModel, true);
            assertNotNull(mergedPolicyModel);
        } catch (final ApexModelException e) {
            fail("test should not throw an exception");
        }
    }
}
