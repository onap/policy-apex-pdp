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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;

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
    private Executor<Map<String, Object>, Map<String, Object>, AxTask, ApexInternalContext> nextExecutorMock;

    @Mock
    private AxTaskLogic taskLogicMock;

    private LinkedHashMap<String, Object> inFieldMap;
    private LinkedHashMap<String, Object> outFieldMap;

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

        Mockito.doReturn(new AxArtifactKey("Context:0.0.1")).when(internalContextMock).getKey();
    }

    @Test
    public void testTaskExecutor() {
        DummyTaskExecutor executor = new DummyTaskExecutor();

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

        try {
            executor.cleanUp();
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("cleanUp() not implemented on class", ex.getMessage());
        }

        Mockito.doReturn(null).when(taskLogicMock).getLogic();

        try {
            executor.prepare();
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("task logic cannot be null.", ex.getMessage());
        }

        Mockito.doReturn("some task logic").when(taskLogicMock).getLogic();

        try {
            executor.prepare();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }

        Map<String, Object> incomingFields = new LinkedHashMap<>();
        try {
            executor.executePre(0, incomingFields);
        } catch (Exception ex) {
            assertEquals("task input fields \"[InField0]\" are missing for task \"Task0:0.0.1\"", ex.getMessage());
        }

        incomingFields.put("InField0", "A Value");
        try {
            executor.executePre(0, incomingFields);
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        try {
            executor.execute(0, incomingFields);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute() not implemented on abstract TaskExecutor class, only on its subclasses",
                            ex.getMessage());
        }

        try {
            executor.executePost(false);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute-post: task logic execution failure on task \"Task0\" in model Context:0.0.1",
                            ex.getMessage());
        }

        executor.getExecutionContext().setMessage("Execution message");
        try {
            executor.executePost(false);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("execute-post: task logic execution failure on task \"Task0\" in model Context:0.0.1, "
                            + "user message: Execution message", ex.getMessage());
        }

        try {
            executor.executePost(true);
        } catch (Exception e) {
            fail("test should not throw an exception");
        }

        outFieldMap.put("MissingField", axMissingOutputFieldMock);
        try {
            executor.executePost(true);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("task output fields \"[MissingField]\" are missing for task \"Task0:0.0.1\"", ex.getMessage());
        }

        outFieldMap.remove("MissingField");
        executor.getExecutionContext().outFields.put("BadExtraField", "Howdy!");
        try {
            executor.executePost(true);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("task output fields \"[BadExtraField]\" are unwanted for task \"Task0:0.0.1\"",
                            ex.getMessage());
        }

        executor.getExecutionContext().outFields.remove("BadExtraField");
        outFieldMap.put("InField1", axMissingOutputFieldMock);
        try {
            executor.executePost(true);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }

        outFieldMap.put("InField0", axMissingOutputFieldMock);
        try {
            executor.executePost(true);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }

        executor.getExecutionContext().outFields.put("InField0", "Output Value");
        try {
            executor.executePost(true);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }

        executor.getExecutionContext().outFields.remove("InField0");
        try {
            executor.executePost(true);
        } catch (Exception ex) {
            fail("test should not throw an exception");
        }
    }
}
