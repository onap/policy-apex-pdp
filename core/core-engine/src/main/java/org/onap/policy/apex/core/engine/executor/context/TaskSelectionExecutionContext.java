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

package org.onap.policy.apex.core.engine.executor.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.Executor;
import org.onap.policy.apex.core.engine.executor.TaskSelectExecutor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Container class for the execution context for Task Selection logic executions in a task being
 * executed in an Apex engine. The task must have easy access to the state definition, the incoming
 * and outgoing event contexts, as well as the policy, global, and external context.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class TaskSelectionExecutionContext {
    // Logger for task execution
    private static final XLogger EXECUTION_LOGGER =
            XLoggerFactory.getXLogger("org.onap.policy.apex.executionlogging.TaskSelectionExecutionLogging");

    // CHECKSTYLE:OFF: checkstyle:VisibilityModifier Logic has access to these field

    /** A constant <code>boolean true</code> value available for reuse e.g., for the return value */
    public final Boolean isTrue = true;

    /**
     * A constant <code>boolean false</code> value available for reuse e.g., for the return value
     */
    public final Boolean isFalse = false;

    /** A facade to the full state definition for the task selection logic being executed. */
    public final AxStateFacade subject;

    /** the execution ID for the current APEX policy execution instance. */
    public final Long executionId;

    /**
     * The incoming fields from the trigger event for the state. The task selection logic can access
     * these fields to decide what task to select for the state.
     */
    public final Map<String, Object> inFields;

    /**
     * The task that the task selection logic has selected for a state. The task selection logic
     * sets this field in its logic prior to executing and the Apex engine executes this task as the
     * task for this state.
     */
    public AxArtifactKey selectedTask;

    /**
     * Logger for task selection execution, task selection logic can use this field to access and
     * log to Apex logging.
     */
    public final XLogger logger = EXECUTION_LOGGER;

    // CHECKSTYLE:ON: checkstyle:VisibilityModifier

    // All available context albums
    private final Map<String, ContextAlbum> context;

    // A message specified in the logic
    private String message;

    /**
     * Instantiates a new task selection execution context.
     *
     * @param taskSelectExecutor the task selection executor that requires context
     * @param executionId the execution identifier
     * @param axState the state definition that is the subject of execution
     * @param incomingEvent the incoming event for the state
     * @param outgoingKey the outgoing key for the task to execute in this state
     * @param internalContext the execution context of the Apex engine in which the task is being
     *        executed
     */
    public TaskSelectionExecutionContext(final TaskSelectExecutor taskSelectExecutor, final long executionId,
            final AxState axState, final EnEvent incomingEvent, final AxArtifactKey outgoingKey,
            final ApexInternalContext internalContext) {
        // The subject is the state definition
        subject = new AxStateFacade(axState);

        // Execution ID is the current policy execution instance
        this.executionId = executionId;

        // The events
        inFields = incomingEvent;
        selectedTask = outgoingKey;

        // Set up the context albums for this task
        // Set up the context albums for this task
        context = new TreeMap<>();
        for (final AxArtifactKey mapKey : subject.state.getContextAlbumReferences()) {
            context.put(mapKey.getName(), internalContext.getContextAlbums().get(mapKey));
        }

        // Get the artifact stack of the users of the policy
        final List<AxConcept> usedArtifactStack = new ArrayList<>();
        for (Executor<?, ?, ?, ?> parent = taskSelectExecutor.getParent(); parent != null; parent =
                parent.getParent()) {
            // Add each parent to the top of the stack
            usedArtifactStack.add(0, parent.getKey());
        }

        // Add the events to the artifact stack
        usedArtifactStack.add(incomingEvent.getKey());

        // Change the stack to an array
        final AxConcept[] usedArtifactStackArray = usedArtifactStack.toArray(new AxConcept[usedArtifactStack.size()]);

        // Set the user of the context
        // Set the user of the context
        for (final ContextAlbum contextAlbum : context.values()) {
            contextAlbum.setUserArtifactStack(usedArtifactStackArray);
        }
        incomingEvent.setUserArtifactStack(usedArtifactStackArray);
    }

    /**
     * Return a context album if it exists in the context definition of this state.
     *
     * @param contextAlbumName The context album name
     * @return The context albumxxxxxx
     * @throws ContextRuntimeException if the context album does not exist on the state for this
     *         executor
     */
    public ContextAlbum getContextAlbum(final String contextAlbumName) {
        // Find the context album
        final ContextAlbum foundContextAlbum = context.get(contextAlbumName);

        // Check if the context album exists
        if (foundContextAlbum != null) {
            return foundContextAlbum;
        } else {
            throw new ContextRuntimeException("cannot find definition of context album \"" + contextAlbumName
                    + "\" on state \"" + subject.getId() + "\"");
        }
    }

    /**
     * Gets the user message.
     *
     * @return the user message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the user message.
     *
     * @param message the message
     */
    public void setMessage(final String message) {
        this.message = message;
    }
}
