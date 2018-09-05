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

package org.onap.policy.apex.context.test.entities;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

/**
 * The Class ArtifactKeyTestEntity is an entity for testing artifact keys.
 */
@Entity
@Table(name = "ArtifactKeyTestEntity")
public class ArtifactKeyTestEntity extends AxConcept {
    private static final long serialVersionUID = -2962570563281067896L;

    @EmbeddedId()
    @XmlElement(name = "key", required = true)
    protected AxArtifactKey key;

    private double doubleValue;

    /**
     * Instantiates a new artifact key test entity.
     */
    public ArtifactKeyTestEntity() {
        this.key = new AxArtifactKey();
        this.doubleValue = 0;
    }

    /**
     * Instantiates a new artifact key test entity.
     *
     * @param doubleValue the double value
     */
    public ArtifactKeyTestEntity(final Double doubleValue) {
        this.key = new AxArtifactKey();
        this.doubleValue = doubleValue;
    }

    /**
     * Instantiates a new artifact key test entity.
     *
     * @param key the key
     * @param doubleValue the double value
     */
    public ArtifactKeyTestEntity(final AxArtifactKey key, final Double doubleValue) {
        this.key = key;
        this.doubleValue = doubleValue;
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
        return Arrays.asList((AxKey) getKey());
    }

    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(final AxArtifactKey key) {
        this.key = key;
    }

    /**
     * Check set key.
     *
     * @return true, if successful
     */
    public boolean checkSetKey() {
        return (this.key != null);
    }

    /**
     * Gets the double value.
     *
     * @return the double value
     */
    public double getDoubleValue() {
        return doubleValue;
    }

    /**
     * Sets the double value.
     *
     * @param doubleValue the new double value
     */
    public void setDoubleValue(final double doubleValue) {
        this.doubleValue = doubleValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.basicmodel.concepts.
     * AxValidationResult)
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        return key.validate(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        key.clean();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        return "ArtifactKeyTestEntity [key=" + key + ", doubleValue=" + doubleValue + "]";
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
        final Object copyObject = ((target == null) ? new ArtifactKeyTestEntity() : target);
        if (copyObject instanceof ArtifactKeyTestEntity) {
            final ArtifactKeyTestEntity copy = ((ArtifactKeyTestEntity) copyObject);
            if (this.checkSetKey()) {
                copy.setKey(new AxArtifactKey(key));
            } else {
                copy.key = null;
            }
            copy.doubleValue = doubleValue;
            return copy;
        } else {
            return null;
        }
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
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        final ArtifactKeyTestEntity other = (ArtifactKeyTestEntity) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return (Double.compare(doubleValue, other.doubleValue) == 0);
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
        final ArtifactKeyTestEntity other = (ArtifactKeyTestEntity) otherObj;
        if (key == null) {
            if (other.key != null) {
                return 1;
            }
        } else if (!key.equals(other.key)) {
            return key.compareTo(other.key);
        }
        return Double.compare(doubleValue, other.doubleValue);
    }
}
