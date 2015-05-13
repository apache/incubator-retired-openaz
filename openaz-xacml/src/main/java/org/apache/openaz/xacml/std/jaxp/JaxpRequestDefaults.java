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

import java.net.URI;

import org.apache.openaz.xacml.std.StdRequestDefaults;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestDefaultsType;

/**
 * JaxpRequestDefaults extends {@link org.apache.openaz.xacml.std.StdRequestDefaults} with methods for creation
 * from JAXP elements.
 */
public class JaxpRequestDefaults extends StdRequestDefaults {

    protected JaxpRequestDefaults(URI xpathVersionIn) {
        super(xpathVersionIn);
    }

    public static JaxpRequestDefaults newInstance(RequestDefaultsType requestDefaultsType) {
        if (requestDefaultsType == null) {
            throw new NullPointerException("Null RequestDefaultsType");
        }
        URI uriXPathVersion = null;
        if (requestDefaultsType.getXPathVersion() != null) {
            try {
                uriXPathVersion = new URI(requestDefaultsType.getXPathVersion());
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid URI for XPathVersion \""
                                                   + requestDefaultsType.getXPathVersion() + "\"", ex);
            }
        }
        return new JaxpRequestDefaults(uriXPathVersion);
    }

}
