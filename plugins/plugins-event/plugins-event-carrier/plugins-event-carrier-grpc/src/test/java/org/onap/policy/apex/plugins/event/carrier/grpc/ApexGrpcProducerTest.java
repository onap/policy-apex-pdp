/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Bell Canada. All rights reserved.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.grpc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.cds.client.CdsProcessorGrpcClient;

@ExtendWith(MockitoExtension.class)
class ApexGrpcProducerTest {
    private static final String PRODUCER_NAME = "TestApexGrpcProducer";
    private static final String HOST = "localhost";
    @Mock
    private CdsProcessorGrpcClient grpcClient;
    private final ApexGrpcProducer apexGrpcProducer = spy(new ApexGrpcProducer());
    @Mock
    private EventHandlerParameters eventHandlerParameters;

    /**
     * Set up testing.
     */
    @BeforeEach
    void setUp() {
        populateEventHandlerParameters();
    }

    @Test
    void testInit_fail() {
        assertThrows(ApexEventException.class,
            () -> apexGrpcProducer.init(PRODUCER_NAME, new EventHandlerParameters()));
    }

    @Test
    void testInit_pass() {
        // should not throw an exception
        Assertions.assertThatCode(() -> apexGrpcProducer.init(PRODUCER_NAME, eventHandlerParameters))
            .doesNotThrowAnyException();
    }

    @Test
    void testStop() throws ApexEventException {
        apexGrpcProducer.init(PRODUCER_NAME, eventHandlerParameters);
        // should not throw an exception
        Assertions.assertThatCode(apexGrpcProducer::stop).doesNotThrowAnyException();
    }

    @Test
    void testSendEvent() throws ApexEventException {
        apexGrpcProducer.init(PRODUCER_NAME, eventHandlerParameters);
        Assertions
            .assertThatCode(() -> apexGrpcProducer.sendEvent(123, null, "grpcEvent",
                Files.readString(Paths.get("src/test/resources/executionServiceInputEvent.json"))))
            .doesNotThrowAnyException();
    }

    private void populateEventHandlerParameters() {
        eventHandlerParameters = new EventHandlerParameters();
        GrpcCarrierTechnologyParameters params = new GrpcCarrierTechnologyParameters();
        params.setLabel("GRPC");
        params.setEventProducerPluginClass(ApexGrpcProducer.class.getName());
        params.setEventConsumerPluginClass(ApexGrpcConsumer.class.getName());
        params.setHost(ApexGrpcProducerTest.HOST);
        params.setPort(3214);
        params.setUsername("dummyUser");
        params.setPassword("dummyPassword");
        params.setTimeout(5);
        eventHandlerParameters.setCarrierTechnologyParameters(params);
    }
}
