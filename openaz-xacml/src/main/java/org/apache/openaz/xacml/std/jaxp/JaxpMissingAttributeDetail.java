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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdMutableMissingAttributeDetail;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MissingAttributeDetailType;

/**
 * JaxpMissingAttributeDetail extends {@link org.apache.openaz.xacml.std.StdMutableMissingAttributeDetail} with
 * methods for creation from JAXP elements.
 */
public class JaxpMissingAttributeDetail extends StdMutableMissingAttributeDetail {

    protected JaxpMissingAttributeDetail(Identifier categoryIdIn, Identifier attributeIdIn,
                                         Identifier dataTypeIdIn, String issuerIn,
                                         Collection<AttributeValue<?>> attributeValuesIn) {
        super(categoryIdIn, attributeIdIn, dataTypeIdIn, issuerIn, attributeValuesIn);
    }

    public static JaxpMissingAttributeDetail newInstance(MissingAttributeDetailType missingAttributeDetailType) {
        if (missingAttributeDetailType == null) {
            throw new NullPointerException("Null MissingAttributeDetailType");
        } else if (missingAttributeDetailType.getCategory() == null) {
            throw new IllegalArgumentException("Null categoryId for MissingAttributeDetailType");
        } else if (missingAttributeDetailType.getAttributeId() == null) {
            throw new IllegalArgumentException("Null attributeId for MissingAttributeDetailType");
        } else if (missingAttributeDetailType.getDataType() == null) {
            throw new IllegalArgumentException("Null dataTypeId for MissingAttributeDetailType");
        }
        Identifier categoryId = new IdentifierImpl(missingAttributeDetailType.getCategory());
        Identifier attributeId = new IdentifierImpl(missingAttributeDetailType.getAttributeId());
        Identifier dataTypeId = new IdentifierImpl(missingAttributeDetailType.getDataType());

        List<AttributeValue<?>> attributeValues = null;
        if (missingAttributeDetailType.getAttributeValue() != null
            && missingAttributeDetailType.getAttributeValue().size() > 0) {
            attributeValues = new ArrayList<AttributeValue<?>>();
            Iterator<AttributeValueType> iterAttributeValueTypes = missingAttributeDetailType
                .getAttributeValue().iterator();
            while (iterAttributeValueTypes.hasNext()) {
                attributeValues.add(JaxpAttributeValue.newInstance(iterAttributeValueTypes.next()));
            }
        }
        return new JaxpMissingAttributeDetail(categoryId, attributeId, dataTypeId,
                                              missingAttributeDetailType.getIssuer(), attributeValues);
    }
}
