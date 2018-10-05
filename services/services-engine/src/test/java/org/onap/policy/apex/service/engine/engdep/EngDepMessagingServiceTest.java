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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingService;
import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;

/**
 * Test the EngDep messaging Service.
 */
public class EngDepMessagingServiceTest {
    @Mock
    private MessagingService<Message> messageServiceMock;
    private EngDepMessagingService edMessagingService;

    /**
     * Set up mocking of the engine service facade.
     * 
     * @throws ApexException on engine service facade setup errors
     */
    @Before
    public void initializeMocking() throws ApexException {
        MockitoAnnotations.initMocks(this);

        edMessagingService = Mockito.spy(new EngDepMessagingService(new DummyEngineService(), 12345));
        Mockito.doReturn(messageServiceMock).when(edMessagingService).getMessageService(12345);
    }

    @Test
    public void testStartStop() throws ApexException {
        edMessagingService.start();
        assertTrue(edMessagingService.isStarted());
        assertFalse(edMessagingService.isStopped());
        edMessagingService.stop();
        assertTrue(edMessagingService.isStopped());
        assertFalse(edMessagingService.isStarted());
    }
}
