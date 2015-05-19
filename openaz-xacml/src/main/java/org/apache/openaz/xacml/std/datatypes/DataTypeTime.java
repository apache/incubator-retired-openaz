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

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML;

/**
 * DataTypeTime extends {@link DataTypeBase} to implement the XACML Time data time mapping to a
 * {@link ISO8601Time} java object.
 */
public class DataTypeTime extends DataTypeSemanticStringBase<ISO8601Time> {
    private static final DataTypeTime singleInstance = new DataTypeTime();

    private DataTypeTime() {
        super(XACML.ID_DATATYPE_TIME, ISO8601Time.class);
    }

    public static DataTypeTime newInstance() {
        return singleInstance;
    }

    @Override
    public ISO8601Time convert(Object source) throws DataTypeException {
        if (source == null || source instanceof ISO8601Time) {
            return (ISO8601Time)source;
        } else if (source instanceof Date) {
            return ISO8601Time.fromDate((Date)source);
        } else if (source instanceof Calendar) {
            return ISO8601Time.fromCalendar((Calendar)source);
        } else {
            String stringValue = this.convertToString(source);
            ISO8601Time timeValue = null;
            try {
                timeValue = ISO8601Time.fromISO8601TimeString(stringValue);
            } catch (ParseException ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to Time", ex);
            }
            return timeValue;
        }
    }
}
