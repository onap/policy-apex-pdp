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

package org.onap.policy.apex.service.engine.engdep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineInfo;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineServiceInfo;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineStatus;
import org.onap.policy.apex.core.protocols.engdep.messages.Response;
import org.onap.policy.apex.core.protocols.engdep.messages.StartEngine;
import org.onap.policy.apex.core.protocols.engdep.messages.StartPeriodicEvents;
import org.onap.policy.apex.core.protocols.engdep.messages.StopEngine;
import org.onap.policy.apex.core.protocols.engdep.messages.StopPeriodicEvents;
import org.onap.policy.apex.core.protocols.engdep.messages.UpdateModel;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the EngDep messaging Service.
 */
public class EngDepMessageListenerTest {
    @Mock
    private WebSocket webSocketMock;

    /**
     * Set up mocking of the engine service facade.
     * 
     * @throws ApexException on engine service facade setup errors
     */
    @Before
    public void initializeMocking() throws ApexException {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(new InetSocketAddress("HostAddress", 123)).when(webSocketMock).getRemoteSocketAddress();
        Mockito.doReturn(true).when(webSocketMock).isOpen();
    }

    @Test
    public void testMessageListener() throws ApexException {
        DummyEngineService dummyEngineService = new DummyEngineService();
        EngDepMessageListener listener = new EngDepMessageListener(dummyEngineService);
        listener.startProcessorThread();

        try {
            listener.onMessage("bad string message");
            fail("test should throw an exception");
        } catch (Exception uoe) {
            assertEquals("String messages are not supported on the EngDep protocol", uoe.getMessage());
        }

        List<Message> messageList = new ArrayList<>();
        messageList.add(new StartEngine(new AxArtifactKey("Start:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals("Start:0.0.1", dummyEngineService.getStartEngineKey().getId());

        messageList.clear();
        messageList.add(new StopEngine(new AxArtifactKey("Stop:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals("Stop:0.0.1", dummyEngineService.getStopEngineKey().getId());

        messageList.clear();
        messageList.add(new StartPeriodicEvents(new AxArtifactKey("StartPeriodic:0.0.1"), "12345"));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals(12345, dummyEngineService.getPeriodicPeriod());

        messageList.clear();
        messageList.add(new StopPeriodicEvents(new AxArtifactKey("StopPeriodic:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals(0, dummyEngineService.getPeriodicPeriod());

        messageList.clear();
        messageList.add(new GetEngineInfo(new AxArtifactKey("EngineInfo:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals("EngineInfo:0.0.1", dummyEngineService.getRuntimeInfoKey().getId());

        messageList.clear();
        messageList.add(new GetEngineStatus(new AxArtifactKey("EngineStatus:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals("EngineStatus:0.0.1", dummyEngineService.getStatusKey().getId());

        messageList.clear();
        messageList.add(new GetEngineServiceInfo(new AxArtifactKey("EngineServiceInfo:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals(1, dummyEngineService.getModelKeyGetCalled());

        messageList.clear();
        messageList.add(new UpdateModel(new AxArtifactKey("UpdateModel:0.0.1")));
        listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
        ThreadUtilities.sleep(50);
        assertEquals("UpdateModel:0.0.1", dummyEngineService.getUpdateModelKey().getId());

        try {
            messageList.clear();
            messageList.add(new Response(new AxArtifactKey("UpdateModel:0.0.1"), false,
                            new GetEngineInfo(new AxArtifactKey("EngineInfo:0.0.1"))));
            listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
            ThreadUtilities.sleep(50);
            assertEquals("UpdateModel:0.0.1", dummyEngineService.getUpdateModelKey().getId());

            messageList.clear();
            Message badMessage0 = new BadMessage(null, null);
            messageList.add(badMessage0);
            listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
            ThreadUtilities.sleep(50);

            messageList.clear();
            Message badMessage1 = new BadMessage(new BadAction(null), null);
            messageList.add(badMessage1);
            listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
            ThreadUtilities.sleep(50);

            messageList.clear();
            Message badMessage2 = new BadMessage(new BadAction("throw exception"), null);
            messageList.add(badMessage2);
            listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
            ThreadUtilities.sleep(50);

            Mockito.doReturn(false).when(webSocketMock).isOpen();
            messageList.add(new StartEngine(new AxArtifactKey("Start:0.0.1")));
            listener.onMessage(new MessageBlock<>(messageList, webSocketMock));
            ThreadUtilities.sleep(50);
        } catch (Exception e) {
            fail("test should not throw exceptions on bad messages");
        }
        listener.stopProcessorThreads();
        ThreadUtilities.sleep(50);
    }
}
