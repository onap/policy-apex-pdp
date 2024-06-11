/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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

package org.onap.policy.apex.core.engine.engine.impl;

import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.ExecutorFactory;
import org.onap.policy.apex.core.engine.executor.StateMachineExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Dummy state machine executor for testing.
 */
public class DummySmExecutor extends StateMachineExecutor {
    private boolean cleanupWorks = false;
    private boolean prepareWorks;

    /**
     * Constructor.
     *
     * @param executorFactory the factory for executors
     * @param owner           the owner key
     */
    public DummySmExecutor(ExecutorFactory executorFactory, AxArtifactKey owner) {
        super(executorFactory, owner);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void prepare() throws StateMachineException {
        if (prepareWorks) {
            prepareWorks = false;
        } else {
            prepareWorks = true;
            throw new StateMachineException("dummy state machine executor exception");
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Collection<EnEvent> execute(final long executionId, final Properties executionProperties,
                                       final EnEvent incomingEvent) {
        return List.of(incomingEvent);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cleanUp() throws StateMachineException {
        if (cleanupWorks) {
            cleanupWorks = false;
        } else {
            cleanupWorks = true;
            throw new StateMachineException("dummy state machine executor exception");
        }
    }
}
