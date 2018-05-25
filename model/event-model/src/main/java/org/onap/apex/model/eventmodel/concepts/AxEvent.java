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

package org.onap.apex.model.eventmodel.concepts;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxConcept;
import org.onap.apex.model.basicmodel.concepts.AxKey;
import org.onap.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class defines an Apex event. An {@link AxEvent} is used to kick off execution of policies in Apex and is emitted
 * by policies when they completer execution. In addition, Apex uses {@link AxEvent} instances internally to pass
 * control from one Apex state to the next during execution.
 * <p>
 * The {@link AxArtifactKey} of an event uniquely identifies it in an Apex system and the name field in the key is the
 * name of the event.
 * <p>
 * Each {@link AxEvent} has a name space, which is usually set to identify the domain of application of an event. For
 * example a 4G cell power event might have the name space {@code org.onap.radio.4g} and the name {@code PowerEvent}.
 * The source and target of the event are reserved to hold an identifier that defines the sender and receiver of an
 * event respectively. The definition and structure of these fields is reserved for future use and their use by
 * applications is currently not recommended.
 * <p>
 * The parameters that an event has are defined as a map of {@link AxField} instances.
 * <p>
 * Validation checks that the event key is valid. If name space is a blank string, a warning is issued. Blank source or
 * target fields result in observations being issued. An event may not have any parameters. If it has parameters, the
 * name and value of each parameter entry is checked to ensure they are not null. Then the local name of each parameter
 * is checked to ensure it matches the event parameter key on the event. Finally, the parent key of each parameter is
 * checked to ensure it matches the event key.
 */
@Entity
@Table(name = "AxEvent")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexEvent", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxEvent", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = { "key", "nameSpace", "source", "target", "parameterMap" })

public class AxEvent extends AxConcept {
    private static final long serialVersionUID = -1460388382582984269L;

    private static final String WHITESPACE_REGEXP = "\\s+$";

    /** The key of the event, unique in the Apex system. */
    @EmbeddedId
    @XmlElement(name = "key", required = true)
    // CHECKSTYLE:OFF: checkstyle:VisibilityMonitor
    protected AxArtifactKey key;
    // CHECKSTYLE:ON: checkstyle:VisibilityMonitor

    @Column(name = "nameSpace")
    @XmlElement(required = true)
    private String nameSpace;

    @Column(name = "source")
    @XmlElement(required = true)
    private String source;

    @Column(name = "target")
    @XmlElement(required = true)
    private String target;

    @OneToMany(cascade = CascadeType.ALL)
    @XmlElement(name = "parameter", required = true)
    private Map<String, AxField> parameterMap;

    /**
     * The default constructor creates an event with a null artifact key. The event name space, source, and target are
     * all defined as empty strings and the parameter map is initialized as an empty map.
     */
    public AxEvent() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor
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
        this(key, "", "", "", new TreeMap<String, AxField>());
    }

    /**
     * This constructor creates an event with the given artifact key and name space. The event source, and target are
     * all defined as empty strings and the parameter map is initialized as an empty map.
     *
     * @param key the key of the event
     * @param nameSpace the name space of the event
     */
    public AxEvent(final AxArtifactKey key, final String nameSpace) {
        this(key, nameSpace, "", "", new TreeMap<String, AxField>());
    }

    /**
     * This constructor creates an event with the given artifact key, name space, source and target. The parameter map
     * is initialized as an empty map.
     *
     * @param key the key of the event
     * @param nameSpace the name space of the event
     * @param source the source of the event
     * @param target the target of the event
     */
    public AxEvent(final AxArtifactKey key, final String nameSpace, final String source, final String target) {
        this(key, nameSpace, source, target, new TreeMap<String, AxField>());
    }

    /**
     * This constructor creates an event with all its fields defined.
     *
     * @param key the key of the event
     * @param nameSpace the name space of the event
     * @param source the source of the event
     * @param target the target of the event
     * @param parameterMap the map of parameters that the event has
     */
    public AxEvent(final AxArtifactKey key, final String nameSpace, final String source, final String target,
            final SortedMap<String, AxField> parameterMap) {
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
     * When an event is unmarshalled from disk or from the database, the parent key in the reference keys in its
     * parameter map are not set. This method is called by JAXB after unmarshaling and is used to set the parent key of
     * the {@link AxField} instances in the parameter map to be the key of the event that contains them.
     *
     * @param u the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller u, final Object parent) {
        for (final AxField parameter : parameterMap.values()) {
            parameter.getKey().setParentArtifactKey(key);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#getKeys()
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
     * Gets the name space of the event.
     *
     * @return the name space of the event
     */
    public String getNameSpace() {
        return nameSpace;
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
     * Gets the source of the event.
     *
     * @return the source of the event
     */
    public String getSource() {
        return source;
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
     * Gets the target of the event.
     *
     * @return the target of the event
     */
    public String getTarget() {
        return target;
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
     * Gets the event parameter map.
     *
     * @return the event parameter map
     */
    public Map<String, AxField> getParameterMap() {
        return parameterMap;
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

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.apex.model.basicmodel.concepts.
     * AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (nameSpace.replaceAll(WHITESPACE_REGEXP, "").length() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.WARNING,
                    "nameSpace on event is blank"));
        }

        if (source.replaceAll(WHITESPACE_REGEXP, "").length() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.OBSERVATION,
                    "source on event is blank"));
        }

        if (target.replaceAll(WHITESPACE_REGEXP, "").length() == 0) {
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
                result = vaidateEventParameters(eventParameterEntry, result);
            }
        }

        return result;
    }

    /**
     * Validate an event parameter entry
     *
     * @param eventParameterEntry the event parameter entry
     * @param result the validation result to append to
     * @return The validation result
     */
    private AxValidationResult vaidateEventParameters(final Entry<String, AxField> eventParameterEntry,
            final AxValidationResult result) {
        if (!eventParameterEntry.getKey().equals(eventParameterEntry.getValue().getKey().getLocalName())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key on parameter " + eventParameterEntry.getKey() + " does not equal parameter field local name "
                            + eventParameterEntry.getValue().getKey().getLocalName()));
        }

        if (!eventParameterEntry.getValue().getKey().getParentArtifactKey().equals(key)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "parent key on parameter field " + eventParameterEntry.getValue().getKey()
                            + " does not equal event key"));
        }

        return eventParameterEntry.getValue().validate(result);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        nameSpace = nameSpace.trim();
        source = source.trim();
        target = target.trim();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("key=");
        builder.append(key);
        builder.append(",nameSpace=");
        builder.append(nameSpace);
        builder.append(",source=");
        builder.append(source);
        builder.append(",target=");
        builder.append(target);
        builder.append(",parameter=");
        builder.append(parameterMap);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.apex.model.basicmodel.concepts. AxConcept)
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

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#hashCode()
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
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
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
        return parameterMap.equals(other.parameterMap);
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

        final AxEvent other = (AxEvent) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!nameSpace.equals(other.nameSpace)) {
            return nameSpace.compareTo(other.nameSpace);
        }
        if (!source.equals(other.source)) {
            return target.compareTo(other.source);
        }
        if (!target.equals(other.target)) {
            return target.compareTo(other.target);
        }
        if (!parameterMap.equals(other.parameterMap)) {
            return (parameterMap.hashCode() - other.parameterMap.hashCode());
        }

        return 0;
    }
}
