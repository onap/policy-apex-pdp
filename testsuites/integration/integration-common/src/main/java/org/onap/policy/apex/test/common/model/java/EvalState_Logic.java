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

package org.onap.policy.apex.test.common.model.java;

import org.onap.policy.apex.core.engine.executor.context.TaskSelectionExecutionContext;

/**
 * The Class EvalState_Logic is default evaluation task selection logic in Java.
 */
//CHECKSTYLE:OFF: checkstyle:typeNames
public class EvalState_Logic {
    // CHECKSTYLE:ON: checkstyle:typeNames
    /**
     * Gets the task.
     *
     * @param executor the executor
     * @return the task
     */
    public boolean getTask(final TaskSelectionExecutionContext executor) {
        executor.logger.debug(executor.subject.getId());
        executor.subject.getDefaultTaskKey().copyTo(executor.selectedTask);
        return true;
    }
}
