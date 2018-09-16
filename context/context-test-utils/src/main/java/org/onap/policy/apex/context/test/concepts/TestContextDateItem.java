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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * The Class TestContextItem008.
 */
public class TestContextDateItem implements Serializable {
    private static final long serialVersionUID = -6984963129968805460L;

    private static final int HASH_PRIME_1 = 31;
    private static final int FOUR_BYTES = 32;

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
        setDateValue(dateValue.getTime());
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
     * Gets the time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the month.
     *
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Gets the day.
     *
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * Gets the hour.
     *
     * @return the hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * Gets the minute.
     *
     * @return the minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public int getSecond() {
        return second;
    }

    /**
     * Gets the milli second.
     *
     * @return the milli second
     */
    public int getMilliSecond() {
        return milliSecond;
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
        setDateValue(dateValue.getTime());
    }

    /**
     * Sets the date value.
     *
     * @param dateValue the date value
     */
    public void setDateValue(final long dateValue) {
        this.time = dateValue;

        final Calendar calendar = Calendar.getInstance();
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


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = HASH_PRIME_1;
        int result = 1;
        result = prime * result + day;
        result = prime * result + hour;
        result = prime * result + milliSecond;
        result = prime * result + minute;
        result = prime * result + month;
        result = prime * result + second;
        result = prime * result + (int) (time ^ (time >>> FOUR_BYTES));
        result = prime * result + year;
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
        final TestContextDateItem other = (TestContextDateItem) obj;
        if (day != other.day) {
            return false;
        }
        if (hour != other.hour) {
            return false;
        }
        if (milliSecond != other.milliSecond) {
            return false;
        }
        if (minute != other.minute) {
            return false;
        }
        if (month != other.month) {
            return false;
        }
        if (second != other.second) {
            return false;
        }
        if (time != other.time) {
            return false;
        }
        return year == other.year;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TestContextItem008 [time=" + time + ", year=" + year + ", month=" + month + ", day=" + day + ", hour="
                + hour + ", minute=" + minute + ", second=" + second + ", milliSecond=" + milliSecond + "]";
    }
}
