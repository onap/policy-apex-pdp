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

import org.onap.policy.apex.model.basicmodel.service.AbstractParameters;
import org.onap.policy.apex.model.basicmodel.service.ParameterService;

/**
 * This class provides the executors for a logic flavour. Plugin classes for execution of task logic, task selection
 * logic, and state finalizer logic for the logic flavour must be specified.
 * <p>
 * Specializations of this class may provide extra parameters for their specific logic flavour executors.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ExecutorParameters extends AbstractParameters {
    // Executor Plugin classes for executors
    private String taskExecutorPluginClass;
    private String taskSelectionExecutorPluginClass;
    private String stateFinalizerExecutorPluginClass;

    /**
     * Constructor to create an executor parameters instance and register the instance with the parameter service.
     */
    public ExecutorParameters() {
        super(ExecutorParameters.class.getCanonicalName());
        ParameterService.registerParameters(ExecutorParameters.class, this);
    }

    /**
     * Constructor to create an executor parameters instance with the name of a sub class of this class and register the
     * instance with the parameter service.
     *
     * @param parameterClassName the class name of a sub class of this class
     */
    public ExecutorParameters(final String parameterClassName) {
        super(parameterClassName);
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
     * @param taskSelectionExecutorPluginClass the task selection executor plugin class for the executor
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
     * @param stateFinalizerExecutorPluginClass the state finalizer executor plugin class for the executor
     */
    public void setStateFinalizerExecutorPluginClass(final String stateFinalizerExecutorPluginClass) {
        this.stateFinalizerExecutorPluginClass = stateFinalizerExecutorPluginClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.service.AbstractParameters#toString()
     */
    @Override
    public String toString() {
        return "ExecutorParameters [taskExecutorPluginClass=" + taskExecutorPluginClass
                + ", taskSelectionExecutorPluginClass=" + taskSelectionExecutorPluginClass
                + ", StateFinalizerExecutorPluginClass=" + stateFinalizerExecutorPluginClass + "]";
    }
}
