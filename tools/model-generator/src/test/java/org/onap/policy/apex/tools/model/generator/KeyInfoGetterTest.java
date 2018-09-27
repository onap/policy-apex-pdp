/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.tools.model.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.testsuites.integration.common.model.SampleDomainModelFactory;

/**
 * Test the Key Info Getter.
 */
public class KeyInfoGetterTest {

    @Test
    public void testKeyInfoGetter() {
        AxPolicyModel sampleModel = new SampleDomainModelFactory().getSamplePolicyModel("JAVASCRIPT");

        KeyInfoGetter kiGetter = new KeyInfoGetter(sampleModel);

        assertNull(kiGetter.getName(null));
        assertEquals("SamplePolicyModelJAVASCRIPT", kiGetter.getName(sampleModel.getKey()));

        assertNull(kiGetter.getUuid(null));
        assertNull(kiGetter.getUuid(new AxArtifactKey()));
        assertEquals(36, kiGetter.getUuid(sampleModel.getKey()).length());

        assertNull(kiGetter.getDesc(null));
        assertNull(kiGetter.getDesc(new AxArtifactKey()));
        assertEquals("Generated description for concept referred to by key " + "\"SamplePolicyModelJAVASCRIPT:0.0.1\"",
                        kiGetter.getDesc(sampleModel.getKey()));
        
        assertNull(kiGetter.getVersion(null));
        assertEquals("0.0.1", kiGetter.getVersion(sampleModel.getKey()));
        
        AxState matchState = sampleModel.getPolicies().get("Policy0").getStateMap().get("Match");
        
        assertNull(kiGetter.getLName(null));
        assertEquals("Match", kiGetter.getLName(matchState.getKey()));
        
        assertNull(kiGetter.getPName(null));
        assertEquals("Policy0", kiGetter.getPName(matchState.getKey()));
        
        assertNull(kiGetter.getPVersion(null));
        assertEquals("0.0.1", kiGetter.getPVersion(matchState.getKey()));
        
        assertNull(kiGetter.getPlName(null));
        assertEquals("NULL", kiGetter.getPlName(matchState.getKey()));
    }
}
