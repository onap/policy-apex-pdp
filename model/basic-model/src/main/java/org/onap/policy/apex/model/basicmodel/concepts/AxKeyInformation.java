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

package org.onap.policy.apex.model.basicmodel.concepts;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

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

import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * The Class AxKeyInformation holds a map of the key information for the entire Apex model. All Apex
 * models {@link AxModel} must have an {@link AxKeyInformation} field. The {@link AxKeyInformation}
 * class implements the helper methods of the {@link AxConceptGetter} interface to allow
 * {@link AxKeyInfo} instances to be retrieved by calling methods directly on this class without
 * referencing the contained map.
 * 
 * <p>Validation checks that the key is not null, that the key information map is not empty, that each
 * key and value in the map is defined, that the key in each map entry matches the key if each entry
 * value, and that no duplicate UUIDs exist. Each key information entry is then validated
 * individually.
 */
@Entity
@Table(name = "AxKeyInformation")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxKeyInformation", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = {"key", "keyInfoMap"})

public class AxKeyInformation extends AxConcept implements AxConceptGetter<AxKeyInfo> {
    private static final long serialVersionUID = -2746380769017043888L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    // @formatter:off
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = { @JoinColumn(name = "keyInfoMapName", referencedColumnName = "name"),
                    @JoinColumn(name = "keyInfoMapVersion", referencedColumnName = "version"), },
            inverseJoinColumns = { @JoinColumn(name = "keyInfoName", referencedColumnName = "name"),
                    @JoinColumn(name = "keyInfoVersion", referencedColumnName = "version") })
    private Map<AxArtifactKey, AxKeyInfo> keyInfoMap;
    // @formatter:on

    /**
     * The Default Constructor creates this concept with a null key.
     */
    public AxKeyInformation() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxKeyInformation(final AxKeyInformation copyConcept) {
        super(copyConcept);
    }

    /**
     * Constructor to create this concept with the specified key.
     *
     * @param key the key of the concept
     */
    public AxKeyInformation(final AxArtifactKey key) {
        this(key, new TreeMap<AxArtifactKey, AxKeyInfo>());
    }

    /**
     * Constructor to create this concept and set all its fields.
     *
     * @param key the key of the concept
     * @param keyInfoMap the key info map of the concept
     */
    public AxKeyInformation(final AxArtifactKey key, final Map<AxArtifactKey, AxKeyInfo> keyInfoMap) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(keyInfoMap, "keyInfoMap may not be null");

        this.key = key;
        this.keyInfoMap = new TreeMap<>();
        this.keyInfoMap.putAll(keyInfoMap);
    }

    /**
     * When a model is unmarshalled from disk or from the database, the key information map is
     * returned as a raw Hash Map. This method is called by JAXB after unmarshaling and is used to
     * convert the hash map to a {@link NavigableMap} so that it will work with the
     * {@link AxConceptGetter} interface.
     *
     * @param um the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller um, final Object parent) {
        // The map must be navigable to allow name and version searching,
        // unmarshaling returns a hash map
        final NavigableMap<AxArtifactKey, AxKeyInfo> navigablekeyInfoMap = new TreeMap<>();
        navigablekeyInfoMap.putAll(keyInfoMap);
        keyInfoMap = navigablekeyInfoMap;
    }

    /**
     * This method generates default key information for all keys found in the concept passed in as
     * a parameter that do not already have key information.
     *
     * @param concept the concept for which to generate key information
     */
    public void generateKeyInfo(final AxConcept concept) {
        for (final AxKey axKey : concept.getKeys()) {
            if (!(axKey instanceof AxArtifactKey)) {
                continue;
            }

            final AxArtifactKey artifactKey = (AxArtifactKey) axKey;
            if (!keyInfoMap.containsKey(artifactKey)) {
                final AxKeyInfo keyInfo = new AxKeyInfo(artifactKey);
                // generate a reproducible UUID
                keyInfo.setUuid(AxKeyInfo.generateReproducibleUuid(keyInfo.getId() + keyInfo.getDescription()));
                keyInfoMap.put(artifactKey, keyInfo);
            }
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
        keyList.addAll(keyInfoMap.keySet());

        return keyList;
    }

    /**
     * Sets the key of this concept.
     *
     * @param key the key of this concept
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the key info map of this concept.
     *
     * @return the key info map of this concept
     */
    public Map<AxArtifactKey, AxKeyInfo> getKeyInfoMap() {
        return keyInfoMap;
    }

    /**
     * Sets the key info map of this concept.
     *
     * @param keyInfoMap the key info map of this concept
     */
    public void setKeyInfoMap(final Map<AxArtifactKey, AxKeyInfo> keyInfoMap) {
        Assertions.argumentNotNull(keyInfoMap, "keyInfoMap may not be null");
        this.keyInfoMap = new TreeMap<>();
        this.keyInfoMap.putAll(keyInfoMap);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.
     * basicmodel.concepts. AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(
                    new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID, "key is a null key"));
        }

        result = key.validate(result);

        if (keyInfoMap.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "keyInfoMap may not be empty"));
        } else {
            final Set<UUID> uuidSet = new TreeSet<>();

            for (final Entry<AxArtifactKey, AxKeyInfo> keyInfoEntry : keyInfoMap.entrySet()) {
                result = validateKeyInfoEntry(keyInfoEntry, uuidSet, result);
            }
        }

        return result;
    }

    /**
     * Validate an key information entry.
     *
     * @param keyInfoEntry the key information entry
     * @param uuidSet the set of UUIDs encountered in validation so far, the UUID of this entry is
     *        added to the set
     * @param result the validation result to append to
     * @return The validation result
     */
    private AxValidationResult validateKeyInfoEntry(final Entry<AxArtifactKey, AxKeyInfo> keyInfoEntry,
            final Set<UUID> uuidSet, AxValidationResult result) {
        if (keyInfoEntry.getKey().equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key on keyInfoMap entry " + keyInfoEntry.getKey() + " may not be the null key"));
        } else if (keyInfoEntry.getValue() == null) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "value on keyInfoMap entry " + keyInfoEntry.getKey() + " may not be null"));
        } else {
            if (!keyInfoEntry.getKey().equals(keyInfoEntry.getValue().getKey())) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "key on keyInfoMap entry " + keyInfoEntry.getKey() + " does not equal entry key "
                                + keyInfoEntry.getValue().getKey()));
            }

            result = keyInfoEntry.getValue().validate(result);

            if (uuidSet.contains(keyInfoEntry.getValue().getUuid())) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "duplicate UUID found on keyInfoMap entry " + keyInfoEntry.getKey() + ":"
                                + keyInfoEntry.getValue().getUuid()));
            } else {
                uuidSet.add(keyInfoEntry.getValue().getUuid());
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        for (final Entry<AxArtifactKey, AxKeyInfo> keyInfoEntry : keyInfoMap.entrySet()) {
            keyInfoEntry.getKey().clean();
            keyInfoEntry.getValue().clean();
        }
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
        builder.append(",keyInfoMap=");
        builder.append(keyInfoMap);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.
     * basicmodel.concepts. AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxKeyInformation.class);

        final AxKeyInformation copy = ((AxKeyInformation) copyObject);
        copy.setKey(new AxArtifactKey(key));
        final Map<AxArtifactKey, AxKeyInfo> newKeyInfoMap = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxKeyInfo> keyInfoMapEntry : keyInfoMap.entrySet()) {
            newKeyInfoMap.put(new AxArtifactKey(keyInfoMapEntry.getKey()), new AxKeyInfo(keyInfoMapEntry.getValue()));
        }
        copy.setKeyInfoMap(newKeyInfoMap);

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
        result = prime * result + keyInfoMap.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#equals(java.lang. Object)
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

        final AxKeyInformation other = (AxKeyInformation) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return keyInfoMap.equals(other.keyInfoMap);
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

        final AxKeyInformation other = (AxKeyInformation) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!keyInfoMap.equals(other.keyInfoMap)) {
            return (keyInfoMap.hashCode() - other.keyInfoMap.hashCode());
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#get(org.onap.policy.apex.core.
     * basicmodel.concepts. AxArtifactKey)
     */
    @Override
    public AxKeyInfo get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxKeyInfo>) keyInfoMap).get(conceptKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#get(java.lang. String)
     */
    @Override
    public AxKeyInfo get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxKeyInfo>) keyInfoMap).get(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#get(java.lang. String,
     * java.lang.String)
     */
    @Override
    public AxKeyInfo get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxKeyInfo>) keyInfoMap).get(conceptKeyName,
                conceptKeyVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#getAll(java. lang.String)
     */
    @Override
    public Set<AxKeyInfo> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxKeyInfo>) keyInfoMap).getAll(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.core.basicmodel.concepts.AxConceptGetter#getAll(java. lang.String,
     * java.lang.String)
     */
    @Override
    public Set<AxKeyInfo> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxKeyInfo>) keyInfoMap).getAll(conceptKeyName,
                conceptKeyVersion);
    }
}
