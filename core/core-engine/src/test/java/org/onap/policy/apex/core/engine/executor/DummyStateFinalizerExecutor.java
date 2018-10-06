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

import java.util.Map;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;

/**
 * Dummy state finalizer executor for testing.
 */
public class DummyStateFinalizerExecutor extends StateFinalizerExecutor {
    private boolean override;
    
    private boolean returnBad;

    public DummyStateFinalizerExecutor() {
        this(false);
    }
    
    public DummyStateFinalizerExecutor(final boolean override) {
        this.override = override;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#execute(java.lang.long, java.lang.Object)
     */
    @Override
    public String execute(final long executionId, final Map<String, Object> newIncomingFields)
                    throws StateMachineException, ContextException {
        
        if (!override) {
            super.execute(executionId, newIncomingFields);
        }
        
        if (returnBad) {
            return "stateOutputBad";
        }
        else {
            return "stateOutput1";
        }
    }

    public void setReturnBad(boolean returnBad) {
        this.returnBad = returnBad;
    }
}
