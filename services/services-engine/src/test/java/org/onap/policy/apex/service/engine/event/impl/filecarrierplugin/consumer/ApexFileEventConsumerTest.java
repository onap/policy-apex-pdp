/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2021. Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.FileCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.main.ApexEventUnmarshaller;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperTokenDelimitedEventProtocolParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextTokenDelimitedParameters;

public class ApexFileEventConsumerTest {
    private ApexFileEventConsumer consumer;
    private EventHandlerParameters handlerParameters;

    @Before
    public void setUp() throws Exception {
        consumer = new ApexFileEventConsumer();
        handlerParameters = new EventHandlerParameters();
    }

    @Test
    public void initNoConsumerParameters() {
        final String name = RandomStringUtils.randomAlphanumeric(4);
        assertThatThrownBy(() -> consumer.init(name, null, null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void initWrongCarrier() {
        final String name = RandomStringUtils.randomAlphanumeric(4);
        final CarrierTechnologyParameters technologyParameters = new SuperDooperCarrierTechnologyParameters();
        handlerParameters.setCarrierTechnologyParameters(technologyParameters);

        assertThatThrownBy(() -> consumer.init(name, handlerParameters, null))
            .isInstanceOf(ApexEventException.class);
    }

    @Test
    public void initWithoutReceiver() {
        final String name = RandomStringUtils.randomAlphanumeric(4);
        final EventHandlerParameters handlerParameters = new EventHandlerParameters();
        final FileCarrierTechnologyParameters technologyParameters = new FileCarrierTechnologyParameters();
        technologyParameters.setStandardIo(true);
        final EventProtocolTextTokenDelimitedParameters params = new SuperTokenDelimitedEventProtocolParameters();

        handlerParameters.setCarrierTechnologyParameters(technologyParameters);
        handlerParameters.setEventProtocolParameters(params);

        assertThatCode(() -> consumer.init(name, handlerParameters, null))
            .doesNotThrowAnyException();
    }

    @Test
    public void init() throws ApexEventException {
        final String name = RandomStringUtils.randomAlphanumeric(4);
        final EventHandlerParameters handlerParameters = new EventHandlerParameters();
        final FileCarrierTechnologyParameters technologyParameters = new FileCarrierTechnologyParameters();
        technologyParameters.setStandardIo(true);
        final EventProtocolTextTokenDelimitedParameters params = new SuperTokenDelimitedEventProtocolParameters();

        handlerParameters.setCarrierTechnologyParameters(technologyParameters);
        handlerParameters.setEventProtocolParameters(params);

        assertThatCode(() -> {
            final ApexEventReceiver incomingEventReceiver = new ApexEventUnmarshaller("name", null, null);
            consumer.init(name, handlerParameters, incomingEventReceiver);
        })
            .doesNotThrowAnyException();
    }

    @Test
    public void getName() throws ApexEventException {
        final String name = RandomStringUtils.randomAlphabetic(5);
        final EventHandlerParameters handlerParameters = new EventHandlerParameters();
        final FileCarrierTechnologyParameters technologyParameters = new FileCarrierTechnologyParameters();
        technologyParameters.setStandardIo(true);
        final EventProtocolTextTokenDelimitedParameters params = new SuperTokenDelimitedEventProtocolParameters();

        handlerParameters.setCarrierTechnologyParameters(technologyParameters);
        handlerParameters.setEventProtocolParameters(params);

        consumer.init(name, handlerParameters, null);
        assertThat(consumer.getName()).isEqualTo(name);
    }
}
