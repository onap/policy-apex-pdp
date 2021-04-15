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
import org.onap.policy.apex.service.engine.event.ApexEventConsumer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.RestPluginCarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;

public class EventConsumerFactoryTest {
    private EventConsumerFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new EventConsumerFactory();
    }

    @Test
    public void createConsumerNoTechnologyParameter() {
        final String name = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters parameters = new EventHandlerParameters();

        assertThatThrownBy(() -> factory.createConsumer(name, parameters))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void createConsumerNoConsumerPlugin() {
        final String name = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters parameters = new EventHandlerParameters();
        parameters.setCarrierTechnologyParameters(new RestPluginCarrierTechnologyParameters());

        assertThatThrownBy(() -> factory.createConsumer(name, parameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void createConsumerWrongPluginClassName() {
        final String name = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters parameters = new EventHandlerParameters();
        final RestPluginCarrierTechnologyParameters technologyParameters =
            new RestPluginCarrierTechnologyParameters();
        technologyParameters.setEventConsumerPluginClass("java.lang.Object");
        parameters.setCarrierTechnologyParameters(technologyParameters);

        assertThatThrownBy(() -> factory.createConsumer(name, parameters))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void createConsumer() throws ApexEventException {
        final String name = RandomStringUtils.randomAlphabetic(6);
        final EventHandlerParameters parameters = new EventHandlerParameters();
        parameters.setCarrierTechnologyParameters(new SuperDooperCarrierTechnologyParameters());

        final ApexEventConsumer actual = factory.createConsumer(name, parameters);
        assertNotNull(actual);
    }
}