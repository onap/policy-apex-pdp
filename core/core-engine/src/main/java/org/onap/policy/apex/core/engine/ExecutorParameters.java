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

package org.onap.policy.apex.core.engine;

import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ParameterGroup;

/**
 * This class provides the executors for a logic flavour. Plugin classes for execution of task
 * logic, task selection logic, and state finalizer logic for the logic flavour must be specified.
 *
 * <p>Specializations of this class may provide extra parameters for their specific logic flavour
 * executors.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ExecutorParameters implements ParameterGroup {
    // Parameter group name
    private String name;

    // Executor Plugin classes for executors
    private String taskExecutorPluginClass;
    private String taskSelectionExecutorPluginClass;
    private String stateFinalizerExecutorPluginClass;

    /**
     * Constructor to create an executor parameters instance and register the instance with the
     * parameter service.
     */
    public ExecutorParameters() {
        super();

        // Set the name for the parameters
        this.name = EngineParameterConstants.EXECUTOR_GROUP_NAME;
    }

    /**
     * Gets the task executor plugin class for the executor.
     *
     * @return the task executor plugin class for the executor
     */
    public String getTaskExecutorPluginClass() {
        return taskExecutorPluginClass;
    }

    /**
     * Sets the task executor plugin class for the executor.
     *
     * @param taskExecutorPluginClass the task executor plugin class for the executor
     */
    public void setTaskExecutorPluginClass(final String taskExecutorPluginClass) {
        this.taskExecutorPluginClass = taskExecutorPluginClass;
    }

    /**
     * Gets the task selection executor plugin class for the executor.
     *
     * @return the task selection executor plugin class for the executor
     */
    public String getTaskSelectionExecutorPluginClass() {
        return taskSelectionExecutorPluginClass;
    }

    /**
     * Sets the task selection executor plugin class for the executor.
     *
     * @param taskSelectionExecutorPluginClass the task selection executor plugin class for the
     *        executor
     */
    public void setTaskSelectionExecutorPluginClass(final String taskSelectionExecutorPluginClass) {
        this.taskSelectionExecutorPluginClass = taskSelectionExecutorPluginClass;
    }

    /**
     * Gets the state finalizer executor plugin class for the executor.
     *
     * @return the state finalizer executor plugin class for the executor
     */
    public String getStateFinalizerExecutorPluginClass() {
        return stateFinalizerExecutorPluginClass;
    }

    /**
     * Sets the state finalizer executor plugin class for the executor.
     *
     * @param stateFinalizerExecutorPluginClass the state finalizer executor plugin class for the
     *        executor
     */
    public void setStateFinalizerExecutorPluginClass(final String stateFinalizerExecutorPluginClass) {
        this.stateFinalizerExecutorPluginClass = stateFinalizerExecutorPluginClass;
    }

    @Override
    public String toString() {
        return "ExecutorParameters [name=" + name + ", taskExecutorPluginClass=" + taskExecutorPluginClass
                        + ", taskSelectionExecutorPluginClass=" + taskSelectionExecutorPluginClass
                        + ", stateFinalizerExecutorPluginClass=" + stateFinalizerExecutorPluginClass + "]";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public GroupValidationResult validate() {
        return new GroupValidationResult(this);
    }
}
