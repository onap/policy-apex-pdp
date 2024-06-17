/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020, 2024 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

class ApexGrpcConsumerTest {
    private static final String CONSUMER_NAME = "TestApexGrpcConsumer";
    private ApexGrpcConsumer grpcConsumer = null;
    private ApexGrpcProducer grpcProducer = null;
    private EventHandlerParameters consumerParameters = null;
    private final ApexEventReceiver incomingEventReceiver = null;

    /**
     * Set up testing.
     */
    @BeforeEach
    void setUp() {
        grpcConsumer = new ApexGrpcConsumer();
        grpcProducer = new ApexGrpcProducer();
    }

    @Test
    void testInit() {
        consumerParameters = populateConsumerParameters(true);
        Assertions.assertThatCode(() -> grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .doesNotThrowAnyException();
    }

    @Test
    void testInit_invalidPeeredMode() {
        consumerParameters = populateConsumerParameters(false);
        assertThatThrownBy(() -> grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .hasMessageContaining(
                "gRPC consumer (" + CONSUMER_NAME + ") must run in peered requestor mode with a gRPC producer");
    }

    @Test
    void testGetName() {
        assertNull(new ApexGrpcConsumer().getName());
    }

    @Test
    void testPeeredReference() throws ApexEventException {
        consumerParameters = populateConsumerParameters(true);
        grpcConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, grpcConsumer, grpcProducer));
        grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);
        PeeredReference peeredReference = grpcConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR);
        assertNotNull(peeredReference);
        assertEquals(grpcConsumer, peeredReference.getPeeredConsumer());
        assertEquals(grpcProducer, peeredReference.getPeeredProducer());
    }

    private EventHandlerParameters populateConsumerParameters(boolean isPeeredMode) {
        consumerParameters = new EventHandlerParameters();
        GrpcCarrierTechnologyParameters params =
            getGrpcCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(params);
        if (isPeeredMode) {
            consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
            consumerParameters.setPeer(EventHandlerPeeredMode.REQUESTOR, "requestorPeerName");
        }
        return consumerParameters;
    }

    private static @NotNull GrpcCarrierTechnologyParameters getGrpcCarrierTechnologyParameters() {
        GrpcCarrierTechnologyParameters params = new GrpcCarrierTechnologyParameters();
        params.setLabel("GRPC");
        params.setEventProducerPluginClass(ApexGrpcProducer.class.getName());
        params.setEventConsumerPluginClass(ApexGrpcConsumer.class.getName());
        return params;
    }
}
