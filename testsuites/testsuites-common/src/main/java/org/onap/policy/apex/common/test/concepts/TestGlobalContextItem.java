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
 * The Class TestGlobalContextItem.
 */
public class TestGlobalContextItem implements Serializable {
    private static final long serialVersionUID = 3348445332683174361L;

    private static final int HASH_PRIME_1 = 31;

    private TestContextBooleanItem testGlobalContextItem000;
    private TestContextByteItem testGlobalContextItem001;
    private TestContextIntItem testGlobalContextItem002;
    private TestContextLongItem testGlobalContextItem003;
    private TestContextFloatItem testGlobalContextItem004;
    private TestContextDoubleItem testGlobalContextItem005;
    private TestContextStringItem testGlobalContextItem006;
    private TestContextLongObjectItem testGlobalContextItem007;
    private TestContextDateItem testGlobalContextItem008;
    private TestContextDateTzItem testGlobalContextItem009;
    private TestContextDateLocaleItem testGlobalContextItem00A;
    private TestContextTreeSetItem testGlobalContextItem00B;
    private TestContextTreeMapItem testGlobalContextItem00C;

    /**
     * Gets the test global context item 000.
     *
     * @return the test global context item 000
     */
    public TestContextBooleanItem getTestGlobalContextItem000() {
        return testGlobalContextItem000;
    }

    /**
     * Sets the test global context item 000.
     *
     * @param testGlobalContextItem000 the test global context item 000
     */
    public void setTestGlobalContextItem000(final TestContextBooleanItem testGlobalContextItem000) {
        this.testGlobalContextItem000 = testGlobalContextItem000;
    }

    /**
     * Gets the test global context item 001.
     *
     * @return the test global context item 001
     */
    public TestContextByteItem getTestGlobalContextItem001() {
        return testGlobalContextItem001;
    }

    /**
     * Sets the test global context item 001.
     *
     * @param testGlobalContextItem001 the test global context item 001
     */
    public void setTestGlobalContextItem001(final TestContextByteItem testGlobalContextItem001) {
        this.testGlobalContextItem001 = testGlobalContextItem001;
    }

    /**
     * Gets the test global context item 002.
     *
     * @return the test global context item 002
     */
    public TestContextIntItem getTestGlobalContextItem002() {
        return testGlobalContextItem002;
    }

    /**
     * Sets the test global context item 002.
     *
     * @param testGlobalContextItem002 the test global context item 002
     */
    public void setTestGlobalContextItem002(final TestContextIntItem testGlobalContextItem002) {
        this.testGlobalContextItem002 = testGlobalContextItem002;
    }

    /**
     * Gets the test global context item 003.
     *
     * @return the test global context item 003
     */
    public TestContextLongItem getTestGlobalContextItem003() {
        return testGlobalContextItem003;
    }

    /**
     * Sets the test global context item 003.
     *
     * @param testGlobalContextItem003 the test global context item 003
     */
    public void setTestGlobalContextItem003(final TestContextLongItem testGlobalContextItem003) {
        this.testGlobalContextItem003 = testGlobalContextItem003;
    }

    /**
     * Gets the test global context item 004.
     *
     * @return the test global context item 004
     */
    public TestContextFloatItem getTestGlobalContextItem004() {
        return testGlobalContextItem004;
    }

    /**
     * Sets the test global context item 004.
     *
     * @param testGlobalContextItem004 the test global context item 004
     */
    public void setTestGlobalContextItem004(final TestContextFloatItem testGlobalContextItem004) {
        this.testGlobalContextItem004 = testGlobalContextItem004;
    }

    /**
     * Gets the test global context item 005.
     *
     * @return the test global context item 005
     */
    public TestContextDoubleItem getTestGlobalContextItem005() {
        return testGlobalContextItem005;
    }

    /**
     * Sets the test global context item 005.
     *
     * @param testGlobalContextItem005 the test global context item 005
     */
    public void setTestGlobalContextItem005(final TestContextDoubleItem testGlobalContextItem005) {
        this.testGlobalContextItem005 = testGlobalContextItem005;
    }

    /**
     * Gets the test global context item 006.
     *
     * @return the test global context item 006
     */
    public TestContextStringItem getTestGlobalContextItem006() {
        return testGlobalContextItem006;
    }

    /**
     * Sets the test global context item 006.
     *
     * @param testGlobalContextItem006 the test global context item 006
     */
    public void setTestGlobalContextItem006(final TestContextStringItem testGlobalContextItem006) {
        this.testGlobalContextItem006 = testGlobalContextItem006;
    }

