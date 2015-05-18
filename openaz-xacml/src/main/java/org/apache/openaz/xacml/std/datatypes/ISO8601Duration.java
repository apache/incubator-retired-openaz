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

import org.apache.openaz.xacml.api.SemanticString;

/**
 * ISO8601Duration implements the ISO8601 duration data type with parsers from strings and to strings.
 */
public class ISO8601Duration implements SemanticString {
    private static int getChunkOrder(boolean sawT, char chunkName) {
        switch (chunkName) {
        case 'Y':
            return 1;
        case 'M':
            return (!sawT ? 2 : 5);
        case 'D':
            return 3;
        case 'H':
            return 4;
        case 'S':
            return 6;
        default:
            return -1;
        }
    }

    private static class DurationChunk {
        private char chunkName;
        private double chunkValue;
        private int length;

        public DurationChunk(char name, double value, int lengthIn) {
            this.chunkName = name;
            this.chunkValue = value;
            this.length = lengthIn;
        }

        public char getChunkName() {
            return this.chunkName;
        }

        public double getChunkValue() {
            return this.chunkValue;
        }

        public int getLength() {
            return this.length;
        }

        public boolean isIntValue() {
            return Math.floor(this.getChunkValue()) == this.getChunkValue();
        }

        public int getIntValue() {
            return (int)Math.floor(this.getChunkValue());
        }

        public static DurationChunk nextChunk(String duration, int startPos) throws ParseException {
            if (duration == null || startPos >= duration.length()) {
                return null;
            }
            int curPos = startPos;
            int endPos = duration.length();
            int dotCount = 0;
            char charAt;
            while (curPos < endPos
                   && ((charAt = duration.charAt(curPos)) == '.' || Character.isDigit(charAt))) {
                if (charAt == '.') {
                    dotCount++;
                }
                curPos++;
            }
            if (curPos < endPos && dotCount <= 1) {
                char chunkName = duration.charAt(curPos);
                Double dvalue = null;
                try {
                    dvalue = Double.parseDouble(duration.substring(startPos, curPos));
                } catch (NumberFormatException ex) {
                    throw new ParseException("Invalid chunk \"" + duration + "\" at position " + startPos,
                                             startPos);
                }
                curPos++;
                return new DurationChunk(chunkName, dvalue, (curPos - startPos));
            } else {
                throw new ParseException("Invalid chunk \"" + duration + "\" at position " + startPos, curPos);
            }
        }
    }

    private int durationSign = 1;
    private int years;
    private int months;
    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private int millis;

    /**
     * Creates a duration with the given values.
     *
     * @param durationSignIn the sign of the duration
     * @param yearsIn the number of years
     * @param monthsIn the number of months
     * @param daysIn the number of days
     * @param hoursIn the number of hours
     * @param minutesIn the number of minutes
     * @param secondsIn the number of fractional seconds
     */
    public ISO8601Duration(int durationSignIn, int yearsIn, int monthsIn, int daysIn, int hoursIn,
                           int minutesIn, double secondsIn) {
        this.durationSign = durationSignIn;
        this.years = yearsIn;
        this.months = monthsIn;
        this.days = daysIn;
        this.hours = hoursIn;
        this.minutes = minutesIn;
        this.seconds = (int)Math.floor(secondsIn);
        this.millis = (int)Math.floor(secondsIn * 1000 - this.seconds * 1000);
    }

