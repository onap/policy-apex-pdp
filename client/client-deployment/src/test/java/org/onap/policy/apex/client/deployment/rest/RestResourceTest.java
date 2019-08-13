/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.client.deployment.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
 * Test the Deployment rest resource.
 */
public class RestResourceTest {
    @Mock
    private EngineServiceFacade engineServiceFacadeMock;
    private ApexDeploymentRestResource restResource;

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
        final AxArtifactKey[] engineServiceKeyArray = {engineKey};
        final AxEngineModel engineModel = new AxEngineModel(engineServiceKeyArray[0]);

        restResource = Mockito.spy(new ApexDeploymentRestResource());
        Mockito.doReturn(engineServiceFacadeMock).when(restResource).getEngineServiceFacade("apexServer", 12345);

        Mockito.doReturn(engineServiceKey).when(engineServiceFacadeMock).getKey();
        Mockito.doReturn(engineServiceKeyArray).when(engineServiceFacadeMock).getEngineKeyArray();
        Mockito.doReturn(engineModel).when(engineServiceFacadeMock).getEngineStatus(engineKey);
    }

    @Test
    public void testRestResourceCreateSession() throws ApexException {
        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testRestResourceCreateSessionWithApexModelKey() throws ApexException {
        Mockito.doReturn(new AxArtifactKey("ModelKey:0.0.1")).when(engineServiceFacadeMock).getApexModelKey();

        Response response = restResource.createSession("apexServer", 12345);
        assertEquals(200, response.getStatus());
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
    public void testRestResourcemodelUpload() throws ApexException {
        InputStream uploadedInputStream =
                new ByteArrayInputStream("src/test/resources/models/SmallModel.json".getBytes());

        Response response = restResource.modelUpload("apexServer", 12345,
                uploadedInputStream, "SmallModel.json", false, false);
        assertEquals(200, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("SmallModel.json"));
    }

    @Test
    public void testRestResourcemodelUploadNoConnection() throws ApexException {
        Mockito.doThrow(new ApexDeploymentException("Connection Failed")).when(engineServiceFacadeMock).init();

        InputStream uploadedInputStream =
                new ByteArrayInputStream("src/test/resources/models/SmallModel.json".getBytes());

        Response response =
                restResource.modelUpload("apexServer", 12345, uploadedInputStream, "SmallModel.json", false, false);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testRestResourcemodelUploadDeploy() throws ApexException {

        InputStream uploadedInputStream =
                new ByteArrayInputStream("src/test/resources/models/SmallModel.json".getBytes());

        Mockito.doThrow(new ApexDeploymentException("Connection Failed")).when(engineServiceFacadeMock)
                .deployModel("SmallModel.json", uploadedInputStream, false, true);


        Response response =
                restResource.modelUpload("apexServer", 12345, uploadedInputStream, "SmallModel.json", false, true);
        assertEquals(500, response.getStatus());
        assertTrue(((String) response.getEntity()).contains("Error updating model on engine service"));
    }
}
