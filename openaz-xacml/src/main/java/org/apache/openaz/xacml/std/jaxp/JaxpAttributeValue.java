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
package org.apache.openaz.xacml.std.jaxp;

import java.math.BigInteger;
import java.net.URI;

import javax.security.auth.x500.X500Principal;
import javax.xml.xpath.XPathExpression;

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.XACML;
import org.apache.openaz.xacml.api.XACML1;
import org.apache.openaz.xacml.api.XACML2;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdAttributeValue;
import org.apache.openaz.xacml.std.datatypes.Base64Binary;
import org.apache.openaz.xacml.std.datatypes.DataTypeAnyURI;
import org.apache.openaz.xacml.std.datatypes.DataTypeBase64Binary;
import org.apache.openaz.xacml.std.datatypes.DataTypeBoolean;
import org.apache.openaz.xacml.std.datatypes.DataTypeDNSName;
import org.apache.openaz.xacml.std.datatypes.DataTypeDate;
import org.apache.openaz.xacml.std.datatypes.DataTypeDateTime;
import org.apache.openaz.xacml.std.datatypes.DataTypeDayTimeDuration;
import org.apache.openaz.xacml.std.datatypes.DataTypeDouble;
import org.apache.openaz.xacml.std.datatypes.DataTypeHexBinary;
import org.apache.openaz.xacml.std.datatypes.DataTypeInteger;
import org.apache.openaz.xacml.std.datatypes.DataTypeIpAddress;
import org.apache.openaz.xacml.std.datatypes.DataTypeRFC822Name;
import org.apache.openaz.xacml.std.datatypes.DataTypeString;
import org.apache.openaz.xacml.std.datatypes.DataTypeTime;
import org.apache.openaz.xacml.std.datatypes.DataTypeX500Name;
import org.apache.openaz.xacml.std.datatypes.DataTypeXPathExpression;
import org.apache.openaz.xacml.std.datatypes.DataTypeYearMonthDuration;
import org.apache.openaz.xacml.std.datatypes.HexBinary;
import org.apache.openaz.xacml.std.datatypes.IPAddress;
import org.apache.openaz.xacml.std.datatypes.ISO8601Date;
import org.apache.openaz.xacml.std.datatypes.ISO8601DateTime;
import org.apache.openaz.xacml.std.datatypes.ISO8601Time;
import org.apache.openaz.xacml.std.datatypes.RFC2396DomainName;
import org.apache.openaz.xacml.std.datatypes.RFC822Name;
import org.apache.openaz.xacml.std.datatypes.XPathDayTimeDuration;
import org.apache.openaz.xacml.std.datatypes.XPathYearMonthDuration;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

/**
 * JaxpAttributeValue<T> extends {@link org.apache.openaz.xacml.std.StdAttributeValue} to instantiate itself
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
