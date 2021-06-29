/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2021 Nordix Foundation.
 *  Modifications Copyright (C) 2021 Bell Canada. All rights reserved.
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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKeyUse;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.eventmodel.concepts.AxEvent;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class holds the definition of a task in Apex. A task is executed by a state and performs
 * some domain specific logic to carry out work required to be done by a policy. The Task Logic that
 * is executed by a task is held in a {@link AxTaskLogic} instance.
 *
 * <p>A task has a set of input fields and output fields, which are passed to and are emitted from the
 * task during a task execution cycle. A task may have task parameters {@link AxTaskParameter},
 * which are configuration values passed to a task at initialization time.
 *
 * <p>The Task Logic in a task may use information in context albums to perform their domain specific
 * work. The context albums that the task uses and that should be made available to the task by Apex
 * policy distribution are held as a set of references to context albums in the task.
 *
 * <p>During validation of a task, the validation checks listed below are executed:
 * <ol>
 * <li>The task key must not be a null key and must be valid, see validation in
 * {@link AxArtifactKey}
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
@XmlType(
    name = "AxTask",
    namespace = "http://www.onap.org/policy/apex-pdp",
    propOrder = {"key", "inputEvent", "outputEvents", "taskParameters",
        "contextAlbumReferenceSet", "taskLogic"})
@Getter
@Setter
public class AxTask extends AxConcept {
    private static final String DOES_NOT_EQUAL_TASK_KEY = " does not equal task key";

    private static final long serialVersionUID = 5374237330697362762L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    @NonNull
    private AxArtifactKey key;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinTable(
        name = "INPUT_EVENT_JT",
        joinColumns = {@JoinColumn(name = "inEventTaskName", referencedColumnName = "name", updatable = false),
            @JoinColumn(name = "inEventTaskVersion", referencedColumnName = "version", updatable = false)})
    @XmlElement(name = "inputEvent", required = false)
    private AxEvent inputEvent;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "OUTPUT_EVENT_JT",
        joinColumns = {@JoinColumn(name = "outEventTaskName", referencedColumnName = "name", updatable = false),
            @JoinColumn(name = "outEventTaskVersion", referencedColumnName = "version", updatable = false)})
    @XmlElement(name = "outputEvents", required = false)
    private Map<String, AxEvent> outputEvents;

    @OneToMany(cascade = CascadeType.ALL)
    @XmlElement(name = "taskParameters", required = true)
    private Map<String, AxTaskParameter> taskParameters;

    // @formatter:off
    @ElementCollection
    @CollectionTable(joinColumns = {@JoinColumn(name = "contextAlbumName", referencedColumnName = "name"),
        @JoinColumn(name = "contextAlbumVersion", referencedColumnName = "version")})
    @XmlElement(name = "contextAlbumReference")
    @NonNull
    private Set<AxArtifactKey> contextAlbumReferenceSet;
    // @formatter:on

    @OneToOne(cascade = CascadeType.ALL)
    @XmlElement(required = true)
    @NonNull
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
     * Copy constructor.
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
                new TreeMap<>(), // Task Parameters
                new TreeSet<>(), // Context Album References
                new AxTaskLogic(new AxReferenceKey(key)) // Task Logic
        );
    }

    /**
     * This Constructor defines all the fields of the task.
     *
     * @param key the key of the task
     * @param taskParameters the task parameters that are used to initialize tasks of this type
     * @param contextAlbumReferenceSet the context album reference set defines the context that may
     *        be used by Task Logic in the state
     * @param taskLogic the task logic that performs the domain specific work of the task
     */
    public AxTask(final AxArtifactKey key, final Map<String, AxTaskParameter> taskParameters,
        final Set<AxArtifactKey> contextAlbumReferenceSet, final AxTaskLogic taskLogic) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(taskParameters, "taskParameters may not be null");
        Assertions.argumentNotNull(contextAlbumReferenceSet, "contextAlbumReferenceSet may not be null");
        Assertions.argumentNotNull(taskLogic, "taskLogic may not be null");

        this.key = key;
        this.taskParameters = taskParameters;
        this.contextAlbumReferenceSet = contextAlbumReferenceSet;
        this.taskLogic = taskLogic;
    }

    /**
     * When a task is unmarshalled from disk or from the database, the parent of contained objects
     * is not defined. This method is called by JAXB after unmarshaling and is used to set the
     * parent keys of all {@link AxTaskParameter} instance in the task.
     *
     * @param unmarshaler the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller unmarshaler, final Object parent) {
        taskLogic.getKey().setParentArtifactKey(key);
        for (final AxTaskParameter parameter : taskParameters.values()) {
            parameter.getKey().setParentArtifactKey(key);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();
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
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        for (final Entry<String, AxTaskParameter> taskParameterEntry : taskParameters.entrySet()) {
            result = validateTaskParameterEntry(taskParameterEntry, result);
        }

        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            result = validateContextAlbumReference(contextAlbumReference, result);
        }

        if (!taskLogic.getKey().getParentArtifactKey().equals(key)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "taskLogic parent key " + taskLogic.getKey().getId() + DOES_NOT_EQUAL_TASK_KEY));
        }

        return taskLogic.validate(result);
    }

    /**
     * Validate a task parameter entry.
     *
     * @param taskParameterEntry the task parameter entry to validate
     * @param result The validation result to append to
     * @return The result of the validation
     */
    private AxValidationResult validateTaskParameterEntry(final Entry<String, AxTaskParameter> taskParameterEntry,
            AxValidationResult result) {
        if (taskParameterEntry.getValue() == null) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "null input task parameter value found on task parameter " + taskParameterEntry.getKey()));
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
     * Validate a context album reference entry.
     *
     * @param contextAlbumReference the context album reference entry to validate
     * @param result The validation result to append to
     * @return The result of the validation
     */
    private AxValidationResult validateContextAlbumReference(final AxArtifactKey contextAlbumReference,
            AxValidationResult result) {
        if (contextAlbumReference.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key on context item reference entry " + contextAlbumReference.getKey()
                            + " may not be the null key"));
        }

        return contextAlbumReference.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        for (final AxTaskParameter parameter : taskParameters.values()) {
            parameter.clean();
        }
        for (final AxArtifactKey contextAlbumReference : contextAlbumReferenceSet) {
            contextAlbumReference.clean();
        }
        taskLogic.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("key=");
        builder.append(key);
        builder.append(",taskParameters=");
        builder.append(taskParameters);
        builder.append(",contextAlbumReferenceSet=");
        builder.append(contextAlbumReferenceSet);
        builder.append(",taskLogic=");
        builder.append(taskLogic);
        builder.append(")");
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxTask.class);

        final AxTask copy = ((AxTask) copyObject);
        copy.setKey(key);

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

        copy.setTaskLogic(new AxTaskLogic(taskLogic));

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + taskParameters.hashCode();
        result = prime * result + contextAlbumReferenceSet.hashCode();
        result = prime * result + taskLogic.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
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
        if (!taskParameters.equals(other.taskParameters)) {
            return false;
        }
        if (!contextAlbumReferenceSet.equals(other.contextAlbumReferenceSet)) {
            return false;
        }
        return taskLogic.equals(other.taskLogic);
    }

    /**
     * {@inheritDoc}.
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
        if (!taskParameters.equals(other.taskParameters)) {
            return (taskParameters.hashCode() - other.taskParameters.hashCode());
        }
        if (!contextAlbumReferenceSet.equals(other.contextAlbumReferenceSet)) {
            return (contextAlbumReferenceSet.hashCode() - other.contextAlbumReferenceSet.hashCode());
        }
        return taskLogic.compareTo(other.taskLogic);
    }
}
