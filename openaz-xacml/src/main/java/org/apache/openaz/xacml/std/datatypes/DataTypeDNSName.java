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

import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.XACML2;

/**
 * DataTypeRFC2396DomainName extends {@link DataTypeBase} to implement the XACML RFC2396 domainName data type.
 */
public class DataTypeDNSName extends DataTypeSemanticStringBase<RFC2396DomainName> {
    private static final DataTypeDNSName singleInstance = new DataTypeDNSName();

    private DataTypeDNSName() {
        super(XACML2.ID_DATATYPE_DNSNAME, RFC2396DomainName.class);
    }

    public static DataTypeDNSName newInstance() {
        return singleInstance;
    }

    @Override
    public RFC2396DomainName convert(Object source) throws DataTypeException {
        if (source == null || source instanceof RFC2396DomainName) {
            return (RFC2396DomainName)source;
        } else {
            String stringValue = this.convertToString(source);
            if (stringValue == null) {
                return null;
            }
            RFC2396DomainName rfc2396DomainName = null;
            try {
                rfc2396DomainName = RFC2396DomainName.newInstance(stringValue);
            } catch (ParseException ex) {
                throw new DataTypeException(this, "Failed to convert \""
                                                  + source.getClass().getCanonicalName() + "\" with value \""
                                                  + stringValue + "\" to DNSName", ex);
            }
            return rfc2396DomainName;
        }
    }

}
