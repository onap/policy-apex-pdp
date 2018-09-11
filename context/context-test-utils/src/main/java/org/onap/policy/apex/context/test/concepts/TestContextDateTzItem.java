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
import java.util.TimeZone;

/**
 * The Class TestContextItem009.
 */
public class TestContextDateTzItem implements Serializable {
    private static final long serialVersionUID = 5604426823170331706L;

    private static final int HASH_PRIME_1 = 31;
    private static final int HASH_PRIME_2 = 1231;
    private static final int HASH_PRIME_3 = 1237;

    private TestContextDateItem dateValue = new TestContextDateItem(System.currentTimeMillis());
    private String timeZoneString = TimeZone.getTimeZone("Europe/Dublin").getDisplayName();
    private boolean dst = false;

    /**
     * The Constructor.
     */
    public TestContextDateTzItem() {
        dst = true;
    }

    /**
     * The Constructor.
     *
     * @param dateValue the date value
     * @param tzValue the tz value
     * @param dst the dst
     */
    public TestContextDateTzItem(final TestContextDateItem dateValue, final String tzValue, final boolean dst) {
        this.dateValue = dateValue;
        this.timeZoneString = TimeZone.getTimeZone(tzValue).getDisplayName();
        this.dst = dst;
    }

    /**
     * The Constructor.
     *
     * @param original the original
     */
    public TestContextDateTzItem(final TestContextDateTzItem original) {
        this.dateValue = original.dateValue;
        this.timeZoneString = original.timeZoneString;
        this.dst = original.dst;
    }

    /**
     * Gets the date value.
     *
     * @return the date value
     */
    public TestContextDateItem getDateValue() {
        return dateValue;
    }

    /**
     * Sets the date value.
     *
     * @param dateValue the date value
     */
    public void setDateValue(final TestContextDateItem dateValue) {
        this.dateValue = dateValue;
    }

    /**
     * Gets the TZ value.
     *
     * @return the TZ value
     */
    public String getTzValue() {
        return timeZoneString;
    }

    /**
     * Sets the TZ value.
     *
     * @param tzValue the TZ value
     */
    public void setTzValue(final String tzValue) {
        this.timeZoneString = TimeZone.getTimeZone(tzValue).getDisplayName();
    }

    /**
     * Gets the DST.
     *
     * @return the dst
     */
    public boolean getDst() {
        return dst;
    }

    /**
     * Sets the DST.
     *
     * @param newDst the dst
     */
    public void setDst(final boolean newDst) {
        this.dst = newDst;
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
        result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
        result = prime * result + (dst ? HASH_PRIME_2 : HASH_PRIME_3);
        result = prime * result + ((timeZoneString == null) ? 0 : timeZoneString.hashCode());
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
        final TestContextDateTzItem other = (TestContextDateTzItem) obj;
        if (dateValue == null) {
            if (other.dateValue != null) {
                return false;
            }
        } else if (!dateValue.equals(other.dateValue)) {
            return false;
        }
        if (dst != other.dst) {
            return false;
        }
        if (timeZoneString == null) {
            if (other.timeZoneString != null) {
                return false;
            }
        } else if (!timeZoneString.equals(other.timeZoneString)) {
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
        return "TestContextItem009 [dateValue=" + dateValue + ", tzValue=" + timeZoneString + ", dst=" + dst + "]";
    }
}
