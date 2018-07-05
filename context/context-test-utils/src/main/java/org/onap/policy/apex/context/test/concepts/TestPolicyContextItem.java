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
 * The Class TestPolicyContextItem.
 */
public class TestPolicyContextItem implements Serializable {
    private static final long serialVersionUID = 6336372857646152910L;

    private static final int HASH_PRIME_1 = 31;

    private TestContextStringItem testPolicyContextItem000;
    private TestContextLongItem testPolicyContextItem001;
    private TestContextDoubleItem testPolicyContextItem002;
    private TestContextBooleanItem testPolicyContextItem003;
    private TestContextLongItem testPolicyContextItem004;
    private TestContextTreeMapItem testPolicyContextItem005;

    /**
     * Gets the test policy context item 000.
     *
     * @return the test policy context item 000
     */
    public TestContextStringItem getTestPolicyContextItem000() {
        return testPolicyContextItem000;
    }

    /**
     * Sets the test policy context item 000.
     *
     * @param testPolicyContextItem000 the test policy context item 000
     */
    public void setTestPolicyContextItem000(final TestContextStringItem testPolicyContextItem000) {
        this.testPolicyContextItem000 = testPolicyContextItem000;
    }

    /**
     * Gets the test policy context item 001.
     *
     * @return the test policy context item 001
     */
    public TestContextLongItem getTestPolicyContextItem001() {
        return testPolicyContextItem001;
    }

    /**
     * Sets the test policy context item 001.
     *
     * @param testPolicyContextItem001 the test policy context item 001
     */
    public void setTestPolicyContextItem001(final TestContextLongItem testPolicyContextItem001) {
        this.testPolicyContextItem001 = testPolicyContextItem001;
    }

    /**
     * Gets the test policy context item 002.
     *
     * @return the test policy context item 002
     */
    public TestContextDoubleItem getTestPolicyContextItem002() {
        return testPolicyContextItem002;
    }

    /**
     * Sets the test policy context item 002.
     *
     * @param testPolicyContextItem002 the test policy context item 002
     */
    public void setTestPolicyContextItem002(final TestContextDoubleItem testPolicyContextItem002) {
        this.testPolicyContextItem002 = testPolicyContextItem002;
    }

    /**
     * Gets the test policy context item 003.
     *
     * @return the test policy context item 003
     */
    public TestContextBooleanItem getTestPolicyContextItem003() {
        return testPolicyContextItem003;
    }

    /**
     * Sets the test policy context item 003.
     *
     * @param testPolicyContextItem003 the test policy context item 003
     */
    public void setTestPolicyContextItem003(final TestContextBooleanItem testPolicyContextItem003) {
        this.testPolicyContextItem003 = testPolicyContextItem003;
    }

    /**
     * Gets the test policy context item 004.
     *
     * @return the test policy context item 004
     */
    public TestContextLongItem getTestPolicyContextItem004() {
        return testPolicyContextItem004;
    }

    /**
     * Sets the test policy context item 004.
     *
     * @param testPolicyContextItem004 the test policy context item 004
     */
    public void setTestPolicyContextItem004(final TestContextLongItem testPolicyContextItem004) {
        this.testPolicyContextItem004 = testPolicyContextItem004;
    }

    /**
     * Gets the test policy context item 005.
     *
     * @return the test policy context item 005
     */
    public TestContextTreeMapItem getTestPolicyContextItem005() {
        return testPolicyContextItem005;
    }

    /**
     * Sets the test policy context item 005.
     *
     * @param testPolicyContextItem005 the test policy context item 005
     */
    public void setTestPolicyContextItem005(final TestContextTreeMapItem testPolicyContextItem005) {
        this.testPolicyContextItem005 = testPolicyContextItem005;
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
        result = prime * result + ((testPolicyContextItem000 == null) ? 0 : testPolicyContextItem000.hashCode());
        result = prime * result + ((testPolicyContextItem001 == null) ? 0 : testPolicyContextItem001.hashCode());
        result = prime * result + ((testPolicyContextItem002 == null) ? 0 : testPolicyContextItem002.hashCode());
        result = prime * result + ((testPolicyContextItem003 == null) ? 0 : testPolicyContextItem003.hashCode());
        result = prime * result + ((testPolicyContextItem004 == null) ? 0 : testPolicyContextItem004.hashCode());
        result = prime * result + ((testPolicyContextItem005 == null) ? 0 : testPolicyContextItem005.hashCode());
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
        final TestPolicyContextItem other = (TestPolicyContextItem) obj;
        if (testPolicyContextItem000 == null) {
            if (other.testPolicyContextItem000 != null) {
                return false;
            }
        } else if (!testPolicyContextItem000.equals(other.testPolicyContextItem000)) {
            return false;
        }
        if (testPolicyContextItem001 == null) {
            if (other.testPolicyContextItem001 != null) {
                return false;
            }
        } else if (!testPolicyContextItem001.equals(other.testPolicyContextItem001)) {
            return false;
        }
        if (testPolicyContextItem002 == null) {
            if (other.testPolicyContextItem002 != null) {
                return false;
            }
        } else if (!testPolicyContextItem002.equals(other.testPolicyContextItem002)) {
            return false;
        }
        if (testPolicyContextItem003 == null) {
            if (other.testPolicyContextItem003 != null) {
                return false;
            }
        } else if (!testPolicyContextItem003.equals(other.testPolicyContextItem003)) {
            return false;
        }
        if (testPolicyContextItem004 == null) {
            if (other.testPolicyContextItem004 != null) {
                return false;
            }
        } else if (!testPolicyContextItem004.equals(other.testPolicyContextItem004)) {
            return false;
        }
        if (testPolicyContextItem005 == null) {
            if (other.testPolicyContextItem005 != null) {
                return false;
            }
        } else if (!testPolicyContextItem005.equals(other.testPolicyContextItem005)) {
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
        return "TestPolicyContextItem [testPolicyContextItem000=" + testPolicyContextItem000
                + ", testPolicyContextItem001=" + testPolicyContextItem001 + ", testPolicyContextItem002="
                + testPolicyContextItem002 + ", testPolicyContextItem003=" + testPolicyContextItem003
                + ", testPolicyContextItem004=" + testPolicyContextItem004 + ", testPolicyContextItem005="
                + testPolicyContextItem005 + "]";
    }
}
