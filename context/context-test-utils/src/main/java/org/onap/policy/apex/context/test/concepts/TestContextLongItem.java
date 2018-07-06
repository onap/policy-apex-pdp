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

/**
 * The Class TestContextItem003.
 */
public class TestContextLongItem implements Serializable {
    private static final long serialVersionUID = 3599267534512489386L;

    private static final int HASH_PRIME_1 = 31;
    private static final int FOUR_BYTES = 33;

    private long longValue = 0;

    /**
     * The Constructor.
     */
    public TestContextLongItem() {}

    /**
     * The Constructor.
     *
     * @param longValue the long value
     */
    public TestContextLongItem(final Long longValue) {
        this.longValue = longValue;
    }

    /**
     * Gets the long value.
     *
     * @return the long value
     */
    public long getLongValue() {
        return longValue;
    }

    /**
     * Sets the long value.
     *
     * @param longValue the long value
     */
    public void setLongValue(final long longValue) {
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
        result = prime * result + (int) (longValue ^ (longValue >>> FOUR_BYTES));
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
        final TestContextLongItem other = (TestContextLongItem) obj;
        if (longValue != other.longValue) {
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
        return "TestContextItem003 [longValue=" + longValue + "]";
    }
}
