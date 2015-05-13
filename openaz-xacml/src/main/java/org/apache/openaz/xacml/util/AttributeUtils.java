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
 *          Copyright (c) 2014 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.util;

import org.apache.openaz.xacml.api.Attribute;
import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.RequestAttributes;
import org.apache.openaz.xacml.api.RequestDefaults;
import org.apache.openaz.xacml.api.RequestReference;
import org.apache.openaz.xacml.api.pip.PIPRequest;

public class AttributeUtils {

    public static String prettyPrint(Attribute attribute) {
        String tab = "\t";
        StringBuilder builder = new StringBuilder();
        builder.append(attribute.getAttributeId());
        builder.append(System.lineSeparator());
        builder.append(tab + attribute.getCategory());
        builder.append(System.lineSeparator());
        builder.append(tab + attribute.getIssuer());
        builder.append(System.lineSeparator());
        tab = tab + "\t";
        for (AttributeValue<?> value : attribute.getValues()) {
            builder.append(tab + value.getDataTypeId());
            builder.append(tab + value.getValue().toString());
        }
        return builder.toString();
    }

    public static String prettyPrint(PIPRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getCategory());
        builder.append(System.lineSeparator());
        builder.append(request.getAttributeId());
        builder.append(System.lineSeparator());
        builder.append(request.getDataTypeId());
        builder.append(System.lineSeparator());
        builder.append(request.getIssuer());
        return builder.toString();
    }

    public static String prettyPrint(Request request) {
        StringBuilder builder = new StringBuilder();
        String tab = "\t";
        builder.append("Combined Decision=" + request.getCombinedDecision() + " returnPolicyIdList="
                       + request.getReturnPolicyIdList());
        builder.append(System.lineSeparator());
        for (RequestAttributes attribute : request.getRequestAttributes()) {
            builder.append(attribute.getCategory());
            builder.append(System.lineSeparator());
            for (Attribute a : attribute.getAttributes()) {
                builder.append(tab + a.getAttributeId() + " issuer=" + a.getIssuer());
                builder.append(System.lineSeparator());
                for (AttributeValue<?> value : a.getValues()) {
                    builder.append(tab + tab + value.getDataTypeId() + " " + value.getValue().toString());
                    builder.append(System.lineSeparator());
                }
            }
        }
        for (RequestReference ref : request.getMultiRequests()) {
            builder.append(System.lineSeparator());
            builder.append("Reference=" + ref);
        }

        RequestDefaults defs = request.getRequestDefaults();
        if (defs != null) {
            builder.append(System.lineSeparator());
            builder.append("Defaults=" + defs.getXPathVersion());
        }

        return builder.toString();
    }
}