    /**
     * Gets the test global context item 007.
     *
     * @return the test global context item 007
     */
    public TestContextLongObjectItem getTestGlobalContextItem007() {
        return testGlobalContextItem007;
    }

    /**
     * Sets the test global context item 007.
     *
     * @param testGlobalContextItem007 the test global context item 007
     */
    public void setTestGlobalContextItem007(final TestContextLongObjectItem testGlobalContextItem007) {
        this.testGlobalContextItem007 = testGlobalContextItem007;
    }

    /**
     * Gets the test global context item 008.
     *
     * @return the test global context item 008
     */
    public TestContextDateItem getTestGlobalContextItem008() {
        return testGlobalContextItem008;
    }

    /**
     * Sets the test global context item 008.
     *
     * @param testGlobalContextItem008 the test global context item 008
     */
    public void setTestGlobalContextItem008(final TestContextDateItem testGlobalContextItem008) {
        this.testGlobalContextItem008 = testGlobalContextItem008;
    }

    /**
     * Gets the test global context item 009.
     *
     * @return the test global context item 009
     */
    public TestContextDateTzItem getTestGlobalContextItem009() {
        return testGlobalContextItem009;
    }

    /**
     * Sets the test global context item 009.
     *
     * @param testGlobalContextItem009 the test global context item 009
     */
    public void setTestGlobalContextItem009(final TestContextDateTzItem testGlobalContextItem009) {
        this.testGlobalContextItem009 = testGlobalContextItem009;
    }

    /**
     * Gets the test global context item 00 A.
     *
     * @return the test global context item 00 A
     */
    public TestContextDateLocaleItem getTestGlobalContextItem00A() {
        return testGlobalContextItem00A;
    }

    /**
     * Sets the test global context item 00 A.
     *
     * @param testGlobalContextItem00A the test global context item 00 A
     */
    public void setTestGlobalContextItem00A(final TestContextDateLocaleItem testGlobalContextItem00A) {
        this.testGlobalContextItem00A = testGlobalContextItem00A;
    }

    /**
     * Gets the test global context item 00 B.
     *
     * @return the test global context item 00 B
     */
    public TestContextTreeSetItem getTestGlobalContextItem00B() {
        return testGlobalContextItem00B;
    }

    /**
     * Sets the test global context item 00 B.
     *
     * @param testGlobalContextItem00B the test global context item 00 B
     */
    public void setTestGlobalContextItem00B(final TestContextTreeSetItem testGlobalContextItem00B) {
        this.testGlobalContextItem00B = testGlobalContextItem00B;
    }

    /**
     * Gets the test global context item 00 C.
     *
     * @return the test global context item 00 C
     */
    public TestContextTreeMapItem getTestGlobalContextItem00C() {
        return testGlobalContextItem00C;
    }

