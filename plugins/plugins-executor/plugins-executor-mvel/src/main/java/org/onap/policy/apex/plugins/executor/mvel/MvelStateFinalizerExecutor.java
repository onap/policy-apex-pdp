/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.plugins.executor.mvel;

import static org.onap.policy.common.utils.validation.Assertions.argumentNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.mvel2.MVEL;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class MvelStateFinalizerExecutor is the state finalizer executor for state finalizer logic written in MVEL.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class MvelStateFinalizerExecutor extends StateFinalizerExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(MvelStateFinalizerExecutor.class);

    // The MVEL code
    private Serializable compiled = null;

    /**
     * Prepares the state finalizer for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();

        // Compile the MVEL code
        try {
            compiled = MVEL.compileExpression(getSubject().getLogic());
        } catch (final Exception e) {
            LOGGER.warn("failed to compile MVEL code for state " + getSubject().getKey().getId(), e);
            throw new StateMachineException("failed to compile MVEL code for state " + getSubject().getKey().getId(),
                    e);
        }
    }

    /**
     * Executes the executor for the state finalizer logic in a sequential manner.
     *
     * @param executionId the execution ID for the current APEX policy execution
     * @param executionProperties properties for the current APEX policy execution
     * @param incomingFields the incoming fields for finalisation
     * @return The state output for the state
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public String execute(final long executionId, final Properties executionProperties,
            final Map<String, Object> incomingFields) throws StateMachineException, ContextException {
        // Do execution pre work
        executePre(executionId, executionProperties, incomingFields);

        // Check and execute the MVEL logic
        argumentNotNull(compiled, "MVEL state finalizer logic not compiled.");

        var returnValue = false;
        try {
            // Execute the MVEL code
            returnValue =
                    (boolean) MVEL.executeExpression(compiled, getExecutionContext(), new HashMap<String, Object>());
        } catch (final Exception e) {
            LOGGER.warn("failed to execute MVEL code for state " + getSubject().getKey().getId(), e);
            throw new StateMachineException("failed to execute MVEL code for state " + getSubject().getKey().getId(),
                    e);
        }

        // Do the execution post work
        executePost(returnValue);

        // Send back the return event
        return getOutgoing();
    }

    /**
     * Cleans up the state finalizer after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void cleanUp() throws StateMachineException {
        LOGGER.debug("cleanUp:" + getSubject().getKey().getId() + "," + getSubject().getLogicFlavour() + ","
                + getSubject().getLogic());
    }
}
