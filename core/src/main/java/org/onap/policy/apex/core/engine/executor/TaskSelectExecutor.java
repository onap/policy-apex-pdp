/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.core.engine.executor;

import static org.onap.policy.common.utils.validation.Assertions.argumentNotNull;

import java.util.Properties;
import lombok.NonNull;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.event.EnEvent;
import org.onap.policy.apex.core.engine.executor.context.TaskSelectionExecutionContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.policymodel.concepts.AxState;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This abstract class executes a the task selection logic of a state of an Apex policy and is specialized by classes
 * that implement execution of task selection logic.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class TaskSelectExecutor implements Executor<EnEvent, AxArtifactKey, AxState, ApexInternalContext> {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TaskSelectExecutor.class);

    // Hold the state and context definitions for this task selector
    private Executor<?, ?, ?, ?> parent = null;
    private AxState axState = null;
    private ApexInternalContext context = null;

    // Holds the incoming event and outgoing task keys
    private EnEvent incomingEvent = null;
    private AxArtifactKey outgoingTaskKey = null;

    // The next task selection executor
    private Executor<EnEvent, AxArtifactKey, AxState, ApexInternalContext> nextExecutor = null;

    // The task selection execution context; contains the facades for events and context to be used
    // by tasks executed by
    // this task selection executor
    private TaskSelectionExecutionContext executionContext;

    /**
     * Gets the execution context.
     *
     * @return the execution context
     */
    protected TaskSelectionExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setContext(final Executor<?, ?, ?, ?> newParent, final AxState newAxState,
            final ApexInternalContext newContext) {
        this.parent = newParent;
        this.axState = newAxState;
        this.context = newContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void prepare() throws StateMachineException {
        LOGGER.debug("prepare:" + axState.getKey().getId() + "," + axState.getTaskSelectionLogic().getLogicFlavour()
                + "," + axState.getTaskSelectionLogic().getLogic());
        argumentNotNull(axState.getTaskSelectionLogic().getLogic(), "task selection logic cannot be null.");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey execute(final long executionId, final Properties executionProperties,
            final EnEvent newIncomingEvent) throws StateMachineException, ContextException {
        throw new StateMachineException("execute() not implemented on class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePre(final long executionId, @NonNull final Properties executionProperties,
            final EnEvent newIncomingEvent) throws StateMachineException {
        LOGGER.debug("execute-pre:" + axState.getKey().getId() + "," + axState.getTaskSelectionLogic().getLogicFlavour()
                + "," + axState.getTaskSelectionLogic().getLogic());

        this.incomingEvent = newIncomingEvent;

        // Initialize the returned task object so it can be set
        outgoingTaskKey = new AxArtifactKey();

        // Get task selection context object
        executionContext = new TaskSelectionExecutionContext(this, executionId, getSubject(), getIncoming(),
                getOutgoing(), getContext());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePost(final boolean returnValue) throws StateMachineException {
        if (!returnValue) {
            String errorMessage = "execute-post: task selection logic failed on state \"" + axState.getKey().getId()
                    + "\"";
            if (executionContext.getMessage() != null) {
                errorMessage += ", user message: " + executionContext.getMessage();
            }
            LOGGER.warn(errorMessage);
            throw new StateMachineException(errorMessage);
        }

        if (outgoingTaskKey == null || AxArtifactKey.getNullKey().getName().equals(outgoingTaskKey.getName())) {
            outgoingTaskKey = axState.getDefaultTask();
            LOGGER.debug("execute-post:" + axState.getKey().getId() + ", returning default task");
            return;
        }

        if (!axState.getTaskReferences().containsKey(outgoingTaskKey)) {
            LOGGER.error("execute-post: task \"" + outgoingTaskKey.getId()
                    + "\" returned by task selection logic not defined on state \"" + axState.getKey().getId() + "\"");
            throw new StateMachineException("task \"" + outgoingTaskKey.getId()
                    + "\" returned by task selection logic not defined on state \"" + axState.getKey().getId() + "\"");
        }

        LOGGER.debug("execute-post:" + axState.getKey().getId() + "," + ", returning task " + outgoingTaskKey.getId());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void cleanUp() throws StateMachineException {
        throw new StateMachineException("cleanUp() not implemented on class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxReferenceKey getKey() {
        return axState.getKey();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Executor<?, ?, ?, ?> getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxState getSubject() {
        return axState;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexInternalContext getContext() {
        return context;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setNext(final Executor<EnEvent, AxArtifactKey, AxState, ApexInternalContext> newNextExecutor) {
        this.nextExecutor = newNextExecutor;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Executor<EnEvent, AxArtifactKey, AxState, ApexInternalContext> getNext() {
        return nextExecutor;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public EnEvent getIncoming() {
        return incomingEvent;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getOutgoing() {
        return outgoingTaskKey;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setParameters(final ExecutorParameters parameters) {
        // Not used
    }
}
