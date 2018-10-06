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

package org.onap.policy.apex.core.engine.executor.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.Executor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskSelectionLogic;
import org.onap.policy.common.parameters.ParameterService;

/**
 * Test the executor factory implementation.
 *
 */
public class ExceutorFactoryImplTest {
    @Mock
    private ApexInternalContext internalContextMock;

    @Mock
    private AxState stateMock;

    @Mock
    private AxTaskSelectionLogic tslMock;

    @Mock
    private AxTask taskMock;

    @Mock
    private AxTaskLogic tlMock;

    @Mock
    private AxStateFinalizerLogic sflMock;

    @Mock
    private Executor<?, ?, ?, ?> parentMock;

    private ExecutorParameters executorPars;

    /**
     * Set up mocking.
     */
    @Before
    public void startMocking() {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(tslMock).when(stateMock).getTaskSelectionLogic();
        Mockito.doReturn("Dummy").when(tslMock).getLogicFlavour();

        Mockito.doReturn(tlMock).when(taskMock).getTaskLogic();
        Mockito.doReturn("Dummy").when(tlMock).getLogicFlavour();

        Mockito.doReturn("Dummy").when(sflMock).getLogicFlavour();
    }

    @After
    public void clearPars() {
        ParameterService.clear();
    }

    @Test
    public void testExecutorFactoryImplGood() {
        setGoodPars();

        ExecutorFactoryImpl factory = null;

        try {
            factory = new ExecutorFactoryImpl();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }

        Mockito.doReturn(true).when(stateMock).checkSetTaskSelectionLogic();
        assertNotNull(factory.getTaskSelectionExecutor(null, stateMock, internalContextMock));
        Mockito.doReturn(false).when(stateMock).checkSetTaskSelectionLogic();
        assertNull(factory.getTaskSelectionExecutor(null, stateMock, internalContextMock));

        assertNotNull(factory.getTaskExecutor(null, taskMock, internalContextMock));

        assertNotNull(factory.getStateFinalizerExecutor(parentMock, sflMock, internalContextMock));
    }

    @Test
    public void testExecutorFactoryImplNonExistant() {
        setNonExistantPars();

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");

        } catch (StateMachineException ex) {
            assertEquals("Apex executor class not found for executor plugin "
                            + "\"org.onap.policy.apex.core.engine.executor.BadTaskExecutor\"", ex.getMessage());
        }

        executorPars.setTaskExecutorPluginClass(null);

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");
        } catch (StateMachineException ex) {
            assertEquals("Apex executor class not found for executor plugin "
                            + "\"org.onap.policy.apex.core.engine.executor.BadTaskSelectExecutor\"", ex.getMessage());
        }

        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyTaskExecutor");

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");
        } catch (StateMachineException ex) {
            assertEquals("Apex executor class not found for executor plugin "
                            + "\"org.onap.policy.apex.core.engine.executor.BadTaskSelectExecutor\"", ex.getMessage());
        }

        executorPars.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyTaskSelectExecutor");

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");
        } catch (StateMachineException ex) {
            assertEquals("Apex executor class not found for executor plugin "
                            + "\"org.onap.policy.apex.core.engine.executor.BadStateFinalizerExecutor\"",
                            ex.getMessage());
        }
    }

    @Test
    public void testExecutorFactoryImplBad() {
        setBadPars();

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");

        } catch (StateMachineException ex) {
            assertEquals("Specified Apex executor plugin class \"java.lang.String\" "
                            + "does not implment the Executor interface", ex.getMessage());
        }

        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyTaskExecutor");

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");
        } catch (StateMachineException ex) {
            assertEquals("Specified Apex executor plugin class \"java.lang.String\" "
                            + "does not implment the Executor interface", ex.getMessage());
        }

        executorPars.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyTaskSelectExecutor");

        try {
            new ExecutorFactoryImpl();
            fail("test should throw an exception");
        } catch (StateMachineException ex) {
            assertEquals("Specified Apex executor plugin class \"java.lang.String\" "
                            + "does not implment the Executor interface", ex.getMessage());
        }
    }

    @Test
    public void testExecutorFactoryCreateErrors() {
        setGoodPars();

        executorPars.setTaskExecutorPluginClass(null);

        ExecutorFactoryImpl factory = null;

        try {
            factory = new ExecutorFactoryImpl();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }

        Mockito.doReturn(true).when(stateMock).checkSetTaskSelectionLogic();

        try {
            factory.getTaskExecutor(null, taskMock, internalContextMock);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("Executor plugin class not defined for \"Dummy\" executor of type "
                            + "\"org.onap.policy.apex.core.engine.executor.TaskExecutor\"", ex.getMessage());
        }

        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyFailingTaskExecutor");

        try {
            factory = new ExecutorFactoryImpl();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }

        try {
            factory.getTaskExecutor(null, taskMock, internalContextMock);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("Instantiation error on \"Dummy\" executor of type "
                            + "\"org.onap.policy.apex.core.engine.executor.DummyFailingTaskExecutor\"",
                            ex.getMessage());
        }

        executorPars.setTaskExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyStateFinalizerExecutor");

        try {
            factory = new ExecutorFactoryImpl();
        } catch (StateMachineException e) {
            fail("test should not throw an exception");
        }

        try {
            factory.getTaskExecutor(null, taskMock, internalContextMock);
            fail("test should throw an exception");
        } catch (Exception ex) {
            assertEquals("Executor on \"Dummy\" "
                            + "of type \"class org.onap.policy.apex.core.engine.executor.DummyStateFinalizerExecutor\""
                            + " is not an instance of \"org.onap.policy.apex.core.engine.executor.TaskExecutor\"",
                            ex.getMessage());
        }
    }

    /**
     * Set up good parameters.
     */
    private void setGoodPars() {
        executorPars = new ExecutorParameters();
        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyTaskExecutor");
        executorPars.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyTaskSelectExecutor");
        executorPars.setStateFinalizerExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyStateFinalizerExecutor");

        EngineParameters enginePars = new EngineParameters();
        enginePars.getExecutorParameterMap().put("Dummy", executorPars);

        ParameterService.register(enginePars);
        ParameterService.register(executorPars);
    }

    /**
     * Set up non existant parameters.
     */
    private void setNonExistantPars() {
        executorPars = new ExecutorParameters();
        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.BadTaskExecutor");
        executorPars.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.BadTaskSelectExecutor");
        executorPars.setStateFinalizerExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.BadStateFinalizerExecutor");

        EngineParameters enginePars = new EngineParameters();
        enginePars.getExecutorParameterMap().put("Dummy", executorPars);

        ParameterService.register(enginePars, true);
        ParameterService.register(executorPars, true);
    }

    /**
     * Set up bad parameters.
     */
    private void setBadPars() {
        executorPars = new ExecutorParameters();
        executorPars.setTaskExecutorPluginClass("java.lang.String");
        executorPars.setTaskSelectionExecutorPluginClass("java.lang.String");
        executorPars.setStateFinalizerExecutorPluginClass("java.lang.String");

        EngineParameters enginePars = new EngineParameters();
        enginePars.getExecutorParameterMap().put("Dummy", executorPars);

        ParameterService.register(enginePars, true);
        ParameterService.register(executorPars, true);
    }
}
