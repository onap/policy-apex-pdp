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

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Dummy task selection executor for testing.
 */
public class DummyTaskSelectExecutor extends TaskSelectExecutor {
    private boolean override;

    private static int taskNo;
    
    public DummyTaskSelectExecutor() {
        this(false);
    }

    public DummyTaskSelectExecutor(final boolean override) {
        this.override = override;
    }

    @Override
    public void prepare() throws StateMachineException {
        if (!override) {
            super.prepare();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#execute(java.lang.long, java.lang.Object)
     */
    @Override
    public AxArtifactKey execute(final long executionId, final EnEvent newIncomingEvent)
                    throws StateMachineException, ContextException {
        if (!override) {
            return super.execute(executionId, newIncomingEvent);
        }
        
        return new AxArtifactKey("task" + (taskNo++) + ":0.0.1");
    }

    public void setTaskNo(int incomingTaskNo) {
        taskNo = incomingTaskNo;
    }
    

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#cleanUp()
     */
    @Override
    public void cleanUp() throws StateMachineException {
        if (!override) {
            super.cleanUp();
        }
    }
}
