/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.context.entities;

import java.util.Arrays;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

/**
 * The Class ArtifactKeyTestEntity is an entity for testing artifact keys.
 */
@Entity
@Table(name = "ArtifactKeyTestEntity")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
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
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return Arrays.asList((AxKey) getKey());
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
        final ArtifactKeyTestEntity other = (ArtifactKeyTestEntity) otherObj;
        return new CompareToBuilder()
                        .append(key, other.key)
                        .append(doubleValue, other.doubleValue)
                        .toComparison();
    }
}
