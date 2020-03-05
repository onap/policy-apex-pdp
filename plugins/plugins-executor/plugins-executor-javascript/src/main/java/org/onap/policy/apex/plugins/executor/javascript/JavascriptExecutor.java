/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
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

import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;

/**
 * The Class JavascriptExecutor is the executor for task logic written in Javascript.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavascriptExecutor {
    public static final int DEFAULT_OPTIMIZATION_LEVEL = 9;

    // Recurring string constants
    private static final String WITH_MESSAGE = " with message: ";

    // The key of the subject that wants to execute Javascript code
    final AxKey subjectKey;

    private Context javascriptContext;
    private Script script;

    /**
     * Initializes the Javascripe executor.
     *
     * @param subjectKey the key of the subject that is requesting Javascript execution
     */
    public JavascriptExecutor(final AxKey subjectKey) {
        this.subjectKey = subjectKey;
    }

    /**
     * Prepares the executor for processing and compiles the Javascript code.
     *
     * @param javascriptCode the Javascript code to execute
     * @throws StateMachineException thrown when instantiation of the executor fails
     */
    public void init(final String javascriptCode) throws StateMachineException {
        if (StringUtils.isEmpty(javascriptCode)) {
            throw new StateMachineException("no logic specified for " + subjectKey.getId());
        }

        try {
            // Create a Javascript context for this thread
            javascriptContext = Context.enter();

            // Set up the default values of the context
            javascriptContext.setOptimizationLevel(DEFAULT_OPTIMIZATION_LEVEL);
            javascriptContext.setLanguageVersion(Context.VERSION_1_8);

            script = javascriptContext.compileString(javascriptCode, subjectKey.getId(), 1, null);
        } catch (Exception e) {
            Context.exit();
            throw new StateMachineException(
                    "logic failed to compile for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
        }
    }

    /**
     * Executes the the Javascript code.
     *
     * @param executionContext the execution context of the subject to be passed to the Javascript context
     * @return true if the Javascript executed properly
     * @throws StateMachineException thrown when Javascript execution fails
     */
    public boolean execute(final Object executionContext) throws StateMachineException {
        Object returnObject = null;

        try {
            // Pass the subject context to the Javascript engine
            Scriptable javascriptScope = javascriptContext.initStandardObjects();
            javascriptScope.put("executor", javascriptScope, executionContext);

            // Run the script
            returnObject = script.exec(javascriptContext, javascriptScope);
        } catch (final Exception e) {
            throw new StateMachineException(
                    "logic failed to run for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
        }

        if (!(returnObject instanceof Boolean)) {
            throw new StateMachineException(
                    "execute: logic for " + subjectKey.getId() + " returned a non-boolean value " + returnObject);
        }

        return (boolean) returnObject;
    }

    /**
     * Cleans up the executor after processing.
     *
     * @throws StateMachineException thrown when cleanup of the executor fails
     */
    public void cleanUp() throws StateMachineException {
        try {
            Context.exit();
        } catch (final Exception e) {
            throw new StateMachineException("cleanUp: executor cleanup failed to close for " + subjectKey.getId()
                    + WITH_MESSAGE + e.getMessage(), e);
        }
    }
}
