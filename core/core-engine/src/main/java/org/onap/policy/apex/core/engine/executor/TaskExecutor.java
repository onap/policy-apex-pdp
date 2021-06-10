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

package org.onap.policy.apex.core.engine.executor;

import static org.onap.policy.common.utils.validation.Assertions.argumentOfClassNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Getter;
import lombok.NonNull;
import org.onap.policy.apex.context.ContextException;
import org.onap.policy.apex.core.engine.ExecutorParameters;
import org.onap.policy.apex.core.engine.TaskParameters;
import org.onap.policy.apex.core.engine.context.ApexInternalContext;
import org.onap.policy.apex.core.engine.executor.context.TaskExecutionContext;
import org.onap.policy.apex.core.engine.executor.exception.StateMachineException;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;
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
        implements Executor<Map<String, Object>, Map<String, Map<String, Object>>, AxTask, ApexInternalContext> {
    // Logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TaskExecutor.class);

    // Hold the task and context definitions for this task
    private Executor<?, ?, ?, ?> parent = null;
    private AxTask axTask = null;
    private ApexInternalContext internalContext = null;

    // Holds the incoming and outgoing fields
    private Map<String, Object> incomingFields = null;
    private Map<String, Map<String, Object>> outgoingFieldsMap = null;

    // The next task executor
    private Executor<Map<String, Object>, Map<String, Map<String, Object>>, AxTask, ApexInternalContext> nextExecutor = null;

    // The task execution context; contains the facades for events and context to be used by tasks
    // executed by this task
    // executor
    @Getter
    private TaskExecutionContext executionContext = null;

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setContext(final Executor<?, ?, ?, ?> newParent, final AxTask newAxTask,
            final ApexInternalContext newInternalContext) {
        this.parent = newParent;
        this.axTask = newAxTask;
        this.internalContext = newInternalContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void prepare() throws StateMachineException {
        LOGGER.debug("prepare:" + axTask.getKey().getId() + "," + axTask.getTaskLogic().getLogicFlavour() + ","
                + axTask.getTaskLogic().getLogic());
        argumentOfClassNotNull(axTask.getTaskLogic().getLogic(), StateMachineException.class,
                "task logic cannot be null.");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Map<String, Object>> execute(final long executionId, final Properties executionProperties,
            final Map<String, Object> newIncomingFields) throws StateMachineException, ContextException {
        throw new StateMachineException(
                "execute() not implemented on abstract TaskExecutor class, only on its subclasses");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePre(final long executionId, @NonNull final Properties executionProperties,
            final Map<String, Object> newIncomingFields) throws StateMachineException, ContextException {
        LOGGER.debug("execute-pre:" + getSubject().getTaskLogic().getLogicFlavour() + ","
                + getSubject().getKey().getId() + "," + getSubject().getTaskLogic().getLogic());

        // Check that the incoming event has all the input fields for this state
        Map<String, AxField> inputEventParameterMap = axTask.getInputEvent().getParameterMap();
        final Set<String> missingTaskInputFields = new TreeSet<>(inputEventParameterMap.keySet());
        missingTaskInputFields.removeAll(newIncomingFields.keySet());

        // Remove fields from the set that are optional
        missingTaskInputFields.removeIf(missingField -> inputEventParameterMap.get(missingField).getOptional());

        if (!missingTaskInputFields.isEmpty()) {
            throw new StateMachineException("task input fields \"" + missingTaskInputFields
                    + "\" are missing for task \"" + axTask.getKey().getId() + "\"");
        }

        // Record the incoming fields
        this.incomingFields = newIncomingFields;

        // Initiate the outgoing fields
        outgoingFieldsMap = new TreeMap<>();
        axTask.getOutputEvents().entrySet().forEach(outputEventEntry -> {
            Map<String, Object> outgoingFields = new TreeMap<>();
            outputEventEntry.getValue().getParameterMap().keySet().forEach(field -> outgoingFields.put(field, null));
            outgoingFieldsMap.put(outputEventEntry.getKey(), outgoingFields);
        });
        // Get task context object
        executionContext = new TaskExecutionContext(this, executionId, executionProperties, getSubject(), getIncoming(),
            outgoingFieldsMap.values(), getContext());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public final void executePost(final boolean returnValue) throws StateMachineException, ContextException {
        if (!returnValue) {
            String errorMessage = "execute-post: task logic execution failure on task \"" + axTask.getKey().getName()
                    + "\" in model " + internalContext.getKey().getId();
            if (executionContext.getMessage() != null) {
                errorMessage += ", user message: " + executionContext.getMessage();
            }
            LOGGER.warn(errorMessage);
            throw new StateMachineException(errorMessage);
        }

        // Copy any unset fields from the input to the output if their data type and names are
        // identical
        axTask.getOutputEvents().entrySet().forEach(outputEventEntry -> outputEventEntry.getValue().getParameterMap()
            .keySet().forEach(field -> copyInputField2Output(outputEventEntry.getKey(), field)));

        // Finally, check that the outgoing fields have all the output fields defined for this state
        // and, if not, output
        // a list of missing fields
        Map<String, Set<String>> missingTaskOutputFieldsMap = new TreeMap<>();
        axTask.getOutputEvents().entrySet().forEach(outputEventEntry -> {
            Set<String> missingTaskOutputFields = new TreeSet<>();
            missingTaskOutputFields.addAll(outputEventEntry.getValue().getParameterMap().keySet());
            String key = outputEventEntry.getKey();
            missingTaskOutputFields.removeAll(outgoingFieldsMap.get(key).keySet());
            missingTaskOutputFieldsMap.put(key, missingTaskOutputFields);
        });

        // Remove fields from the set that are optional
        missingTaskOutputFieldsMap.entrySet().forEach(missingTaskOutputFieldsEntry -> {
            missingTaskOutputFieldsEntry.getValue()
                .removeIf(missingField -> axTask.getInputEvent().getParameterMap().containsKey(missingField)
                    || axTask.getOutputEvents().get(missingTaskOutputFieldsEntry.getKey()).getParameterMap()
                        .get(missingField).getOptional());
        });
        missingTaskOutputFieldsMap.entrySet()
            .removeIf(missingTaskOutputFieldsEntry -> missingTaskOutputFieldsEntry.getValue().isEmpty());
        if (!missingTaskOutputFieldsMap.isEmpty()) {
            throw new StateMachineException("Fields for task output events \"" + missingTaskOutputFieldsMap.keySet()
                + "\" are missing for task \"" + axTask.getKey().getId() + "\"");
        }

        // Finally, check that the outgoing field map don't have any extra fields, if present, raise
        // exception with the
        // list of extra fields
        final Map<String, Set<String>> extraTaskOutputFieldsMap = new TreeMap<>();
        outgoingFieldsMap.entrySet().forEach(outgoingFieldsEntry -> extraTaskOutputFieldsMap
            .put(outgoingFieldsEntry.getKey(), new TreeSet<String>(outgoingFieldsEntry.getValue().keySet())));
        extraTaskOutputFieldsMap.entrySet().forEach(extraTaskOutputFieldsEntry -> extraTaskOutputFieldsEntry.getValue()
            .removeAll(axTask.getOutputEvents().get(extraTaskOutputFieldsEntry.getKey()).getParameterMap().keySet()));
        extraTaskOutputFieldsMap.entrySet()
            .removeIf(extraTaskOutputFieldsEntry -> extraTaskOutputFieldsEntry.getValue().isEmpty());
        if (!extraTaskOutputFieldsMap.isEmpty()) {
            throw new StateMachineException("task output event \"" + extraTaskOutputFieldsMap.keySet()
                + "\" contains fields that are unwanted for task \"" + axTask.getKey().getId() + "\"");
        }

        String message =
            "execute-post:" + axTask.getKey().getId() + ", returning fields " + outgoingFieldsMap.toString();
        LOGGER.debug(message);
    }

    /**
     * If the input field exists on the output and it is not set in the task, then it should be copied to the output.
     *
     * @param field the input field
     */
    private void copyInputField2Output(String eventName, String field) {
        // Check if the field exists and is not set on the output
        if (outgoingFieldsMap.get(eventName).containsKey(field)
            && outgoingFieldsMap.get(eventName).get(field) != null) {
            return;
        }

        // This field is not in the output, check if it's on the input and is the same type
        // (Note here, the output
        // field definition has to exist so it's not
        // null checked)
        final AxField inputFieldDef = axTask.getInputEvent().getParameterMap().get(field);
        final AxField outputFieldDef = axTask.getOutputEvents().get(eventName).getParameterMap().get(field);
        if (inputFieldDef == null || !inputFieldDef.getSchema().equals(outputFieldDef.getSchema())) {
            return;
        }

        // We have an input field that matches our output field, copy the value across
        outgoingFieldsMap.get(eventName).put(field, getIncoming().get(field));
    }

    /**
     * If taskParameters are provided in ApexConfig, then they will be updated in the Tasks.
     * If taskId is empty, the task parameter is added/updated to all available tasks
     * Otherwise, the task parameter is added/updated to the corresponding task only.
     *
     * @param taskParametersFromConfig the list of task parameters provided in ApexConfig during deployment
     */
    public void updateTaskParameters(List<TaskParameters> taskParametersFromConfig) {
        Map<String, AxTaskParameter> taskParameters = getSubject().getTaskParameters();
        if (null == taskParameters) {
            taskParameters = new HashMap<>();
        }
        for (TaskParameters taskParameterFromConfig : taskParametersFromConfig) {
            if (null == taskParameterFromConfig.getTaskId()
                || getSubject().getId().equals(taskParameterFromConfig.getTaskId())) {
                taskParameters.put(taskParameterFromConfig.getKey(),
                    new AxTaskParameter(new AxReferenceKey(), taskParameterFromConfig.getValue()));
            }
        }
        getSubject().setTaskParameters(taskParameters);
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
    public AxArtifactKey getKey() {
        return axTask.getKey();
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
    public AxTask getSubject() {
        return axTask;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public ApexInternalContext getContext() {
        return internalContext;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Object> getIncoming() {
        return incomingFields;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Map<String, Map<String, Object>> getOutgoing() {
        return outgoingFieldsMap;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setNext(final Executor<Map<String, Object>, Map<String, Map<String, Object>>, AxTask, ApexInternalContext> nextEx) {
        this.nextExecutor = nextEx;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Executor<Map<String, Object>, Map<String, Map<String, Object>>, AxTask, ApexInternalContext> getNext() {
        return nextExecutor;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void setParameters(final ExecutorParameters parameters) {
        // Not used
    }
}
