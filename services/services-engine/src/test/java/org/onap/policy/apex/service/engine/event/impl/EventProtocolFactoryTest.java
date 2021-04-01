/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2021  Nordix Foundation
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

import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventProtocolConverter;
import org.onap.policy.apex.service.engine.event.ApexEventRuntimeException;
import org.onap.policy.apex.service.engine.event.impl.apexprotocolplugin.ApexEventProtocolParameters;
import org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin.JsonEventProtocolParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

public class EventProtocolFactoryTest {
    private EventProtocolFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new EventProtocolFactory();
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void createConsumerNoConsumerPlugin() {
        final EventProtocolParameters parameters = new ApexEventProtocolParameters();
        parameters.setEventProtocolPluginClass("");
        final String name = RandomStringUtils.randomAlphabetic(9);

        factory.createConverter(name, parameters);
    }

    @Test(expected = ApexEventRuntimeException.class)
    public void createConsumer2() {
        final EventProtocolParameters parameters = new ApexEventProtocolParameters();
        final String name = RandomStringUtils.randomAlphabetic(9);
        parameters.setEventProtocolPluginClass("java.lang.Object");

        factory.createConverter(name, parameters);
    }

    @Test
    public void createConsumer() {
        final EventProtocolParameters parameters = new JsonEventProtocolParameters();
        final String name = RandomStringUtils.randomAlphabetic(9);

        final ApexEventProtocolConverter actual = factory.createConverter(name, parameters);
        assertNotNull(actual);
    }
}