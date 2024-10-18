/*
 *  ============LICENSE_START=======================================================
 *  Copyright (C) 2024 Nordix Foundation.
 *  ================================================================================
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

package org.onap.policy.apex.service.engine.main;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.runtime.EngineService;
import org.onap.policy.apex.service.engine.runtime.EngineServiceEventInterface;

@ExtendWith(MockitoExtension.class)
class ApexEngineServiceHandlerTest {

    @Mock
    private EngineService engineService;

    @Mock
    private ApexEvent apexEvent;

    @Mock
    private EngineServiceEventInterface interfaceMock;

    @InjectMocks
    private ApexEngineServiceHandler apexEngineServiceHandler;

    @BeforeEach
    public void setup() {
        Mockito.lenient().when(engineService.getEngineServiceEventInterface()).thenReturn(interfaceMock);
    }

    @Test
    void testForwardEvent_Success() {
        Mockito.lenient().when(apexEvent.getName()).thenReturn("TestEvent");

        apexEngineServiceHandler.forwardEvent(apexEvent);

        verify(engineService.getEngineServiceEventInterface(), times(1)).sendEvent(apexEvent);
    }

    @Test
    void testForwardEvent_ExceptionThrown() {
        Mockito.lenient().when(apexEvent.getName()).thenReturn("TestEvent");

        when(engineService.getEngineServiceEventInterface()).thenThrow(new RuntimeException("Simulated Exception"));

        ApexActivatorRuntimeException thrown = assertThrows(ApexActivatorRuntimeException.class, () -> {
            apexEngineServiceHandler.forwardEvent(apexEvent);
        });

        assertTrue(thrown.getMessage().contains("error transferring event"));
        assertTrue(thrown.getCause().getMessage().contains("Simulated Exception"));
    }

    @Test
    void testTerminate_Success() throws ApexException {
        assertThatCode(apexEngineServiceHandler::terminate)
            .doesNotThrowAnyException();

        verify(engineService, times(1)).stop();
        verify(engineService, times(1)).clear();
    }

    @Test
    void testTerminate_NullService() {
        ApexEngineServiceHandler handlerWithNullService = new ApexEngineServiceHandler(null);
        assertDoesNotThrow(() -> handlerWithNullService.terminate());
    }
}
