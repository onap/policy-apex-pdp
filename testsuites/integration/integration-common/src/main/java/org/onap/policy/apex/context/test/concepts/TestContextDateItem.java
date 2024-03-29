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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import lombok.Data;

/**
 * The Class TestContextDateItem.
 */
@Data
public class TestContextDateItem implements Serializable {
    private static final long serialVersionUID = -6984963129968805460L;

    private long time;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int milliSecond;

    /**
     * The Constructor.
     */
    public TestContextDateItem() {
        this(new Date(System.currentTimeMillis()));
    }

    /**
     * The Constructor.
     *
     * @param dateValue the date value
     */
    public TestContextDateItem(final Date dateValue) {
        if (dateValue != null) {
            setDateValue(dateValue.getTime());
        } else {
            new Date(0);
        }
    }

    /**
     * The Constructor.
     *
     * @param time the time
     */
    public TestContextDateItem(final long time) {
        setDateValue(time);
    }

    /**
     * Gets the date value.
     *
     * @return the date value
     */
    public Date getDateValue() {
        return new Date(time);
    }

    /**
     * Sets the date value.
     *
     * @param dateValue the date value
     */
    public void setDateValue(final Date dateValue) {
        if (dateValue != null) {
            setDateValue(dateValue.getTime());
        }
    }

    /**
     * Sets the date value.
     *
     * @param dateValue the date value
     */
    public void setDateValue(final long dateValue) {
        this.time = dateValue;

        final var calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        milliSecond = calendar.get(Calendar.MILLISECOND);
    }
}