    /**
     * Sets the test global context item 00 C.
     *
     * @param testGlobalContextItem00C the test global context item 00 C
     */
    public void setTestGlobalContextItem00C(final TestContextTreeMapItem testGlobalContextItem00C) {
        this.testGlobalContextItem00C = testGlobalContextItem00C;
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
        result = prime * result + ((testGlobalContextItem000 == null) ? 0 : testGlobalContextItem000.hashCode());
        result = prime * result + ((testGlobalContextItem001 == null) ? 0 : testGlobalContextItem001.hashCode());
        result = prime * result + ((testGlobalContextItem002 == null) ? 0 : testGlobalContextItem002.hashCode());
        result = prime * result + ((testGlobalContextItem003 == null) ? 0 : testGlobalContextItem003.hashCode());
        result = prime * result + ((testGlobalContextItem004 == null) ? 0 : testGlobalContextItem004.hashCode());
        result = prime * result + ((testGlobalContextItem005 == null) ? 0 : testGlobalContextItem005.hashCode());
        result = prime * result + ((testGlobalContextItem006 == null) ? 0 : testGlobalContextItem006.hashCode());
        result = prime * result + ((testGlobalContextItem007 == null) ? 0 : testGlobalContextItem007.hashCode());
        result = prime * result + ((testGlobalContextItem008 == null) ? 0 : testGlobalContextItem008.hashCode());
        result = prime * result + ((testGlobalContextItem009 == null) ? 0 : testGlobalContextItem009.hashCode());
        result = prime * result + ((testGlobalContextItem00A == null) ? 0 : testGlobalContextItem00A.hashCode());
        result = prime * result + ((testGlobalContextItem00B == null) ? 0 : testGlobalContextItem00B.hashCode());
        result = prime * result + ((testGlobalContextItem00C == null) ? 0 : testGlobalContextItem00C.hashCode());
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
        final TestGlobalContextItem other = (TestGlobalContextItem) obj;
        if (testGlobalContextItem000 == null) {
            if (other.testGlobalContextItem000 != null) {
                return false;
            }
        } else if (!testGlobalContextItem000.equals(other.testGlobalContextItem000)) {
            return false;
        }
        if (testGlobalContextItem001 == null) {
            if (other.testGlobalContextItem001 != null) {
                return false;
            }
        } else if (!testGlobalContextItem001.equals(other.testGlobalContextItem001)) {
            return false;
        }
        if (testGlobalContextItem002 == null) {
            if (other.testGlobalContextItem002 != null) {
                return false;
            }
        } else if (!testGlobalContextItem002.equals(other.testGlobalContextItem002)) {
            return false;
        }
        if (testGlobalContextItem003 == null) {
            if (other.testGlobalContextItem003 != null) {
                return false;
            }
        } else if (!testGlobalContextItem003.equals(other.testGlobalContextItem003)) {
            return false;
        }
        if (testGlobalContextItem004 == null) {
            if (other.testGlobalContextItem004 != null) {
                return false;
            }
        } else if (!testGlobalContextItem004.equals(other.testGlobalContextItem004)) {
            return false;
        }
        if (testGlobalContextItem005 == null) {
            if (other.testGlobalContextItem005 != null) {
                return false;
            }
        } else if (!testGlobalContextItem005.equals(other.testGlobalContextItem005)) {
            return false;
        }
        if (testGlobalContextItem006 == null) {
            if (other.testGlobalContextItem006 != null) {
                return false;
            }
        } else if (!testGlobalContextItem006.equals(other.testGlobalContextItem006)) {
            return false;
        }
        if (testGlobalContextItem007 == null) {
            if (other.testGlobalContextItem007 != null) {
                return false;
            }
        } else if (!testGlobalContextItem007.equals(other.testGlobalContextItem007)) {
            return false;
        }
        if (testGlobalContextItem008 == null) {
            if (other.testGlobalContextItem008 != null) {
                return false;
            }
        } else if (!testGlobalContextItem008.equals(other.testGlobalContextItem008)) {
            return false;
        }
        if (testGlobalContextItem009 == null) {
            if (other.testGlobalContextItem009 != null) {
                return false;
            }
        } else if (!testGlobalContextItem009.equals(other.testGlobalContextItem009)) {
            return false;
        }
        if (testGlobalContextItem00A == null) {
            if (other.testGlobalContextItem00A != null) {
                return false;
            }
        } else if (!testGlobalContextItem00A.equals(other.testGlobalContextItem00A)) {
            return false;
        }
        if (testGlobalContextItem00B == null) {
            if (other.testGlobalContextItem00B != null) {
                return false;
            }
        } else if (!testGlobalContextItem00B.equals(other.testGlobalContextItem00B)) {
            return false;
        }
        if (testGlobalContextItem00C == null) {
            if (other.testGlobalContextItem00C != null) {
                return false;
            }
        } else if (!testGlobalContextItem00C.equals(other.testGlobalContextItem00C)) {
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
        return "TestGlobalContextItem [testGlobalContextItem000=" + testGlobalContextItem000
                + ", testGlobalContextItem001=" + testGlobalContextItem001 + ", testGlobalContextItem002="
                + testGlobalContextItem002 + ", testGlobalContextItem003=" + testGlobalContextItem003
                + ", testGlobalContextItem004=" + testGlobalContextItem004 + ", testGlobalContextItem005="
                + testGlobalContextItem005 + ", testGlobalContextItem006=" + testGlobalContextItem006
                + ", testGlobalContextItem007=" + testGlobalContextItem007 + ", testGlobalContextItem008="
                + testGlobalContextItem008 + ", testGlobalContextItem009=" + testGlobalContextItem009
                + ", testGlobalContextItem00A=" + testGlobalContextItem00A + ", testGlobalContextItem00B="
                + testGlobalContextItem00B + ", testGlobalContextItem00C=" + testGlobalContextItem00C + "]";
    }
}
