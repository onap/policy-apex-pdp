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

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexGrpcConsumerTest {
    ApexGrpcConsumer grpcConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;

    private static final String GRPC_CONSUMER_ERROR_MSG =
        "A gRPC Consumer may not be specified. Only sending events is possible using gRPC";

    /**
     * Set up testing.
     *
     * @throws ApexEventException on test set up errors.
     */
    @Before
    public void setUp() throws ApexEventException {
        grpcConsumer = new ApexGrpcConsumer();
        consumerParameters = new EventHandlerParameters();
        consumerParameters.setCarrierTechnologyParameters(new GrpcCarrierTechnologyParameters() {});
    }

    @Test
    public void testInit() {
        assertThatThrownBy(() -> {
            grpcConsumer.init("TestApexGrpcConsumer", consumerParameters, incomingEventReceiver);
        }).hasMessage(GRPC_CONSUMER_ERROR_MSG);
    }

    @Test
    public void testStart() {
        assertThatThrownBy(() -> {
            grpcConsumer.start();
        }).hasMessage(GRPC_CONSUMER_ERROR_MSG);
    }

    @Test
    public void testGetName() {
        assertEquals(null, new ApexGrpcConsumer().getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertThatThrownBy(() -> {
            grpcConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR);
        }).hasMessage(GRPC_CONSUMER_ERROR_MSG);
    }

    @Test
    public void testSetPeeredReference() {
        assertThatThrownBy(() -> {
            grpcConsumer.setPeeredReference(null, null);
        }).hasMessage(GRPC_CONSUMER_ERROR_MSG);
    }

    @Test()
    public void testStop() {
        assertThatThrownBy(() -> {
            new ApexGrpcConsumer().stop();
        }).hasMessage(GRPC_CONSUMER_ERROR_MSG);
    }

}
