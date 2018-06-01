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

package org.onap.policy.apex.core.engine.executor;

import static org.onap.policy.apex.model.utilities.Assertions.argumentNotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.context.TaskExecutionContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This abstract class executes a task in a state of an Apex policy and is specialized by classes that implement
 * execution of task logic.
 *
 * @author Sven van der Meer (sven.van.der.meer@ericsson.com)
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public abstract class TaskExecutor
        implements Executor<Map<String, Object>, Map<String, Object>, AxTask, ApexInternalContext> {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TaskExecutor.class);

    // Hold the task and context definitions for this task
    private Executor<?, ?, ?, ?> parent = null;
    private AxTask axTask = null;
    private ApexInternalContext internalContext = null;

    // Holds the incoming and outgoing fields
    private Map<String, Object> incomingFields = null;
    private Map<String, Object> outgoingFields = null;

    // The next task executor
    private Executor<Map<String, Object>, Map<String, Object>, AxTask, ApexInternalContext> nextExecutor = null;

    // The task execution context; contains the facades for events and context to be used by tasks executed by this task
    // executor
    private TaskExecutionContext executionContext = null;

    /**
     * Gets the execution internalContext.
     *
     * @return the execution context
     */
    protected TaskExecutionContext getExecutionContext() {
        return executionContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.executor.Executor#setContext(org.onap.policy.apex.core.engine.executor.Executor,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void setContext(final Executor<?, ?, ?, ?> newParent, final AxTask newAxTask,
            final ApexInternalContext newInternalContext) {
        this.parent = newParent;
        this.axTask = newAxTask;
        this.internalContext = newInternalContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#prepare()
     */
    @Override
    public void prepare() throws StateMachineException {
        LOGGER.debug("prepare:" + axTask.getKey().getID() + "," + axTask.getTaskLogic().getLogicFlavour() + ","
                + axTask.getTaskLogic().getLogic());
        argumentNotNull(axTask.getTaskLogic().getLogic(), StateMachineException.class, "task logic cannot be null.");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#execute(java.lang.long, java.lang.Object)
     */
    @Override
    public Map<String, Object> execute(final long executionID, final Map<String, Object> newIncomingFields)
            throws StateMachineException, ContextException {
        throw new StateMachineException(
                "execute() not implemented on abstract TaskExecutor class, only on its subclasses");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#executePre(java.lang.long, java.lang.Object)
     */
    @Override
    public final void executePre(final long executionID, final Map<String, Object> newIncomingFields)
            throws StateMachineException, ContextException {
        LOGGER.debug("execute-pre:" + getSubject().getTaskLogic().getLogicFlavour() + ","
                + getSubject().getKey().getID() + "," + getSubject().getTaskLogic().getLogic());

        // Check that the incoming event has all the input fields for this state
        final Set<String> missingTaskInputFields = new TreeSet<>(axTask.getInputFields().keySet());
        missingTaskInputFields.removeAll(newIncomingFields.keySet());

        // Remove fields from the set that are optional
        for (final Iterator<String> missingFieldIterator = missingTaskInputFields.iterator(); missingFieldIterator
                .hasNext();) {
            final String missingField = missingFieldIterator.next();
            if (axTask.getInputFields().get(missingField).getOptional()) {
                missingTaskInputFields.remove(missingField);
            }
        }
        if (!missingTaskInputFields.isEmpty()) {
            throw new StateMachineException("task input fields \"" + missingTaskInputFields
                    + "\" are missing for task \"" + axTask.getKey().getID() + "\"");
        }

        // Record the incoming fields
        this.incomingFields = newIncomingFields;

        // Initiate the outgoing fields
        outgoingFields = new TreeMap<>();
        for (final String outputFieldName : getSubject().getOutputFields().keySet()) {
            outgoingFields.put(outputFieldName, null);
        }

        // Get task context object
        executionContext =
                new TaskExecutionContext(this, executionID, getSubject(), getIncoming(), getOutgoing(), getContext());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#executePost(boolean)
     */
    @Override
    public final void executePost(final boolean returnValue) throws StateMachineException, ContextException {
        if (!returnValue) {
            String errorMessage = "execute-post: task logic execution failure on task \"" + axTask.getKey().getName()
                    + "\" in model " + internalContext.getKey().getID();
            if (executionContext.getMessage() != null) {
                errorMessage += ", user message: " + executionContext.getMessage();
            }
            LOGGER.warn(errorMessage);
            throw new StateMachineException(errorMessage);
        }

        // Copy any unset fields from the input to the output if their data type and names are identical
        for (final String field : axTask.getOutputFields().keySet()) {
            // Check if the field exists and is not set on the output
            if (!getOutgoing().containsKey(field) || getOutgoing().get(field) != null) {
                continue;
            }

            // This field is not in the output, check if it's on the input and is the same type (Note here, the output
            // field definition has to exist so it's not
            // null checked)
            final AxInputField inputFieldDef = axTask.getInputFields().get(field);
            final AxOutputField outputFieldDef = axTask.getOutputFields().get(field);
            if (inputFieldDef == null || !inputFieldDef.getSchema().equals(outputFieldDef.getSchema())) {
                continue;
            }

            // We have an input field that matches our output field, copy the value across
            getOutgoing().put(field, getIncoming().get(field));
        }

        // Finally, check that the outgoing fields have all the output fields defined for this state and, if not, output
        // a list of missing fields
        final Set<String> missingTaskOutputFields = new TreeSet<>(axTask.getOutputFields().keySet());
        missingTaskOutputFields.removeAll(outgoingFields.keySet());

        // Remove fields from the set that are optional
        for (final Iterator<String> missingFieldIterator = missingTaskOutputFields.iterator(); missingFieldIterator
                .hasNext();) {
            final String missingField = missingFieldIterator.next();
            if (axTask.getInputFields().get(missingField).getOptional()) {
                missingTaskOutputFields.remove(missingField);
            }
        }
        if (!missingTaskOutputFields.isEmpty()) {
            throw new StateMachineException("task output fields \"" + missingTaskOutputFields
                    + "\" are missing for task \"" + axTask.getKey().getID() + "\"");
        }

        // Finally, check that the outgoing field map don't have any extra fields, if present, raise exception with the
        // list of extra fields
        final Set<String> extraTaskOutputFields = new TreeSet<>(outgoingFields.keySet());
        extraTaskOutputFields.removeAll(axTask.getOutputFields().keySet());
        if (!extraTaskOutputFields.isEmpty()) {
            throw new StateMachineException("task output fields \"" + extraTaskOutputFields
                    + "\" are unwanted for task \"" + axTask.getKey().getID() + "\"");
        }

        LOGGER.debug("execute-post:" + axTask.getKey().getID() + ", returning fields " + outgoingFields.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#cleanUp()
     */
    @Override
    public void cleanUp() throws StateMachineException {
        throw new StateMachineException("cleanUp() not implemented on class");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return axTask.getKey();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getParent()
     */
    @Override
    public Executor<?, ?, ?, ?> getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getSubject()
     */
    @Override
    public AxTask getSubject() {
        return axTask;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getContext()
     */
    @Override
    public ApexInternalContext getContext() {
        return internalContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getIncoming()
     */
    @Override
    public Map<String, Object> getIncoming() {
        return incomingFields;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getOutgoing()
     */
    @Override
    public Map<String, Object> getOutgoing() {
        return outgoingFields;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.engine.executor.Executor#setNext(org.onap.policy.apex.core.engine.executor.Executor)
     */
    @Override
    public void setNext(
            final Executor<Map<String, Object>, Map<String, Object>, AxTask, ApexInternalContext> newNextExecutor) {
        this.nextExecutor = newNextExecutor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#getNext()
     */
    @Override
    public Executor<Map<String, Object>, Map<String, Object>, AxTask, ApexInternalContext> getNext() {
        return nextExecutor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.engine.executor.Executor#setParameters(org.onap.policy.apex.core.engine.
     * ExecutorParameters)
     */
    @Override
    public void setParameters(final ExecutorParameters parameters) {}
}
