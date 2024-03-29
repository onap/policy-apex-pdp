/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020,2022 Nordix Foundation.
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
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetterImpl;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class is a task container and holds a map of the tasks for an entire Apex model. All Apex
 * models that use tasks must have an {@link AxTasks} field. The {@link AxTasks} class implements
 * the helper methods of the {@link AxConceptGetter} interface to allow {@link AxTask} instances to
 * be retrieved by calling methods directly on this class without referencing the contained map.
 *
 * <p>Validation checks that the container key is not null. An error is issued if no tasks are defined
 * in the container. Each task entry is checked to ensure that its key and value are not null and
 * that the key matches the key in the map value. Each task entry is then validated individually.
 */
public class AxTasks extends AxConcept implements AxConceptGetter<AxTask> {
    private static final long serialVersionUID = 4290442590545820316L;

    private AxArtifactKey key;
    private Map<AxArtifactKey, AxTask> taskMap;

    /**
     * The Default Constructor creates a {@link AxTasks} object with a null artifact key and creates
     * an empty event map.
     */
    public AxTasks() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxTasks(final AxTasks copyConcept) {
        super(copyConcept);
    }

    /**
     * The Keyed Constructor creates a {@link AxTasks} object with the given artifact key and
     * creates an empty event map.
     *
     * @param key the key
     */
    public AxTasks(final AxArtifactKey key) {
        this(key, new TreeMap<>());
    }

    /**
     * This Constructor creates a task container with all of its fields defined.
     *
     * @param key     the task container key
     * @param taskMap the tasks to be stored in the task container
     */
    public AxTasks(final AxArtifactKey key, final Map<AxArtifactKey, AxTask> taskMap) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(taskMap, "taskMap may not be null");

        this.key = key;
        this.taskMap = new TreeMap<>();
        this.taskMap.putAll(taskMap);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();

        for (final AxTask task : taskMap.values()) {
            keyList.addAll(task.getKeys());
        }

        return keyList;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void buildReferences() {
        taskMap.values().stream().forEach(task -> task.buildReferences());
    }

    /**
     * Sets the task container key.
     *
     * @param key the task container key
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the tasks stored in the task container.
     *
     * @return the tasks stored in the task container
     */
    public Map<AxArtifactKey, AxTask> getTaskMap() {
        return taskMap;
    }

    /**
     * Sets the tasks to be stored in the task container.
     *
     * @param taskMap the tasks to be stored in the task container
     */
    public void setTaskMap(final Map<AxArtifactKey, AxTask> taskMap) {
        Assertions.argumentNotNull(taskMap, "taskMap may not be null");
        this.taskMap = new TreeMap<>();
        this.taskMap.putAll(taskMap);
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

        if (taskMap.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "taskMap may not be empty"));
        } else {
            for (final Entry<AxArtifactKey, AxTask> taskEntry : taskMap.entrySet()) {
                if (taskEntry.getKey().equals(AxArtifactKey.getNullKey())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "key on task entry " + taskEntry.getKey() + " may not be the null key"));
                } else if (taskEntry.getValue() == null) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "value on task entry " + taskEntry.getKey() + " may not be null"));
                } else {
                    if (!taskEntry.getKey().equals(taskEntry.getValue().getKey())) {
                        result.addValidationMessage(new AxValidationMessage(key, this.getClass(),
                            ValidationResult.INVALID, "key on task entry key " + taskEntry.getKey()
                            + " does not equal task value key " + taskEntry.getValue().getKey()));
                    }

                    result = taskEntry.getValue().validate(result);
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        for (final Entry<AxArtifactKey, AxTask> taskEntry : taskMap.entrySet()) {
            taskEntry.getKey().clean();
            taskEntry.getValue().clean();
        }
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
        builder.append(",taskMap=");
        builder.append(taskMap);
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
        Assertions.instanceOf(copyObject, AxTasks.class);

        final AxTasks copy = ((AxTasks) copyObject);
        copy.setKey(new AxArtifactKey(key));

        final Map<AxArtifactKey, AxTask> newTaskMap = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxTask> taskMapEntry : taskMap.entrySet()) {
            newTaskMap.put(new AxArtifactKey(taskMapEntry.getKey()), new AxTask(taskMapEntry.getValue()));
        }
        copy.setTaskMap(newTaskMap);

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
        result = prime * result + taskMap.hashCode();
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

        final AxTasks other = (AxTasks) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return taskMap.equals(other.taskMap);
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

        final AxTasks other = (AxTasks) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!taskMap.equals(other.taskMap)) {
            return (taskMap.hashCode() - other.taskMap.hashCode());
        }

        return 0;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxTask get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxTask>) taskMap).get(conceptKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxTask get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxTask>) taskMap).get(conceptKeyName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxTask get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxTask>) taskMap).get(conceptKeyName,
            conceptKeyVersion);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<AxTask> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxTask>) taskMap).getAll(conceptKeyName);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Set<AxTask> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxTask>) taskMap).getAll(conceptKeyName,
            conceptKeyVersion);
    }
}
