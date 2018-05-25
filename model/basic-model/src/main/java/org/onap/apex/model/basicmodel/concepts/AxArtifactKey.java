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
 * An artifact key uniquely identifies every first order entity in the system. Every first order concept in the system
 * must have an {@link AxArtifactKey} to identify it. Concepts that are wholly contained in another concept are
 * identified using a {@link AxReferenceKey} key.
 * <p>
 * Key validation checks that the name and version fields match the {@link NAME_REGEXP} and {@link VERSION_REGEXP}
 * regular expressions respectively.
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "apexArtifactKey", namespace = "http://www.onap.org/policy/apex-pdp")

@XmlType(name = "AxArtifactKey", namespace = "http://www.onap.org/policy/apex-pdp", propOrder = { "name", "version" })

public class AxArtifactKey extends AxKey {
    private static final long serialVersionUID = 8932717618579392561L;

    private static final String NAME_TOKEN = "name";
    private static final String VERSION_TOKEN = "version";

    @Column(name = NAME_TOKEN)
    @XmlElement(required = true)
    private String name;

    @Column(name = VERSION_TOKEN)
    @XmlElement(required = true)
    private String version;

    /**
     * The default constructor creates a null artifact key.
     */
    public AxArtifactKey() {
        this(NULL_KEY_NAME, NULL_KEY_VERSION);
    }

    /**
     * Copy constructor
     *
     * @param copyConcept the concept to copy from
     */
    public AxArtifactKey(final AxArtifactKey copyConcept) {
        super(copyConcept);
    }

    /**
     * Constructor to create a key with the specified name and version.
     *
     * @param name the key name
     * @param version the key version
     */
    public AxArtifactKey(final String name, final String version) {
        super();
        this.name = Assertions.validateStringParameter(NAME_TOKEN, name, NAME_REGEXP);
        this.version = Assertions.validateStringParameter(VERSION_TOKEN, version, VERSION_REGEXP);
    }

    /**
     * Constructor to create a key using the key and version from the specified key ID.
     *
     * @param id the key ID in a format that respects the {@link KEY_ID_REGEXP}
     */
    public AxArtifactKey(final String id) {
        Assertions.argumentNotNull(id, "id may not be null");

        // Check the incoming ID is valid
        Assertions.validateStringParameter("id", id, KEY_ID_REGEXP);

        // Split on colon, if the id passes the regular expression test above
        // it'll have just one colon separating the name and version
        // No need for range checks or size checks on the array
        final String[] nameVersionArray = id.split(":");

        // Return the new key
        name = Assertions.validateStringParameter(NAME_TOKEN, nameVersionArray[0], NAME_REGEXP);
        version = Assertions.validateStringParameter(VERSION_TOKEN, nameVersionArray[1], VERSION_REGEXP);
    }

    /**
     * Get a null artifact key.
     *
     * @return a null artifact key
     */
    public static final AxArtifactKey getNullKey() {
        return new AxArtifactKey(AxKey.NULL_KEY_NAME, AxKey.NULL_KEY_VERSION);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxArtifactKey getKey() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#getKeys()
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
     * @see com.ericsson.apex.model.basicmodel.concepts.AxKey#getID()
     */
    @Override
    public String getID() {
        return name + ':' + version;
    }

    /**
     * Gets the key name.
     *
     * @return the key name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the key name.
     *
     * @param name the key name
     */
    public void setName(final String name) {
        this.name = Assertions.validateStringParameter(NAME_TOKEN, name, NAME_REGEXP);
    }

    /**
     * Gets the key version.
     *
     * @return the key version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the key version.
     *
     * @param version the key version
     */
    public void setVersion(final String version) {
        this.version = Assertions.validateStringParameter(VERSION_TOKEN, version, VERSION_REGEXP);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxKey#getCompatibility(com.
     * ericsson.apex.model.basicmodel.concepts.AxKey)
     */
    @Override
    public AxKey.Compatibility getCompatibility(final AxKey otherKey) {
        if (!(otherKey instanceof AxArtifactKey)) {
            return Compatibility.DIFFERENT;
        }
        final AxArtifactKey otherArtifactKey = (AxArtifactKey) otherKey;

        if (this.equals(otherArtifactKey)) {
            return Compatibility.IDENTICAL;
        }
        if (!this.getName().equals(otherArtifactKey.getName())) {
            return Compatibility.DIFFERENT;
        }

        final String[] thisVersionArray = getVersion().split("\\.");
        final String[] otherVersionArray = otherArtifactKey.getVersion().split("\\.");

        // There must always be at least one element in each version
        if (!thisVersionArray[0].equals(otherVersionArray[0])) {
            return Compatibility.MAJOR;
        }

        if (thisVersionArray.length >= 2 && otherVersionArray.length >= 2
                && !thisVersionArray[1].equals(otherVersionArray[1])) {
            return Compatibility.MINOR;
        }

        return Compatibility.PATCH;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxKey#isCompatible(com.
     * ericsson.apex.model.basicmodel.concepts.AxKey)
     */
    @Override
    public boolean isCompatible(final AxKey otherKey) {
        if (!(otherKey instanceof AxArtifactKey)) {
            return false;
        }
        final AxArtifactKey otherArtifactKey = (AxArtifactKey) otherKey;

        final Compatibility compatibility = this.getCompatibility(otherArtifactKey);

        return !(compatibility == Compatibility.DIFFERENT || compatibility == Compatibility.MAJOR);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#validate(com.
     * ericsson.apex.model.basicmodel.concepts.AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        try {
            Assertions.validateStringParameter(NAME_TOKEN, name, NAME_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(this, this.getClass(), ValidationResult.INVALID,
                    "name invalid-" + e.getMessage()));
        }

        try {
            Assertions.validateStringParameter(VERSION_TOKEN, version, VERSION_REGEXP);
        } catch (final IllegalArgumentException e) {
            result.addValidationMessage(new AxValidationMessage(this, this.getClass(), ValidationResult.INVALID,
                    "version invalid-" + e.getMessage()));
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
        name = Assertions.validateStringParameter(NAME_TOKEN, name, NAME_REGEXP);
        version = Assertions.validateStringParameter(VERSION_TOKEN, version, VERSION_REGEXP);
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
        builder.append("name=");
        builder.append(name);
        builder.append(",version=");
        builder.append(version);
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
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final AxConcept copyObject = target;
        Assertions.instanceOf(copyObject, AxArtifactKey.class);

        final AxArtifactKey copy = ((AxArtifactKey) copyObject);
        copy.setName(name);
        copy.setVersion(version);

        return copyObject;
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
        result = prime * result + name.hashCode();
        result = prime * result + version.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ericsson.apex.model.basicmodel.concepts.AxConcept#equals(java.lang. Object)
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

        final AxArtifactKey other = (AxArtifactKey) obj;

        if (!name.equals(other.name)) {
            return false;
        }
        return version.equals(other.version);
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

        final AxArtifactKey other = (AxArtifactKey) otherObj;

        if (!name.equals(other.name)) {
            return name.compareTo(other.name);
        }
        return version.compareTo(other.version);
    }
}
