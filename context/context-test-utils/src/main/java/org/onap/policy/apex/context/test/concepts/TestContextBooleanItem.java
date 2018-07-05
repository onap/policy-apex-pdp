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
 * The Class TestContextItem000.
 */
public class TestContextBooleanItem implements Serializable {
    private static final int HASH_PRIME_1 = 31;
    private static final int HASH_PRIME_2 = 1231;
    private static final int HASH_PRIME_3 = 1237;

    private static final long serialVersionUID = 7241008665286367796L;

    private boolean flag = false;;

    /**
     * The Constructor.
     */
    public TestContextBooleanItem() {}

    /**
     * The Constructor.
     *
     * @param flag the flag
     */
    public TestContextBooleanItem(final Boolean flag) {
        this.flag = flag;
    }

    /**
     * Gets the flag.
     *
     * @return the flag
     */
    public boolean getFlag() {
        return flag;
    }

    /**
     * Sets the flag.
     *
     * @param flag the flag
     */
    public void setFlag(final boolean flag) {
        this.flag = flag;
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
        result = prime * result + (flag ? HASH_PRIME_2 : HASH_PRIME_3);
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
        final TestContextBooleanItem other = (TestContextBooleanItem) obj;
        if (flag != other.flag) {
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
        return "TestContextItem000 [flag=" + flag + "]";
    }
}
