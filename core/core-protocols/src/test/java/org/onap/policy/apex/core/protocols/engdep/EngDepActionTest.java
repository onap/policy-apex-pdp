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

package org.onap.policy.apex.core.protocols.engdep;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.apex.core.protocols.engdep.EngDepAction;

/**
 * Test the Eng Dep Action class.
 *
 */
public class EngDepActionTest {

    @Test
    public void test() {
        assertEquals("gets runtime information an Apex engine service", EngDepAction.GET_ENGINE_INFO.getActionString());
        assertEquals("Apex engine service information", EngDepAction.GET_ENGINE_SERVICE_INFO.getActionString());
        assertEquals("gets the status of an Apex engine service", EngDepAction.GET_ENGINE_STATUS.getActionString());
        assertEquals("response from Apex engine service", EngDepAction.RESPONSE.getActionString());
        assertEquals("starts an Apex engine", EngDepAction.START_ENGINE.getActionString());
        assertEquals("starts periodic events on an Apex engine service",
                        EngDepAction.START_PERIODIC_EVENTS.getActionString());
        assertEquals("stops an Apex engine service", EngDepAction.STOP_ENGINE.getActionString());
        assertEquals("stops periodic events on an Apex engine service",
                        EngDepAction.STOP_PERIODIC_EVENTS.getActionString());
        assertEquals("update model on Apex engine service", EngDepAction.UPDATE_MODEL.getActionString());
    }

}
