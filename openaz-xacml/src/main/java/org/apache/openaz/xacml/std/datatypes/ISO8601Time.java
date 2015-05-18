/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.datatypes;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.openaz.xacml.api.SemanticString;

/**
 * ISO8601Time represents a time of day with an optional timezone indication using ISO8601 standard
 * representations for time, ordering, and operations.
 */
public class ISO8601Time implements IDateTime<ISO8601Time>, Comparable<ISO8601Time>, SemanticString {
    private static final int ARBITRARY_YEAR = 1970;
    private static final int ARBITRARY_MONTH = 11;
    private static final int ARBITRARY_DAY = 15;

    private ISO8601DateTime dateTime;

    protected ISO8601Time(ISO8601DateTime iso8601DateTime) {
        this.dateTime = iso8601DateTime;
    }

    /**
     * Creates a <code>ISO8601Time</code> object for the given instant of time in the given
     * <code>ISO8601TimeZone</code>.
     *
     * @param tzOffsetHours the signed offset in hours for the timezone of this time
     * @param tzOffsetMinutes the unsigned offset in minutes for the timezone of this time
     * @param hourIn the hour
     * @param minuteIn the minute
     * @param secondIn the second
     * @param millisecondIn the millisecond
     */
    public ISO8601Time(String timeZoneIn, int hourIn, int minuteIn, int secondIn, int millisecondIn) {
        this.dateTime = new ISO8601DateTime(timeZoneIn, ARBITRARY_YEAR, ARBITRARY_MONTH, ARBITRARY_DAY,
                                            hourIn, minuteIn, secondIn, millisecondIn);
    }

    public ISO8601Time(int hourIn, int minuteIn, int secondIn, int millisecondIn) {
        this((String)null, hourIn, minuteIn, secondIn, millisecondIn);
    }

    public ISO8601Time(ISO8601TimeZone timeZoneIn, int hourIn, int minuteIn, int secondIn, int millisecondIn) {
        this((timeZoneIn == null ? null : timeZoneIn.getTimeZoneString()), hourIn, minuteIn, secondIn,
             millisecondIn);
    }

    public ISO8601Time(TimeZone timeZoneIn, int hourIn, int minuteIn, int secondIn, int millisecondIn) {
        this((timeZoneIn == null ? null : timeZoneIn.getID()), hourIn, minuteIn, secondIn, millisecondIn);
    }

    public boolean getHasTimeZone() {
        return this.dateTime.getHasTimeZone();
    }

    public String getTimeZone() {
        return this.dateTime.getTimeZone();
    }

    public int getHour() {
        return this.dateTime.getHour();
    }

    public int getMinute() {
        return this.dateTime.getMinute();
    }

    public int getSecond() {
        return this.dateTime.getSecond();
    }

    public int getMillisecond() {
        return this.dateTime.getMillisecond();
    }

    @Override
    public ISO8601Time add(ISO8601Duration iso8601Duration) {
        return new ISO8601Time(this.dateTime.add(iso8601Duration));
    }

    @Override
    public ISO8601Time sub(ISO8601Duration iso8601Duration) {
        return new ISO8601Time(this.dateTime.sub(iso8601Duration));
    }