    /**
     * Creates a new <code>ISO8601Duration</code> by parsing the given <code>String</code>.
     *
     * @param iso8601DurationString the ISO8601 duration <code>String</code>
     * @return a new <code>ISO8601Duration</code> parsed from the given <code>String</code>
     */
    public static ISO8601Duration newInstance(String iso8601DurationString) throws ParseException {
        if (iso8601DurationString == null || iso8601DurationString.length() == 0) {
            return null;
        }
        int curPos = 0;
        int endPos = iso8601DurationString.length();

        int durationSign = 1;
        int years = 0;
        int months = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        double fractionalSeconds = 0.0;

        if (iso8601DurationString.charAt(curPos) == '-') {
            durationSign = -1;
            curPos++;
        }
        if (iso8601DurationString.charAt(curPos) != 'P') {
            throw new ParseException("Invalid ISO8601 duration string \"" + iso8601DurationString
                                     + "\" at position " + curPos, curPos);
        }
        curPos++;

        if (curPos >= endPos) {
            throw new ParseException("Invalid ISO8601 duration string \"" + iso8601DurationString
                                     + "\": No duration components following P", curPos);
        }

        int lastChunkOrder = 0;
        boolean sawT = false;
        while (curPos < endPos) {
            /*
             * Look for the Time divider character
             */
            if (iso8601DurationString.charAt(curPos) == 'T') {
                if (sawT) {
                    throw new ParseException("Invalid ISO8601 duration string \"" + iso8601DurationString
                                             + "\" at position " + curPos + ": saw multiple T separators",
                                             curPos);
                } else {
                    sawT = true;
                }
                curPos++;
            } else {
                DurationChunk durationChunk = DurationChunk.nextChunk(iso8601DurationString, curPos);

                /*
                 * Check for unknown chunks or out of order chunks
                 */
                int chunkOrder = getChunkOrder(sawT, durationChunk.getChunkName());
                if (chunkOrder <= 0) {
                    throw new ParseException("Invalid ISO8601 duration string \"" + iso8601DurationString
                                             + "\" at position " + curPos + ": invalid component", curPos);
                } else if (chunkOrder <= lastChunkOrder) {
                    throw new ParseException("Invalid ISO8601 duration string \"" + iso8601DurationString
                                             + "\" at position " + curPos + ": out of order component",
                                             curPos);
                }
                lastChunkOrder = chunkOrder;

                /*
                 * Check for correct value type
                 */
                if (durationChunk.getChunkName() != 'S' && !durationChunk.isIntValue()) {
                    throw new ParseException("Invalid ISO8601 duration string \"" + iso8601DurationString
                                             + "\" at position " + curPos + ": expected int value", curPos);
                }

                /*
                 * Assign the value to the right component
                 */
                switch (durationChunk.getChunkName()) {
                case 'Y':
                    years = durationChunk.getIntValue();
                    break;
                case 'M':
                    if (!sawT) {
                        months = durationChunk.getIntValue();
                    } else {
                        minutes = durationChunk.getIntValue();
                    }
                    break;
                case 'D':
                    days = durationChunk.getIntValue();
                    break;
                case 'H':
                    hours = durationChunk.getIntValue();
                    break;
                case 'S':
                    fractionalSeconds = durationChunk.getChunkValue();
                    break;
                default:
                    assert false;
                    break;
                }

                /*
                 * Advance the current position
                 */
                curPos += durationChunk.getLength();
            }
        }

        return new ISO8601Duration(durationSign, years, months, days, hours, minutes, fractionalSeconds);
    }

    public int getDurationSign() {
        return this.durationSign;
    }

    public int getYears() {
        return this.years;
    }

    public int getMonths() {
        return this.months;
    }

    public int getDays() {
        return this.days;
    }

    public int getHours() {
        return this.hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return this.seconds;
    }

    public int getMillis() {
        return this.millis;
    }

    public double getFractionalSecs() {
        double dSec = this.seconds + (double)this.millis / 1000;
        return dSec;
    }

    @Override
    public int hashCode() {
        return this.getDurationSign() + this.getYears() + this.getMonths() + this.getDays() + this.getHours()
               + this.getMinutes() + this.getSeconds() + this.getMillis();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ISO8601Duration)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            ISO8601Duration iso8601Duration = (ISO8601Duration)obj;
            return this.getDurationSign() == iso8601Duration.getDurationSign()
                    && this.getYears() == iso8601Duration.getYears()
                    && this.getMonths() == iso8601Duration.getMonths()
                    && this.getDays() == iso8601Duration.getDays()
                    && this.getHours() == iso8601Duration.getHours()
                    && this.getMinutes() == iso8601Duration.getMinutes()
                    && this.getSeconds() == iso8601Duration.getSeconds() 
                    && this.getMillis() == iso8601Duration.getMillis();

        }
    }

    @Override
    public String stringValue() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.getDurationSign() < 0) {
            stringBuilder.append("-P");
        } else {
            stringBuilder.append("P");
        }
        boolean sawOne = false;
        if (this.getYears() > 0) {
            stringBuilder.append(this.getYears());
            stringBuilder.append('Y');
            sawOne = true;
        }
        if (this.getMonths() > 0) {
            stringBuilder.append(this.getMonths());
            stringBuilder.append('M');
            sawOne = true;
        }
        if (this.getDays() > 0) {
            stringBuilder.append(this.getDays());
            stringBuilder.append('D');
            sawOne = true;
        }
        if (this.getHours() > 0 || this.getMinutes() > 0 || this.getSeconds() > 0 || this.getMillis() > 0) {
            stringBuilder.append('T');
            if (this.getHours() > 0) {
                stringBuilder.append(this.getHours());
                stringBuilder.append('H');
            }
            if (this.getMinutes() > 0) {
                stringBuilder.append(this.getMinutes());
                stringBuilder.append('M');
            }
            double dSeconds = this.getFractionalSecs();
            if (dSeconds > 0) {
                if (dSeconds == Math.floor(dSeconds)) {
                    stringBuilder.append((int)dSeconds);
                } else {
                    stringBuilder.append(dSeconds);
                }
                stringBuilder.append('S');
            }
        } else if (!sawOne) {
            stringBuilder.append("T0S");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "{" + "durationSign=" + this.getDurationSign() + "years=" + this.getYears() + "months="
               + this.getMonths() + "days=" + this.getDays() + "hours=" + this.getHours() + "minutes="
               + this.getMinutes() + "seconds=" + this.getSeconds() + "millis=" + this.getMillis() + "}";
    }
}
