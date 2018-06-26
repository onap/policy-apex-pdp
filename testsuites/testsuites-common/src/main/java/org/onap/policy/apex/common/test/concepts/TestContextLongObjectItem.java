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

package org.onap.policy.apex.common.test.concepts;

import java.io.Serializable;

/**
 * The Class TestContextItem007.
 */
public class TestContextLongObjectItem implements Serializable {
    private static final long serialVersionUID = -1029406737866392421L;

    private static final int HASH_PRIME_1 = 31;

    private Long longValue = new Long(0);

    /**
     * The Constructor.
     */
    public TestContextLongObjectItem() {}

    /**
     * The Constructor.
     *
     * @param longValue the long value
     */
    public TestContextLongObjectItem(final Long longValue) {
        this.longValue = longValue;
    }

    /**
     * Gets the long value.
     *
     * @return the long value
     */
    public Long getLongValue() {
        return longValue;
    }

    /**
     * Sets the long value.
     *
     * @param longValue the long value
     */
    public void setLongValue(final Long longValue) {
        this.longValue = longValue;
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
        result = prime * result + ((longValue == null) ? 0 : longValue.hashCode());
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
        final TestContextLongObjectItem other = (TestContextLongObjectItem) obj;
        if (longValue == null) {
            if (other.longValue != null) {
                return false;
            }
        } else if (!longValue.equals(other.longValue)) {
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
        return "TestContextItem007 [longValue=" + longValue + "]";
    }
}
