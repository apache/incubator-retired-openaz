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

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.std.StdAttributeValue;

/**
 * DataTypeBoolean extends {@link DataTypeBase} with conversions to the java <code>Boolean</code> type.
 */
public class DataTypeBoolean extends DataTypeBase<Boolean> {
    private static final DataTypeBoolean singleInstance = new DataTypeBoolean();

    public static final AttributeValue<Boolean> AV_TRUE = new StdAttributeValue<Boolean>(
                                                                                         XACML.ID_DATATYPE_BOOLEAN,
                                                                                         Boolean.TRUE);
    public static final AttributeValue<Boolean> AV_FALSE = new StdAttributeValue<Boolean>(
                                                                                          XACML.ID_DATATYPE_BOOLEAN,
                                                                                          Boolean.FALSE);

    private DataTypeBoolean() {
        super(XACML.ID_DATATYPE_BOOLEAN, Boolean.class);
    }

    public static DataTypeBoolean newInstance() {
        return singleInstance;
    }

    @Override
    public Boolean convert(Object source) throws DataTypeException {
        if (source == null || source instanceof Boolean) {
            return (Boolean)source;
        } else if (source instanceof Integer) {
            int iValue = ((Integer)source).intValue();
            if (iValue == 0) {
                return Boolean.FALSE;
            } else if (iValue == 1) {
                return Boolean.TRUE;
            } else {
                throw new DataTypeException(this, "Cannot convert from integer " + iValue + " to boolean");
            }
        } else {
            String stringValue = this.convertToString(source);
            if (stringValue == null) {
                return null;
            } else if (stringValue.equals("0") || stringValue.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            } else if (stringValue.equals("1") || stringValue.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else {
                throw new DataTypeException(this, "Cannot convert from \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to boolean");
            }
        }
    }
}
