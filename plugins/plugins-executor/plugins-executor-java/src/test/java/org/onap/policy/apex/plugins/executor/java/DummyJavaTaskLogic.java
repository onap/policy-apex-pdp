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

package org.onap.policy.apex.plugins.executor.java;

import org.onap.policy.apex.core.engine.executor.context.TaskExecutionContext;

/**
 * This is a dummy task logic class.
 *
 */
public class DummyJavaTaskLogic {
    /**
     * Sets up the return event in the execution context.
     *
     * @param context the execution context
     * @return the true if the return event was set in the context, false on errors. 
     */
    public boolean getEvent(final TaskExecutionContext context) {
        if (context.executionId == -1) {
            return false;
            
        }
        
        return true;
    }
}
