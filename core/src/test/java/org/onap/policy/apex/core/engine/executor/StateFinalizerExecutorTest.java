/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023-2024 Nordix Foundation.
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

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;

/**
 * Test task executor.
 */
@ExtendWith(MockitoExtension.class)
class StateFinalizerExecutorTest {
    @Mock
    private Executor<?, ?, ?, ?> parentMock;

    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private AxStateFinalizerLogic stateFinalizerLogicMock;

    @Mock
    private Executor<Map<String, Object>, String, AxStateFinalizerLogic, ApexInternalContext> nextExecutorMock;

    @Mock
    private EnEvent incomingEvent;

    /**
     * Set up mocking.
     */
    @BeforeEach
    void startMocking() {

        AxState state = new AxState();
        state.getStateOutputs().put("ValidOutput", null);

        Mockito.doReturn(state).when(parentMock).getSubject();

        Mockito.doReturn(new AxReferenceKey("State:0.0.1:StateName:StateSFL")).when(stateFinalizerLogicMock).getKey();
    }

    @Test
    void testStateFinalizerExecutor() throws StateMachineException, ContextException {
        DummyStateFinalizerExecutor executor = new DummyStateFinalizerExecutor();

        executor.setContext(parentMock, stateFinalizerLogicMock, internalContextMock);
        assertEquals("State:0.0.1:StateName:StateSFL", executor.getKey().getId());
        assertNull(executor.getExecutionContext());
        assertEquals(parentMock, executor.getParent());
        assertEquals(internalContextMock, executor.getContext());
        assertNull(executor.getNext());
        assertNull(executor.getIncoming());
        assertNull(executor.getOutgoing());
        assertEquals(stateFinalizerLogicMock, executor.getSubject());

        executor.setParameters(new ExecutorParameters());
        executor.setNext(nextExecutorMock);
        assertEquals(nextExecutorMock, executor.getNext());
        executor.setNext(null);
        assertNull(executor.getNext());

        assertThatThrownBy(executor::cleanUp)
            .hasMessage("cleanUp() not implemented on class");
        Mockito.doReturn(null).when(stateFinalizerLogicMock).getLogic();

        assertThatThrownBy(executor::prepare)
            .hasMessage("state finalizer logic cannot be null.");
        Mockito.doReturn("some task logic").when(stateFinalizerLogicMock).getLogic();

        executor.prepare();

        executor.executePre(0, new Properties(), incomingEvent);
        assertThatThrownBy(() -> executor.executePre(0, null, incomingEvent))
            .hasMessageMatching("^executionProperties is marked .*on.*ull but is null$");

        executor.executePre(0, new Properties(), incomingEvent);

        assertThatThrownBy(() -> executor.execute(0, new Properties(), incomingEvent))
            .hasMessage("execute() not implemented on abstract StateFinalizerExecutionContext class, "
                + "only on its subclasses");
        assertThatThrownBy(() -> executor.executePost(false))
            .hasMessage("execute-post: state finalizer logic execution failure on state \"NULL:0.0.0:"
                + "NULL:NULL\" on finalizer logic null");
        executor.getExecutionContext().setMessage("Execution message");
        assertThatThrownBy(() -> executor.executePost(false))
            .hasMessage("execute-post: state finalizer logic execution failure on state \"NULL:0.0.0:"
                + "NULL:NULL\" on finalizer logic null, user message: Execution message");
        executor.executePre(0, new Properties(), incomingEvent);

        assertThatThrownBy(() -> executor.executePost(true))
            .hasMessage("execute-post: state finalizer logic \"null\" did not select an output state");
        executor.executePre(0, new Properties(), incomingEvent);

        executor.getExecutionContext().setSelectedStateOutputName("ThisOutputDoesNotExist");
        assertThatThrownBy(() -> executor.executePost(true))
            .hasMessage("execute-post: state finalizer logic \"null\" selected output state "
                + "\"ThisOutputDoesNotExist\" that does not exsist on state \"NULL:0.0.0:NULL:NULL\"");
        executor.executePre(0, new Properties(), incomingEvent);

        executor.getExecutionContext().setSelectedStateOutputName("ValidOutput");
        executor.executePost(true);

    }
}
