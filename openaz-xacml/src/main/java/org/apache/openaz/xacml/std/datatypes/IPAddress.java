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
 * IPAddress represents either an IPv4 or IPv6 network address with optional (IPv4)masks or (IPv6)prefixes and
 * port range components.
 */
public abstract class IPAddress implements SemanticString {

    /**
     * Given a string purporting to represent an <code>IPAddress</code>, try to convert it into an object. The
     * string may represent either an <code>IPv4Address</code> or an <code>IPv6Address</code>, and may include
     * a mask (for IPv4) or a prefix (for IPv6) and may also include a <code>PortRange</code>
     *
     * @param addressString
     * @return
     */
    public static IPAddress newInstance(String ipAddressString) throws ParseException {
        if (ipAddressString == null || ipAddressString.length() == 0) {
            return null;
        }
        if (IPv4Address.isIPv4Address(ipAddressString)) {
            return IPv4Address.newIPv4Instance(ipAddressString);
        } else if (IPv6Address.isIPv6Address(ipAddressString)) {
            return IPv6Address.newIPv6Instance(ipAddressString);
        } else {
            throw new ParseException("Unknown IPAddress type for \"" + ipAddressString + "\"", 0);
        }
    }

    // implementation (version) dependent
    @Override
    public abstract String stringValue();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);
    
    @Override
    public abstract int hashCode();
}
