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

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;

/**
 * The Class JavascriptExecutor is the executor for task logic written in Javascript.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavascriptExecutor {
    // The key of the subject that wants to execute Javascript code
    final AxKey subjectKey;

    // The Javascript context
    private final Context jsContext;

    /**
     * Prepares the executor for processing.
     *
     * @param subjectKey the key of the subject that is requesting Javascript execution
     * @throws StateMachineException thrown when instantiation of the executor fails
     */
    public JavascriptExecutor(final AxKey subjectKey) throws StateMachineException {
        this.subjectKey = subjectKey;

        // @formatter:off
        jsContext =
                Context.newBuilder("js")
                .allowHostClassLookup(s -> true)
                .allowHostAccess(HostAccess.ALL)
                .build();
        // @formatter:on

        try {
            jsContext.getBindings("js");
        } catch (Exception e) {
            jsContext.close();
            throw new StateMachineException(
                    "prepare: javascript engine failed to initialize properly for \"" + subjectKey.getId() + "\"", e);
        }
    }

    /**
     * Executes the the Javascript code.
     *
     * @param executionContext the execution context of the subject to be passed to the Javascript context
     * @param javascriptCode the Javascript code to execute
     * @return true if the Javascript executed properly
     * @throws StateMachineException thrown when Javascript execution fails
     */
    public boolean execute(final Object executionContext, final String javascriptCode) throws StateMachineException {
        try {
            // Set up the Javascript engine context
            jsContext.getBindings("js").putMember("executor", executionContext);
            jsContext.eval("js", javascriptCode);

        } catch (final Exception e) {
            throw new StateMachineException("execute: logic failed to run for \"" + subjectKey.getId() + "\"", e);
        }

        Value returnValue = jsContext.getBindings("js").getMember("returnValue");

        if (returnValue == null || returnValue.isNull()) {
            throw new StateMachineException(
                    "execute: logic failed to set a return value for \"" + subjectKey.getId() + "\"");
        }

        return returnValue.asBoolean();
    }

    /**
     * Cleans up the executor after processing.
     *
     * @throws StateMachineException thrown when cleanup of the executor fails
     */
    public void cleanUp() throws StateMachineException {
        try {
            jsContext.close();
        } catch (final Exception e) {
            throw new StateMachineException(
                    "cleanUp: executor cleanup failed to close for \"" + subjectKey.getId() + "\"", e);
        }
    }
}
