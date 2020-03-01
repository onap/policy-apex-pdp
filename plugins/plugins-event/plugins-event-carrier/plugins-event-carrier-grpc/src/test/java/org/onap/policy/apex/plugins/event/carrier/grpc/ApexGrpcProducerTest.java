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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.ccsdk.cds.controllerblueprints.processing.api.ExecutionServiceInput;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.cds.client.CdsProcessorGrpcClient;

@RunWith(MockitoJUnitRunner.class)
public class ApexGrpcProducerTest {
    @Mock
    private CdsProcessorGrpcClient grpcClient;
    private ApexGrpcProducer apexGrpcProducer;
    @Mock
    private EventHandlerParameters eventHandlerParameters;

    /**
     * Set up testing.
     *
     * @throws ApexEventException on test set up errors.
     */
    @Before
    public void setUp() throws ApexEventException {
        apexGrpcProducer = new ApexGrpcProducer();
        when(grpcClient.sendRequest(any(ExecutionServiceInput.class))).thenReturn(mock(CountDownLatch.class));
    }

    @Test(expected = ApexEventException.class)
    public void testInit_fail() throws ApexEventException {
        apexGrpcProducer.init("TestApexGrpcProducer", eventHandlerParameters);
    }

    @Test
    public void testInit_pass() {
        populateEventHandlerParameters();
        // should not throw an exception
        Assertions.assertThatCode(() -> apexGrpcProducer.init("TestApexGrpcProducer", eventHandlerParameters))
            .doesNotThrowAnyException();
    }

    @Test
    public void testStop() throws ApexEventException {
        populateEventHandlerParameters();
        apexGrpcProducer.init("TestApexGrpcProducer", eventHandlerParameters);
        // should not throw an exception
        Assertions.assertThatCode(() -> apexGrpcProducer.stop()).doesNotThrowAnyException();
    }

    @Test
    public void testSendEvent() throws ApexEventException {
        populateEventHandlerParameters();
        apexGrpcProducer.init("TestApexGrpcProducer", eventHandlerParameters);
        assertThatThrownBy(() -> {
            apexGrpcProducer.sendEvent(123, null, "grpcEvent", ExecutionServiceInput.newBuilder().build());
        }).hasMessage("Timed out");
    }

    private void populateEventHandlerParameters() {
        eventHandlerParameters = new EventHandlerParameters();
        GrpcCarrierTechnologyParameters params = new GrpcCarrierTechnologyParameters();
        params.setLabel("GRPC");
        params.setEventProducerPluginClass(ApexGrpcProducer.class.getName());
        params.setEventConsumerPluginClass(ApexGrpcConsumer.class.getName());
        params.setHost("10.20.30.40");
        params.setPort(3214);
        params.setUsername("dummyUser");
        params.setPassword("dummyPassword");
        params.setTimeout(2);
        eventHandlerParameters.setCarrierTechnologyParameters(params);
    }
}
