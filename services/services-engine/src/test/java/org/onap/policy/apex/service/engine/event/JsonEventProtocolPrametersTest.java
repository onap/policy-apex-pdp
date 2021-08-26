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

package org.onap.policy.apex.service.engine.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;

/**
 * Test the JSON event parameters.
 *
 */
public class JsonEventProtocolPrametersTest {

    @Test
    public void testJsonParameters() {
        assertNotNull(new JsonEventProtocolParameters());
        
        JsonEventProtocolParameters params = new JsonEventProtocolParameters();

        params.setLabel("MyLabel");
        assertEquals("MyLabel", params.getLabel());
        assertEquals("MyLabel", params.getName());

        params.setEventProtocolPluginClass("MyPluginClass");
        assertEquals("MyPluginClass", params.getEventProtocolPluginClass());
        
        params.setNameAlias("MyNameAlias");
        assertEquals("MyNameAlias", params.getNameAlias());
        
        params.setVersionAlias("MyVersionAlias");
        assertEquals("MyVersionAlias", params.getVersionAlias());
        
        params.setNameSpaceAlias("MyNameSpaceAlias");
        assertEquals("MyNameSpaceAlias", params.getNameSpaceAlias());
        
        params.setSourceAlias("MySourceAlias");
        assertEquals("MySourceAlias", params.getSourceAlias());
        
        params.setTargetAlias("MyTargetAlias");
        assertEquals("MyTargetAlias", params.getTargetAlias());
        
        params.setPojoField("MyPojoField");
        assertEquals("MyPojoField", params.getPojoField());
    }
}