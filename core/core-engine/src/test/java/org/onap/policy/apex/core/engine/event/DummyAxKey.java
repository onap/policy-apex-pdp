/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.onap.policy.apex.model.basicmodel.concepts.AxConcept;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

/**
 * Dummy Key Class.
 */
public class DummyAxKey extends AxKey {
    private static final long serialVersionUID = 964899169013353800L;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AxConcept concept) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxKey#getId()
     */
    @Override
    public String getId() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxKey#getCompatibility(org.onap.policy.apex.model.basicmodel.
     * concepts.AxKey)
     */
    @Override
    public Compatibility getCompatibility(AxKey otherKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxKey#isCompatible(org.onap.policy.apex.model.basicmodel.concepts.
     * AxKey)
     */
    @Override
    public boolean isCompatible(AxKey otherKey) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKey()
     */
    @Override
    public AxKey getKey() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#getKeys()
     */
    @Override
    public List<AxKey> getKeys() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#validate(org.onap.policy.apex.model.basicmodel.concepts.
     * AxValidationResult)
     */
    @Override
    public AxValidationResult validate(AxValidationResult result) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#clean()
     */
    @Override
    public void clean() {
        throw new NotImplementedException("Not implemented on dummy class");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherObject) {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#toString()
     */
    @Override
    public String toString() {
        return "Dummy Key";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.apex.model.basicmodel.concepts.AxConcept#hashCode()
     */
    @Override
    public int hashCode() {
        throw new NotImplementedException("Not implemented on dummy class");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.onap.policy.apex.model.basicmodel.concepts.AxConcept#copyTo(org.onap.policy.apex.model.basicmodel.concepts.
     * AxConcept)
     */
    @Override
    public AxConcept copyTo(AxConcept target) {
        throw new NotImplementedException("Not implemented on dummy class");
    }
}
