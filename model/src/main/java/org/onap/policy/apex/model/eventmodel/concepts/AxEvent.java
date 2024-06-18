/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020, 2022, 2024 Nordix Foundation.
 *  Modifications Copyright (C) 2022 Bell Canada.
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

package org.onap.policy.apex.model.eventmodel.concepts;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxToscaPolicyProcessingStatus;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class defines an Apex event. An {@link AxEvent} is used to kick off execution of policies in Apex and is emitted
 * by policies when they complete execution. In addition, Apex uses {@link AxEvent} instances internally to pass
 * control from one Apex state to the next during execution.
 *
 * <p>The {@link AxArtifactKey} of an event uniquely identifies it in an Apex system and the name field in the key is
 * the name of the event.
 *
 * <p>Each {@link AxEvent} has a name space, which is usually set to identify the domain of application of an event. For
 * example a 4G cell power event might have the name space {@code org.onap.radio.4g} and the name {@code PowerEvent}.
 * The source and target of the event are reserved to hold an identifier that defines the sender and receiver of an
 * event respectively. The definition and structure of these fields is reserved for future use and their use by
 * applications is currently not recommended.
 *
 * <p>The parameters that an event has are defined as a map of {@link AxField} instances.
 *
 * <p>Validation checks that the event key is valid. If name space is a blank string, a warning is issued. Blank source
 * or target fields result in observations being issued. An event may not have any parameters. If it has parameters, the
 * name and value of each parameter entry is checked to ensure they are not null. Then the local name of each parameter
 * is checked to ensure it matches the event parameter key on the event. Finally, the parent key of each parameter is
 * checked to ensure it matches the event key.
 */
public class AxEvent extends AxConcept {

    @Serial
    private static final long serialVersionUID = -1460388382582984269L;

    /**
     * The key of the event, unique in the Apex system.
     */
    // CHECKSTYLE:OFF: checkstyle:VisibilityMonitor
    protected AxArtifactKey key;
    // CHECKSTYLE:ON: checkstyle:VisibilityMonitor

    @Getter
    private String nameSpace;

    @Getter
    private String source;

    @Getter
    private String target;

    @Getter
    @SerializedName("parameter")
    private Map<String, AxField> parameterMap;

    @Setter
    @Getter
    private String toscaPolicyState;

    /**
     * The default constructor creates an event with a null artifact key. The event name space, source, and target are
     * all defined as empty strings and the parameter map is initialized as an empty map.
     */
    public AxEvent() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxEvent(final AxEvent copyConcept) {
        super(copyConcept);
    }

    /**
     * The default constructor creates an event with the given artifact key. The event name space, source, and target
     * are all defined as empty strings and the parameter map is initialized as an empty map.
     *
     * @param key the key of the event
     */
    public AxEvent(final AxArtifactKey key) {
        this(key, "", "", "", new TreeMap<>(), "");
    }

    /**
     * This constructor creates an event with the given artifact key and name space. The event source, and target are
     * all defined as empty strings and the parameter map is initialized as an empty map.
     *
     * @param key       the key of the event
     * @param nameSpace the name space of the event
     */
    public AxEvent(final AxArtifactKey key, final String nameSpace) {
        this(key, nameSpace, "", "", new TreeMap<>(), "");
    }

    /**
     * This constructor creates an event with the given artifact key, name space, source and target. The parameter map
     * is initialized as an empty map.
     *
     * @param key       the key of the event
     * @param nameSpace the name space of the event
     * @param source    the source of the event
     * @param target    the target of the event
     */
    public AxEvent(final AxArtifactKey key, final String nameSpace, final String source, final String target) {
        this(key, nameSpace, source, target, new TreeMap<>(), "");
    }

    /**
     * This constructor creates an event with all its fields defined.
     *
     * @param key              the key of the event
     * @param nameSpace        the name space of the event
     * @param source           the source of the event
     * @param target           the target of the event
     * @param parameterMap     the map of parameters that the event has
     * @param toscaPolicyState the TOSCA policy processing status that event is flagged with
     */
    public AxEvent(final AxArtifactKey key, final String nameSpace, final String source, final String target,
                   final SortedMap<String, AxField> parameterMap, final String toscaPolicyState) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(nameSpace, "nameSpace may not be null");
        Assertions.argumentNotNull(source, "source may not be null");
        Assertions.argumentNotNull(target, "target may not be null");
        Assertions.argumentNotNull(parameterMap, "parameterMap may not be null");

