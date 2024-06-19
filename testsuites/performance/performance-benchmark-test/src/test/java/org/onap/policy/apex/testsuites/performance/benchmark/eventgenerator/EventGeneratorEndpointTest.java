/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2023-2024 Nordix Foundation.
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

package org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.inject.Provider;
import jakarta.ws.rs.core.Response;
import org.glassfish.grizzly.http.server.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.OutputEvent;

/**
 * Test the EventGeneratorEndpoint class.
 */
@ExtendWith(MockitoExtension.class)
class EventGeneratorEndpointTest {
    @Mock
    private Provider<Request> httpRequestProviderMock;

    @Mock
    private Request httpRequestMock;

    /**
     * Set up mocking of the engine service facade.
     */
    @BeforeEach
    void initializeMocking() {

        Mockito.lenient().doReturn(httpRequestMock).when(httpRequestProviderMock).get();
        Mockito.lenient().doReturn("zooby").when(httpRequestMock).getRemoteHost();
        Mockito.lenient().doReturn(12345).when(httpRequestMock).getRemotePort();

    }

    @Test
    void testEventGeneratorEndpointGetStats() {
        EventGeneratorEndpoint.clearEventGenerationStats();
        EventGeneratorEndpoint.setFinished(false);

        EventGeneratorEndpoint egep = new EventGeneratorEndpoint(null);
        assertNotNull(egep);

        Response stats = egep.serviceGetStats();
        assertEquals(200, stats.getStatus());
        stats.close();
    }

    @Test
    void testEventGeneratorEndpointGetEventsZeroBatchCount() {
        EventGeneratorParameters incomingParameters = new EventGeneratorParameters();
        incomingParameters.setBatchCount(1);

        EventGeneratorEndpoint.setParameters(incomingParameters);
        EventGeneratorEndpoint.clearEventGenerationStats();
        EventGeneratorEndpoint.setFinished(false);

        EventGeneratorEndpoint egep = new EventGeneratorEndpoint(httpRequestProviderMock);
        assertNotNull(egep);

        Response events = egep.getEvents();
        assertEquals(200, events.getStatus());

        incomingParameters.setBatchCount(1);
        events = egep.getEvents();
        assertEquals(204, events.getStatus());
    }

    @Test
    void testEventGeneratorEndpointPostBadEvent() {
        EventGeneratorParameters incomingParameters = new EventGeneratorParameters();
        incomingParameters.setBatchCount(1);

        EventGeneratorEndpoint.setParameters(incomingParameters);
        EventGeneratorEndpoint.clearEventGenerationStats();
        EventGeneratorEndpoint.setFinished(false);

        EventGeneratorEndpoint egep = new EventGeneratorEndpoint(httpRequestProviderMock);
        assertNotNull(egep);

        OutputEvent oe = new OutputEvent();
        oe.setTestSlogan("99-99: Whatever");

        Response events = egep.postEventResponse(oe.asJson());
        assertEquals(409, events.getStatus());
        events.close();
    }
}
