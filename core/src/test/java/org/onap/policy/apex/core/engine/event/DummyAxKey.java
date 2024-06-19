/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
 * Modifications Copyright (C) 2024 Nordix Foundation.
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

package org.onap.policy.apex.core.engine.event;

import java.io.Serial;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

/**
 * Dummy Key Class.
 */
public class DummyAxKey extends AxKey {

    @Serial
    private static final long serialVersionUID = 964899169013353800L;

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(AxConcept concept) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getId() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Compatibility getCompatibility(AxKey otherKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isCompatible(AxKey otherKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxKey getKey() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(AxValidationResult result) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        throw new NotImplementedException("Not implemented on dummy class");

    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(Object otherObject) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Dummy Key";
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(AxConcept target) {
        throw new NotImplementedException("Not implemented on dummy class");
    }
}
