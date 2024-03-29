/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
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

package org.onap.policy.apex.plugins.executor.java;

import java.util.Properties;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.core.engine.executor.context.TaskSelectionExecutionContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JavaTaskSelectExecutor is the task selection executor for task selection logic written in Java.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavaTaskSelectExecutor extends TaskSelectExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavaTaskSelectExecutor.class);

    // The Java Task Selection executor class
    private Object taskSelectionLogicObject = null;

    /**
     * Prepares the task for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();

        // Get the class for task selection
        try {
            // Create the task logic object from the byte code of the class
            taskSelectionLogicObject = Class.forName(getSubject().getTaskSelectionLogic().getLogic())
                    .getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            LOGGER.error(
                    "instantiation error on Java class \"" + getSubject().getTaskSelectionLogic().getLogic() + "\"", e);
            throw new StateMachineException(
                    "instantiation error on Java class \"" + getSubject().getTaskSelectionLogic().getLogic() + "\"", e);
        }
    }

    /**
     * Executes the executor for the task in a sequential manner.
     *
     * @param executionId the execution ID for the current APEX policy execution
     * @param executionProperties properties for the current APEX policy execution
     * @param incomingEvent the incoming event
     * @return The outgoing event
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public AxArtifactKey execute(final long executionId, final Properties executionProperties,
            final EnEvent incomingEvent) throws StateMachineException, ContextException {
        // Do execution pre work
        executePre(executionId, executionProperties, incomingEvent);

        // Check and execute the Java logic
        var returnValue = false;
        try {
            // Find and call the method with the signature "public boolean getTask(final TaskSelectionExecutionContext
            // executor)" to invoke the task selection
            // logic in the Java class
            final var classes = new Class[] {TaskSelectionExecutionContext.class};
            final var method = taskSelectionLogicObject.getClass().getDeclaredMethod("getTask", classes);
            returnValue = (boolean) method.invoke(taskSelectionLogicObject, getExecutionContext());
        } catch (final Exception e) {
            LOGGER.error(
                    "execute: task selection logic failed to run for state  \"" + getSubject().getKey().getId() + "\"",
                    e);
            throw new StateMachineException(
                    "task selection logic failed to run for state  \"" + getSubject().getKey().getId() + "\"", e);
        }

        // Do the execution post work
        executePost(returnValue);

        // Send back the return event
        return getOutgoing();
    }

    /**
     * Cleans up the task after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void cleanUp() throws StateMachineException {
        LOGGER.debug("cleanUp:" + getSubject().getKey().getId() + ","
                + getSubject().getTaskSelectionLogic().getLogicFlavour() + ","
                + getSubject().getTaskSelectionLogic().getLogic());
    }
}
