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
 * The Class TestContextItem005.
 */
public class TestContextDoubleItem implements Serializable {
    private static final long serialVersionUID = -2958758261076734821L;

    private static final int HASH_PRIME_1 = 31;
    private static final int FOUR_BYTES = 32;

    private double doubleValue = 0;

    /**
     * The Constructor.
     */
    public TestContextDoubleItem() {}

    /**
     * The Constructor.
     *
     * @param doubleValue the double value
     */
    public TestContextDoubleItem(final Double doubleValue) {
        this.doubleValue = doubleValue;
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
     * @param doubleValue the double value
     */
    public void setDoubleValue(final double doubleValue) {
        this.doubleValue = doubleValue;
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
        long temp;
        temp = Double.doubleToLongBits(doubleValue);
        result = prime * result + (int) (temp ^ (temp >>> FOUR_BYTES));
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
        final TestContextDoubleItem other = (TestContextDoubleItem) obj;
        return Double.doubleToLongBits(doubleValue) == Double.doubleToLongBits(other.doubleValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TestContextItem005 [doubleValue=" + doubleValue + "]";
    }
}
