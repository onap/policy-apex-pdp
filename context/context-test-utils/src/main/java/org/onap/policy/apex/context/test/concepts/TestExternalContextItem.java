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
 * The Class TestExternalContextItem.
 */
public class TestExternalContextItem implements Serializable {
    private static final long serialVersionUID = 3512435733818672173L;

    private static final int HASH_PRIME_1 = 31;

    private TestContextBooleanItem testExternalContextItem000;
    private TestContextByteItem testExternalContextItem001;
    private TestContextIntItem testExternalContextItem002;
    private TestContextLongItem testExternalContextItem003;
    private TestContextFloatItem testExternalContextItem004;
    private TestContextDoubleItem testExternalContextItem005;
    private TestContextStringItem testExternalContextItem006;
    private TestContextLongObjectItem testExternalContextItem007;
    private TestContextDateItem testExternalContextItem008;
    private TestContextDateTzItem testExternalContextItem009;
    private TestContextDateLocaleItem testExternalContextItem00A;
    private TestContextTreeSetItem testExternalContextItem00B;
    private TestContextTreeMapItem testExternalContextItem00C;

    /**
     * Gets the test external context item 000.
     *
     * @return the test external context item 000
     */
    public TestContextBooleanItem getTestExternalContextItem000() {
        return testExternalContextItem000;
    }

    /**
     * Sets the test external context item 000.
     *
     * @param testExternalContextItem000 the test external context item 000
     */
    public void setTestExternalContextItem000(final TestContextBooleanItem testExternalContextItem000) {
        this.testExternalContextItem000 = testExternalContextItem000;
    }

    /**
     * Gets the test external context item 001.
     *
     * @return the test external context item 001
     */
    public TestContextByteItem getTestExternalContextItem001() {
        return testExternalContextItem001;
    }

    /**
     * Sets the test external context item 001.
     *
     * @param testExternalContextItem001 the test external context item 001
     */
    public void setTestExternalContextItem001(final TestContextByteItem testExternalContextItem001) {
        this.testExternalContextItem001 = testExternalContextItem001;
    }

    /**
     * Gets the test external context item 002.
     *
     * @return the test external context item 002
     */
    public TestContextIntItem getTestExternalContextItem002() {
        return testExternalContextItem002;
    }

    /**
     * Sets the test external context item 002.
     *
     * @param testExternalContextItem002 the test external context item 002
     */
    public void setTestExternalContextItem002(final TestContextIntItem testExternalContextItem002) {
        this.testExternalContextItem002 = testExternalContextItem002;
    }

    /**
     * Gets the test external context item 003.
     *
     * @return the test external context item 003
     */
    public TestContextLongItem getTestExternalContextItem003() {
        return testExternalContextItem003;
    }

    /**
     * Sets the test external context item 003.
     *
     * @param testExternalContextItem003 the test external context item 003
     */
    public void setTestExternalContextItem003(final TestContextLongItem testExternalContextItem003) {
        this.testExternalContextItem003 = testExternalContextItem003;
    }

    /**
     * Gets the test external context item 004.
     *
     * @return the test external context item 004
     */
    public TestContextFloatItem getTestExternalContextItem004() {
        return testExternalContextItem004;
    }

    /**
     * Sets the test external context item 004.
     *
     * @param testExternalContextItem004 the test external context item 004
     */
    public void setTestExternalContextItem004(final TestContextFloatItem testExternalContextItem004) {
        this.testExternalContextItem004 = testExternalContextItem004;
    }

    /**
     * Gets the test external context item 005.
     *
     * @return the test external context item 005
     */
    public TestContextDoubleItem getTestExternalContextItem005() {
        return testExternalContextItem005;
    }

    /**
     * Sets the test external context item 005.
     *
     * @param testExternalContextItem005 the test external context item 005
     */
    public void setTestExternalContextItem005(final TestContextDoubleItem testExternalContextItem005) {
        this.testExternalContextItem005 = testExternalContextItem005;
    }

    /**
     * Gets the test external context item 006.
     *
     * @return the test external context item 006
     */
    public TestContextStringItem getTestExternalContextItem006() {
        return testExternalContextItem006;
    }

