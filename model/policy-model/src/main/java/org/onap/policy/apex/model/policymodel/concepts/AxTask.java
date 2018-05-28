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

package org.onap.policy.apex.model.policymodel.concepts;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.eventmodel.concepts.AxField;
import org.onap.policy.apex.model.eventmodel.concepts.AxInputField;
import org.onap.policy.apex.model.eventmodel.concepts.AxOutputField;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class holds the definition of a task in Apex. A task is executed by a state and performs
 * some domain specific logic to carry out work required to be done by a policy. The Task Logic that
 * is executed by a task is held in a {@link AxTaskLogic} instance.
 * <p>
 * A task has a set of input fields and output fields, which are passed to and are emitted from the
 * task during a task execution cycle. A task may have task parameters {@link AxTaskParameter},
 * which are configuration values passed to a task at initialization time.
 * <p>
 * The Task Logic in a task may use information in context albums to perform their domain specific
 * work. The context albums that the task uses and that should be made available to the task by Apex
 * policy distribution are held as a set of references to context albums in the task.
 * <p>
 * During validation of a task, the validation checks listed below are executed:
 * <ol>
 * <li>The task key must not be a null key and must be valid, see validation in
 * {@link AxArtifactKey}
 * <li>The task must have at least one input field
 * <li>The parent of each input field of a task must be that task
 * <li>Each input field must be valid, see validation in {@link AxInputField}
 * <li>The task must have at least one output field
 * <li>The parent of each output field of a task must be that task
 * <li>Each output field must be valid, see validation in {@link AxOutputField}
 * <li>The parent of each task parameter of a task must be that task
 * <li>Each task parameter must be valid, see validation in {@link AxTaskParameter}
 * <li>The parent of the task logic in a task must be that task
 * <li>The task logic must be valid, see validation in {@link AxTaskLogic}
 * </ol>
 */

@Entity
@Table(name = "AxTask")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexTask", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxTask", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"key", "inputFields", "outputFields", "taskParameters", "contextAlbumReferenceSet", "taskLogic"})

public class AxTask extends AxConcept {
    private static final String DOES_NOT_EQUAL_TASK_KEY = " does not equal task key";

    private static final long serialVersionUID = 5374237330697362762L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    @OneToMany(cascade = CascadeType.ALL)
    @XmlElement(name = "inputFields", required = true)
    private Map<String, AxInputField> inputFields;

    @OneToMany(cascade = CascadeType.ALL)
    @XmlElement(name = "outputFields", required = true)
    private Map<String, AxOutputField> outputFields;

    @OneToMany(cascade = CascadeType.ALL)
    @XmlElement(name = "taskParameters", required = true)
    private Map<String, AxTaskParameter> taskParameters;

