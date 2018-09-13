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

package org.onap.policy.apex.plugins.executor.javascript;

import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JavascriptStateFinalizerExecutor is the state finalizer executor for state finalizer logic written in
 * Javascript It is unlikely that this is thread safe.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavascriptStateFinalizerExecutor extends StateFinalizerExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavascriptStateFinalizerExecutor.class);

    // Javascript engine
    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    private CompiledScript compiled = null;

    /**
     * Prepares the state finalizer for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();
        try {
            compiled = ((Compilable) engine).compile(getSubject().getLogic());
        } catch (final ScriptException e) {
            LOGGER.error("execute: state finalizer logic failed to compile for state finalizer  \""
                    + getSubject().getKey().getId() + "\"");
            throw new StateMachineException("state finalizer logic failed to compile for state finalizer  \""
                    + getSubject().getKey().getId() + "\"", e);
        }
    }

    /**
     * Executes the executor for the state finalizer logic in a sequential manner.
     *
     * @param executionId the execution ID for the current APEX policy execution
     * @param incomingFields the incoming fields for finalisation
     * @return The state output for the state
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public String execute(final long executionId, final Map<String, Object> incomingFields)
            throws StateMachineException, ContextException {
        // Do execution pre work
        executePre(executionId, incomingFields);

        // Set up the Javascript engine
        engine.put("executor", getExecutionContext());

        // Check and execute the Javascript logic
        boolean returnValue = false;
        try {
            if (compiled == null) {
                engine.eval(getSubject().getLogic());
            } else {
                compiled.eval(engine.getContext());
            }
        } catch (final ScriptException e) {
            LOGGER.error("execute: state finalizer logic failed to run for state finalizer  \""
                    + getSubject().getKey().getId() + "\"");
            throw new StateMachineException("state finalizer logic failed to run for state finalizer  \""
                    + getSubject().getKey().getId() + "\"", e);
        }

        returnValue = (boolean) engine.get("returnValue");

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
        LOGGER.debug("cleanUp:" + getSubject().getKey().getId() + "," + getSubject().getLogicFlavour() + ","
                + getSubject().getLogic());
        engine = null;
    }
}
