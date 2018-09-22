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
import org.onap.policy.apex.core.engine.executor.TaskExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.python.core.CompileMode;
import org.python.core.CompilerFlags;
import org.python.core.Py;
import org.python.core.PyCode;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JythonTaskExecutor is the task executor for task logic written in Jython It is unlikely
 * that this is thread safe.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JythonTaskExecutor extends TaskExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JythonTaskExecutor.class);

    // The Jython interpreter
    private final PythonInterpreter interpreter = new PythonInterpreter();
    private PyCode compiled = null;

    /**
     * Prepares the task for processing.
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
                final String logic = getSubject().getTaskLogic().getLogic();
                final String filename = "<" + getSubject().getKey().toString() + ">";
                compiled = Py.compile_flags(logic, filename, CompileMode.exec, new CompilerFlags());
            }
        } catch (final PyException e) {
            LOGGER.warn("failed to compile Jython code for task " + getSubject().getKey().getId(), e);
            throw new StateMachineException("failed to compile Jython code for task " + getSubject().getKey().getId(),
                    e);
        }

    }

    /**
     * Executes the executor for the task in a sequential manner.
     *
     * @param executionId the execution ID for the current APEX policy execution
     * @param incomingFields the incoming fields
     * @return The outgoing fields
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public Map<String, Object> execute(final long executionId, final Map<String, Object> incomingFields)
            throws StateMachineException, ContextException {

        boolean returnValue = false;

        // Do execution pre work
        executePre(executionId, incomingFields);

        try {

            // Check and execute the Jython logic
            /* Precompiled Version */
            synchronized (Py.class) {
                // Set up the Jython engine
                interpreter.set("executor", getExecutionContext());
                interpreter.exec(compiled);
                returnValue = handleInterpreterResult();
            }
            /* */
        } catch (final Exception e) {
            LOGGER.warn("failed to execute Jython code for task " + getSubject().getKey().getId(), e);
            throw new StateMachineException("failed to execute Jython code for task " + getSubject().getKey().getId(),
                    e);
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
     * Handle the result returned by the interpreter.
     * 
     * @return true if the result was successful
     * @throws StateMachineException on interpreter failures
     */
    private boolean handleInterpreterResult() throws StateMachineException {
        boolean returnValue = false;
        
        try {
            final Object ret = interpreter.get("returnValue", java.lang.Boolean.class);
            if (ret == null) {
                LOGGER.error("execute: task logic failed to set a return value for task  \""
                        + getSubject().getKey().getId() + "\"");
                throw new StateMachineException("execute: task logic failed to set a return value for task  \""
                        + getSubject().getKey().getId() + "\"");
            }
            returnValue = (Boolean) ret;
        } catch (NullPointerException | ClassCastException e) {
            LOGGER.error("execute: task selection logic failed to set a correct return value for state  \""
                    + getSubject().getKey().getId() + "\"", e);
            throw new StateMachineException(
                    "execute: task selection logic failed to set a return value for state  \""
                            + getSubject().getKey().getId() + "\"",
                    e);
        }
        return returnValue;
    }

    /**
     * Cleans up the task after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void cleanUp() throws StateMachineException {
        interpreter.cleanup();
        LOGGER.debug("cleanUp:" + getSubject().getKey().getId() + "," + getSubject().getTaskLogic().getLogicFlavour()
                + "," + getSubject().getTaskLogic().getLogic());
    }
}
