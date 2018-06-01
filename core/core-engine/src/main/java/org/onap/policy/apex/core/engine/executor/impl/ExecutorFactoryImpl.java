/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.onap.policy.apex.core.engine.EngineParameters;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.Executor;
import org.onap.policy.apex.core.engine.executor.ExecutorFactory;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.core.engine.executor.TaskExecutor;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineRuntimeException;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.utilities.Assertions;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class ExecutorFactoryImpl is a factory class that returns task selection logic and task logic executors depending
 * on the type of logic executor has been specified for the task selection logic in a state or task logic in a task.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ExecutorFactoryImpl extends ExecutorFactory {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(ExecutorFactoryImpl.class);

    // A map of logic flavours mapped to executor classes for plugins to executors for those logic flavours
    private Map<String, Class<Executor<?, ?, ?, ?>>> taskExecutorPluginClassMap =
            new TreeMap<String, Class<Executor<?, ?, ?, ?>>>();
    private Map<String, Class<Executor<?, ?, ?, ?>>> taskSelectionExecutorPluginClassMap =
            new TreeMap<String, Class<Executor<?, ?, ?, ?>>>();
    private Map<String, Class<Executor<?, ?, ?, ?>>> stateFinalizerExecutorPluginClassMap =
            new TreeMap<String, Class<Executor<?, ?, ?, ?>>>();

    // A map of parameters for executors
    private final Map<String, ExecutorParameters> implementationParameterMap =
            new TreeMap<String, ExecutorParameters>();

    /**
     * Constructor, builds the class map for executors.
     *
     * @throws StateMachineException on plugin creation errors
     */
    public ExecutorFactoryImpl() throws StateMachineException {
        final EngineParameters engineParameters = ParameterService.getParameters(EngineParameters.class);

        Assertions.argumentNotNull(engineParameters, StateMachineException.class,
                "Parameter \"engineParameters\" may not be null");

        // Instantiate each executor class map entry
        for (final Entry<String, ExecutorParameters> executorParameterEntry : engineParameters.getExecutorParameterMap()
                .entrySet()) {
            // Get classes for all types of executors for this logic type
            taskExecutorPluginClassMap.put(executorParameterEntry.getKey(),
                    getExecutorPluginClass(executorParameterEntry.getValue().getTaskExecutorPluginClass()));
            taskSelectionExecutorPluginClassMap.put(executorParameterEntry.getKey(),
                    getExecutorPluginClass(executorParameterEntry.getValue().getTaskSelectionExecutorPluginClass()));
            stateFinalizerExecutorPluginClassMap.put(executorParameterEntry.getKey(),
                    getExecutorPluginClass(executorParameterEntry.getValue().getStateFinalizerExecutorPluginClass()));

            // Save the executor implementation parameters
            implementationParameterMap.put(executorParameterEntry.getKey(), executorParameterEntry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.executor.ExecutorFactory#getTaskSelectionExecutor(org.onap.policy.apex.core.
     * model. concepts.AxState, org.onap.policy.apex.core.engine.context.Context)
     */
    @Override
    public TaskSelectExecutor getTaskSelectionExecutor(final Executor<?, ?, ?, ?> parentExecutor, final AxState state,
            final ApexInternalContext context) {
        if (!state.checkSetTaskSelectionLogic()) {
            return null;
        }

        // Create task selection executor
        final TaskSelectExecutor tsExecutor =
                (TaskSelectExecutor) createExecutor(state.getTaskSelectionLogic().getLogicFlavour(),
                        taskSelectionExecutorPluginClassMap.get(state.getTaskSelectionLogic().getLogicFlavour()),
                        TaskSelectExecutor.class);
        tsExecutor.setParameters(implementationParameterMap.get(state.getTaskSelectionLogic().getLogicFlavour()));
        tsExecutor.setContext(parentExecutor, state, context);

        return tsExecutor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.ExecutorFactory#getTaskExecutor(org.onap.policy.apex.core.model.
     * concepts. AxTask, org.onap.policy.apex.core.engine.context.Context)
     */
    @Override
    public TaskExecutor getTaskExecutor(final Executor<?, ?, ?, ?> parentExecutor, final AxTask task,
            final ApexInternalContext context) {
        // Create task executor
        final TaskExecutor taskExecutor = (TaskExecutor) createExecutor(task.getTaskLogic().getLogicFlavour(),
                taskExecutorPluginClassMap.get(task.getTaskLogic().getLogicFlavour()), TaskExecutor.class);
        taskExecutor.setParameters(implementationParameterMap.get(task.getTaskLogic().getLogicFlavour()));
        taskExecutor.setContext(parentExecutor, task, context);

        return taskExecutor;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.executor.ExecutorFactory#getStateFinalizerExecutor(org.onap.policy.apex.core.
     * engine. executor.Executor, org.onap.policy.apex.core.policymodel.concepts.AxStateFinalizerLogic,
     * org.onap.policy.apex.core.engine.context.ApexInternalContext)
     */
    @Override
    public StateFinalizerExecutor getStateFinalizerExecutor(final Executor<?, ?, ?, ?> parentExecutor,
            final AxStateFinalizerLogic logic, final ApexInternalContext context) {
        // Create state finalizer executor
        final StateFinalizerExecutor sfExecutor = (StateFinalizerExecutor) createExecutor(logic.getLogicFlavour(),
                stateFinalizerExecutorPluginClassMap.get(logic.getLogicFlavour()), StateFinalizerExecutor.class);
        sfExecutor.setParameters(implementationParameterMap.get(logic.getLogicFlavour()));
        sfExecutor.setContext(parentExecutor, logic, context);

        return sfExecutor;
    }

    /**
     * Get an executor class for a given executor plugin class name.
     *
     * @param executorClassName The name of the executor plugin class
     * @return an executor class
     * @throws StateMachineException on plugin instantiation errors
     */
    @SuppressWarnings("unchecked")
    private Class<Executor<?, ?, ?, ?>> getExecutorPluginClass(final String executorClassName)
            throws StateMachineException {
        // It's OK for an executor class not to be defined as long as it's not called
        if (executorClassName == null) {
            return null;
        }

        // Get the class for the executor using reflection
        Class<? extends Object> executorPluginClass = null;
        try {
            executorPluginClass = Class.forName(executorClassName);
        } catch (final ClassNotFoundException e) {
            LOGGER.error("Apex executor class not found for executor plugin \"" + executorClassName + "\"", e);
            throw new StateMachineException(
                    "Apex executor class not found for executor plugin \"" + executorClassName + "\"", e);
        }

        // Check the class is an executor
        if (!Executor.class.isAssignableFrom(executorPluginClass)) {
            LOGGER.error("Specified Apex executor plugin class \"" + executorClassName
                    + "\" does not implment the Executor interface");
            throw new StateMachineException("Specified Apex executor plugin class \"" + executorClassName
                    + "\" does not implment the Executor interface");
        }

        return (Class<Executor<?, ?, ?, ?>>) executorPluginClass;
    }

    /**
     * Get an instance of an executor plugin class of the specified type and super type.
     *
     * @param logicFlavour The logic flavour of the logic
     * @param executorClass The sub-class of the executor type to be instantiated
     * @param executorSuperClass The super type of the class of executor to be instantiated
     * @return The instantiated class
     */
    private Executor<?, ?, ?, ?> createExecutor(final String logicFlavour,
            final Class<Executor<?, ?, ?, ?>> executorClass,
            final Class<? extends Executor<?, ?, ?, ?>> executorSuperClass) {
        // It's OK for an executor class not to be defined but it's not all right to try and create a non-defined
        // executor class
        if (executorClass == null) {
            final String errorMessage = "Executor plugin class not defined for \"" + logicFlavour
                    + "\" executor of type \"" + executorSuperClass.getCanonicalName() + "\"";
            LOGGER.error(errorMessage);
            throw new StateMachineRuntimeException(errorMessage);
        }

        // Create an executor for the specified logic flavour
        Object executorObject = null;
        try {
            executorObject = executorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            final String errorMessage = "Instantiation error on \"" + logicFlavour + "\" executor of type \""
                    + executorClass.getCanonicalName() + "\"";
            LOGGER.error(errorMessage, e);
            throw new StateMachineRuntimeException(errorMessage, e);
        }

        // Check the class is a Task Selection Executor
        if (!(executorSuperClass.isAssignableFrom(executorObject.getClass()))) {
            final String errorMessage = "Executor on \"" + logicFlavour + "\" of type \"" + executorClass
                    + "\" is not an instance of \"" + executorSuperClass.getCanonicalName() + "\"";

            LOGGER.error(errorMessage);
            throw new StateMachineRuntimeException(errorMessage);
        }

        return (Executor<?, ?, ?, ?>) executorObject;
    }
}
