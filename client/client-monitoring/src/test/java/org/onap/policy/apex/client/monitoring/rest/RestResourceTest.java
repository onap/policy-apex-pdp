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
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.deployment.EngineServiceFacade;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.enginemodel.concepts.AxEngineModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Test the monitoring rest resource.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApexMonitoringRestResource.class)
public class RestResourceTest {
    @Mock
    EngineServiceFacade engineServiceFacadeMock;

    /**
     * Set up the mocking for this test.
     * 
     * @throws Exception on mock setup failures
     */
    @Before
    public void setupFacade() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.whenNew(EngineServiceFacade.class).withAnyArguments().thenReturn(engineServiceFacadeMock);
    }

    @Test
    public void testRestResourceCreateSession() throws ApexException {
        final AxArtifactKey engineServiceKey = new AxArtifactKey("EngineServiceKey", "0.0.1");
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");
        final AxArtifactKey[] engineServiceKeyArray =
            { engineKey };
        final AxEngineModel engineModel = new AxEngineModel(engineServiceKeyArray[0]);

        Mockito.when(engineServiceFacadeMock.getKey()).thenReturn(engineServiceKey);
        Mockito.when(engineServiceFacadeMock.getEngineKeyArray()).thenReturn(engineServiceKeyArray);
        Mockito.when(engineServiceFacadeMock.getEngineStatus(engineKey)).thenReturn(engineModel);

        ApexMonitoringRestResource restResource = new ApexMonitoringRestResource();
        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
        assertTrue(((String) response.getEntity()).contains(engineKey.getId()));
    }

    @Test
    public void testRestResourceStartEngine() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        ApexMonitoringRestResource restResource = new ApexMonitoringRestResource();
        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Start");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceStopEngine() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        ApexMonitoringRestResource restResource = new ApexMonitoringRestResource();
        Response response = restResource.startStop("apexServer", 12345, engineKey.getId(), "Stop");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceStartPeriodicEvents() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        ApexMonitoringRestResource restResource = new ApexMonitoringRestResource();
        Response response = restResource.periodiceventStartStop("apexServer", 12345, engineKey.getId(), "Start", 1000);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceStopEPeriodicEvents() throws ApexException {
        final AxArtifactKey engineKey = new AxArtifactKey("Engine0", "0.0.1");

        ApexMonitoringRestResource restResource = new ApexMonitoringRestResource();
        Response response = restResource.periodiceventStartStop("apexServer", 12345, engineKey.getId(), "Stop", 1000);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testCounter() {
        ApexMonitoringRestResource restResource = new ApexMonitoringRestResource();

        ApexMonitoringRestResource.Counter counter = restResource.new Counter(1538338576, 1538338592);

        assertEquals(1538338576, counter.getTimestamp());
        assertEquals(1538338592, counter.getValue());
    }
}
