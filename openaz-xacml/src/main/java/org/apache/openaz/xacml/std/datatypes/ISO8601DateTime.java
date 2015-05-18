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
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.openaz.xacml.api.SemanticString;

/**
 * ISO8601Datetime is a combination of an {@link ISO8601Date} and a {@link ISO8601Time} with a common
 * {@link org.apache.openaz.xacml.std.datatypes.ISO8601TimeZone}. Note: This is a temporary implementation. It
 * appears Java 8 will have better classes for dealing with ISO8601 dates and times.
 */
public class ISO8601DateTime implements IDateTime<ISO8601DateTime>, Comparable<ISO8601DateTime>,
    SemanticString {
    private static final long TZOFFSET_14_HOURS_MILLIS = 14 * 60 * 60 * 1000;
    private boolean hasTimeZone;
    private Calendar calendar;

    private void validateDate() {
        this.calendar.getTime();
        int year = this.calendar.get(Calendar.YEAR);
        if (this.calendar.isSet(Calendar.ERA) && this.calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
            year = 0 - year + 1;
        }

    }

    /**
     * Creates a new <code>ISO8601DateTime</code> using the supplied field values.
     *
     * @param timeZone the <code>String</code> name of the time zone. Use null for non-time-zoned instances
     * @param yearIn the integer year (-9999 to 9999)
     * @param monthIn the integer month (1-12)
     * @param dayIn the integer day of the month (1-31)
     * @param hourIn the integer hour of the day (0-23)
     * @param minuteIn the integer minute of the hour (0-59)
     * @param secondIn the integer second of the minute (0-59)
     * @param millisecondIn the integer milliseconds (0-999)
     */
    public ISO8601DateTime(TimeZone timeZone, int yearIn, int monthIn, int dayIn, int hourIn, int minuteIn,
                           int secondIn, int millisecondIn) {
        this.calendar = Calendar.getInstance();
        this.calendar.setLenient(false);
        if (timeZone != null) {
            this.hasTimeZone = true;
            this.calendar.setTimeZone(timeZone);
        }
        if (yearIn <= 0) {
            this.calendar.set(Calendar.ERA, GregorianCalendar.BC);
            this.calendar.set(Calendar.YEAR, Math.abs(yearIn) + 1);
        } else {
            this.calendar.set(Calendar.YEAR, yearIn);
        }
        this.calendar.set(Calendar.MONTH, monthIn - 1);
        this.calendar.set(Calendar.DAY_OF_MONTH, dayIn);
        this.calendar.set(Calendar.HOUR_OF_DAY, hourIn);
        this.calendar.set(Calendar.MINUTE, minuteIn);
        this.calendar.set(Calendar.SECOND, secondIn);
        this.calendar.set(Calendar.MILLISECOND, millisecondIn);

        /*
         * Now do the tests to throw illegal argument exceptions if the request was invalid
         */
        this.validateDate();
    }

    public ISO8601DateTime(String timeZone, int yearIn, int monthIn, int dayIn, int hourIn, int minuteIn,
                           int secondIn, int millisecondIn) {
        this((timeZone == null ? null : TimeZone.getTimeZone(timeZone)), yearIn, monthIn, dayIn, hourIn,
             minuteIn, secondIn, millisecondIn);
    }

    public ISO8601DateTime(ISO8601TimeZone timeZone, ISO8601Date iso8601Date, ISO8601Time iso8601Time) {
        this((timeZone == null ? null : timeZone.getTimeZoneString()), iso8601Date.getYear(), iso8601Date
            .getMonth(), iso8601Date.getDay(), iso8601Time.getHour(), iso8601Time.getMinute(), iso8601Time
            .getSecond(), iso8601Time.getMillisecond());
    }

    public ISO8601DateTime(int yearIn, int monthIn, int dayIn, int hourIn, int minuteIn, int secondIn,
                           int millisecondIn) {
        this((TimeZone)null, yearIn, monthIn, dayIn, hourIn, minuteIn, secondIn, millisecondIn);
    }

    /**
     * Creates a new <code>ISO8601DateTime</code> using the given <code>Calendar</code>. If
     * <code>hasTimeZoneIn</code> is true, then the time zone of the given <code>Calendar</code> is used,
     * otherwise this is a non-time-zoned <code>ISO8601DateTime</code>
     *
     * @param calendarIn the <code>Calendar</code> for the new <code>ISO8601DateTime</code>
     * @param hasTimeZoneIn if true, create a time-zoned <code>ISO8601DateTime</code> else create a
     *            non-time-zoned <code>ISO8601DateTime</code>
     */
    private ISO8601DateTime(Calendar calendarIn, boolean hasTimeZoneIn) {
        /*
         * this.calendar = Calendar.getInstance(); this.calendar.setLenient(false);
         * this.calendar.setTimeZone(calendarIn.getTimeZone());
         * this.calendar.setTimeInMillis(calendarIn.getTimeInMillis());
         */
        this.calendar = calendarIn;
        this.hasTimeZone = hasTimeZoneIn;
    }

    /**
     * Gets the value indicating whether this <code>ISO8601DateTime</code> is time-zoned or non-time-zoned.
     *
     * @return true if this <code>ISO8601DateTime</code> is time-zoned, else false
     */
    public boolean getHasTimeZone() {
        return this.hasTimeZone;
    }

    public String getTimeZone() {
        if (this.getHasTimeZone()) {
            int tzOffsetMillis = this.calendar.get(Calendar.ZONE_OFFSET);
            if (tzOffsetMillis == 0) {
                return "Z";
            } else {
                int tzOffsetHours = Math.abs(tzOffsetMillis) / (60 * 60 * 1000);
                int tzOffsetMinutes = (Math.abs(tzOffsetMillis) - (tzOffsetHours * 60 * 60 * 1000))
                                      / (60 * 1000);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append((tzOffsetMillis < 0 ? '-' : '+'));
                stringBuilder.append(String.format("%02d", tzOffsetHours));
                stringBuilder.append(':');
                stringBuilder.append(String.format("%02d", tzOffsetMinutes));
                return stringBuilder.toString();
            }

        } else {
            return null;
        }
    }

    public int getYear() {
        int year = this.calendar.get(Calendar.YEAR);
        if (this.calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
            return -(year - 1);
        } else {
            return year;
        }
    }

    public int getMonth() {
        return this.calendar.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return this.calendar.get(Calendar.MINUTE);
    }

    public int getSecond() {
        return this.calendar.get(Calendar.SECOND);
    }

    public int getMillisecond() {
        return this.calendar.get(Calendar.MILLISECOND);
    }

    /**
     * Gets a copy of the <code>Calendar</code> backing this <code>ISO8601DateTime</code>.
     *
     * @return a copy of the <code>Calendar</code> backing this <code>ISO8601DateTime</code>.
     */
    public Calendar getCalendar() {
        Calendar calendarCopy = Calendar.getInstance();
        calendarCopy.setLenient(false);
        if (this.getHasTimeZone()) {
            calendarCopy.setTimeZone(this.calendar.getTimeZone());
        }
        calendarCopy.setTimeInMillis(this.calendar.getTimeInMillis());
        return calendarCopy;
    }

    /**
     * Gets a <code>ISO8601DateTime</code> equivalent to this <code>ISO8601DateTime</code> converted to the
     * GMT time zone. If this <code>ISO8601DateTime</code> is non-time-zoned, this method will throw an
     * illegal state exception.
     *
     * @return a <code>ISO8601DateTime</code> equivalent to this <code>ISO8601DateTime</code> in the GMT time
     *         zone.
     * @throws IllegalStateException if this <code>ISO8601DateTime</code> is non-time-zoned.
     */
    public ISO8601DateTime getGMTDateTime() {
        if (!this.getHasTimeZone()) {
            throw new IllegalStateException("Cannot convert non-time-zoned ISO8601DateTime to GMT");
        }
        if (this.calendar.get(Calendar.ZONE_OFFSET) == 0) {
            return this;
        } else {
            Calendar calendarGMT = Calendar.getInstance();
            calendarGMT.setLenient(false);
            calendarGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendarGMT.setTimeInMillis(this.calendar.getTimeInMillis());
            return new ISO8601DateTime(calendarGMT, true);
        }
    }

    private ISO8601DateTime add(ISO8601Duration iso8601Duration, int sign) {
        Calendar calendarCopy = this.getCalendar();

        int value;
        if ((value = iso8601Duration.getYears()) != 0) {
            calendarCopy.add(Calendar.YEAR, sign * value);
        }
        if ((value = iso8601Duration.getMonths()) != 0) {
            calendarCopy.add(Calendar.MONTH, sign * value);
        }
        if ((value = iso8601Duration.getDays()) != 0) {
            calendarCopy.add(Calendar.DAY_OF_YEAR, sign * value);
        }
        if ((value = iso8601Duration.getHours()) != 0) {
            calendarCopy.add(Calendar.HOUR_OF_DAY, sign * value);
        }
        if ((value = iso8601Duration.getMinutes()) != 0) {
            calendarCopy.add(Calendar.MINUTE, sign * value);
        }
        int seconds = iso8601Duration.getSeconds();
        if (seconds >= 1) {
            calendarCopy.add(Calendar.SECOND, sign * seconds);
        }
        int millis = iso8601Duration.getMillis();
        if (millis != 0) {
            calendarCopy.add(Calendar.MILLISECOND, sign * millis);
        }

        return new ISO8601DateTime(calendarCopy, this.getHasTimeZone());
    }

    /**
     * Adds the given <code>ISO8601Duration</code> to this <code>ISO8601DateTime</code> and returns a new
     * <code>ISO8601DateTime</code> with the result.
     *
     * @param iso8601Duration the <code>ISO8601Duration</code> to add
     * @return a new <code>ISO8601DateTime</code> with the result of the addition
     */
    @Override
    public ISO8601DateTime add(ISO8601Duration iso8601Duration) {
        return this.add(iso8601Duration, iso8601Duration.getDurationSign());
    }

    @Override
    public ISO8601DateTime sub(ISO8601Duration iso8601Duration) {
        return this.add(iso8601Duration, -iso8601Duration.getDurationSign());
    }

    @Override
    public int hashCode() {
        return (this.getHasTimeZone() ? 999 : 0) + this.calendar.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof ISO8601DateTime)) {
            return false;
        } else {
            ISO8601DateTime iso8601DateTime = (ISO8601DateTime)obj;
            return this.getHasTimeZone() == iso8601DateTime.getHasTimeZone()
                   && this.calendar.equals(iso8601DateTime.calendar);
        }
    }

    public static ISO8601DateTime fromCalendar(Calendar calendar) {
        Calendar calendarNew = Calendar.getInstance();
        calendarNew.setTime(calendar.getTime());
        calendarNew.setLenient(false);
        return new ISO8601DateTime(calendarNew, true);
    }

    public static ISO8601DateTime fromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setLenient(false);
        return new ISO8601DateTime(calendar, true);
    }

    /**
     * Creates a new <code>ISO8601DateTime</code> by parsing the given <code>String</code> in the extended
     * ISO8601 format defined for XML.
     *
     * @param strDateTime the <code>String</code> in ISO8601 date-time format.
     * @return a new <code>ISO8601DateTime</code>.
     * @throws java.text.ParseException if there is an error parsing the <code>String</code>
     */
    public static ISO8601DateTime fromISO8601DateTimeString(String strDateTime) throws ParseException {
        /*
         * Trim whitespace
         */
        int startPos = ParseUtils.nextNonWhite(strDateTime, 0);

        ParseUtils.ParseValue<Integer> parseValue = ParseUtils.getSignedValue(strDateTime, startPos);
        if (parseValue == null) {
            throw new ParseException("Invalid year", startPos);
        }
        int year = parseValue.getValue();
        if (parseValue.getNextPos() < 4) {
            throw new ParseException("Invalid year (must be at least 4 digits)", startPos);
        }
        startPos += parseValue.getNextPos();
        if (startPos >= strDateTime.length() || strDateTime.charAt(startPos) != '-') {
            throw new ParseException("Invalid date", startPos);
        }
        startPos++;

        int month = ParseUtils.getTwoDigitValue(strDateTime, startPos);
        if (month <= 0 || month > 12) {
            throw new ParseException("Invalid month", startPos);
        }
        startPos += 2;
        if (startPos >= strDateTime.length() || strDateTime.charAt(startPos) != '-') {
            throw new ParseException("Invalid date", startPos);
        }
        startPos++;

        int day = ParseUtils.getTwoDigitValue(strDateTime, startPos);
        if (day < 1 || day > 31) {
            throw new ParseException("Invalid day", startPos);
        }
        startPos += 2;

        /*
         * There should now be a 'T'
         */
        if (startPos >= strDateTime.length() || strDateTime.charAt(startPos) != 'T') {
            throw new ParseException("Missing time separator 'T'", startPos);
        }
        startPos++;

        /*
         * Get the hours
         */
        int hours = ParseUtils.getTwoDigitValue(strDateTime, startPos);
        if (hours < 0 || hours > 23) {
            throw new ParseException("Invalid hours", startPos);
        }
        startPos += 2;
        if (startPos >= strDateTime.length() || strDateTime.charAt(startPos) != ':') {
            throw new ParseException("Invalid date", startPos);
        }
        startPos++;

        /*
         * Get the minutes
         */
        int minutes = ParseUtils.getTwoDigitValue(strDateTime, startPos);
        if (minutes < 0 || minutes > 59) {
            throw new ParseException("Invalid minutes", startPos);
        }
        startPos += 2;
        if (startPos >= strDateTime.length() || strDateTime.charAt(startPos) != ':') {
            throw new ParseException("Invalid date", startPos);
        }
        startPos++;

        /*
         * Get the two-digit second of minute
         */
        int ss = ParseUtils.getTwoDigitValue(strDateTime, startPos);
        if (ss < 0 || ss >= 60) {
            throw new ParseException("Invalid second of minute", startPos);
        }
        startPos += 2;

        /*
         * Now determine if we have a milliseconds portion
         */
        int ms = 0;
        if (startPos < strDateTime.length() && strDateTime.charAt(startPos) == '.') {
            startPos++;
            if ((ms = ParseUtils.getThreeDigitValue(strDateTime, startPos)) < 0 || ms >= 1000) {
                throw new ParseException("Invalid milliseconds", startPos);
            }
            startPos += 3;
        }

        /*
         * Now determine if there is a timezone
         */
        String timezone = null;

        if (startPos < strDateTime.length()) {
            switch (strDateTime.charAt(startPos)) {
            case 'Z':
                timezone = "GMT";
                startPos++;
                break;
            case '-':
            case '+':
                if (startPos + 5 < strDateTime.length()) {
                    timezone = "GMT" + strDateTime.substring(startPos, startPos + 6);
                } else {
                    throw new ParseException("Invalid timezone", startPos);
                }
                break;
            default:
                throw new ParseException("Invalid timezone", startPos);
            }
        }

        return new ISO8601DateTime(timezone, year, month, day, hours, minutes, ss, ms);
    }

    @Override
    public String stringValue() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
            int yearBC = this.calendar.get(Calendar.YEAR) - 1;
            stringBuilder.append('-');
            stringBuilder.append(String.format("%d", yearBC));
        } else {
            stringBuilder.append(String.format("%d", this.calendar.get(Calendar.YEAR)));
        }
        stringBuilder.append('-');
        stringBuilder.append(String.format("%02d", this.calendar.get(Calendar.MONTH) + 1));
        stringBuilder.append('-');
        stringBuilder.append(String.format("%02d", this.calendar.get(Calendar.DAY_OF_MONTH)));
        stringBuilder.append('T');
        stringBuilder.append(String.format("%02d", this.calendar.get(Calendar.HOUR_OF_DAY)));
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02d", this.calendar.get(Calendar.MINUTE)));
        stringBuilder.append(':');
        stringBuilder.append(String.format("%02d", this.calendar.get(Calendar.SECOND)));
        int ms = this.calendar.get(Calendar.MILLISECOND);
        if (ms != 0) {
            stringBuilder.append('.');
            stringBuilder.append(String.format("%03d", ms));
        }
        if (this.getHasTimeZone()) {
            stringBuilder.append(this.getTimeZone());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return this.stringValue();
    }

    private static int compareCalendars(Calendar cal1, Calendar cal2) {
        long cal1Time = cal1.getTimeInMillis();
        long cal2Time = cal2.getTimeInMillis();
        if (cal1Time < cal2Time) {
            return -1;
        } else if (cal1Time > cal2Time) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(ISO8601DateTime o) {
        if (this.getHasTimeZone()) {
            if (o.getHasTimeZone()) {
                return compareCalendars(this.calendar, o.calendar);
            } else {
                long thisMilliseconds = this.calendar.getTimeInMillis();
                long oMilliseconds = o.calendar.getTimeInMillis();
                if (thisMilliseconds < (oMilliseconds - TZOFFSET_14_HOURS_MILLIS)) {
                    return -1;
                } else if (thisMilliseconds > (oMilliseconds + TZOFFSET_14_HOURS_MILLIS)) {
                    return 1;
                } else {
                    throw new IllegalArgumentException(
                                                       "Cannot compare this ISO8601DateTime with non-time-zoned ISO8601DateTime");
                }
            }
        } else {
            if (o.getHasTimeZone()) {
                long thisMilliseconds = this.calendar.getTimeInMillis();
                long oMilliseconds = o.calendar.getTimeInMillis();
                if ((thisMilliseconds + TZOFFSET_14_HOURS_MILLIS) < oMilliseconds) {
                    return -1;
                } else if ((thisMilliseconds - TZOFFSET_14_HOURS_MILLIS) > oMilliseconds) {
                    return 1;
                } else {
                    throw new IllegalArgumentException(
                                                       "Cannot compare this ISO8601DateTime with time-zoned ISO8601DateTime");
                }
            } else {
                /*
                 * Neither has a timezone, so we can just compare the time in milliseconds
                 */
                return compareCalendars(this.calendar, o.calendar);
            }
        }
    }

}
