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

package org.onap.policy.apex.plugins.persistence.jpa.hibernate;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;

import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

@Entity
@Table(name = "ArtifactKeyTestEntity")
public class ArtifactKeyTestEntity extends AxConcept {
    private static final long serialVersionUID = -2962570563281067895L;

    @EmbeddedId()
    @XmlElement(name = "key", required = true)
    protected AxArtifactKey key;

    private double doubleValue;

    public ArtifactKeyTestEntity() {
        this.key = new AxArtifactKey();
        this.doubleValue = 0;
    }

    public ArtifactKeyTestEntity(final Double doubleValue) {
        this.key = new AxArtifactKey();
        this.doubleValue = doubleValue;
    }

    public ArtifactKeyTestEntity(final AxArtifactKey key, final Double doubleValue) {
        this.key = key;
        this.doubleValue = doubleValue;
    }

    public AxArtifactKey getKey() {
        return key;
    }

    public List<AxKey> getKeys() {
        return Arrays.asList((AxKey) getKey());
    }

    public void setKey(final AxArtifactKey key) {
        this.key = key;
    }

    public boolean checkSetKey() {
        return (this.key != null);
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(final double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        return key.validate(result);
    }

    @Override
    public void clean() {
        key.clean();
    }

    @Override
    public String toString() {
        return "ArtifactKeyTestEntity [key=" + key + ", doubleValue=" + doubleValue + "]";
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final ArtifactKeyTestEntity other = (ArtifactKeyTestEntity) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (doubleValue != other.doubleValue)
            return false;
        return true;
    }

    @Override
    public int compareTo(final AxConcept otherObj) {
        if (otherObj == null)
            return -1;
        if (this == otherObj)
            return 0;
        final ArtifactKeyTestEntity other = (ArtifactKeyTestEntity) otherObj;
        if (key == null) {
            if (other.key != null)
                return 1;
        } else if (!key.equals(other.key))
            return key.compareTo(other.key);
        if (doubleValue != other.doubleValue)
            return new Double(doubleValue).compareTo(other.doubleValue);

        return 0;
    }
}
