/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.client.editor.rest.handling;

import java.util.Map.Entry;

import org.onap.policy.apex.client.editor.rest.handling.bean.BeanField;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanKeyRef;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanLogic;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanTask;
import org.onap.policy.apex.client.editor.rest.handling.bean.BeanTaskParameter;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyInfo;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexApiResult.Result;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * This class handles commands on tasks in Apex models.
 */
public class TaskHandler implements RestCommandHandler {
    // Get a reference to the logger
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TaskHandler.class);

    // Recurring string constants
    private static final String OK = ": OK";
    private static final String NOT_OK = ": Not OK";
    private static final String IN_TASK = "\" in task ";
    private static final String TASK_PARTIALLY_DEFINED = " The task has only been partially defined.";

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command) {
        return getUnsupportedCommandResultMessage(session, commandType, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String jsonString) {
        if (!RestCommandType.TASK.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case CREATE:
                return createTask(session, jsonString);
            case UPDATE:
                return updateTask(session, jsonString);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApexApiResult executeRestCommand(final RestSession session, final RestCommandType commandType,
                    final RestCommand command, final String name, final String version) {
        if (!RestCommandType.TASK.equals(commandType)) {
            return getUnsupportedCommandResultMessage(session, commandType, command);
        }

        switch (command) {
            case LIST:
                return listTasks(session, name, version);
            case DELETE:
                return deleteTask(session, name, version);
            case VALIDATE:
                return validateTask(session, name, version);
            default:
                return getUnsupportedCommandResultMessage(session, commandType, command);
        }
    }

    /**
     * Creates a task with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createTask(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanTask jsonbean = RestUtils.getJsonParameters(jsonString, BeanTask.class);

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().createTask(jsonbean.getName(), jsonbean.getVersion(),
                        jsonbean.getUuid(), jsonbean.getDescription());

        if (result.isOk()) {
            result = createTaskContent(session, jsonbean);
        }

        session.finishSession(result.isOk());

        LOGGER.exit("Task/Create" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Create the content of the task.
     * 
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult createTaskContent(final RestSession session, final BeanTask jsonbean) {
        ApexApiResult result = createInputFields(session, jsonbean);

        if (result.isOk()) {
            result = createOutputFields(session, jsonbean);
        }

        if (result.isOk()) {
            result = createTaskLogic(session, jsonbean);
        }

        if (result.isOk()) {
            result = createTaskParameters(session, jsonbean);
        }

        if (result.isOk()) {
            result = createContextReferences(session, jsonbean);
        }
        return result;
    }

    /**
     * Create the input fields for the task.
     * 
     * @param session the Apex model editing session
     * @param jsonbean the ban containing the fields
     * @return the result of the operation
     */
    private ApexApiResult createInputFields(final RestSession session, final BeanTask jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getInputFields() == null || jsonbean.getInputFields().isEmpty()) {
            return result;
        }

        for (final Entry<String, BeanField> fieldEntry : jsonbean.getInputFields().entrySet()) {
            if (fieldEntry.getValue() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null task input field information for field \"" + fieldEntry.getKey() + IN_TASK
                                + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the input fields."
                                + TASK_PARTIALLY_DEFINED);
                continue;
            }

            if (fieldEntry.getKey() == null || !fieldEntry.getKey().equals(fieldEntry.getValue().getLocalName())) {
                result.setResult(Result.FAILED);
                result.addMessage("Invalid task input field information for field \"" + fieldEntry.getKey() + IN_TASK
                                + jsonbean.getName() + ":" + jsonbean.getVersion() + ". The localName of the field (\""
                                + fieldEntry.getValue().getLocalName() + "\") is not the same as the field name. "
                                + "The task was created, but there was an error adding the input fields."
                                + TASK_PARTIALLY_DEFINED);
            } else {
                ApexApiResult fieldCreationResult = session.getApexModelEdited().createTaskInputField(
                                jsonbean.getName(), jsonbean.getVersion(), fieldEntry.getKey(),
                                fieldEntry.getValue().getName(), fieldEntry.getValue().getVersion(),
                                fieldEntry.getValue().getOptional());

                if (fieldCreationResult.isNok()) {
                    result.setResult(fieldCreationResult.getResult());
                    result.addMessage("Failed to add task input field information for field \"" + fieldEntry.getKey()
                                    + IN_TASK + jsonbean.getName() + ":" + jsonbean.getVersion()
                                    + ". The task was created, but there was an error adding the input fields."
                                    + TASK_PARTIALLY_DEFINED);
                }
            }
        }

        return result;
    }

    /**
     * Create the output fields for the task.
     * 
     * @param session the Apex model editing session
     * @param jsonbean the ban containing the fields
     * @return the result of the operation
     */
    private ApexApiResult createOutputFields(final RestSession session, final BeanTask jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getOutputFields() == null || jsonbean.getOutputFields().isEmpty()) {
            return result;
        }

        for (final Entry<String, BeanField> fieldEntry : jsonbean.getOutputFields().entrySet()) {
            if (fieldEntry.getValue() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null task output field information for field \"" + fieldEntry.getKey() + IN_TASK
                                + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the output fields."
                                + TASK_PARTIALLY_DEFINED);
                continue;
            }

            if (fieldEntry.getKey() == null || !fieldEntry.getKey().equals(fieldEntry.getValue().getLocalName())) {
                result.setResult(Result.FAILED);
                result.addMessage("Invalid task output field information for field \"" + fieldEntry.getKey() + IN_TASK
                                + jsonbean.getName() + ":" + jsonbean.getVersion() + ". The localName of the field (\""
                                + fieldEntry.getValue().getLocalName() + "\") is not the same as the field name. "
                                + "The task was created, but there was an error adding the output fields."
                                + TASK_PARTIALLY_DEFINED);
            } else {
                ApexApiResult fieldCreationResult = session.getApexModelEdited().createTaskOutputField(
                                jsonbean.getName(), jsonbean.getVersion(), fieldEntry.getKey(),
                                fieldEntry.getValue().getName(), fieldEntry.getValue().getVersion(),
                                fieldEntry.getValue().getOptional());
                if (fieldCreationResult.isNok()) {
                    result.setResult(fieldCreationResult.getResult());
                    result.addMessage("Failed to add task output field information for field \"" + fieldEntry.getKey()
                                    + IN_TASK + jsonbean.getName() + ":" + jsonbean.getVersion()
                                    + ". The task was created, but there was an error adding the output fields."
                                    + TASK_PARTIALLY_DEFINED);
                }
            }
        }

        return result;
    }

    /**
     * Create the task logic for the task.
     * 
     * @param session the Apex model editing session
     * @param jsonbean the bean containing the logic
     * @return the result of the operation
     */
    private ApexApiResult createTaskLogic(final RestSession session, final BeanTask jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getTaskLogic() == null) {
            return result;
        }

        final BeanLogic logic = jsonbean.getTaskLogic();
        result = session.getApexModelEdited().createTaskLogic(jsonbean.getName(), jsonbean.getVersion(),
                        logic.getLogicFlavour(), logic.getLogic());

        if (result.isNok()) {
            result.addMessage("Failed to add task logic in task " + jsonbean.getName() + ":" + jsonbean.getVersion()
                            + ". The task was created, but there was an error adding the logic."
                            + TASK_PARTIALLY_DEFINED);
        }

        return result;
    }

    /**
     * Create the task parameters for the task.
     * 
     * @param session the Apex model editing session
     * @param jsonbean the bean containing the parameters
     * @return the result of the operation
     */
    private ApexApiResult createTaskParameters(final RestSession session, final BeanTask jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getParameters() == null || jsonbean.getParameters().isEmpty()) {
            return result;
        }

        for (final Entry<String, BeanTaskParameter> parameterEntry : jsonbean.getParameters().entrySet()) {
            if (parameterEntry.getKey() == null || parameterEntry.getValue() == null
                            || !parameterEntry.getKey().equals(parameterEntry.getValue().getParameterName())) {
                result.setResult(Result.FAILED);
                result.addMessage("Null or invalid task parameter information for parameter \""
                                + parameterEntry.getKey() + IN_TASK + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, " + "but there was an error adding the parameters."
                                + TASK_PARTIALLY_DEFINED);
                continue;
            }
            ApexApiResult createParResult = session.getApexModelEdited().createTaskParameter(jsonbean.getName(),
                            jsonbean.getVersion(), parameterEntry.getValue().getParameterName(),
                            parameterEntry.getValue().getDefaultValue());
            if (createParResult.isNok()) {
                result.setResult(createParResult.getResult());
                result.addMessage("Failed to add task parameter \"" + parameterEntry.getKey() + IN_TASK
                                + jsonbean.getName() + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the parameters."
                                + TASK_PARTIALLY_DEFINED);
            }
        }

        return result;
    }

    /**
     * Create the context references for the task.
     * 
     * @param session the Apex model editing session
     * @param jsonbean the bean containing the context references
     * @return the result of the operation
     */
    private ApexApiResult createContextReferences(final RestSession session, final BeanTask jsonbean) {
        ApexApiResult result = new ApexApiResult();

        if (jsonbean.getContexts() == null || jsonbean.getContexts().length == 0) {
            return result;
        }

        for (final BeanKeyRef contextalbum : jsonbean.getContexts()) {
            if (contextalbum.getName() == null || contextalbum.getVersion() == null) {
                result.setResult(Result.FAILED);
                result.addMessage("Null or invalid context album reference information in task " + jsonbean.getName()
                                + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the"
                                + " context album reference. " + "The task has only been partially defined.");
                continue;
            }
            ApexApiResult createRefResult = session.getApexModelEdited().createTaskContextRef(jsonbean.getName(),
                            jsonbean.getVersion(), contextalbum.getName(), contextalbum.getVersion());
            if (createRefResult.isNok()) {
                result.setResult(createRefResult.getResult());
                result.addMessage("Failed to add context album reference information in task " + jsonbean.getName()
                                + ":" + jsonbean.getVersion()
                                + ". The task was created, but there was an error adding the"
                                + " context album reference. " + "The task has only been partially defined.");
            }
        }

        return result;
    }

    /**
     * Update a task with the information in the JSON string passed.
     *
     * @param session the Apex model editing session
     * @param jsonString the JSON string to be parsed. See {@linkplain BeanTask}
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult updateTask(final RestSession session, final String jsonString) {
        LOGGER.entry(jsonString);

        final BeanTask jsonbean = RestUtils.getJsonParameters(jsonString, BeanTask.class);

        if (blank2Null(jsonbean.getName()) == null || blank2Null(jsonbean.getVersion()) == null) {
            LOGGER.exit("Task/Update" + NOT_OK);
            return new ApexApiResult(Result.FAILED, "Null/Empty task name/version (\"" + jsonbean.getName() + ":"
                            + jsonbean.getVersion() + "\" passed to UpdateTask");
        }

        session.editModel();

        ApexApiResult result = session.getApexModelEdited().deleteTask(jsonbean.getName(), jsonbean.getVersion());

        if (result.isOk()) {
            result = session.getApexModelEdited().createTask(jsonbean.getName(), jsonbean.getVersion(),
                            jsonbean.getUuid(), jsonbean.getDescription());

            if (result.isOk()) {
                result = createTaskContent(session, jsonbean);
            }
        }

        session.finishSession(result.isOk());

        LOGGER.exit("Task/Update" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * List tasks with the given key names/versions. If successful the result(s) will be available in the result
     * messages. The returned value(s) will be similar to {@link AxTask}, with merged {@linkplain AxKeyInfo} for the
     * root object.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult listTasks(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().listTask(blank2Null(name), blank2Null(version));

        LOGGER.exit("Task/Get" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Delete tasks with the given key names/versions.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult deleteTask(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        session.editModel();

        // all input/output fields, parameters, logic, context references is "owned"/contained
        // in the task, so
        // deleting the task removes all of these
        ApexApiResult result = session.getApexModelEdited().deleteTask(blank2Null(name), blank2Null(version));

        session.finishSession(result.isOk());

        LOGGER.exit("Task/Delete" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }

    /**
     * Validate tasks with the given key names/versions. The result(s) will be available in the result messages.
     *
     * @param session the Apex model editing session
     * @param name the name to search for. If null or empty, then all names will be queried
     * @param version the version to search for. If null then all versions will be searched for.
     * @return an ApexAPIResult object. If successful then {@link ApexApiResult#isOk()} will return true. Any
     *         messages/errors can be retrieved using {@link ApexApiResult#getMessages()}
     */
    private ApexApiResult validateTask(final RestSession session, final String name, final String version) {
        LOGGER.entry(name, version);

        ApexApiResult result = session.getApexModel().validateTask(blank2Null(name), blank2Null(version));

        LOGGER.exit("Validate/Task" + (result != null && result.isOk() ? OK : NOT_OK));
        return result;
    }
}
