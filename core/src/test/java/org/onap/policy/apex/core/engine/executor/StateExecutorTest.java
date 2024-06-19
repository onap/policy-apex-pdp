/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation
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

package org.onap.policy.apex.core.engine.executor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;

/**
 * Test task executor.
 */
@ExtendWith(MockitoExtension.class)
class StateExecutorTest {
    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private AxState axStateMock;

    @Mock
    private ExecutorFactory executorFactoryMock;

    @Mock
    private Executor<EnEvent, StateOutput, AxState, ApexInternalContext> nextExecutorMock;

    /**
     * Set up mocking.
     */
    @BeforeEach
    void startMocking() {
        Mockito.doReturn(new AxReferenceKey("Policy:0.0.1:PolName:State0")).when(axStateMock).getKey();
    }

    @Test
    void testStateExecutor() {
        StateExecutor executor = new StateExecutor(executorFactoryMock);

        executor.setContext(null, axStateMock, internalContextMock);
        assertEquals("Policy:0.0.1:PolName:State0", executor.getKey().getId());
        assertNull(executor.getParent());
        assertEquals(internalContextMock, executor.getContext());
        assertNull(executor.getNext());
        assertNull(executor.getIncoming());
        assertNull(executor.getOutgoing());
        assertEquals(axStateMock, executor.getSubject());

        executor.setParameters(new ExecutorParameters());
        executor.setNext(nextExecutorMock);
        assertEquals(nextExecutorMock, executor.getNext());
        executor.setNext(null);
        assertNull(executor.getNext());

        assertThatThrownBy(() -> executor.executePre(0, null, null))
            .hasMessage("execution pre work not implemented on class");
        assertThatThrownBy(() -> executor.executePost(false))
            .hasMessage("execution post work not implemented on class");
    }
}
