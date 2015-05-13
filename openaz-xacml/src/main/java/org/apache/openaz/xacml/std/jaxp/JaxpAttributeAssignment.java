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

import java.util.List;

import org.apache.openaz.xacml.api.AttributeValue;
import org.apache.openaz.xacml.api.DataType;
import org.apache.openaz.xacml.api.DataTypeException;
import org.apache.openaz.xacml.api.DataTypeFactory;
import org.apache.openaz.xacml.api.Identifier;
import org.apache.openaz.xacml.std.IdentifierImpl;
import org.apache.openaz.xacml.std.StdMutableAttributeAssignment;
import org.apache.openaz.xacml.util.FactoryException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeAssignmentType;

/**
 * JaxpAttributeAssignment extends {@link org.apache.openaz.xacml.std.StdMutableAttributeAssignment} with
 * methods for creation from JAXP elements.
 *
 * @param <T> the java class of the value type for the assignment
 */
public class JaxpAttributeAssignment extends StdMutableAttributeAssignment {

    protected JaxpAttributeAssignment(Identifier attributeIdIn, Identifier categoryIdIn, String issuerIn,
                                      AttributeValue<?> attributeValueIn) {
        super(attributeIdIn, categoryIdIn, issuerIn, attributeValueIn);
    }

    public static JaxpAttributeAssignment newInstance(AttributeAssignmentType attributeAssignmentType) {
        if (attributeAssignmentType == null) {
            throw new NullPointerException("Null AttributeAssignmentType");
        } else if (attributeAssignmentType.getAttributeId() == null) {
            throw new IllegalArgumentException("Null attributeId in AttributeAssignmentType");
        } else if (attributeAssignmentType.getCategory() == null) {
            throw new IllegalArgumentException("Null categoryId in AttributeAssignmentType");
        } else if (attributeAssignmentType.getDataType() == null) {
            throw new IllegalArgumentException("Null dataTypeId in AttributeAssignmentType");
        } else if (attributeAssignmentType.getContent() == null
                   || attributeAssignmentType.getContent().get(0) == null) {
            throw new IllegalArgumentException("Null value in AttributeAssignmentType");
        }
        Identifier attributeId = new IdentifierImpl(attributeAssignmentType.getAttributeId());
        Identifier categoryId = new IdentifierImpl(attributeAssignmentType.getCategory());
        Identifier dataTypeId = new IdentifierImpl(attributeAssignmentType.getDataType());
        DataTypeFactory dataTypeFactory = null;
        try {
            dataTypeFactory = DataTypeFactory.newInstance();
            if (dataTypeFactory == null) {
                return null;
            }
        } catch (FactoryException ex) {
            return null;
        }
        DataType<?> dataType = dataTypeFactory.getDataType(dataTypeId);
        if (dataType == null) {
            throw new IllegalArgumentException("Unknown data type \"" + dataTypeId.toString() + "\"");
        }

        List<Object> content = attributeAssignmentType.getContent();
        String issuer = attributeAssignmentType.getIssuer();
        AttributeValue<?> attributeValue = null;
        try {
            attributeValue = dataType.createAttributeValue(content);
        } catch (DataTypeException ex) {
            throw new IllegalArgumentException("Failed to create AttributeValue from \""
                                               + dataTypeId.toString() + "\"", ex);
        }

        return new JaxpAttributeAssignment(attributeId, categoryId, issuer, attributeValue);
    }
}