    /**
     * Sets the test external context item 006.
     *
     * @param testExternalContextItem006 the test external context item 006
     */
    public void setTestExternalContextItem006(final TestContextStringItem testExternalContextItem006) {
        this.testExternalContextItem006 = testExternalContextItem006;
    }

    /**
     * Gets the test external context item 007.
     *
     * @return the test external context item 007
     */
    public TestContextLongObjectItem getTestExternalContextItem007() {
        return testExternalContextItem007;
    }

    /**
     * Sets the test external context item 007.
     *
     * @param testExternalContextItem007 the test external context item 007
     */
    public void setTestExternalContextItem007(final TestContextLongObjectItem testExternalContextItem007) {
        this.testExternalContextItem007 = testExternalContextItem007;
    }

    /**
     * Gets the test external context item 008.
     *
     * @return the test external context item 008
     */
    public TestContextDateItem getTestExternalContextItem008() {
        return testExternalContextItem008;
    }

    /**
     * Sets the test external context item 008.
     *
     * @param testExternalContextItem008 the test external context item 008
     */
    public void setTestExternalContextItem008(final TestContextDateItem testExternalContextItem008) {
        this.testExternalContextItem008 = testExternalContextItem008;
    }

    /**
     * Gets the test external context item 009.
     *
     * @return the test external context item 009
     */
    public TestContextDateTzItem getTestExternalContextItem009() {
        return testExternalContextItem009;
    }

    /**
     * Sets the test external context item 009.
     *
     * @param testExternalContextItem009 the test external context item 009
     */
    public void setTestExternalContextItem009(final TestContextDateTzItem testExternalContextItem009) {
        this.testExternalContextItem009 = testExternalContextItem009;
    }

    /**
     * Gets the test external context item 00 A.
     *
     * @return the test external context item 00 A
     */
    public TestContextDateLocaleItem getTestExternalContextItem00A() {
        return testExternalContextItem00A;
    }

    /**
     * Sets the test external context item 00 A.
     *
     * @param testExternalContextItem00A the test external context item 00 A
     */
    public void setTestExternalContextItem00A(final TestContextDateLocaleItem testExternalContextItem00A) {
        this.testExternalContextItem00A = testExternalContextItem00A;
    }

    /**
     * Gets the test external context item 00 B.
     *
     * @return the test external context item 00 B
     */
    public TestContextTreeSetItem getTestExternalContextItem00B() {
        return testExternalContextItem00B;
    }

    /**
     * Sets the test external context item 00 B.
     *
     * @param testExternalContextItem00B the test external context item 00 B
     */
    public void setTestExternalContextItem00B(final TestContextTreeSetItem testExternalContextItem00B) {
        this.testExternalContextItem00B = testExternalContextItem00B;
    }

    /**
     * Gets the test external context item 00 C.
     *
     * @return the test external context item 00 C
     */
    public TestContextTreeMapItem getTestExternalContextItem00C() {
        return testExternalContextItem00C;
    }

