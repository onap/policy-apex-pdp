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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
public class StateFinalizerExecutorTest {
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
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);
        
        AxState state = new AxState();
        state.getStateOutputs().put("ValidOutput", null);
        
        Mockito.doReturn(state).when(parentMock).getSubject();

        Mockito.doReturn(new AxReferenceKey("State:0.0.1:StateName:StateSFL")).when(stateFinalizerLogicMock).getKey();
    }

    @Test
    public void testStateFinalizerExecutor() {
        DummyStateFinalizerExecutor executor = new DummyStateFinalizerExecutor();

        executor.setContext(parentMock, stateFinalizerLogicMock, internalContextMock);
        assertEquals("State:0.0.1:StateName:StateSFL", executor.getKey().getId());
        assertEquals(null, executor.getExecutionContext());
        assertEquals(parentMock, executor.getParent());
        assertEquals(internalContextMock, executor.getContext());
        assertEquals(null, executor.getNext());
        assertEquals(null, executor.getIncoming());
        assertEquals(null, executor.getOutgoing());
        assertEquals(stateFinalizerLogicMock, executor.getSubject());

        executor.setParameters(new ExecutorParameters());
        executor.setNext(nextExecutorMock);
        assertEquals(nextExecutorMock, executor.getNext());
        executor.setNext(null);
        assertEquals(null, executor.getNext());

        try {
            executor.cleanUp();
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("cleanUp() not implemented on class", ex.getMessage());
        }

        Mockito.doReturn(null).when(stateFinalizerLogicMock).getLogic();

        try {
            executor.prepare();
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("state finalizer logic cannot be null.", ex.getMessage());
        }

        Mockito.doReturn("some task logic").when(stateFinalizerLogicMock).getLogic();

        try {
            executor.prepare();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }

        try {
            executor.executePre(0, incomingEvent);
        } catch (Exception ex) {
            assertEquals("task input fields \"[InField0]\" are missing for task \"Task0:0.0.1\"", ex.getMessage());
        }

        try {
            executor.executePre(0, incomingEvent);
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        try {
            executor.execute(0, incomingEvent);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute() not implemented on abstract StateFinalizerExecutionContext class, "
                            + "only on its subclasses", ex.getMessage());
        }

        try {
            executor.executePost(false);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute-post: state finalizer logic execution failure on state \"NULL:0.0.0:NULL:NULL\" "
                            + "on finalizer logic null", ex.getMessage());
        }

        executor.getExecutionContext().setMessage("Execution message");
        try {
            executor.executePost(false);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute-post: state finalizer logic execution failure on state \"NULL:0.0.0:NULL:NULL\" "
                            + "on finalizer logic null, user message: Execution message", ex.getMessage());
        }

        try {
            executor.executePre(0, incomingEvent);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }

        try {
            executor.executePost(true);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute-post: state finalizer logic \"null\" did not select an output state",
                            ex.getMessage());
        }

        try {
            executor.executePre(0, incomingEvent);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }

        executor.getExecutionContext().setSelectedStateOutputName("ThisOutputDoesNotExist");
        try {
            executor.executePost(true);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute-post: state finalizer logic \"null\" selected output state "
                            + "\"ThisOutputDoesNotExist\" that does not exsist on state \"NULL:0.0.0:NULL:NULL\"",
                            ex.getMessage());
        }

        try {
            executor.executePre(0, incomingEvent);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }

        executor.getExecutionContext().setSelectedStateOutputName("ValidOutput");
        try {
            executor.executePost(true);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }
}
