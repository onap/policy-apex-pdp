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

package org.onap.policy.apex.core.engine.executor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;

/**
 * Test task executor.
 */
public class StateExecutorTest {
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
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);
        
        Mockito.doReturn(new AxReferenceKey("Policy:0.0.1:PolName:State0")).when(axStateMock).getKey();
    }

    @Test
    public void testStateExecutor() {
        StateExecutor executor = new StateExecutor(executorFactoryMock);

        executor.setContext(null, axStateMock, internalContextMock);
        assertEquals("Policy:0.0.1:PolName:State0", executor.getKey().getId());
        assertEquals(null, executor.getParent());
        assertEquals(internalContextMock, executor.getContext());
        assertEquals(null, executor.getNext());
        assertEquals(null, executor.getIncoming());
        assertEquals(null, executor.getOutgoing());
        assertEquals(axStateMock, executor.getSubject());

        executor.setParameters(new ExecutorParameters());
        executor.setNext(nextExecutorMock);
        assertEquals(nextExecutorMock, executor.getNext());
        executor.setNext(null);
        assertEquals(null, executor.getNext());

        try {
            executor.executePre(0, null);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execution pre work not implemented on class", ex.getMessage());
        }

        try {
            executor.executePost(false);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execution post work not implemented on class", ex.getMessage());
        }
    }
}
