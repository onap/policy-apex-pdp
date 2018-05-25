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
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * This class is the base class for all models in Apex. All model classes inherit from this model so all models must
 * have a key and have key information.
 * <p>
 * Validation checks that the model key is valid. It goes on to check for null keys and checks each key for uniqueness
 * in the model. A check is carried out to ensure that an {@link AxKeyInfo} instance exists for every
 * {@link AxArtifactKey} key. For each {@link AxReferenceKey} instance, a check is made that its parent and local name
 * are nut null and that a {@link AxKeyInfo} entry exists for its parent. Then a check is made that each used
 * {@link AxArtifactKey} and {@link AxReferenceKey} usage references a key that exists. Finally, a check is made to
 * ensure that an {@link AxArtifactKey} instance exists for every {@link AxKeyInfo} instance.
 */

@Entity
@Table(name = "AxModel")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@XmlRootElement(name = "apexModel", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxModel", namespace = "http://www.onap.org/policy/apex-pdp", propOrder = { "key", "keyInformation" })

public class AxModel extends AxConcept {
    private static final String IS_A_NULL_KEY = " is a null key";

    private static final long serialVersionUID = -771659065637205430L;

    @EmbeddedId
    @XmlElement(name = "key", required = true)
    private AxArtifactKey key;

    // @formatter:off
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({ @JoinColumn(name = "keyInformationName", referencedColumnName = "name"),
            @JoinColumn(name = "keyInformationVersion", referencedColumnName = "version") })
    @XmlElement(name = "keyInformation", required = true)
    private AxKeyInformation keyInformation;
    // @formatter:on

    /**
     * The Default Constructor creates this concept with a NULL artifact key.
     */
    public AxModel() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor
     *
     * @param copyConcept the concept to copy from
     */
    public AxModel(final AxModel copyConcept) {
        super(copyConcept);
    }

    /**
     * Constructor to create this concept with the specified key.
     *
     * @param key the key of this concept
     */
    public AxModel(final AxArtifactKey key) {
        this(key, new AxKeyInformation(new AxArtifactKey(key.getName() + "_KeyInfo", key.getVersion())));
    }

    /**
     * Constructor to create this concept and set all its fields.
     *
     * @param key the key of this concept
     * @param keyInformation the key information of this concept
     */
    public AxModel(final AxArtifactKey key, final AxKeyInformation keyInformation) {
        super();
        Assertions.argumentNotNull(key, "key may not be null");
        Assertions.argumentNotNull(keyInformation, "keyInformation may not be null");

        this.key = key;
        this.keyInformation = keyInformation;
    }

    /**
     * Registers this model with the {@link ModelService}. All models are registered with the model service so that
     * models can be references from anywhere in the Apex system without being passed as references through deep call
     * chains.
     */
    public void register() {
        ModelService.registerModel(AxKeyInformation.class, getKeyInformation());
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

        // We just add the key for the KeyInformation itself. We don't add the
        // keys from key information because
        // that is a list of key information for existing keys
        keyList.add(keyInformation.getKey());

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
     * Gets the key information of this concept.
     *
     * @return the key information of this concept
     */
    public AxKeyInformation getKeyInformation() {
        return keyInformation;
    }

    /**
     * Sets the key information of this concept.
     *
     * @param keyInformation the key information of this concept
     */
    public void setKeyInformation(final AxKeyInformation keyInformation) {
        Assertions.argumentNotNull(keyInformation, "keyInformation may not be null");
        this.keyInformation = keyInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.basicmodel.concepts.
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
        result = keyInformation.validate(result);

        // Key consistency check
        final Set<AxArtifactKey> artifactKeySet = new TreeSet<>();
        final Set<AxReferenceKey> referenceKeySet = new TreeSet<>();
        final Set<AxKeyUse> usedKeySet = new TreeSet<>();

        for (final AxKey axKey : this.getKeys()) {
            // Check for the two type of keys we have
            if (axKey instanceof AxArtifactKey) {
                result = validateArtifactKeyInModel((AxArtifactKey) axKey, artifactKeySet, result);
            } else if (axKey instanceof AxReferenceKey) {
                result = validateReferenceKeyInModel((AxReferenceKey) axKey, referenceKeySet, result);
            }
            // It must be an AxKeyUse, nothing else is legal
            else {
                usedKeySet.add((AxKeyUse) axKey);
            }
        }

        // Check all reference keys have correct parent keys
        for (final AxReferenceKey referenceKey : referenceKeySet) {
            if (!artifactKeySet.contains(referenceKey.getParentArtifactKey())) {
                result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                        "parent artifact key not found for reference key " + referenceKey));
            }
        }

        result = validateKeyUses(usedKeySet, artifactKeySet, referenceKeySet, result);

        // Check key information for unused key information
        for (final AxArtifactKey keyInfoKey : keyInformation.getKeyInfoMap().keySet()) {
            if (!artifactKeySet.contains(keyInfoKey)) {
                result.addValidationMessage(new AxValidationMessage(keyInfoKey, this.getClass(),
                        ValidationResult.WARNING, "key not found for key information entry"));
            }
        }

        return result;
    }

    /**
     * Check for consistent usage of an artifact key in the model
     *
     * @param artifactKey The artifact key to check
     * @param artifactKeySet The set of artifact keys encountered so far, this key is appended to the set
     * @param result The validation result to append to
     * @return the result of the validation
     */
    private AxValidationResult validateArtifactKeyInModel(final AxArtifactKey artifactKey,
            final Set<AxArtifactKey> artifactKeySet, final AxValidationResult result) {
        // Null key check
        if (artifactKey.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key " + artifactKey + IS_A_NULL_KEY));
        }

        // Null key name start check
        if (artifactKey.getName().toUpperCase().startsWith(AxKey.NULL_KEY_NAME)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key " + artifactKey + " name starts with keyword " + AxKey.NULL_KEY_NAME));
        }

        // Unique key check
        if (artifactKeySet.contains(artifactKey)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "duplicate key " + artifactKey + " found"));
        } else {
            artifactKeySet.add(artifactKey);
        }

        // Key information check
        if (!keyInformation.getKeyInfoMap().containsKey(artifactKey)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key information not found for key " + artifactKey));
        }

        return result;
    }

    /**
     * Check for consistent usage of a reference key in the model
     *
     * @param artifactKey The reference key to check
     * @param referenceKeySet The set of reference keys encountered so far, this key is appended to the set
     * @param result The validation result to append to
     * @return the result of the validation
     */
    private AxValidationResult validateReferenceKeyInModel(final AxReferenceKey referenceKey,
            final Set<AxReferenceKey> referenceKeySet, final AxValidationResult result) {
        // Null key check
        if (referenceKey.equals(AxReferenceKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key " + referenceKey + IS_A_NULL_KEY));
        }

        // Null parent key check
        if (referenceKey.getParentArtifactKey().equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "parent artifact key of key " + referenceKey + IS_A_NULL_KEY));
        }

        // Null local name check
        if (referenceKey.getLocalName().equals(AxKey.NULL_KEY_NAME)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key " + referenceKey + " has a null local name"));
        }

        // Null key name start check
        if (referenceKey.getParentArtifactKey().getName().toUpperCase().startsWith(AxKey.NULL_KEY_NAME)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key " + referenceKey + " parent name starts with keyword " + AxKey.NULL_KEY_NAME));
        }

        // Unique key check
        if (referenceKeySet.contains(referenceKey)) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "duplicate key " + referenceKey + " found"));
        } else {
            referenceKeySet.add(referenceKey);
        }

        // Key information check
        if (!keyInformation.getKeyInfoMap().containsKey(referenceKey.getParentArtifactKey())) {
            result.addValidationMessage(new AxValidationMessage(key, this.getClass(), ValidationResult.INVALID,
                    "key information not found for parent key of key " + referenceKey));
        }

        return result;
    }

    /**
     * Check for consistent usage of cross-key references in the model
     *
     * @param usedKeySet The set of all keys used in the model
     * @param artifactKeySet The set of artifact keys encountered so far, this key is appended to the set
     * @param referenceKeySet The set of reference keys encountered so far, this key is appended to the set
     * @param result The validation result to append to
     * @return the result of the validation
     */
    private AxValidationResult validateKeyUses(final Set<AxKeyUse> usedKeySet, final Set<AxArtifactKey> artifactKeySet,
            final Set<AxReferenceKey> referenceKeySet, final AxValidationResult result) {
        // Check all key uses
        for (final AxKeyUse usedKey : usedKeySet) {
            if (usedKey.getKey() instanceof AxArtifactKey) {
                // AxArtifact key usage, check the key exists
                if (!artifactKeySet.contains(usedKey.getKey())) {
                    result.addValidationMessage(new AxValidationMessage(usedKey.getKey(), this.getClass(),
                            ValidationResult.INVALID, "an artifact key used in the model is not defined"));
                }
            } else {
                // AxReference key usage, check the key exists
                if (!referenceKeySet.contains(usedKey.getKey())) {
                    result.addValidationMessage(new AxValidationMessage(usedKey.getKey(), this.getClass(),
                            ValidationResult.INVALID, "a reference key used in the model is not defined"));
                }
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
        keyInformation.clean();
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
        builder.append(",keyInformation=");
        builder.append(keyInformation);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.basicmodel.concepts.
     * AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxModel.class);

        final AxModel copy = ((AxModel) copyObject);
        copy.setKey(new AxArtifactKey(key));
        copy.setKeyInformation(new AxKeyInformation(keyInformation));

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
        result = prime * result + keyInformation.hashCode();
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

        final AxModel other = (AxModel) obj;
        if (!key.equals(other.key)) {
            return false;
        }
        return keyInformation.equals(other.keyInformation);
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

        final AxModel other = (AxModel) otherObj;
        if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        return keyInformation.compareTo(other.keyInformation);
    }
}
