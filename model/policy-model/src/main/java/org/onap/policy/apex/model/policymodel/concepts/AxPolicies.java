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

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetterImpl;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class is a policy container and holds a map of the policies for an entire Apex model. All Apex models that use
 * policies must have an {@link AxPolicies} field. The {@link AxPolicies} class implements the helper methods of the
 * {@link AxConceptGetter} interface to allow {@link AxPolicy} instances to be retrieved by calling methods directly on
 * this class without referencing the contained map.
 * 
 * <p>Validation checks that the container key is not null. An error is issued if no policies are defined in the
 * container. Each policy entry is checked to ensure that its key and value are not null and that the key matches the
 * key in the map value. Each policy entry is then validated individually.
 */
@Entity
@Table(name = "AxPolicies")

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxPolicies", namespace = "http://www.onap.org/policy/apex-pdp", propOrder =
    { "key", "policyMap" })

public class AxPolicies extends AxConcept implements AxConceptGetter<AxPolicy> {
    private static final long serialVersionUID = 4290442590545820316L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    // @formatter:off
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            joinColumns = {@JoinColumn(name = "policyMapName", referencedColumnName = "name"),
                    @JoinColumn(name = "policyMapVersion", referencedColumnName = "version")},
            inverseJoinColumns = {@JoinColumn(name = "policyName", referencedColumnName = "name"),
                    @JoinColumn(name = "policyVersion", referencedColumnName = "version")})
    @XmlElement(required = true)
    private Map<AxArtifactKey, AxPolicy> policyMap;
    // @formatter:on

    /**
     * The Default Constructor creates a {@link AxPolicies} object with a null artifact key and creates an empty event
     * map.
     */
    public AxPolicies() {
        this(new AxArtifactKey());
    }

    /**
     * The Key Constructor creates a {@link AxPolicies} object with the given artifact key and creates an empty event
     * map.
     *
     * @param key the key
     */
    public AxPolicies(final AxArtifactKey key) {
        this(key, new TreeMap<AxArtifactKey, AxPolicy>());
    }

    /**
     * Copy constructor.
     * 
     * @param copyConcept the concept to copy from
     */
    public AxPolicies(final AxPolicies copyConcept) {
        super(copyConcept);
    }

    /**
     * This Constructor creates a policy container with all of its fields defined.
     *
     * @param key the policy container key
     * @param policyMap the policies to be stored in the policy container
     */
    public AxPolicies(final AxArtifactKey key, final Map<AxArtifactKey, AxPolicy> policyMap) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(policyMap, "policyMap may not be null");

        this.key = key;
        this.policyMap = new TreeMap<>();
        this.policyMap.putAll(policyMap);
    }

    /**
     * When a model is unmarshalled from disk or from the database, the policy map is returned as a raw hash map. This
     * method is called by JAXB after unmarshaling and is used to convert the hash map to a {@link NavigableMap} so that
     * it will work with the {@link AxConceptGetter} interface.
     *
     * @param unmarshaler the unmarshaler that is unmarshaling the model
     * @param parent the parent object of this object in the unmarshaler
     */
    public void afterUnmarshal(final Unmarshaller unmarshaler, final Object parent) {
        // The map must be navigable to allow name and version searching, unmarshaling returns a
        // hash map
        final NavigableMap<AxArtifactKey, AxPolicy> navigablePolicyMap = new TreeMap<>();
        navigablePolicyMap.putAll(policyMap);
        policyMap = navigablePolicyMap;
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

        for (final AxPolicy policy : policyMap.values()) {
            keyList.addAll(policy.getKeys());
        }

        return keyList;
    }

    /**
     * Sets the key of the policy container.
     *
     * @param key the policy container key
     */
    public void setKey(final AxArtifactKey key) {
        Assertions.argumentNotNull(key, "key may not be null");
        this.key = key;
    }

    /**
     * Gets the policy map containing all policies in the policy container.
     *
     * @return the policy map with all the policies in the container
     */
    public Map<AxArtifactKey, AxPolicy> getPolicyMap() {
        return policyMap;
    }

    /**
     * Sets the policy map containing all policies in the policy container.
     *
     * @param policyMap the policy map with all the policies to be put in the container
     */
    public void setPolicyMap(final Map<AxArtifactKey, AxPolicy> policyMap) {
        Assertions.argumentNotNull(policyMap, "policyMap may not be null");
        this.policyMap = new TreeMap<>();
        this.policyMap.putAll(policyMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.
     * basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult resultIn) {
        AxValidationResult result = resultIn;

        if (key.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key is a null key"));
        }

        result = key.validate(result);

        if (policyMap.size() == 0) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "policyMap may not be empty"));
        } else {
            for (final Entry<AxArtifactKey, AxPolicy> policyEntry : policyMap.entrySet()) {
                final AxArtifactKey entryKey = policyEntry.getKey();
                if (entryKey.equals(AxArtifactKey.getNullKey())) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "key on policy entry " + entryKey + " may not be the null key"));
                } else if (policyEntry.getValue() == null) {
                    result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                                    "value on policy entry " + entryKey + " may not be null"));
                } else {
                    validate(result, policyEntry, entryKey);
                    result = policyEntry.getValue().validate(result);
                }
            }
        }

        return result;
    }

    private void validate(final AxValidationResult result, final Entry<AxArtifactKey, AxPolicy> policyEntry,
                    final AxArtifactKey entryKey) {
        if (!entryKey.equals(policyEntry.getValue().getKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                            "key on policy entry key " + entryKey + " does not equal policy value key "
                                            + policyEntry.getValue().getKey()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
        for (final Entry<AxArtifactKey, AxPolicy> policyEntry : policyMap.entrySet()) {
            policyEntry.getKey().clean();
            policyEntry.getValue().clean();
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
        builder.append(",policyMap=");
        builder.append(policyMap);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.
     * basicmodel.concepts.AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept targetObject) {
        Assertions.argumentNotNull(targetObject, "target may not be null");

        final Object copyObject = targetObject;
        Assertions.instanceOf(copyObject, AxPolicies.class);

        final AxPolicies copy = ((AxPolicies) copyObject);
        copy.setKey(new AxArtifactKey(key));

        final Map<AxArtifactKey, AxPolicy> newPolicyMap = new TreeMap<>();
        for (final Entry<AxArtifactKey, AxPolicy> policyMapEntry : policyMap.entrySet()) {
            newPolicyMap.put(new AxArtifactKey(policyMapEntry.getKey()), new AxPolicy(policyMapEntry.getValue()));
        }
        copy.setPolicyMap(newPolicyMap);

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
        result = prime * result + policyMap.hashCode();
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

        final AxPolicies other = (AxPolicies) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return policyMap.equals(other.policyMap);
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

        final AxPolicies other = (AxPolicies) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        if (!policyMap.equals(other.policyMap)) {
            return (policyMap.hashCode() - other.policyMap.hashCode());
        }

        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#get(org.onap.policy.apex.model
     * .basicmodel.concepts.AxArtifactKey)
     */
    @Override
    public AxPolicy get(final AxArtifactKey conceptKey) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxPolicy>) policyMap).get(conceptKey);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#get(java.lang.String)
     */
    @Override
    public AxPolicy get(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxPolicy>) policyMap).get(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#get(java.lang.String, java.lang.String)
     */
    @Override
    public AxPolicy get(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxPolicy>) policyMap).get(conceptKeyName,
                        conceptKeyVersion);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String)
     */
    @Override
    public Set<AxPolicy> getAll(final String conceptKeyName) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxPolicy>) policyMap).getAll(conceptKeyName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConceptGetter#getAll(java.lang.String, java.lang.String)
     */
    @Override
    public Set<AxPolicy> getAll(final String conceptKeyName, final String conceptKeyVersion) {
        return new AxConceptGetterImpl<>((NavigableMap<AxArtifactKey, AxPolicy>) policyMap).getAll(conceptKeyName,
                        conceptKeyVersion);
    }
}
