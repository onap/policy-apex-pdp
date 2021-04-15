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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.RestPluginCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;

public class EventProducerFactoryTest {
    private EventProducerFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new EventProducerFactory();
    }

    @Test
    public void createConsumerNoTechnologyParameter() {
        final EventHandlerParameters parameters = new EventHandlerParameters();
        final String name = RandomStringUtils.randomAlphabetic(4);

        assertThatThrownBy(() -> factory.createProducer(name, parameters))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void createConsumerNoConsumerPlugin() {
        final EventHandlerParameters parameters = new EventHandlerParameters();
        final String name = RandomStringUtils.randomAlphabetic(4);
        parameters.setCarrierTechnologyParameters(new RestPluginCarrierTechnologyParameters());

        assertThatThrownBy(() -> factory.createProducer(name, parameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void createConsumerWrongProducerPluginName() {
        final EventHandlerParameters parameters = new EventHandlerParameters();
        final RestPluginCarrierTechnologyParameters technologyParameters =
            new RestPluginCarrierTechnologyParameters();
        final String name = RandomStringUtils.randomAlphabetic(4);
        technologyParameters.setEventProducerPluginClass("java.lang.Object");
        parameters.setCarrierTechnologyParameters(technologyParameters);

        assertThatThrownBy(() -> factory.createProducer(name, parameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void createConsumer() throws ApexEventException {
        final EventHandlerParameters parameters = new EventHandlerParameters();
        parameters.setCarrierTechnologyParameters(new SuperDooperCarrierTechnologyParameters());
        final String name = RandomStringUtils.randomAlphabetic(4);

        final ApexEventProducer actual = factory.createProducer(name, parameters);
        assertNotNull(actual);
    }
}