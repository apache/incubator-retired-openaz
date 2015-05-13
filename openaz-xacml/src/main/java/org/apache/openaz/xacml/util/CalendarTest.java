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

package org.apache.openaz.xacml.util;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarTest {

    public CalendarTest() {
    }

    private static class CField {
        private String fieldName;
        private int calId;

        public CField(String fieldNameIn, int calIdIn) {
            this.fieldName = fieldNameIn;
            this.calId = calIdIn;
        }

        public String getFieldName() {
            return this.fieldName;
        }

        public int getCalId() {
            return this.calId;
        }
    }

    private static CField[] calFields = {
                                           new CField("Era", Calendar.ERA), new CField("TimeZone", Calendar.ZONE_OFFSET),
                                           new CField("Year", Calendar.YEAR), new CField("Month", Calendar.MONTH),
                                           new CField("Day", Calendar.DATE), new CField("Hour", Calendar.HOUR_OF_DAY),
                                           new CField("Minute", Calendar.MINUTE), new CField("Second", Calendar.SECOND),
                                           new CField("Millisecond", Calendar.MILLISECOND)
    };

    private static void dumpCalendar(Calendar calendar) {
        System.out.println("Current timestamp=" + calendar.getTimeInMillis());
        System.out.println("Current Date=" + calendar.getTime());
        System.out.println("Current TimeZone=" + calendar.getTimeZone());
        System.out.print("Fields=");
        boolean needsComma = false;
        for (CField cfield : calFields) {
            if (needsComma) {
                System.out.print(",");
            }
            System.out.print(cfield.getFieldName() + "=" + calendar.get(cfield.getCalId()));
            needsComma = true;
        }
        System.out.println();

    }

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);

        System.out.println("Current Time");
        dumpCalendar(calendar);

        /*
         * Change the timezone to GMT
         */
        try {
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            System.out.println("GMT Time");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception using timezone GMT: " + ex);
        }

        /*
         * Change the timezone to GMT-06:00
         */
        try {
            calendar.setTimeZone(TimeZone.getTimeZone("GMT-06:00"));
            System.out.println("GMT Time-06:00");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception using timezone GMT: " + ex);
        }

        /*
         * Change the timezone to GMT-06:10
         */
        try {
            calendar.setTimeZone(TimeZone.getTimeZone("GMT-06:10"));
            System.out.println("GMT Time-06:10");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception using timezone GMT: " + ex);
        }

        /*
         * Try setting the year to 10012
         */
        try {
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.set(Calendar.YEAR, 10012);
            System.out.println("GMT Time in 10012");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception setting year to 10012 " + ex);
        }

        /*
         * Try setting the year to 1812
         */
        try {
            calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.set(Calendar.YEAR, 1812);
            System.out.println("GMT Time in 1812");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception setting year to 1812 " + ex);
        }

        /*
         * Try adding 60 days
         */
        try {
            calendar.add(Calendar.DAY_OF_YEAR, 60);
            System.out.println("GMT Time in 1812 + 60 days");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception adding 60 days " + ex);
        }

        /*
         * Try subtracting 900 days
         */
        try {
            calendar.add(Calendar.DAY_OF_YEAR, -900);
            System.out.println("GMT Time in 1812 -900 days");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception subtracting 900 days " + ex);
        }

        /*
         * Try adding 11 months
         */
        try {
            calendar.add(Calendar.MONTH, 11);
            System.out.println("GMT Time in 1812 + 11 months");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception adding 11 months " + ex);
        }

        /*
         * Try setting month/day to November 31 in 1812
         */
        try {
            calendar.set(Calendar.MONTH, 10);
            calendar.set(Calendar.DATE, 31);
            System.out.println("GMT Time for Nov 31, 1812");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception setting year to 1812 " + ex);
        }

        /*
         * Try setting the time-in-millis to a negative number
         */
        try {
            calendar.setTimeInMillis(-8888888888L);
            System.out.println("GMT Time for -8888888888");
            dumpCalendar(calendar);
        } catch (Exception ex) {
            System.err.println("Exception setting time-in-millis to -8888888888 " + ex);
        }
    }

}
