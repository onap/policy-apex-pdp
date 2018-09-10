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

package org.onap.policy.apex.model.modelapi.impl;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextAlbum;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.modelapi.ApexModel;
import org.onap.policy.apex.model.policymodel.concepts.AxTask;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskLogic;
import org.onap.policy.apex.model.policymodel.concepts.AxTaskParameter;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class acts as a facade for operations towards a policy model for task operations.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class TaskFacade {
    private static final String CONCEPT = "concept ";
    private static final String CONCEPT_S = "concept(s) ";
    private static final String DOES_NOT_EXIST = " does not exist";
    private static final String DO_ES_NOT_EXIST = " do(es) not exist";
    private static final String ALREADY_EXISTS = " already exists";

    // Apex model we're working towards
    private final ApexModel apexModel;

    // Properties to use for the model
    private final Properties apexProperties;

    // Facade classes for working towards the real Apex model
    private final KeyInformationFacade keyInformationFacade;

    // JSON output on list/delete if set
    private final boolean jsonMode;

    /**
     * Constructor that creates a task facade for the Apex Model API.
     *
     * @param apexModel the apex model
     * @param apexProperties Properties for the model
     * @param jsonMode set to true to return JSON strings in list and delete operations, otherwise
     *        set to false
     */
    public TaskFacade(final ApexModel apexModel, final Properties apexProperties, final boolean jsonMode) {
        this.apexModel = apexModel;
        this.apexProperties = apexProperties;
        this.jsonMode = jsonMode;

        keyInformationFacade = new KeyInformationFacade(apexModel, apexProperties, jsonMode);
    }

    /**
     * Create a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the default version
     * @param uuid task UUID, set to null to generate a UUID
     * @param description task description, set to null to generate a description
     * @return result of the operation
     */
    public ApexApiResult createTask(final String name, final String version, final String uuid,
            final String description) {
        try {
            final AxArtifactKey key = new AxArtifactKey();
            key.setName(name);
            if (version != null) {
                key.setVersion(version);
            } else {
                key.setVersion(apexProperties.getProperty("DEFAULT_CONCEPT_VERSION"));
            }

            if (apexModel.getPolicyModel().getTasks().getTaskMap().containsKey(key)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, CONCEPT + key.getId() + ALREADY_EXISTS);
            }

            apexModel.getPolicyModel().getTasks().getTaskMap().put(key, new AxTask(key));

            if (apexModel.getPolicyModel().getKeyInformation().getKeyInfoMap().containsKey(key)) {
                return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
            } else {
                return keyInformationFacade.createKeyInformation(name, version, uuid, description);
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param uuid task UUID, set to null to not update
     * @param description task description, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updateTask(final String name, final String version, final String uuid,
            final String description) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            return keyInformationFacade.updateKeyInformation(name, version, uuid, description);
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List tasks.
     *
     * @param name name of the task, set to null to list all
     * @param version starting version of the task, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult listTask(final String name, final String version) {
        try {
            final Set<AxTask> taskSet = apexModel.getPolicyModel().getTasks().getAll(name, version);
            if (name != null && taskSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxTask task : taskSet) {
                result.addMessage(new ApexModelStringWriter<AxTask>(false).writeString(task, AxTask.class, jsonMode));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult deleteTask(final String name, final String version) {
        try {
            if (version != null) {
                final AxArtifactKey key = new AxArtifactKey(name, version);
                final AxTask removedTask = apexModel.getPolicyModel().getTasks().getTaskMap().remove(key);
                if (removedTask != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                            new ApexModelStringWriter<AxTask>(false).writeString(removedTask, AxTask.class, jsonMode));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + key.getId() + DOES_NOT_EXIST);
                }
            }

            final Set<AxTask> taskSet = apexModel.getPolicyModel().getTasks().getAll(name, version);
            if (taskSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxTask task : taskSet) {
                result.addMessage(new ApexModelStringWriter<AxTask>(false).writeString(task, AxTask.class, jsonMode));
                apexModel.getPolicyModel().getTasks().getTaskMap().remove(task.getKey());
                keyInformationFacade.deleteKeyInformation(name, version);
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Validate tasks.
     *
     * @param name name of the task, set to null to list all
     * @param version starting version of the task, set to null to list all versions
     * @return result of the operation
     */
    public ApexApiResult validateTask(final String name, final String version) {
        try {
            final Set<AxTask> taskSet = apexModel.getPolicyModel().getTasks().getAll(name, version);
            if (taskSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT_S + name + ':' + version + DO_ES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            for (final AxTask task : taskSet) {
                final AxValidationResult validationResult = task.validate(new AxValidationResult());
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(task.getKey(),
                        AxArtifactKey.class, jsonMode));
                result.addMessage(validationResult.toString());
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create logic for a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param logicFlavour the task logic flavour for the task, set to null to use the default task
     *        logic flavour
     * @param logic the source code for the logic of the task
     * @return result of the operation
     */
    public ApexApiResult createTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            // There is only one logic item associated with a task so we use a hard coded logic name
            final AxReferenceKey refKey = new AxReferenceKey(task.getKey(), "TaskLogic");

            if (!task.getTaskLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                        CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            task.setTaskLogic(new AxTaskLogic(refKey, logicFlavour, logic));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Update logic for a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param logicFlavour the task logic flavour for the task, set to null to not update
     * @param logic the source code for the logic of the task, set to null to not update
     * @return result of the operation
     */
    public ApexApiResult updateTaskLogic(final String name, final String version, final String logicFlavour,
            final String logic) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (task.getTaskLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + task.getTaskLogic().getKey().getId() + DOES_NOT_EXIST);
            }

            final AxTaskLogic taskLogic = task.getTaskLogic();
            if (logicFlavour != null) {
                taskLogic.setLogicFlavour(logicFlavour);
            }
            if (logic != null) {
                taskLogic.setLogic(logic);
            }

            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List task logic.
     *
     * @param name name of the task
     * @param version version of the task, set to null to list the latest version
     * @return result of the operation
     */
    public ApexApiResult listTaskLogic(final String name, final String version) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            return new ApexApiResult(ApexApiResult.Result.SUCCESS, new ApexModelStringWriter<AxTaskLogic>(false)
                    .writeString(task.getTaskLogic(), AxTaskLogic.class, jsonMode));
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete logic for a task.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult deleteTaskLogic(final String name, final String version) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (task.getTaskLogic().getKey().getLocalName().equals(AxKey.NULL_KEY_NAME)) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + task.getTaskLogic().getKey().getId() + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            result.addMessage(new ApexModelStringWriter<AxTaskLogic>(false).writeString(task.getTaskLogic(),
                    AxTaskLogic.class, jsonMode));
            task.setTaskLogic(new AxTaskLogic());
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a task input field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the input field
     * @param contextSchemaName name of the input field context schema
     * @param contextSchemaVersion version of the input field context schema, set to null to use the
     *        latest version
     * @param optional true if the task field is optional, false otherwise
     * @return result of the operation
     */
    public ApexApiResult createTaskInputField(final String name, final String version, final String fieldName,
            final String contextSchemaName, final String contextSchemaVersion, final boolean optional) {
        try {
            Assertions.argumentNotNull(fieldName, "fieldName may not be null");

            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey =
                    new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(), "inputFields", fieldName);

            if (task.getInputFields().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                        CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            final AxContextSchema schema =
                    apexModel.getPolicyModel().getSchemas().get(contextSchemaName, contextSchemaVersion);
            if (schema == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextSchemaName + ':' + contextSchemaVersion + DOES_NOT_EXIST);
            }

            task.getInputFields().put(refKey.getLocalName(), new AxInputField(refKey, schema.getKey(), optional));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List task input fields.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName field name of the input field, set to null to list all input fields of the
     *        task
     * @return result of the operation
     */
    public ApexApiResult listTaskInputField(final String name, final String version, final String fieldName) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (fieldName != null) {
                final AxInputField inputField = task.getInputFields().get(fieldName);
                if (inputField != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                            new ApexModelStringWriter<AxInputField>(false).writeString(inputField, AxInputField.class,
                                    jsonMode));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + inputField + DOES_NOT_EXIST);
                }
            } else {
                if (task.getInputFields().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no input fields defined on task " + task.getKey().getId());
                }

                final ApexApiResult result = new ApexApiResult();
                for (final AxInputField field : task.getInputFields().values()) {
                    result.addMessage(new ApexModelStringWriter<AxInputField>(false).writeString(field,
                            AxInputField.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }

    }

    /**
     * Delete a task input field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the input field, set to null to delete all input fields
     * @return result of the operation
     */
    public ApexApiResult deleteTaskInputField(final String name, final String version, final String fieldName) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            if (fieldName != null) {
                if (task.getInputFields().containsKey(fieldName)) {
                    result.addMessage(new ApexModelStringWriter<AxInputField>(false)
                            .writeString(task.getInputFields().get(fieldName), AxInputField.class, jsonMode));
                    task.getInputFields().remove(fieldName);
                    return result;
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + fieldName + DOES_NOT_EXIST);
                }
            } else {
                if (task.getInputFields().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no input fields defined on task " + task.getKey().getId());
                }

                for (final AxInputField field : task.getInputFields().values()) {
                    result.addMessage(new ApexModelStringWriter<AxInputField>(false).writeString(field,
                            AxInputField.class, jsonMode));
                }
                task.getInputFields().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }

    }

    /**
     * Create a task output field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the output field
     * @param contextSchemaName name of the output field context schema
     * @param contextSchemaVersion version of the output field context schema, set to null to use
     *        the latest version
     * @param optional true if the task field is optional, false otherwise
     * @return result of the operation
     */
    public ApexApiResult createTaskOutputField(final String name, final String version, final String fieldName,
            final String contextSchemaName, final String contextSchemaVersion, final boolean optional) {
        try {
            Assertions.argumentNotNull(fieldName, "fieldName may not be null");

            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey =
                    new AxReferenceKey(task.getKey().getName(), task.getKey().getVersion(), "outputFields", fieldName);

            if (task.getOutputFields().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                        CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            final AxContextSchema schema =
                    apexModel.getPolicyModel().getSchemas().get(contextSchemaName, contextSchemaVersion);
            if (schema == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextSchemaName + ':' + contextSchemaVersion + DOES_NOT_EXIST);
            }

            task.getOutputFields().put(refKey.getLocalName(), new AxOutputField(refKey, schema.getKey(), optional));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List task output fields.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName field name of the output field, set to null to list all output fields of the
     *        task
     * @return result of the operation
     */
    public ApexApiResult listTaskOutputField(final String name, final String version, final String fieldName) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (fieldName != null) {
                final AxOutputField outputField = task.getOutputFields().get(fieldName);
                if (outputField != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                            new ApexModelStringWriter<AxOutputField>(false).writeString(outputField,
                                    AxOutputField.class, jsonMode));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + outputField + DOES_NOT_EXIST);
                }
            } else {
                if (task.getOutputFields().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no output fields defined on task " + task.getKey().getId());
                }

                final ApexApiResult result = new ApexApiResult();
                for (final AxOutputField field : task.getOutputFields().values()) {
                    result.addMessage(new ApexModelStringWriter<AxOutputField>(false).writeString(field,
                            AxOutputField.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a task output field.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param fieldName of the output field, set to null to delete all output fields
     * @return result of the operation
     */
    public ApexApiResult deleteTaskOutputField(final String name, final String version, final String fieldName) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            if (fieldName != null) {
                if (task.getOutputFields().containsKey(fieldName)) {
                    result.addMessage(new ApexModelStringWriter<AxOutputField>(false)
                            .writeString(task.getOutputFields().get(fieldName), AxOutputField.class, jsonMode));
                    task.getOutputFields().remove(fieldName);
                    return result;
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + fieldName + DOES_NOT_EXIST);
                }
            } else {
                if (task.getOutputFields().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no output fields defined on task " + task.getKey().getId());
                }

                for (final AxOutputField field : task.getOutputFields().values()) {
                    result.addMessage(new ApexModelStringWriter<AxOutputField>(false).writeString(field,
                            AxOutputField.class, jsonMode));
                }
                task.getOutputFields().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a task parameter.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param parName of the parameter
     * @param defaultValue of the parameter
     * @return result of the operation
     */
    public ApexApiResult createTaskParameter(final String name, final String version, final String parName,
            final String defaultValue) {
        try {
            Assertions.argumentNotNull(parName, "parName may not be null");

            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxReferenceKey refKey = new AxReferenceKey(task.getKey(), parName);

            if (task.getTaskParameters().containsKey(refKey.getLocalName())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS,
                        CONCEPT + refKey.getId() + ALREADY_EXISTS);
            }

            task.getTaskParameters().put(refKey.getLocalName(), new AxTaskParameter(refKey, defaultValue));
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List task parameters.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param parName name of the parameter, set to null to list all parameters of the task
     * @return result of the operation
     */
    public ApexApiResult listTaskParameter(final String name, final String version, final String parName) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            if (parName != null) {
                final AxTaskParameter taskParameter = task.getTaskParameters().get(parName);
                if (taskParameter != null) {
                    return new ApexApiResult(ApexApiResult.Result.SUCCESS,
                            new ApexModelStringWriter<AxTaskParameter>(false).writeString(taskParameter,
                                    AxTaskParameter.class, jsonMode));
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + taskParameter + DOES_NOT_EXIST);
                }
            } else {
                if (task.getTaskParameters().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no task parameters defined on task " + task.getKey().getId());
                }

                final ApexApiResult result = new ApexApiResult();
                for (final AxTaskParameter parameter : task.getTaskParameters().values()) {
                    result.addMessage(new ApexModelStringWriter<AxTaskParameter>(false).writeString(parameter,
                            AxTaskParameter.class, jsonMode));
                }
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a task parameter.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param parName of the parameter, set to null to delete all task parameters
     * @return result of the operation
     */
    public ApexApiResult deleteTaskParameter(final String name, final String version, final String parName) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            if (parName != null) {
                if (task.getTaskParameters().containsKey(parName)) {
                    result.addMessage(new ApexModelStringWriter<AxTaskParameter>(false)
                            .writeString(task.getTaskParameters().get(parName), AxTaskParameter.class, jsonMode));
                    task.getTaskParameters().remove(parName);
                    return result;
                } else {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            CONCEPT + name + ':' + version + ':' + parName + DOES_NOT_EXIST);
                }
            } else {
                if (task.getTaskParameters().size() == 0) {
                    return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                            "no task parameters defined on task " + task.getKey().getId());
                }

                for (final AxTaskParameter parameter : task.getTaskParameters().values()) {
                    result.addMessage(new ApexModelStringWriter<AxTaskParameter>(false).writeString(parameter,
                            AxTaskParameter.class, jsonMode));
                }
                task.getTaskParameters().clear();
                return result;
            }
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Create a task context album reference.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param contextAlbumName name of the context album for the context album reference
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult createTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final AxContextAlbum contextAlbum =
                    apexModel.getPolicyModel().getAlbums().get(contextAlbumName, contextAlbumVersion);
            if (contextAlbum == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextAlbumName + ':' + contextAlbumVersion + DOES_NOT_EXIST);
            }

            if (task.getContextAlbumReferences().contains(contextAlbum.getKey())) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_EXISTS, "context album reference for concept "
                        + contextAlbum.getKey().getId() + " already exists in task");
            }

            task.getContextAlbumReferences().add(contextAlbum.getKey());
            return new ApexApiResult();
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * List task context album references.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param contextAlbumName name of the context album for the context album reference, set to
     *        null to list all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult listTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final ApexApiResult result = new ApexApiResult();
            boolean found = false;
            for (final AxArtifactKey albumKey : task.getContextAlbumReferences()) {
                if ((contextAlbumName != null && !albumKey.getName().equals(contextAlbumName))
                        || (contextAlbumVersion != null && !albumKey.getVersion().equals(contextAlbumVersion))) {
                    continue;
                }
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(albumKey,
                        AxArtifactKey.class, jsonMode));
                found = true;
            }
            if (!found) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextAlbumName + ':' + contextAlbumVersion + DOES_NOT_EXIST);
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }

    /**
     * Delete a task context album reference.
     *
     * @param name name of the task
     * @param version version of the task, set to null to use the latest version
     * @param contextAlbumName name of the context album for the context album reference, set to
     *        null to delete all task context album references
     * @param contextAlbumVersion version of the context album for the context album reference, set
     *        to null to use the latest version
     * @return result of the operation
     */
    public ApexApiResult deleteTaskContextRef(final String name, final String version, final String contextAlbumName,
            final String contextAlbumVersion) {
        try {
            final AxTask task = apexModel.getPolicyModel().getTasks().get(name, version);
            if (task == null) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + name + ':' + version + DOES_NOT_EXIST);
            }

            final Set<AxArtifactKey> deleteSet = new TreeSet<>();

            for (final AxArtifactKey albumKey : task.getContextAlbumReferences()) {
                if ((contextAlbumName != null && !albumKey.getName().equals(contextAlbumName))
                        || (contextAlbumVersion != null && !albumKey.getVersion().equals(contextAlbumVersion))) {
                    continue;
                }
                deleteSet.add(albumKey);
            }

            if (deleteSet.isEmpty()) {
                return new ApexApiResult(ApexApiResult.Result.CONCEPT_DOES_NOT_EXIST,
                        CONCEPT + contextAlbumName + ':' + contextAlbumVersion + DOES_NOT_EXIST);
            }
            final ApexApiResult result = new ApexApiResult();
            for (final AxArtifactKey keyToDelete : deleteSet) {
                task.getContextAlbumReferences().remove(keyToDelete);
                result.addMessage(new ApexModelStringWriter<AxArtifactKey>(false).writeString(keyToDelete,
                        AxArtifactKey.class, jsonMode));
            }
            return result;
        } catch (final Exception e) {
            return new ApexApiResult(ApexApiResult.Result.FAILED, e);
        }
    }
}
