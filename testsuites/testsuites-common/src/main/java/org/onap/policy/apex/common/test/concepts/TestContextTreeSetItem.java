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
import java.util.Set;
import java.util.TreeSet;

/**
 * The Class TestContextItem00B.
 */
public class TestContextTreeSetItem implements Serializable {
    private static final long serialVersionUID = 1254589722957250388L;

    private static final int HASH_PRIME_1 = 31;

    private Set<String> setValue = new TreeSet<String>();

    /**
     * The Constructor.
     */
    public TestContextTreeSetItem() {}

    /**
     * The Constructor.
     *
     * @param setArray the set array
     */
    public TestContextTreeSetItem(final String[] setArray) {}

    /**
     * The Constructor.
     *
     * @param setValue the set value
     */
    public TestContextTreeSetItem(final TreeSet<String> setValue) {
        this.setValue = setValue;
    }

    /**
     * Gets the set value.
     *
     * @return the sets the value
     */
    public Set<String> getSetValue() {
        if (setValue == null) {
            setValue = new TreeSet<String>();
        }
        return setValue;
    }

    /**
     * Sets the set value.
     *
     * @param setValue the sets the value
     */
    public void setSetValue(final TreeSet<String> setValue) {
        this.setValue = setValue;
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
        result = prime * result + ((setValue == null) ? 0 : setValue.hashCode());
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
        final TestContextTreeSetItem other = (TestContextTreeSetItem) obj;
        if (setValue == null) {
            if (other.setValue != null) {
                return false;
            }
        } else if (!setValue.equals(other.setValue)) {
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
        return "TestContextItem00B [setValue=" + setValue + "]";
    }
}
