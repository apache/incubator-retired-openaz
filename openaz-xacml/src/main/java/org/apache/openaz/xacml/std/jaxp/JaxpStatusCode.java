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

import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.api.StatusCode;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdStatusCode;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.StatusCodeType;

/**
 * JaxpStatusCode extends {@link org.apache.openaz.xacml.std.StdStatusCode} with static methods for creating a
 * <code>StatusCode</code> object by parsing JAXP elements based on the XACML 3.0 schema.
 */
public class JaxpStatusCode extends StdStatusCode {

    protected JaxpStatusCode(Identifier statusCodeValueIn, StatusCode childIn) {
        super(statusCodeValueIn, childIn);
    }

    public static JaxpStatusCode newInstance(StatusCodeType statusCodeType) {
        if (statusCodeType == null) {
            throw new NullPointerException("Null StatusCodeType");
        } else if (statusCodeType.getValue() == null) {
            throw new IllegalArgumentException("Null StatusCodeValue");
        }
        Identifier statusCodeValue = new IdentifierImpl(statusCodeType.getValue());

        StatusCode statusCodeChild = null;
        if (statusCodeType.getStatusCode() != null) {
            try {
                statusCodeChild = JaxpStatusCode.newInstance(statusCodeType.getStatusCode());
            } catch (Exception ex) {
                throw new IllegalArgumentException("Invalid child StatusCodeValue", ex);
            }
        }
        return new JaxpStatusCode(statusCodeValue, statusCodeChild);
    }
}