    // @formatter:off
    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "contextAlbumName", referencedColumnName = "name"),
            @JoinColumn(name = "contextAlbumVersion", referencedColumnName = "version")})
    @XmlElement(name = "contextAlbumReference")
    private Set<AxArtifactKey> contextAlbumReferenceSet;
    // @formatter:on

    @OneToOne(cascade = CascadeType.ALL)
    @XmlElement(required = true)
    private AxTaskLogic taskLogic;

    /**
     * The Default Constructor creates a task with a null key no input or output fields, no task
     * parameters, no context album references and no logic.
     */
    public AxTask() {
        this(new AxArtifactKey());
        contextAlbumReferenceSet = new TreeSet<>();
    }

    /**
     * Copy constructor
     * 
     * @param copyConcept the concept to copy from
     */
    public AxTask(final AxTask copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a task with the given key no input or output fields, no task
     * parameters, no context album references and no logic.
     *
     * @param key the key of the task
     */
    public AxTask(final AxArtifactKey key) {
        this(key, // Task Key
                new TreeMap<String, AxInputField>(), // Input fields
                new TreeMap<String, AxOutputField>(), // Output Fields
                new TreeMap<String, AxTaskParameter>(), // Task Parameters
                new TreeSet<AxArtifactKey>(), // Context Album References
                new AxTaskLogic(new AxReferenceKey(key)) // Task Logic
        );
    }

    /**
     * This Constructor defines all the fields of the task.
     *
     * @param key the key of the task
     * @param inputFields the input fields that the task expects
     * @param outputFields the output fields that the task emits
     * @param taskParameters the task parameters that are used to initialize tasks of this type
     * @param contextAlbumReferenceSet the context album reference set defines the context that may
     *        be used by Task Logic in the state
     * @param taskLogic the task logic that performs the domain specific work of the task
     */
    public AxTask(final AxArtifactKey key, final Map<String, AxInputField> inputFields,
            final Map<String, AxOutputField> outputFields, final Map<String, AxTaskParameter> taskParameters,
            final Set<AxArtifactKey> contextAlbumReferenceSet, final AxTaskLogic taskLogic) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(inputFields, "inputFields may not be null");
        Assertions.argumentNotNull(outputFields, "outputFields may not be null");
        Assertions.argumentNotNull(taskParameters, "taskParameters may not be null");
        Assertions.argumentNotNull(contextAlbumReferenceSet, "contextAlbumReferenceSet may not be null");
        Assertions.argumentNotNull(taskLogic, "taskLogic may not be null");

        this.key = key;
        this.inputFields = inputFields;
        this.outputFields = outputFields;
        this.taskParameters = taskParameters;
        this.contextAlbumReferenceSet = contextAlbumReferenceSet;
        this.taskLogic = taskLogic;
    }

    /**
     * When a task is unmarshalled from disk or from the database, the parent of contained objects
     * is not defined. This method is called by JAXB after unmarshaling and is used to set the
     * parent keys of all {@link AxInputField}, {@link AxOutputField}, and {@link AxTaskParameter}
     * instance in the task.
     *
     * @param u the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller u, final Object parent) {
        taskLogic.getKey().setParentArtifactKey(key);

        for (final AxInputField inputField : inputFields.values()) {
            inputField.getKey().setParentArtifactKey(key);
            inputField.getKey().setParentLocalName("InField");
        }
        for (final AxOutputField outputField : outputFields.values()) {
            outputField.getKey().setParentArtifactKey(key);
            outputField.getKey().setParentLocalName("OutField");
        }
        for (final AxTaskParameter parameter : taskParameters.values()) {
            parameter.getKey().setParentArtifactKey(key);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();
        for (final AxInputField inputField : inputFields.values()) {
            keyList.addAll(inputField.getKeys());
        }
        for (final AxOutputField outputField : outputFields.values()) {
            keyList.addAll(outputField.getKeys());
        }
        for (final AxTaskParameter taskParameter : taskParameters.values()) {
            keyList.addAll(taskParameter.getKeys());
        }
        for (final AxArtifactKey contextAlbumKey : contextAlbumReferenceSet) {
            keyList.add(new AxKeyUse(contextAlbumKey));
        }
        keyList.addAll(taskLogic.getKeys());
        return keyList;
    }

    /**
     * Sets the key of the task.
     *
     * @param key the key of the task
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the input fields that the task expects.
     *
     * @return the input fields that the task expects
     */
    public Map<String, AxInputField> getInputFields() {
        return inputFields;
    }

    /**
     * Gets the raw input fields that the task expects as a tree map.
     *
     * @return the raw input fields that the task expects
     */
    public Map<String, AxField> getRawInputFields() {
        return new TreeMap<>(inputFields);
    }

    /**
     * Convenience method to get the input fields as a set.
     *
     * @return the input fields as a set
     */
    public Set<AxField> getInputFieldSet() {
        final Set<AxField> inputFieldSet = new TreeSet<>();
        for (final AxInputField field : inputFields.values()) {
            inputFieldSet.add(field);
        }
        return inputFieldSet;
    }

    /**
     * Sets the input fields that the task expects.
     *
     * @param inputFields the input fields that the task expects
     */
    public void setInputFields(final Map<String, AxInputField> inputFields) {
        Assertions.argumentNotNull(inputFields, "inputFields may not be null");
        this.inputFields = inputFields;
    }

    /**
     * Copy the input fields from the given map into the task. This method is used to get a copy of
     * the input fields, which can be useful for unit testing of policies and tasks.
     *
     * @param fields the fields to copy into the task
     */
    public void duplicateInputFields(final Map<String, AxField> fields) {
        Assertions.argumentNotNull(fields, "fields may not be null");

        for (final AxField field : fields.values()) {
            final AxReferenceKey fieldKey = new AxReferenceKey(this.getKey().getName(), this.getKey().getVersion(),
                    "inputFields", field.getKey().getLocalName());
            final AxInputField inputField = new AxInputField(fieldKey, field.getSchema());
            inputFields.put(inputField.getKey().getLocalName(), inputField);
        }
    }

    /**
     * Gets the output fields that the task emits.
     *
     * @return the output fields that the task emits
     */
    public Map<String, AxOutputField> getOutputFields() {
        return outputFields;
    }

    /**
     * Gets the raw output fields that the task emits as a tree map.
     *
     * @return the raw output fields as a tree map
     */
    public Map<String, AxField> getRawOutputFields() {
        return new TreeMap<>(outputFields);
    }

    /**
     * Gets the output fields that the task emits as a set.
     *
     * @return the output fields as a set
     */
    public Set<AxField> getOutputFieldSet() {
        final Set<AxField> outputFieldSet = new TreeSet<>();
        for (final AxOutputField field : outputFields.values()) {
            outputFieldSet.add(field);
        }
        return outputFieldSet;
    }

    /**
     * Sets the output fields that the task emits.
     *
     * @param outputFields the output fields that the task emits
     */
    public void setOutputFields(final Map<String, AxOutputField> outputFields) {
        Assertions.argumentNotNull(outputFields, "outputFields may not be null");
        this.outputFields = outputFields;
    }

    /**
     * Copy the output fields from the given map into the task. This method is used to get a copy of
     * the output fields, which can be useful for unit testing of policies and tasks.
     *
     * @param fields the fields to copy into the task
     */
    public void duplicateOutputFields(final Map<String, AxField> fields) {
        Assertions.argumentNotNull(fields, "fields may not be null");

        for (final AxField field : fields.values()) {
            final AxReferenceKey fieldKey = new AxReferenceKey(this.getKey().getName(), this.getKey().getVersion(),
                    "outputFields", field.getKey().getLocalName());
            final AxOutputField outputField = new AxOutputField(fieldKey, field.getSchema());
            outputFields.put(outputField.getKey().getLocalName(), outputField);
        }
    }

    /**
     * Gets the task parameters that are used to initialize tasks of this type.
     *
     * @return the task parameters that are used to initialize tasks of this type
     */
    public Map<String, AxTaskParameter> getTaskParameters() {
        return taskParameters;
    }

    /**
     * Sets the task parameters that are used to initialize tasks of this type.
     *
     * @param taskParameters the task parameters that are used to initialize tasks of this type
     */
    public void setTaskParameters(final Map<String, AxTaskParameter> taskParameters) {
        Assertions.argumentNotNull(taskParameters, "taskParameters may not be null");
        this.taskParameters = taskParameters;
    }

    /**
     * Gets the context album reference set defines the context that may be used by Task Logic in
     * the state.
     *
     * @return the context album reference set defines the context that may be used by Task Logic in
     *         the state
     */
    public Set<AxArtifactKey> getContextAlbumReferences() {
        return contextAlbumReferenceSet;
    }

    /**
     * Sets the context album reference set defines the context that may be used by Task Logic in
     * the state.
     *
     * @param contextAlbumReferences the context album reference set defines the context that may be
     *        used by Task Logic in the state
     */
    public void setContextAlbumReferences(final Set<AxArtifactKey> contextAlbumReferences) {
        Assertions.argumentNotNull(contextAlbumReferences, "contextAlbumReferences may not be null");
        this.contextAlbumReferenceSet = contextAlbumReferences;
    }

    /**
     * Gets the task logic that performs the domain specific work of the task.
     *
     * @return the task logic that performs the domain specific work of the task
     */
    public AxTaskLogic getTaskLogic() {
        return taskLogic;
    }

    /**
     * Sets the task logic that performs the domain specific work of the task.
     *
     * @param taskLogic the task logic that performs the domain specific work of the task
     */
    public void setTaskLogic(final AxTaskLogic taskLogic) {
        Assertions.argumentNotNull(taskLogic, "taskLogic may not be null");
        this.taskLogic = taskLogic;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.
     * basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (inputFields.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "inputFields may not be empty"));
        } else {
            for (final Entry<String, AxInputField> inputFieldEntry : inputFields.entrySet()) {
                result = validateField(inputFieldEntry.getKey(), inputFieldEntry.getValue(), "input", result);
            }
        }

        if (outputFields.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "outputFields may not be empty"));
        } else {
            for (final Entry<String, AxOutputField> outputFieldEntry : outputFields.entrySet()) {
                result = validateField(outputFieldEntry.getKey(), outputFieldEntry.getValue(), "input", result);
            }
        }

        for (final Entry<String, AxTaskParameter> taskParameterEntry : taskParameters.entrySet()) {
            result = vaildateTaskParameterEntry(taskParameterEntry, result);
        }

        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            result = vaildateContextAlbumReference(contextAlbumReference, result);
        }

        if (!taskLogic.getKey().getParentArtifactKey().equals(key)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "taskLogic parent key " + taskLogic.getKey().getID() + DOES_NOT_EQUAL_TASK_KEY));
        }

        return taskLogic.validate(result);
    }

    /**
     * Validate a field
     * 
     * @param key the key of the field to validate
     * @param field the field to validate
     * @param direction The direction of the field
     * @param result The validation result to append to
     * @return The result of the validation
     */
    private AxValidationResult validateField(final String fieldKey, final AxField field, final String direction,
            AxValidationResult result) {
        if (field == null) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "null " + direction + " field value found on " + direction + " field " + fieldKey));
        } else {
            if (!field.getKey().getParentArtifactKey().equals(key)) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "parent key on " + direction + " field " + fieldKey + DOES_NOT_EQUAL_TASK_KEY));
            }

            result = field.validate(result);
        }

        return result;
    }

    /**
     * Validate a task parameter entry
     * 
     * @param taskParameterEntry the task parameter entry to validate
     * @param result The validation result to append to
     * @return The result of the validation
     */
    private AxValidationResult vaildateTaskParameterEntry(final Entry<String, AxTaskParameter> taskParameterEntry,
            AxValidationResult result) {
        if (taskParameterEntry.getValue() == null) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "null input task parameer value found on task parameter " + taskParameterEntry.getKey()));
        } else {
            if (!taskParameterEntry.getValue().getKey().getParentArtifactKey().equals(key)) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "parent key on task parameter " + taskParameterEntry.getKey() + DOES_NOT_EQUAL_TASK_KEY));
            }

            result = taskParameterEntry.getValue().validate(result);
        }

        return result;
    }

    /**
     * Validate a context album reference entry
     * 
     * @param taskParameterEntry the context album reference entry to validate
     * @param result The validation result to append to
     * @return The result of the validation
     */
    private AxValidationResult vaildateContextAlbumReference(final AxArtifactKey contextAlbumReference,
            AxValidationResult result) {
        if (contextAlbumReference.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key on context item reference entry " + contextAlbumReference.getKey()
                            + " may not be the null key"));
        }

        return contextAlbumReference.validate(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        for (final AxInputField inputField : inputFields.values()) {
            inputField.clean();
        }
        for (final AxOutputField outputField : outputFields.values()) {
            outputField.clean();
        }
        for (final AxTaskParameter parameter : taskParameters.values()) {
            parameter.clean();
        }
        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            contextAlbumReference.clean();
        }
        taskLogic.clean();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("key=");
        builder.append(key);
        builder.append(",inputFields=");
        builder.append(inputFields);
        builder.append(",outputFields=");
        builder.append(outputFields);
        builder.append(",taskParameters=");
        builder.append(taskParameters);
        builder.append(",contextAlbumReferenceSet=");
        builder.append(contextAlbumReferenceSet);
        builder.append(",taskLogic=");
        builder.append(taskLogic);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.
     * basicmodel.concepts.AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxTask.class);

        final AxTask copy = ((AxTask) copyObject);
        copy.setKey(key);

        final Map<String, AxInputField> newInputFields = new TreeMap<>();
        for (final Entry<String, AxInputField> inputFieldEntry : inputFields.entrySet()) {
            newInputFields.put(inputFieldEntry.getKey(), new AxInputField(inputFieldEntry.getValue()));
        }
        copy.setInputFields(newInputFields);

        final Map<String, AxOutputField> newOutputFields = new TreeMap<>();
        for (final Entry<String, AxOutputField> outputFieldEntry : outputFields.entrySet()) {
            newOutputFields.put(outputFieldEntry.getKey(), new AxOutputField(outputFieldEntry.getValue()));
        }
        copy.setOutputFields(newOutputFields);

        final Map<String, AxTaskParameter> newTaskParameter = new TreeMap<>();
        for (final Entry<String, AxTaskParameter> taskParameterEntry : taskParameters.entrySet()) {
            newTaskParameter.put(taskParameterEntry.getKey(), new AxTaskParameter(taskParameterEntry.getValue()));
        }
        copy.setTaskParameters(newTaskParameter);

        final Set<AxArtifactKey> newContextUsage = new TreeSet<>();
        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            newContextUsage.add(new AxArtifactKey(contextAlbumReference));
        }
        copy.setContextAlbumReferences(newContextUsage);

        copy.setTaskLogic(new AxTaskLogic((AxLogic) taskLogic));

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + inputFields.hashCode();
        result = prime * result + outputFields.hashCode();
        result = prime * result + taskParameters.hashCode();
        result = prime * result + contextAlbumReferenceSet.hashCode();
        result = prime * result + taskLogic.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxTask other = (AxTask) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!inputFields.equals(other.inputFields)) {
            return false;
        }
        if (!outputFields.equals(other.outputFields)) {
            return false;
        }
        if (!taskParameters.equals(other.taskParameters)) {
            return false;
        }
        if (!contextAlbumReferenceSet.equals(other.contextAlbumReferenceSet)) {
            return false;
        }
        return taskLogic.equals(other.taskLogic);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null) {
            return -1;
        }
        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxTask other = (AxTask) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!inputFields.equals(other.inputFields)) {
            return (inputFields.hashCode() - other.inputFields.hashCode());
        }
        if (!outputFields.equals(other.outputFields)) {
            return (outputFields.hashCode() - other.outputFields.hashCode());
        }
        if (!taskParameters.equals(other.taskParameters)) {
            return (taskParameters.hashCode() - other.taskParameters.hashCode());
        }
        if (!contextAlbumReferenceSet.equals(other.contextAlbumReferenceSet)) {
            return (contextAlbumReferenceSet.hashCode() - other.contextAlbumReferenceSet.hashCode());
        }
        return taskLogic.compareTo(other.taskLogic);
    }
}
