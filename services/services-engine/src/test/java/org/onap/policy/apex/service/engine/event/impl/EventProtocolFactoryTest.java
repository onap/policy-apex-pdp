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

package org.onap.policy.apex.service.engine.event.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

class EventProtocolFactoryTest {
    private EventProtocolFactory factory;

    @BeforeEach
    void setUp() {
        factory = new EventProtocolFactory();
    }

    @Test
    void createConsumerNoConsumerPlugin() {
        final EventProtocolParameters parameters = new ApexEventProtocolParameters();
        parameters.setEventProtocolPluginClass("");
        final String name = RandomStringUtils.randomAlphabetic(9);

        assertThatThrownBy(() -> factory.createConverter(name, parameters))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void createConsumer2() {
        final EventProtocolParameters parameters = new ApexEventProtocolParameters();
        final String name = RandomStringUtils.randomAlphabetic(9);
        parameters.setEventProtocolPluginClass("java.lang.Object");

        assertThatThrownBy(() -> factory.createConverter(name, parameters))
            .isInstanceOf(ApexEventRuntimeException.class);
    }

    @Test
    void createConsumer() {
        final EventProtocolParameters parameters = new JsonEventProtocolParameters();
        final String name = RandomStringUtils.randomAlphabetic(9);

        final ApexEventProtocolConverter actual = factory.createConverter(name, parameters);
        assertNotNull(actual);
    }
}