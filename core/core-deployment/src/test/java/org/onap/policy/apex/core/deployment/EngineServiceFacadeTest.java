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

package org.onap.policy.apex.core.deployment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the deployment web socket client.
 */
public class EngineServiceFacadeTest {
    @Test
    public void testEngineServiceFacade() throws Exception {
        EngineServiceFacade facade = new EngineServiceFacade("localhost", 51273);

        facade.setDeploymentClient(new DummyDeploymentClient("localhost", 51273));

        // First init should fail
        facade.init();

        assertNull(facade.getKey());
        assertNull(facade.getApexModelKey());
        assertNull(facade.getEngineKeyArray());

        try {
            facade.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("cound not deploy apex model, deployer is not initialized", ade.getMessage());
        }

        // Second init should work
        facade.init();

        assertEquals("EngineService:0.0.1", facade.getKey().getId());
        assertEquals("Model:0.0.1", facade.getApexModelKey().getId());
        assertEquals("Engine:0.0.1", facade.getEngineKeyArray()[0].getId());

        try {
            facade.deployModel("src/test/resources/models/NonExistantModel.json", false, false);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("cound not create apex model, could not read from file "
                            + "src/test/resources/models/NonExistantModel.json", ade.getMessage());
        }

        try {
            facade.deployModel("src/test/resources/models/JunkModel.json", false, false);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("could not deploy apex model from src/test/resources/models/JunkModel.json", ade.getMessage());
        }

        InputStream badStream = new ByteArrayInputStream("".getBytes());
        try {
            facade.deployModel("MyModel", badStream, false, false);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("format of input for Apex concept is neither JSON nor XML", ade.getMessage());
        }

        InputStream closedStream = new ByteArrayInputStream("".getBytes());
        closedStream.close();
        try {
            facade.deployModel("MyModel", closedStream, false, false);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("format of input for Apex concept is neither JSON nor XML", ade.getMessage());
        }

        try {
            facade.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("could not deploy apex model from src/test/resources/models/SamplePolicyModelJAVASCRIPT.json",
                            ade.getMessage());
        }

        try {
            facade.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json", false, false);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.startEngine(facade.getEngineKeyArray()[0]);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:51273", ade.getMessage());
        }

        try {
            facade.startEngine(facade.getEngineKeyArray()[0]);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.stopEngine(facade.getEngineKeyArray()[0]);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:51273", ade.getMessage());
        }

        try {
            facade.stopEngine(facade.getEngineKeyArray()[0]);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.startPerioidicEvents(facade.getEngineKeyArray()[0], 1000);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:51273", ade.getMessage());
        }

        try {
            facade.startPerioidicEvents(facade.getEngineKeyArray()[0], 1000);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.stopPerioidicEvents(facade.getEngineKeyArray()[0]);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:51273", ade.getMessage());
        }

        try {
            facade.stopPerioidicEvents(facade.getEngineKeyArray()[0]);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.getEngineStatus(facade.getEngineKeyArray()[0]);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:51273", ade.getMessage());
        }

        try {
            facade.getEngineStatus(facade.getEngineKeyArray()[0]);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.getEngineInfo(facade.getEngineKeyArray()[0]);
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("failed response Operation failed received from serverlocalhost:51273", ade.getMessage());
        }

        try {
            facade.getEngineInfo(facade.getEngineKeyArray()[0]);
        } catch (final Exception ade) {
            fail("test should not throw an exception here");
        }

        try {
            facade.getEngineStatus(new AxArtifactKey("ReturnBadMessage", "0.0.1"));
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("response received from server is of incorrect type "
                            + "org.onap.policy.apex.core.protocols.engdep.messages.GetEngineStatus, should be of type "
                            + "org.onap.policy.apex.core.protocols.engdep.messages.Response", ade.getMessage());
        }

        try {
            facade.getEngineStatus(new AxArtifactKey("ReturnBadResponse", "0.0.1"));
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("response received is not correct response to sent message GET_ENGINE_STATUS",
                            ade.getMessage());
        }

        try {
            facade.getEngineStatus(new AxArtifactKey("DoNotRespond", "0.0.1"));
            fail("test should throw an exception here");
        } catch (final Exception ade) {
            assertEquals("no response received to sent message GET_ENGINE_STATUS", ade.getMessage());
        }

        facade.close();
    }
}
