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
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.apex.model.basicmodel.concepts.AxConcept;
import org.onap.apex.model.basicmodel.concepts.AxConceptGetter;
import org.onap.apex.model.basicmodel.concepts.AxConceptGetterImpl;
import org.onap.apex.model.basicmodel.concepts.AxKey;
import org.onap.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class is an event container and holds a map of the events for an entire Apex model. All Apex models that use
 * events must have an {@link AxEvents} field. The {@link AxEvents} class implements the helper methods of the
 * {@link AxConceptGetter} interface to allow {@link AxEvents} instances to be retrieved by calling methods directly on
 * this class without referencing the contained map.
 * <p>
 * Validation checks that the container key is not null. An error is issued if no events are defined in the container.
 * Each event entry is checked to ensure that its key and value are not null and that the key matches the key in the map
 * value. Each event entry is then validated individually.
 */
@Entity
@Table(name = "AxEvents")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxEvents", namespace = "http://www.onap.org/policy/apex-pdp", propOrder = { "key", "eventMap" })

public class AxEvents extends AxConcept implements AxConceptGetter<AxEvent> {
    private static final long serialVersionUID = 4290442590545820316L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    // @formatter:off
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = { @JoinColumn(name = "eventMapName", referencedColumnName = "name"),
                    @JoinColumn(name = "eventMapVersion", referencedColumnName = "version") },
            inverseJoinColumns = { @JoinColumn(name = "eventName", referencedColumnName = "name"),
                    @JoinColumn(name = "eventVersion", referencedColumnName = "version") })
    @XmlElement(required = true)
    private Map<AxArtifactKey, AxEvent> eventMap;
    // @formatter:on

    /**
     * The Default Constructor creates a {@link AxEvents} object with a null artifact key and creates an empty event
     * map.
     */
    public AxEvents() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor
     *
     * @param copyConcept the concept to copy from
     */
    public AxEvents(final AxEvents copyConcept) {
        super(copyConcept);
    }

    /**
     * The Key Constructor creates a {@link AxEvents} object with the given artifact key and creates an empty event map.
     *
     * @param key the event container key
     */
    public AxEvents(final AxArtifactKey key) {
        this(key, new TreeMap<AxArtifactKey, AxEvent>());
    }

    /**
     * This Constructor creates an event container with all of its fields defined.
     *
     * @param key the event container key
     * @param eventMap the events to be stored in the event container
     */
    public AxEvents(final AxArtifactKey key, final Map<AxArtifactKey, AxEvent> eventMap) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(eventMap, "eventMap may not be null");

        this.key = key;
        this.eventMap = new TreeMap<>();
        this.eventMap.putAll(eventMap);
    }

    /**
     * When a model is unmarshalled from disk or from the database, the event map is returned as a raw hash map. This
     * method is called by JAXB after unmarshaling and is used to convert the hash map to a {@link NavigableMap} so that
     * it will work with the {@link AxConceptGetter} interface.
     *
     * @param u the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller u, final Object parent) {
        // The map must be navigable to allow name and version searching, unmarshaling returns a hash map
        final NavigableMap<AxArtifactKey, AxEvent> navigableEventMap = new TreeMap<>();
        navigableEventMap.putAll(eventMap);
        eventMap = navigableEventMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = key.getKeys();

        for (final AxEvent event : eventMap.values()) {
            keyList.addAll(event.getKeys());
        }

        return keyList;
    }

    /**
     * Sets the key of the event container.
     *
     * @param key the event container key
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the event map containing the events in the event container.
     *
     * @return the event map with all the events in the event container
     */
    public Map<AxArtifactKey, AxEvent> getEventMap() {
        return eventMap;
    }

    /**
     * Sets the event map containing the events in the event container.
     *
     * @param eventMap the event map containing the events in the event container
     */
    public void setEventMap(final Map<AxArtifactKey, AxEvent> eventMap) {
        Assertions.argumentNotNull(eventMap, "eventMap may not be null");
        this.eventMap = new TreeMap<>();
        this.eventMap.putAll(eventMap);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#validate(com.ericsson.apex.model.basicmodel.concepts.
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

        if (eventMap.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "eventMap may not be empty"));
        } else {
            for (final Entry<AxArtifactKey, AxEvent> eventEntry : eventMap.entrySet()) {
                if (eventEntry.getKey().equals(AxArtifactKey.getNullKey())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key on event entry " + eventEntry.getKey() + " may not be the null key"));
                } else if (eventEntry.getValue() == null) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "value on event entry " + eventEntry.getKey() + " may not be null"));
                } else {
                    if (!eventEntry.getKey().equals(eventEntry.getValue().getKey())) {
                        result.addValidationMessage(new AxValidationMessage(key, this.getClass(),
                                ValidationResult.INVALID, "key on event entry key " + eventEntry.getKey()
                                        + " does not equal event value key " + eventEntry.getValue().getKey()));
                    }

                    result = eventEntry.getValue().validate(result);
                }
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        for (final Entry<AxArtifactKey, AxEvent> eventEntry : eventMap.entrySet()) {
            eventEntry.getKey().clean();
            eventEntry.getValue().clean();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("key=");
        builder.append(key);
        builder.append(",eventMap=");
        builder.append(eventMap);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#copyTo(com.ericsson.apex.model.basicmodel.concepts.
     * AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxEvents.class);

        final AxEvents copy = (AxEvents) copyObject;
        copy.setKey(new AxArtifactKey(key));
        final Map<AxArtifactKey, AxEvent> newEventMap = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxEvent> eventMapEntry : eventMap.entrySet()) {
            newEventMap.put(new AxArtifactKey(eventMapEntry.getKey()), new AxEvent(eventMapEntry.getValue()));
        }
        copy.setEventMap(newEventMap);

        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key.hashCode();
        result = prime * result + eventMap.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
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

        final AxEvents other = (AxEvents) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return eventMap.equals(other.eventMap);
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

        final AxEvents other = (AxEvents) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!eventMap.equals(other.eventMap)) {
            return (eventMap.hashCode() - other.eventMap.hashCode());
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConceptGetter#get(com.ericsson.apex.model.basicmodel.concepts.
     * AxArtifactKey)
     */
    @Override
    public AxEvent get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxEvent>) eventMap).get(conceptKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConceptGetter#get(java.lang.String)
     */
    @Override
    public AxEvent get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxEvent>) eventMap).get(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConceptGetter#get(java.lang.String, java.lang.String)
     */
    @Override
    public AxEvent get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxEvent>) eventMap).get(conceptKeyName,
                conceptKeyVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String)
     */
    @Override
    public Set<AxEvent> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxEvent>) eventMap).getAll(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String, java.lang.String)
     */
    @Override
    public Set<AxEvent> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxEvent>) eventMap).getAll(conceptKeyName,
                conceptKeyVersion);
    }
}
