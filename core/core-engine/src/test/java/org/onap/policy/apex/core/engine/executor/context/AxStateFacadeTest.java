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

package org.onap.policy.apex.core.engine.executor.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTasks;

/**
 * Test the state facade.
 */
public class AxStateFacadeTest {
    @Mock
    private AxState axStateMock;

    @Mock
    private AxTask axTaskMock;

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);
        
        AxReferenceKey stateKey = new AxReferenceKey("StateParent:0.0.1:ParentName:StateName");
        Mockito.doReturn(stateKey).when(axStateMock).getKey();

        AxArtifactKey task0Key = new AxArtifactKey("Task0:0.0.1");
        Mockito.doReturn(task0Key).when(axStateMock).getDefaultTask();
        
        Map<AxArtifactKey, Object> taskReferences = new LinkedHashMap<>();;
        taskReferences.put(task0Key, null);
        Mockito.doReturn(taskReferences).when(axStateMock).getTaskReferences();

        AxTasks tasks = new AxTasks();
        tasks.getTaskMap().put(task0Key, axTaskMock);
        
        ModelService.registerModel(AxTasks.class, tasks);
        Mockito.doReturn(task0Key).when(axTaskMock).getKey();
    }

    @Test
    public void testAxStateFacade() {
        AxStateFacade stateFacade = new AxStateFacade(axStateMock);
        
        assertEquals("StateName", stateFacade.getStateName());
        assertEquals("StateParent:0.0.1:ParentName:StateName", stateFacade.getId());
        
        assertEquals("Task0", stateFacade.getDefaultTaskKey().getName());
        assertNull(stateFacade.getTaskKey(null));
        assertEquals("Task0", stateFacade.getTaskKey("Task0").getName());
        stateFacade.getTaskNames();
    }
}
