/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021, 2024 Nordix Foundation.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  SPDX-License-Identifier: Apache-2.0
 *  ============LICENSE_END=========================================================
 */

package org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

class ApexEventProtocolParametersTest {

    @Test
    void testNoArgConstructor() {
        final ApexEventProtocolParameters apexEventProtocolParameters = new ApexEventProtocolParameters();
        final String actual = apexEventProtocolParameters.getLabel();
        final String pluginClass = apexEventProtocolParameters.getEventProtocolPluginClass();

        assertEquals(ApexEventProtocolParameters.APEX_EVENT_PROTOCOL_LABEL, actual);
        assertEquals(Apex2ApexEventConverter.class.getName(), pluginClass);
    }

    @Test
    void testConstructor() {
        final String expected = RandomStringUtils.randomAlphabetic(6);
        final ApexEventProtocolParameters apexEventProtocolParameters = new ApexEventProtocolParameters(expected);
        final String actual = apexEventProtocolParameters.getLabel();
        final String pluginClass = apexEventProtocolParameters.getEventProtocolPluginClass();

        assertEquals(expected, actual);
        assertEquals(Apex2ApexEventConverter.class.getName(), pluginClass);
    }

}