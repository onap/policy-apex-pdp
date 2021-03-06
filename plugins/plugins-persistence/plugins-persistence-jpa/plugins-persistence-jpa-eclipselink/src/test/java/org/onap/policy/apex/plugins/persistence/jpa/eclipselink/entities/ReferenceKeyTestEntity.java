/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.persistence.jpa.eclipselink.entities;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.xml.AxReferenceKeyAdapter;

/**
 * The Class ReferenceKeyTestEntity provides a reference key test concept.
 */
@Entity
@Table(name = "ReferenceKeyTestEntity")
public class ReferenceKeyTestEntity extends AxConcept {
    private static final long serialVersionUID = -2962570563281067895L;

    @EmbeddedId()
    @XmlElement(name = "key", required = true)
    @XmlJavaTypeAdapter(AxReferenceKeyAdapter.class)
    protected AxReferenceKey key;

    private double doubleValue;

    /**
     * Instantiates a new reference key test entity.
     */
    public ReferenceKeyTestEntity() {
        this.key = new AxReferenceKey();
        this.doubleValue = 0;
    }

    /**
     * Instantiates a new reference key test entity.
     *
     * @param doubleValue the double value
     */
    public ReferenceKeyTestEntity(final Double doubleValue) {
        this.key = new AxReferenceKey();
        this.doubleValue = doubleValue;
    }

    /**
     * Instantiates a new reference key test entity.
     *
     * @param key the key
     * @param doubleValue the double value
     */
    public ReferenceKeyTestEntity(final AxReferenceKey key, final Double doubleValue) {
        this.key = key;
        this.doubleValue = doubleValue;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxReferenceKey getKey() {
        return key;
    }

    /**
     * {@inheritDoc}.
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
    public void setKey(final AxReferenceKey key) {
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

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        return key.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        key.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "ReferenceKeyTestEntity [key=" + key + ", doubleValue=" + doubleValue + "]";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        final Object copyObject = ((target == null) ? new ReferenceKeyTestEntity() : target);
        if (copyObject instanceof ReferenceKeyTestEntity) {
            final ReferenceKeyTestEntity copy = ((ReferenceKeyTestEntity) copyObject);
            if (this.checkSetKey()) {
                copy.setKey(new AxReferenceKey(key));
            } else {
                copy.key = null;
            }
            copy.doubleValue = doubleValue;
            return copy;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
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
        final ReferenceKeyTestEntity other = (ReferenceKeyTestEntity) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return (Double.compare(doubleValue, other.doubleValue) == 0);
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
            return -1;
        }
        final ReferenceKeyTestEntity other = (ReferenceKeyTestEntity) otherObj;
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
