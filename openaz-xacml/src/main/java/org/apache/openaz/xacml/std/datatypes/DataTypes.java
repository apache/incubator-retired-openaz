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

/**
 * DataTypes provides constant instances of the built-in {@link org.apache.openaz.xacml.api.DataType}
 * implementation classes.
 */
public class DataTypes {
    private DataTypes() {
    }

    public static final DataTypeString DT_STRING = DataTypeString.newInstance();
    public static final DataTypeBoolean DT_BOOLEAN = DataTypeBoolean.newInstance();
    public static final DataTypeInteger DT_INTEGER = DataTypeInteger.newInstance();
    public static final DataTypeDouble DT_DOUBLE = DataTypeDouble.newInstance();
    public static final DataTypeTime DT_TIME = DataTypeTime.newInstance();
    public static final DataTypeDate DT_DATE = DataTypeDate.newInstance();
    public static final DataTypeDateTime DT_DATETIME = DataTypeDateTime.newInstance();
    public static final DataTypeDayTimeDuration DT_DAYTIMEDURATION = DataTypeDayTimeDuration.newInstance();
    public static final DataTypeYearMonthDuration DT_YEARMONTHDURATION = DataTypeYearMonthDuration
        .newInstance();
    public static final DataTypeAnyURI DT_ANYURI = DataTypeAnyURI.newInstance();
    public static final DataTypeHexBinary DT_HEXBINARY = DataTypeHexBinary.newInstance();
    public static final DataTypeBase64Binary DT_BASE64BINARY = DataTypeBase64Binary.newInstance();
    public static final DataTypeX500Name DT_X500NAME = DataTypeX500Name.newInstance();
    public static final DataTypeRFC822Name DT_RFC822NAME = DataTypeRFC822Name.newInstance();
    public static final DataTypeIpAddress DT_IPADDRESS = DataTypeIpAddress.newInstance();
    public static final DataTypeDNSName DT_DNSNAME = DataTypeDNSName.newInstance();
    public static final DataTypeXPathExpression DT_XPATHEXPRESSION = DataTypeXPathExpression.newInstance();
}
