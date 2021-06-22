/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.TaskParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;

/**
 * Test task excutor.
 */
public class TaskExecutorTest {
    @Mock
    private AxTask axTaskMock;

    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private AxInputField axInputFieldMock;

    @Mock
    private AxInputField axOptionalInputFieldMock;

    @Mock
    private AxOutputField axOutputFieldMock;

    @Mock
    private AxOutputField axOptionalOutputFieldMock;

    @Mock
    private AxOutputField axMissingOutputFieldMock;

    @Mock
    private Executor<Map<String, Object>, Map<String, Map<String, Object>>, AxTask,
        ApexInternalContext> nextExecutorMock;

    @Mock
    private AxTaskLogic taskLogicMock;

    private LinkedHashMap<String, Object> inFieldMap;
    private LinkedHashMap<String, Object> outFieldMap;
    private List<TaskParameters> taskParametersFromConfig;
    private Map<String, AxEvent> outEvents = new TreeMap<>();

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);

        AxArtifactKey task0Key = new AxArtifactKey("Task0:0.0.1");
        Mockito.doReturn(task0Key).when(axTaskMock).getKey();
        Mockito.doReturn(task0Key.getId()).when(axTaskMock).getId();

        inFieldMap = new LinkedHashMap<>();
        outFieldMap = new LinkedHashMap<>();

        inFieldMap.put("InField0", axInputFieldMock);
        inFieldMap.put("InField1", axOptionalInputFieldMock);
        outFieldMap.put("OutField0", axOutputFieldMock);
        outFieldMap.put("OutField0", axOptionalOutputFieldMock);

        AxArtifactKey schemaKey = new AxArtifactKey("Schema:0.0.1");
        Mockito.doReturn(schemaKey).when(axInputFieldMock).getSchema();
        Mockito.doReturn(schemaKey).when(axOptionalInputFieldMock).getSchema();
        Mockito.doReturn(schemaKey).when(axOutputFieldMock).getSchema();
        Mockito.doReturn(schemaKey).when(axOptionalOutputFieldMock).getSchema();
        Mockito.doReturn(schemaKey).when(axMissingOutputFieldMock).getSchema();

        Mockito.doReturn(true).when(axOptionalInputFieldMock).getOptional();
        Mockito.doReturn(true).when(axOptionalOutputFieldMock).getOptional();
        Mockito.doReturn(false).when(axMissingOutputFieldMock).getOptional();

        Mockito.doReturn(inFieldMap).when(axTaskMock).getInputFields();
        Mockito.doReturn(outFieldMap).when(axTaskMock).getOutputFields();
        Mockito.doReturn(taskLogicMock).when(axTaskMock).getTaskLogic();

        AxEvent inEvent = new AxEvent();
        Map<String, AxField> inFields = new TreeMap<>();
        inFields.put("InField0", axInputFieldMock);
        inFields.put("InField1", axOptionalInputFieldMock);
        inEvent.setParameterMap(inFields);
        AxEvent outEvent = new AxEvent(new AxArtifactKey("outputEvent:1.0.0"));
        Map<String, AxField> outFields = new TreeMap<>();
        outFields.put("OutField0", axOutputFieldMock);
        outFields.put("OutField0", axOptionalOutputFieldMock);
        outEvent.setParameterMap(outFields);
        outEvents.put(outEvent.getKey().getName(), outEvent);

        Mockito.doReturn(inEvent).when(axTaskMock).getInputEvent();
        Mockito.doReturn(outEvents).when(axTaskMock).getOutputEvents();

        Mockito.doReturn(new AxArtifactKey("Context:0.0.1")).when(internalContextMock).getKey();

        Map<String, AxTaskParameter> taskParameters = new HashMap<>();
        taskParameters.put("parameterKey2", new AxTaskParameter(new AxReferenceKey(), "parameterOriginalValue2"));
        Mockito.doReturn(taskParameters).when(axTaskMock).getTaskParameters();

        taskParametersFromConfig = new ArrayList<>();
        taskParametersFromConfig.add(new TaskParameters("parameterKey0", "parameterNewValue0", "Task0:0.0.1"));
        taskParametersFromConfig.add(new TaskParameters("parameterKey1", "parameterNewValue1", "Task1:0.0.1"));
        taskParametersFromConfig.add(new TaskParameters("parameterKey2", "parameterNewValue2", null));
    }

    @Test
    public void testTaskExecutor() throws StateMachineException, ContextException {
        final DummyTaskExecutor executor = new DummyTaskExecutor();
        executor.setContext(null, axTaskMock, internalContextMock);
        assertEquals("Task0:0.0.1", executor.getKey().getId());
        assertEquals(null, executor.getExecutionContext());
        assertEquals(null, executor.getParent());
        assertEquals(internalContextMock, executor.getContext());
        assertEquals(null, executor.getNext());
        assertEquals(null, executor.getIncoming());
        assertEquals(null, executor.getOutgoing());
        assertNotNull(executor.getSubject());

        executor.setParameters(new ExecutorParameters());
        executor.setNext(nextExecutorMock);
        assertEquals(nextExecutorMock, executor.getNext());
        executor.setNext(null);
        assertEquals(null, executor.getNext());

        assertThatThrownBy(() -> executor.cleanUp()).hasMessageContaining("cleanUp() not implemented on class");

        Mockito.doReturn(null).when(taskLogicMock).getLogic();

        assertThatThrownBy(() -> executor.prepare()).hasMessageContaining("task logic cannot be null.");

        Mockito.doReturn("some task logic").when(taskLogicMock).getLogic();

        executor.prepare();

        Map<String, Object> incomingFields = new LinkedHashMap<>();

        incomingFields.put("InField0", "A Value");

        executor.executePre(0, new Properties(), incomingFields);

        assertThatThrownBy(() -> executor.execute(0, new Properties(), incomingFields))
            .hasMessageContaining("execute() not implemented on abstract TaskExecutor class, only on its subclasses");

        assertThatThrownBy(() -> executor.executePost(false)).hasMessageContaining(
            "execute-post: task logic execution failure on task \"Task0\" in model Context:0.0.1");

        executor.getExecutionContext().setMessage("Execution message");

        assertThatThrownBy(() -> executor.executePost(false)).hasMessageContaining(
            "execute-post: task logic execution failure on task \"Task0\" in model Context:0.0.1, "
                + "user message: Execution message");

        executor.executePost(true);

        outFieldMap.put("MissingField", axMissingOutputFieldMock);
        outEvents.get("outputEvent").getParameterMap().put("MissingField", axMissingOutputFieldMock);
        assertThatThrownBy(() -> executor.executePost(true)).hasMessageContaining(
            "Fields for task output events \"[outputEvent]\" are missing for task \"Task0:0.0.1\"");

        outFieldMap.remove("MissingField");
        outEvents.get("outputEvent").getParameterMap().remove("MissingField");
        executor.getExecutionContext().outFields.put("BadExtraField", "Howdy!");

        assertThatThrownBy(() -> executor.executePost(true)).hasMessageContaining(
            "task output event \"[outputEvent]\" contains fields that are unwanted for task \"Task0:0.0.1\"");

        executor.getExecutionContext().outFields.remove("BadExtraField");
        outFieldMap.put("InField1", axMissingOutputFieldMock);
        executor.executePost(true);

        outFieldMap.put("InField0", axMissingOutputFieldMock);
        executor.executePost(true);

        executor.getExecutionContext().outFields.put("InField0", "Output Value");
        outEvents.get("outputEvent").getParameterMap().put("InField0", axMissingOutputFieldMock);
        executor.executePost(true);

        executor.getExecutionContext().outFields.remove("InField0");
        executor.executePost(true);

        assertThatThrownBy(() -> executor.executePre(0, null, incomingFields))
            .hasMessageMatching("^executionProperties is marked .*on.*ull but is null$");
    }

    @Test
    public void testTaskExecutorForTaskParameters() {
        DummyTaskExecutor executorForParmeterTest = new DummyTaskExecutor(false);

        executorForParmeterTest.setContext(null, axTaskMock, internalContextMock);
        executorForParmeterTest.updateTaskParameters(taskParametersFromConfig);
        assertNotNull(executorForParmeterTest.getSubject().getTaskParameters());
        // taskId matched, parameter value updated with the new value
        assertEquals("parameterNewValue0",
            executorForParmeterTest.getSubject().getTaskParameters().get("parameterKey0").getTaskParameterValue());
        // taskId mismatch, so the parameter is not updated in the task
        assertNull(executorForParmeterTest.getSubject().getTaskParameters().get("parameterKey1"));
        // taskId is not available, so parameter is updated in the task
        assertEquals("parameterNewValue2",
            executorForParmeterTest.getSubject().getTaskParameters().get("parameterKey2").getTaskParameterValue());
    }
}
