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

package org.onap.policy.apex.plugins.executor.jruby;

import java.util.Set;
import java.util.TreeSet;

import org.jruby.embed.EmbedEvalUnit;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.jruby.runtime.builtin.IRubyObject;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * The Class JrubyTaskSelectExecutor is the task selection executor for task selection logic written in JRuby.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class JrubyTaskSelectExecutor extends TaskSelectExecutor {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(JrubyTaskSelectExecutor.class);

    // Jruby container
    private ScriptingContainer container =
            new ScriptingContainer(LocalContextScope.CONCURRENT, LocalVariableBehavior.TRANSIENT, true);
    private EmbedEvalUnit parsedjruby = null;

    /**
     * Prepares the task for processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void prepare() throws StateMachineException {
        // Call generic prepare logic
        super.prepare();

        // Set up the JRuby engine
        container = (container == null)
                ? new ScriptingContainer(LocalContextScope.CONCURRENT, LocalVariableBehavior.TRANSIENT, true)
                : container;
        container.put("executor", getExecutionContext()); // needed for compile as a placeholder
        parsedjruby = container.parse(getSubject().getTaskSelectionLogic().getLogic());
    }

    /**
     * Executes the executor for the task in a sequential manner.
     *
     * @param executionId the execution ID for the current APEX policy execution
     * @param incomingEvent the incoming event
     * @return The outgoing event
     * @throws StateMachineException on an execution error
     * @throws ContextException on context errors
     */
    @Override
    public AxArtifactKey execute(final long executionId, final EnEvent incomingEvent)
            throws StateMachineException, ContextException {
        // Do execution pre work
        executePre(executionId, incomingEvent);

        // Check and execute the JRuby logic
        container.put("executor", getExecutionContext());

        /* Precompiled version */
        boolean returnValue = false;
        final IRubyObject ret = parsedjruby.run();
        if (ret != null) {
            final Boolean retbool = (Boolean) ret.toJava(java.lang.Boolean.class);
            if (retbool != null) {
                returnValue = true;
            }
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
     * Cleans up the task after processing.
     *
     * @throws StateMachineException thrown when a state machine execution error occurs
     */
    @Override
    public void cleanUp() throws StateMachineException {
        LOGGER.debug("cleanUp:" + getSubject().getKey().getId() + ","
                + getSubject().getTaskSelectionLogic().getLogicFlavour() + ","
                + getSubject().getTaskSelectionLogic().getLogic());
        container.terminate();
        container = null;
    }

    /**
     * Gets the output event set.
     *
     * @return the output event set
     */
    public Set<AxArtifactKey> getOutputEventSet() {
        return new TreeSet<>();
    }
}
