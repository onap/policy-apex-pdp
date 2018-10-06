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
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxState;

/**
 * Test task executor.
 */
public class StateMachineExecutorTest {
    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private Executor<EnEvent, EnEvent, AxPolicy, ApexInternalContext> nextExecutorMock;

    @Mock
    private AxState axState0Mock;

    @Mock
    private AxState axState1Mock;

    @Mock
    private ExecutorFactory executorFactoryMock;

    @Mock
    private EnEvent incomingEventMock;

    private AxPolicy axPolicy = new AxPolicy();

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);

        axPolicy.setKey(new AxArtifactKey("Policy:0.0.1"));
        axPolicy.getStateMap().put("State0", axState0Mock);
        axPolicy.getStateMap().put("State1", axState1Mock);
        axPolicy.setFirstState("state0");

        Mockito.doReturn(new AxReferenceKey("Policy:0.0.1:APolicy:state0")).when(axState0Mock).getKey();
        Mockito.doReturn(new AxReferenceKey("Policy:0.0.1:APolicy:state1")).when(axState1Mock).getKey();

        AxArtifactKey event0Key = new AxArtifactKey("Event0:0.0.1");
        AxEvent event0 = new AxEvent(event0Key, "a.name.space", "source", "target");
        AxArtifactKey event1Key = new AxArtifactKey("Event1:0.0.1");
        AxEvent event1 = new AxEvent(event1Key, "a.name.space", "source", "target");
        AxEvents events = new AxEvents();
        events.getEventMap().put(event0Key, event0);
        events.getEventMap().put(event1Key, event1);
        ModelService.registerModel(AxEvents.class, events);

        Mockito.doReturn(event0Key).when(incomingEventMock).getKey();
        Mockito.doReturn(event0).when(incomingEventMock).getAxEvent();

        Mockito.doReturn(event0Key).when(axState0Mock).getTrigger();
        Mockito.doReturn(event1Key).when(axState1Mock).getTrigger();
        
        Mockito.doReturn(new DummyTaskExecutor()).when(executorFactoryMock).getTaskExecutor(Mockito.anyObject(),
                        Mockito.anyObject(), Mockito.anyObject());

        Mockito.doReturn(new DummyTaskSelectExecutor(true)).when(executorFactoryMock)
                        .getTaskSelectionExecutor(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());

        Mockito.doReturn(new DummyStateFinalizerExceutor()).when(executorFactoryMock)
                        .getStateFinalizerExecutor(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject());
    }

    @Test
    public void testStateMachineExecutor() {
        StateMachineExecutor executor = new StateMachineExecutor(executorFactoryMock,
                        new AxArtifactKey("OwnerKey:0.0.1"));

        try {
            executor.execute(0, incomingEventMock);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("no states defined on state machine", ex.getMessage());
        }

        executor.setContext(null, axPolicy, internalContextMock);
        assertEquals("Policy:0.0.1", executor.getKey().getId());
        assertEquals(null, executor.getParent());
        assertEquals(internalContextMock, executor.getContext());
        assertEquals(null, executor.getNext());
        assertEquals(null, executor.getIncoming());
        assertEquals(null, executor.getOutgoing());
        assertEquals(axPolicy, executor.getSubject());

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

        try {
            executor.prepare();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }
        
        axPolicy.setFirstState("BadState");
        executor.setContext(null, axPolicy, internalContextMock);
        try {
            executor.execute(0, incomingEventMock);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("first state not defined on state machine", ex.getMessage());
        }

        try {
            executor.cleanUp();
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("cleanUp() not implemented on class", ex.getMessage());
        }

    }
}