        this.key = key;
        this.nameSpace = nameSpace;
        this.source = source;
        this.target = target;
        this.parameterMap = parameterMap;
        this.toscaPolicyState = toscaPolicyState;
    }

    /**
     * This method checks that an event has all the fields in the {@code otherFieldSet} set defined on it.
     *
     * @param otherFieldSet the set of fields to check for existence on this event
     * @return true, if all the {@code otherFieldSet} fields are defined on this event
     */
    public boolean hasFields(final Set<AxField> otherFieldSet) {
        return parameterMap.values().containsAll(otherFieldSet);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void buildReferences() {
        for (final AxField parameter : parameterMap.values()) {
            parameter.getKey().setParentArtifactKey(key);
        }
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

        for (final AxField field : parameterMap.values()) {
            keyList.addAll(field.getKeys());
        }
        return keyList;
    }

    /**
     * Sets the key of the event.
     *
     * @param key the key of the event
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;

        for (final AxField parameter : parameterMap.values()) {
            parameter.getKey().setParentArtifactKey(key);
        }
    }

    /**
     * Sets the name space of the event.
     *
     * @param nameSpace the name space of the event
     */
    public void setNameSpace(final String nameSpace) {
        Assertions.argumentNotNull(nameSpace, "nameSpace may not be null");
        this.nameSpace = nameSpace.trim();
    }

    /**
     * Sets the source of the event.
     *
     * @param source the source of the event
     */
    public void setSource(final String source) {
        Assertions.argumentNotNull(source, "source may not be null");
        this.source = source.trim();
    }

    /**
     * Sets the target of the event.
     *
     * @param target the target of the event
     */
    public void setTarget(final String target) {
        Assertions.argumentNotNull(target, "target may not be null");
        this.target = target.trim();
    }

    /**
     * Gets the fields defined on the event as a set.
     *
     * @return the fields defined on the event as a set
     */
    public Set<AxField> getFields() {
        return new TreeSet<>(parameterMap.values());
    }

    /**
     * Sets the event parameter map, containing all the fields of the event.
     *
     * @param parameterMap the event parameter map
     */
    public void setParameterMap(final Map<String, AxField> parameterMap) {
        Assertions.argumentNotNull(parameterMap, "parameterMap may not be null");
        this.parameterMap = parameterMap;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "key is a null key"));
        }

        result = key.validate(result);

        if (nameSpace.replaceAll(WHITESPACE_REGEX, "").isEmpty()) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.WARNING,
                "nameSpace on event is blank"));
        }

        if (source.replaceAll(WHITESPACE_REGEX, "").isEmpty()) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                "source on event is blank"));
        }

        if (target.replaceAll(WHITESPACE_REGEX, "").isEmpty()) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                "target on event is blank"));
        }

        for (final Entry<String, AxField> eventParameterEntry : parameterMap.entrySet()) {
            if (eventParameterEntry.getKey() == null || eventParameterEntry.getKey().equals(AxKey.NULL_KEY_NAME)) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key on parameter " + eventParameterEntry.getKey() + " may not be the null key"));
            } else if (eventParameterEntry.getValue() == null) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "value on parameter " + eventParameterEntry.getKey() + " may not be null"));
            } else {
                result = validateEventParameters(eventParameterEntry, result);
            }
        }

        if (!Strings.isNullOrEmpty(toscaPolicyState)
            && !EnumUtils.isValidEnum(AxToscaPolicyProcessingStatus.class, toscaPolicyState)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "toscaPolicyState on event is not a valid enum. Valid values are: "
                    + Arrays.asList(AxToscaPolicyProcessingStatus.values())));
        }

        return result;
    }

    /**
     * Validate an event parameter entry.
     *
     * @param eventParameterEntry the event parameter entry
     * @param result              the validation result to append to
     * @return The validation result
     */
    private AxValidationResult validateEventParameters(final Entry<String, AxField> eventParameterEntry,
                                                       final AxValidationResult result) {
        if (!eventParameterEntry.getKey().equals(eventParameterEntry.getValue().getKey().getLocalName())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "key on parameter " + eventParameterEntry.getKey()
                    + " does not equal parameter field local name "
                    + eventParameterEntry.getValue().getKey().getLocalName()));
        }

        if (!eventParameterEntry.getValue().getKey().getParentArtifactKey().equals(key)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                "parent key on parameter field " + eventParameterEntry.getValue().getKey()
                    + " does not equal event key"));
        }

        return eventParameterEntry.getValue().validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
        nameSpace = nameSpace.trim();
        source = source.trim();
        target = target.trim();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName()
            + ":(" + "key=" + key + ",nameSpace=" + nameSpace + ",source=" + source + ",target=" + target
            + ",parameter=" + parameterMap + ",toscaPolicyState=" + toscaPolicyState + ")";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "targetObject may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxEvent.class);

        final AxEvent copy = (AxEvent) copyObject;

        final Map<String, AxField> newParameterMap = new TreeMap<>();
        for (final Entry<String, AxField> eventParameterMapEntry : parameterMap.entrySet()) {
            newParameterMap.put(eventParameterMapEntry.getKey(), new AxField(eventParameterMapEntry.getValue()));
        }
        copy.setParameterMap(newParameterMap);

        copy.setKey(new AxArtifactKey(key));
        copy.setNameSpace(nameSpace);
        copy.setSource(source);
        copy.setTarget(target);
        copy.setToscaPolicyState(toscaPolicyState);

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
        result = prime * result + nameSpace.hashCode();
        result = prime * result + source.hashCode();
        result = prime * result + target.hashCode();
        result = prime * result + parameterMap.hashCode();
        result = prime * result + toscaPolicyState.hashCode();
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

        final AxEvent other = (AxEvent) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        if (!nameSpace.equals(other.nameSpace)) {
            return false;
        }
        if (!source.equals(other.source)) {
            return false;
        }
        if (!target.equals(other.target)) {
            return false;
        }
        if (!toscaPolicyState.equals(other.toscaPolicyState)) {
            return false;
        }
        return parameterMap.equals(other.parameterMap);
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

        final AxEvent other = (AxEvent) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!nameSpace.equals(other.nameSpace)) {
            return nameSpace.compareTo(other.nameSpace);
        }
        if (!source.equals(other.source)) {
            return source.compareTo(other.source);
        }
        if (!target.equals(other.target)) {
            return target.compareTo(other.target);
        }
        if (!parameterMap.equals(other.parameterMap)) {
            return (parameterMap.hashCode() - other.parameterMap.hashCode());
        }
        if (!toscaPolicyState.equals(other.toscaPolicyState)) {
            return toscaPolicyState.compareTo(other.toscaPolicyState);
        }

        return 0;
    }
}
