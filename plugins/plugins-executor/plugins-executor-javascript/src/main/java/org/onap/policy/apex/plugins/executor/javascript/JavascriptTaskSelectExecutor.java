/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

import java.util.Properties;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JavascriptTaskSelectExecutor is the task selection executor for task selection logic written in Javascript
 * It is unlikely that this is thread safe.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JavascriptTaskSelectExecutor extends TaskSelectExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JavascriptTaskSelectExecutor.class);

    // Javascript context
    private Context jsContext;

    /**
     * Prepares the task selection logic for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();

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
            throw new StateMachineException("prepare: javascript engine failed to initialize properly for state \""
                    + getSubject().getKey().getId() + "\"", e);
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

        try {
            // Set up the Javascript engine context
            jsContext.getBindings("js").putMember("executor", getExecutionContext());
            jsContext.eval("js", getSubject().getTaskSelectionLogic().getLogic());

        } catch (final Exception e) {
            throw new StateMachineException(
                    "execute: task selection logic failed to run for state \"" + getSubject().getKey().getId() + "\"",
                    e);
        }

        Value returnValue = jsContext.getBindings("js").getMember("returnValue");

        if (returnValue == null || returnValue.isNull()) {
            throw new StateMachineException("execute: task selection logic failed to set a return value for state \""
                    + getSubject().getKey().getId() + "\"");
        }

        // Do the execution post work
        executePost(returnValue.asBoolean());

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

        try {
            jsContext.close();
        } catch (final Exception e) {
            throw new StateMachineException("cleanUp: task selection executor failed to close for state \""
                    + getSubject().getKey().getId() + "\"", e);
        }
    }
}
