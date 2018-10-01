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

package org.onap.policy.apex.client.monitoring.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.deployment.ApexDeploymentException;
import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;

/**
 * Test the monitoring rest resource.
 */
public class RestResourceTest {
    @Mock
    private EngineServiceFacade engineServiceFacadeMock;
    private ApexMonitoringRestResource restResource;

    /**
     * Set up mocking of the engine service facade.
     * 
     * @throws ApexException on engine service facade setup errors
     */
    @Before
    public void initializeMocking() throws ApexException {
        MockitoAnnotations.initMocks(this);

        final AxArtifactKey engineServiceKey = new AxArtifactKey("EngineServiceKey", "0.0.1");
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        final AxArtifactKey[] engineServiceKeyArray =
            { engineKey };
        final AxEngineModel engineModel = new AxEngineModel(engineServiceKeyArray[0]);

        restResource = Mockito.spy(new ApexMonitoringRestResource());
        Mockito.doReturn(engineServiceFacadeMock).when(restResource).getEngineServiceFacade("apexServer", 12345);

        Mockito.doReturn(engineServiceKey).when(engineServiceFacadeMock).getKey();
        Mockito.doReturn(engineServiceKeyArray).when(engineServiceFacadeMock).getEngineKeyArray();
        Mockito.doReturn(engineModel).when(engineServiceFacadeMock).getEngineStatus(engineKey);
    }

    @Test
    public void testRestResourceCreateSession() throws ApexException {
        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Engine0:0.0.1"));
    }

    @Test
    public void testRestResourceCreateSessionWithApexModelKey() throws ApexException {
        Mockito.doReturn(new AxArtifactKey("ModelKey:0.0.1")).when(engineServiceFacadeMock).getApexModelKey();

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Engine0:0.0.1"));
    }

    @Test
    public void testRestResourceCreateSessionConnectException() throws ApexException {
        Mockito.doThrow(new ApexDeploymentException("Connection Failed")).when(engineServiceFacadeMock).init();

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Error connecting to Apex Engine Service"));
    }

    @Test
    public void testRestResourceCreateSessionGetException() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doThrow(new ApexException("Exception on get")).when(engineServiceFacadeMock).getEngineStatus(engineKey);

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceCreateSessionInfo() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doReturn("{}").when(engineServiceFacadeMock).getEngineInfo(engineKey);

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceCreateSessionNullInfo() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doReturn(null).when(engineServiceFacadeMock).getEngineInfo(engineKey);

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceCreateSessionEmptyInfo() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doReturn(" ").when(engineServiceFacadeMock).getEngineInfo(engineKey);

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceCreateSessionExceptionInfo() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doThrow(new ApexException("Exception on info")).when(engineServiceFacadeMock).getEngineInfo(engineKey);

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceStartEngine() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Start");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceStopEngine() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Stop");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceNotStartStopEngine() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Hello");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceInitExceptionStartStopEngine() throws ApexException {
        Mockito.doThrow(new ApexDeploymentException("Exception on init")).when(engineServiceFacadeMock).init();

        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Hello");
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testRestResourceExceptionStartStopEngine() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doThrow(new ApexDeploymentException("Exception on Start/Stop")).when(engineServiceFacadeMock)
                        .startEngine(engineKey);

        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Start");
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testRestResourceStartPeriodicEvents() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.periodiceventStartStop("apexServer", 12345, engineKey.getId(), "Start", 1000);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceStopPeriodicEvents() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.periodiceventStartStop("apexServer", 12345, engineKey.getId(), "Stop", 1000);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceNotStartStopPeriodicEvents() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        Response response = restResource.periodiceventStartStop("apexServer", 12345, engineKey.getId(), "Hello", 1000);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceExceptionPeriodicEvents() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        Mockito.doThrow(new ApexDeploymentException("Exception on Periodic Events")).when(engineServiceFacadeMock)
                        .stopPerioidicEvents(engineKey);

        Response response = restResource.periodiceventStartStop("apexServer", 12345, engineKey.getId(), "Stop", 1000);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCounter() {
        ApexMonitoringRestResource.Counter counter = restResource.new Counter(1538338576, 1538338592);

        assertEquals(1538338576, counter.getTimestamp());
        assertEquals(1538338592, counter.getValue());
    }

    @Test
    public void testSlidingWindow() {
        ApexMonitoringRestResource.SlidingWindowList<String> slidingWindowList0 = restResource.new SlidingWindowList<>(
                        2);

        assertFalse(slidingWindowList0.hashCode() == 0);
        
        assertTrue(slidingWindowList0.add("Hello"));
        assertTrue(slidingWindowList0.add("Hi"));
        assertTrue(slidingWindowList0.add("Howdy"));
        
        assertFalse(slidingWindowList0.equals(null));
        assertTrue(slidingWindowList0.equals(slidingWindowList0));

        ApexMonitoringRestResource.SlidingWindowList<String> slidingWindowList1 = restResource.new SlidingWindowList<>(
                        2);
        ApexMonitoringRestResource.SlidingWindowList<String> slidingWindowList2 = restResource.new SlidingWindowList<>(
                        2);
        assertFalse(slidingWindowList0.equals(slidingWindowList1));
        assertFalse(slidingWindowList0.equals(slidingWindowList2));
        assertTrue(slidingWindowList1.equals(slidingWindowList2));
        ApexMonitoringRestResource.SlidingWindowList<String> slidingWindowList3 = restResource.new SlidingWindowList<>(
                        3);
        assertFalse(slidingWindowList1.equals(slidingWindowList3));
        ApexMonitoringRestResource.SlidingWindowList<Integer> slidingWindowList4 = restResource.new SlidingWindowList<>(
                        3);
        assertTrue(slidingWindowList3.add("Hello"));
        assertTrue(slidingWindowList4.add(10));
        assertFalse(slidingWindowList3.equals(slidingWindowList4));
    }

    @Test
    public void mopUp() {
        assertEquals(engineServiceFacadeMock, restResource.getEngineServiceFacade("apexServer", 12345));
    }
}
