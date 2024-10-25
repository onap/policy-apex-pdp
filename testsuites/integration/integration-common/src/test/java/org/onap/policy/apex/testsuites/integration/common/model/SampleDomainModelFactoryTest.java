/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.integration.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;

/**
 * Test the evaluation domain model factory.
 */
class SampleDomainModelFactoryTest {

    @Test
    void testSampleDomainModelFactory() {
        SampleDomainModelFactory sdmf = new SampleDomainModelFactory();
        assertNotNull(sdmf);

        AxPolicyModel samplePolicyModel = sdmf.getSamplePolicyModel("JAVASCRIPT");
        assertEquals("SamplePolicyModelJAVASCRIPT:0.0.1", samplePolicyModel.getId());

        samplePolicyModel = sdmf.getSamplePolicyModel("JAVA");
        assertEquals("SamplePolicyModelJAVA:0.0.1", samplePolicyModel.getId());

        samplePolicyModel = sdmf.getSamplePolicyModel("JYTHON");
        assertEquals("SamplePolicyModelJYTHON:0.0.1", samplePolicyModel.getId());

        samplePolicyModel = sdmf.getSamplePolicyModel("JRUBY");
        assertEquals("SamplePolicyModelJRUBY:0.0.1", samplePolicyModel.getId());

        samplePolicyModel = sdmf.getSamplePolicyModel("MVEL");
        assertEquals("SamplePolicyModelMVEL:0.0.1", samplePolicyModel.getId());
    }
}