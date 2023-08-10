/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020, 2023 Nordix Foundation
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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
@RunWith(MockitoJUnitRunner.class)
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
    public void testExecutorFactoryImplGood() throws StateMachineException {
        setGoodPars();

        ExecutorFactoryImpl factory = null;

        factory = new ExecutorFactoryImpl();

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

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Apex executor class not found for executor plugin "
                            + "\"org.onap.policy.apex.core.engine.executor.BadTaskExecutor\"");
        executorPars.setTaskExecutorPluginClass(null);

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Apex executor class not found for executor plugin "
                    + "\"org.onap.policy.apex.core.engine.executor.BadTaskSelectExecutor\"");
        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyTaskExecutor");

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Apex executor class not found for executor plugin "
                    + "\"org.onap.policy.apex.core.engine.executor.BadTaskSelectExecutor\"");
        executorPars.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyTaskSelectExecutor");

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Apex executor class not found for executor plugin "
                    + "\"org.onap.policy.apex.core.engine.executor.BadStateFinalizerExecutor\"");
    }

    @Test
    public void testExecutorFactoryImplBad() {
        setBadPars();

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Specified Apex executor plugin class \"java.lang.String\" "
                    + "does not implment the Executor interface");
        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyTaskExecutor");

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Specified Apex executor plugin class \"java.lang.String\" "
                    + "does not implment the Executor interface");
        executorPars.setTaskSelectionExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyTaskSelectExecutor");

        assertThatThrownBy(() -> new ExecutorFactoryImpl())
            .hasMessage("Specified Apex executor plugin class \"java.lang.String\" "
                    + "does not implment the Executor interface");
    }

    @Test
    public void testExecutorFactoryCreateErrors() throws StateMachineException {
        setGoodPars();

        executorPars.setTaskExecutorPluginClass(null);

        final ExecutorFactoryImpl factory = new ExecutorFactoryImpl();

        assertThatThrownBy(() -> factory.getTaskExecutor(null, taskMock, internalContextMock))
            .hasMessage("Executor plugin class not defined for \"Dummy\" executor of type "
                    + "\"org.onap.policy.apex.core.engine.executor.TaskExecutor\"");
        executorPars.setTaskExecutorPluginClass("org.onap.policy.apex.core.engine.executor.DummyFailingTaskExecutor");

        ExecutorFactoryImpl factoryInitError = new ExecutorFactoryImpl();

        assertThatThrownBy(() -> factoryInitError.getTaskExecutor(null, taskMock, internalContextMock))
            .hasMessage("Instantiation error on \"Dummy\" executor of type "
                    + "\"org.onap.policy.apex.core.engine.executor.DummyFailingTaskExecutor\"");
        executorPars.setTaskExecutorPluginClass(
                        "org.onap.policy.apex.core.engine.executor.DummyStateFinalizerExecutor");

        ExecutorFactoryImpl factoryDummyError = new ExecutorFactoryImpl();

        assertThatThrownBy(() -> factoryDummyError.getTaskExecutor(null, taskMock, internalContextMock))
            .hasMessage("Executor on \"Dummy\" "
                    + "of type \"class org.onap.policy.apex.core.engine.executor.DummyStateFinalizerExecutor\""
                    + " is not an instance of \"org.onap.policy.apex.core.engine.executor.TaskExecutor\"");
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
