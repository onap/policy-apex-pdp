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
import static org.junit.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvents;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxStateOutput;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskOutputType;
import org.onap.policy.apex.model.policymodel.concepts.AxStateTaskReference;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test task executor.
 */
public class StateMachineExecutorTest {
    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private Executor<EnEvent, EnEvent, AxPolicy, ApexInternalContext> nextExecutorMock;

    @Mock
    private ExecutorFactory executorFactoryMock;

    @Mock
    private EnEvent incomingEventMock;

    private AxPolicy axPolicy = new AxPolicy();

    private DummyTaskSelectExecutor dummyTsle;

    private DummyStateFinalizerExecutor dummySfle;

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);

        axPolicy.setKey(new AxArtifactKey("Policy:0.0.1"));

        AxReferenceKey state0Key = new AxReferenceKey(axPolicy.getKey(), "state0");
        AxState state0 = new AxState(state0Key);

        AxReferenceKey state1Key = new AxReferenceKey(axPolicy.getKey(), "state1");
        AxState state1 = new AxState(state1Key);

        axPolicy.getStateMap().put("State0", state0);
        axPolicy.getStateMap().put("State1", state1);
        axPolicy.setFirstState("state0");

        AxArtifactKey event0Key = new AxArtifactKey("Event0:0.0.1");
        AxEvent event0 = new AxEvent(event0Key, "a.name.space", "source", "target");
        AxArtifactKey event1Key = new AxArtifactKey("Event1:0.0.1");
        AxEvent event1 = new AxEvent(event1Key, "a.name.space", "source", "target");
        AxArtifactKey event2Key = new AxArtifactKey("Event2:0.0.1");
        AxEvent event2 = new AxEvent(event2Key, "a.name.space", "source", "target");
        AxEvents events = new AxEvents();
        events.getEventMap().put(event0Key, event0);
        events.getEventMap().put(event1Key, event1);
        events.getEventMap().put(event2Key, event2);
        ModelService.registerModel(AxEvents.class, events);

        AxReferenceKey fieldKey = new AxReferenceKey("Event1:0.0.1:event:Field0");
        AxArtifactKey stringSchemaKey = new AxArtifactKey("StringSchema:0.0.1");
        AxContextSchema stringSchema = new AxContextSchema(stringSchemaKey, "Java", "java.lang.String");
        AxContextSchemas schemas = new AxContextSchemas();
        schemas.getSchemasMap().put(stringSchemaKey, stringSchema);
        ModelService.registerModel(AxContextSchemas.class, schemas);

        AxField event1Field0Definition = new AxField(fieldKey, stringSchemaKey);
        event1.getParameterMap().put("Event1Field0", event1Field0Definition);

        event0.getParameterMap().put("Event1Field0", event1Field0Definition);
        event0.getParameterMap().put("UnusedField", event1Field0Definition);

        Mockito.doReturn(event0Key).when(incomingEventMock).getKey();
        Mockito.doReturn(event0).when(incomingEventMock).getAxEvent();

        state0.setTrigger(event0Key);
        state1.setTrigger(event1Key);

        AxArtifactKey task0Key = new AxArtifactKey("task0:0.0.1");
        AxTask task0 = new AxTask(task0Key);

        AxArtifactKey task1Key = new AxArtifactKey("task1:0.0.1");
        AxTask task1 = new AxTask(task1Key);

        AxTasks tasks = new AxTasks();
        tasks.getTaskMap().put(task0Key, task0);
        tasks.getTaskMap().put(task1Key, task1);
        ModelService.registerModel(AxTasks.class, tasks);

        ParameterService.register(new SchemaParameters());

        AxReferenceKey stateOutput0Key = new AxReferenceKey("Policy:0.0.1:state0:stateOutput0");
        AxStateOutput stateOutput0 = new AxStateOutput(stateOutput0Key, event1Key, state1.getKey());

        state0.getStateOutputs().put(stateOutput0Key.getLocalName(), stateOutput0);

        AxReferenceKey stateOutput1Key = new AxReferenceKey("Policy:0.0.1:state0:stateOutput1");
        AxStateOutput stateOutput1 = new AxStateOutput(stateOutput1Key, event2Key, AxReferenceKey.getNullKey());

        state1.getStateOutputs().put(stateOutput1Key.getLocalName(), stateOutput1);

        AxReferenceKey str0Key = new AxReferenceKey("Policy:0.0.1:state0:str0");
        AxStateTaskReference str0 = new AxStateTaskReference(str0Key, AxStateTaskOutputType.DIRECT, stateOutput0Key);
        state0.getTaskReferences().put(task0Key, str0);

        AxReferenceKey sflKey = new AxReferenceKey("Policy:0.0.1:state1:sfl");
        AxStateFinalizerLogic sfl = new AxStateFinalizerLogic(sflKey, "Java", "State fianlizer logic");
        state1.getStateFinalizerLogicMap().put("sfl", sfl);

        AxReferenceKey str1Key = new AxReferenceKey("Policy:0.0.1:state1:str1");
        AxStateTaskReference str1 = new AxStateTaskReference(str1Key, AxStateTaskOutputType.LOGIC, sflKey);
        state1.getTaskReferences().put(task1Key, str1);

        Mockito.doReturn(new DummyTaskExecutor(true)).when(executorFactoryMock).getTaskExecutor(Mockito.anyObject(),
            Mockito.anyObject(), Mockito.anyObject());

        dummyTsle = new DummyTaskSelectExecutor(true);
        Mockito.doReturn(dummyTsle).when(executorFactoryMock).getTaskSelectionExecutor(Mockito.anyObject(),
            Mockito.anyObject(), Mockito.anyObject());

        dummySfle = new DummyStateFinalizerExecutor(true);
        Mockito.doReturn(dummySfle).when(executorFactoryMock).getStateFinalizerExecutor(Mockito.anyObject(),
            Mockito.anyObject(), Mockito.anyObject());
    }

    @After
    public void cleardown() {
        ParameterService.clear();
        ModelService.clear();
    }

    @Test
    public void testStateMachineExecutor() throws StateMachineException, ContextException {
        StateMachineExecutor executor =
            new StateMachineExecutor(executorFactoryMock, new AxArtifactKey("OwnerKey:0.0.1"));

        assertThatThrownBy(() -> executor.execute(0, null, incomingEventMock))
            .hasMessageContaining("no states defined on state machine");
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

        assertThatThrownBy(() -> executor.executePre(0, null, null))
            .hasMessageContaining("execution pre work not implemented on class");
        assertThatThrownBy(() -> executor.executePost(false))
            .hasMessageContaining("execution post work not implemented on class");
        assertThatThrownBy(() -> executor.prepare())
            .isInstanceOf(NullPointerException.class);
        axPolicy.setFirstState("BadState");
        executor.setContext(null, axPolicy, internalContextMock);
        assertThatThrownBy(() -> executor.execute(0, null, incomingEventMock))
            .hasMessageContaining("first state not defined on state machine");
        axPolicy.setFirstState("state0");
        executor.setContext(null, axPolicy, internalContextMock);
        executor.execute(0, null, incomingEventMock);

        dummyTsle.setTaskNo(0);
        executor.execute(0, null, incomingEventMock);

        AxReferenceKey badStateKey = new AxReferenceKey("Policy:0.0.1:PName:BadState");
        axPolicy.getStateMap().get("State1").getStateOutputs().get("stateOutput1").setNextState(badStateKey);
        dummyTsle.setTaskNo(0);
        assertThatThrownBy(() -> executor.execute(0, null, incomingEventMock))
            .hasMessageContaining("state execution failed, next state \"Policy:0.0.1:PName:BadState\" not found");
        axPolicy.getStateMap().get("State1").getStateOutputs().get("stateOutput1")
            .setNextState(AxReferenceKey.getNullKey());
        dummyTsle.setTaskNo(0);
        executor.execute(0, null, incomingEventMock);

        axPolicy.getStateMap().get("State1").setTrigger(new AxArtifactKey("BadTrigger:0.0.1"));
        dummyTsle.setTaskNo(0);
        assertThatThrownBy(() -> executor.execute(0, null, incomingEventMock))
            .hasMessageContaining("incoming event \"Event1:0.0.1\" does not match trigger \"BadTrigger:0.0.1\" "
                + "of state \"Policy:0.0.1:NULL:state1\"");
        axPolicy.getStateMap().get("State1").setTrigger(new AxArtifactKey("Event1:0.0.1"));
        dummyTsle.setTaskNo(0);
        executor.execute(0, null, incomingEventMock);

        AxStateFinalizerLogic savedSfl = axPolicy.getStateMap().get("State1").getStateFinalizerLogicMap().get("sfl");
        axPolicy.getStateMap().get("State1").getStateFinalizerLogicMap().put("sfl", null);
        assertThatThrownBy(() -> executor.setContext(null, axPolicy, internalContextMock))
            .hasMessageContaining("state finalizer logic on task reference "
                + "\"AxStateTaskReference:(stateKey=AxReferenceKey:(parentKeyName=Policy,"
                + "parentKeyVersion=0.0.1,parentLocalName=state1,localName=str1),"
                + "outputType=LOGIC,output=AxReferenceKey:(parentKeyName=Policy,parentKeyVersion=0.0.1,"
                + "parentLocalName=state1,localName=sfl))\" on state \"Policy:0.0.1:NULL:state1\" " + "does not exist");
        axPolicy.getStateMap().get("State1").getStateFinalizerLogicMap().put("sfl", savedSfl);
        executor.setContext(null, axPolicy, internalContextMock);

        dummyTsle.setTaskNo(0);
        executor.execute(0, null, incomingEventMock);

        AxArtifactKey task1Key = new AxArtifactKey("task1:0.0.1");
        assertThatThrownBy(() -> {
            axPolicy.getStateMap().get("State1").getTaskReferences().get(task1Key)
                .setStateTaskOutputType(AxStateTaskOutputType.UNDEFINED);
            executor.setContext(null, axPolicy, internalContextMock);
        }).hasMessageContaining("invalid state output type on task reference \"AxStateTaskReference:(stateKey"
                + "=AxReferenceKey:(parentKeyName=Policy,parentKeyVersion=0.0.1,parentLocalName=state1,localName=str1),"
                + "outputType=UNDEFINED,output=AxReferenceKey:(parentKeyName=Policy,"
                + "parentKeyVersion=0.0.1,parentLocalName=state1,localName=sfl))\" "
                + "on state \"Policy:0.0.1:NULL:state1\"");
        axPolicy.getStateMap().get("State1").getTaskReferences().get(task1Key)
            .setStateTaskOutputType(AxStateTaskOutputType.LOGIC);
        executor.setContext(null, axPolicy, internalContextMock);

        dummyTsle.setTaskNo(0);
        executor.execute(0, null, incomingEventMock);

        dummyTsle.setTaskNo(0);
        dummySfle.setReturnBad(true);
        assertThatThrownBy(() -> executor.execute(0, null, incomingEventMock))
            .hasMessageContaining("State execution of state \"Policy:0.0.1:NULL:state1\" on task \"task1:0.0.1\""
                + " failed: state output definition for state output \"stateOutputBad\" not found for "
                + "state \"Policy:0.0.1:NULL:state1\"");
        dummyTsle.setTaskNo(0);
        dummySfle.setReturnBad(false);
        executor.execute(0, null, incomingEventMock);

        assertThatThrownBy(() -> executor.cleanUp())
            .hasMessageContaining("cleanUp() not implemented on class");
    }

    @Test
    public void testStateOutput() throws StateMachineException {
        final StateOutput output =
            new StateOutput(axPolicy.getStateMap().get("State0").getStateOutputs().get("stateOutput0"));
        assertNotNull(output);

        assertEquals("stateOutput0", output.getStateOutputDefinition().getKey().getLocalName());

        assertThatThrownBy(() -> output.setEventFields(null, null))
            .hasMessageContaining("incomingFieldDefinitionMap may not be null");
        Map<String, AxField> incomingFieldDefinitionMap = new LinkedHashMap<>();
        assertThatThrownBy(() -> output.setEventFields(incomingFieldDefinitionMap, null))
            .hasMessageContaining("eventFieldMap may not be null");
        Map<String, Object> eventFieldMap = new LinkedHashMap<>();
        output.setEventFields(incomingFieldDefinitionMap, eventFieldMap);

        eventFieldMap.put("key", "Value");
        assertThatThrownBy(() -> output.setEventFields(incomingFieldDefinitionMap, eventFieldMap))
            .hasMessageContaining("field definitions and values do not match for event Event1:0.0.1\n[]\n[key]");
        AxField axBadFieldDefinition = new AxField();
        incomingFieldDefinitionMap.put("key", axBadFieldDefinition);
        assertThatThrownBy(() -> output.setEventFields(incomingFieldDefinitionMap, eventFieldMap))
            .hasMessageContaining("field \"key\" does not exist on event \"Event1:0.0.1\"");
        incomingFieldDefinitionMap.clear();
        eventFieldMap.clear();
        AxArtifactKey stringSchemaKey = new AxArtifactKey("StringSchema:0.0.1");
        AxReferenceKey fieldKey = new AxReferenceKey("Event1:0.0.1:event:Field0");
        AxField event1Field0Definition = new AxField(fieldKey, stringSchemaKey);
        incomingFieldDefinitionMap.put("Event1Field0", event1Field0Definition);
        eventFieldMap.put("Event1Field0", "Value");
        output.setEventFields(incomingFieldDefinitionMap, eventFieldMap);

        StateOutput outputCopy = new StateOutput(axPolicy.getStateMap().get("State0")
                .getStateOutputs().get("stateOutput0"));

        EnEvent incomingEvent = new EnEvent(new AxArtifactKey("Event0:0.0.1"));
        outputCopy.copyUnsetFields(incomingEvent);

        incomingEvent.put("Event1Field0", "Hello");
        outputCopy.copyUnsetFields(incomingEvent);
    }
}
