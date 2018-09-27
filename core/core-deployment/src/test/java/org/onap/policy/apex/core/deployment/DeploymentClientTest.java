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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.policy.apex.core.infrastructure.messaging.MessageHolder;
import org.onap.policy.apex.core.infrastructure.messaging.MessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingServiceFactory;
import org.onap.policy.apex.core.infrastructure.messaging.impl.ws.messageblock.MessageBlock;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.core.protocols.engdep.messages.GetEngineStatus;
import org.onap.policy.apex.core.protocols.engdep.messages.Response;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the deployment web socket client.
 */
@RunWith(MockitoJUnitRunner.class)
public class DeploymentClientTest {
    @Mock
    private static MessagingServiceFactory<Message> mockServiceFactory;

    @Mock
    private static MessagingService<Message> mockService;

    @SuppressWarnings("rawtypes")
    ArgumentCaptor<MessageListener> messageListener = ArgumentCaptor.forClass(MessageListener.class);

    @SuppressWarnings("unchecked")
    @Test
    public void testDeploymentClientStart() throws Exception {
        DeploymentClient deploymentClient = new DeploymentClient("localhost", 51273);

        final Field factoryField = deploymentClient.getClass().getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(deploymentClient, mockServiceFactory);

        Mockito.doReturn(mockService).when(mockServiceFactory).createClient(anyObject());

        Mockito.doNothing().when(mockService).addMessageListener(messageListener.capture());
        Mockito.doNothing().when(mockService).startConnection();
        
        Mockito.doNothing().when(mockService).send((MessageHolder<Message>) anyObject());
        
        Thread clientThread = new Thread(deploymentClient);
        clientThread.start();

        ThreadUtilities.sleep(20);

        assertTrue(deploymentClient.isStarted());
        assertTrue(clientThread.isAlive());
        
        AxArtifactKey engineKey = new AxArtifactKey("MyEngine", "0.0.1");
        GetEngineStatus getEngineStatus = new GetEngineStatus(engineKey);
        deploymentClient.sendMessage(new GetEngineStatus(engineKey));

        ThreadUtilities.sleep(20);
        Response response = new Response(engineKey, true, getEngineStatus);
        List<Message> messageList = new ArrayList<>();
        messageList.add(response);
        
        MessageBlock<Message> responseBlock = new MessageBlock<>(messageList, null);
        messageListener.getValue().onMessage(responseBlock);
        
        try {
            messageListener.getValue().onMessage("StringMessage");
            fail("test should throw an exception here");
        } catch (UnsupportedOperationException use) {
            assertEquals("String mesages are not supported on the EngDep protocol", use.getMessage());
        }

        ThreadUtilities.sleep(300);
        assertEquals(1, deploymentClient.getMessagesSent());
        assertEquals(2, deploymentClient.getMessagesReceived());
        
        deploymentClient.stopClient();
    }

    @Test
    public void testDeploymentClientStartException() throws Exception {
        DeploymentClient depoymentClient = new DeploymentClient("localhost", 51273);

        final Field factoryField = depoymentClient.getClass().getDeclaredField("factory");
        factoryField.setAccessible(true);
        factoryField.set(depoymentClient, mockServiceFactory);

        Mockito.doReturn(mockService).when(mockServiceFactory).createClient(anyObject());

        Mockito.doNothing().when(mockService).addMessageListener(anyObject());
        Mockito.doThrow(new ApexRuntimeException("connection start failed")).when(mockService).startConnection();

        Thread clientThread = new Thread(depoymentClient);
        clientThread.start();

        ThreadUtilities.sleep(50);

        assertFalse(depoymentClient.isStarted());
        assertFalse(clientThread.isAlive());
        assertEquals(0, depoymentClient.getReceiveQueue().size());

        ThreadUtilities.sleep(100);
    }
}
