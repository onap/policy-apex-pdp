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

package org.onap.policy.apex.plugins.executor.java;

import java.lang.reflect.Method;
import java.util.Map;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.core.engine.executor.context.StateFinalizerExecutionContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JavaStateFinalizerExecutor is the state finalizer executor for state finalizer logic written in Java.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavaStateFinalizerExecutor extends StateFinalizerExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavaStateFinalizerExecutor.class);

    // The Java State Finalizer executor class
    private Object stateFinalizerLogicObject = null;

    /**
     * Prepares the state finalizer for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();

        // Get the class for state finalizer execution
        try {
            // Create the state finalizer logic object from the byte code of the class
            stateFinalizerLogicObject = Class.forName(getSubject().getLogic()).newInstance();
        } catch (final Exception e) {
            LOGGER.error("instantiation error on Java class \"" + getSubject().getLogic() + "\"", e);
            throw new StateMachineException("instantiation error on Java class \"" + getSubject().getLogic() + "\"", e);
        }
    }

    /**
     * Executes the executor for the state finalizer logic in a sequential manner.
     *
     * @param executionID the execution ID for the current APEX policy execution
     * @param incomingFields the incoming fields for finalisation
     * @return The state output for the state
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public String execute(final long executionID, final Map<String, Object> incomingFields)
            throws StateMachineException, ContextException {
        // Do execution pre work
        executePre(executionID, incomingFields);

        // Check and execute the Java logic
        boolean returnValue = false;
        try {
            // Find and call the method with the signature "public boolean getStateOutput(final
            // StateFinalizerExecutionContext executor) throws ApexException"
            // to invoke the
            // task logic in the Java class
            final Method method = stateFinalizerLogicObject.getClass().getDeclaredMethod("getStateOutput",
                    new Class[] { StateFinalizerExecutionContext.class });
            returnValue = (boolean) method.invoke(stateFinalizerLogicObject, getExecutionContext());
        } catch (final Exception e) {
            LOGGER.error("execute: state finalizer logic failed to run for state finalizer  \"" + getSubject().getID()
                    + "\"");
            throw new StateMachineException(
                    "state finalizer logic failed to run for state finalizer  \"" + getSubject().getID() + "\"", e);
        }

        // Do the execution post work
        executePost(returnValue);

        // Send back the return event
        if (returnValue) {
            return getOutgoing();
        } else {
            return null;
        }
    }

    /**
     * Cleans up the state finalizer after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void cleanUp() throws StateMachineException {
        LOGGER.debug("cleanUp:" + getSubject().getID() + "," + getSubject().getLogicFlavour() + ","
                + getSubject().getLogic());
    }
}
