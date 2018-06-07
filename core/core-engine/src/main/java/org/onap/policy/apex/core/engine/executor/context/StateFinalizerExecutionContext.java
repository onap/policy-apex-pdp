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
import java.util.Set;
import java.util.TreeMap;

import org.onap.policy.apex.context.ContextAlbum;
import org.onap.policy.apex.context.ContextRuntimeException;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.Executor;
import org.onap.policy.apex.core.engine.executor.StateFinalizerExecutor;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Container class for the execution context for state finalizer logic executions in a state being executed in an Apex
 * engine. The state finalizer must have easy access to the state definition, the fields, as well as the policy, global,
 * and external context.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 */
public class StateFinalizerExecutionContext {
    /**
     * Logger for state finalizer execution, state finalizer logic can use this field to access and log to Apex logging.
     */
    private static final XLogger EXCEUTION_LOGGER =
            XLoggerFactory.getXLogger("org.onap.policy.apex.executionlogging.StateFinalizerExecutionLogging");

    // CHECKSTYLE:OFF: checkstyle:VisibilityModifier Logic has access to these field

    /** A facade to the full state definition for the state finalizer logic being executed. */
    public final AxStateFacade subject;

    /** the execution ID for the current APEX policy execution instance. */
    public final Long executionID;

    /**
     * The list of state outputs for this state finalizer. The purpose of a state finalizer is to select a state output
     * for a state from this list of state output names.
     */
    public final Set<String> stateOutputNames;

    /**
     * The fields of this state finalizer. A state finalizer receives this list of fields from a task and may use these
     * fields to determine what state output to select. Once a state finalizer has selected a state output, it must
     * marshal these fields so that they match the fields required for the event defined in the state output.
     */
    public final Map<String, Object> fields;

    // A message specified in the logic
    private String message;

    /**
     * The state output that the state finalizer logic has selected for a state. The state finalizer logic sets this
     * field in its logic after executing and the Apex engine uses this state output for this state.
     */
    private String selectedStateOutputName;

    /**
     * Logger for state finalizer execution, state finalizer logic can use this field to access and log to Apex logging.
     */
    public final XLogger logger = EXCEUTION_LOGGER;

    // CHECKSTYLE:ON: checkstyle:visibilityModifier

    // All available context albums
    private final Map<String, ContextAlbum> context;

    /**
     * Instantiates a new state finalizer execution context.
     *
     * @param stateFinalizerExecutor the state finalizer executor that requires context
     * @param executionID the execution ID for the current APEX policy execution instance
     * @param axState the state definition that is the subject of execution
     * @param fields the fields to be manipulated by the state finalizer
     * @param stateOutputNames the state output names, one of which will be selected by the state finalizer
     * @param internalContext the execution context of the Apex engine in which the task is being executed
     */
    public StateFinalizerExecutionContext(final StateFinalizerExecutor stateFinalizerExecutor, final long executionID,
            final AxState axState, final Map<String, Object> fields, final Set<String> stateOutputNames,
            final ApexInternalContext internalContext) {
        subject = new AxStateFacade(axState);

        // Execution ID is the current policy execution instance
        this.executionID = executionID;

        this.fields = fields;
        this.stateOutputNames = stateOutputNames;

        // Set up the context albums for this task
        context = new TreeMap<>();
        for (final AxArtifactKey mapKey : subject.state.getContextAlbumReferences()) {
            context.put(mapKey.getName(), internalContext.getContextAlbums().get(mapKey));
        }

        // Get the artifact stack of the users of the policy
        final List<AxConcept> usedArtifactStack = new ArrayList<>();
        for (Executor<?, ?, ?, ?> parent = stateFinalizerExecutor.getParent(); parent != null; parent =
                parent.getParent()) {
            // Add each parent to the top of the stack
            usedArtifactStack.add(0, parent.getKey());
        }

        // Change the stack to an array
        final AxConcept[] usedArtifactStackArray = usedArtifactStack.toArray(new AxConcept[usedArtifactStack.size()]);

        // Set the user of the context
        // Set the user of the context
        for (final ContextAlbum contextAlbum : context.values()) {
            contextAlbum.setUserArtifactStack(usedArtifactStackArray);
        }
    }

    /**
     * Return a context album if it exists in the context definition of this state.
     *
     * @param contextAlbumName The context album name
     * @return The context album
     * @throws ContextRuntimeException if the context album does not exist on the state for this executor
     */
    public ContextAlbum getContextAlbum(final String contextAlbumName) throws ContextRuntimeException {
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
     * Return the state output name selected by the state finalizer logic.
     *
     * @return the state output name
     */
    public String getSelectedStateOutputName() {
        return selectedStateOutputName;
    }

    /**
     * Set the state output name selected by the state finalizer logic.
     *
     * @param selectedStateOutputName the state output name
     */
    public void setSelectedStateOutputName(final String selectedStateOutputName) {
        this.selectedStateOutputName = selectedStateOutputName;
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
