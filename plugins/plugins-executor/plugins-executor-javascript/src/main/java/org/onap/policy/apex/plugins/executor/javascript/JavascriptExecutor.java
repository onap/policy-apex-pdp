/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2020 Nordix Foundation.
 * Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
    private final AxKey subjectKey;

    private final Script script;

    /**
     * Initializes the Javascript executor.
     *
     * @param subjectKey the key of the subject that is requesting Javascript execution
     * @param javascriptCode the Javascript code to execute
     */
    public JavascriptExecutor(final AxKey subjectKey, String javascriptCode)  throws StateMachineException {
        if (StringUtils.isBlank(javascriptCode)) {
            throw new StateMachineException("no logic specified for " + subjectKey.getId());
        }
        this.subjectKey = subjectKey;
        this.script = compile(subjectKey.getId(), javascriptCode);
    }

    /**
     * Executes the Javascript code.
     *
     * @param executionContext the execution context of the subject to be passed to the Javascript context
     * @return true if the Javascript executed properly
     * @throws StateMachineException thrown when Javascript execution fails
     */
    public boolean execute(final Object executionContext) throws StateMachineException {
        Object returnObject = null;

        var context = Context.enter();
        try {
            // Pass the subject context to the Javascript engine
            Scriptable javascriptScope = context.initStandardObjects();
            javascriptScope.put("executor", javascriptScope, executionContext);

            // Run the script
            returnObject = script.exec(context, javascriptScope);
        } catch (final Exception e) {
            throw new StateMachineException(
                    "logic failed to run for " + subjectKey.getId() + WITH_MESSAGE + e.getMessage(), e);
        } finally {
            Context.exit();
        }

        if (!(returnObject instanceof Boolean)) {
            throw new StateMachineException(
                    "execute: logic for " + subjectKey.getId() + " returned a non-boolean value " + returnObject);
        }

        return (boolean) returnObject;
    }

    private Script compile(String id, String javascriptCode) throws StateMachineException {
        var context = Context.enter();
        try {
            // Set up the default values of the context
            context.setOptimizationLevel(DEFAULT_OPTIMIZATION_LEVEL);
            context.setLanguageVersion(Context.VERSION_1_8);
            return context.compileString(javascriptCode, id, 1, null);
        } catch (Exception e) {
            throw new StateMachineException(
                "logic failed to compile for " + id + WITH_MESSAGE + e.getMessage(), e);
        } finally {
            Context.exit();
        }
    }
}
