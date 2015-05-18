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

/**
 * XPathYearMonthDuration extends {@link ISO8601Duration} to implement the XPath yearMonthDuration data type.
 */
public class XPathYearMonthDuration extends ISO8601Duration implements Comparable<XPathYearMonthDuration> {
    private int monthsDuration;

    public XPathYearMonthDuration(int durationSignIn, int yearsIn, int monthsIn) {
        super(durationSignIn, yearsIn, monthsIn, 0, 0, 0, 0);
        this.monthsDuration = this.getDurationSign() * (this.getYears() * 12 + this.getMonths());
    }

    /**
     * Computes the duration as a number of months.
     *
     * @return the duration in months
     */
    public int getMonthsDuration() {
        return this.monthsDuration;
    }

    /**
     * Gets a canonical <code>XPathYearMonthDuration</code> from this <code>XPathYearMonthDuration</code> by
     * ensuring the number of months never exceeds 11, converting excess months to additional years.
     *
     * @return a new <code>XPathYearMonthDuration</code> in canonical format
     */
    public XPathYearMonthDuration getCanonical() {
        int monthsLeft = Math.abs(this.getMonthsDuration());
        int years = monthsLeft / 12;
        monthsLeft -= years * 12;
        return new XPathYearMonthDuration(this.getDurationSign(), years, monthsLeft);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof XPathYearMonthDuration)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            return this.getMonthsDuration() == ((XPathYearMonthDuration)obj).getMonthsDuration();
        }
    }

    public static XPathYearMonthDuration newInstance(ISO8601Duration iso8601Duration) throws ParseException {
        if (iso8601Duration == null) {
            return null;
        }
        if (iso8601Duration.getDays() > 0 || iso8601Duration.getHours() > 0
            || iso8601Duration.getMinutes() > 0 || iso8601Duration.getFractionalSecs() > 0) {
            throw new ParseException("Invalid XPath yearMonthDuraiton \"" + iso8601Duration.toString()
                                     + "\": includes days, hours, minutes, or seconds", 0);
        }
        return new XPathYearMonthDuration(iso8601Duration.getDurationSign(), iso8601Duration.getYears(),
                                          iso8601Duration.getMonths());
    }

    public static XPathYearMonthDuration newInstance(String iso8601DurationString) throws ParseException {
        return newInstance(ISO8601Duration.newInstance(iso8601DurationString));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{super=");
        stringBuilder.append(super.toString());
        stringBuilder.append(",monthsDuration=");
        stringBuilder.append(this.getMonthsDuration());
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public int compareTo(XPathYearMonthDuration o) {
        if (o == null) {
            return 1;
        }
        return Integer.compare(this.getMonthsDuration(), o.getMonthsDuration());
    }

}
