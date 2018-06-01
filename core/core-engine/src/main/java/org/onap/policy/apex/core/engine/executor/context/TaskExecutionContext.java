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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.Executor;
import org.onap.policy.apex.core.engine.executor.TaskExecutor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Container class for the execution context for Task logic executions in a task being executed in an Apex engine. The
 * task must have easy access to the task definition, the incoming and outgoing field contexts, as well as the policy,
 * global, and external context.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class TaskExecutionContext {
    // Logger for task execution
    private static final XLogger EXECUTION_LOGGER =
            XLoggerFactory.getXLogger("org.onap.policy.apex.executionlogging.TaskExecutionLogging");

    // CHECKSTYLE:OFF: checkstyle:VisibilityModifier Logic has access to these field

    /** A constant <code>boolean true</code> value available for reuse e.g., for the return value */
    public final Boolean TRUE = true;

    /** A constant <code>boolean false</code> value available for reuse e.g., for the return value */
    public final Boolean FALSE = false;

    /** A facade to the full task definition for the task logic being executed. */
    public final AxTaskFacade subject;

    /** the execution ID for the current APEX policy execution instance. */
    public final Long executionID;

    /**
     * The incoming fields from the trigger event for the task. The task logic can access these fields when executing
     * its logic.
     */
    public final Map<String, Object> inFields;

    /**
     * The outgoing fields from the task. The task logic can access and set these fields with its logic. A task outputs
     * its result using these fields.
     */
    public final Map<String, Object> outFields;

    /** Logger for task execution, task logic can use this field to access and log to Apex logging. */
    public final XLogger logger = EXECUTION_LOGGER;

    // CHECKSTYLE:ON: checkstyle:VisibilityModifier

    // All available context albums
    private final Map<String, ContextAlbum> context;

    // The artifact stack of users of this context
    private final List<AxConcept> usedArtifactStack;

    // A message specified in the logic
    private String message;

    /**
     * Instantiates a new task execution context.
     *
     * @param taskExecutor the task executor that requires context
     * @param executionID the execution ID for the current APEX policy execution instance
     * @param axTask the task definition that is the subject of execution
     * @param inFields the in fields
     * @param outFields the out fields
     * @param internalContext the execution context of the Apex engine in which the task is being executed
     */
    public TaskExecutionContext(final TaskExecutor taskExecutor, final long executionID, final AxTask axTask,
            final Map<String, Object> inFields, final Map<String, Object> outFields,
            final ApexInternalContext internalContext) {
        // The subject is the task definition
        subject = new AxTaskFacade(axTask);

        // Execution ID is the current policy execution instance
        this.executionID = executionID;

        // The input and output fields
        this.inFields = Collections.unmodifiableMap(inFields);
        this.outFields = outFields;

        // Set up the context albums for this task
        context = new TreeMap<>();
        for (final AxArtifactKey mapKey : subject.task.getContextAlbumReferences()) {
            context.put(mapKey.getName(), internalContext.getContextAlbums().get(mapKey));
        }

        // Get the artifact stack of the users of the policy
        usedArtifactStack = new ArrayList<>();
        for (Executor<?, ?, ?, ?> parent = taskExecutor.getParent(); parent != null; parent = parent.getParent()) {
            // Add each parent to the top of the stack
            usedArtifactStack.add(0, parent.getKey());
        }

        // Change the stack to an array
        final AxConcept[] usedArtifactStackArray = usedArtifactStack.toArray(new AxConcept[usedArtifactStack.size()]);

        // Set the user of the context
        for (final ContextAlbum contextAlbum : context.values()) {
            contextAlbum.setUserArtifactStack(usedArtifactStackArray);
        }
    }

    /**
     * Return a context album if it exists in the context definition of this task.
     *
     * @param contextAlbumName The context album name
     * @return The context album
     * @throws ContextRuntimeException if the context album does not exist on the task for this executor
     */
    public ContextAlbum getContextAlbum(final String contextAlbumName) throws ContextRuntimeException {
        // Find the context album
        final ContextAlbum foundContextAlbum = context.get(contextAlbumName);

        // Check if the context album exists
        if (foundContextAlbum != null) {
            return foundContextAlbum;
        } else {
            throw new ContextRuntimeException("cannot find definition of context album \"" + contextAlbumName
                    + "\" on task \"" + subject.getId() + "\"");
        }
    }

    /**
     * Get the user message.
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
