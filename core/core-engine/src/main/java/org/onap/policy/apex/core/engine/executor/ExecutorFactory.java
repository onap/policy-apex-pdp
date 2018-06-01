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

package org.onap.policy.apex.core.engine.executor;

import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.onap.policy.apex.model.policymodel.concepts.AxStateFinalizerLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;

/**
 * This class is used by the state machine to get implementations of task selection and task executors.
 *
 * @author Liam Fallon
 */

public abstract class ExecutorFactory {
    /**
     * Get an executor for task selection logic.
     *
     * @param stateExecutor the state executor that is requesting the task selection executor
     * @param state the state containing the task selection logic
     * @param context the context the context in which the task selection logic will execute
     * @return The executor that will run the task selection logic
     */
    public abstract TaskSelectExecutor getTaskSelectionExecutor(Executor<?, ?, ?, ?> stateExecutor, AxState state,
            ApexInternalContext context);

    /**
     * Get an executor for task logic.
     *
     * @param stateExecutor the state executor that is requesting the task executor
     * @param task the task containing the task logic
     * @param context the context the context in which the task logic will execute
     * @return The executor that will run the task logic
     */
    public abstract TaskExecutor getTaskExecutor(Executor<?, ?, ?, ?> stateExecutor, AxTask task,
            ApexInternalContext context);

    /**
     * Get an executor for state finalizer logic.
     *
     * @param stateExecutor the state executor that is requesting the state finalizer executor
     * @param logic the state finalizer logic to execute
     * @param context the context the context in which the state finalizer logic will execute
     * @return The executor that will run the state finalizer logic
     */
    public abstract StateFinalizerExecutor getStateFinalizerExecutor(Executor<?, ?, ?, ?> stateExecutor,
            AxStateFinalizerLogic logic, ApexInternalContext context);
}
