/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
        assertNotEquals(0, response.hashCode());
        response.setApexModelKey(apexModelKey);
        assertNotEquals(0, response.hashCode());
        response.setApexModelKey(null);
        response.setEngineServiceKey(engineServiceKey);;
        assertNotEquals(0, response.hashCode());
        response.setEngineServiceKey(null);
        response.setEngineKeyArray(engineKeyArrayList);
        assertNotEquals(0, response.hashCode());
        response.setEngineKeyArray(null);

        assertEquals(response, response);
        assertNotNull(response);
        assertNotEquals(response, (Object) new StartEngine(new AxArtifactKey()));

        response = new EngineServiceInfoResponse(null, false, null);
        EngineServiceInfoResponse otherResponse = new EngineServiceInfoResponse(null, false, null);

        response.setApexModelKey(apexModelKey);
        assertNotEquals(response, otherResponse);
        otherResponse.setApexModelKey(apexModelKey);
        assertEquals(response, otherResponse);
        response.setApexModelKey(null);
        assertNotEquals(response, otherResponse);
        otherResponse.setApexModelKey(null);
        assertEquals(response, otherResponse);

        response.setEngineServiceKey(engineServiceKey);
        assertNotEquals(response, otherResponse);
        otherResponse.setEngineServiceKey(engineServiceKey);
        assertEquals(response, otherResponse);
        response.setEngineServiceKey(null);
        assertNotEquals(response, otherResponse);
        otherResponse.setEngineServiceKey(null);
        assertEquals(response, otherResponse);

        response.setEngineKeyArray(engineKeyArrayList);
        assertNotEquals(response, otherResponse);
        otherResponse.setEngineKeyArray(engineKeyArrayList);
        assertEquals(response, otherResponse);
        response.setEngineKeyArray(null);
        assertNotEquals(response, otherResponse);
        otherResponse.setEngineKeyArray(null);
        assertEquals(response, otherResponse);

    }
}
