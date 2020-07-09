/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import org.awaitility.Awaitility;
import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the deployment web socket client.
 */
public class EngineServiceFacadeTest {
    @Test
    public void testEngineServiceFacade() throws Exception {
        EngineServiceFacade facade = new EngineServiceFacade("localhost", 51273);

        final DummyDeploymentClient dummyDeploymentClient = new DummyDeploymentClient("aHost", 54553);
        facade.setDeploymentClient(dummyDeploymentClient);

        // First init should fail due to our dummy client
        dummyDeploymentClient.setInitSuccessful(false);
        assertThatThrownBy(facade::init)
            .hasMessage("could not handshake with server localhost:51273");
        assertNull(facade.getKey());
        assertNull(facade.getApexModelKey());
        assertNull(facade.getEngineKeyArray());

        assertThatThrownBy(() -> facade.deployModel("src/test/resources/models/SamplePolicyModelJAVASCRIPT.json",
                false, false))
            .hasMessage("could not deploy apex model, deployer is not initialized");

        // Second init should work
        Awaitility.await().atLeast(Duration.ofMillis(1000));
        dummyDeploymentClient.setInitSuccessful(true);
        facade.init();

        assertEquals("EngineService:0.0.1", facade.getKey().getId());
        assertEquals("Model:0.0.1", facade.getApexModelKey().getId());
        assertEquals("Engine:0.0.1", facade.getEngineKeyArray()[0].getId());

        assertThatThrownBy(() -> facade.deployModel("src/test/resources/models/NonExistantModel.json",
                false, false))
            .hasMessage("could not create apex model, could not read from file "
                            + "src/test/resources/models/NonExistantModel.json");
        assertThatThrownBy(() -> facade.deployModel("src/test/resources/models/JunkModel.json",
                false, false))
            .hasMessage("could not deploy apex model from src/test/resources/models/JunkModel.json");

        InputStream badStream = new ByteArrayInputStream("".getBytes());
        assertThatThrownBy(() -> facade.deployModel("MyModel", badStream, false, false))
            .hasMessage("format of input for Apex concept is neither JSON nor XML");
        InputStream closedStream = new ByteArrayInputStream("".getBytes());
        closedStream.close();

        assertThatThrownBy(() -> facade.deployModel("MyModel", closedStream, false, false))
            .hasMessage("format of input for Apex concept is neither JSON nor XML");
        assertThatThrownBy(() -> facade.deployModel("src/test/resources/models/SmallModel.json", false, false))
            .hasMessage("could not deploy apex model from src/test/resources/models/SmallModel.json");
        facade.deployModel("src/test/resources/models/SmallModel.json", false, false);

        assertThatThrownBy(() -> facade.startEngine(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");
        facade.startEngine(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.stopEngine(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");
        facade.stopEngine(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.startPerioidicEvents(facade.getEngineKeyArray()[0], 1000))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");
        facade.startPerioidicEvents(facade.getEngineKeyArray()[0], 1000);

        assertThatThrownBy(() -> facade.stopPerioidicEvents(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");
        facade.stopPerioidicEvents(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.getEngineStatus(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");
        facade.getEngineStatus(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.getEngineInfo(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");
        facade.getEngineInfo(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.getEngineStatus(new AxArtifactKey("ReturnBadMessage", "0.0.1")))
            .hasMessage("response received from server is of incorrect type "
                + "org.onap.policy.apex.core.protocols.engdep.messages.GetEngineStatus, should be of type "
                + "org.onap.policy.apex.core.protocols.engdep.messages.Response");
        assertThatThrownBy(() -> facade.getEngineStatus(new AxArtifactKey("ReturnBadResponse", "0.0.1")))
            .hasMessage("response received is not correct response to sent message GET_ENGINE_STATUS");
        assertThatThrownBy(() -> facade.getEngineStatus(new AxArtifactKey("DoNotRespond", "0.0.1")))
            .hasMessage("no response received to sent message GET_ENGINE_STATUS");
        assertThatThrownBy(() -> facade.stopPerioidicEvents(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");

        facade.stopPerioidicEvents(facade.getEngineKeyArray()[0]);

        facade.getEngineStatus(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.getEngineInfo(facade.getEngineKeyArray()[0]))
            .hasMessage("failed response Operation failed received from serverlocalhost:51273");

        facade.getEngineInfo(facade.getEngineKeyArray()[0]);

        assertThatThrownBy(() -> facade.getEngineStatus(new AxArtifactKey("ReturnBadMessage", "0.0.1")))
            .hasMessage("response received from server is of incorrect type "
                            + "org.onap.policy.apex.core.protocols.engdep.messages.GetEngineStatus, should be of type "
                            + "org.onap.policy.apex.core.protocols.engdep.messages.Response");
        assertThatThrownBy(() -> facade.getEngineStatus(new AxArtifactKey("ReturnBadResponse", "0.0.1")))
            .hasMessage("response received is not correct response to sent message GET_ENGINE_STATUS");
        assertThatThrownBy(() -> facade.getEngineStatus(new AxArtifactKey("DoNotRespond", "0.0.1")))
            .hasMessage("no response received to sent message GET_ENGINE_STATUS");
        facade.close();
    }
}
