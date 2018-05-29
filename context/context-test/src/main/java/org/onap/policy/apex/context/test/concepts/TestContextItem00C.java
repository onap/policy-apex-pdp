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

package org.onap.policy.apex.context.test.concepts;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Class TestContextItem00C.
 */
public class TestContextItem00C implements Serializable {
    private static final long serialVersionUID = -7497746259264651884L;

    private static final int HASH_PRIME_1 = 31;

    private Map<String, String> mapValue = new TreeMap<String, String>();

    /**
     * The Constructor.
     */
    public TestContextItem00C() {}

    /**
     * The Constructor.
     *
     * @param mapValue the map value
     */
    public TestContextItem00C(final Map<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    /**
     * Gets the map value.
     *
     * @return the map value
     */
    public Map<String, String> getMapValue() {
        if (mapValue == null) {
            mapValue = new TreeMap<String, String>();
        }
        return mapValue;
    }

    /**
     * Sets the map value.
     *
     * @param mapValue the map value
     */
    public void setMapValue(final Map<String, String> mapValue) {
        this.mapValue = mapValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = HASH_PRIME_1;
        int result = 1;
        result = prime * result + ((mapValue == null) ? 0 : mapValue.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestContextItem00C other = (TestContextItem00C) obj;
        if (mapValue == null) {
            if (other.mapValue != null) {
                return false;
            }
        } else if (!mapValue.equals(other.mapValue)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TestContextItem00C [mapValue=" + mapValue + "]";
    }

}
