/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexGrpcConsumerTest {
    private static final String CONSUMER_NAME = "TestApexGrpcConsumer";
    private ApexGrpcConsumer grpcConsumer = null;
    private ApexGrpcProducer grpcProducer = null;
    private EventHandlerParameters consumerParameters = null;
    private ApexEventReceiver incomingEventReceiver = null;

    /**
     * Set up testing.
     *
     * @throws ApexEventException on test set up errors.
     */
    @Before
    public void setUp() throws ApexEventException {
        grpcConsumer = new ApexGrpcConsumer();
        grpcProducer = new ApexGrpcProducer();
    }

    @Test
    public void testInit() {
        consumerParameters = populateConsumerParameters(true, true);
        Assertions.assertThatCode(() -> grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .doesNotThrowAnyException();
    }

    @Test
    public void testInit_invalidConsumerParams() {
        consumerParameters = populateConsumerParameters(false, true);
        assertThatThrownBy(() -> grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .hasMessageContaining("host details may not be specified for gRPC Consumer");
    }

    @Test
    public void testInit_invalidPeeredMode() {
        consumerParameters = populateConsumerParameters(true, false);
        assertThatThrownBy(() -> grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver))
            .hasMessageContaining(
                "gRPC consumer (" + CONSUMER_NAME + ") must run in peered requestor mode with a gRPC producer");
    }

    @Test
    public void testGetName() {
        assertEquals(null, new ApexGrpcConsumer().getName());
    }

    @Test
    public void testPeeredReference() throws ApexEventException {
        consumerParameters = populateConsumerParameters(true, true);
        grpcConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR,
            new PeeredReference(EventHandlerPeeredMode.REQUESTOR, grpcConsumer, grpcProducer));
        grpcConsumer.init(CONSUMER_NAME, consumerParameters, incomingEventReceiver);
        PeeredReference peeredReference = grpcConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR);
        assertNotNull(peeredReference);
        assertTrue(peeredReference.getPeeredConsumer().equals(grpcConsumer));
        assertTrue(peeredReference.getPeeredProducer().equals(grpcProducer));
    }

    private EventHandlerParameters populateConsumerParameters(boolean isConsumer, boolean isPeeredMode) {
        consumerParameters = new EventHandlerParameters();
        GrpcCarrierTechnologyParameters params = new GrpcCarrierTechnologyParameters();
        params.setLabel("GRPC");
        params.setEventProducerPluginClass(ApexGrpcProducer.class.getName());
        params.setEventConsumerPluginClass(ApexGrpcConsumer.class.getName());
        if (!isConsumer) {
            params.setHost("hostname");
            params.setPort(3214);
            params.setUsername("dummyUser");
            params.setPassword("dummyPassword");
            params.setTimeout(1);
        }
        consumerParameters.setCarrierTechnologyParameters(params);
        if (isPeeredMode) {
            consumerParameters.setPeeredMode(EventHandlerPeeredMode.REQUESTOR, true);
            consumerParameters.setPeer(EventHandlerPeeredMode.REQUESTOR, "requestorPeerName");
        }
        return consumerParameters;
    }
}
