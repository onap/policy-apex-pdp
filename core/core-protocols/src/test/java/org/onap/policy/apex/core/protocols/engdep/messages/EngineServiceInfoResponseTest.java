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

package org.onap.policy.apex.core.protocols.engdep.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the start engine message.
 */
public class EngineServiceInfoResponseTest {

    @Test
    public void test() {
        AxArtifactKey targetKey = new AxArtifactKey("Target:0.0.1");
        GetEngineServiceInfo request = new GetEngineServiceInfo(targetKey);
        
        EngineServiceInfoResponse response = new EngineServiceInfoResponse(targetKey, true, request);
        assertNotNull(response);
        response = new EngineServiceInfoResponse(targetKey, true, "Response Data", request);
        assertNotNull(response);
        assertEquals("Response Data", response.getMessageData());
        
        AxArtifactKey apexModelKey = new AxArtifactKey("Model:0.0.1");
        response.setApexModelKey(apexModelKey);
        assertEquals(apexModelKey, response.getApexModelKey());
        
        AxArtifactKey engineServiceKey = new AxArtifactKey("EngineService:0.0.1");
        response.setEngineServiceKey(engineServiceKey);;
        assertEquals(engineServiceKey, response.getEngineServiceKey());
        
        List<AxArtifactKey> engineKeyArrayList = new ArrayList<>();
        AxArtifactKey engineKey = new AxArtifactKey("Engine:0.0.1");
        engineKeyArrayList.add(engineKey);
        response.setEngineKeyArray(engineKeyArrayList);
        assertEquals(engineKeyArrayList.get(0), response.getEngineKeyArray()[0]);
        
        response = new EngineServiceInfoResponse(null, false, null);
        assertTrue(response.hashCode() != 0);
        response.setApexModelKey(apexModelKey);
        assertTrue(response.hashCode() != 0);
        response.setApexModelKey(null);
        response.setEngineServiceKey(engineServiceKey);;
        assertTrue(response.hashCode() != 0);
        response.setEngineServiceKey(null);
        response.setEngineKeyArray(engineKeyArrayList);
        assertTrue(response.hashCode() != 0);
        response.setEngineKeyArray(null);
        
        assertTrue(response.equals(response));
        assertFalse(response.equals(null));
        assertFalse(response.equals(new StartEngine(new AxArtifactKey())));

        response = new EngineServiceInfoResponse(null, false, null);
        EngineServiceInfoResponse otherResponse = new EngineServiceInfoResponse(null, false, null);

        response.setApexModelKey(apexModelKey);
        assertFalse(response.equals(otherResponse));
        otherResponse.setApexModelKey(apexModelKey);
        assertTrue(response.equals(otherResponse));
        response.setApexModelKey(null);
        assertFalse(response.equals(otherResponse));
        otherResponse.setApexModelKey(null);
        assertTrue(response.equals(otherResponse));

        response.setEngineServiceKey(engineServiceKey);
        assertFalse(response.equals(otherResponse));
        otherResponse.setEngineServiceKey(engineServiceKey);
        assertTrue(response.equals(otherResponse));
        response.setEngineServiceKey(null);
        assertFalse(response.equals(otherResponse));
        otherResponse.setEngineServiceKey(null);
        assertTrue(response.equals(otherResponse));

        response.setEngineKeyArray(engineKeyArrayList);
        assertFalse(response.equals(otherResponse));
        otherResponse.setEngineKeyArray(engineKeyArrayList);
        assertTrue(response.equals(otherResponse));
        response.setEngineKeyArray(null);
        assertFalse(response.equals(otherResponse));
        otherResponse.setEngineKeyArray(null);
        assertTrue(response.equals(otherResponse));

    }
}
