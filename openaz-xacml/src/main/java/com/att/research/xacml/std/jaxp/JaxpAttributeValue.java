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
package com.att.research.xacml.std.jaxp;

import java.math.BigInteger;
import java.net.URI;

import javax.security.auth.x500.X500Principal;
import javax.xml.xpath.XPathExpression;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacml.api.XACML;
import com.att.research.xacml.api.XACML1;
import com.att.research.xacml.api.XACML2;
import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.std.IdentifierImpl;
import com.att.research.xacml.std.StdAttributeValue;
import com.att.research.xacml.std.datatypes.Base64Binary;
import com.att.research.xacml.std.datatypes.DataTypeAnyURI;
import com.att.research.xacml.std.datatypes.DataTypeBase64Binary;
import com.att.research.xacml.std.datatypes.DataTypeBoolean;
import com.att.research.xacml.std.datatypes.DataTypeDNSName;
import com.att.research.xacml.std.datatypes.DataTypeDate;
import com.att.research.xacml.std.datatypes.DataTypeDateTime;
import com.att.research.xacml.std.datatypes.DataTypeDayTimeDuration;
import com.att.research.xacml.std.datatypes.DataTypeDouble;
import com.att.research.xacml.std.datatypes.DataTypeHexBinary;
import com.att.research.xacml.std.datatypes.DataTypeInteger;
import com.att.research.xacml.std.datatypes.DataTypeIpAddress;
import com.att.research.xacml.std.datatypes.DataTypeRFC822Name;
import com.att.research.xacml.std.datatypes.DataTypeString;
import com.att.research.xacml.std.datatypes.DataTypeTime;
import com.att.research.xacml.std.datatypes.DataTypeX500Name;
import com.att.research.xacml.std.datatypes.DataTypeXPathExpression;
import com.att.research.xacml.std.datatypes.DataTypeYearMonthDuration;
import com.att.research.xacml.std.datatypes.HexBinary;
import com.att.research.xacml.std.datatypes.IPAddress;
import com.att.research.xacml.std.datatypes.ISO8601Date;
import com.att.research.xacml.std.datatypes.ISO8601DateTime;
import com.att.research.xacml.std.datatypes.ISO8601Time;
import com.att.research.xacml.std.datatypes.RFC2396DomainName;
import com.att.research.xacml.std.datatypes.RFC822Name;
import com.att.research.xacml.std.datatypes.XPathDayTimeDuration;
import com.att.research.xacml.std.datatypes.XPathYearMonthDuration;

/**
 * JaxpAttributeValue<T> extends {@link com.att.research.xacml.std.StdAttributeValue} to instantiate itself
 * from a JAXP {@link oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType}.
 *
 * @param <T> the data type of the value in the <code>AttributeValue</code>.
 */
public class JaxpAttributeValue<T> extends StdAttributeValue<T> {

    private JaxpAttributeValue(Identifier dataTypeIdIn, T valueIn) {
        super(dataTypeIdIn, valueIn);
    }

    public static JaxpAttributeValue<?> newInstance(AttributeValueType attributeValueType) {
        if (attributeValueType == null) {
            throw new NullPointerException("Null AttributeValueType");
        } else if (attributeValueType.getDataType() == null) {
            throw new IllegalArgumentException("Null dataType in AttributeValueType");
        }
        Identifier dataTypeId = new IdentifierImpl(attributeValueType.getDataType());
        Object source = (attributeValueType.getContent() == null
                         || attributeValueType.getContent().size() == 0 ? "" : attributeValueType
            .getContent().get(0));
        try {
            if (dataTypeId.equals(XACML.ID_DATATYPE_ANYURI)) {
                return new JaxpAttributeValue<URI>(dataTypeId, DataTypeAnyURI.newInstance().convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_BASE64BINARY)) {
                return new JaxpAttributeValue<Base64Binary>(dataTypeId, DataTypeBase64Binary.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_BOOLEAN)) {
                return new JaxpAttributeValue<Boolean>(dataTypeId, DataTypeBoolean.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_DATE)) {
                return new JaxpAttributeValue<ISO8601Date>(dataTypeId, DataTypeDate.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_DATETIME)) {
                return new JaxpAttributeValue<ISO8601DateTime>(dataTypeId, DataTypeDateTime.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_DAYTIMEDURATION)) {
                return new JaxpAttributeValue<XPathDayTimeDuration>(dataTypeId, DataTypeDayTimeDuration
                    .newInstance().convert(source));
            } else if (dataTypeId.equals(XACML2.ID_DATATYPE_DNSNAME)) {
                return new JaxpAttributeValue<RFC2396DomainName>(dataTypeId, DataTypeDNSName.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_DOUBLE)) {
                return new JaxpAttributeValue<Double>(dataTypeId, DataTypeDouble.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_HEXBINARY)) {
                return new JaxpAttributeValue<HexBinary>(dataTypeId, DataTypeHexBinary.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_INTEGER)) {
                return new JaxpAttributeValue<BigInteger>(dataTypeId, DataTypeInteger.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML2.ID_DATATYPE_IPADDRESS)) {
                return new JaxpAttributeValue<IPAddress>(dataTypeId, DataTypeIpAddress.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML1.ID_DATATYPE_RFC822NAME)) {
                return new JaxpAttributeValue<RFC822Name>(dataTypeId, DataTypeRFC822Name.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_STRING)) {
                return new JaxpAttributeValue<String>(dataTypeId, DataTypeString.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_TIME)) {
                return new JaxpAttributeValue<ISO8601Time>(dataTypeId, DataTypeTime.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML1.ID_DATATYPE_X500NAME)) {
                return new JaxpAttributeValue<X500Principal>(dataTypeId, DataTypeX500Name.newInstance()
                    .convert(source));
            } else if (dataTypeId.equals(XACML3.ID_DATATYPE_XPATHEXPRESSION)) {
                return new JaxpAttributeValue<XPathExpression>(dataTypeId, DataTypeXPathExpression
                    .newInstance().convert(source));
            } else if (dataTypeId.equals(XACML.ID_DATATYPE_YEARMONTHDURATION)) {
                return new JaxpAttributeValue<XPathYearMonthDuration>(dataTypeId, DataTypeYearMonthDuration
                    .newInstance().convert(source));
            } else {
                throw new IllegalArgumentException("Unknown dataType \"" + attributeValueType.getDataType()
                                                   + "\"");
            }
        } catch (DataTypeException ex) {
            throw new IllegalArgumentException("DataTypeException converting to dataType \""
                                               + attributeValueType.getDataType() + "\"");
        }
    }

}
