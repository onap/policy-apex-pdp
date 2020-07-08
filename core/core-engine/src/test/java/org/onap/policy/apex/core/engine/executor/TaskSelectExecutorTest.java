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

package org.onap.policy.apex.core.engine.executor;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;

/**
 * Test task executor.
 */
public class TaskSelectExecutorTest {
    @Mock
    private AxState axStateMock;

    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private Executor<EnEvent, AxArtifactKey, AxState, ApexInternalContext> nextExecutorMock;

    @Mock
    private AxTaskSelectionLogic taskSelectionLogicMock;

    @Mock
    private EnEvent incomingEvent;

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);

        AxReferenceKey state0Key = new AxReferenceKey("State0Parent:0.0.1:Parent:State0");
        Mockito.doReturn(state0Key).when(axStateMock).getKey();
        Mockito.doReturn(state0Key.getId()).when(axStateMock).getId();

        Map<AxArtifactKey, AxStateTaskReference> taskReferences = new LinkedHashMap<>();
        taskReferences.put(new AxArtifactKey("Task0:0.0.0"), null);
        taskReferences.put(new AxArtifactKey("Task1:0.0.0"), null);
        Mockito.doReturn(taskReferences).when(axStateMock).getTaskReferences();
        Mockito.doReturn(new AxArtifactKey("Task1:0.0.0")).when(axStateMock).getDefaultTask();

        Mockito.doReturn(taskSelectionLogicMock).when(axStateMock).getTaskSelectionLogic();

        Mockito.doReturn(new AxArtifactKey("Context:0.0.1")).when(internalContextMock).getKey();
    }

    @Test
    public void testTaskSelectionExecutor() throws StateMachineException {
        DummyTaskSelectExecutor executor = new DummyTaskSelectExecutor();

        executor.setContext(null, axStateMock, internalContextMock);
        assertEquals("State0Parent:0.0.1:Parent:State0", executor.getKey().getId());
        assertEquals(null, executor.getExecutionContext());
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

        assertThatThrownBy(executor::cleanUp)
            .hasMessage("cleanUp() not implemented on class");
        Mockito.doReturn(null).when(taskSelectionLogicMock).getLogic();

        assertThatThrownBy(executor::prepare)
            .hasMessage("task selection logic cannot be null.");
        Mockito.doReturn("some task logic").when(taskSelectionLogicMock).getLogic();

        executor.prepare();

        executor.executePre(0, new Properties(), incomingEvent);

        assertThatThrownBy(() -> executor.execute(0, new Properties(), incomingEvent))
            .hasMessage("execute() not implemented on class");
        assertThatThrownBy(() -> executor.executePost(false))
            .hasMessage("execute-post: task selection logic failed on state \"State0Parent:0.0.1:Parent:State0\"");

        executor.getExecutionContext().setMessage("Execution message");
        assertThatThrownBy(() -> executor.executePost(false))
            .hasMessageContaining("execute-post: task selection logic failed on state \""
                + "State0Parent:0.0.1:Parent:State0\", user message: Execution message");
        executor.executePre(0, new Properties(), incomingEvent);

        executor.executePost(true);
        assertEquals("Task1", executor.getOutgoing().getName());

        executor.executePre(0, new Properties(), incomingEvent);

        executor.getOutgoing().setName("IDontExist");
        assertThatThrownBy(() -> executor.executePost(true))
            .hasMessageContaining("task \"IDontExist:0.0.0\" returned by task selection logic not defined "
                + "on state \"State0Parent:0.0.1:Parent:State0\"");
        executor.executePre(0, new Properties(), incomingEvent);

        executor.getOutgoing().setName("Task0");

        executor.executePost(true);
        assertEquals("Task0", executor.getOutgoing().getName());

        assertThatThrownBy(() -> executor.executePre(0, null, incomingEvent))
            .hasMessageMatching("^executionProperties is marked .*on.*ull but is null$");
    }
}
