/*-
 * ============LICENSE_START=======================================================
 *  Copyright (c) 2024 Nordix Foundation.
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

package org.onap.policy.apex.service.engine.runtime.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.event.ApexEvent;
import org.onap.policy.apex.service.engine.event.impl.enevent.ApexEvent2EnEventConverter;
import org.onap.policy.apex.service.engine.runtime.ApexEventListener;

class EnEventListenerImplTest {

    @Mock
    private ApexEventListener mockApexEventListener;

    @Mock
    private ApexEvent2EnEventConverter mockApexEnEventConverter;

    @Mock
    private EnEvent mockEnEvent;

    @Mock
    private ApexEvent mockApexEvent;

    @InjectMocks
    private EnEventListenerImpl enEventListenerImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOnEnEvent_success() throws ApexException {
        String eventName = "testEvent";
        when(mockEnEvent.getName()).thenReturn(eventName);
        List<ApexEvent> apexEvents = Arrays.asList(mockApexEvent);
        when(mockApexEnEventConverter.toApexEvent(eventName, mockEnEvent)).thenReturn(apexEvents);

        assertDoesNotThrow(() -> enEventListenerImpl.onEnEvent(mockEnEvent));

        verify(mockApexEventListener, times(1)).onApexEvent(mockApexEvent);
    }

    @Test
    void testOnEnEvent_apexExceptionThrown() throws ApexException {
        String eventName = "testEvent";
        when(mockEnEvent.getName()).thenReturn(eventName);
        when(mockApexEnEventConverter.toApexEvent(eventName, mockEnEvent))
            .thenThrow(new ApexException("Conversion error"));

        assertThrows(ApexException.class, () -> enEventListenerImpl.onEnEvent(mockEnEvent));

        verify(mockApexEventListener, never()).onApexEvent(any(ApexEvent.class));
    }

    @Test
    void testOnEnEvent_multipleApexEvents() throws ApexException {
        String eventName = "testEvent";
        when(mockEnEvent.getName()).thenReturn(eventName);
        ApexEvent apexEvent1 = mock(ApexEvent.class);
        ApexEvent apexEvent2 = mock(ApexEvent.class);
        List<ApexEvent> apexEvents = Arrays.asList(apexEvent1, apexEvent2);
        when(mockApexEnEventConverter.toApexEvent(eventName, mockEnEvent)).thenReturn(apexEvents);

        assertDoesNotThrow(() -> enEventListenerImpl.onEnEvent(mockEnEvent));

        verify(mockApexEventListener, times(1)).onApexEvent(apexEvent1);
        verify(mockApexEventListener, times(1)).onApexEvent(apexEvent2);
    }
}