    /**
     * Sets the test external context item 00 C.
     *
     * @param testExternalContextItem00C the test external context item 00 C
     */
    public void setTestExternalContextItem00C(final TestContextTreeMapItem testExternalContextItem00C) {
        this.testExternalContextItem00C = testExternalContextItem00C;
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
        result = prime * result + ((testExternalContextItem000 == null) ? 0 : testExternalContextItem000.hashCode());
        result = prime * result + ((testExternalContextItem001 == null) ? 0 : testExternalContextItem001.hashCode());
        result = prime * result + ((testExternalContextItem002 == null) ? 0 : testExternalContextItem002.hashCode());
        result = prime * result + ((testExternalContextItem003 == null) ? 0 : testExternalContextItem003.hashCode());
        result = prime * result + ((testExternalContextItem004 == null) ? 0 : testExternalContextItem004.hashCode());
        result = prime * result + ((testExternalContextItem005 == null) ? 0 : testExternalContextItem005.hashCode());
        result = prime * result + ((testExternalContextItem006 == null) ? 0 : testExternalContextItem006.hashCode());
        result = prime * result + ((testExternalContextItem007 == null) ? 0 : testExternalContextItem007.hashCode());
        result = prime * result + ((testExternalContextItem008 == null) ? 0 : testExternalContextItem008.hashCode());
        result = prime * result + ((testExternalContextItem009 == null) ? 0 : testExternalContextItem009.hashCode());
        result = prime * result + ((testExternalContextItem00A == null) ? 0 : testExternalContextItem00A.hashCode());
        result = prime * result + ((testExternalContextItem00B == null) ? 0 : testExternalContextItem00B.hashCode());
        result = prime * result + ((testExternalContextItem00C == null) ? 0 : testExternalContextItem00C.hashCode());
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
        final TestExternalContextItem other = (TestExternalContextItem) obj;
        if (testExternalContextItem000 == null) {
            if (other.testExternalContextItem000 != null) {
                return false;
            }
        } else if (!testExternalContextItem000.equals(other.testExternalContextItem000)) {
            return false;
        }
        if (testExternalContextItem001 == null) {
            if (other.testExternalContextItem001 != null) {
                return false;
            }
        } else if (!testExternalContextItem001.equals(other.testExternalContextItem001)) {
            return false;
        }
        if (testExternalContextItem002 == null) {
            if (other.testExternalContextItem002 != null) {
                return false;
            }
        } else if (!testExternalContextItem002.equals(other.testExternalContextItem002)) {
            return false;
        }
        if (testExternalContextItem003 == null) {
            if (other.testExternalContextItem003 != null) {
                return false;
            }
        } else if (!testExternalContextItem003.equals(other.testExternalContextItem003)) {
            return false;
        }
        if (testExternalContextItem004 == null) {
            if (other.testExternalContextItem004 != null) {
                return false;
            }
        } else if (!testExternalContextItem004.equals(other.testExternalContextItem004)) {
            return false;
        }
        if (testExternalContextItem005 == null) {
            if (other.testExternalContextItem005 != null) {
                return false;
            }
        } else if (!testExternalContextItem005.equals(other.testExternalContextItem005)) {
            return false;
        }
        if (testExternalContextItem006 == null) {
            if (other.testExternalContextItem006 != null) {
                return false;
            }
        } else if (!testExternalContextItem006.equals(other.testExternalContextItem006)) {
            return false;
        }
        if (testExternalContextItem007 == null) {
            if (other.testExternalContextItem007 != null) {
                return false;
            }
        } else if (!testExternalContextItem007.equals(other.testExternalContextItem007)) {
            return false;
        }
        if (testExternalContextItem008 == null) {
            if (other.testExternalContextItem008 != null) {
                return false;
            }
        } else if (!testExternalContextItem008.equals(other.testExternalContextItem008)) {
            return false;
        }
        if (testExternalContextItem009 == null) {
            if (other.testExternalContextItem009 != null) {
                return false;
            }
        } else if (!testExternalContextItem009.equals(other.testExternalContextItem009)) {
            return false;
        }
        if (testExternalContextItem00A == null) {
            if (other.testExternalContextItem00A != null) {
                return false;
            }
        } else if (!testExternalContextItem00A.equals(other.testExternalContextItem00A)) {
            return false;
        }
        if (testExternalContextItem00B == null) {
            if (other.testExternalContextItem00B != null) {
                return false;
            }
        } else if (!testExternalContextItem00B.equals(other.testExternalContextItem00B)) {
            return false;
        }
        if (testExternalContextItem00C == null) {
            if (other.testExternalContextItem00C != null) {
                return false;
            }
        } else if (!testExternalContextItem00C.equals(other.testExternalContextItem00C)) {
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
        return "TestExternalContextItem [testExternalContextItem000=" + testExternalContextItem000
                + ", testExternalContextItem001=" + testExternalContextItem001 + ", testExternalContextItem002="
                + testExternalContextItem002 + ", testExternalContextItem003=" + testExternalContextItem003
                + ", testExternalContextItem004=" + testExternalContextItem004 + ", testExternalContextItem005="
                + testExternalContextItem005 + ", testExternalContextItem006=" + testExternalContextItem006
                + ", testExternalContextItem007=" + testExternalContextItem007 + ", testExternalContextItem008="
                + testExternalContextItem008 + ", testExternalContextItem009=" + testExternalContextItem009
                + ", testExternalContextItem00A=" + testExternalContextItem00A + ", testExternalContextItem00B="
                + testExternalContextItem00B + ", testExternalContextItem00C=" + testExternalContextItem00C + "]";
    }
}
