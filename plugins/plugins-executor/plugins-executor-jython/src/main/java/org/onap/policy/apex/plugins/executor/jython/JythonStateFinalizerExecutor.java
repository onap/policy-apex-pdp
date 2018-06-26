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

package org.onap.policy.apex.plugins.executor.jython;

import java.util.Map;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.python.core.CompileMode;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JythonStateFinalizerExecutor is the state finalizer executor for state finalizer logic written in Jython It
 * is unlikely that this is thread safe.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JythonStateFinalizerExecutor extends StateFinalizerExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JythonStateFinalizerExecutor.class);

    // The Jython interpreter
    private final PythonInterpreter interpreter = new PythonInterpreter();
    private PyCode compiled = null;

    /**
     * Prepares the state finalizer for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        interpreter.setErr(System.err);
        interpreter.setOut(System.out);

        // Call generic prepare logic
        super.prepare();
        try {
            synchronized (Py.class) {
                compiled = Py.compile_flags(getSubject().getLogic(), "<" + getSubject().getKey().toString() + ">",
                        CompileMode.exec, null);
            }
        } catch (final PyException e) {
            LOGGER.warn("failed to compile Jython code for state finalizer " + getSubject().getKey(), e);
            throw new StateMachineException(
                    "failed to compile Jython code for state finalizer " + getSubject().getKey(), e);
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

        boolean returnValue = false;

        // Do execution pre work
        executePre(executionID, incomingFields);

        try {

            // Check and execute the Jython logic
            /* Precompiled Version */
            synchronized (Py.class) {
                // Set up the Jython engine
                interpreter.set("executor", getExecutionContext());
                interpreter.exec(compiled);
                returnValue = (boolean) interpreter.get("returnValue", java.lang.Boolean.class);
            }
            /* */
        } catch (final Exception e) {
            LOGGER.warn("failed to execute Jython code for state finalizer " + getSubject().getKey(), e);
            throw new StateMachineException(
                    "failed to execute Jython code for state finalizer " + getSubject().getKey(), e);
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
        interpreter.cleanup();
        LOGGER.debug("cleanUp:" + getSubject().getKey() + "," + getSubject().getLogicFlavour() + ","
                + getSubject().getLogic());
    }
}
