/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
 *  Modifications Copyright (C) 2021 AT&T Intellectual Property. All rights reserved.
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
import java.util.Locale;
import java.util.TimeZone;
import lombok.Data;

/**
 * The Class TestContextDateLocaleItem.
 */
@Data
public class TestContextDateLocaleItem implements Serializable {
    private static final long serialVersionUID = -6579903685538233754L;

    private TestContextDateItem dateValue = new TestContextDateItem(System.currentTimeMillis());
    private String tzValue = TimeZone.getTimeZone("Europe/Dublin").getDisplayName();
    private boolean dst = false;
    private int utcOffset = 0;
    private Locale locale = Locale.ENGLISH;

    /**
     * The Constructor.
     */
    public TestContextDateLocaleItem() {
    }

    /**
     * The Constructor.
     *
     * @param dateValue the date value
     * @param tzValue the tz value
     * @param dst the dst
     * @param utcOffset the utc offset
     * @param language the language
     * @param country the country
     */
    public TestContextDateLocaleItem(final TestContextDateItem dateValue, final String tzValue, final boolean dst,
            final int utcOffset, final String language, final String country) {
        this.dateValue = dateValue;
        this.tzValue = TimeZone.getTimeZone(tzValue).getDisplayName();
        this.dst = dst;
        this.utcOffset = utcOffset;

        this.locale = new Locale(language, country);
    }

    /**
     * The Constructor.
     *
     * @param original the original
     */
    public TestContextDateLocaleItem(final TestContextDateLocaleItem original) {
        this.dateValue = original.dateValue;
        this.tzValue = TimeZone.getTimeZone(original.tzValue).getDisplayName();
        this.dst = original.dst;
        this.utcOffset = original.utcOffset;

        this.locale = new Locale(original.getLocale().getCountry(), original.getLocale().getLanguage());
    }

    /**
     * Sets the TZ value.
     *
     * @param tzValue the TZ value
     */
    public void setTzValue(final String tzValue) {
        if (tzValue != null) {
            this.tzValue = TimeZone.getTimeZone(tzValue).getDisplayName();
        } else {
            this.tzValue = null;
        }
    }
}
