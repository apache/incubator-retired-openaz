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
 * RFC2396DomainName represents a host name from the RFC2396 specification.
 */
public class RFC2396DomainName implements SemanticString {
    private String domainName;
    private PortRange portRange;

    public static boolean isValidTopLabel(String topLabel) {
        if (topLabel == null || topLabel.length() == 0) {
            return false;
        }

        if (!Character.isLetter(topLabel.charAt(0))) {
            return false;
        }
        if (topLabel.length() > 1) {
            for (int i = 1; i < topLabel.length() - 1; i++) {
                char c = topLabel.charAt(i);
                if (!(Character.isLetterOrDigit(c) || c == '-')) {
                    return false;
                }
            }
            if (!Character.isLetterOrDigit(topLabel.charAt(topLabel.length() - 1))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidDomainLabel(String domainLabel) {
        if (domainLabel == null || domainLabel.length() == 0) {
            return false;
        }

        if (!Character.isLetterOrDigit(domainLabel.charAt(0))) {
            return false;
        }
        if (domainLabel.length() > 1) {
            for (int i = 1; i < domainLabel.length() - 1; i++) {
                char c = domainLabel.charAt(i);
                if (!(Character.isLetterOrDigit(c) || c == '-')) {
                    return false;
                }
            }
            if (!Character.isLetterOrDigit(domainLabel.charAt(domainLabel.length() - 1))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidHostName(String hostName) {
        if (hostName == null || hostName.length() == 0) {
            return false;
        }
        String[] labels = hostName.split("[.]", -1);
        if (labels == null || labels.length == 0) {
            return false;
        }

        /*
         * Validate the domain label parts
         */
        for (int i = 0; i < labels.length - 1; i++) {
            if (!isValidDomainLabel(labels[i])) {
                return false;
            }
        }

        /*
         * Validate the top label
         */
        if (!isValidTopLabel(labels[labels.length - 1])) {
            return false;
        }
        return true;
    }

    public RFC2396DomainName(String domainNameIn, PortRange portRangeIn) {
        if (!isValidHostName(domainNameIn)) {
            throw new IllegalArgumentException("Invalid RFC2396 domain name");
        }
        this.domainName = domainNameIn;
        this.portRange = portRangeIn;
    }

    /**
     * Parses an RFC2396 domain name of the form [domain.[domain.[...]].name[:port-range] to create a new
     * <code>RFC2396DomainName</code>.
     *
     * @param rfc2396DomainNameString the <code>String</code> name to parse
     * @return a new <code>RFC2396DomainName</code> generated from the given <code>String</code>
     * @throws java.text.ParseException if there is an error parsing the name.
     */
    public static RFC2396DomainName newInstance(String rfc2396DomainNameString) throws ParseException {
        if (rfc2396DomainNameString == null || rfc2396DomainNameString.length() == 0) {
            return null;
        }

        /*
         * Break the string up into its name and port range parts
         */
        int colonPos = rfc2396DomainNameString.indexOf(':');
        String hostName = (colonPos < 0 ? rfc2396DomainNameString : rfc2396DomainNameString
            .substring(0, colonPos));
        if (!isValidHostName(hostName)) {
            throw new ParseException("Invalid RFC 2396 host name \"" + hostName + "\"", 0);
        }

        PortRange portRange = null;
        if (colonPos >= 0) {
            if (colonPos + 1 >= rfc2396DomainNameString.length()) {
                throw new ParseException("Invalid RFC 2396 port range \"" + rfc2396DomainNameString
                                         + "\": no port numbers", colonPos);
            }
            String stringPortRange = rfc2396DomainNameString.substring(colonPos + 1);
            try {
                portRange = PortRange.newInstance(stringPortRange);
            } catch (ParseException ex) {
                throw new ParseException("Invalid RFC 2396 port range \"" + stringPortRange + "\"", colonPos);
            }
        }

        return new RFC2396DomainName(hostName, portRange);
    }

    public String getDomainName() {
        return this.domainName;
    }

    public PortRange getPortRange() {
        return this.portRange;
    }

    @Override
    public String stringValue() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.getDomainName() != null) {
            stringBuilder.append(this.getDomainName());
        }
        if (this.getPortRange() != null) {
            stringBuilder.append(':');
            stringBuilder.append(this.getPortRange().stringValue());
        }
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        int hc = (this.getDomainName() == null ? 0 : this.getDomainName().hashCode());
        return hc + (this.getPortRange() == null ? 0 : this.getPortRange().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RFC2396DomainName)) {
            return false;
        } else if (obj == this) {
            return true;
        } else {
            RFC2396DomainName rfc2396DomainName = (RFC2396DomainName)obj;
            if (this.getDomainName() == null) {
                if (rfc2396DomainName.getDomainName() != null) {
                    return false;
                }
            } else {
                if (!this.getDomainName().equals(rfc2396DomainName.getDomainName())) {
                    return false;
                }
            }
            if (this.getPortRange() == null) {
                return rfc2396DomainName.getPortRange() == null;
            } else {
                return this.getPortRange().equals(rfc2396DomainName.getPortRange());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        boolean needComma = false;

        if (this.getDomainName() != null) {
            stringBuilder.append("domainName=");
            stringBuilder.append(this.getDomainName());
            needComma = true;
        }
        if (this.getPortRange() != null) {
            if (needComma) {
                stringBuilder.append(',');
            }
            stringBuilder.append("portRange=");
            stringBuilder.append(this.getPortRange().toString());
            needComma = true;
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
