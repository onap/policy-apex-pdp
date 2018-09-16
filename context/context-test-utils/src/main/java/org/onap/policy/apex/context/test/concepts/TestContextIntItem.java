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
 * The Class TestContextItem002.
 */
public class TestContextIntItem implements Serializable {
    private static final long serialVersionUID = -8978435658277900984L;

    private static final int HASH_PRIME_1 = 31;

    private int intValue = 0;

    /**
     * The Constructor.
     */
    public TestContextIntItem() {}

    /**
     * The Constructor.
     *
     * @param intValue the int value
     */
    public TestContextIntItem(final Integer intValue) {
        this.intValue = intValue;
    }

    /**
     * The Constructor.
     *
     * @param original the original
     */
    public TestContextIntItem(final TestContextIntItem original) {
        this.intValue = original.intValue;
    }

    /**
     * Gets the int value.
     *
     * @return the int value
     */
    public int getIntValue() {
        return intValue;
    }

    /**
     * Sets the int value.
     *
     * @param intValue the int value
     */
    public void setIntValue(final int intValue) {
        this.intValue = intValue;
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
        result = prime * result + intValue;
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
        final TestContextIntItem other = (TestContextIntItem) obj;
        return intValue == other.intValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TestContextItem002 [intValue=" + intValue + "]";
    }
}
