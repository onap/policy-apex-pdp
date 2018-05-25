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

package org.onap.apex.model.basicmodel.concepts;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.onap.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.apex.model.utilities.Assertions;

/**
 * A reference key identifies entities in the system that are contained in other entities. Every contained concept in
 * the system must have an {@link AxReferenceKey} to identify it. Non-contained first order concepts are identified
 * using an {@link AxArtifactKey} key.
 * <p>
 * An {@link AxReferenceKey} contains an {@link AxArtifactKey} key reference to the first order entity that contains it.
 * The local name of the reference key must uniquely identify the referenced concept among those concepts contained in
 * the reference key's parent. In other words, if a parent concept has more than one child, the local name in the key of
 * all its children must be unique.
 * <p>
 * If a reference key's parent is itself a reference key, then the parent's local name must be set in the reference key.
 * If the parent is a first order concept, then the parent's local name in the key will be set to NULL.
 * <p>
 * Key validation checks that the parent name and parent version fields match the {@link NAME_REGEXP} and
 * {@link VERSION_REGEXP} regular expressions respectively and that the local name fields match the
 * {@link LOCAL_NAME_REGEXP} regular expression.
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexReferenceKey", namespace = "http://www.onap.org/policy/apex-pdp")
@XmlType(name = "AxReferenceKey", namespace = "http://www.onap.org/policy/apex-pdp",
        propOrder = { "parentKeyName", "parentKeyVersion", "parentLocalName", "localName" })

public class AxReferenceKey extends AxKey {
    private static final String PARENT_KEY_NAME = "parentKeyName";
    private static final String PARENT_KEY_VERSION = "parentKeyVersion";
    private static final String PARENT_LOCAL_NAME = "parentLocalName";
    private static final String LOCAL_NAME = "localName";

    private static final long serialVersionUID = 8932717618579392561L;

    /** Regular expression to specify the structure of local names in reference keys. */
    public static final String LOCAL_NAME_REGEXP = "[A-Za-z0-9\\-_\\.]+|^$";

    /** Regular expression to specify the structure of IDs in reference keys. */
    public static final String REFERENCE_KEY_ID_REGEXP =
            "[A-Za-z0-9\\-_]+:[0-9].[0-9].[0-9]:[A-Za-z0-9\\-_]+:[A-Za-z0-9\\-_]+";

    private static final int PARENT_NAME_FIELD = 0;
    private static final int PARENT_VERSION_FIELD = 1;
    private static final int PARENT_LOCAL_NAME_FIELD = 2;
    private static final int LOCAL_NAME_FIELD = 3;

    @Column(name = PARENT_KEY_NAME)
    @XmlElement(required = true)
    private String parentKeyName;

    @Column(name = PARENT_KEY_VERSION)
    @XmlElement(required = true)
    private String parentKeyVersion;

    @Column(name = PARENT_LOCAL_NAME)
    @XmlElement(required = true)
    private String parentLocalName;

    @Column(name = LOCAL_NAME)
    @XmlElement(required = true)
    private String localName;

    /**
     * The default constructor creates a null reference key.
     */
    public AxReferenceKey() {
        this(NULL_KEY_NAME, NULL_KEY_VERSION, NULL_KEY_NAME, NULL_KEY_NAME);
    }

    /**
     * The Copy Constructor creates a key by copying another key.
     *
     * @param referenceKey the reference key to copy from
     */
    public AxReferenceKey(final AxReferenceKey referenceKey) {
        this(referenceKey.getParentKeyName(), referenceKey.getParentKeyVersion(), referenceKey.getParentLocalName(),
                referenceKey.getLocalName());
    }

    /**
     * Constructor to create a null reference key for the specified parent artifact key.
     *
     * @param axArtifactKey the parent artifact key of this reference key
     */
    public AxReferenceKey(final AxArtifactKey axArtifactKey) {
        this(axArtifactKey.getName(), axArtifactKey.getVersion(), NULL_KEY_NAME, NULL_KEY_NAME);
    }

    /**
     * Constructor to create a reference key for the given parent artifact key with the given local name.
     *
     * @param axArtifactKey the parent artifact key of this reference key
     * @param localName the local name of this reference key
     */
    public AxReferenceKey(final AxArtifactKey axArtifactKey, final String localName) {
        this(axArtifactKey, NULL_KEY_NAME, localName);
    }

    /**
     * Constructor to create a reference key for the given parent reference key with the given local name.
     *
     * @param parentReferenceKey the parent reference key of this reference key
     * @param localName the local name of this reference key
     */
    public AxReferenceKey(final AxReferenceKey parentReferenceKey, final String localName) {
        this(parentReferenceKey.getParentArtifactKey(), parentReferenceKey.getLocalName(), localName);
    }

    /**
     * Constructor to create a reference key for the given parent reference key (specified by the parent reference key's
     * artifact key and local name) with the given local name.
     *
     * @param axArtifactKey the artifact key of the parent reference key of this reference key
     * @param parentLocalName the local name of the parent reference key of this reference key
     * @param localName the local name of this reference key
     */
    public AxReferenceKey(final AxArtifactKey axArtifactKey, final String parentLocalName, final String localName) {
        this(axArtifactKey.getName(), axArtifactKey.getVersion(), parentLocalName, localName);
    }

    /**
     * Constructor to create a reference key for the given parent artifact key (specified by the parent artifact key's
     * name and version) with the given local name.
     *
     * @param parentKeyName the name of the parent artifact key of this reference key
     * @param parentKeyVersion the version of the parent artifact key of this reference key
     * @param localName the local name of this reference key
     */
    public AxReferenceKey(final String parentKeyName, final String parentKeyVersion, final String localName) {
        this(parentKeyName, parentKeyVersion, NULL_KEY_NAME, localName);
    }

    /**
     * Constructor to create a reference key for the given parent key (specified by the parent key's name, version nad
     * local name) with the given local name.
     *
     * @param parentKeyName the parent key name of this reference key
     * @param parentKeyVersion the parent key version of this reference key
     * @param parentLocalName the parent local name of this reference key
     * @param localName the local name of this reference key
     */
    public AxReferenceKey(final String parentKeyName, final String parentKeyVersion, final String parentLocalName,
            final String localName) {
        super();
        this.parentKeyName = Assertions.validateStringParameter(PARENT_KEY_NAME, parentKeyName, NAME_REGEXP);
        this.parentKeyVersion =
                Assertions.validateStringParameter(PARENT_KEY_VERSION, parentKeyVersion, VERSION_REGEXP);
        this.parentLocalName =
                Assertions.validateStringParameter(PARENT_LOCAL_NAME, parentLocalName, LOCAL_NAME_REGEXP);
        this.localName = Assertions.validateStringParameter(LOCAL_NAME, localName, LOCAL_NAME_REGEXP);
    }

    /**
     * Constructor to create a key from the specified key ID.
     *
     * @param id the key ID in a format that respects the {@link KEY_ID_REGEXP}
     */
    public AxReferenceKey(final String id) {
        final String conditionedId = Assertions.validateStringParameter("id", id, REFERENCE_KEY_ID_REGEXP);

        // Split on colon, if the id passes the regular expression test above
        // it'll have just three colons separating the parent name,
        // parent version, parent local name, and and local name
        // No need for range checks or size checks on the array
        final String[] nameVersionNameArray = conditionedId.split(":");

        // Initiate the new key
        parentKeyName = Assertions.validateStringParameter(PARENT_KEY_NAME, nameVersionNameArray[PARENT_NAME_FIELD],
                NAME_REGEXP);
        parentKeyVersion = Assertions.validateStringParameter(PARENT_KEY_VERSION,
                nameVersionNameArray[PARENT_VERSION_FIELD], VERSION_REGEXP);
        parentLocalName = Assertions.validateStringParameter(PARENT_LOCAL_NAME,
                nameVersionNameArray[PARENT_LOCAL_NAME_FIELD], LOCAL_NAME_REGEXP);
        localName = Assertions.validateStringParameter(LOCAL_NAME, nameVersionNameArray[LOCAL_NAME_FIELD],
                LOCAL_NAME_REGEXP);
    }

    /**
     * Get a null reference key.
     *
     * @return a null reference key
     */
    public static AxReferenceKey getNullKey() {
        return new AxReferenceKey(AxKey.NULL_KEY_NAME, AxKey.NULL_KEY_VERSION, AxKey.NULL_KEY_NAME,
                AxKey.NULL_KEY_NAME);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxReferenceKey getKey() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        final List<AxKey> keyList = new ArrayList<>();
        keyList.add(getKey());
        return keyList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxKey#getID()
     */
    @Override
    public String getID() {
        return parentKeyName + ':' + parentKeyVersion + ':' + parentLocalName + ':' + localName;
    }

    /**
     * Gets the parent artifact key of this reference key.
     *
     * @return the parent artifact key of this reference key
     */
    public AxArtifactKey getParentArtifactKey() {
        return new AxArtifactKey(parentKeyName, parentKeyVersion);
    }

    /**
     * Gets the parent reference key of this reference key.
     *
     * @return the parent reference key of this reference key
     */
    public AxReferenceKey getParentReferenceKey() {
        return new AxReferenceKey(parentKeyName, parentKeyVersion, parentLocalName);
    }

    /**
     * Sets the parent artifact key of this reference key.
     *
     * @param parentKey the parent artifact key of this reference key
     */
    public void setParentArtifactKey(final AxArtifactKey parentKey) {
        Assertions.argumentNotNull(parentKey, "parentKey may not be null");

        parentKeyName = parentKey.getName();
        parentKeyVersion = parentKey.getVersion();
        parentLocalName = NULL_KEY_NAME;
    }

    /**
     * Sets the parent reference key of this reference key.
     *
     * @param parentKey the parent reference key of this reference key
     */
    public void setParentReferenceKey(final AxReferenceKey parentKey) {
        Assertions.argumentNotNull(parentKey, "parentKey may not be null");

        parentKeyName = parentKey.getParentKeyName();
        parentKeyVersion = parentKey.getParentKeyVersion();
        parentLocalName = parentKey.getLocalName();
    }

    /**
     * Gets the parent key name of this reference key.
     *
     * @return the parent key name of this reference key
     */
    public String getParentKeyName() {
        return parentKeyName;
    }

    /**
     * Sets the parent key name of this reference key.
     *
     * @param parentKeyName the parent key name of this reference key
     */
    public void setParentKeyName(final String parentKeyName) {
        this.parentKeyName = Assertions.validateStringParameter(PARENT_KEY_NAME, parentKeyName, NAME_REGEXP);
    }

    /**
     * Gets the parent key version of this reference key.
     *
     * @return the parent key version of this reference key
     */
    public String getParentKeyVersion() {
        return parentKeyVersion;
    }

    /**
     * Sets the parent key version of this reference key.
     *
     * @param parentKeyVersion the parent key version of this reference key
     */
    public void setParentKeyVersion(final String parentKeyVersion) {
        this.parentKeyVersion =
                Assertions.validateStringParameter(PARENT_KEY_VERSION, parentKeyVersion, VERSION_REGEXP);
    }

    /**
     * Gets the parent local name of this reference key.
     *
     * @return the parent local name of this reference key
     */
    public String getParentLocalName() {
        return parentLocalName;
    }

    /**
     * Sets the parent local name of this reference key.
     *
     * @param parentLocalName the parent local name of this reference key
     */
    public void setParentLocalName(final String parentLocalName) {
        this.parentLocalName =
                Assertions.validateStringParameter(PARENT_LOCAL_NAME, parentLocalName, LOCAL_NAME_REGEXP);
    }

    /**
     * Gets the local name of this reference key.
     *
     * @return the local name of this reference key
     */
    public String getLocalName() {
        return localName;
    }

    /**
     * Sets the local name of this reference key.
     *
     * @param localName the local name of this reference key
     */
    public void setLocalName(final String localName) {
        this.localName = Assertions.validateStringParameter(LOCAL_NAME, localName, LOCAL_NAME_REGEXP);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxKey#getCompatibility(com.
     * ericsson.apex.model.basicmodel.concepts.AxKey)
     */
    @Override
    public AxKey.Compatibility getCompatibility(final AxKey otherKey) {
        if (!(otherKey instanceof AxReferenceKey)) {
            return Compatibility.DIFFERENT;
        }
        final AxReferenceKey otherReferenceKey = (AxReferenceKey) otherKey;

        return this.getParentArtifactKey().getCompatibility(otherReferenceKey.getParentArtifactKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxKey#isCompatible(com.
     * ericsson.apex.model.basicmodel.concepts.AxKey)
     */
    @Override
    public boolean isCompatible(final AxKey otherKey) {
        if (!(otherKey instanceof AxReferenceKey)) {
            return false;
        }
        final AxReferenceKey otherReferenceKey = (AxReferenceKey) otherKey;

        return this.getParentArtifactKey().isCompatible(otherReferenceKey.getParentArtifactKey());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#validate(com.
     * ericsson.apex.model.basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        try {
            Assertions.validateStringParameter(PARENT_KEY_NAME, parentKeyName, NAME_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(this, this.getClass(), ValidationResult.INVALID,
                    "parentKeyName invalid-" + e.getMessage()));
        }

        try {
            Assertions.validateStringParameter(PARENT_KEY_VERSION, parentKeyVersion, VERSION_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(this, this.getClass(), ValidationResult.INVALID,
                    "parentKeyVersion invalid-" + e.getMessage()));
        }

        try {
            Assertions.validateStringParameter(PARENT_LOCAL_NAME, parentLocalName, LOCAL_NAME_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(this, this.getClass(), ValidationResult.INVALID,
                    "parentLocalName invalid-" + e.getMessage()));
        }

        try {
            Assertions.validateStringParameter(LOCAL_NAME, localName, LOCAL_NAME_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(this, this.getClass(), ValidationResult.INVALID,
                    "localName invalid-" + e.getMessage()));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        parentKeyName = Assertions.validateStringParameter(PARENT_KEY_NAME, parentKeyName, NAME_REGEXP);
        parentKeyVersion = Assertions.validateStringParameter(PARENT_KEY_VERSION, parentKeyVersion, VERSION_REGEXP);
        parentLocalName = Assertions.validateStringParameter(PARENT_LOCAL_NAME, parentLocalName, LOCAL_NAME_REGEXP);
        localName = Assertions.validateStringParameter(LOCAL_NAME, localName, LOCAL_NAME_REGEXP);
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
        builder.append("parentKeyName=");
        builder.append(parentKeyName);
        builder.append(",parentKeyVersion=");
        builder.append(parentKeyVersion);
        builder.append(",parentLocalName=");
        builder.append(parentLocalName);
        builder.append(",localName=");
        builder.append(localName);
        builder.append(")");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.apex.model.basicmodel.concepts. AxConcept)
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxReferenceKey.class);

        final AxReferenceKey copy = ((AxReferenceKey) copyObject);
        copy.setParentKeyName(parentKeyName);
        copy.setParentKeyVersion(parentKeyVersion);
        copy.setLocalName(localName);
        copy.setParentLocalName(parentLocalName);

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
        result = prime * result + parentKeyName.hashCode();
        result = prime * result + parentKeyVersion.hashCode();
        result = prime * result + parentLocalName.hashCode();
        result = prime * result + localName.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.onap.apex.model.basicmodel.concepts.AxConcept#equals(java.lang. Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("comparison object may not be null");
        }

        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxReferenceKey other = (AxReferenceKey) obj;

        if (!parentKeyName.equals(other.parentKeyName)) {
            return false;
        }
        if (!parentKeyVersion.equals(other.parentKeyVersion)) {
            return false;
        }
        if (!parentLocalName.equals(other.parentLocalName)) {
            return false;
        }
        return localName.equals(other.localName);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        Assertions.argumentNotNull(otherObj, "comparison object may not be null");

        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxReferenceKey other = (AxReferenceKey) otherObj;
        if (!parentKeyName.equals(other.parentKeyName)) {
            return parentKeyName.compareTo(other.parentKeyName);
        }
        if (!parentKeyVersion.equals(other.parentKeyVersion)) {
            return parentKeyVersion.compareTo(other.parentKeyVersion);
        }
        if (!parentLocalName.equals(other.parentLocalName)) {
            return parentLocalName.compareTo(other.parentLocalName);
        }
        return localName.compareTo(other.localName);
    }
}
