/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Provider;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.Request;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.events.OutputEvent;

/**
 * Test the EventGeneratorEndpoint class.
 *
 */
public class EventGeneratorEndpointTest {
    @Mock
    private Provider<Request> httpRequestProviderMock;

    @Mock
    private Request httpRequestMock;

    /**
     * Set up mocking of the engine service facade.
     * 
     * @throws ApexException on engine service facade setup errors
     */
    @Before
    public void initializeMocking() throws ApexException {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(httpRequestMock).when(httpRequestProviderMock).get();

        Mockito.doReturn("zooby").when(httpRequestMock).getRemoteHost();
        Mockito.doReturn(12345).when(httpRequestMock).getRemotePort();

    }
    
    @Test
    public void testEventGeneratorEndpointGetStats() {
        EventGeneratorEndpoint.clearEventGenerationStats();
        EventGeneratorEndpoint.setFinished(false);

        EventGeneratorEndpoint egep = new EventGeneratorEndpoint(null);
        assertNotNull(egep);
        
        Response stats = egep.serviceGetStats();
        assertEquals(200, stats.getStatus());
    }

    @Test
    public void testEventGeneratorEndpointGetEventsZeroBatchCount() {
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
    public void testEventGeneratorEndpointPostBadEvent() {
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
    }
}
