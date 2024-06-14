/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation
 *  Modifications Copyright (C) 2022 Bell Canada. All rights reserved.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.Apex2JsonEventConverter;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

/**
 * Test the JSON event converter corner cases.
 *
 */
class JsonEventConverterTest {

    @Test
    void testJsonEventConverter() {
        Apex2JsonEventConverter converter = new Apex2JsonEventConverter();

        assertThatThrownBy(() -> converter.init(null))
            .hasMessage("specified consumer properties are not applicable to the JSON event protocol");
        assertThatThrownBy(() -> converter.init(new EventProtocolParameters() {}))
            .hasMessage("specified consumer properties are not applicable to the JSON event protocol");
        JsonEventProtocolParameters pars = new JsonEventProtocolParameters();
        converter.init(pars);

        assertThatThrownBy(() -> converter.toApexEvent(null, null))
            .hasMessage("event processing failed, event is null");
        assertThatThrownBy(() -> converter.toApexEvent(null, 1))
            .hasMessage("error converting event \"1\" to a string");
        assertThatThrownBy(() -> converter.toApexEvent(null, "[{\"aKey\": 1},{\"aKey\": 2}]"))
            .hasMessageStartingWith("Failed to unmarshal JSON event")
            .cause().hasMessageContaining("event received without mandatory parameter \"name\" "
                            + "on configuration or on event");
        assertThatThrownBy(() -> converter.toApexEvent(null, "[1,2,3]"))
            .hasMessageStartingWith("Failed to unmarshal JSON event")
            .cause().hasMessageContaining("incoming event ([1,2,3]) is a JSON object array "
                    + "containing an invalid object 1.0");
        assertThatThrownBy(() -> converter.fromApexEvent(null))
            .hasMessage("event processing failed, Apex event is null");
        assertThatThrownBy(() -> converter.fromApexEvent(new ApexEvent("Event", "0.0.1", "a.name.space",
                "here", "there", "")))
            .hasMessage("Model for org.onap.policy.apex.model.eventmodel.concepts.AxEvents not found in model service");
    }
}
