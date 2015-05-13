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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdMutableAttribute;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

/**
 * JaxpAttribute extends {@link org.apache.openaz.xacml.std.StdMutableAttribute} with methods for creation from
 * JAXP elements.
 */
public class JaxpAttribute extends StdMutableAttribute {

    protected JaxpAttribute(Identifier attributeIdIn, Identifier categoryIdIn,
                            List<AttributeValue<?>> valuesIn, String issuerIn, boolean includeInResultsIn) {
        super(attributeIdIn, categoryIdIn, valuesIn, issuerIn, includeInResultsIn);
    }

    public static JaxpAttribute newInstance(Identifier categoryId, AttributeType attributeType) {
        if (categoryId == null) {
            throw new NullPointerException("Null categoryId");
        } else if (attributeType == null) {
            throw new NullPointerException("Null AttributeType");
        } else if (attributeType.getAttributeId() == null) {
            throw new IllegalArgumentException("Null attributeId in AttributeType");
        } else if (attributeType.getAttributeValue() == null) {
            throw new IllegalArgumentException("Null attributeValue in AttributeType");
        }
        Identifier attributeId = new IdentifierImpl(attributeType.getAttributeId());
        List<AttributeValue<?>> values = new ArrayList<AttributeValue<?>>();
        Iterator<AttributeValueType> iterAttributeValueTypes = attributeType.getAttributeValue().iterator();
        while (iterAttributeValueTypes.hasNext()) {
            values.add(JaxpAttributeValue.newInstance(iterAttributeValueTypes.next()));
        }

        return new JaxpAttribute(attributeId, categoryId, values, attributeType.getIssuer(),
                                 attributeType.isIncludeInResult());
    }
}
