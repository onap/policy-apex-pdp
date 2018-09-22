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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

/**
 * Test the JSON event converter corner cases.
 *
 */
public class TestJsonEventConverter {
    @Test
    public void testJsonEventConverter() {
        Apex2JsonEventConverter converter = new Apex2JsonEventConverter();

        try {
            converter.init(null);
            fail("test should throw an exception");
        } catch (Exception ie) {
            assertEquals("specified consumer properties are not applicable to the JSON event protocol",
                            ie.getMessage());
        }

        try {
            converter.init(new EventProtocolParameters() {
            });
            fail("test should throw an exception");
        } catch (Exception ie) {
            assertEquals("specified consumer properties are not applicable to the JSON event protocol",
                            ie.getMessage());
        }

        JsonEventProtocolParameters pars = new JsonEventProtocolParameters();
        converter.init(pars);

        try {
            converter.toApexEvent(null, null);
            fail("test should throw an exception");
        } catch (Exception tae) {
            assertEquals("event processing failed, event is null", tae.getMessage());
        }

        try {
            converter.toApexEvent(null, 1);
            fail("test should throw an exception");
        } catch (Exception tae) {
            assertEquals("error converting event \"1\" to a string", tae.getMessage());
        }

        try {
            converter.toApexEvent(null, "[{\"aKey\": 1},{\"aKey\": 2}]");
            fail("test should throw an exception");
        } catch (Exception tae) {
            assertEquals("Failed to unmarshal JSON event: incoming event ([{\"aKey\": 1},{\"aKey\": 2}]) "
                            + "is a JSON object array containing an invalid object "
                            + "{aKey=1.0}, event=[{\"aKey\": 1},{\"aKey\": 2}]", tae.getMessage());
        }

        try {
            converter.toApexEvent(null, "[1,2,3]");
            fail("test should throw an exception");
        } catch (Exception tae) {
            assertEquals("Failed to unmarshal JSON event: incoming event ([1,2,3]) is a JSON object array "
                            + "containing an invalid object 1.0, event=[1,2,3]", tae.getMessage());
        }

        try {
            converter.fromApexEvent(null);
            fail("test should throw an exception");
        } catch (Exception tae) {
            assertEquals("event processing failed, Apex event is null", tae.getMessage());
        }

        try {
            converter.fromApexEvent(new ApexEvent("Event", "0.0.1", "a.name.space", "here", "there"));
            fail("test should throw an exception");
        } catch (Exception tae) {
            assertEquals("Model for org.onap.policy.apex.model.eventmodel.concepts.AxEvents not found in model service",
                            tae.getMessage());
        }
    }
}