    /**
     * Gets the <code>String</code> ISO8601 time representation of this <code>ISO8601Time</code>.
     *
     * @param includeTimeZone <code>boolean</code> indicating whether the time zone information should be
     *            included
     * @return the <code>String</code> ISO8601 time representation of this <code>ISO8601Time</code>.
     */
    public String stringValue(boolean includeTimeZone) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%02d", this.getHour()));
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02d", this.getMinute()));
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02d", this.getSecond()));
        int ms = this.getMillisecond();
        if (ms > 0) {
            stringBuilder.append('.');
            stringBuilder.append(String.format("%03d", ms));
        }
        if (this.getHasTimeZone() && includeTimeZone) {
            stringBuilder.append(this.getTimeZone());
        }
        return stringBuilder.toString();
    }

    @Override
    public String stringValue() {
        return this.stringValue(true);
    }

    @Override
    public String toString() {
        return this.stringValue(true);
    }

    @Override
    public int hashCode() {
        return this.dateTime.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof ISO8601Time)) {
            return false;
        } else {
            return this.dateTime.equals(((ISO8601Time)obj).dateTime);
        }
    }

    @Override
    public int compareTo(ISO8601Time o) {
        return this.dateTime.compareTo(o.dateTime);
    }

    public static ISO8601Time fromCalendar(Calendar calendar) {
        return new ISO8601Time(calendar.getTimeZone(), calendar.get(Calendar.HOUR_OF_DAY),
                               calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                               calendar.get(Calendar.MILLISECOND));
    }

    public static ISO8601Time fromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return ISO8601Time.fromCalendar(cal);
    }

    /**
     * Creates a new <code>Time</code> object by parsing the <code>String</code> supplied which must conform
     * to the ISO8601 standard for time formats {@link http://www.w3.org/TR/NOTE-datetime}
     *
     * @param timeString the timeString to parse
     * @return a new <code>Time</code> representing the given <code>String</code>
     * @throws java.text.ParseException if the string cannot be interpreted as an ISO8601 time string
     */
    public static ISO8601Time fromISO8601TimeString(String timeString) throws ParseException {
        if (timeString == null) {
            throw new NullPointerException("Null time string");
        } else if (timeString.length() < 8) {
            throw new ParseException("Time string too short", 0);
        }

        /*
         * Find the starting position by searching past any whitespace
         */
        int startPos = ParseUtils.nextNonWhite(timeString, 0);

        /*
         * Get the two digit hour of day
         */
        int hh = ParseUtils.getTwoDigitValue(timeString, startPos);
        if (hh < 0 || hh >= 24) {
            throw new ParseException("Invalid hour of day", startPos);
        }
        startPos += 2;
        if (startPos >= timeString.length()) {
            throw new ParseException("Invalid time string", startPos);
        } else if (timeString.charAt(startPos) != ':') {
            throw new ParseException("Missing hour-minute separator", startPos);
        }
        startPos++;

        /*
         * Get the two-digit minute of hour
         */
        int mm = ParseUtils.getTwoDigitValue(timeString, startPos);
        if (mm < 0 || mm >= 60) {
            throw new ParseException("Invalid minute of hour", startPos);
        }
        startPos += 2;
        if (startPos >= timeString.length()) {
            throw new ParseException("Invalid time string", startPos);
        } else if (timeString.charAt(startPos) != ':') {
            throw new ParseException("Missing minute-second separator", startPos);
        }
        startPos++;

        /*
         * Get the two-digit second of minute
         */
        int ss = ParseUtils.getTwoDigitValue(timeString, startPos);
        if (ss < 0 || ss >= 60) {
            throw new ParseException("Invalid second of minute", startPos);
        }
        startPos += 2;

        /*
         * Now determine if we have a milliseconds portion
         */
        int ms = 0;
        if (startPos < timeString.length() && timeString.charAt(startPos) == '.') {
            startPos++;
            if ((ms = ParseUtils.getThreeDigitValue(timeString, startPos)) < 0 || ms >= 1000) {
                throw new ParseException("Invalid milliseconds", startPos);
            }
            startPos += 3;
        }

        /*
         * Now determine if there is a timezone
         */
        String timezone = null;

        if (startPos < timeString.length()) {
            switch (timeString.charAt(startPos)) {
            case 'Z':
                timezone = "GMT";
                startPos++;
                break;
            case '-':
            case '+':
                if (startPos + 5 < timeString.length()) {
                    timezone = "GMT" + timeString.substring(startPos, startPos + 6);
                } else {
                    throw new ParseException("Invalid timezone", startPos);
                }
                break;
            default:
                throw new ParseException("Invalid timezone", startPos);
            }
        }
        return new ISO8601Time(timezone, hh, mm, ss, ms);
    }
}
